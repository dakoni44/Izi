package com.social.world.tracy.mvvm.kotlin

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import javax.inject.Inject

class ImgViewModel @Inject constructor(private val repository: ImgRepository) : ViewModel() {
    fun getAllImgs(img: Img): LiveData<List<Img>>? {
        return repository.getAllImgs()
    }
}