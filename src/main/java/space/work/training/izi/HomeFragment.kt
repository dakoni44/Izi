package space.work.training.izi

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import com.social.world.tracy.mvvm.kotlin.Img
import com.social.world.tracy.mvvm.kotlin.ImgViewModel
import dagger.hilt.android.AndroidEntryPoint
import space.work.training.izi.adapters.HomeAdapter
import space.work.training.izi.databinding.FragmentHomeBinding
import space.work.training.izi.notifications.Token

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

@AndroidEntryPoint
class HomeFragment : Fragment() , HomeAdapter.OnItemClickListener {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentHomeBinding
    private var posts: ArrayList<Img>? = null

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var userReference: DatabaseReference
    private lateinit var postReference: DatabaseReference

    private var userID: String? = null

    private val imgViewModel: ImgViewModel by viewModels()
    private lateinit var homeAdapter: HomeAdapter
    private lateinit var gridManager: GridLayoutManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
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

        binding.ivMessage.setOnClickListener(View.OnClickListener {
            Navigation.findNavController(view).navigate(R.id.homeToChat)
        })

        gridManager = GridLayoutManager(requireContext(), 3)
        binding.homeRecycler.setLayoutManager(gridManager)
        homeAdapter = HomeAdapter(requireContext(), this)
        binding.homeRecycler.setAdapter(homeAdapter)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseUser = firebaseAuth.currentUser!!
        firebaseDatabase = FirebaseDatabase.getInstance()
        userReference = firebaseDatabase.getReference("Users")
        postReference = firebaseDatabase.getReference("Posts")
        userID = firebaseUser.getUid()

        val sp: SharedPreferences = requireContext().getSharedPreferences("SP_USER", Context.MODE_PRIVATE)
        val editor = sp.edit()
        editor.putString("Current_USERID", userID)
        editor.apply()

        getToken()
    }

    override fun onStart() {
        super.onStart()
        checkUserStatus()
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
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

    private fun checkUserStatus() {
        val user = firebaseAuth.currentUser
        if (user != null) {
        } else {
            /*startActivity(Intent(this@HomeActivity, WelcomeActivity::class.java))
            finish()*/
        }
    }

    override fun onItemClick(position: Int) {
        TODO("Not yet implemented")
    }
}