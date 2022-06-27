package com.social.world.tracy.mvvm.kotlin

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


class ImgViewModel constructor(private val repository: ImgRepository) : ViewModel() {
    fun getAllImgs(img: Img): LiveData<List<Img>>? {
        return repository.getAllImgs()
    }
}