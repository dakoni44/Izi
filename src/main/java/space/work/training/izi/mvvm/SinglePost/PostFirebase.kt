package space.work.training.izi.mvvm.SinglePost

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import space.work.training.izi.mvvm.chatList.User
import space.work.training.izi.mvvm.posts.Img
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostFirebase @Inject constructor(
    private var firebaseDatabase: FirebaseDatabase,
    private var firebaseAuth: FirebaseAuth
) {
    private var currentUserID = ""
    private var imgId = ""
    var img: Img? = null
    var user: User? = null
    var user1: User? = null
    private var viewsList: ArrayList<String> = ArrayList()
    private var likedList: ArrayList<String> = ArrayList()
    private var dislikedList: ArrayList<String> = ArrayList()
    private var seen = false
    private var isLike = false
    private var isDislike = false
    private var isMyImg = false
    private var nrLikes = ""
    private var nrDislikes = ""

    private var viewsLive = MutableLiveData<List<String>>()
    private var likesLive = MutableLiveData<List<String>>()
    private var dislikesLive = MutableLiveData<List<String>>()
    private var isLikeL = MutableLiveData<Boolean>()
    private var isDislikeL = MutableLiveData<Boolean>()
    private var isMyImgL = MutableLiveData<Boolean>()
    private var nrLikesL = MutableLiveData<String>()
    private var nrDislikesL = MutableLiveData<String>()
    private var imgLive = MutableLiveData<Img>()

    fun load() {
        currentUserID = firebaseAuth.currentUser!!.uid
        img = Img()
        user = User()
        user1 = User()
        loadViews()
        loadReactions()
        isLikes()
        isDislikes()
        nrLikes()
        nrDislikes()
        showPost()
        updatePost()
    }

    fun setImgId(id: String) {
        imgId = id
    }

    fun getNrLikes(): LiveData<String> {
        return nrLikesL
    }

    fun getNrDislikes(): LiveData<String> {
        return nrDislikesL
    }

    fun getViews(): LiveData<List<String>> {
        return viewsLive
    }

    fun getLikes(): LiveData<List<String>> {
        return likesLive
    }

    fun getDislikes(): LiveData<List<String>> {
        return dislikesLive
    }

    fun isLike(): LiveData<Boolean> {
        return isLikeL
    }

    fun isDislike(): LiveData<Boolean> {
        return isDislikeL
    }

    fun isMyImg(): LiveData<Boolean> {
        return isMyImgL
    }

    @JvmName("getImg1")
    fun getImg(): LiveData<Img> {
        return imgLive
    }

    private fun loadViews() {
        firebaseDatabase.getReference("Posts").child(imgId).child("views")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    viewsList.clear()
                    for (snapshot in dataSnapshot.children) {
                        val id = snapshot.getValue(String::class.java).toString()
                        if (!id.equals(currentUserID))
                            viewsList.add(id)
                    }
                    viewsLive.postValue(viewsList)
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
    }

    private fun loadReactions() {
        firebaseDatabase.getReference("Likes").child(imgId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    likedList.clear()
                    for (snapshot in dataSnapshot.children) {
                        likedList.add(snapshot.key.toString())
                    }
                    likesLive.postValue(likedList)
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })

        firebaseDatabase.getReference("Dislikes").child(imgId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    dislikedList.clear()
                    for (snapshot in dataSnapshot.children) {
                        dislikedList.add(snapshot.key.toString())
                    }
                    dislikesLive.postValue(dislikedList)
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
    }

    private fun isLikes() {
        val reference = firebaseDatabase.reference.child("Likes")
            .child(imgId)
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.child(currentUserID).exists()) {
                    isLike = true
                    isLikeL.postValue(isLike)
                } else {
                    isLike = false
                    isLikeL.postValue(isLike)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun nrLikes() {
        val reference = firebaseDatabase.reference.child("Likes").child(imgId)
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                nrLikes = dataSnapshot.childrenCount.toString()
                nrLikesL.postValue(nrLikes)
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun isDislikes() {
        val reference = firebaseDatabase.reference.child("Dislikes")
            .child(imgId)
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.child(currentUserID).exists()) {
                    isDislike = true
                    isDislikeL.postValue(isDislike)
                } else {
                    isDislike = false
                    isDislikeL.postValue(isDislike)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun nrDislikes() {
        val reference = firebaseDatabase.reference.child("Dislikes").child(imgId)
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                nrDislikes = dataSnapshot.childrenCount.toString()
                nrDislikesL.postValue(nrDislikes)
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

/*    private fun deletePost() {
        postRef!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                postUserId = dataSnapshot.child("publisher").value.toString()
                if (currentUserID == postUserId) {
                    tvDeletePost.setVisibility(View.VISIBLE)
                    tvDeletePost.setOnClickListener(View.OnClickListener {
                        val dialog = Dialog(requireContext())
                        dialog.setContentView(R.layout.remove_dialog)
                        dialog.setCanceledOnTouchOutside(false)
                        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                        val bnRemove = dialog.findViewById<Button>(R.id.bnRemove)
                        bnRemove.setOnClickListener {
                            postRef!!.removeValue()
                            val intent = Intent(this@PostActivity, ProfileActivity::class.java)
                            startActivity(intent)
                        }
                        val bnCancle = dialog.findViewById<Button>(R.id.bnCancle)
                        bnCancle.setOnClickListener { dialog.dismiss() }
                        dialog.show()
                    })
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }*/

    private fun showPost() {
        firebaseDatabase.getReference("Posts").child(imgId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    img?.imgId =
                        dataSnapshot.child("postid").getValue(String::class.java).toString()
                    img?.publisher =
                        dataSnapshot.child("publisher").getValue(String::class.java).toString()
                    img?.img =
                        dataSnapshot.child("postimage").getValue(String::class.java).toString()
                    img?.text =
                        dataSnapshot.child("description").getValue(String::class.java).toString()
                    img?.views = (dataSnapshot.child("views").childrenCount - 1).toString()
                    img?.timestamp =
                        dataSnapshot.child("timestamp").getValue(String::class.java).toString()
                    imgLive.postValue(img)
                    if (currentUserID.equals(img?.publisher)) {
                        isMyImg = true
                        isMyImgL.postValue(isMyImg)
                    } else {
                        isMyImg = false
                        isMyImgL.postValue(isMyImg)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {

                }
            })
    }

    private fun updatePost() {
        firebaseDatabase.getReference("Posts").child(imgId).child("views")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (!dataSnapshot.hasChild(currentUserID)) {
                        seen = true
                    }
                    if (seen) {
                        firebaseDatabase.getReference("Posts").child(imgId).child("views")
                            .child(currentUserID).setValue(currentUserID)
                        seen = false
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
    }
}