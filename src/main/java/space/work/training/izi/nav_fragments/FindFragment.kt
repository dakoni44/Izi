package space.work.training.izi.nav_fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.AndroidEntryPoint
import space.work.training.izi.R
import space.work.training.izi.adapters.FindAdapter
import space.work.training.izi.databinding.FragmentFindBinding
import space.work.training.izi.mvvm.chatList.User
import java.util.*

@AndroidEntryPoint
class FindFragment : Fragment(), FindAdapter.OnItemClickListener {

    private lateinit var binding: FragmentFindBinding

    var findAdapter: FindAdapter? = null
    var findUsers: ArrayList<User> = ArrayList()

    var firebaseUser: FirebaseUser? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_find, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvFind.setHasFixedSize(true)
        binding.rvFind.layoutManager = LinearLayoutManager(requireContext())

        firebaseUser = FirebaseAuth.getInstance().currentUser

        findUsers = ArrayList()
        findAdapter = FindAdapter(requireContext(), findUsers, this)
        binding.rvFind.adapter = findAdapter

        binding.etFind.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    searchUsers(s.toString().lowercase(Locale.getDefault()))
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    private fun searchUsers(s: String) {
        if(!s.trim().equals("")){
            val query = FirebaseDatabase.getInstance().getReference("Users")
                .orderByChild("username").startAt(s).endAt(s + "\uf8ff")
            query.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    findUsers.clear()
                    for (snapshot in dataSnapshot.children) {
                        val user = User()
                        user.uid = snapshot.child("uid").getValue(String::class.java).toString()
                        user.name = snapshot.child("name").getValue(String::class.java).toString()
                        user.username =
                            snapshot.child("username").getValue(String::class.java).toString()
                        user.email = snapshot.child("email").getValue(String::class.java).toString()
                        user.image = snapshot.child("image").getValue(String::class.java).toString()
                        user.bio = dataSnapshot.child("bio").getValue(String::class.java).toString()
                        if (!user.uid.equals(firebaseUser!!.uid))
                            findUsers.add(user)
                    }
                    findAdapter!!.notifyDataSetChanged()
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
        }
    }

    override fun onItemClick(position: Int) {
        if (!findUsers.get(position).uid.equals(firebaseUser!!.uid)) {
            val action =
                FindFragmentDirections.findToOtherProfile(findUsers.get(position).uid)
            findNavController().navigate(action)
        }
    }
}