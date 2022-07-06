package space.work.training.izi.nav_fragments

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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import dagger.hilt.android.AndroidEntryPoint
import space.work.training.izi.R
import space.work.training.izi.databinding.FragmentLoginBinding

@AndroidEntryPoint
class LogInFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding

    private var mAuth: FirebaseAuth? = null
    var db: FirebaseDatabase? = null
    var userRef: DatabaseReference? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mAuth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance()
        userRef = db!!.getReference("Users")

        binding.bLogin.setOnClickListener {
            val email: String = binding.etLoginEmail.text.toString().trim { it <= ' ' }
            val password: String = binding.etLoginPassword.text.toString().trim { it <= ' ' }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.etLoginEmail.error = "Invalid Email"
                binding.etLoginEmail.isFocusable = true
            } else if (password.length < 6) {
                binding.etLoginPassword.error = "At least 6 characters"
                binding.etLoginPassword.isFocusable = true
            } else {
                loginUser(email, password)
            }
        }

        binding.tvRegister.setOnClickListener {
            findNavController().navigate(R.id.logInToSignUp)
        }

        binding.tvForgot.setOnClickListener { showRecoverPasswordDialog() }
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
        binding.pbLogin.visibility = View.VISIBLE
        mAuth!!.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity())
            { task ->
                if (task.isSuccessful) {
                    FirebaseDatabase.getInstance().getReference("Users")
                        .child(FirebaseAuth.getInstance().currentUser!!.uid)
                        .addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                findNavController().navigate(R.id.logInToHome)
                                binding.pbLogin.visibility = View.INVISIBLE
                            }

                            override fun onCancelled(databaseError: DatabaseError) {}
                        })
                } else {
                    binding.pbLogin.visibility = View.INVISIBLE
                    Toast.makeText(
                        requireContext(), "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }.addOnFailureListener {
                binding.pbLogin.visibility = View.INVISIBLE
                Toast.makeText(requireContext(), "" + it.message, Toast.LENGTH_SHORT).show()
            }

    }

    override fun onStart() {
        super.onStart()
        val firebaseUser = mAuth!!.currentUser
        if (firebaseUser != null) {
            findNavController().navigate(R.id.logInToHome)
        }
    }

}