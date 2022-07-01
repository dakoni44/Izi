package space.work.training.izi.nav_fragments

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnLayoutChangeListener
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import space.work.training.izi.R
import space.work.training.izi.adapters.ChatAdapter
import space.work.training.izi.databinding.FragmentChatBinding
import space.work.training.izi.model.Chat
import space.work.training.izi.mvvm.chat.User
import space.work.training.izi.notifications.NotifData
import space.work.training.izi.notifications.RetrofitInstance
import space.work.training.izi.notifications.Sender
import space.work.training.izi.notifications.Token

class ChatFragment : Fragment() {

    private lateinit var binding: FragmentChatBinding

    private val args: ChatFragmentArgs by navArgs()
    private var receiverId: String = args.uId

    var senderId: String? = null
    var receiverImg: String? = null

    private var firebaseAuth: FirebaseAuth? = null
    private var user: FirebaseUser? = null
    private var firebaseDatabase: FirebaseDatabase? = null
    private var usersDbRef: DatabaseReference? = null
    private var userRefForSeen: DatabaseReference? = null

    private var chatList: ArrayList<Chat> = ArrayList()
    private var chatAdapter: ChatAdapter? = null

    private var requestQueue: RequestQueue? = null

    private var notify = false

    private var seenListener: ValueEventListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_chat, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val linearLayoutManager = LinearLayoutManager(requireContext())
        linearLayoutManager.stackFromEnd = true
        binding.rvChat.setHasFixedSize(true)
        binding.rvChat.layoutManager = linearLayoutManager

        requestQueue = Volley.newRequestQueue(requireContext())

        firebaseAuth = FirebaseAuth.getInstance()
        user = firebaseAuth!!.currentUser
        firebaseDatabase = FirebaseDatabase.getInstance()
        usersDbRef = firebaseDatabase!!.getReference("Users")

        senderId = user!!.uid

        showData()

        binding.bnSend.setOnClickListener {
            notify = true
            val message: String = binding.etMessage.text.toString().trim { it <= ' ' }
            if (TextUtils.isEmpty(message)) {
            } else {
                sendMessage(message)
            }
            binding.etMessage.setText("")
        }

        readMessages()

        seenMessage()
    }

    private fun showData() {
        usersDbRef!!.child(receiverId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                receiverImg = dataSnapshot.child("image").getValue(String::class.java)
                binding.tvName.text = dataSnapshot.child("name").getValue(String::class.java)
                Glide.with(requireContext()).load(receiverImg).into(binding.ivProfile)
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun seenMessage() {
        userRefForSeen = FirebaseDatabase.getInstance().getReference("Chats")
        seenListener = userRefForSeen!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds in dataSnapshot.children) {
                    val chat = Chat()
                    chat.message = ds.child("message").getValue(String::class.java).toString()
                    chat.reciever = ds.child("receiver").getValue(String::class.java).toString()
                    chat.sender = ds.child("sender").getValue(String::class.java).toString()
                    chat.timestamp = ds.child("timestamp").getValue(String::class.java).toString()
                    chat.isSeen = ds.child("isSeen").getValue(Boolean::class.java)!!
                    if (chat.reciever.equals(senderId) && chat.sender
                            .equals(receiverId)
                    ) {
                        val hasSeenHashMap = HashMap<String, Any>()
                        hasSeenHashMap["isSeen"] = true
                        ds.ref.updateChildren(hasSeenHashMap)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun readMessages() {
        chatList = ArrayList()
        val dbRef = FirebaseDatabase.getInstance().getReference("Chats")
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                chatList.clear()
                for (ds in dataSnapshot.children) {
                    val chat = Chat()
                    chat.message = ds.child("message").getValue(String::class.java).toString()
                    chat.reciever = ds.child("receiver").getValue(String::class.java).toString()
                    chat.sender = ds.child("sender").getValue(String::class.java).toString()
                    chat.timestamp = ds.child("timestamp").getValue(String::class.java).toString()
                    chat.isSeen = ds.child("isSeen").getValue(Boolean::class.java)!!
                    if (chat.reciever.equals(senderId) && chat.sender
                            .equals(receiverId) ||
                        chat.reciever.equals(receiverId) && chat.sender.equals(senderId)
                    ) {
                        chatList.add(chat)
                    }
                    chatAdapter = ChatAdapter(requireContext(), chatList, receiverImg!!)
                    chatAdapter!!.notifyDataSetChanged()
                    binding.rvChat.setAdapter(chatAdapter)
                    binding.rvChat.smoothScrollToPosition(chatAdapter!!.itemCount)
                    binding.rvChat.addOnLayoutChangeListener(OnLayoutChangeListener { view, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
                        if (bottom < oldBottom) {
                            binding.rvChat.postDelayed(Runnable {
                                binding.rvChat.smoothScrollToPosition(
                                    chatAdapter!!.itemCount
                                )
                            }, 10)
                        }
                    })
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun sendMessage(message: String) {
        val databaseReference = FirebaseDatabase.getInstance().reference
        val timestamp = System.currentTimeMillis().toString()
        val hashMap = HashMap<String, Any>()
        hashMap["sender"] = senderId!!
        hashMap["receiver"] = receiverId
        hashMap["message"] = message
        hashMap["timestamp"] = timestamp
        hashMap["isSeen"] = false
        databaseReference.child("Chats").push().setValue(hashMap)
        val msg = message
        val database = FirebaseDatabase.getInstance().getReference("Users").child(senderId!!)
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = User()
                user.uid = dataSnapshot.child("uid").getValue(String::class.java).toString()
                user.name = dataSnapshot.child("name").getValue(String::class.java).toString()
                user.username =
                    dataSnapshot.child("username").getValue(String::class.java).toString()
                user.email = dataSnapshot.child("email").getValue(String::class.java).toString()
                user.image = dataSnapshot.child("image").getValue(String::class.java).toString()
                user.bio = dataSnapshot.child("bio").getValue(String::class.java).toString()
                if (notify) {
                    val notifData =
                        NotifData(senderId!!, message, user.username, receiverId, R.drawable.izi)
                    checkTokenAndSend(notifData)
                }
                notify = false
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })

        //Pravim cet listu kad ja posaljem poruku u bazu ide kome sam ja sve slao poruku i upisujem njihov id
        val chatRef1 = FirebaseDatabase.getInstance().getReference("Chatlist")
            .child(senderId!!).child(receiverId)
        chatRef1.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (!dataSnapshot.exists()) {
                    chatRef1.child("id").setValue(receiverId)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
        val chatRef2 = FirebaseDatabase.getInstance().getReference("Chatlist")
            .child(receiverId).child(senderId!!)
        chatRef2.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (!dataSnapshot.exists()) {
                    chatRef2.child("id").setValue(senderId)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun checkTokenAndSend(notifData: NotifData) {
        val allTokens = FirebaseDatabase.getInstance().getReference("Tokens")
        val query = allTokens.orderByKey().equalTo(receiverId)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds in dataSnapshot.children) {
                    val token = Token()
                    token.token = ds.child("token").getValue(String::class.java).toString()
                    val sender = Sender(notifData, token.token)
                    sendNotification(sender)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }


    private fun sendNotification(notification: Sender) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = RetrofitInstance.api.pushNotification(notification)
        } catch (e: Exception) {

        }
    }

}