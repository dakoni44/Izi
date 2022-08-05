package space.work.training.izi.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import de.hdodenhof.circleimageview.CircleImageView
import space.work.training.izi.R

class LikesAdapter(var mContext: Context, listener: OnItemClickListener) :
    RecyclerView.Adapter<LikesAdapter.ImageViewHolder?>() {

    private var mdata: List<String> = ArrayList()
    var publisher = ""
    private var image = ""
    private var username = ""
    private var userRef = FirebaseDatabase.getInstance().getReference("Users")
    private val listener: OnItemClickListener


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view: View = LayoutInflater.from(mContext).inflate(R.layout.comment_item, parent, false)
        return ImageViewHolder(view)
    }

    interface OnItemClickListener {
        fun onLikeClick(position: Int)
    }

    fun setData(users: List<String>) {
        mdata = users
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val id: String = mdata[position]

        userRef.child(id).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                image = snapshot.child("image").value.toString()
                username = snapshot.child("username").value.toString()
                holder.tvUsernameComment.text = username
                Glide.with(mContext).load(image).into(holder.ivProfile)
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }


    override fun getItemCount(): Int {
        return mdata.size
    }

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var ivProfile: CircleImageView
        var tvUsername: TextView
        var tvComment: TextView
        var tvUsernameComment: TextView

        init {
            ivProfile = itemView.findViewById(R.id.ivProfile)
            tvUsername = itemView.findViewById<TextView>(R.id.tvUsername)
            tvUsernameComment = itemView.findViewById<TextView>(R.id.tvUsernameComment)
            tvComment = itemView.findViewById<TextView>(R.id.tvComment)
            tvComment.visibility = View.INVISIBLE
            tvUsername.visibility = View.INVISIBLE
            tvUsernameComment.visibility = View.VISIBLE
            itemView.setOnClickListener {
                val position: Int = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onLikeClick(position)
                }
            }
        }
    }

    init {
        this.listener = listener
    }
}