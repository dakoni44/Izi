package com.social.world.tracy.mvvm.kotlin

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject


class ImgFirebase constructor(private val imgRepository: ImgRepository) {

    private var friendList: ArrayList<String>? = null
    private var imgs: ArrayList<Img>? = null

    init {
        checkFollowing()
    }

    fun getAllImgs(): List<Img>? {
        return imgs
    }

    private fun checkFollowing() {
        friendList = ArrayList<String>()
        val reference = FirebaseDatabase.getInstance().getReference("Friends")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                friendList?.let {
                    it.clear()
                    for (snapshot in dataSnapshot.children) {
                        it.add(snapshot.key.toString())
                    }
                    readPosts()
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }


    private fun readPosts() {
        val postRef = FirebaseDatabase.getInstance().getReference("Posts")
        postRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                imgs?.let {
                    it.clear()
                    for (snapshot in dataSnapshot.children) {
                     /*   var img = Img()
                        img.imgId = snapshot.child("postid").getValue(String::class.java).toString()
                        img.publisher = snapshot.child("publisher").getValue(String::class.java).toString()
                        img.img = snapshot.child("postimage").getValue(String::class.java).toString()
                        img.text = snapshot.child("description").getValue(String::class.java).toString()
                        img.views = snapshot.child("views").childrenCount.toString()
                        for (id in friendList!!) {
                            if (img.publisher == id) {
                                it.add(img)
                            }
                        }*/
                    }
                    if (it.isNotEmpty()) {
                        GlobalScope.launch {
                            imgRepository.notifyFirebaseDataChange()
                        }

                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }
}