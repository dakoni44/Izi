package space.work.training.izi.nav_fragments

import android.graphics.Color
import android.os.Bundle
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.android.material.transition.MaterialContainerTransform
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import dagger.hilt.android.AndroidEntryPoint
import space.work.training.izi.R
import space.work.training.izi.databinding.FragmentPostBinding
import space.work.training.izi.mvvm.chat.User
import space.work.training.izi.mvvm.posts.Img

@AndroidEntryPoint
class PostFragment : Fragment() {

    private lateinit var binding: FragmentPostBinding

    private val args: PostFragmentArgs by navArgs()
    private var imgId: String? = null
    private var currentUserID: String? = null

    private var seen = false

    private var firebaseAuth: FirebaseAuth? = null
    private var firebaseUser: FirebaseUser? = null
    private var firebaseDatabase: FirebaseDatabase? = null
    private var databaseReference: DatabaseReference? = null
    private var postRef: DatabaseReference? = null

    var img: Img? = null
    var user: User? = null
    var user1: User? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_post, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imgId = args.imgId

        img = Img()
        user = User()
        user1 = User()

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseUser = firebaseAuth!!.currentUser
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase!!.getReference("Users")
        postRef = firebaseDatabase!!.getReference("Posts").child(imgId!!)

        currentUserID = firebaseUser!!.uid

        updatePost()
        showPost()
        // deletePost()
        isLikes(imgId!!, binding.like)
        isDislikes(imgId!!, binding.dislike)
        nrLikes(binding.like, imgId!!)
        nrDislikes(binding.dislike, imgId!!)
        likeDislike()


        currentUser()
        // readComm()

        binding.postDetails.setOnClickListener {
            val transform = MaterialContainerTransform().apply {
                startView = binding.postDetails
                endView = binding.detailsLayout
                scrimColor = Color.TRANSPARENT
                addTarget(endView as ConstraintLayout)
            }

            TransitionManager.beginDelayedTransition(binding.postLayout)
            binding.postDetails.visibility = View.GONE
            binding.usernameLayout.visibility = View.GONE
            binding.detailsLayout.visibility = View.VISIBLE
        }

