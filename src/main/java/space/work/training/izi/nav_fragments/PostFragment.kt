package space.work.training.izi.nav_fragments

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import dagger.hilt.android.AndroidEntryPoint
import space.work.training.izi.R
import space.work.training.izi.ViewUtils
import space.work.training.izi.adapters.CommentsAdapter
import space.work.training.izi.databinding.FragmentPostBinding
import space.work.training.izi.model.ModelComment
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
            ViewUtils.expand(binding.detailsLayout)
            binding.postDetails.visibility = View.GONE
            binding.usernameLayout.visibility = View.GONE
        }

        binding.hideDetails.setOnClickListener {
            ViewUtils.collapse(binding.detailsLayout)
            binding.postDetails.visibility = View.VISIBLE
            binding.usernameLayout.visibility = View.VISIBLE
        }

        binding.comments.setOnClickListener{
            ViewUtils.collapse(binding.detailsLayout)
            if(currentUserID!!.equals(img!!.publisher)){
                val action = PostFragmentDirections.postToCommentList(imgId!!)
                findNavController().navigate(action)
            }else{
                val action = PostFragmentDirections.postToComment(imgId!!,currentUserID!!)
                findNavController().navigate(action)
            }
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
        binding.like.setOnClickListener {
            if (binding.like.tag == "like" && binding.dislike.tag == "dislike") {
                firebaseDatabase!!.reference.child("Likes").child(imgId!!).child(currentUserID!!)
                    .setValue(true)
            } else {
                firebaseDatabase!!.reference.child("Likes").child(imgId!!).child(currentUserID!!)
                    .removeValue()
            }
        }
        binding.dislike.setOnClickListener {
            if (binding.dislike.tag == "dislike" && binding.like.tag == "like") {
                firebaseDatabase!!.reference.child("Dislikes").child(imgId!!).child(currentUserID!!)
                    .setValue(true)
            } else {
                firebaseDatabase!!.reference.child("Dislikes").child(imgId!!).child(currentUserID!!)
                    .removeValue()
            }
        }
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