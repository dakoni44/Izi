package space.work.training.izi

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.yalantis.ucrop.UCrop
import java.io.File
import java.util.*

class CropperActivity : AppCompatActivity() {

    var result :String = ""
    lateinit var fileUri : Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent=intent
        result=intent.getStringExtra("DATA").toString()
        fileUri=Uri.parse(result)

        val dest_uri=StringBuilder(UUID.randomUUID().toString()).append("jpg").toString()

        val options = UCrop.Options()
        UCrop.of(fileUri,Uri.fromFile(File(cacheDir,dest_uri)))
            .withOptions(options)
            .withAspectRatio(16F,9F)
            .useSourceImageAspectRatio()
            .withMaxResultSize(2000,2000)
            .start(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode== RESULT_OK && requestCode==UCrop.REQUEST_CROP){
            var resultUri=UCrop.getOutput(data!!)
            var returnIntent=Intent().apply {
                putExtra("RESULT", resultUri)
                setResult(-1,this)
                finish()
            }

        }
    }
}