package space.work.training.izi.di

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {

    //get from firebase, write changes in room, read data from room
    //adding links images videos in chat
    //adding some representation to profiles that are hidden
    //add back buttons to fragments
    //mvvvm, coroutines, firebase, hilt, flow, room
    //every class is mvvm and coroutines for room and background, add flow instead of livedata
    //flow, coroutines delay, live data outside viewModel
    //request poster reaction
    //addgroup, comment, editprofile, groupchat(room)
}