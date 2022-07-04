package space.work.training.izi.nav_fragments

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.Button
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import space.work.training.izi.R
import space.work.training.izi.adapters.HomeAdapter
import space.work.training.izi.databinding.FragmentProfileOtherBinding
import space.work.training.izi.mvvm.chat.User
import space.work.training.izi.mvvm.posts.Img
import java.text.SimpleDateFormat
import java.util.*

class ProfileOtherFragment : Fragment(), HomeAdapter.OnItemClickListener {

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

    private var profileRManager: GridLayoutManager? = null
    private var homeAdapter: HomeAdapter? = null
    private val imgs: List<Img> = ArrayList<Img>()

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
        friendRequestRef = firebaseDatabase!!.reference.child("FirendRequests")
        friendsRef = firebaseDatabase!!.reference.child("Friends")
        likeRef = firebaseDatabase!!.getReference("Likes")
        dislikeRef = firebaseDatabase!!.getReference("Dislikes")

        senderId = user!!.uid
        CURRENT_STATE = "not_friends"

        profileRManager = GridLayoutManager(requireContext(), 3)
        binding.profileRecycler.setLayoutManager(profileRManager)
        homeAdapter = HomeAdapter(requireContext(), this)
        binding.profileRecycler.setAdapter(homeAdapter)

        /* binding.bnSendMessage.setOnClickListener(View.OnClickListener {
             val intent = Intent(this@Profile_Other_Activity, ChatActivity::class.java)
             intent.putExtra("receiverID", userID)
             startActivity(intent)
         })*/

        binding.tvNameFull.setOnClickListener(View.OnClickListener {
            if (binding.rlBio.getVisibility() == View.GONE) {
                binding.rlBio.setVisibility(View.VISIBLE)
                binding.ivArrow.setImageResource(R.drawable.ic_arrow_up)
            } else {
                binding.rlBio.setVisibility(View.GONE)
                binding.ivArrow.setImageResource(R.drawable.ic_arrow_down)
            }
        })

        friendsRef!!.child(senderId!!).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (!dataSnapshot.hasChild(friendId!!)) {
                    binding.rlFriends.setVisibility(View.INVISIBLE)
                    binding.rlNotFriends.setVisibility(View.VISIBLE)

                } else {
                    binding.rlFriends.setVisibility(View.VISIBLE)
                    binding.rlNotFriends.setVisibility(View.INVISIBLE)

                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun showData2() {
        databaseReference!!.child(friendId!!).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = User()
                user.uid=friendId
                user.setName(dataSnapshot.child("name").getValue(String::class.java))
                user.setUsername(dataSnapshot.child("username").getValue(String::class.java))
                user.setEmail(dataSnapshot.child("email").getValue(String::class.java))
                user.setImage(dataSnapshot.child("image").getValue(String::class.java))
                user.setBio(dataSnapshot.child("bio").getValue(String::class.java))
                binding.tvUsername.setText(user.getUsername())
                binding.tvName2.setText(user.getName())
                Glide.with(getApplicationContext()).load(user.getImage()).into( binding.ivProfile)
                Glide.with(getApplicationContext()).load(R.drawable.background).into( binding.ivBackground)
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
                user.setUid(friendId)
                user.setName(dataSnapshot.child("name").getValue(String::class.java))
                user.setUsername(dataSnapshot.child("username").getValue(String::class.java))
                user.setEmail(dataSnapshot.child("email").getValue(String::class.java))
                user.setImage(dataSnapshot.child("image").getValue(String::class.java))
                user.setBio(dataSnapshot.child("bio").getValue(String::class.java))
                //if(user.getUid().equals(userID)){
                binding.tvName.setText(user.getUsername())
                binding.tvNameFull.setText(user.getName())
                binding.tvBio.setText(user.getBio())
                Glide.with(requireContext()).load(user.getImage()).into(binding.ivMalaSlika1)
                // }
                //setupToolbar();
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
                    binding.bnSendRequest2.setText("Add friend")
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
                        binding.bnSendRequest2.setText("Cancel request")
                    } else if (requset_type == "recieved") {
                        CURRENT_STATE = "request_recieved"
                        binding.bnSendRequest2.setText("Accept request")
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
                    img.setPostId(snapshot.child("postid").getValue(String::class.java))
                    img.setPublisher(snapshot.child("publisher").getValue(String::class.java))
                    img.setImg(snapshot.child("postimage").getValue(String::class.java))
                    img.setText(snapshot.child("description").getValue(String::class.java))
                    img.setViews(snapshot.child("views").childrenCount.toString())
                    if (img.getPublisher().equals(friendId)) {
                        imgs.add(img)
                    }
                }
                Collections.reverse(imgs)
                homeAdapter.setData(imgs)
                showNumbers()
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }


    private fun showNumbers() {
        friendsRef!!.child(friendId!!).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                binding.tvFriends.setText(dataSnapshot.childrenCount.toInt().toString())
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
        binding.tvPosts.setText(imgs.size.toString())
        var sum = 0
        for (i in imgs.indices) {
            sum += imgs.get(i).getViews().toInt()
        }
        binding.tvViews.setText((sum - imgs.size).toString())
        likeRef!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var sum = 0
                for (snapshot in dataSnapshot.children) {
                    for (i in imgs.indices) {
                        if (snapshot.key == imgs.get(i).getPostId()) {
                            sum += snapshot.childrenCount.toInt()
                        }
                    }
                }
                binding.tvLikes.setText(sum.toString())
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
        dislikeRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var sum = 0
                for (snapshot in dataSnapshot.children) {
                    for (i in imgs.indices) {
                        if (snapshot.key == imgs.get(i).getPostId()) {
                            sum += snapshot.childrenCount.toInt()
                        }
                    }
                }
                binding.tvDislikes.setText(sum.toString())
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
        friendRequestRef!!.child(senderId!!).child(friendId!!).child("request_type").setValue("sent")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    friendRequestRef!!.child(friendId!!).child(senderId!!).child("request_type")
                        .setValue("recieved").addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                CURRENT_STATE = "request_sent"
                                binding.bnSendRequest2.setText("Cancel request")
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
                                binding.bnSendRequest2.setText("Add friend")
                            }
                        }
                }
            }
    }

    private fun acceptFreindRequest() {
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
            if (CURRENT_STATE == "friends") {
                val dialog = Dialog(requireContext())
                dialog.setContentView(R.layout.remove_dialog)
                dialog.setCanceledOnTouchOutside(false)
                dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                val bnRemove = dialog.findViewById<Button>(R.id.bnRemove)
                bnRemove.setOnClickListener {
                    removeFriend()
                    // llFriends.setVisibility(View.INVISIBLE);
                    // rlNotFriends.setVisibility(View.VISIBLE);
                    dialog.dismiss()
                }
                val bnCancle = dialog.findViewById<Button>(R.id.bnCancle)
                bnCancle.setOnClickListener { dialog.dismiss() }
                dialog.show()
            }
        })

        binding.bnSendRequest2.setOnClickListener(View.OnClickListener {
            if (CURRENT_STATE == "request_recieved") {
                acceptFreindRequest()
                //  llFriends.setVisibility(View.VISIBLE);
                // rlNotFriends.setVisibility(View.INVISIBLE);
            } else if (CURRENT_STATE == "not_friends") {
                sendFriendRequest()
            } else if (CURRENT_STATE == "request_sent") {
                cancelFriendRequest()
            }
        })
    }

    override fun onItemClick(position: Int) {
        TODO("Not yet implemented")
    }

}