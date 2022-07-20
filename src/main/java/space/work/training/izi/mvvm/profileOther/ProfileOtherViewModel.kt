package space.work.training.izi.mvvm.profileOther

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import space.work.training.izi.mvvm.profile.UserInfo
import javax.inject.Inject

@HiltViewModel
class ProfileOtherViewModel @Inject constructor(private var profileOtherFirebase: ProfileOtherFirebase) :ViewModel(){

    private val _currentState = MutableStateFlow("not_friends")
    val currentState: StateFlow<String> = _currentState

    fun setFriendId(id: String) {
        profileOtherFirebase.setFriendId(id)
    }

    fun load() {
        profileOtherFirebase.showData()
        profileOtherFirebase.maintanceOfButtons()
        profileOtherFirebase.showPost()
    }

    fun send() {
        profileOtherFirebase.sendFriendRequest()
    }

    fun remove() {
        profileOtherFirebase.removeFriend()
    }

    fun accept() {
        profileOtherFirebase.acceptFriendRequest()
    }

    fun cancel() {
        profileOtherFirebase.cancelFriendRequest()
    }

    fun getCurrState(): StateFlow<String> {
        _currentState.value=profileOtherFirebase.getCurrentState()
        return currentState
    }
}