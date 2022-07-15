package space.work.training.izi.mvvm.profile

import androidx.lifecycle.LiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import space.work.training.izi.mvvm.chatList.User
import javax.inject.Inject
import javax.inject.Named


class ProfileRepository @Inject constructor(
    private var profileDao: ProfileDao
) {

    @Inject
    @Named("Users")
    lateinit var userRef: DatabaseReference

    @Inject
    @Named("Posts")
    lateinit var postRef: DatabaseReference

    @Inject
    @Named("Likes")
    lateinit var likesRef: DatabaseReference

    @Inject
    @Named("Dislikes")
    lateinit var dislikesRef: DatabaseReference

    @Inject
    @Named("Friends")
    lateinit var friendsRef: DatabaseReference

    @Inject
    @Named("User")
    lateinit var user: FirebaseUser

    private val imgs: ArrayList<ProfileImg> = ArrayList<ProfileImg>()

    private var userID: String? = null

    private var userInfo: ArrayList<String> = ArrayList()
    var viewsS = ""
    var friendsS = ""
    var likesS = ""
    var dislikesS = ""
    var postsS = ""

    init {
        userID = FirebaseAuth.getInstance().currentUser!!.uid
        CoroutineScope(Dispatchers.IO).launch {
            if (profileDao.getUserInfo().value == null)
                profileDao.insertUSerInfo(
                    UserInfo(
                        0,
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

    fun getProfileImgs(): LiveData<List<ProfileImg>> {
        return profileDao.getAllImgs()
    }

    fun getUserInfo(): LiveData<UserInfo> {
        return profileDao.getUserInfo()
    }

    fun updateViews() {
        CoroutineScope(Dispatchers.IO).launch {
            profileDao.updateViews(viewsS)
        }

    }

    fun updateFriends() {
        CoroutineScope(Dispatchers.IO).launch {
            profileDao.updateFriends(friendsS)
        }

    }

    fun updateLikes() {
        CoroutineScope(Dispatchers.IO).launch {
            profileDao.updateLikes(likesS)
        }

    }

    fun updateDislikes() {
        CoroutineScope(Dispatchers.IO).launch {
            profileDao.updateDislikes(dislikesS)
        }

    }

    fun updatePosts() {
        CoroutineScope(Dispatchers.IO).launch {
            profileDao.updatePosts(postsS)
        }

    }

    fun updateUList() {
        CoroutineScope(Dispatchers.IO).launch {
            profileDao.updateUList(userInfo)
        }

    }

    fun showData() {
        userRef.child(userID!!).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = User()
                user.uid = userID as String
                user.name = dataSnapshot.child("name").getValue(String::class.java).toString()
                user.username =
                    dataSnapshot.child("username").getValue(String::class.java).toString()
                user.email = dataSnapshot.child("email").getValue(String::class.java).toString()
                user.image = dataSnapshot.child("image").getValue(String::class.java).toString()
                user.bio = dataSnapshot.child("bio").getValue(String::class.java).toString()
                if (user.uid.equals(userID)) {
                    if (!user.username.equals("")) userInfo.add(user.username)
                    if (!user.name.equals("")) userInfo.add(user.name)
                    if (!user.bio.equals("")) userInfo.add(user.bio)
                    if (!user.bio.equals("")) userInfo.add(user.image)
                }
                updateUList()
                showPost()
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun showPost() {
        postRef.addValueEventListener(object : ValueEventListener {
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

        friendsRef.child(userID!!).addValueEventListener(object : ValueEventListener {
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

        likesRef.addValueEventListener(object : ValueEventListener {
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

        dislikesRef.addValueEventListener(object : ValueEventListener {
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
