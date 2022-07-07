package space.work.training.izi.nav_fragments

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnLayoutChangeListener
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import space.work.training.izi.R
import space.work.training.izi.adapters.GroupChatAdapter
import space.work.training.izi.databinding.FragmentGroupChatBinding
import space.work.training.izi.model.ModelGroupChat

class GroupChatFragment : Fragment() {

    private lateinit var binding: FragmentGroupChatBinding

    private var groupId: String = ""
    private val args: GroupChatFragmentArgs by navArgs()

    private var firebaseAuth: FirebaseAuth? = null
    private var user: FirebaseUser? = null

    private var chatList: ArrayList<ModelGroupChat> = ArrayList()
    private var groupChatAdapter: GroupChatAdapter? = null

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

        val linearLayoutManager = LinearLayoutManager(requireContext())
        linearLayoutManager.stackFromEnd = true
        binding.rvChat.layoutManager = linearLayoutManager


        loadGroup()

        binding.bnSend.setOnClickListener(View.OnClickListener { // notify=true;
            val message: String = binding.etMessage.text.toString().trim { it <= ' ' }
            if (!TextUtils.isEmpty(message)) {
                sendMessage(message)
            }
            binding.etMessage.setText("")
        })

        readMessages()
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
                groupChatAdapter = GroupChatAdapter(requireContext(), chatList)
                binding.rvChat.adapter = groupChatAdapter
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
                binding.tvName.setText(dataSnapshot.child("groupName").getValue(String::class.java))
                val icon = dataSnapshot.child("gropuIcon").getValue(
                    String::class.java
                )
                if (icon == null) {
                    Glide.with(requireContext()).load(R.drawable.background).into(binding.ivProfile)
                } else {
                    Glide.with(requireContext()).load(
                        dataSnapshot.child("gropuIcon").getValue(
                            String::class.java
                        )
                    ).into(binding.ivProfile)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

}