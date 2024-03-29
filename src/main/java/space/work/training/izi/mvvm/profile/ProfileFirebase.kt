package space.work.training.izi.mvvm.profile

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import space.work.training.izi.model.Img
import space.work.training.izi.model.User
import javax.inject.Inject

class ProfileFirebase @Inject constructor(
    private var profileDao: ProfileDao,
    private var firebaseDatabase: FirebaseDatabase,
    private var firebaseAuth: FirebaseAuth
) {

    private val imgs: ArrayList<Img> = ArrayList()

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
            if (!profileDao.exists(userID)) {
                profileDao.insertUSerInfo(
                    UserInfo(0, userID, ArrayList(), "", "", "", "", "", ArrayList())
                )
            }
        }
    }

    suspend fun notifyFirebaseDataChange(list: ArrayList<Img>) {
        profileDao.updatePImgs(list, userID)

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
                    val user = User().apply {
                        uid = userID
                        name = dataSnapshot.child("name").getValue(String::class.java).toString()
                        username =
                            dataSnapshot.child("username").getValue(String::class.java).toString()
                        email = dataSnapshot.child("email").getValue(String::class.java).toString()
                        image = dataSnapshot.child("image").getValue(String::class.java).toString()
                        bio = dataSnapshot.child("bio").getValue(String::class.java).toString()
                        if (uid.equals(userID)) {
                            userInfo.clear()
                            userInfo.add(username)
                            userInfo.add(name)
                            userInfo.add(bio)
                            userInfo.add(image)
                        }
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
                    val img = Img().apply {
                        imgId = snapshot.child("postid").getValue(String::class.java).toString()
                        publisher =
                            snapshot.child("publisher").getValue(String::class.java).toString()
                        img = snapshot.child("postimage").getValue(String::class.java).toString()
                        text = snapshot.child("description").getValue(String::class.java).toString()
                        views = (snapshot.child("views").childrenCount - 1).toString()
                        timestamp =
                            snapshot.child("timestamp").getValue(String::class.java).toString()
                        if (publisher.equals(userID)) {
                            imgs.add(this)
                        }
                    }
                }
                imgs.reverse()
                CoroutineScope(Dispatchers.IO).launch {
                    if (imgs.isNotEmpty())
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