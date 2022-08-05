package space.work.training.izi.mvvm.posts

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import space.work.training.izi.model.Img
import space.work.training.izi.mvvm.posts.newImgs.ImgNew
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImgFirebase @Inject constructor(
    private var imgDao: ImgDao,
    private var firebaseAuth: FirebaseAuth,
    private var firebaseDatabase: FirebaseDatabase
) {

    private var friendList: ArrayList<String> = ArrayList()
    private var imgs: ArrayList<Img> = ArrayList()
    private var imgsNew: ArrayList<Img> = ArrayList()
    private var imgHome = ImgHome()
    private var imgNew = ImgNew()
    private var currentUser = ""

    fun load() {
        currentUser = firebaseAuth.currentUser!!.uid
        checkFollowing()
    }

    suspend fun updateRoomImg(imgHome: ImgHome) {
        imgDao.apply {
            deleteAllImgs(imgHome.uid)
            insert(imgHome)
        }
    }

    suspend fun updateRoomImgNew(imgNew: ImgNew) {
        imgDao.apply {
            deleteAllNewImgs(imgNew.uId)
            insertNew(imgNew)
        }
    }

    fun checkFollowing() {
        val reference = firebaseDatabase.getReference("Friends")
            .child(currentUser)
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                friendList.let {
                    it.clear()
                    for (snapshot in dataSnapshot.children) {
                        it.add(snapshot.key.toString())
                    }
                }
                readPosts()
                readNewPosts()
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }


    private fun readPosts() {
        val postRef = firebaseDatabase.getReference("Posts")
        postRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                imgs.let {
                    it.clear()
                    for (snapshot in dataSnapshot.children) {
                        val img = Img().apply {
                            imgId = snapshot.child("postid").getValue(String::class.java).toString()
                            publisher =
                                snapshot.child("publisher").getValue(String::class.java).toString()
                            img =
                                snapshot.child("postimage").getValue(String::class.java).toString()
                            text =
                                snapshot.child("description").getValue(String::class.java)
                                    .toString()
                            views = (snapshot.child("views").childrenCount - 1).toString()
                            timestamp =
                                snapshot.child("timestamp").getValue(String::class.java).toString()
                            for (id in friendList) {
                                if (publisher == id) {
                                    it.add(this)
                                }
                            }
                        }
                    }
                    imgHome.uid = currentUser
                    imgHome.imgs = imgs
                }
                CoroutineScope(Dispatchers.IO).launch {
                    if (!imgs.isEmpty())
                        updateRoomImg(imgHome)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun readNewPosts() {
        val currentTime = System.currentTimeMillis()

        val postRef = firebaseDatabase.getReference("Posts")
        postRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                imgsNew.let {
                    it.clear()
                    for (snapshot in dataSnapshot.children) {
                        val img = Img().apply {
                            imgId = snapshot.child("postid").getValue(String::class.java).toString()
                            publisher =
                                snapshot.child("publisher").getValue(String::class.java).toString()
                            img =
                                snapshot.child("postimage").getValue(String::class.java).toString()
                            text =
                                snapshot.child("description").getValue(String::class.java)
                                    .toString()
                            views = (snapshot.child("views").childrenCount - 1).toString()
                            timestamp =
                                snapshot.child("timestamp").getValue(String::class.java).toString()
                            for (id in friendList) {
                                if (publisher == id && currentTime <= timestamp.toLong() + 24 * 60 * 60 * 1000) {
                                    it.add(this)
                                }
                            }
                        }
                        imgNew.uId = currentUser
                        imgNew.imgs = imgsNew
                    }
                    CoroutineScope(Dispatchers.IO).launch {
                        if (!imgsNew.isEmpty())
                            updateRoomImgNew(imgNew)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }
}