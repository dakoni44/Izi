package space.work.training.izi.mvvm.posts

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import space.work.training.izi.mvvm.posts.newImgs.ImgNew
import javax.inject.Inject

@HiltViewModel
class ImgViewModel @Inject constructor(private val repository: ImgRepository) : ViewModel() {

    init {
        repository.load()
    }

    fun getImgs(id: String): LiveData<ImgHome> {
        return repository.getAllImgs(id)
    }

    fun getNewImgs(id: String): LiveData<ImgNew> {
        return repository.getAllImgsNew(id)
    }
}