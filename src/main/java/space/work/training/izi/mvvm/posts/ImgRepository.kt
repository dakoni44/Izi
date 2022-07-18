package space.work.training.izi.mvvm.posts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import space.work.training.izi.mvvm.posts.newImgs.ImgNew
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImgRepository @Inject constructor(
    private var imgDao: ImgDao,
    private var imgFirebase: ImgFirebase
) {

    fun load() {
        imgFirebase.load()
    }

    fun getAllImgs(): LiveData<List<Img>> {
        return imgDao.getAllImgs()
    }

    fun getAllImgsNew(): LiveData<List<ImgNew>> {
        return imgDao.getAllImgsNew()
    }

}