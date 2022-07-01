package space.work.training.izi.mvvm.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChatListViewModel @Inject constructor(private val repository: ChatListRepository) :
    ViewModel() {

    fun getAllUsers(): LiveData<List<User>> {
        return repository.getAllUsers()
    }
}