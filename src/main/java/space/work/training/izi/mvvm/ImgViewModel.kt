package com.social.world.tracy.mvvm.kotlin

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import space.work.training.izi.NetworkUtil
import space.work.training.izi.mvvm.Img
import space.work.training.izi.mvvm.ImgRepository
import javax.inject.Inject

@HiltViewModel
class ImgViewModel @Inject constructor(private val repository: ImgRepository) : ViewModel() {

    fun getAllImgs(): LiveData<List<Img>> {
            return repository.getAllImgs()
    }

 /*   fun saveArticle(article: Article) {
        viewModelScope.launch {
            newsRepository.insertArticle(article)
            articleEventChannel.send(ArticleEvent.ShowArticleSavedMessage("Article Saved."))
        }
    }*/
}