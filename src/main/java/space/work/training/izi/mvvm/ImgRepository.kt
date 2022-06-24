package com.social.world.tracy.mvvm.kotlin

import androidx.lifecycle.LiveData
import space.work.training.izi.mvvm.ImgRoom
import javax.inject.Inject

class ImgRepository @Inject constructor(val imgFirebase: ImgFirebase, val imgRoom: ImgRoom) {

    var imgs: ArrayList<Img>? = null

    suspend fun notifyFirebaseDataChange() {
        imgRoom.deletePosts()
        imgs = ArrayList<Img>()
        imgs?.let {
            imgFirebase.getAllImgs()
            for (img in it) {
                imgRoom.addImg(img)
            }
        }
    }

    fun getAllImgs(): LiveData<List<Img>>? {
        return imgRoom.getAllImgs()
    }

    fun addImg(img: Img) {
        // remoteDatabase.addPost(post);
    }
}