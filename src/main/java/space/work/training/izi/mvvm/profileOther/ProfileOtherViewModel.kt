package space.work.training.izi.mvvm.profileOther

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import space.work.training.izi.model.Img
import space.work.training.izi.mvvm.profile.UserInfo
import javax.inject.Inject

@HiltViewModel
class ProfileOtherViewModel @Inject constructor(private var profileOtherFirebase: ProfileOtherFirebase) :
    ViewModel() {

    val profileUser = MutableLiveData<UserInfo>()

    fun setFriendId(id: String) {
        profileOtherFirebase.setFriendId(id)
    }

    fun load() {
        profileOtherFirebase.apply {
            showNumbers()
            showData()
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

    /* fun getProfileUserFlow() {
         viewModelScope.launch {
             profileOtherFirebase.getProfileUserFlow().collect {
                 profileUser.postValue(it)
             }
         }
    }*/

    /*  fun getProfileUserFlow(): Flow<UserInfo> {
          return profileOtherFirebase.getProfileUserFlow()
      }*/

   /* fun getProfileUserFlow2(): MutableStateFlow<UserInfo> {
        return profileOtherFirebase.getProfileUserFlow2()
    }*/

}