package space.work.training.izi.mvvm

import androidx.lifecycle.LiveData
import kotlinx.coroutines.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext

@Singleton
class ImgRepository @Inject constructor(
    private val imgFirebase: ImgFirebase,
    private val imgDao: ImgDao
) :CoroutineScope{
    private lateinit var job: Job

    override val coroutineContext:CoroutineContext
    get() = job+Dispatchers.IO

    suspend fun notifyFirebaseDataChange(list: List<Img>) {
        imgDao.deleteAllImgs()
        for (img in list) {
            imgDao.insert(img)
        }
    }

      fun getAllImgs(): LiveData<List<Img>> {
        var list = imgFirebase.getAllImgs()
        if (!list.isNullOrEmpty()) {
           launch {
               notifyFirebaseDataChange(list)
           }
        }
        return imgDao.getAllImgs()
    }

    suspend fun insertImg(img: Img) = imgDao.insert(img)

    fun addImg(img: Img) {
        //imgFirebase.addImg(img);
    }
}