package space.work.training.izi.mvvm.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatListViewModel @Inject constructor(private val repository: ChatListRepository) :
    ViewModel() {

    private var online: MutableLiveData<List<User>> = MutableLiveData()

    fun load(){
        online=repository.loadChatList()
    }

    fun getUsers(): MutableLiveData<List<User>> {
        return online
    }

}