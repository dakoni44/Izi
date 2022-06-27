package com.social.world.tracy.mvvm.kotlin

import androidx.lifecycle.LiveData
import space.work.training.izi.mvvm.ImgDao
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImgRepository @Inject constructor(
    private val imgFirebase: ImgFirebase,
    private val imgDao: ImgDao
) {

    fun notifyFirebaseDataChange(list: List<Img>) {
        imgDao.deleteAllImgs()
        for (img in list) {
            imgDao.insert(img)
        }
    }

    fun getAllImgs(): LiveData<List<Img>> {
        var list: List<Img>
        list = imgFirebase.getAllImgs()!!
        if (list.isNullOrEmpty()) {
        } else {
            notifyFirebaseDataChange(list)
        }
        return imgDao.getAllImgs()
    }

    suspend fun insertImg(img: Img) = imgDao.insert(img)

    fun addImg(img: Img) {
        //imgFirebase.addImg(img);
    }
}