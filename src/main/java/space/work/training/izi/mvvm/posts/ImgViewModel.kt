package space.work.training.izi.mvvm.posts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ImgViewModel @Inject constructor(private val repository: ImgRepository) : ViewModel() {

     init {
         repository.readPosts()
         repository.readNewPosts()
     }

    fun getImgs(): LiveData<List<Img>> {
        return repository.getAllImgs()
    }

    fun getNewImgs(): MutableLiveData<List<Img>> {
        return repository.readNewPosts()
    }
}