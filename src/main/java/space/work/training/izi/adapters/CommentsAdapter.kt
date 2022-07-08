package space.work.training.izi.adapters

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import de.hdodenhof.circleimageview.CircleImageView
import space.work.training.izi.R
import space.work.training.izi.model.ModelComment

class CommentsAdapter(var mContext: Context, mdata: List<ModelComment>) :
    RecyclerView.Adapter<CommentsAdapter.ImageViewHolder?>() {

    private val mdata: List<ModelComment>
    var publisher = ""
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view: View = LayoutInflater.from(mContext).inflate(R.layout.comment_item, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val modelComment: ModelComment = mdata[position]
        holder.tvUsername.setText(modelComment.uName)
        holder.tvComment.setText(modelComment.comment)
        Glide.with(mContext).load(modelComment.uPic).into(holder.ivProfile)
        val postRef2: DatabaseReference = FirebaseDatabase.getInstance().getReference("Posts")
        postRef2.child(modelComment.postId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                publisher =
                    dataSnapshot.child("publisher").getValue<String>(String::class.java).toString()
                if (modelComment.uId.equals(
                        FirebaseAuth.getInstance().currentUser!!.getUid()
                    ) || publisher == FirebaseAuth.getInstance().currentUser!!.uid
                ) {
                    holder.itemView.setOnClickListener(View.OnClickListener { v ->
                        val postRef: DatabaseReference =
                            FirebaseDatabase.getInstance().getReference("Posts")
                        val alertDialogBuilder = AlertDialog.Builder(v.rootView.context)
                        alertDialogBuilder.setMessage("Remove?")
                        alertDialogBuilder.setPositiveButton("Yes",
                            object : DialogInterface.OnClickListener {
                                override fun onClick(arg0: DialogInterface, arg1: Int) {
                                    postRef.child(modelComment.postId).child("Comments")
                                        .child(modelComment.cid).removeValue()
                                }
                            })
                        alertDialogBuilder.setNegativeButton("No",
                            object : DialogInterface.OnClickListener {
                                override fun onClick(arg0: DialogInterface, arg1: Int) {}
                            })
                        val alertDialog = alertDialogBuilder.create()
                        alertDialog.show()
                        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
                        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK)
                    })
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    override fun getItemCount(): Int {
        return mdata.size
    }

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var ivProfile: CircleImageView
        var tvUsername: TextView
        var tvComment: TextView

        init {
            ivProfile = itemView.findViewById(R.id.ivProfile)
            tvUsername = itemView.findViewById<TextView>(R.id.tvUsername)
            tvComment = itemView.findViewById<TextView>(R.id.tvComment)
        }
    }

    init {
        this.mdata = mdata
    }
}