package space.work.training.izi

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth

class SplashScreenFragment : Fragment() {

    private var mAuth: FirebaseAuth? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view= inflater.inflate(R.layout.fragment_splash_screen, container, false)
        mAuth = FirebaseAuth.getInstance()
        val firebaseUser = mAuth!!.currentUser
        Handler(Looper.myLooper()!!).postDelayed({
            if (firebaseUser == null) {
                findNavController().navigate(R.id.splashToLogin)
            } else {
                findNavController().navigate(R.id.splashToHome)
            }

        },2000)
        return view
    }

}


