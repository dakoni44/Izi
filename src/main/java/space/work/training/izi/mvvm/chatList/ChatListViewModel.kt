package space.work.training.izi.mvvm.chatList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChatListViewModel @Inject constructor(private val repository: ChatListRepository) :
    ViewModel() {

    init {
        repository.load()
    }

    fun getUsers(): LiveData<List<User>> {
        return repository.getAllUsers()
    }

}