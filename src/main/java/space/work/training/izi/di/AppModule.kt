package space.work.training.izi.di

import android.content.Context
import androidx.room.Room
import com.social.world.tracy.mvvm.kotlin.ImgDao
import com.social.world.tracy.mvvm.kotlin.ImgDatabase
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
    fun provideApplication(@ApplicationContext app: Context): App {
        return app as App
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