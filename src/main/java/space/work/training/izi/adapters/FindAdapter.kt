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
import space.work.training.izi.model.User

class FindAdapter(var mContext: Context, mdata: List<User>, listener: OnItemClickListener) :
    RecyclerView.Adapter<FindAdapter.ImageViewHolder?>() {

    private val mdata: List<User>
    private val listener: OnItemClickListener

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view: View = LayoutInflater.from(mContext).inflate(R.layout.find_item, parent, false)
        return ImageViewHolder(view, listener)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val user: User = mdata[position]
        holder.tvUsername.text = user.username
        holder.tvName.text = user.name
        Glide.with(mContext).load(user.image).into(holder.ivFind)
    }

    override fun getItemCount(): Int {
        return mdata.size
    }

    inner class ImageViewHolder(itemView: View, listener: OnItemClickListener?) :
        RecyclerView.ViewHolder(itemView) {
        var ivFind: CircleImageView
        var tvUsername: TextView
        var tvName: TextView

        init {
            ivFind = itemView.findViewById(R.id.ivFind)
            tvUsername = itemView.findViewById(R.id.tvUsername)
            tvName = itemView.findViewById(R.id.tvName)
            itemView.setOnClickListener {
                if (listener != null) {
                    val position: Int = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position)
                    }
                }
            }
        }
    }

    init {
        this.mdata = mdata
        this.listener = listener
    }
}