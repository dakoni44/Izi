package space.work.training.izi.mvvm.profileOther

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import space.work.training.izi.model.Img
import space.work.training.izi.model.User
import space.work.training.izi.mvvm.profile.UserInfo
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class ProfileOtherFirebase @Inject constructor(
    private var firebaseDatabase: FirebaseDatabase,
    private var firebaseAuth: FirebaseAuth
) {

    private var friendId = ""
    private var senderId = ""
    private var saveCurrentDate = ""
    private var CURRENT_STATE = "not friends"
    private var user = User()
    private var profileUser = UserInfo()
    private var userInfo: ArrayList<String> = ArrayList()
    private val imgs: ArrayList<Img> = ArrayList<Img>()

    private val currentState = MutableLiveData<String>()
    private var profileUserLive = MutableLiveData<UserInfo>()
    private val imgsLive = MutableLiveData<ArrayList<Img>>()

    private var profileUserFlow: Flow<UserInfo> = flow { }
    private var profileUserFlow2 = MutableStateFlow<UserInfo>(UserInfo())

    init {
        senderId = firebaseAuth.currentUser!!.uid
    }

    fun setFriendId(id: String) {
        friendId = id
    }

    fun getProfileUser(): LiveData<UserInfo> {
        return profileUserLive
    }

    /*  fun getProfileUserFlow(): Flow<UserInfo> {
          return profileUserFlow
      }

      fun getProfileUserFlow2(): MutableStateFlow<UserInfo> {
          return profileUserFlow2
      }*/

    fun getCurrentState(): LiveData<String> {
        return currentState
    }

    fun getImgs(): LiveData<ArrayList<Img>> {
        return imgsLive
    }

    fun showData() {
        firebaseDatabase.getReference("Users").child(friendId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    user.uid = friendId
                    user.name =
                        dataSnapshot.child("name").getValue(String::class.java).toString()
                    user.username =
                        dataSnapshot.child("username").getValue(String::class.java)
                            .toString()
                    user.email =
                        dataSnapshot.child("email").getValue(String::class.java).toString()
                    user.image =
                        dataSnapshot.child("image").getValue(String::class.java).toString()
                    user.bio =
                        dataSnapshot.child("bio").getValue(String::class.java).toString()
                    userInfo.clear()
                    userInfo.add(user.username)
                    userInfo.add(user.name)
                    userInfo.add(user.bio)
                    userInfo.add(user.image)
                    profileUser.uList.clear()
                    profileUser.uList.addAll(userInfo)

                    profileUserLive.postValue(profileUser)

                    /*  profileUserFlow = flow {
                          emit(profileUser)
                      }*/

                    // profileUserFlow2.value = profileUser
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
    }

    fun maintanceOfButtons() {
        firebaseDatabase.getReference("Friends").child(senderId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.hasChild(friendId)) {
                        CURRENT_STATE = "friends"
                        currentState.postValue(CURRENT_STATE)
                    } else {
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
        firebaseDatabase.getReference("Friends").child(senderId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.hasChild(friendId)) {
                        val requset_type = dataSnapshot.child(friendId).child("request_type")
                            .value.toString()
                        if (requset_type == "sent") {
                            CURRENT_STATE = "request_sent"
                            currentState.postValue(CURRENT_STATE)
                        } else if (requset_type == "received") {
                            CURRENT_STATE = "request_received"
                            currentState.postValue(CURRENT_STATE)
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
    }

    fun showPost() {
        firebaseDatabase.getReference("Posts").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                imgs.clear()
                for (snapshot in dataSnapshot.children) {
                    val img = Img()
                    img.imgId = snapshot.child("postid").getValue(String::class.java).toString()
                    img.publisher =
                        snapshot.child("publisher").getValue(String::class.java).toString()
                    img.img = snapshot.child("postimage").getValue(String::class.java).toString()
                    img.text = snapshot.child("description").getValue(String::class.java).toString()
                    img.views = (snapshot.child("views").childrenCount).toString()
                    img.timestamp =
                        snapshot.child("timestamp").getValue(String::class.java).toString()
                    if (img.publisher.equals(friendId)) {
                        imgs.add(img)
                    }
                }
                imgs.reverse()
                imgsLive.postValue(imgs)

                profileUser.posts = imgs.size.toString()
                profileUserLive.postValue(profileUser)
                /*  profileUserFlow = flow {
                      emit(profileUser)
                  }*/
                // profileUserFlow2.value = profileUser

                var sum = 0
                for (i in imgs.indices) {
                    sum += imgs.get(i).views.toInt()
                }
                var all = sum - imgs.size
                if (all < 0)
                    all = 0
                profileUser.views = all.toString()
                profileUserLive.postValue(profileUser)
                /*  profileUserFlow = flow {
                      emit(profileUser)
                  }*/
                // profileUserFlow2.value = profileUser
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }


    fun showNumbers() {
        firebaseDatabase.getReference("Friends").child(friendId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    profileUser.friends = dataSnapshot.childrenCount.toInt().toString()
                    profileUserLive.postValue(profileUser)
                    /*    profileUserFlow = flow {
                            emit(profileUser)
                        }*/
                    // profileUserFlow2.value = profileUser
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })

        firebaseDatabase.getReference("Likes").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var sum = 0
                for (snapshot in dataSnapshot.children) {
                    for (i in imgs.indices) {
                        if (snapshot.key == imgs.get(i).imgId) {
                            sum += snapshot.childrenCount.toInt()
                        }
                    }
                }
                profileUser.likes = sum.toString()
                profileUserLive.postValue(profileUser)
                /* profileUserFlow = flow {
                     emit(profileUser)
                 }*/
                // profileUserFlow2.value = profileUser
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })

        firebaseDatabase.getReference("Dislikes")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    var sum = 0
                    for (snapshot in dataSnapshot.children) {
                        for (i in imgs.indices) {
                            if (snapshot.key == imgs.get(i).imgId) {
                                sum += snapshot.childrenCount.toInt()
                            }
                        }
                    }
                    profileUser.dislikes = sum.toString()
                    profileUserLive.postValue(profileUser)
                    /* profileUserFlow = flow {
                         emit(profileUser)
                     }*/
                    // profileUserFlow2.value = profileUser
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
    }

    fun removeFriend() {
        firebaseDatabase.getReference("Friends").child(senderId).child(friendId).removeValue()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    firebaseDatabase.getReference("Friends").child(friendId).child(senderId)
                        .removeValue().addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                CURRENT_STATE = "not_friends"
                                currentState.postValue(CURRENT_STATE)
                            }
                        }
                }
            }
    }

    fun sendFriendRequest() {
        firebaseDatabase.getReference("Friends").child(senderId).child(friendId)
            .child("request_type")
            .setValue("sent")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    firebaseDatabase.getReference("Friends").child(friendId).child(senderId)
                        .child("request_type")
                        .setValue("received").addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                CURRENT_STATE = "request_sent"
                                currentState.postValue(CURRENT_STATE)
                            }
                        }
                }
            }
    }

    fun cancelFriendRequest() {
        firebaseDatabase.getReference("Friends").child(senderId).child(friendId).removeValue()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    firebaseDatabase.getReference("Friends").child(friendId).child(senderId)
                        .child("request_type")
                        .removeValue().addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                CURRENT_STATE = "not_friends"
                                currentState.postValue(CURRENT_STATE)
                            }
                        }
                }
            }
    }

    fun acceptFriendRequest() {
        val calForDate = Calendar.getInstance()
        val currentDate = SimpleDateFormat("dd-MMMM-yyyy")
        saveCurrentDate = currentDate.format(calForDate.time)
        firebaseDatabase.getReference("Friends").child(senderId).child(friendId).child("date")
            .setValue(saveCurrentDate)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    firebaseDatabase.getReference("Friends").child(friendId).child(senderId)
                        .child("date")
                        .setValue(saveCurrentDate)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                firebaseDatabase.getReference("Friends").child(senderId)
                                    .child(friendId).removeValue()
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            firebaseDatabase.getReference("Friends").child(friendId)
                                                .child(senderId)
                                                .child("request_type")
                                                .removeValue().addOnCompleteListener { task ->
                                                    if (task.isSuccessful) {
                                                        CURRENT_STATE = "friends"
                                                        currentState.postValue(CURRENT_STATE)
                                                    }
                                                }
                                        }
                                    }
                            }
                        }
                }
            }
    }

}