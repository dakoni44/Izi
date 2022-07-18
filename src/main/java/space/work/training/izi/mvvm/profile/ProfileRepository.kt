package space.work.training.izi.mvvm.profile

import androidx.lifecycle.LiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import space.work.training.izi.mvvm.chatList.User
import javax.inject.Inject


class ProfileRepository @Inject constructor(
    private var profileDao: ProfileDao,
    private var profileFirebase: ProfileFirebase
) {

    fun load(){
        profileFirebase.showData()
    }

    fun getProfileImgs(): LiveData<List<ProfileImg>> {
        return profileDao.getAllImgs()
    }

    fun getUserInfo(): LiveData<UserInfo> {
        return profileDao.getUserInfo()
    }

}
