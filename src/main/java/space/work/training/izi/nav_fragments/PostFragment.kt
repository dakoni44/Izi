package space.work.training.izi.nav_fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.widget.TextView
import android.widget.Toast
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
import space.work.training.izi.adapters.CommentListAdapter
import space.work.training.izi.adapters.DislikeAdapter
import space.work.training.izi.adapters.LikesAdapter
import space.work.training.izi.databinding.FragmentPostBinding
import space.work.training.izi.mvvm.chat.User
import space.work.training.izi.mvvm.posts.Img
import java.util.*
import kotlin.collections.ArrayList


@AndroidEntryPoint
class PostFragment : Fragment(), CommentListAdapter.OnItemClickListener,
    LikesAdapter.OnItemClickListener, DislikeAdapter.OnItemClickListener {

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
    private var likedRef: DatabaseReference? = null
    private var dislikedRef: DatabaseReference? = null

    private var viewsAdapter: CommentListAdapter? = null
    private var likedAdapter: LikesAdapter? = null
    private var dislikedAdapter: DislikeAdapter? = null
    private var viewsList: ArrayList<String> = ArrayList()
    private var likedList: ArrayList<String> = ArrayList()
    private var dislikedList: ArrayList<String> = ArrayList()

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
        likedRef = firebaseDatabase!!.getReference("Likes").child(imgId!!)
        dislikedRef = firebaseDatabase!!.getReference("Dislikes").child(imgId!!)

        currentUserID = firebaseUser!!.uid

        binding.rvViews.setHasFixedSize(true)
        binding.rvViews.layoutManager = LinearLayoutManager(requireContext())
        viewsAdapter = CommentListAdapter(requireContext(), this)
        binding.rvViews.adapter = viewsAdapter

        binding.rvLikes.setHasFixedSize(true)
        binding.rvLikes.layoutManager = LinearLayoutManager(requireContext())
        likedAdapter = LikesAdapter(requireContext(), this)
        binding.rvLikes.adapter = likedAdapter

        binding.rvDislikes.setHasFixedSize(true)
        binding.rvDislikes.layoutManager = LinearLayoutManager(requireContext())
        dislikedAdapter = DislikeAdapter(requireContext(), this)
        binding.rvDislikes.adapter = dislikedAdapter

        updatePost()
        showPost()
        // deletePost()
        isLikes(imgId!!, binding.like)
        isDislikes(imgId!!, binding.dislike)
        nrLikes(binding.like, binding.liked, imgId!!)
        nrDislikes(binding.dislike, binding.disliked, imgId!!)

        loadReactions()
        loadViews()

        currentUser()

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

        binding.comments.setOnClickListener {
            ViewUtils.collapse(binding.detailsLayout)
            if (currentUserID!!.equals(img!!.publisher)) {
                val action = PostFragmentDirections.postToCommentList(imgId!!)
                findNavController().navigate(action)
            } else {
                val action = PostFragmentDirections.postToComment(imgId!!, currentUserID!!)
                findNavController().navigate(action)
            }
        }


    }

    private fun availableData() {
        binding.views.setOnClickListener {
            ViewUtils.collapse(binding.detailsLayout)
            binding.clViews.visibility = View.VISIBLE
            binding.clViews.alpha = 0f
            binding.clViews.animate()
                .alpha(1f)
                .setDuration(400)
                .setInterpolator(AccelerateInterpolator())
                .start()
        }

        binding.hideViews.setOnClickListener {
            val handler = Handler(Looper.getMainLooper()).postDelayed({
                binding.clViews.visibility = View.INVISIBLE
            }, 200)
            ViewUtils.expand(binding.detailsLayout)
        }

        binding.reactions.setOnClickListener {
            ViewUtils.collapse(binding.detailsLayout)
            binding.clReactions.visibility = View.VISIBLE
            binding.clReactions.alpha = 0f
            binding.clReactions.animate()
                .alpha(1f)
                .setDuration(400)
                .setInterpolator(AccelerateInterpolator())
                .start()

        }

        binding.hideReactions.setOnClickListener {
            val handler = Handler(Looper.getMainLooper()).postDelayed({
                binding.clReactions.visibility = View.INVISIBLE
            }, 200)
            ViewUtils.expand(binding.detailsLayout)
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

    private fun loadViews() {
        postRef!!.child("views").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                viewsList.clear()
                for (snapshot in dataSnapshot.children) {
                    val id = snapshot.getValue(String::class.java).toString()
                    if (!id.equals(currentUserID))
                        viewsList.add(id)
                }
                viewsAdapter?.setData(viewsList)
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun loadReactions() {
        likedRef!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                likedList.clear()
                for (snapshot in dataSnapshot.children) {
                    likedList.add(snapshot.key.toString())
                }
                likedAdapter?.setData(likedList)
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })

        dislikedRef!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dislikedList.clear()
                for (snapshot in dataSnapshot.children) {
                    dislikedList.add(snapshot.key.toString())
                }
                dislikedAdapter?.setData(dislikedList)
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

    private fun nrLikes(likes: TextView, liked: TextView, postid: String) {
        val reference = firebaseDatabase!!.reference.child("Likes").child(postid)
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                likes.text = dataSnapshot.childrenCount.toString() + ""
                liked.text = dataSnapshot.childrenCount.toString() + ""
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

    private fun nrDislikes(dislike: TextView, disliked: TextView, postid: String) {
        val reference = firebaseDatabase!!.reference.child("Dislikes").child(postid)
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dislike.text = dataSnapshot.childrenCount.toString() + ""
                disliked.text = dataSnapshot.childrenCount.toString() + ""
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
                img?.timestamp = dataSnapshot.child("timestamp").getValue(String::class.java).toString()
                showData()
                if (currentUserID.equals(img?.publisher)) {
                    binding.reactions.visibility = View.VISIBLE
                    availableData()
                }else{
                    likeDislike()
                }
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

    override fun onItemClick(position: Int) {
        val action = PostFragmentDirections.postToProfileOther(viewsList.get(position))
        findNavController().navigate(action)
    }

    override fun onDislikeClick(position: Int) {
        val action = PostFragmentDirections.postToComment(imgId!!, dislikedList.get(position))
        findNavController().navigate(action)
    }

    override fun onLikeClick(position: Int) {
        val action = PostFragmentDirections.postToComment(imgId!!, likedList.get(position))
        findNavController().navigate(action)
    }

}