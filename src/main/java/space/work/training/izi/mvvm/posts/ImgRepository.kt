package space.work.training.izi.mvvm.posts

import android.text.format.DateFormat
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.collections.ArrayList

@Singleton
class ImgRepository @Inject constructor(
) {

    private var friendList: ArrayList<String> = ArrayList()
    private var imgs: ArrayList<Img> = ArrayList()
    private var newImgs: ArrayList<Img> = ArrayList()

    private var onlineList: MutableLiveData<List<Img>> = MutableLiveData()
    private var onlineNew: MutableLiveData<List<Img>> = MutableLiveData()

    /*  suspend fun notifyFirebaseDataChange(list: List<Img>) {
          imgDao.deleteAllImgs()
          for (img in list) {
              imgDao.insert(img)
          }
      }*/

    private fun checkFollowing() {
        val reference = FirebaseDatabase.getInstance().getReference("Friends")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
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


    fun readPosts(): MutableLiveData<List<Img>> {
        checkFollowing()

        val postRef = FirebaseDatabase.getInstance().getReference("Posts")
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
                        img.views = snapshot.child("views").childrenCount.toString()
                        img.timestamp = snapshot.child("timestamp").getValue(String::class.java).toString()
                        for (id in friendList) {
                            if (img.publisher == id ) {
                                it.add(img)
                            }
                        }
                    }
                }
                onlineList.postValue(imgs)
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
        return onlineList
    }

    fun readNewPosts(): MutableLiveData<List<Img>> {
        checkFollowing()

        val currentTime=System.currentTimeMillis()

        val postRef = FirebaseDatabase.getInstance().getReference("Posts")
        postRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                newImgs.let {
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
                        img.views = snapshot.child("views").childrenCount.toString()
                        img.timestamp = snapshot.child("timestamp").getValue(String::class.java).toString()
                        for (id in friendList) {
                            if (img.publisher == id && currentTime <= img.timestamp.toLong()+24*60*60*1000) {
                                it.add(img)
                            }
                        }
                    }
                }
                onlineNew.postValue(newImgs)
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
        return onlineNew
    }
}