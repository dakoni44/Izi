package space.work.training.izi.mvvm.profile

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import space.work.training.izi.mvvm.chatList.User
import javax.inject.Inject

class ProfileFirebase @Inject constructor(
    private var profileDao: ProfileDao,
    private var firebaseDatabase: FirebaseDatabase,
    private var firebaseAuth: FirebaseAuth
) {

    private val imgs: ArrayList<ProfileImg> = ArrayList<ProfileImg>()

    private var userID = ""

    private var userInfo: ArrayList<String> = ArrayList()
    var viewsS = ""
    var friendsS = ""
    var likesS = ""
    var dislikesS = ""
    var postsS = ""

    init {
        userID = firebaseAuth.currentUser!!.uid
        CoroutineScope(Dispatchers.IO).launch {
            if (profileDao.getUserInfo(userID).value == null)
                profileDao.insertUSerInfo(
                    UserInfo(
                        0,
                        userID,
                        userInfo,
                        viewsS,
                        friendsS,
                        likesS,
                        dislikesS,
                        postsS
                    )
                )
        }
    }

    suspend fun notifyFirebaseDataChange(list: List<ProfileImg>) {
        profileDao.deleteAllImgs()
        for (img in list) {
            profileDao.insertProfileImgs(img)
        }
    }

    fun updateViews() {
        CoroutineScope(Dispatchers.IO).launch {
            profileDao.updateViews(viewsS, userID)
        }

    }

    fun updateFriends() {
        CoroutineScope(Dispatchers.IO).launch {
            profileDao.updateFriends(friendsS, userID)
        }

    }

    fun updateLikes() {
        CoroutineScope(Dispatchers.IO).launch {
            profileDao.updateLikes(likesS, userID)
        }

    }

    fun updateDislikes() {
        CoroutineScope(Dispatchers.IO).launch {
            profileDao.updateDislikes(dislikesS, userID)
        }

    }

    fun updatePosts() {
        CoroutineScope(Dispatchers.IO).launch {
            profileDao.updatePosts(postsS, userID)
        }

    }

    fun updateUList() {
        CoroutineScope(Dispatchers.IO).launch {
            profileDao.updateUList(userInfo, userID)
        }

    }

    fun showData() {
        userID = firebaseAuth.currentUser!!.uid
        firebaseDatabase.getReference("Users").child(userID)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val user = User()
                    user.uid = userID
                    user.name = dataSnapshot.child("name").getValue(String::class.java).toString()
                    user.username =
                        dataSnapshot.child("username").getValue(String::class.java).toString()
                    user.email = dataSnapshot.child("email").getValue(String::class.java).toString()
                    user.image = dataSnapshot.child("image").getValue(String::class.java).toString()
                    user.bio = dataSnapshot.child("bio").getValue(String::class.java).toString()
                    if (user.uid.equals(userID)) {
                        userInfo.clear()
                        userInfo.add(user.username)
                        userInfo.add(user.name)
                        userInfo.add(user.bio)
                        userInfo.add(user.image)
                    }
                    updateUList()
                    showPost()
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
    }

    private fun showPost() {
        firebaseDatabase.getReference("Posts").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                imgs.clear()
                for (snapshot in dataSnapshot.children) {
                    val img = ProfileImg()
                    img.imgId = snapshot.child("postid").getValue(String::class.java).toString()
                    img.publisher =
                        snapshot.child("publisher").getValue(String::class.java).toString()
                    img.img = snapshot.child("postimage").getValue(String::class.java).toString()
                    img.text = snapshot.child("description").getValue(String::class.java).toString()
                    img.views = (snapshot.child("views").childrenCount - 1).toString()
                    img.timestamp =
                        snapshot.child("timestamp").getValue(String::class.java).toString()
                    if (img.publisher.equals(userID)) {
                        imgs.add(img)
                    }
                }
                imgs.reverse()
                CoroutineScope(Dispatchers.IO).launch {
                    if (!imgs.isNullOrEmpty())
                        notifyFirebaseDataChange(imgs)
                }
                showNumbers()
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun showNumbers() {
        firebaseDatabase.getReference("Friends").child(userID)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    friendsS = dataSnapshot.childrenCount.toInt().toString()
                    updateFriends()
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })

        postsS = imgs.size.toString()
        updatePosts()

        var sum = 0
        for (i in imgs.indices) {
            sum += imgs.get(i).views.toInt()
        }
        var all = sum - imgs.size
        if (all < 0)
            all = 0
        viewsS = (all).toString()
        updateViews()

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
                likesS = sum.toString()
                updateLikes()
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
                    dislikesS = sum.toString()
                    updateDislikes()
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
    }

}