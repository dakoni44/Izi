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
import space.work.training.izi.mvvm.posts.newImgs.ImgNew

class HomeAdapter(mContext: Context, listener: OnItemClickListener) :
    RecyclerView.Adapter<HomeAdapter.ImageViewHolder>() {

    var mContext: Context
    private var mdata: List<ImgNew>
    private val listener: OnItemClickListener
    private var firebaseUser: FirebaseUser? = null

    init {
        mdata = ArrayList<ImgNew>()
        this.listener = listener
        this.mContext = mContext
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.home_item, parent, false)
        return ImageViewHolder(view, listener)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser()
        val img: ImgNew = mdata[position]
        unblurPost(holder.ivMainImage, img)
        publisherInfo(img.publisher, holder.tvMainText, holder.ivProfilePic)
    }

    override fun getItemCount(): Int {
        return mdata.size
    }

    fun setData(posts: List<ImgNew>) {
        mdata = posts
        notifyDataSetChanged()
    }

    class ImageViewHolder(view: View, listener: OnItemClickListener?) :
        RecyclerView.ViewHolder(view) {
        var ivMainImage: ImageView
        var tvMainText: TextView
        var ivProfilePic: ImageView

        init {
            ivMainImage = view.findViewById(R.id.ivMainImage)
            tvMainText = view.findViewById(R.id.tvMainText)
            ivProfilePic = itemView.findViewById<ImageView>(R.id.ivProfilePic)
            view.setOnClickListener {
                if (listener != null) {
                    val position: Int = getAdapterPosition()
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position)
                    }
                }
            }
        }
    }

    private fun unblurPost(ivPost: ImageView, post: ImgNew) {
        val postRef: DatabaseReference =
            FirebaseDatabase.getInstance().getReference("Posts").child(post.imgId)
        postRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.child("views").child(firebaseUser!!.uid).exists()) {
                    Glide.with(mContext).load(post.img).into(ivPost)
                } else {
                    Glide.with(mContext).load(post.img)
                        .apply(RequestOptions.bitmapTransform(BlurTransformation(50, 5)))
                        .into(ivPost)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun publisherInfo(userid: String, username: TextView, ivuser: ImageView) {
        val reference = FirebaseDatabase.getInstance().getReference("Users")
            .child(userid)
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = User()
                user.uid = userid
                user.name = dataSnapshot.child("name").getValue(String::class.java).toString()
                user.username =
                    dataSnapshot.child("username").getValue(String::class.java).toString()
                user.email = dataSnapshot.child("email").getValue(String::class.java).toString()
                user.image = dataSnapshot.child("image").getValue(String::class.java).toString()
                username.setText(user.username)
                Glide.with(mContext).load(user.image).into(ivuser)
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

}