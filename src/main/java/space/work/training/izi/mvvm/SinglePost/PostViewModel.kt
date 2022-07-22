package space.work.training.izi.mvvm.SinglePost

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import space.work.training.izi.mvvm.posts.Img
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(private var postFirebase: PostFirebase) : ViewModel() {

    fun load() {
        postFirebase.load()
    }

    fun getLike(): LiveData<Boolean> {
        return postFirebase.isLike()
    }

    fun getDislike(): LiveData<Boolean> {
        return postFirebase.isDislike()
    }

    fun getNrLikes(): LiveData<String> {
         return postFirebase.getNrLikes()
     }

    fun getNrDislikes(): LiveData<String> {
        return postFirebase.getNrDislikes()
    }

    fun getMyImg(): LiveData<Boolean> {
        return postFirebase.isMyImg()
    }

    fun getViews(): LiveData<List<String>> {
        return postFirebase.getViews()
    }

    fun getLikes(): LiveData<List<String>> {
        return postFirebase.getLikes()
    }

    fun getDislikes(): LiveData<List<String>> {
        return postFirebase.getDislikes()
    }

    fun getImg(): LiveData<Img> {
        return postFirebase.getImg()
    }

    fun setImgID(id: String) {
        postFirebase.setImgId(id)
    }
}