package space.work.training.izi.mvvm.profileOther

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import space.work.training.izi.model.Img
import space.work.training.izi.mvvm.profile.UserInfo
import javax.inject.Inject

@HiltViewModel
class ProfileOtherViewModel @Inject constructor(private var profileOtherFirebase: ProfileOtherFirebase) :
    ViewModel() {

    fun setFriendId(id: String) {
        profileOtherFirebase.setFriendId(id)
    }

    fun load() {
        profileOtherFirebase.apply {
            showData()
            showNumbers()
            maintanceOfButtons()
            showPost()
        }
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

    fun getCurrState(): LiveData<String> {
        return profileOtherFirebase.getCurrentState()
    }

    fun getProfileUser(): LiveData<UserInfo> {
        return profileOtherFirebase.getProfileUser()
    }

    fun getImgs(): LiveData<ArrayList<Img>> {
        return profileOtherFirebase.getImgs()
    }

}