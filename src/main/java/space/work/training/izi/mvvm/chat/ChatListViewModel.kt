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

    var online: MutableLiveData<List<User>>? = null

    fun getOnlineUsers(): MutableLiveData<List<User>> {
        val onlineUsers = repository.getOnlineUsers()
        online = MutableLiveData<List<User>>()
        if(!onlineUsers.isNullOrEmpty()){
            online!!.postValue(onlineUsers)
            viewModelScope.launch {
                repository.notifyFirebaseDataChange(onlineUsers)
            }
        }
        return online as MutableLiveData<List<User>>
    }

    fun getOfflineUsers(): LiveData<List<User>> {
        return repository.getOfflineUsers()
    }
}