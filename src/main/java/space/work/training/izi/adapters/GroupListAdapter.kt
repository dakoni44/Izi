package space.work.training.izi.adapters

import android.content.Context
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.*
import de.hdodenhof.circleimageview.CircleImageView
import space.work.training.izi.R
import space.work.training.izi.model.GroupList
import java.util.*

class GroupListAdapter(
    var mContext: Context,
    mdata: List<GroupList>,
    listener: OnItemClickListener
) :
    RecyclerView.Adapter<GroupListAdapter.ImageViewHolder?>() {

    private val mdata: List<GroupList>
    private val listener: OnItemClickListener

    interface OnItemClickListener {
        fun onItemGroupClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view: View =
            LayoutInflater.from(mContext).inflate(R.layout.groupchat_list_item, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val modelGroup: GroupList = mdata[position]
        val groupId: String = mdata[position].id
        holder.tvGroupName.text = modelGroup.name
        lastMessage(modelGroup, holder)
        if (modelGroup.icon.equals("")) {
            Glide.with(mContext).load(R.drawable.background).into(holder.ivProfile)
        } else {
            Glide.with(mContext).load(modelGroup.icon).into(holder.ivProfile)
        }
        /*  holder.itemView.setOnClickListener(View.OnClickListener {
              val intent = Intent(mContext, GroupChatActivity::class.java)
              intent.putExtra("groupId", groupId)
              intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
              mContext.startActivity(intent)
          })*/
    }

    override fun getItemCount(): Int {
        return mdata.size
    }

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var ivProfile: CircleImageView
        var tvGroupName: TextView
        var tvSender: TextView
        var tvTime: TextView

        init {
            ivProfile = itemView.findViewById(R.id.ivProfile)
            tvGroupName = itemView.findViewById(R.id.tvGroupName)
            tvSender = itemView.findViewById(R.id.tvSender)
            tvTime = itemView.findViewById(R.id.tvTime)
            itemView.setOnClickListener {
                val position: Int = getAdapterPosition()
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemGroupClick(position)
                }
            }
        }
    }

    fun lastMessage(modelGroup: GroupList, holder: ImageViewHolder) {
        val ref: DatabaseReference = FirebaseDatabase.getInstance().getReference("Groups")
        ref.child(modelGroup.id).child("Messages").limitToLast(1)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (ds in snapshot.getChildren()) {
                        val message: String = ds.child("message").getValue<String>(
                            String::class.java
                        ).toString()
                        val timestamp: String = ds.child("timestamp").getValue<String>(
                            String::class.java
                        ).toString()
                        val sender: String =
                            ds.child("sender").getValue<String>(String::class.java).toString()
                        val cal = Calendar.getInstance(Locale.ENGLISH)
                        cal.timeInMillis = timestamp.toLong()
                        val dateTime = DateFormat.format("HH:mm", cal).toString()
                        holder.tvTime.text = "· $dateTime"
                        val ref: DatabaseReference =
                            FirebaseDatabase.getInstance().getReference("Users")
                        ref.child(sender)
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    holder.tvSender.text =
                                        snapshot.child("username").getValue<String>(
                                            String::class.java
                                        ).toString() + ": " + message
                                }

                                override fun onCancelled(error: DatabaseError) {}
                            })
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    init {
        this.mdata = mdata
        this.listener = listener
    }
}