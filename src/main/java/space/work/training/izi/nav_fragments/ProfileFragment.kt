package space.work.training.izi.nav_fragments

import android.app.Dialog
import android.content.Intent
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
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import space.work.training.izi.R
import space.work.training.izi.adapters.HomeAdapter
import space.work.training.izi.databinding.FragmentProfileBinding
import space.work.training.izi.model.Users
import space.work.training.izi.mvvm.Img
import java.util.*


class ProfileFragment : Fragment(), HomeAdapter.OnItemClickListener {

    private lateinit var binding : FragmentProfileBinding

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var user: FirebaseUser
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private lateinit var postRef: DatabaseReference
    private lateinit var likeRef: DatabaseReference
    private lateinit var dislRef: DatabaseReference
    private lateinit var friendRef: DatabaseReference

    private var gridManager: GridLayoutManager? = null
    private var homeAdapter: HomeAdapter? = null
    private val imgs: ArrayList<Img> = ArrayList<Img>()

    private var userID: String? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()
        user = firebaseAuth.currentUser!!
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.getReference("Users")
        postRef = firebaseDatabase.getReference("Posts")
        likeRef = firebaseDatabase.getReference("Likes")
        dislRef = firebaseDatabase.getReference("Dislikes")
        friendRef = firebaseDatabase.getReference("Friends")


        userID = user.uid
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        gridManager = GridLayoutManager(requireContext(), 3)
        binding.profileRecycler.setLayoutManager(gridManager)
        homeAdapter = HomeAdapter(requireContext(), this)
        binding.profileRecycler.setAdapter(homeAdapter)

        binding.bnEditProfile.setOnClickListener(View.OnClickListener {
           // findNavController().navigate(R.id.profileToEdit)
        })

        binding.bnLogout.setOnClickListener(View.OnClickListener {
            val dialog = Dialog(requireContext())
            dialog.setContentView(R.layout.logout_dialog)
            dialog.setCanceledOnTouchOutside(false)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val bnLogout = dialog.findViewById<Button>(R.id.bnLogout)
            bnLogout.setOnClickListener {
                firebaseAuth.signOut()
                findNavController().navigate(R.id.profileToLogIn)
            }
            val bnCancle = dialog.findViewById<Button>(R.id.bnCancle)
            bnCancle.setOnClickListener { dialog.dismiss() }
            dialog.show()
        })

        binding.tvNameFull.setOnClickListener(View.OnClickListener {
            if (binding.rlBio.getVisibility() == View.GONE) {
                binding.rlBio.setVisibility(View.VISIBLE)
                binding.ivArrow.setImageResource(R.drawable.ic_arrow_up)
            } else {
                binding.rlBio.setVisibility(View.GONE)
                binding. ivArrow.setImageResource(R.drawable.ic_arrow_down)
            }
        })
    }

    private fun showData() {
        databaseReference.child(userID!!).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = Users()
                user.uid=userID as String
                user.name=dataSnapshot.child("name").getValue(String::class.java).toString()
                user.username=dataSnapshot.child("username").getValue(String::class.java).toString()
                user.email=dataSnapshot.child("email").getValue(String::class.java).toString()
                user.image=dataSnapshot.child("image").getValue(String::class.java).toString()
                user.bio=dataSnapshot.child("bio").getValue(String::class.java).toString()
                if (user.uid.equals(userID)) {
                    binding.tvName.setText(user.username)
                    binding.tvNameFull.setText(user.name)
                    binding.tvBio.setText(user.bio)
                    Glide.with(requireContext()).load(user.image).into(binding.ivMalaSlika1)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun showPost() {
        postRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                imgs.clear()
                for (snapshot in dataSnapshot.children) {
                    val img = Img()
                    img.imgId=snapshot.child("postid").getValue(String::class.java).toString()
                    img.publisher=snapshot.child("publisher").getValue(String::class.java).toString()
                    img.img=snapshot.child("postimage").getValue(String::class.java).toString()
                    img.text=snapshot.child("description").getValue(String::class.java).toString()
                    img.views=snapshot.child("views").childrenCount.toString().toString()
                    if (img.publisher.equals(userID)) {
                        imgs.add(img)
                    }
                }
                Collections.reverse(imgs)
                homeAdapter!!.notifyDataSetChanged()
                showNumbers()
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun showNumbers() {

        friendRef.child(userID!!).addValueEventListener(object : ValueEventListener {
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
        binding.tvViews.setText((sum - imgs.size).toString())

        likeRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var sum = 0
                for (snapshot in dataSnapshot.children) {
                    for (i in imgs.indices) {
                        if (snapshot.key == imgs.get(i).imgId) {
                            sum += snapshot.childrenCount.toInt()
                        }
                    }
                }
                binding.tvLikes.setText(sum.toString())
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })

        dislRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var sum = 0
                for (snapshot in dataSnapshot.children) {
                    for (i in imgs.indices) {
                        if (snapshot.key == imgs.get(i).imgId) {
                            sum += snapshot.childrenCount.toInt()
                        }
                    }
                }
                binding.tvDislikes.text=sum.toString()
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    override fun onItemClick(position: Int) {
        TODO("Not yet implemented")
    }

     override fun onResume() {
        super.onResume()
        showData()
        showPost()
    }
}