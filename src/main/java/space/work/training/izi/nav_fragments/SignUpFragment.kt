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
import space.work.training.izi.R
import space.work.training.izi.databinding.FragmentSignUpBinding
import space.work.training.izi.model.Users

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class SignUpFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentSignUpBinding

    private var mAuth: FirebaseAuth? = null
    private var db: FirebaseDatabase? = null
    private var userRef: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        mAuth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance()
        userRef = db!!.getReference("Users")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_sign_up, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.bRegister.setOnClickListener(View.OnClickListener {
            val email: String = binding.etRegisterEmail.getText().toString().trim { it <= ' ' }
            val password: String =
                binding.etRegisterPassword.getText().toString().trim { it <= ' ' }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.etRegisterEmail.setError("Invalid Email")
                binding.etRegisterEmail.setFocusable(true)
            } else if (password.length < 6) {
                binding.etRegisterPassword.setError("At least 6 characters")
                binding.etRegisterPassword.setFocusable(true)
            } else {
                registerUser(email, password)
            }
        })

        binding.tvLogin.setOnClickListener(View.OnClickListener {
            findNavController().navigate(R.id.signUpToLogIn)
        })
    }

    private fun registerUser(email: String, password: String) {
        binding.pbRegister.setVisibility(View.VISIBLE)
        mAuth!!.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    val firebaseUser = mAuth!!.currentUser
                    val userId = firebaseUser!!.uid
                    userRef = db!!.reference.child("Users").child(userId)
                    val users = Users()
                    users.uid = FirebaseAuth.getInstance().currentUser!!.uid
                    users.email = binding.etRegisterEmail.getText().toString().trim { it <= ' ' }
                    users.name = ""
                    users.username = ""
                    users.image = ""
                    users.bio = ""
                    userRef!!.setValue(users)
                        .addOnCompleteListener {
                            binding.pbRegister.setVisibility(View.INVISIBLE)
                            //findNavController().navigate(R.id.signtoprofile)
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
                    binding.pbRegister.setVisibility(View.INVISIBLE)
                    Toast.makeText(
                        requireContext(), "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }.addOnFailureListener(OnFailureListener { e ->
                binding.pbRegister.setVisibility(View.INVISIBLE)
                Toast.makeText(requireContext(), "" + e.message, Toast.LENGTH_SHORT).show()
            })
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SignUpFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}