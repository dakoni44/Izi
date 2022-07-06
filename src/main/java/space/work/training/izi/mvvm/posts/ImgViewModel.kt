package space.work.training.izi.mvvm.posts

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ImgViewModel @Inject constructor(private val repository: ImgRepository) : ViewModel() {

    var online: MutableLiveData<List<Img>> = MutableLiveData()

    fun load() {
        online = repository.readPosts()
    }

    fun getImgs(): MutableLiveData<List<Img>> {
        return online
    }
}