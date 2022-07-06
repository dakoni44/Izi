package space.work.training.izi.mvvm.chat

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import space.work.training.izi.model.ChatList
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatListRepository @Inject constructor(
) {

    private val chatList: ArrayList<ChatList> = ArrayList()
    private var usersList: ArrayList<User> = ArrayList()

    private var onlineList: MutableLiveData<List<User>> = MutableLiveData()

/*    suspend fun notifyFirebaseDataChange(list: List<User>) {
        userDao.deleteAllUsers()
        for (img in list) {
            userDao.insert(img)
        }
    }*/

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
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    fun loadChatList(): MutableLiveData<List<User>> {
        checkList()
        val reference = FirebaseDatabase.getInstance().getReference("Users")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                usersList.clear()
                for (snapshot in dataSnapshot.children) {
                    val user = User()
                    user.uid = snapshot.child("uid").getValue(String::class.java).toString()
                    user.name = snapshot.child("name").getValue(String::class.java).toString()
                    user.username =
                        snapshot.child("username").getValue(String::class.java).toString()
                    user.email = snapshot.child("email").getValue(String::class.java).toString()
                    user.image = snapshot.child("image").getValue(String::class.java).toString()
                    user.bio = dataSnapshot.child("bio").getValue(String::class.java).toString()
                    //preko isto postojecih ide-va trazim cim se poklope i ubacujem ih u listu
                    for (chat in chatList) {
                        if (user.uid.equals(chat.id)) {
                            usersList.add(user)
                        }
                    }
                    onlineList.postValue(usersList)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
        return onlineList
    }

}