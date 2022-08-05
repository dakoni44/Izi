package space.work.training.izi.mvvm.chatList

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChatListViewModel @Inject constructor(private val repository: ChatListRepository) :
    ViewModel() {

    init {
        repository.load()
    }

    fun getUsers(id: String): LiveData<ChatListUsers> {
        return repository.getAllUsers(id)
    }

}