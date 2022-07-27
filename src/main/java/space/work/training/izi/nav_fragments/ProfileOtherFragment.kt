package space.work.training.izi.nav_fragments

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import space.work.training.izi.R
import space.work.training.izi.adapters.ProfileAdapter
import space.work.training.izi.databinding.FragmentProfileOtherBinding
import space.work.training.izi.model.Img
import space.work.training.izi.mvvm.profile.UserInfo
import space.work.training.izi.mvvm.profileOther.ProfileOtherViewModel

@AndroidEntryPoint
class ProfileOtherFragment : Fragment(), ProfileAdapter.OnItemClickListener {

    private lateinit var binding: FragmentProfileOtherBinding

    private val args: ProfileOtherFragmentArgs by navArgs()
    private var friendId: String? = null
    private val profileOtherViewModel: ProfileOtherViewModel by viewModels()

    private var profileRManager: StaggeredGridLayoutManager? = null
    private var profileAdapter: ProfileAdapter? = null
    private val imgs: ArrayList<Img> = ArrayList<Img>()

    private var senderId: String? = null
    private var CURRENT_STATE: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_profile_other, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        friendId = args.uId
        profileOtherViewModel.setFriendId(friendId!!)
        profileOtherViewModel.load()

        CURRENT_STATE = "not_friends"

        profileRManager = StaggeredGridLayoutManager(3, 1)
        binding.profileRecycler.layoutManager = profileRManager
        profileAdapter = ProfileAdapter(requireContext(), this)
        binding.profileRecycler.adapter = profileAdapter

        binding.sendMessage.setOnClickListener {
            val action = ProfileOtherFragmentDirections.profileOtherToChat(friendId!!)
            findNavController().navigate(action)
        }

        binding.tvNameFull.setOnClickListener {
            if (binding.rlBio.visibility == View.GONE) {
                binding.rlBio.visibility = View.VISIBLE
                binding.ivArrow.animate().rotation(180f)
            } else {
                binding.rlBio.visibility = View.GONE
                binding.ivArrow.animate().rotation(0f)
            }
        }

        /*  lifecycleScope.launch {
              repeatOnLifecycle(Lifecycle.State.STARTED) {
                  profileOtherViewModel.getProfileUserFlow2().collect {
                      updateUi(it)
                  }
              }
          }*/

        /*   lifecycleScope.launch {
             repeatOnLifecycle(Lifecycle.State.STARTED) {
                 profileOtherViewModel.getProfileUserFlow().collect {
                     updateUi(it)
                 }
             }
         }*/

        /*  profileOtherViewModel.getProfileUserFlow()
          profileOtherViewModel.profileUser.observe(viewLifecycleOwner) {
              updateUi(it)
          }*/


    }

    private fun updateUi(userInfo: UserInfo) {
        if (userInfo.uList.size > 0) {
            binding.tvName.text = userInfo.uList.get(0)
            binding.tvNameFull.text = userInfo.uList.get(1)
            binding.tvBio.text = userInfo.uList.get(2)
            Glide.with(requireContext()).load(userInfo.uList.get(3)).into(binding.ivMalaSlika1)

            binding.tvUsername.text = userInfo.uList.get(0)
            binding.tvName2.text = userInfo.uList.get(1)
            Glide.with(requireContext()).load(userInfo.uList.get(3)).into(binding.ivProfile)
            Glide.with(requireContext()).load(R.drawable.background).into(binding.ivBackground)
        }
        binding.tvFriends.text = userInfo.friends
        binding.tvViews.text = userInfo.views
        binding.tvLikes.text = userInfo.likes
        binding.tvDislikes.text = userInfo.dislikes
        binding.tvPosts.text = userInfo.posts
    }

    fun checkState(state: String) {
        if (state.equals("request_sent")) {
            binding.bnSendRequest2.text = "Cancel request"
        } else if (state.equals("request_received")) {
            binding.bnSendRequest2.text = "Accept request"
        } else if (state.equals("not_friends")) {
            binding.bnSendRequest2.text = "Add friend"
        }

        if (state.equals("friends")) {
            binding.rlFriends.visibility = View.VISIBLE
            binding.rlNotFriends.visibility = View.INVISIBLE
        } else if (state.equals("not_friends")) {
            binding.rlFriends.visibility = View.INVISIBLE
            binding.rlNotFriends.visibility = View.VISIBLE
        }

        binding.removeFriend.setOnClickListener(View.OnClickListener {
            if (state.equals("friends")) {
                val dialog = Dialog(requireActivity())
                dialog.setContentView(R.layout.remove_dialog)
                dialog.setCanceledOnTouchOutside(false)
                dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                val bnRemove = dialog.findViewById<Button>(R.id.bnRemove)
                bnRemove.setOnClickListener {
                    profileOtherViewModel.remove()
                    dialog.dismiss()
                }
                val bnCancle = dialog.findViewById<Button>(R.id.bnCancle)
                bnCancle.setOnClickListener { dialog.dismiss() }
                dialog.show()
            }
        })

        binding.bnSendRequest2.setOnClickListener(View.OnClickListener {
            if (state.equals("request_received")) {
                profileOtherViewModel.accept()
            } else if (state.equals("not_friends")) {
                profileOtherViewModel.send()
            } else if (state.equals("request_sent")) {
                profileOtherViewModel.cancel()
            }
        })
    }

    override fun onItemClick(position: Int) {
        val action = ProfileOtherFragmentDirections.profileOtherToImgList(position, friendId!!)
        findNavController().navigate(action)
    }

    override fun onItemLongClick(position: Int) {

    }

    override fun onResume() {
        super.onResume()
        profileOtherViewModel.getImgs().observe(viewLifecycleOwner) {
            profileAdapter!!.setData(it)
        }

        profileOtherViewModel.getCurrState().observe(viewLifecycleOwner) {
            checkState(it)
        }

        profileOtherViewModel.getProfileUser().observe(viewLifecycleOwner) {
            updateUi(it)
        }
    }

}