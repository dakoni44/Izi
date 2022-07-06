package space.work.training.izi.nav_fragments

import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import space.work.training.izi.CropperActivity
import space.work.training.izi.EditTextLinesLimiter
import space.work.training.izi.R
import space.work.training.izi.databinding.FragmentEditProfileBinding
import space.work.training.izi.mvvm.chat.User
import java.util.*

class EditProfileFragment : Fragment() {

    private lateinit var binding: FragmentEditProfileBinding

    private var firebaseAuth: FirebaseAuth? = null
    private var user: FirebaseUser? = null
    private var firebaseDatabase: FirebaseDatabase? = null
    private var databaseReference: DatabaseReference? = null

    private var imageUri: Uri? = null
    private var myUrl = ""
    private var uploadTask: UploadTask? = null
    private var storageReference: StorageReference? = null

    private var userID: String? = null

    lateinit var mGetContent: ActivityResultLauncher<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_edit_profile, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.etEditBio.addTextChangedListener(EditTextLinesLimiter(binding.etEditBio, 10))

        firebaseAuth = FirebaseAuth.getInstance()
        user = firebaseAuth!!.currentUser
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase!!.getReference("Users")
        userID = user!!.uid
        storageReference = FirebaseStorage.getInstance().getReference("profileImages")

        mGetContent = registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) {
            val intent = Intent(activity, CropperActivity::class.java).apply {
                putExtra("DATA", it.toString())
                startActivityForResult(this, 101)
            }
        }

        binding.ivChangePic.setOnClickListener {
            mGetContent.launch("image/*")
        }

        binding.bEditProfile.setOnClickListener {
            updateText()
        }
    }

    private fun showData() {
        databaseReference!!.child(userID!!).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = User()
                user.uid = userID as String
                user.name = dataSnapshot.child("name").getValue(String::class.java).toString()
                user.username =
                    dataSnapshot.child("username").getValue(String::class.java).toString()
                user.email = dataSnapshot.child("email").getValue(String::class.java).toString()
                user.image = dataSnapshot.child("image").getValue(String::class.java).toString()
                user.bio = dataSnapshot.child("bio").getValue(String::class.java).toString()
                if (user.uid.equals(userID)) {
                    binding.etEditName.setText(user.name)
                    binding.etEditUsername.setText(user.username)
                    binding.etEditBio.setText(user.bio)
                    Glide.with(requireActivity()).load(user.image).into(binding.editedPic)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101 && resultCode == -1) {
            val result = data!!.getStringExtra("RESULT")
            result?.let {
                imageUri = Uri.parse(it)
            }
            binding.editedPic.setImageURI(imageUri)
        }
    }

    private fun getFileExtension(uri: Uri): String? {
        val contentResolver: ContentResolver = requireContext().contentResolver
        val mime = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(contentResolver.getType(uri))
    }

    private fun uploadImage() {
        imageUri?.let {
            val filereference = storageReference!!.child(
                System.currentTimeMillis().toString() +
                        "." + getFileExtension(it)
            )
            uploadTask = filereference.putFile(it)
            uploadTask!!.continueWithTask { task: Task<*> ->
                if (!task.isComplete) {
                    throw task.exception!!
                }
                filereference.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri: Uri = task.result
                    myUrl = downloadUri.toString()

                    val reference = FirebaseDatabase.getInstance().getReference("Users")

                    reference.child(userID!!).child("image").setValue(myUrl)
                }
            }.addOnFailureListener { }
        }
    }

    private fun updateText() {
        val reference = FirebaseDatabase.getInstance().getReference("Users")
        reference.child(userID!!).child("name")
            .setValue(binding.etEditName.text.toString().trim { it <= ' ' })
        reference.child(userID!!).child("username")
            .setValue(binding.etEditUsername.text.toString().trim { it <= ' ' }
                .lowercase(Locale.getDefault()))
        reference.child(userID!!).child("bio")
            .setValue(binding.etEditBio.text.toString().trim { it <= ' ' })
        findNavController().navigate(R.id.editProfileToProfile)
    }

    override fun onResume() {
        super.onResume()
        uploadImage()
        showData()
    }

}