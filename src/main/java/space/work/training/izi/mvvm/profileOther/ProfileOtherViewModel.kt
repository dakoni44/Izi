package space.work.training.izi.mvvm.profileOther

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import space.work.training.izi.mvvm.posts.Img
import space.work.training.izi.mvvm.profile.ProfileImg
import space.work.training.izi.mvvm.profile.UserInfo
import javax.inject.Inject

@HiltViewModel
class ProfileOtherViewModel @Inject constructor(private var profileOtherFirebase: ProfileOtherFirebase) :
    ViewModel() {

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

    fun getProfileUser(): StateFlow<UserInfo> {
        return profileOtherFirebase.getProfileUser()
    }

    fun getImgs(): StateFlow<List<ProfileImg>> {
        return profileOtherFirebase.getImgs()
    }
}