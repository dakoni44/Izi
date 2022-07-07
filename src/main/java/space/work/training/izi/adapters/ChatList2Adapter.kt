package space.work.training.izi.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView
import space.work.training.izi.R
import space.work.training.izi.mvvm.chat.User

class ChatList2Adapter(private val mContext: Context, listener: OnItemClickListener) :
    RecyclerView.Adapter<ChatList2Adapter.ImageViewHolder?>() {

    private var mdata: List<User> = ArrayList()
    private val lastMessageMap: HashMap<String, String> = HashMap()
    private val listener: OnItemClickListener
    var time: String? = null

    init {
        this.listener=listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view: View =
            LayoutInflater.from(mContext).inflate(R.layout.chat_list_item, parent, false)
        return ImageViewHolder(view)
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val user = mdata[position]
        val uid = mdata[position].uid
        val lastMessage = lastMessageMap[uid]
        val time = lastMessageMap[uid + "time"]
        holder.tvUsername.setText(user.username)
        if (lastMessage == null || lastMessage == "default") {
            holder.tvMessage.setVisibility(View.GONE)
        } else {
            holder.tvMessage.setVisibility(View.VISIBLE)
            holder.tvMessage.setText(lastMessage)
            holder.tvTime.setText("· $time")
        }
        Glide.with(mContext).load(user.image).into(holder.ivFind)
    }

    fun setData(users: List<User>) {
        mdata = users
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return mdata.size
    }

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var ivFind: CircleImageView
        var tvUsername: TextView
        var tvMessage: TextView
        var tvTime: TextView

        init {
            ivFind = itemView.findViewById(R.id.ivFind)
            tvUsername = itemView.findViewById<TextView>(R.id.tvUsername)
            tvMessage = itemView.findViewById<TextView>(R.id.tvMessage)
            tvTime = itemView.findViewById<TextView>(R.id.tvTime)
            itemView.setOnClickListener {
                val position: Int = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(position)
                }
            }
        }
    }

    fun setLastMessageMap(userId: String, lastMessage: String, time: String) {
        lastMessageMap[userId] = lastMessage
        lastMessageMap[userId + "time"] = time
    }
}