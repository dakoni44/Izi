package space.work.training.izi.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.*
import de.hdodenhof.circleimageview.CircleImageView
import space.work.training.izi.R
import space.work.training.izi.mvvm.chat.User
import space.work.training.izi.mvvm.posts.Img

class ParticipantsAdapter(var mContext: Context, timestamp: String) :
    RecyclerView.Adapter<ParticipantsAdapter.ImageViewHolder?>() {

    private var mdata: List<User> = ArrayList()
    private val timestamp: String
    private var circle = true
    private val groupRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("Groups")

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view: View =
            LayoutInflater.from(mContext).inflate(R.layout.participants_item, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val user: User = mdata[position]
        holder.tvUsername.setText(user.username)
        Glide.with(mContext).load(user.image).into(holder.ivProfile)
        checkCircle(position,holder.ivChecked,holder.ivCircle)
    }

    override fun getItemCount(): Int {
        return mdata.size
    }

    fun setData(posts: List<User>) {
        mdata = posts
        notifyDataSetChanged()
    }

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var ivProfile: CircleImageView
        var tvUsername: TextView
        var ivCircle: ImageView
        var ivChecked: ImageView

        init {
            ivProfile = itemView.findViewById(R.id.ivProfile)
            tvUsername = itemView.findViewById<TextView>(R.id.tvUsername)
            ivCircle = itemView.findViewById(R.id.circle)
            ivChecked = itemView.findViewById(R.id.checked)
            itemView.setOnClickListener {
                groupRef.child(timestamp)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (!snapshot.child("Participants")
                                    .child(mdata[getAdapterPosition()].uid).exists() && circle
                            ) {
                                val hashMap = HashMap<String, Any>()
                                hashMap["role"] = "participant"
                                groupRef.child(timestamp).child("Participants")
                                    .child(mdata[getAdapterPosition()].uid).setValue(hashMap)
                                ivChecked.visibility = View.VISIBLE
                                ivCircle.visibility = View.INVISIBLE
                                circle = false
                            } else {
                                ivChecked.visibility = View.INVISIBLE
                                ivCircle.visibility = View.VISIBLE
                                circle = true
                                groupRef.child(timestamp).child("Participants")
                                    .child(mdata[getAdapterPosition()].uid).removeValue()
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {}
                    })
            }
        }
    }

    fun checkCircle(position: Int, iv1 :ImageView, iv2:ImageView){
        groupRef.child(timestamp).child("Participants")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.child(mdata[position].uid).exists()
                    ) {
                        iv1.visibility = View.VISIBLE
                        iv2.visibility = View.INVISIBLE
                    } else {
                        iv1.visibility = View.INVISIBLE
                        iv2.visibility = View.VISIBLE
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    init {
        this.timestamp = timestamp
    }
}