package space.work.training.izi.nav_fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import space.work.training.izi.R

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashScreenFragment : Fragment() {

    private var mAuth: FirebaseAuth? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_splash_screen, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        println("Splash created")

        mAuth = FirebaseAuth.getInstance()
        val firebaseUser = mAuth!!.currentUser
        Handler(Looper.myLooper()!!).postDelayed({
            if (firebaseUser == null) {
                findNavController().navigate(R.id.splashToLogin)
            } else {
                findNavController().navigate(R.id.splashToHome)
            }
            val sp: SharedPreferences =
                requireContext().getSharedPreferences("SP_USER", Context.MODE_PRIVATE)
            val editor = sp.edit()
            editor.putString("Current_USERID", firebaseUser?.uid)
            editor.apply()
        }, 1500)
    }
}


