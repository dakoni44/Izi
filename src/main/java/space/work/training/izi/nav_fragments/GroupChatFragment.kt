package space.work.training.izi.nav_fragments

import android.app.AlertDialog
import android.content.ContentResolver
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnLayoutChangeListener
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.webkit.MimeTypeMap
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import space.work.training.izi.CropperActivity
import space.work.training.izi.R
import space.work.training.izi.ViewUtils
import space.work.training.izi.adapters.GroupChatAdapter
import space.work.training.izi.adapters.ParticipantsAdapter
import space.work.training.izi.databinding.FragmentGroupChatBinding
import space.work.training.izi.model.ModelGroupChat
import space.work.training.izi.mvvm.chatList.User

class GroupChatFragment : Fragment(), GroupChatAdapter.OnItemClickListener {

    private lateinit var binding: FragmentGroupChatBinding

    private var groupId: String = ""
    private val args: GroupChatFragmentArgs by navArgs()

    private var firebaseAuth: FirebaseAuth? = null
    private var user: FirebaseUser? = null

    private var chatList: ArrayList<ModelGroupChat> = ArrayList()
    private var groupChatAdapter: GroupChatAdapter? = null

    private var creator = ""
    lateinit var mGetContent: ActivityResultLauncher<String>
    private var imageUri: Uri? = null
    private var myUrl = ""
    private var uploadTask: UploadTask? = null
    private var storageReference: StorageReference? = null

    private var friendList: ArrayList<String>? = null
    private var list: ArrayList<User> = ArrayList<User>()
    private var databaseReference: DatabaseReference? = null
    var groupMembersAdapter: ParticipantsAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_group_chat, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        groupId = args.groupId

        firebaseAuth = FirebaseAuth.getInstance()
        user = firebaseAuth!!.currentUser
        databaseReference = FirebaseDatabase.getInstance().getReference("Users")
        storageReference = FirebaseStorage.getInstance().getReference("profileImages")

        val linearLayoutManager = LinearLayoutManager(requireContext())
        linearLayoutManager.stackFromEnd = true
        binding.rvChat.layoutManager = linearLayoutManager
        groupChatAdapter = GroupChatAdapter(requireContext(),this)
        binding.rvChat.adapter = groupChatAdapter

        binding.rvParticipants.setHasFixedSize(true)
        binding.rvParticipants.layoutManager = LinearLayoutManager(requireContext())
        groupMembersAdapter = ParticipantsAdapter(requireContext(), groupId)
        binding.rvParticipants.adapter = groupMembersAdapter

