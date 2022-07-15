package space.work.training.izi.nav_fragments

import android.content.Context
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import space.work.training.izi.R
import space.work.training.izi.adapters.HomeAdapter
import space.work.training.izi.adapters.ImgListAdapter
import space.work.training.izi.databinding.FragmentHomeBinding
import space.work.training.izi.mvvm.posts.Img
import space.work.training.izi.mvvm.posts.ImgViewModel
import space.work.training.izi.notifications.Token


@AndroidEntryPoint
class HomeFragment : Fragment(), HomeAdapter.OnItemClickListener,
    ImgListAdapter.OnItemClickListener {

    private lateinit var binding: FragmentHomeBinding
    private var imgs: ArrayList<Img> = ArrayList()
    private var newImgs: ArrayList<Img> = ArrayList()

    private val imgViewModel: ImgViewModel by viewModels()
    private lateinit var homeAdapter: HomeAdapter
    private lateinit var listHomeAdapter: ImgListAdapter
    private lateinit var gridManager: StaggeredGridLayoutManager
    private var linearLayoutManager: LinearLayoutManager? = null

    private var state = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = TransitionInflater.from(requireContext())
        enterTransition = inflater.inflateTransition(R.transition.fade)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ivMessage.setOnClickListener {
            findNavController().navigate(R.id.homeToChatList)
        }

        val prefs = requireContext().getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE)
        val editor = prefs.edit()

        gridManager = StaggeredGridLayoutManager(3, LinearLayoutManager.VERTICAL)
        binding.homeRecycler.layoutManager = gridManager
        homeAdapter = HomeAdapter(requireContext(), this)
        binding.homeRecycler.adapter = homeAdapter

        linearLayoutManager = LinearLayoutManager(requireContext())
        binding.listImg.layoutManager = linearLayoutManager
        listHomeAdapter = ImgListAdapter(requireContext(), this)
        binding.listImg.adapter = listHomeAdapter

        getToken()

        imgViewModel.getImgs().observe(viewLifecycleOwner) {
            it.let {
                getPosts(it)
            }
        }

        imgViewModel.getNewImgs().observe(viewLifecycleOwner) {
            it.let {
                getNewPosts(it)
            }
        }

        state = prefs.getBoolean("state", false)

        if (state) {
            binding.homeRecycler.visibility = View.INVISIBLE
            binding.listImg.visibility = View.VISIBLE
        } else {
            binding.listImg.visibility = View.INVISIBLE
            binding.homeRecycler.visibility = View.VISIBLE
        }


        binding.ivList.setOnClickListener {
            if (binding.homeRecycler.visibility == View.VISIBLE) {
                editor.putBoolean("state", true).apply()
                binding.homeRecycler.visibility = View.INVISIBLE
                binding.listImg.visibility = View.VISIBLE
                binding.ivList.setImageResource(R.drawable.ic_grid_blur)
            } else {
                editor.putBoolean("state", false).apply()
                binding.listImg.visibility = View.INVISIBLE
                binding.homeRecycler.visibility = View.VISIBLE
                binding.ivList.setImageResource(R.drawable.ic_img)
            }

        }
    }

    private fun getPosts(pctrs: List<Img>) {
        listHomeAdapter.setData(pctrs)
        imgs.addAll(pctrs)
    }

    private fun getNewPosts(pctrs: List<Img>) {
        homeAdapter.setData(pctrs)
        newImgs.addAll(pctrs)
    }

    private fun getToken() {
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                val token = task.result
                val user = FirebaseAuth.getInstance().currentUser
                val reference = FirebaseDatabase.getInstance().getReference("Tokens")
                val token2 = Token(token)
                reference.child(user!!.uid).setValue(token2)
            }
    }

    override fun onItemClick(position: Int) {
        val action = HomeFragmentDirections.homeToPost(newImgs.get(position).imgId)
        findNavController().navigate(action)
    }

    override fun onListItemClick(position: Int) {
        val action = HomeFragmentDirections.homeToPost(imgs.get(position).imgId)
        findNavController().navigate(action)
    }
}