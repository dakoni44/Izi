package space.work.training.izi.mvvm.chatList.chat

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import space.work.training.izi.model.Chat
import space.work.training.izi.model.User
import space.work.training.izi.notifications.NotifData
import space.work.training.izi.notifications.RetrofitInstance
import space.work.training.izi.notifications.Sender
import space.work.training.izi.notifications.Token
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatFirebase @Inject constructor(
    private var chatDao: ChatDao,
    private var firebaseDatabase: FirebaseDatabase,
    private var firebaseAuth: FirebaseAuth
) {

    private var seenListener: ValueEventListener? = null
    private var receiverId = ""
    private var senderId = ""
    private var chatList: ArrayList<Chat> = ArrayList()
    private val chatUser = ChatUser()

    fun setReceiverId(id: String) {
        receiverId = id
    }

    suspend fun updateRoomChatUser(chatUser: ChatUser) {
        chatDao.updateChatUser(chatUser.img, chatUser.username, chatUser.uId)
    }

    suspend fun updateChatList(chatUser: ChatUser) {
        chatDao.updateChatList(chatUser.chatList, chatUser.uId)
    }

    fun showData() {
        CoroutineScope(Dispatchers.IO).launch {
            if (!chatDao.exists(receiverId))
                chatDao.insertChatUser(
                    ChatUser(
                        0,
                        receiverId, "", "", chatList
                    )
                )
        }

        senderId = firebaseAuth.currentUser!!.uid
        firebaseDatabase.getReference("Users").child(receiverId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    chatUser.apply {
                        uId = dataSnapshot.child("uid").getValue(String::class.java).toString()
                        img =
                            dataSnapshot.child("image").getValue(String::class.java).toString()
                        username =
                            dataSnapshot.child("username").getValue(String::class.java).toString()
                    }
                    CoroutineScope(Dispatchers.IO).launch {
                        if (chatList.isNotEmpty())
                            updateRoomChatUser(chatUser)
                    }
                    readMessages()
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
    }

    private fun seenMessage() {
        val userRefForSeen = firebaseDatabase.getReference("Chats")
        seenListener = userRefForSeen.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds in dataSnapshot.children) {
                    val chat = Chat().apply {
                        message = ds.child("message").getValue(String::class.java).toString()
                        reciever = ds.child("receiver").getValue(String::class.java).toString()
                        sender = ds.child("sender").getValue(String::class.java).toString()
                        timestamp = ds.child("timestamp").getValue(String::class.java).toString()
                        isSeen = ds.child("isSeen").getValue(Boolean::class.java)!!
                        if (reciever.equals(senderId) && sender
                                .equals(receiverId)
                        ) {
                            val hasSeenHashMap = HashMap<String, Any>()
                            hasSeenHashMap["isSeen"] = true
                            ds.ref.updateChildren(hasSeenHashMap)
                        }
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
                    val chat = Chat().apply {
                        message = ds.child("message").getValue(String::class.java).toString()
                        reciever = ds.child("receiver").getValue(String::class.java).toString()
                        sender = ds.child("sender").getValue(String::class.java).toString()
                        timestamp = ds.child("timestamp").getValue(String::class.java).toString()
                        isSeen = ds.child("isSeen").getValue(Boolean::class.java)!!
                        if (reciever.equals(senderId) && sender
                                .equals(receiverId) ||
                            reciever.equals(receiverId) && sender.equals(senderId)
                        ) {
                            chatList.add(this)
                        }
                    }
                }
                chatUser.chatList.clear()
                chatUser.chatList.addAll(chatList)
                CoroutineScope(Dispatchers.IO).launch {
                    if (chatList.isNotEmpty())
                        updateChatList(chatUser)
                }
                seenMessage()
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    fun sendMessage(message: String) {
        val databaseReference = FirebaseDatabase.getInstance().reference
        val timestamp = System.currentTimeMillis().toString()
        val hashMap = HashMap<String, Any>()
        hashMap["sender"] = senderId
        hashMap["receiver"] = receiverId
        hashMap["message"] = message
        hashMap["timestamp"] = timestamp
        hashMap["isSeen"] = false
        databaseReference.child("Chats").child(timestamp).setValue(hashMap)
        val database = FirebaseDatabase.getInstance().getReference("Users").child(senderId)
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = User().apply {
                    uid = dataSnapshot.child("uid").getValue(String::class.java).toString()
                    name = dataSnapshot.child("name").getValue(String::class.java).toString()
                    username =
                        dataSnapshot.child("username").getValue(String::class.java).toString()
                    email = dataSnapshot.child("email").getValue(String::class.java).toString()
                    image = dataSnapshot.child("image").getValue(String::class.java).toString()
                    bio = dataSnapshot.child("bio").getValue(String::class.java).toString()
                    val notifData =
                        NotifData(username, message)
                    checkTokenAndSend(notifData)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })

        //Pravim cet listu kad ja posaljem poruku u bazu ide kome sam ja sve slao poruku i upisujem njihov id
        val chatRef1 = FirebaseDatabase.getInstance().getReference("Chatlist")
            .child(senderId).child(receiverId)
        chatRef1.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (!dataSnapshot.exists()) {
                    chatRef1.child("id").setValue(receiverId)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
        val chatRef2 = FirebaseDatabase.getInstance().getReference("Chatlist")
            .child(receiverId).child(senderId)
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
        val allTokens = FirebaseDatabase.getInstance().getReference("Tokens").child(receiverId)
        allTokens.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val token = Token()
                token.token =
                    dataSnapshot.child("token").getValue(String::class.java).toString()
                val sender = Sender(token.token, notifData)
                sendNotification(sender)
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }


    private fun sendNotification(notification: Sender) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = RetrofitInstance.api.pushNotification(notification)
            if (response.isSuccessful) {
                Log.e("ChatViewModel", "Sent fcm notif")
            } else {
                Log.e("ChatViewModel", response.errorBody().toString())
            }
        } catch (e: Exception) {
            Log.e("ChatViewModel", e.toString())
        }
    }
}