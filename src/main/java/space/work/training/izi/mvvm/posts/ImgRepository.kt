package space.work.training.izi.mvvm.posts

import androidx.lifecycle.LiveData
import kotlinx.coroutines.*
import space.work.training.izi.mvvm.chat.User
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext

@Singleton
class ImgRepository @Inject constructor(
    private val imgFirebase: ImgFirebase,
    private val imgDao: ImgDao
) {

    suspend fun notifyFirebaseDataChange(list: List<Img>) {
        imgDao.deleteAllImgs()
        for (img in list) {
            imgDao.insert(img)
        }
    }

    fun getOfflineImgs():LiveData<List<Img>>{
        return imgDao.getAllImgs()
    }

    fun getOnlineImgs(): List<Img> {
        return imgFirebase.getAllImgs()
    }

    suspend fun insertImg(img: Img) = imgDao.insert(img)

    fun addImg(img: Img) {
        //imgFirebase.addImg(img);
    }
}