package space.work.training.izi.mvvm.chat.room_v_firebase

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import space.work.training.izi.model.ChatList
import space.work.training.izi.mvvm.chat.User
import javax.inject.Inject
import javax.inject.Singleton

class ChatListFirebase{

    private val chatList: ArrayList<ChatList> = ArrayList()
    private var usersList: ArrayList<User> = ArrayList()
    private var online:MutableLiveData<List<User>> = MutableLiveData()

    //Odavde odma citam id-eve i spajam ih sa userima u sledecoj metodi
    //ne moram da ih trazim gde me sve ima u chats-u
    private fun checkList() {
        val reference = FirebaseDatabase.getInstance().getReference("Chatlist")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                chatList.clear()
                for (snapshot in dataSnapshot.children) {
                    val chat = ChatList()
                    chat.id = snapshot.child("id").getValue(String::class.java).toString()
                    chatList.add(chat)
                }
                loadChatList()
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

     fun loadChatList() : MutableLiveData<List<User>>{
         checkList()
        val reference = FirebaseDatabase.getInstance().getReference("Users")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                usersList.clear()
                for (snapshot in dataSnapshot.children) {
                    val user = User()
                    user.uid=snapshot.child("uid").getValue(String::class.java).toString()
                    user.name=snapshot.child("name").getValue(String::class.java).toString()
                    user.username=snapshot.child("username").getValue(String::class.java).toString()
                    user.email=snapshot.child("email").getValue(String::class.java).toString()
                    user.image=snapshot.child("image").getValue(String::class.java).toString()
                    user.bio=dataSnapshot.child("bio").getValue(String::class.java).toString()
                    //preko isto postojecih ide-va trazim cim se poklope i ubacujem ih u listu
                    for (chat in chatList) {
                        if (user.uid != null && user.uid.equals(chat.id)) {
                            usersList.add(user)
                        }
                    }
                }
                online.postValue(usersList)
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
        return online
    }
}