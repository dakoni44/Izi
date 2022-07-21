package space.work.training.izi.mvvm.profileOther

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import space.work.training.izi.mvvm.posts.Img
import space.work.training.izi.mvvm.profile.ProfileImg
import space.work.training.izi.mvvm.profile.UserInfo
import javax.inject.Inject

@HiltViewModel
class ProfileOtherViewModel @Inject constructor(private var profileOtherFirebase: ProfileOtherFirebase) :
    ViewModel() {

    private val _profileUser = MutableStateFlow(UserInfo())
    val profileUser: StateFlow<UserInfo> = _profileUser

    fun setFriendId(id: String) {
        profileOtherFirebase.setFriendId(id)
    }

    fun load() {
        profileOtherFirebase.showData()
        profileOtherFirebase.showNumbers()
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
        return profileOtherFirebase.getCurrentState()
    }

    /*  @JvmName("getProfileUser1")
      fun getProfileUser(): StateFlow<UserInfo> {
          viewModelScope.launch {
            profileOtherFirebase.getProfileUser().collect {
                _profileUser.value = it
            }
        }
    }*/

    /*
     fun getProfileUser(): StateFlow<UserInfo> {
       return profileOtherFirebase.getProfileUser()
    }*/

    val pUser: Flow<UserInfo> = flow {
        emit(profileOtherFirebase.getProfileUser())
        delay(500)
    }


    fun getImgs(): StateFlow<List<Img>> {
        return profileOtherFirebase.getImgs()
    }
}