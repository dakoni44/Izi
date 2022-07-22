package space.work.training.izi.nav_fragments

import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import dagger.hilt.android.AndroidEntryPoint
import space.work.training.izi.R
import space.work.training.izi.adapters.ImgListAdapter
import space.work.training.izi.databinding.FragmentImgsListBinding
import space.work.training.izi.model.Img

@AndroidEntryPoint
class ImgsListFragment : Fragment(), ImgListAdapter.OnItemClickListener {

    private lateinit var binding: FragmentImgsListBinding

    private val args: ImgsListFragmentArgs by navArgs()

    private var firebaseAuth: FirebaseAuth? = null
    private var user: FirebaseUser? = null
    private var firebaseDatabase: FirebaseDatabase? = null
    private var databaseReference: DatabaseReference? = null
    private var postRef: DatabaseReference? = null

    private var linearLayoutManager: LinearLayoutManager? = null
    private var imgListAdapter: ImgListAdapter? = null
    private val imgs: ArrayList<Img> = ArrayList<Img>()

    var position = 0
    var userID = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_imgs_list, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()
        user = firebaseAuth!!.currentUser
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase!!.getReference("Users")
        postRef = firebaseDatabase!!.getReference("Posts")

        userID = args.userId
        position = args.position

        linearLayoutManager = LinearLayoutManager(requireContext())
        binding.rvImgsList.layoutManager = linearLayoutManager
        binding.rvImgsList.addItemDecoration(
            ItemDecorator(
                -50
            )
        )
        imgListAdapter = ImgListAdapter(requireContext(), this)
        binding.rvImgsList.adapter = imgListAdapter
        val handler = Handler()
        handler.postDelayed(Runnable { binding.rvImgsList.scrollToPosition(position) }, 500)


        showPost()
    }

    class ItemDecorator(private val mSpace: Int) : ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            val position = parent.getChildAdapterPosition(view)
            if (position != 0) outRect.top = mSpace
        }
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
                    if (img.publisher.equals(userID)) {
                        imgs.add(img)
                    }
                }
                imgs.reverse()
                imgListAdapter!!.setData(imgs)
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    override fun onListItemClick(position: Int) {
        val action = ImgsListFragmentDirections.imgListToPost(imgs.get(position).imgId)
        findNavController().navigate(action)
    }
}