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

class ParticipantsAdapter(var mContext: Context, mdata: List<User>, timestamp: String) :
    RecyclerView.Adapter<ParticipantsAdapter.ImageViewHolder?>() {

    private val mdata: List<User>
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
    }

    override fun getItemCount(): Int {
        return mdata.size
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

    init {
        this.mdata = mdata
        this.timestamp = timestamp
    }
}