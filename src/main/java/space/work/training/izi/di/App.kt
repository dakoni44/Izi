package space.work.training.izi.di

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {

    //  reading users from firebase directly, no offline ( room ) read and
    //write option, maybe bcs of hilt, idea was get from firebase save in base than read from base,
    // now we need to save in room somehow
    //adding links images videos in chat
    //adding some representation to profiles that are hidden
    //add back buttons to fragments
    //mvvvm, coroutines, firebase, hilt, flow, room
}