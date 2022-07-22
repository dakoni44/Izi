package space.work.training.izi.mvvm.profile

import androidx.lifecycle.LiveData
import javax.inject.Inject


class ProfileRepository @Inject constructor(
    private var profileDao: ProfileDao,
    private var profileFirebase: ProfileFirebase
) {

    fun load(){
        profileFirebase.showData()
    }

    fun getUserInfo(id:String): LiveData<UserInfo> {
        return profileDao.getUserInfo(id)
    }

}
