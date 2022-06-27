package space.work.training.izi.mvvm

import androidx.lifecycle.LiveData
import com.social.world.tracy.mvvm.kotlin.Img

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

    suspend fun deletePosts() {
        imgDao?.deleteAllImgs()
    }
}