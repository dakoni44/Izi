package space.work.training.izi.adapters

import android.content.Context
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import de.hdodenhof.circleimageview.CircleImageView
import space.work.training.izi.R
import space.work.training.izi.model.Chat
import java.util.*

class ChatAdapter(var mContext: Context, listener: OnItemClickListener) :
    RecyclerView.Adapter<ChatAdapter.ImageViewHolder?>() {

    private var mdata: List<Chat>
    private var imageUri: String = ""
    private var firebaseUser: FirebaseUser? = null
    private var listener: OnItemClickListener

    interface OnItemClickListener {
        fun onItemLongClick(position: Int)
    }

    override fun getItemViewType(position: Int): Int {
        //mozda ovde treba switch case
        firebaseUser = FirebaseAuth.getInstance().currentUser

        if (mdata.size < 2) {
            if (mdata[position].sender.equals(firebaseUser?.uid)) {
                return MSG_TYPE_RIGHT_ALONE
            } else if (!mdata[position].sender.equals(firebaseUser?.uid)) {
                return MSG_TYPE_LEFT_ALONE
            }
        }
        return if (position == 0 && !mdata[position].sender
                .equals(mdata[position + 1].sender) && mdata[position].sender
                .equals(firebaseUser?.uid)
        ) {
            MSG_TYPE_RIGHT_ALONE
        } else if (position == 0 && mdata[position].sender
                .equals(mdata[position + 1].sender) && mdata[position].sender
                .equals(firebaseUser?.uid)
        ) {
            MSG_TYPE_RIGHT_FIRST
        } else if (position == 0 && !mdata[position].sender
                .equals(mdata[position + 1].sender) && !mdata[position].sender
                .equals(firebaseUser?.uid)
        ) {
            MSG_TYPE_LEFT_ALONE
        } else if (position == 0 && mdata[position].sender
                .equals(mdata[position + 1].sender) && !mdata[position].sender
                .equals(firebaseUser?.uid)
        ) {
            MSG_TYPE_LEFT_FIRST
        } else if (position == mdata.size - 1 && mdata[position].sender
                .equals(mdata[position - 1].sender)
            && mdata[position].sender.equals(firebaseUser?.uid)
        ) {
            MSG_TYPE_RIGHT_LAST
        } else if (position == mdata.size - 1 && !mdata[position].sender
                .equals(mdata[position - 1].sender)
            && mdata[position].sender.equals(firebaseUser?.uid)
        ) {
            MSG_TYPE_RIGHT_ALONE
        } else if (position == mdata.size - 1 && mdata[position].sender
                .equals(mdata[position - 1].sender)
            && !mdata[position].sender.equals(firebaseUser?.uid)
        ) {
            MSG_TYPE_LEFT_LAST
        } else if (position == mdata.size - 1 && !mdata[position].sender
                .equals(mdata[position - 1].sender)
            && !mdata[position].sender.equals(firebaseUser?.uid)
        ) {
            MSG_TYPE_LEFT_ALONE
        } else if (position > 0 && position < mdata.size - 1 && !mdata[position].sender
                .equals(mdata[position - 1].sender)
            && !mdata[position].sender
                .equals(mdata[position + 1].sender) && mdata[position].sender
                .equals(firebaseUser?.uid)
        ) {
            MSG_TYPE_RIGHT_ALONE
        } else if (position > 0 && position < mdata.size - 1 && !mdata[position].sender
                .equals(mdata[position - 1].sender)
            && !mdata[position].sender
                .equals(mdata[position + 1].sender) && !mdata[position].sender
                .equals(firebaseUser?.uid)
        ) {
            MSG_TYPE_LEFT_ALONE
        } else if (position > 0 && !mdata[position].sender
                .equals(mdata[position - 1].sender)
            && mdata[position].sender.equals(firebaseUser?.uid)
        ) {
            MSG_TYPE_RIGHT_FIRST
        } else if (position > 0 && !mdata[position].sender
                .equals(mdata[position - 1].sender)
            && !mdata[position].sender.equals(firebaseUser?.uid)
        ) {
            MSG_TYPE_LEFT_FIRST
        } else if (position < mdata.size - 1 && !mdata[position].sender
                .equals(mdata[position + 1].sender)
            && mdata[position].sender.equals(firebaseUser?.uid)
        ) {
            MSG_TYPE_RIGHT_LAST
        } else if (position < mdata.size - 1 && !mdata[position].sender
                .equals(mdata[position + 1].sender)
            && !mdata[position].sender.equals(firebaseUser?.uid)
        ) {
            MSG_TYPE_LEFT_LAST
        } else if (mdata[position].sender.equals(firebaseUser?.uid)) {
            MSG_TYPE_RIGHT
        } else {
            MSG_TYPE_LEFT
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        return if (viewType == MSG_TYPE_RIGHT_FIRST) {
            val view: View =
                LayoutInflater.from(mContext).inflate(R.layout.row_chat_right_first, parent, false)
            ImageViewHolder(view)
        } else if (viewType == MSG_TYPE_LEFT_FIRST) {
            val view: View =
                LayoutInflater.from(mContext).inflate(R.layout.row_chat_left_first, parent, false)
            ImageViewHolder(view)
        } else if (viewType == MSG_TYPE_RIGHT_LAST) {
            val view: View =
                LayoutInflater.from(mContext).inflate(R.layout.row_chat_right_last, parent, false)
            ImageViewHolder(view)
        } else if (viewType == MSG_TYPE_LEFT_LAST) {
            val view: View =
                LayoutInflater.from(mContext).inflate(R.layout.row_chat_left_last, parent, false)
            ImageViewHolder(view)
        } else if (viewType == MSG_TYPE_RIGHT_ALONE) {
            val view: View =
                LayoutInflater.from(mContext).inflate(R.layout.row_chat_right_alone, parent, false)
            ImageViewHolder(view)
        } else if (viewType == MSG_TYPE_LEFT_ALONE) {
            val view: View =
                LayoutInflater.from(mContext).inflate(R.layout.row_chat_left_alone, parent, false)
            ImageViewHolder(view)
        } else if (viewType == MSG_TYPE_RIGHT) {
            val view: View =
                LayoutInflater.from(mContext).inflate(R.layout.row_chat_right, parent, false)
            ImageViewHolder(view)
        } else {
            val view: View =
                LayoutInflater.from(mContext).inflate(R.layout.row_chat_left, parent, false)
            ImageViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val message: String = mdata[position].message
        val timestamp: String = mdata[position].timestamp
        val cal = Calendar.getInstance(Locale.ENGLISH)
        cal.timeInMillis = timestamp.toLong()
        val dateTime = DateFormat.format("HH:mm", cal).toString()
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser()
        holder.tvMessage.text = message
        holder.tvTime.text = dateTime
        Glide.with(mContext).load(imageUri).into(holder.ivProfile)
        if (mdata[position].isSeen) {
            holder.ivSeen.setImageResource(R.drawable.ic_seen)
        } else {
            holder.ivSeen.setImageResource(R.drawable.ic_delivered)
        }
    }

    fun setData(posts: List<Chat>) {
        mdata = posts
        notifyDataSetChanged()
    }

    fun setImageUri(uri: String) {
        imageUri = uri
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return mdata.size
    }

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var ivProfile: CircleImageView
        var tvMessage: TextView
        var tvTime: TextView
        var ivSeen: ImageView

        init {
            ivProfile = itemView.findViewById(R.id.ivProfile)
            tvMessage = itemView.findViewById<TextView>(R.id.tvMessage)
            tvTime = itemView.findViewById<TextView>(R.id.tvTime)
            ivSeen = itemView.findViewById(R.id.ivSeen)
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

    companion object {
        private const val MSG_TYPE_LEFT = 0
        private const val MSG_TYPE_RIGHT = 1
        private const val MSG_TYPE_LEFT_FIRST = 2
        private const val MSG_TYPE_RIGHT_FIRST = 3
        private const val MSG_TYPE_LEFT_LAST = 4
        private const val MSG_TYPE_RIGHT_LAST = 5
        private const val MSG_TYPE_LEFT_ALONE = 6
        private const val MSG_TYPE_RIGHT_ALONE = 7
    }

    init {
        this.mdata = ArrayList()
        this.imageUri = imageUri
        this.listener = listener
    }
}