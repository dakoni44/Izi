package com.social.world.tracy.mvvm.kotlin

import android.content.Context
import androidx.lifecycle.LiveData
import com.social.world.tracy.mvvm.homePost.Post

class ImgRoom(context: Context) {
    var imgDao: ImgDao? = null
    var allImgs: LiveData<List<Img>>? = null

    init {
        imgDao = ImgDatabase.getDatabase(context).imgDao()
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