        binding.hideDetails.setOnClickListener {
            val transform = MaterialContainerTransform().apply {
                startView = binding.detailsLayout
                endView = binding.postDetails
                scrimColor = Color.TRANSPARENT
                addTarget(endView as ImageView)
            }

            TransitionManager.beginDelayedTransition(binding.postLayout)
            binding.postDetails.visibility = View.VISIBLE
            binding.usernameLayout.visibility = View.VISIBLE
            binding.detailsLayout.visibility = View.GONE
        }
    }

    private fun currentUser() {
        databaseReference!!.child(currentUserID!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    user1?.uid = dataSnapshot.child("uid").getValue(String::class.java).toString()
                    user1?.name = dataSnapshot.child("name").getValue(String::class.java).toString()
                    user1?.username =
                        dataSnapshot.child("username").getValue(String::class.java).toString()
                    user1?.email =
                        dataSnapshot.child("email").getValue(String::class.java).toString()
                    user1?.image =
                        dataSnapshot.child("image").getValue(String::class.java).toString()
                    user1?.bio = dataSnapshot.child("bio").getValue(String::class.java).toString()
                    //  Glide.with(requireContext()).load(user1?.image).into(binding.ivComm)
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
    }

    private fun likeDislike() {
        binding.like.setOnClickListener(View.OnClickListener {
            if (binding.like.getTag() == "like" && binding.dislike.getTag() == "dislike") {
                firebaseDatabase!!.reference.child("Likes").child(imgId!!).child(currentUserID!!)
                    .setValue(true)
            } else {
                firebaseDatabase!!.reference.child("Likes").child(imgId!!).child(currentUserID!!)
                    .removeValue()
            }
        })
        binding.dislike.setOnClickListener(View.OnClickListener {
            if (binding.dislike.getTag() == "dislike" && binding.like.getTag() == "like") {
                firebaseDatabase!!.reference.child("Dislikes").child(imgId!!).child(currentUserID!!)
                    .setValue(true)
            } else {
                firebaseDatabase!!.reference.child("Dislikes").child(imgId!!).child(currentUserID!!)
                    .removeValue()
            }
        })
    }

    private fun isLikes(postid: String, textView: TextView) {
        val reference = firebaseDatabase!!.reference.child("Likes")
            .child(postid)
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.child(firebaseUser!!.uid).exists()) {
                    textView.background = resources.getDrawable(R.drawable.rounded_like)
                    textView.tag = "liked"
                } else {
                    textView.background = resources.getDrawable(R.drawable.rounded_likedis)
                    textView.tag = "like"
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun nrLikes(likes: TextView, postid: String) {
        val reference = firebaseDatabase!!.reference.child("Likes").child(postid)
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                likes.text = dataSnapshot.childrenCount.toString() + ""
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun isDislikes(postid: String, textView: TextView) {
        val reference = firebaseDatabase!!.reference.child("Dislikes")
            .child(postid)
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.child(firebaseUser!!.uid).exists()) {
                    textView.background = resources.getDrawable(R.drawable.rounded_dislike)
                    textView.tag = "disliked"
                } else {
                    textView.background = resources.getDrawable(R.drawable.rounded_likedis)
                    textView.tag = "dislike"
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun nrDislikes(likes: TextView, postid: String) {
        val reference = firebaseDatabase!!.reference.child("Dislikes").child(postid)
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                likes.text = dataSnapshot.childrenCount.toString() + ""
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

/*    private fun deletePost() {
        postRef!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                postUserId = dataSnapshot.child("publisher").value.toString()
                if (currentUserID == postUserId) {
                    tvDeletePost.setVisibility(View.VISIBLE)
                    tvDeletePost.setOnClickListener(View.OnClickListener {
                        val dialog = Dialog(requireContext())
                        dialog.setContentView(R.layout.remove_dialog)
                        dialog.setCanceledOnTouchOutside(false)
                        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                        val bnRemove = dialog.findViewById<Button>(R.id.bnRemove)
                        bnRemove.setOnClickListener {
                            postRef!!.removeValue()
                            val intent = Intent(this@PostActivity, ProfileActivity::class.java)
                            startActivity(intent)
                        }
                        val bnCancle = dialog.findViewById<Button>(R.id.bnCancle)
                        bnCancle.setOnClickListener { dialog.dismiss() }
                        dialog.show()
                    })
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }*/

    private fun showPost() {
        postRef!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                img?.imgId = dataSnapshot.child("postid").getValue(String::class.java).toString()
                img?.publisher =
                    dataSnapshot.child("publisher").getValue(String::class.java).toString()
                img?.img = dataSnapshot.child("postimage").getValue(String::class.java).toString()
                img?.text =
                    dataSnapshot.child("description").getValue(String::class.java).toString()
                img?.views = (dataSnapshot.child("views").childrenCount - 1).toString()
                showData()
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun showData() {
        databaseReference!!.child(img!!.publisher)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    user?.uid = dataSnapshot.child("uid").getValue(String::class.java).toString()
                    user?.name = dataSnapshot.child("name").getValue(String::class.java).toString()
                    user?.username =
                        dataSnapshot.child("username").getValue(String::class.java).toString()
                    user?.email =
                        dataSnapshot.child("email").getValue(String::class.java).toString()
                    user?.image =
                        dataSnapshot.child("image").getValue(String::class.java).toString()
                    user?.bio = dataSnapshot.child("bio").getValue(String::class.java).toString()
                    showPostUI()
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
    }

    /* private fun addComm() {
         val comment: String = etComm.getText().toString().trim { it <= ' ' }
         if (TextUtils.isEmpty(comment)) {
             return
         } else {
             val timestamp = System.currentTimeMillis().toString()
             val hashMap = HashMap<String, Any>()
             hashMap["cId"] = timestamp
             hashMap["comment"] = comment
             hashMap["timestamp"] = timestamp
             hashMap["uId"] = currentUserID!!
             hashMap["uName"] = user1.getUsername()
             hashMap["uPic"] = user1.getImage()
             hashMap["postId"] = postid
             postRef!!.child("Comments").child(timestamp).setValue(hashMap)
         }
         etComm.setText("")
     }

     private fun readComm() {
         postRef!!.child("Comments").addValueEventListener(object : ValueEventListener {
             override fun onDataChange(dataSnapshot: DataSnapshot) {
                 modelComments.clear()
                 for (snapshot in dataSnapshot.children) {
                     val modelComment = ModelComment()
                     modelComment.setCid(snapshot.child("cId").getValue(String::class.java))
                     modelComment.setComment(snapshot.child("comment").getValue(String::class.java))
                     modelComment.setTimestamp(
                         snapshot.child("timestamp").getValue(String::class.java)
                     )
                     modelComment.setuId(snapshot.child("uId").getValue(String::class.java))
                     modelComment.setuName(snapshot.child("uName").getValue(String::class.java))
                     modelComment.setuPic(snapshot.child("uPic").getValue(String::class.java))
                     modelComment.setPostId(snapshot.child("postId").getValue(String::class.java))
                     modelComments.add(modelComment)
                 }
                 tvCommNum.setText(modelComments.size.toString())
                 rvComments.setHasFixedSize(true)
                 rvComments.setLayoutManager(LinearLayoutManager(getApplicationContext()))
                 commentsAdapter = CommentsAdapter(getApplicationContext(), modelComments)
                 rvComments.setAdapter(commentsAdapter)
             }

             override fun onCancelled(databaseError: DatabaseError) {}
         })
     }*/

    private fun showPostUI() {
        Glide.with(requireContext()).load(user?.image).into(binding.profilePic)
        binding.username.text = user?.username
        Glide.with(requireContext()).load(img?.img).into(binding.postImage)
        binding.description.text = img?.text
        binding.views.text = img?.views
    }

    private fun updatePost() {
        postRef!!.child("views").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (!dataSnapshot.hasChild(currentUserID!!)) {
                    seen = true
                }
                if (seen) {
                    postRef!!.child("views").child(currentUserID!!).setValue(currentUserID)
                    seen = false
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

}