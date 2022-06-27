package space.work.training.izi.mvvm

import androidx.lifecycle.LiveData
import com.social.world.tracy.mvvm.kotlin.Img
import com.social.world.tracy.mvvm.kotlin.ImgDao
import javax.inject.Inject

class ImgRoom constructor() {
    var imgDao: ImgDao? = null
    var allImgs: LiveData<List<Img>>? = null

    init {
        allImgs = imgDao?.getAllImgs()
    }

    @JvmName("getAllImgs1")
    fun getAllImgs(): LiveData<List<Img>>? {
        return allImgs
    }

    suspend fun addImg(img: Img) {
        imgDao?.insert(img)
    }

    fun deletePosts() {
        imgDao?.deleteAllImgs()
    }
}