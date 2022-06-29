package space.work.training.izi.nav_fragments

import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.yalantis.ucrop.UCrop
import space.work.training.izi.CropperActivity
import space.work.training.izi.R
import space.work.training.izi.databinding.FragmentAddPostBinding


class AddPostFragment : Fragment() {

    private lateinit var binding : FragmentAddPostBinding

    var firebaseAuth: FirebaseAuth? = null
    var user: FirebaseUser? = null
    var firebaseDatabase: FirebaseDatabase? = null
    var databaseReference: DatabaseReference? = null

    var imageUri: Uri? = null
    var myUrl = ""
    var storageReference: StorageReference? = null

    lateinit var mGetContent : ActivityResultLauncher<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_post, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()
        user = firebaseAuth!!.currentUser
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase!!.getReference("Users")
        storageReference = FirebaseStorage.getInstance().getReference("posts")

        binding.bAddPost.setOnClickListener(View.OnClickListener { uploadImage() })

        mGetContent.launch("image/*")
        mGetContent=registerForActivityResult(ActivityResultContracts.GetContent(),
            ActivityResultCallback {
                val intent = Intent(activity, CropperActivity::class.java).apply {
                    putExtra("DATA",it.toString())
                    startActivityForResult(this,101)
                }
            })
    }



    private fun getFileExtension(uri: Uri): String? {
        val contentResolver: ContentResolver = requireContext().contentResolver
        val mime = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(contentResolver.getType(uri))
    }

     override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101 && resultCode==1) {
            var result=data!!.getStringExtra("RESULT")
            var resultUri : Uri? = null
            result?.let {
                resultUri=Uri.parse(it)
            }
            binding.ivAddPost.setImageURI(resultUri)
        }
    }

    private fun uploadImage() {
       imageUri?.let {
           val filereference = storageReference!!.child(
               System.currentTimeMillis().toString() +
                       "." + getFileExtension(it)
           )
           var uploadTask = filereference.putFile(it)
           uploadTask.continueWithTask { task: Task<*> ->
               if (!task.isComplete) {
                   throw task.exception!!
               }
               filereference.downloadUrl
           }.addOnCompleteListener(OnCompleteListener<Uri> { task ->
               if (task.isSuccessful) {
                   val downloadUri = task.result
                   myUrl = downloadUri.toString()
                   val reference = FirebaseDatabase.getInstance().getReference("Posts")
                   val postid = reference.push().key
                   val hashMap = HashMap<String, Any?>()
                   val views = HashMap<Any, Any>()
                   hashMap["postid"] = postid
                   hashMap["postimage"] = myUrl
                   hashMap["description"] =
                       binding.etAddDescription.text.toString().trim { it <= ' ' }
                   hashMap["publisher"] = user!!.uid
                   views[user!!.uid] = user!!.uid
                   hashMap["views"] = views
                   reference.child((postid)!!).setValue(hashMap)
                   findNavController().navigate(R.id.addPostToProfile)
               }
           }).addOnFailureListener(OnFailureListener { })
       }
    }
}