package space.work.training.izi.nav_fragments

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.AndroidEntryPoint
import space.work.training.izi.R
import space.work.training.izi.databinding.FragmentSignUpBinding
import space.work.training.izi.mvvm.chatList.User

@AndroidEntryPoint
class SignUpFragment : Fragment() {

    private lateinit var binding: FragmentSignUpBinding

    private var mAuth: FirebaseAuth? = null
    private var db: FirebaseDatabase? = null
    private var userRef: DatabaseReference? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_sign_up, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mAuth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance()
        userRef = db!!.getReference("Users")

        binding.bRegister.setOnClickListener(View.OnClickListener {
            val email: String = binding.etRegisterEmail.text.toString().trim { it <= ' ' }
            val password: String =
                binding.etRegisterPassword.text.toString().trim { it <= ' ' }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.etRegisterEmail.error = "Invalid Email"
                binding.etRegisterEmail.isFocusable = true
            } else if (password.length < 6) {
                binding.etRegisterPassword.error = "At least 6 characters"
                binding.etRegisterPassword.isFocusable = true
            } else {
                registerUser(email, password)
            }
        })

        binding.tvLogin.setOnClickListener(View.OnClickListener {
            findNavController().navigate(R.id.signUpToLogIn)
        })
    }

    private fun registerUser(email: String, password: String) {
        binding.pbRegister.visibility = View.VISIBLE
        mAuth!!.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    val firebaseUser = mAuth!!.currentUser
                    val userId = firebaseUser!!.uid
                    userRef = db!!.reference.child("Users").child(userId)
                    val users = User()
                    users.uid = FirebaseAuth.getInstance().currentUser!!.uid
                    users.email = binding.etRegisterEmail.text.toString().trim { it <= ' ' }
                    users.name = ""
                    users.username = ""
                    users.image = ""
                    users.bio = ""
                    userRef!!.setValue(users)
                        .addOnCompleteListener {
                            binding.pbRegister.visibility = View.INVISIBLE
                            findNavController().navigate(R.id.signUpToProfile)
                            Toast.makeText(
                                requireContext(),
                                "User registered",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }.addOnFailureListener { e ->
                            Toast.makeText(
                                requireContext(),
                                "Failed " + e.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                } else {
                    binding.pbRegister.visibility = View.INVISIBLE
                    Toast.makeText(
                        requireContext(), "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }.addOnFailureListener(OnFailureListener { e ->
                binding.pbRegister.visibility = View.INVISIBLE
                Toast.makeText(requireContext(), "" + e.message, Toast.LENGTH_SHORT).show()
            })
    }

}