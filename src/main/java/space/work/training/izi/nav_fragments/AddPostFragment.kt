package space.work.training.izi.nav_fragments

import android.content.ContentResolver
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.tasks.Task
import com.google.android.material.transition.MaterialContainerTransform
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.AndroidEntryPoint
import space.work.training.izi.CropperActivity
import space.work.training.izi.R
import space.work.training.izi.ViewUtils
import space.work.training.izi.databinding.FragmentAddPostBinding

@AndroidEntryPoint
class AddPostFragment : Fragment() {

    private lateinit var binding: FragmentAddPostBinding

    private var firebaseAuth: FirebaseAuth? = null
    private var user: FirebaseUser? = null
    private var firebaseDatabase: FirebaseDatabase? = null
    private var databaseReference: DatabaseReference? = null

    private var imageUri: Uri? = null
    private var myUrl = ""
    private var storageReference: StorageReference? = null

    private var desc: String=""

    lateinit var mGetContent: ActivityResultLauncher<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_post, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()
        user = firebaseAuth!!.currentUser
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase!!.getReference("Users")
        storageReference = FirebaseStorage.getInstance().getReference("posts")

        binding.bAddPost.setOnClickListener { uploadImage() }

        mGetContent = registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) {
            val intent = Intent(activity, CropperActivity::class.java).apply {
                putExtra("DATA", it.toString())
                startActivityForResult(this, 101)
            }
        }
        mGetContent.launch("image/*")

        binding.tvAddDescription.setOnClickListener {
            ViewUtils.expandOrCollapse(binding.rl,"expand")
            binding.tvAddDescription.visibility = View.GONE
        }

        binding.descDown.setOnClickListener {
            ViewUtils.expandOrCollapse(binding.rl,"collapse")
            binding.tvAddDescription.visibility = View.VISIBLE
            desc = binding.etAddDesc.text.toString().trim()
        }
    }

    private fun getFileExtension(uri: Uri): String? {
        val contentResolver: ContentResolver = requireContext().contentResolver
        val mime = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(contentResolver.getType(uri))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101 && resultCode == -1) {
            val result = data!!.getStringExtra("RESULT")
            result?.let {
                imageUri = Uri.parse(it)
            }
            binding.ivAddPost.setImageURI(imageUri)
        }
    }

    private fun uploadImage() {
        imageUri?.let {
            val filereference = storageReference!!.child(
                System.currentTimeMillis().toString() +
                        "." + getFileExtension(it)
            )
            val uploadTask = filereference.putFile(it)
            uploadTask.continueWithTask { task: Task<*> ->
                if (!task.isComplete) {
                    throw task.exception!!
                }
                filereference.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    myUrl = downloadUri.toString()
                    val reference = FirebaseDatabase.getInstance().getReference("Posts")
                    val postid = reference.push().key
                    val hashMap = HashMap<String, Any?>()
                    val views = HashMap<Any, Any>()
                    hashMap["postid"] = postid
                    hashMap["postimage"] = myUrl
                    hashMap["description"] = desc
                    hashMap["publisher"] = user!!.uid
                    views[user!!.uid] = user!!.uid
                    hashMap["views"] = views
                    reference.child((postid)!!).setValue(hashMap)
                }
            }.addOnFailureListener { }
        }
        findNavController().navigate(R.id.addPostToProfile)
    }
}