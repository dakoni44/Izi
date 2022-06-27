package com.social.world.tracy.mvvm.kotlin

import androidx.lifecycle.LiveData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImgRepository @Inject constructor(
    private val imgFirebase: ImgFirebase,
    private val imgDao: ImgDao
) {

    var imgs: ArrayList<Img>? = null

    suspend fun notifyFirebaseDataChange() {
        imgDao.deleteAllImgs()
        imgs = ArrayList<Img>()
        imgs?.let {
            imgFirebase.getAllImgs()
            for (img in it) {
                imgDao.insert(img)
            }
        }
    }

    fun getAllImgs(): LiveData<List<Img>> {
        return imgDao.getAllImgs()
    }

    suspend fun insertImg(img: Img) = imgDao.insert(img)

    fun addImg(img: Img) {
        //imgFirebase.addImg(img);
    }
}