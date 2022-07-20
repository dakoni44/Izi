package space.work.training.izi.mvvm.SinglePost

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import space.work.training.izi.mvvm.posts.Img
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(private var postFirebase: PostFirebase) : ViewModel() {

    //private var dislikesLive = MutableLiveData<List<String>>()
    private val likesLiveData = MutableLiveData<String>()

    fun load() {
        postFirebase.load()
    }

    fun getLike(): LiveData<Boolean> {
        return postFirebase.isLike()
    }

    fun getDislike(): LiveData<Boolean> {
        //dislikesLiveData.value=postFirebase.isDislike
        return postFirebase.isDislike()
    }

    fun getNrLikes(owner: LifecycleOwner, observingBlock: (String) -> Unit): Observer<String> =
        Observer(observingBlock).also { likesLiveData.observe(owner, it) }
        //  Log.d("LiveLikes : ViewModel", likes.value.toString())
        //  return postFirebase.getNrLikes()


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