        mGetContent = registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) {
            val intent = Intent(activity, CropperActivity::class.java).apply {
                putExtra("DATA", it.toString())
                startActivityForResult(this, 101)
            }
        }

        loadGroup()
        checkFollowing()

        binding.bnSend.setOnClickListener(View.OnClickListener { // notify=true;
            val message: String = binding.etMessage.text.toString().trim { it <= ' ' }
            if (!TextUtils.isEmpty(message)) {
                sendMessage(message)
            }
            binding.etMessage.setText("")
        })

        readMessages()

        binding.editGroup.setOnClickListener {
            ViewUtils.expand(binding.editGroupLayout)
        }

        binding.changeGroupPic.setOnClickListener {
            mGetContent.launch("image/*")
        }

        binding.groupDone.setOnClickListener {
            updateText()
            uploadImage()
            ViewUtils.collapse(binding.editGroupLayout)
        }

        binding.collapseChange.setOnClickListener {
            ViewUtils.collapse(binding.editGroupLayout)
        }

        binding.addGroupMembers.setOnClickListener {
            ViewUtils.collapse(binding.editGroupLayout)
            binding.rlAddMembers.visibility = View.VISIBLE
            binding.rlAddMembers.alpha = 0f
            binding.rlAddMembers.animate()
                .alpha(1f)
                .setDuration(400)
                .setInterpolator(AccelerateInterpolator())
                .start()
        }

        binding.ivCreate.setOnClickListener {
            ViewUtils.expand(binding.editGroupLayout)
            Handler(Looper.getMainLooper()).postDelayed({
                binding.rlAddMembers.visibility = View.INVISIBLE
            }, 200)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101 && resultCode == -1) {
            val result = data!!.getStringExtra("RESULT")
            result?.let {
                imageUri = Uri.parse(it)
            }
            binding.changeGroupPic.setImageURI(imageUri)
        }
    }

    private fun updateText() {
        if (!binding.changeGroupName.text.toString().trim().equals("")) {
            val reference = FirebaseDatabase.getInstance().getReference("Groups")
            reference.child(groupId).child("groupName")
                .setValue(binding.changeGroupName.text.toString().trim { it <= ' ' })
        }
    }

    private fun getFileExtension(uri: Uri): String? {
        val contentResolver: ContentResolver = requireContext().contentResolver
        val mime = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(contentResolver.getType(uri))
    }

    private fun uploadImage() {
        imageUri?.let {
            val filereference = storageReference!!.child(
                System.currentTimeMillis().toString() +
                        "." + getFileExtension(it)
            )
            uploadTask = filereference.putFile(it)
            uploadTask!!.continueWithTask { task: Task<*> ->
                if (!task.isComplete) {
                    throw task.exception!!
                }
                filereference.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri: Uri = task.result
                    myUrl = downloadUri.toString()

                    val reference = FirebaseDatabase.getInstance().getReference("Groups")

                    reference.child(groupId).child("groupIcon").setValue(myUrl)
                }
            }.addOnFailureListener { }
        }
    }

    private fun sendMessage(message: String) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("Groups")
        val timestamp = System.currentTimeMillis().toString()
        val hashMap = HashMap<String, Any>()
        hashMap["sender"] = user!!.uid
        hashMap["message"] = message
        hashMap["timestamp"] = timestamp
        databaseReference.child(groupId).child("Messages").child(timestamp).setValue(hashMap)
    }

    private fun readMessages() {
        chatList = ArrayList()
        val dbRef = FirebaseDatabase.getInstance().getReference("Groups")
        dbRef.child(groupId).child("Messages").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                chatList.clear()
                for (ds in dataSnapshot.children) {
                    val chat = ModelGroupChat()
                    chat.message = ds.child("message").getValue(String::class.java).toString()
                    chat.sender = ds.child("sender").getValue(String::class.java).toString()
                    chat.timestamp = ds.child("timestamp").getValue(String::class.java).toString()
                    chatList.add(chat)
                }
                groupChatAdapter!!.setData(chatList)
                binding.rvChat.smoothScrollToPosition(groupChatAdapter!!.itemCount)
                binding.rvChat.addOnLayoutChangeListener(OnLayoutChangeListener { view, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
                    if (bottom < oldBottom) {
                        binding.rvChat.postDelayed(Runnable {
                            binding.rvChat.smoothScrollToPosition(
                                groupChatAdapter!!.itemCount
                            )
                        }, 10)
                    }
                })
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun loadGroup() {
        val groupRef = FirebaseDatabase.getInstance().getReference("Groups")
        groupRef.child(groupId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                binding.tvName.text = dataSnapshot.child("groupName").getValue(String::class.java)
                creator = dataSnapshot.child("createdBy").getValue(String::class.java).toString()
                val icon = dataSnapshot.child("groupIcon").getValue(
                    String::class.java
                )
                if (icon == null) {
                    Glide.with(requireContext()).load(R.drawable.background).into(binding.ivProfile)
                    Glide.with(requireContext()).load(R.drawable.background)
                        .into(binding.changeGroupPic)
                } else {
                    Glide.with(requireActivity()).load(
                        icon
                    ).into(binding.ivProfile)
                    Glide.with(requireActivity()).load(
                        icon
                    ).into(binding.changeGroupPic)
                }
                if (creator.equals(user!!.uid)) {
                    binding.addGroupMembers.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun checkFollowing() {
        friendList = java.util.ArrayList()
        val reference = FirebaseDatabase.getInstance().getReference("Friends")
            .child(user!!.uid)
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                friendList!!.clear()
                for (snapshot in dataSnapshot.children) {
                    friendList!!.add(snapshot.key!!)
                }
                loadParticipants()
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun loadParticipants() {
        databaseReference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                list.clear()
                for (snapshot in dataSnapshot.children) {
                    val user = User()
                    user.uid = snapshot.child("uid").getValue(String::class.java).toString()
                    user.name = snapshot.child("name").getValue(String::class.java).toString()
                    user.username =
                        snapshot.child("username").getValue(String::class.java).toString()
                    user.email = snapshot.child("email").getValue(String::class.java).toString()
                    user.image = snapshot.child("image").getValue(String::class.java).toString()
                    user.bio = dataSnapshot.child("bio").getValue(String::class.java).toString()
                    for (id in friendList!!) {
                        if (user.uid.equals(id)) {
                            list.add(user)
                        }
                    }
                }
                groupMembersAdapter!!.setData(list)
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    override fun onItemLongClick(position: Int) {
        if(chatList.get(position).sender.equals(user!!.uid)){
            val alertDialogBuilder = AlertDialog.Builder(requireActivity())
            alertDialogBuilder.setMessage("Delete message?")
            alertDialogBuilder.setPositiveButton(
                "Ok"
            ) { _, _ ->
                val dbRef = FirebaseDatabase.getInstance().getReference("Groups")
                dbRef.child(groupId).child("Messages").child(chatList.get(position).timestamp).removeValue()
            }
            alertDialogBuilder.setNegativeButton(
                "Cancel"
            ) { arg0, arg1 -> }
            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK)
        }
    }
}