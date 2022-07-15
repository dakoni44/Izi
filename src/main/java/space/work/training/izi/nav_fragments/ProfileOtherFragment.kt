package space.work.training.izi.nav_fragments

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import dagger.hilt.android.AndroidEntryPoint
import space.work.training.izi.R
import space.work.training.izi.adapters.ProfileAdapter
import space.work.training.izi.databinding.FragmentProfileOtherBinding
import space.work.training.izi.mvvm.chatList.User
import space.work.training.izi.mvvm.posts.Img
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class ProfileOtherFragment : Fragment(), ProfileAdapter.OnItemClickListener {

    private lateinit var binding: FragmentProfileOtherBinding

    private val args: ProfileOtherFragmentArgs by navArgs()
    private var friendId: String? = null

    private var firebaseAuth: FirebaseAuth? = null
    private var user: FirebaseUser? = null
    private var firebaseDatabase: FirebaseDatabase? = null
    private var databaseReference: DatabaseReference? = null
    private var postRef: DatabaseReference? = null
    private var friendRequestRef: DatabaseReference? = null
    private var friendsRef: DatabaseReference? = null
    private var likeRef: DatabaseReference? = null
    private var dislikeRef: DatabaseReference? = null

    private var profileRManager: StaggeredGridLayoutManager? = null
    private var profileAdapter: ProfileAdapter? = null
    private val imgs: ArrayList<Img> = ArrayList<Img>()

    private var senderId: String? = null
    private var CURRENT_STATE: String? = null
    private var saveCurrentDate: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_profile_other, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        friendId = args.uId

        firebaseAuth = FirebaseAuth.getInstance()
        user = firebaseAuth!!.currentUser
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase!!.getReference("Users")
        postRef = firebaseDatabase!!.getReference("Posts")
        friendRequestRef = firebaseDatabase!!.reference.child("FriendRequests")
        friendsRef = firebaseDatabase!!.reference.child("Friends")
        likeRef = firebaseDatabase!!.getReference("Likes")
        dislikeRef = firebaseDatabase!!.getReference("Dislikes")

        senderId = user!!.uid
        CURRENT_STATE = "not_friends"

        profileRManager = StaggeredGridLayoutManager(3, 1)
        binding.profileRecycler.layoutManager = profileRManager
        profileAdapter = ProfileAdapter(requireContext(), this)
        binding.profileRecycler.adapter = profileAdapter

        binding.sendMessage.setOnClickListener {
            val action = ProfileOtherFragmentDirections.profileOtherToChat(friendId!!)
            findNavController().navigate(action)
        }

        binding.tvNameFull.setOnClickListener {
            if (binding.rlBio.visibility == View.GONE) {
                binding.rlBio.visibility = View.VISIBLE
                binding.ivArrow.animate().rotation(180f)
            } else {
                binding.rlBio.visibility = View.GONE
                binding.ivArrow.animate().rotation(0f)
            }
        }

        friendsRef!!.child(senderId!!).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (!dataSnapshot.hasChild(friendId!!)) {
                    binding.rlFriends.visibility = View.INVISIBLE
                    binding.rlNotFriends.visibility = View.VISIBLE

                } else {
                    binding.rlFriends.visibility = View.VISIBLE
                    binding.rlNotFriends.visibility = View.INVISIBLE
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })

    }

    private fun showData2() {
        databaseReference!!.child(friendId!!).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = User()
                user.uid = friendId!!
                user.name = dataSnapshot.child("name").getValue(String::class.java).toString()
                user.username =
                    dataSnapshot.child("username").getValue(String::class.java).toString()
                user.email = dataSnapshot.child("email").getValue(String::class.java).toString()
                user.image = dataSnapshot.child("image").getValue(String::class.java).toString()
                user.bio = dataSnapshot.child("bio").getValue(String::class.java).toString()
                binding.tvUsername.text = user.username
                binding.tvName2.text = user.name
                Glide.with(requireContext()).load(user.image).into(binding.ivProfile)
                Glide.with(requireContext()).load(R.drawable.background).into(binding.ivBackground)
                //Glide.with(getApplicationContext()).load(user.getImage())
                //.apply(RequestOptions.bitmapTransform(new BlurTransformation(20, 2)))
                //.into(ivBackground);
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun showData() {
        databaseReference!!.child(friendId!!).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = User()
                user.uid = friendId!!
                user.name = dataSnapshot.child("name").getValue(String::class.java).toString()
                user.username =
                    dataSnapshot.child("username").getValue(String::class.java).toString()
                user.email = dataSnapshot.child("email").getValue(String::class.java).toString()
                user.image = dataSnapshot.child("image").getValue(String::class.java).toString()
                user.bio = dataSnapshot.child("bio").getValue(String::class.java).toString()
                binding.tvName.text = user.username
                binding.tvNameFull.text = user.name
                binding.tvBio.text = user.bio
                Glide.with(requireContext()).load(user.image).into(binding.ivMalaSlika1)
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun maintanceOfButtons() {
        friendsRef!!.child(senderId!!).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.hasChild(friendId!!)) {
                    CURRENT_STATE = "friends"
                } else {
                    binding.bnSendRequest2.text = "Add friend"
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
        friendRequestRef!!.child(senderId!!).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.hasChild(friendId!!)) {
                    val requset_type = dataSnapshot.child(friendId!!).child("request_type")
                        .value.toString()
                    if (requset_type == "sent") {
                        CURRENT_STATE = "request_sent"
                        binding.bnSendRequest2.text = "Cancel request"
                    } else if (requset_type == "received") {
                        CURRENT_STATE = "request_received"
                        binding.bnSendRequest2.text = "Accept request"
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun showPost() {
        postRef!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                imgs.clear()
                for (snapshot in dataSnapshot.children) {
                    val img = Img()
                    img.imgId = snapshot.child("postid").getValue(String::class.java).toString()
                    img.publisher =
                        snapshot.child("publisher").getValue(String::class.java).toString()
                    img.img = snapshot.child("postimage").getValue(String::class.java).toString()
                    img.text = snapshot.child("description").getValue(String::class.java).toString()
                    img.views = (snapshot.child("views").childrenCount-1).toString()
                    img.timestamp = snapshot.child("timestamp").getValue(String::class.java).toString()
                    if (img.publisher.equals(friendId)) {
                        imgs.add(img)
                    }
                }
                imgs.reverse()
                profileAdapter?.setData(imgs)
                showNumbers()
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }


    private fun showNumbers() {
        friendsRef!!.child(friendId!!).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                binding.tvFriends.text = dataSnapshot.childrenCount.toInt().toString()
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
        binding.tvPosts.text = imgs.size.toString()
        var sum = 0
        for (i in imgs.indices) {
            sum += imgs.get(i).views.toInt()
        }
        var all=sum - imgs.size
        if(all<0)
            all=0
        binding.tvViews.text = (all).toString()
        
        likeRef!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var sum = 0
                for (snapshot in dataSnapshot.children) {
                    for (i in imgs.indices) {
                        if (snapshot.key == imgs.get(i).imgId) {
                            sum += snapshot.childrenCount.toInt()
                        }
                    }
                }
                binding.tvLikes.text = sum.toString()
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
        dislikeRef?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var sum = 0
                for (snapshot in dataSnapshot.children) {
                    for (i in imgs.indices) {
                        if (snapshot.key == imgs.get(i).imgId) {
                            sum += snapshot.childrenCount.toInt()
                        }
                    }
                }
                binding.tvDislikes.text = sum.toString()
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
        /*for(int i=0;i<likes.size();i++){
            for(int j=0;j<likes.size();j++){
                if(likes.get(i).equals(posts.get(j).getPostId())){
                    likesNum.add(likes.get(i));
                    break;
                }
            }
        }*/
        //tvLikes.setText(String.valueOf(likesNum.size()));
    }

    private fun removeFriend() {
        friendsRef!!.child(senderId!!).child(friendId!!).removeValue()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    friendsRef!!.child(friendId!!).child(senderId!!)
                        .removeValue().addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                CURRENT_STATE = "not_friends"
                            }
                        }
                }
            }
    }

    private fun sendFriendRequest() {
        friendRequestRef!!.child(senderId!!).child(friendId!!).child("request_type")
            .setValue("sent")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    friendRequestRef!!.child(friendId!!).child(senderId!!).child("request_type")
                        .setValue("received").addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                CURRENT_STATE = "request_sent"
                                binding.bnSendRequest2.text = "Cancel request"
                            }
                        }
                }
            }
    }

    private fun cancelFriendRequest() {
        friendRequestRef!!.child(senderId!!).child(friendId!!).removeValue()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    friendRequestRef!!.child(friendId!!).child(senderId!!).child("request_type")
                        .removeValue().addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                CURRENT_STATE = "not_friends"
                                binding.bnSendRequest2.text = "Add friend"
                            }
                        }
                }
            }
    }

    private fun acceptFriendRequest() {
        val calForDate = Calendar.getInstance()
        val currentDate = SimpleDateFormat("dd-MMMM-yyyy")
        saveCurrentDate = currentDate.format(calForDate.time)
        friendsRef!!.child(senderId!!).child(friendId!!).child("date").setValue(saveCurrentDate)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    friendsRef!!.child(friendId!!).child(senderId!!).child("date")
                        .setValue(saveCurrentDate)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                friendRequestRef!!.child(senderId!!).child(friendId!!).removeValue()
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            friendRequestRef!!.child(friendId!!).child(senderId!!)
                                                .child("request_type")
                                                .removeValue().addOnCompleteListener { task ->
                                                    if (task.isSuccessful) {
                                                        CURRENT_STATE = "friends"
                                                    }
                                                }
                                        }
                                    }
                            }
                        }
                }
            }
    }

    override fun onResume() {
        super.onResume()
        maintanceOfButtons()
        showData()
        showPost()
        showData2()

        binding.removeFriend.setOnClickListener(View.OnClickListener {
            if (CURRENT_STATE.equals("friends")) {
                val dialog = Dialog(requireActivity())
                dialog.setContentView(R.layout.remove_dialog)
                dialog.setCanceledOnTouchOutside(false)
                dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                val bnRemove = dialog.findViewById<Button>(R.id.bnRemove)
                bnRemove.setOnClickListener {
                    removeFriend()
                    /*binding.rlFriends.visibility = View.INVISIBLE;
                    binding.rlNotFriends.visibility = View.VISIBLE;*/
                    dialog.dismiss()
                }
                val bnCancle = dialog.findViewById<Button>(R.id.bnCancle)
                bnCancle.setOnClickListener { dialog.dismiss() }
                dialog.show()
            }
        })

        binding.bnSendRequest2.setOnClickListener(View.OnClickListener {
            if (CURRENT_STATE.equals("request_received")) {
                acceptFriendRequest()
                /* binding.rlFriends.visibility = View.VISIBLE;
                 binding.rlNotFriends.visibility = View.INVISIBLE;*/
            } else if (CURRENT_STATE.equals("not_friends")) {
                sendFriendRequest()
            } else if (CURRENT_STATE.equals("request_sent")) {
                cancelFriendRequest()
            }
        })
    }

    override fun onItemClick(position: Int) {
        val action = ProfileOtherFragmentDirections.profileOtherToImgList(position,friendId!!)
        findNavController().navigate(action)
    }

    override fun onItemLongClick(position: Int) {

    }

}