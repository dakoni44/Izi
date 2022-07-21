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
import space.work.training.izi.mvvm.posts.Img
import space.work.training.izi.mvvm.profile.ProfileImg

class ProfileAdapter(mContext: Context, listener: OnItemClickListener) :
    RecyclerView.Adapter<ProfileAdapter.ImageViewHolder>() {

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
        fun onItemLongClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.home_item, parent, false)
        return ImageViewHolder(view, listener)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser()
        val img: Img = mdata[position]
        unblurPost(holder.ivMainImage, img)
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
        var tvMainText: TextView

        init {
            ivMainImage = view.findViewById(R.id.ivMainImage)
            tvMainText = view.findViewById(R.id.tvMainText)
            view.setOnClickListener {
                if (listener != null) {
                    val position: Int = getAdapterPosition()
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position)
                    }
                }
            }
            itemView.setOnLongClickListener{
                if (listener != null) {
                    val position: Int = getAdapterPosition()
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemLongClick(position)
                    }
                }
                return@setOnLongClickListener true
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
}