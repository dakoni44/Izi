package space.work.training.izi.nav_fragments

import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import dagger.hilt.android.AndroidEntryPoint
import space.work.training.izi.R
import space.work.training.izi.adapters.ChatList2Adapter
import space.work.training.izi.adapters.GroupListAdapter
import space.work.training.izi.databinding.FragmentChatListBinding
import space.work.training.izi.model.Chat
import space.work.training.izi.model.GroupList
import space.work.training.izi.mvvm.chat.ChatListViewModel
import space.work.training.izi.mvvm.chat.User
import java.util.*

@AndroidEntryPoint
class ChatListFragment : Fragment(), ChatList2Adapter.OnItemClickListener {

    private lateinit var binding: FragmentChatListBinding

    private var firebaseAuth: FirebaseAuth? = null
    private var user: FirebaseUser? = null
    private var firebaseDatabase: FirebaseDatabase? = null
    private var groupReference: DatabaseReference? = null

    var groupList: ArrayList<GroupList> = ArrayList()
    private var chatUsers: ArrayList<User> = ArrayList()

    var adapter: ChatList2Adapter? = null
    var groupAdapter: GroupListAdapter? = null

    private val chatListViewModel: ChatListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_chat_list, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()
        user = firebaseAuth!!.currentUser
        firebaseDatabase = FirebaseDatabase.getInstance()
        groupReference = firebaseDatabase!!.getReference("Groups")

        binding.rvChatList.setHasFixedSize(true)
        binding.rvChatList.layoutManager = LinearLayoutManager(requireContext())

        binding.rvGroupList.setHasFixedSize(true)
        binding.rvGroupList.setLayoutManager(LinearLayoutManager(requireContext()))

        adapter = ChatList2Adapter(requireContext(), this)
        binding.rvChatList.adapter = adapter

        chatListViewModel.getAllUsers().observe(viewLifecycleOwner) {
            it.let {
                adapter!!.setData(it)
                chatUsers.addAll(it)
                for (i in it.indices) {
                    lastMessage(it[i].uid)
                }
            }
        }

        loadGroups()

        binding.oneChat.setOnClickListener {
            binding.rvChatList.visibility = View.VISIBLE
            binding.rvGroupList.visibility = View.INVISIBLE
            binding.bnCreate.visibility = View.INVISIBLE
        }
        binding.groupChat.setOnClickListener {
            binding.rvChatList.visibility = View.INVISIBLE
            binding.rvGroupList.visibility = View.VISIBLE
            binding.bnCreate.visibility = View.VISIBLE
        }
    }

    private fun lastMessage(uid: String) {
        //Odavde moram sad da trazim zadnju poruku
        val reference = FirebaseDatabase.getInstance().getReference("Chats")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var theLastMessage = "default"
                var dateTime = ""
                for (snapshot in dataSnapshot.children) {
                    val chat = Chat()
                    chat.message = snapshot.child("message").getValue(String::class.java).toString()
                    chat.reciever =
                        snapshot.child("receiver").getValue(String::class.java).toString()
                    chat.sender = snapshot.child("sender").getValue(String::class.java).toString()
                    chat.timestamp =
                        snapshot.child("timestamp").getValue(String::class.java).toString()
                    chat.isSeen = snapshot.child("isSeen").getValue(Boolean::class.java)!!
                    if (chat == null) {
                        continue
                    }
                    val sender: String = chat.sender
                    val receiver: String = chat.reciever
                    if (sender == null || receiver == null) {
                        continue
                    }
                    if (chat.reciever.equals(user?.getUid())
                        && chat.sender.equals(uid) || chat.reciever.equals(uid)
                        && chat.sender.equals(user?.getUid())
                    ) {
                        theLastMessage = chat.message
                        val cal = Calendar.getInstance(Locale.ENGLISH)
                        cal.timeInMillis = chat.timestamp.toLong()
                        dateTime = DateFormat.format("HH:mm", cal).toString()
                    }
                }
                adapter!!.setLastMessageMap(uid, theLastMessage, dateTime)
                adapter!!.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun loadGroups() {
        groupReference?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                groupList.clear()
                for (dataSnapshot in snapshot.children) {
                    val modelGroup = GroupList()
                    modelGroup.id =
                        dataSnapshot.child("groupId").getValue(String::class.java).toString()
                    modelGroup.icon =
                        dataSnapshot.child("groupIcon").getValue(String::class.java).toString()
                    modelGroup.name =
                        dataSnapshot.child("groupName").getValue(String::class.java).toString()
                    modelGroup.creator =
                        dataSnapshot.child("createdBy").getValue(String::class.java).toString()

                    if (modelGroup.creator.equals(user?.uid) || dataSnapshot.child("Participants")
                            .hasChild(user?.uid.toString())
                    ) {
                        groupList.add(modelGroup)
                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {}
        })
        groupAdapter = GroupListAdapter(requireContext(), groupList, this)
        binding.rvGroupList.adapter = groupAdapter
    }

    override fun onItemClick(position: Int) {
        val action = ChatListFragmentDirections.chatListToChat(chatUsers.get(position).uid)
        findNavController().navigate(action)
    }

}