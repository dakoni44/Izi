package space.work.training.izi

import android.os.Bundle
import android.text.InputType
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import space.work.training.izi.databinding.FragmentLoginBinding

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class LogInFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentLoginBinding

    private var mAuth: FirebaseAuth? = null
    var db: FirebaseDatabase? = null
    var userRef: DatabaseReference? = null

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
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.bLogin.setOnClickListener(View.OnClickListener {
            val email: String = binding.etLoginEmail.getText().toString().trim { it <= ' ' }
            val password: String = binding.etLoginPassword.getText().toString().trim { it <= ' ' }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.etLoginEmail.setError("Invalid Email")
                binding.etLoginEmail.setFocusable(true)
            } else if (password.length < 6) {
                binding.etLoginPassword.setError("At least 6 characters")
                binding.etLoginPassword.setFocusable(true)
            } else {
                loginUser(email, password)
            }
        })

        binding.tvRegister.setOnClickListener(View.OnClickListener {
            findNavController().navigate(R.id.logInToSignUp)
        })

        binding.tvForgot.setOnClickListener(View.OnClickListener { showRecoverPasswordDialog() })
    }

    private fun showRecoverPasswordDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Recover Password")
        val linearLayout = LinearLayout(requireContext())
        val etEmail = EditText(requireContext())
        etEmail.hint = "Email"
        etEmail.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        etEmail.minEms = 16
        linearLayout.addView(etEmail)
        linearLayout.setPadding(10, 10, 10, 10)
        builder.setView(linearLayout)
        builder.setPositiveButton(
            "Recover"
        ) { dialog, which ->
            val email = etEmail.text.toString().trim { it <= ' ' }
            beginRecovery(email)
        }
        builder.setNegativeButton(
            "Cancel"
        ) { dialog, which -> dialog.dismiss() }
        builder.create().show()
    }

    private fun beginRecovery(email: String) {
        mAuth!!.sendPasswordResetEmail(email).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(requireContext(), "Email sent", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Failed...", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { e ->
            Toast.makeText(
                requireContext(),
                "" + e.message,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun loginUser(email: String, password: String) {
        binding.pbLogin.setVisibility(View.VISIBLE)
        mAuth!!.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity())
            { task ->
                if (task.isSuccessful) {
                    FirebaseDatabase.getInstance().getReference("Users")
                        .child(FirebaseAuth.getInstance().currentUser!!.uid)
                        .addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                findNavController().navigate(R.id.homeToChat)
                                binding.pbLogin.setVisibility(View.INVISIBLE)
                            }

                            override fun onCancelled(databaseError: DatabaseError) {}
                        })
                } else {
                    binding.pbLogin.setVisibility(View.INVISIBLE)
                    Toast.makeText(
                        requireContext(), "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }.addOnFailureListener(OnFailureListener {
                binding.pbLogin.setVisibility(View.INVISIBLE)
                Toast.makeText(requireContext(), "" + it.message, Toast.LENGTH_SHORT).show()
            })

    }

    override fun onStart() {
        super.onStart()
        val firebaseUser = mAuth!!.currentUser
        if (firebaseUser == null) {
        } else {
            findNavController().navigate(R.id.logInToHome)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LogInFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}