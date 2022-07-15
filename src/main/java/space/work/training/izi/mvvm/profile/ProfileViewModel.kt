package space.work.training.izi.mvvm.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(private val repository: ProfileRepository) :
    ViewModel() {

    init {
        repository.showData()
    }

    fun getUserInfo(): LiveData<UserInfo> {
        return repository.getUserInfo()
    }

}