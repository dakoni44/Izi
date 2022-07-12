package space.work.training.izi.nav_fragments

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import space.work.training.izi.R
import space.work.training.izi.adapters.CommentsAdapter
import space.work.training.izi.databinding.FragmentCommentsBinding
import space.work.training.izi.model.ModelComment
import space.work.training.izi.mvvm.chatList.User

class CommentsFragment : Fragment() {

    private lateinit var binding: FragmentCommentsBinding

    private val args: CommentsFragmentArgs by navArgs()

    private var userId: String = ""
    private var imgId: String = ""

    private var postRef: DatabaseReference? = null
    private var firebaseDatabase: FirebaseDatabase? = null
    private var databaseReference: DatabaseReference? = null
    private var currentUser: FirebaseUser? = null

    private var user1: User? = null
    private var modelComments = ArrayList<ModelComment>()
    private var commentsAdapter: CommentsAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_comments, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        user1 = User()

        userId = args.userId
        imgId = args.imgId

        firebaseDatabase = FirebaseDatabase.getInstance()
        postRef = firebaseDatabase!!.getReference("Posts").child(imgId)
        databaseReference = firebaseDatabase!!.getReference("Users")
        currentUser=FirebaseAuth.getInstance().currentUser

        currentUser()
        readComm()

        binding.tvPostComm.setOnClickListener {
            addComm()
        }

    }

    private fun currentUser() {
        databaseReference!!.child(currentUser!!.uid)
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
                    Glide.with(requireContext()).load(user1?.image).into(binding.ivComm)
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
    }

    private fun addComm() {
        val comment: String = binding.etComm.getText().toString().trim { it <= ' ' }
        if (TextUtils.isEmpty(comment)) {
            return
        } else {
            val timestamp = System.currentTimeMillis().toString()
            val hashMap = HashMap<String, Any>()
            hashMap["cId"] = timestamp
            hashMap["comment"] = comment
            hashMap["timestamp"] = timestamp
            hashMap["uId"] = userId
            hashMap["uName"] = user1!!.username
            hashMap["uPic"] = user1!!.image
            hashMap["postId"] = imgId
            postRef!!.child("Comments").child(userId).child(timestamp).setValue(hashMap)
        }
        binding.etComm.setText("")
    }

    private fun readComm() {
        postRef!!.child("Comments").child(userId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    modelComments.clear()
                    for (snapshot in dataSnapshot.children) {
                        val modelComment = ModelComment()
                        modelComment.cid =
                            snapshot.child("cId").getValue(String::class.java).toString()
                        modelComment.comment =
                            snapshot.child("comment").getValue(String::class.java).toString()
                        modelComment.timestamp =
                            snapshot.child("timestamp").getValue(String::class.java)
                                .toString()
                        modelComment.uId =
                            snapshot.child("uId").getValue(String::class.java).toString()
                        modelComment.uName =
                            snapshot.child("uName").getValue(String::class.java).toString()
                        modelComment.uPic =
                            snapshot.child("uPic").getValue(String::class.java).toString()
                        modelComment.postId =
                            snapshot.child("postId").getValue(String::class.java).toString()
                        modelComments.add(modelComment)
                    }

                    binding.rvComments.setHasFixedSize(true)
                    binding.rvComments.layoutManager = LinearLayoutManager(requireContext())
                    commentsAdapter = CommentsAdapter(requireContext(), modelComments)
                    binding.rvComments.adapter = commentsAdapter
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
    }

}