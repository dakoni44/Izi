package space.work.training.izi.nav_fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import dagger.hilt.android.AndroidEntryPoint
import space.work.training.izi.R
import space.work.training.izi.adapters.CommentListAdapter
import space.work.training.izi.databinding.FragmentCommentListBinding

@AndroidEntryPoint
class CommentListFragment : Fragment(), CommentListAdapter.OnItemClickListener {

    private val args: CommentListFragmentArgs by navArgs()
    private lateinit var binding: FragmentCommentListBinding

    private var imgId: String = ""

    private var postRef: DatabaseReference? = null
    private var firebaseDatabase: FirebaseDatabase? = null

    private var userList: ArrayList<String> = ArrayList()
    private var adapter: CommentListAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_comment_list, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imgId = args.imgId

        firebaseDatabase = FirebaseDatabase.getInstance()
        postRef = firebaseDatabase!!.getReference("Posts").child(imgId)

        binding.commentList.setHasFixedSize(true)
        binding.commentList.layoutManager = LinearLayoutManager(requireContext())
        adapter = CommentListAdapter(requireContext(), this)
        binding.commentList.adapter = adapter

        getUsers()
    }

    private fun getUsers() {
        userList.clear()
        postRef!!.child("Comments")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (snapshot in dataSnapshot.children) {
                        userList.add(snapshot.key.toString())
                    }
                    adapter!!.setData(userList)
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
    }

    override fun onItemClick(position: Int) {
        val action =
            CommentListFragmentDirections.commentListToComments(imgId, userList.get(position))
        findNavController().navigate(action)
    }
}