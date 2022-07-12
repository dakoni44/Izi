package space.work.training.izi.mvvm.posts

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ImgViewModel @Inject constructor(private val repository: ImgRepository) : ViewModel() {

    var online: MutableLiveData<List<Img>> = MutableLiveData()
    var onlineNew: MutableLiveData<List<Img>> = MutableLiveData()

    fun load() {
        online = repository.readPosts()
        onlineNew = repository.readNewPosts()
    }

    fun getImgs(): MutableLiveData<List<Img>> {
        return online
    }

    fun getNewImgs(): MutableLiveData<List<Img>> {
        return onlineNew
    }
}