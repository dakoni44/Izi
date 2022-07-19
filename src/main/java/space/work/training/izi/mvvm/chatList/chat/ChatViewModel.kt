package space.work.training.izi.mvvm.chatList.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import space.work.training.izi.model.Chat
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(private var chatRepository: ChatRepository) : ViewModel() {

    fun load(){
        chatRepository.load()
    }

    fun getUserChat(id:String): LiveData<ChatUser> {
        return chatRepository.getUserChat(id)
    }

    fun setReceiverId(id: String) {
        chatRepository.setReceiverId(id)
    }

    fun sendMessage(message: String) {
        chatRepository.sendMessage(message)
    }
}