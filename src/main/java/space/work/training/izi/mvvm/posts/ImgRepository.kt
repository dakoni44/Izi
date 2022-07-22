package space.work.training.izi.mvvm.posts

import androidx.lifecycle.LiveData
import space.work.training.izi.model.Img
import space.work.training.izi.mvvm.posts.newImgs.ImgNew
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImgRepository @Inject constructor(
    private var imgDao: ImgDao,
    private var imgFirebase: ImgFirebase
) {

    fun load() {
        imgFirebase.load()
    }

    fun getAllImgs(): LiveData<List<Img>> {
        return imgDao.getAllImgs()
    }

    fun getAllImgsNew(): LiveData<List<ImgNew>> {
        return imgDao.getAllImgsNew()
    }

}