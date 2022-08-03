package space.work.training.izi.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import space.work.training.izi.R
import space.work.training.izi.model.Drink

class CocktailAdapter(mContext: Context, listener: OnItemClickListener) :
    RecyclerView.Adapter<CocktailAdapter.ImageViewHolder>() {

    var mContext: Context
    private var mdata: List<Drink>
    private val listener: OnItemClickListener

    init {
        mdata = ArrayList()
        this.listener = listener
        this.mContext = mContext
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.cocktail_item, parent, false)
        return ImageViewHolder(view, listener)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        if (mdata.size != 0) {
            val drink: Drink = mdata.get(position)
            Glide.with(mContext).load(drink.strDrinkThumb).into(holder.ivCocktail)
            holder.tvName.text = drink.strDrink
        }
    }

    override fun getItemCount(): Int {
        return mdata.size
    }



    fun setData(drink: List<Drink>) {
        if (drink != null) {
            mdata = drink
        } else {
            mdata = ArrayList()
        }

        notifyDataSetChanged()
    }

    class ImageViewHolder(view: View, listener: OnItemClickListener?) :
        RecyclerView.ViewHolder(view) {
        var ivCocktail: ImageView
        var tvName: TextView

        init {
            ivCocktail = view.findViewById(R.id.ivCocktail)
            tvName = view.findViewById(R.id.tvName)
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

}