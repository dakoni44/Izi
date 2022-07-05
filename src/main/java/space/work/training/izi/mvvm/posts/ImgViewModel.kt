package space.work.training.izi.mvvm.posts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ImgViewModel @Inject constructor(private val repository: ImgRepository) : ViewModel() {

    var online: MutableLiveData<List<Img>>? = null

    fun getOnlineImgs(): MutableLiveData<List<Img>> {
        val onlineImgs = repository.getOnlineImgs()
        online = MutableLiveData<List<Img>>()
        if (!onlineImgs.isNullOrEmpty()) {
            online!!.postValue(onlineImgs)
            viewModelScope.launch {
                repository.notifyFirebaseDataChange(onlineImgs)
            }
        }

        return online as MutableLiveData<List<Img>>
    }

    fun getOfflineImgs(): LiveData<List<Img>> {
        return repository.getOfflineImgs()
    }

    /*   fun saveArticle(article: Article) {
           viewModelScope.launch {
               newsRepository.insertArticle(article)
               articleEventChannel.send(ArticleEvent.ShowArticleSavedMessage("Article Saved."))
           }
       }*/
}