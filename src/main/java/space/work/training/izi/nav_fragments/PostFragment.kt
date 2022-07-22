package space.work.training.izi.nav_fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.AndroidEntryPoint
import space.work.training.izi.R
import space.work.training.izi.ViewUtils
import space.work.training.izi.adapters.CommentListAdapter
import space.work.training.izi.adapters.DislikeAdapter
import space.work.training.izi.adapters.LikesAdapter
import space.work.training.izi.databinding.FragmentPostBinding
import space.work.training.izi.mvvm.SinglePost.PostViewModel
import space.work.training.izi.mvvm.chatList.User
import space.work.training.izi.mvvm.posts.Img
import javax.inject.Inject


@AndroidEntryPoint
class PostFragment : Fragment(), CommentListAdapter.OnItemClickListener,
    LikesAdapter.OnItemClickListener, DislikeAdapter.OnItemClickListener {

    private lateinit var binding: FragmentPostBinding

    private val args: PostFragmentArgs by navArgs()
    private var imgId: String? = null
    private var currentUserID: String? = null
    private var imgPublisher: String? = null

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    @Inject
    lateinit var firebaseDatabase: FirebaseDatabase

    private val postViewModel: PostViewModel by viewModels()

    private var viewsAdapter: CommentListAdapter? = null
    private var likedAdapter: LikesAdapter? = null
    private var dislikedAdapter: DislikeAdapter? = null
    private var viewsList: ArrayList<String> = ArrayList()
    private var likedList: ArrayList<String> = ArrayList()
    private var dislikedList: ArrayList<String> = ArrayList()

    var img: Img? = null
    var user: User? = null


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
        postViewModel.setImgID(imgId!!)
        postViewModel.load()

        img = Img()
        user = User()

        currentUserID = firebaseAuth.currentUser!!.uid

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
            if (currentUserID!!.equals(imgPublisher)) {
                val action = PostFragmentDirections.postToCommentList(imgId!!)
                findNavController().navigate(action)
            } else {
                val action = PostFragmentDirections.postToComment(imgId!!, currentUserID!!)
                findNavController().navigate(action)
            }
        }

        postViewModel.getViews().observe(viewLifecycleOwner) {
            viewsAdapter?.setData(it)
            viewsList.clear()
            viewsList.addAll(it)
        }

        postViewModel.getLikes().observe(viewLifecycleOwner) {
            likedAdapter?.setData(it)
            likedList.clear()
            likedList.addAll(it)
        }

        postViewModel.getDislikes().observe(viewLifecycleOwner) {
            dislikedAdapter?.setData(it)
            dislikedList.clear()
            dislikedList.addAll(it)
        }

        postViewModel.getNrLikes().observe(viewLifecycleOwner) {
            Log.d("LiveLikes : PostFragment", it)
            binding.like.text = it
            binding.liked.text = it
        }
        postViewModel.getNrDislikes().observe(viewLifecycleOwner) {
            binding.dislike.text = it
            binding.disliked.text = it
        }

        postViewModel.getLike().observe(viewLifecycleOwner) {
            if (it) {
                binding.like.background = resources.getDrawable(R.drawable.rounded_like)
                binding.like.tag = "liked"
            } else {
                binding.like.background = resources.getDrawable(R.drawable.rounded_likedis)
                binding.like.tag = "like"
            }
        }

        postViewModel.getDislike().observe(viewLifecycleOwner) {
            if (it) {
                binding.dislike.background = resources.getDrawable(R.drawable.rounded_dislike)
                binding.dislike.tag = "disliked"
            } else {
                binding.dislike.background = resources.getDrawable(R.drawable.rounded_likedis)
                binding.dislike.tag = "dislike"
            }
        }

        postViewModel.getMyImg().observe(viewLifecycleOwner) {
            if (it) {
                binding.reactions.visibility = View.VISIBLE
                availableData(it)
            } else {
                availableData(it)
                binding.reactions.visibility = View.INVISIBLE
            }
        }

        postViewModel.getImg().observe(viewLifecycleOwner) {
            imgPublisher = it.publisher
            showData(it)
        }

        likeDislike()
    }

    private fun availableData(currentUser: Boolean) {
        binding.views.setOnClickListener {
            if (currentUser) {
                ViewUtils.collapse(binding.detailsLayout)
                binding.clViews.visibility = View.VISIBLE
                binding.clViews.alpha = 0f
                binding.clViews.animate()
                    .alpha(1f)
                    .setDuration(400)
                    .setInterpolator(AccelerateInterpolator())
                    .start()
            }
        }

        binding.hideViews.setOnClickListener {
            if (currentUser) {
                val handler = Handler(Looper.getMainLooper()).postDelayed({
                    binding.clViews.visibility = View.INVISIBLE
                }, 200)
                ViewUtils.expand(binding.detailsLayout)
            }
        }

        binding.reactions.setOnClickListener {
            if (currentUser) {
                ViewUtils.collapse(binding.detailsLayout)
                binding.clReactions.visibility = View.VISIBLE
                binding.clReactions.alpha = 0f
                binding.clReactions.animate()
                    .alpha(1f)
                    .setDuration(400)
                    .setInterpolator(AccelerateInterpolator())
                    .start()
            }
        }

        binding.hideReactions.setOnClickListener {
            if (currentUser) {
                val handler = Handler(Looper.getMainLooper()).postDelayed({
                    binding.clReactions.visibility = View.INVISIBLE
                }, 200)
                ViewUtils.expand(binding.detailsLayout)
            }
        }
    }

    //ostaje
    private fun likeDislike() {
        binding.like.setOnClickListener {
            if (binding.like.tag == "like" && binding.dislike.tag == "dislike") {
                firebaseDatabase.reference.child("Likes").child(imgId!!).child(currentUserID!!)
                    .setValue(true)
            } else {
                firebaseDatabase.reference.child("Likes").child(imgId!!).child(currentUserID!!)
                    .removeValue()
            }
        }
        binding.dislike.setOnClickListener {
            if (binding.dislike.tag == "dislike" && binding.like.tag == "like") {
                firebaseDatabase.reference.child("Dislikes").child(imgId!!).child(currentUserID!!)
                    .setValue(true)
            } else {
                firebaseDatabase.reference.child("Dislikes").child(imgId!!).child(currentUserID!!)
                    .removeValue()
            }
        }
    }

    //ostaje
    private fun showData(img: Img) {
        firebaseDatabase.getReference("Users").child(img.publisher)
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
                    showPostUI(img, user!!)
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
    }

    private fun showPostUI(img: Img, user: User) {
        Glide.with(requireContext()).load(user.image).into(binding.profilePic)
        binding.username.text = user.username
        Glide.with(requireContext()).load(img.img).into(binding.postImage)
        binding.description.text = img.text
        binding.views.text = img.views
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