package space.work.training.izi.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import jp.wasabeef.glide.transformations.BlurTransformation
import space.work.training.izi.R
import space.work.training.izi.mvvm.chatList.User
import space.work.training.izi.mvvm.posts.Img

class ImgListAdapter(var mContext: Context, listener: OnItemClickListener) :
    RecyclerView.Adapter<ImgListAdapter.ImageViewHolder?>() {

    private var mdata: List<Img>
    private val listener: OnItemClickListener
    private var firebaseUser: FirebaseUser? = null

    interface OnItemClickListener {
        fun onListItemClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view: View =
            LayoutInflater.from(mContext).inflate(R.layout.img_list_item, parent, false)
        return ImageViewHolder(view, listener)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser()
        val img: Img = mdata[position]
        unblurPost(holder.ivMainImage, img)
        publisherInfo(img.publisher, holder.tvMainText, holder.ivProfilePic)
        showNumbers(holder.likes, holder.dislikes, holder.views, img)
    }

    override fun getItemCount(): Int {
        return mdata.size
    }

    fun setData(posts: List<Img>) {
        mdata = posts
        notifyDataSetChanged()
    }

    inner class ImageViewHolder(itemView: View, listener: OnItemClickListener?) :
        RecyclerView.ViewHolder(itemView) {
        var ivMainImage: ImageView
        var ivProfilePic: ImageView
        var tvMainText: TextView
        var likes: TextView
        var dislikes: TextView
        var views: TextView

        init {
            ivMainImage = itemView.findViewById(R.id.ivMainImage)
            tvMainText = itemView.findViewById<TextView>(R.id.tvMainText)
            ivProfilePic = itemView.findViewById(R.id.ivProfilePic)
            likes = itemView.findViewById(R.id.likes)
            dislikes = itemView.findViewById(R.id.dislikes)
            views = itemView.findViewById(R.id.views)
            itemView.setOnClickListener {
                if (listener != null) {
                    val position: Int = getAdapterPosition()
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onListItemClick(position)
                    }
                }
            }
        }
    }

    private fun unblurPost(ivPost: ImageView, img: Img) {
        val postRef: DatabaseReference =
            FirebaseDatabase.getInstance().getReference("Posts").child(img.imgId)
        postRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.child("views").child(firebaseUser!!.getUid()).exists()) {
                    Glide.with(mContext).load(img.img).into(ivPost)
                } else {
                    Glide.with(mContext).load(img.img)
                        .apply(RequestOptions.bitmapTransform(BlurTransformation(50, 5)))
                        .into(ivPost)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun publisherInfo(userid: String, username: TextView, ivuser: ImageView) {
        val reference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Users")
            .child(userid)
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = User()
                user.uid = userid
                user.name =
                    dataSnapshot.child("name").getValue<String>(String::class.java).toString()
                user.username =
                    dataSnapshot.child("username").getValue<String>(String::class.java).toString()
                user.email =
                    dataSnapshot.child("email").getValue<String>(String::class.java).toString()
                user.image =
                    dataSnapshot.child("image").getValue<String>(String::class.java).toString()
                username.text = user.username
                Glide.with(mContext).load(user.image).into(ivuser)
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun showNumbers(likes: TextView, dislikes: TextView, views: TextView, img: Img) {
        val likeRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("Likes")
            .child(img.imgId)
        likeRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                likes.text = dataSnapshot.childrenCount.toString()
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })

        val dislikeRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("Dislikes")
            .child(img.imgId)
        dislikeRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dislikes.text = dataSnapshot.childrenCount.toString()
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })

        views.text = img.views
    }

    init {
        mdata = ArrayList<Img>()
        this.listener = listener
    }
}