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
import space.work.training.izi.model.Users
import space.work.training.izi.mvvm.Img

class HomeAdapter(mContext: Context, listener: OnItemClickListener) :
    RecyclerView.Adapter<HomeAdapter.ImageViewHolder>() {

    var mContext: Context
    private var mdata: List<Img>
    private val listener: OnItemClickListener
    private var firebaseUser: FirebaseUser? = null

    init {
        mdata = ArrayList<Img>()
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
        val img: Img = mdata[position]
        unblurPost(holder.ivMainImage, img)
        publisherInfo(img.publisher, holder.tvMainText, holder.ivProfilePic)
    }

    override fun getItemCount(): Int {
        return mdata.size
    }

    fun setData(posts: List<Img>) {
        mdata = posts
        notifyDataSetChanged()
    }

    class ImageViewHolder(view: View, listener: OnItemClickListener?) :
        RecyclerView.ViewHolder(view) {
        var ivMainImage: ImageView
        var ivProfilePic: ImageView
        var tvMainText: TextView

        init {
            ivMainImage = view.findViewById(R.id.ivMainImage)
            ivProfilePic = view.findViewById(R.id.tvMainText)
            tvMainText = view.findViewById(R.id.ivProfilePic)
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

    private fun unblurPost(ivPost: ImageView, post: Img) {
        val postRef: DatabaseReference =
            FirebaseDatabase.getInstance().getReference("Posts").child(post.imgId)
        postRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.child("views").child(firebaseUser!!.getUid()).exists()) {
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
        val reference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Users")
            .child(userid)
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = Users()
                user.uid = userid
                user.name =
                    dataSnapshot.child("name").getValue<String>(String::class.java).toString()
                user.username =
                    dataSnapshot.child("username").getValue<String>(String::class.java).toString()
                user.email =
                    dataSnapshot.child("email").getValue<String>(String::class.java).toString()
                user.image =
                    dataSnapshot.child("image").getValue<String>(String::class.java).toString()
                username.setText(user.username)
                Glide.with(mContext).load(user.image).into(ivuser)
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

}