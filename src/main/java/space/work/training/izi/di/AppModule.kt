package space.work.training.izi.di

import android.content.Context
import androidx.room.Room
import space.work.training.izi.mvvm.ImgDao
import com.social.world.tracy.mvvm.kotlin.ImgDatabase
import com.social.world.tracy.mvvm.kotlin.ImgFirebase
import com.social.world.tracy.mvvm.kotlin.ImgRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideRepository(imgFirebase: ImgFirebase,imgDao: ImgDao): ImgRepository {
        return ImgRepository(imgFirebase,imgDao)
    }

    @Singleton
    @Provides
    fun provideImgDatabase(@ApplicationContext context: Context): ImgDatabase {
        return Room.databaseBuilder(
            context,
            ImgDatabase::class.java, "img_database"
        ).allowMainThreadQueries()
            .build()
    }

    @Provides
    fun provideChannelDao(imgDatabase: ImgDatabase): ImgDao {
        return imgDatabase.imgDao()
    }
}