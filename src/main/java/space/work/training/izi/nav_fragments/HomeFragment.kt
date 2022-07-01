package space.work.training.izi.nav_fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import space.work.training.izi.mvvm.posts.ImgViewModel
import dagger.hilt.android.AndroidEntryPoint
import space.work.training.izi.R
import space.work.training.izi.adapters.HomeAdapter
import space.work.training.izi.databinding.FragmentHomeBinding
import space.work.training.izi.mvvm.posts.Img
import space.work.training.izi.notifications.Token

@AndroidEntryPoint
class HomeFragment : Fragment(), HomeAdapter.OnItemClickListener {

    private lateinit var binding: FragmentHomeBinding
    private var imgs: ArrayList<Img>? = null

    private val imgViewModel: ImgViewModel by viewModels()
    private lateinit var homeAdapter: HomeAdapter
    private lateinit var gridManager: GridLayoutManager


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ivMessage.setOnClickListener(View.OnClickListener {
            findNavController().navigate(R.id.homeToChatList)
        })

        gridManager = GridLayoutManager(requireContext(), 3)
        binding.homeRecycler.setLayoutManager(gridManager)
        homeAdapter = HomeAdapter(requireContext(), this)
        binding.homeRecycler.setAdapter(homeAdapter)

        getToken()

        imgViewModel.getAllImgs().observe(viewLifecycleOwner) {
            it.let {
                getPosts(it)
            }
        }
    }

    private fun getPosts(pctrs: List<Img>) {
        homeAdapter.setData(pctrs)
        imgs?.addAll(pctrs)
    }

    private fun getToken() {
        FirebaseMessaging.getInstance().getToken()
            .addOnCompleteListener(OnCompleteListener<String?> { task ->
                val token = task.result
                val user = FirebaseAuth.getInstance().currentUser
                val reference = FirebaseDatabase.getInstance().getReference("Tokens")
                val token2 = Token(token)
                reference.child(user!!.uid).setValue(token2)
            })
    }

    override fun onItemClick(position: Int) {
        TODO("Not yet implemented")
    }
}