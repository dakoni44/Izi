package space.work.training.izi.nav_fragments

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.AndroidEntryPoint
import space.work.training.izi.R
import space.work.training.izi.adapters.ProfileAdapter
import space.work.training.izi.databinding.FragmentProfileBinding
import space.work.training.izi.model.Img
import space.work.training.izi.mvvm.profile.ProfileViewModel
import space.work.training.izi.mvvm.profile.UserInfo
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : Fragment(), ProfileAdapter.OnItemClickListener {

    private lateinit var binding: FragmentProfileBinding

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    private var gridManager: StaggeredGridLayoutManager? = null
    private var profileAdapter: ProfileAdapter? = null
    private val imgs: ArrayList<Img> = ArrayList()

    private var userID: String? = null

    private val profileViewModel: ProfileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = TransitionInflater.from(requireContext())
        enterTransition = inflater.inflateTransition(R.transition.fade)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userID = firebaseAuth.currentUser!!.uid

        gridManager = StaggeredGridLayoutManager(3, 1)
        binding.profileRecycler.layoutManager = gridManager
        profileAdapter = ProfileAdapter(requireContext(), this)
        binding.profileRecycler.adapter = profileAdapter

        binding.bnEditProfile.setOnClickListener {
            findNavController().navigate(R.id.profileToEditProfile)
        }

        binding.bnLogout.setOnClickListener {
            val dialog = Dialog(requireContext())
            dialog.setContentView(R.layout.logout_dialog)
            dialog.setCanceledOnTouchOutside(false)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val bnLogout = dialog.findViewById<Button>(R.id.bnLogout)
            bnLogout.setOnClickListener {
                firebaseAuth.signOut()
                dialog.dismiss()
                findNavController().navigate(R.id.profileToLogIn)
            }
            val bnCancel = dialog.findViewById<Button>(R.id.bnCancle)
            bnCancel.setOnClickListener { dialog.dismiss() }
            dialog.show()
        }

        profileViewModel.getUserInfo(userID!!).observe(viewLifecycleOwner) {
            it?.let {
                updateUi(it)
                updateUiImgs(it)
            }
        }

        binding.bBio.setOnClickListener {
            if (binding.rlBio.visibility == View.GONE) {
                binding.rlBio.visibility = View.VISIBLE
                binding.ivArrow.animate().rotation(180f)
            } else {
                binding.rlBio.visibility = View.GONE
                binding.ivArrow.animate().rotation(0f)
            }
        }
    }

    private fun updateUi(userInfo: UserInfo) {
        if (userInfo.uList.size > 0) {
            binding.tvName.text = userInfo.uList.get(0)
            binding.tvNameFull.text = userInfo.uList.get(1)
            binding.tvBio.text = userInfo.uList.get(2)
            Glide.with(requireContext()).load(userInfo.uList.get(3)).into(binding.ivMalaSlika1)
        }
        binding.tvFriends.text = userInfo.friends
        binding.tvViews.text = userInfo.views
        binding.tvLikes.text = userInfo.likes
        binding.tvDislikes.text = userInfo.dislikes
        binding.tvPosts.text = userInfo.posts
    }

    private fun updateUiImgs(userInfo: UserInfo) {
        profileAdapter!!.setData(userInfo.profileImgs)
        imgs.addAll(userInfo.profileImgs)
    }

    override fun onItemClick(position: Int) {
        val action = ProfileFragmentDirections.profileToImgList(position, userID!!)
        findNavController().navigate(action)
    }

    override fun onItemLongClick(position: Int) {
        val alertDialogBuilder = AlertDialog.Builder(requireActivity())
        alertDialogBuilder.setMessage("Delete image?")
        alertDialogBuilder.setPositiveButton(
            "Ok"
        ) { _, _ ->
            val dbRef = FirebaseDatabase.getInstance().getReference("Posts")
            dbRef.child(imgs.get(position).imgId).removeValue()
        }
        alertDialogBuilder.setNegativeButton(
            "Cancel"
        ) { arg0, arg1 -> }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK)
    }

}