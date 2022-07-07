package space.work.training.izi.nav_fragments

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import space.work.training.izi.R
import space.work.training.izi.adapters.ParticipantsAdapter
import space.work.training.izi.databinding.FragmentAddGroupBinding
import space.work.training.izi.mvvm.chat.User

class AddGroupFragment : Fragment() {

    private lateinit var binding:FragmentAddGroupBinding

    private var friendList: ArrayList<String>? = null
    private var list: ArrayList<User> = ArrayList<User>()

    private var firebaseAuth: FirebaseAuth? = null
    private var user: FirebaseUser? = null
    private var firebaseDatabase: FirebaseDatabase? = null
    private var databaseReference: DatabaseReference? = null
    private var groupRef:DatabaseReference? = null

    private var userID: String? = null
    private var timestamp: String? = null

    var adapter: ParticipantsAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_group, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        timestamp = "" + System.currentTimeMillis()

        firebaseAuth = FirebaseAuth.getInstance()
        user = firebaseAuth!!.currentUser
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase!!.getReference("Users")
        groupRef = firebaseDatabase!!.getReference("Groups")
        userID = user!!.uid

        binding.rvParticipants.setHasFixedSize(true)
        binding.rvParticipants.layoutManager = LinearLayoutManager(requireContext())
        adapter = ParticipantsAdapter(requireContext(), list, timestamp!!)

        checkFollowing()

        binding.next.setOnClickListener(View.OnClickListener {
            if (!binding.etGroupName.text.toString().trim { it <= ' ' }.isEmpty()) {
                createGroup()
                binding.rvParticipants.visibility = View.VISIBLE
                binding.next.visibility = View.INVISIBLE
            } else {
                Toast.makeText(
                    requireContext(),
                    "Group name is required",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

        binding.ivCreate.setOnClickListener(View.OnClickListener { v ->
            val alertDialogBuilder = AlertDialog.Builder(v.rootView.context)
            alertDialogBuilder.setMessage("Confirm")
            alertDialogBuilder.setPositiveButton(
                "Ok"
            ) { _, _ ->
                val action = AddGroupFragmentDirections.addGroupToChatList(true)
               findNavController().navigate(action)
            }
            alertDialogBuilder.setNegativeButton(
                "Cancel"
            ) { arg0, arg1 -> }
            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK)
        })

        binding.ivBack.setOnClickListener(View.OnClickListener { v ->
            val alertDialogBuilder = AlertDialog.Builder(v.rootView.context)
            alertDialogBuilder.setMessage("Confirm")
            alertDialogBuilder.setPositiveButton(
                "Ok"
            ) { _, _ ->
                groupRef!!.child(timestamp!!).removeValue()
                val action = AddGroupFragmentDirections.addGroupToChatList(true)
                findNavController().navigate(action)
            }
            alertDialogBuilder.setNegativeButton(
                "Cancel"
            ) { arg0, arg1 -> }
            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK)
        })

    }

    private fun checkFollowing() {
        friendList = java.util.ArrayList()
        val reference = FirebaseDatabase.getInstance().getReference("Friends")
            .child(userID!!)
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                friendList!!.clear()
                for (snapshot in dataSnapshot.children) {
                    friendList!!.add(snapshot.key!!)
                }
                loadParticipants()
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun loadParticipants() {
        databaseReference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                list.clear()
                for (snapshot in dataSnapshot.children) {
                    val user = User()
                    user.uid=snapshot.child("uid").getValue(String::class.java).toString()
                    user.name=snapshot.child("name").getValue(String::class.java).toString()
                    user.username=snapshot.child("username").getValue(String::class.java).toString()
                    user.email=snapshot.child("email").getValue(String::class.java).toString()
                    user.image=snapshot.child("image").getValue(String::class.java).toString()
                    user.bio=dataSnapshot.child("bio").getValue(String::class.java).toString()
                    for (id in friendList!!) {
                        if (user.uid.equals(id)) {
                            list.add(user)
                            break
                        }
                    }
                }
                binding.rvParticipants.adapter = adapter
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun createGroup() {
        val groupName: String = binding.etGroupName.text.toString().trim { it <= ' ' }
        val hashMap = HashMap<String, Any>()
        hashMap["groupId"] = timestamp!!
        hashMap["groupName"] = groupName
        hashMap["groupIcon"] = ""
        hashMap["timestamp"] = timestamp!!
        hashMap["createdBy"] = userID!!
        groupRef!!.child(timestamp!!).setValue(hashMap).addOnSuccessListener {
            val hashMap = HashMap<String, Any>()
            hashMap["role"] = "creator"
            groupRef!!.child(timestamp!!).child("Participants").child(userID!!).setValue(hashMap)
        }
    }

    override fun onResume() {
        super.onResume()
        this.requireView().isFocusableInTouchMode = true
        this.requireView().requestFocus()
        this.requireView().setOnKeyListener { _, keyCode, _ ->
            keyCode == KeyEvent.KEYCODE_BACK
        }
    }
}