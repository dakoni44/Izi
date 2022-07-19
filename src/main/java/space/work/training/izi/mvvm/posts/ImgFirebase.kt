package space.work.training.izi.mvvm.posts

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import space.work.training.izi.mvvm.posts.newImgs.ImgNew
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImgFirebase @Inject constructor(private var imgDao: ImgDao, private var firebaseAuth: FirebaseAuth
,private var firebaseDatabase: FirebaseDatabase) {

    private var friendList: ArrayList<String> = ArrayList()
    private var imgs: ArrayList<Img> = ArrayList()
    private var imgsNew: ArrayList<ImgNew> = ArrayList()

    fun load() {
        readPosts()
        readNewPosts()
    }

    suspend fun updateRoomImg(list: List<Img>) {
        imgDao.deleteAllImgs()
        for (img in list) {
            imgDao.insert(img)
        }
    }

    suspend fun updateRoomImgNew(list: List<ImgNew>) {
        imgDao.deleteAllImgs()
        for (img in list) {
            imgDao.insertNew(img)
        }
    }

    fun checkFollowing() {
        val reference = firebaseDatabase.getReference("Friends")
            .child(firebaseAuth.currentUser!!.uid)
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                friendList.let {
                    it.clear()
                    for (snapshot in dataSnapshot.children) {
                        it.add(snapshot.key.toString())
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }


    fun readPosts() {
        checkFollowing()

        val postRef = firebaseDatabase.getReference("Posts")
        postRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                imgs.let {
                    it.clear()
                    for (snapshot in dataSnapshot.children) {
                        val img = Img()
                        img.imgId = snapshot.child("postid").getValue(String::class.java).toString()
                        img.publisher =
                            snapshot.child("publisher").getValue(String::class.java).toString()
                        img.img =
                            snapshot.child("postimage").getValue(String::class.java).toString()
                        img.text =
                            snapshot.child("description").getValue(String::class.java).toString()
                        img.views = (snapshot.child("views").childrenCount - 1).toString()
                        img.timestamp =
                            snapshot.child("timestamp").getValue(String::class.java).toString()
                        for (id in friendList) {
                            if (img.publisher == id) {
                                it.add(img)
                            }
                        }
                    }
                }
                CoroutineScope(Dispatchers.IO).launch {
                    if (!imgs.isEmpty())
                        updateRoomImg(imgs)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    fun readNewPosts() {
        checkFollowing()

        val currentTime = System.currentTimeMillis()

        val postRef = firebaseDatabase.getReference("Posts")
        postRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                imgsNew.let {
                    it.clear()
                    for (snapshot in dataSnapshot.children) {
                        val img = ImgNew()
                        img.imgId = snapshot.child("postid").getValue(String::class.java).toString()
                        img.publisher =
                            snapshot.child("publisher").getValue(String::class.java).toString()
                        img.img =
                            snapshot.child("postimage").getValue(String::class.java).toString()
                        img.text =
                            snapshot.child("description").getValue(String::class.java).toString()
                        img.views = (snapshot.child("views").childrenCount - 1).toString()
                        img.timestamp =
                            snapshot.child("timestamp").getValue(String::class.java).toString()
                        for (id in friendList) {
                            if (img.publisher == id && currentTime <= img.timestamp.toLong() + 24 * 60 * 60 * 1000) {
                                it.add(img)
                            }
                        }
                    }
                    CoroutineScope(Dispatchers.IO).launch {
                        if (!imgs.isEmpty())
                            updateRoomImgNew(imgsNew)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }
}