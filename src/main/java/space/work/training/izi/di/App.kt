package space.work.training.izi.di

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {

    //  reading users from firebase directly, no offline ( room ) read and
    //write option, maybe bcs of hilt, idea was get from firebase save in base than read from base,
    // now we need to save in room somehow
    //get in list from profiles and homeList, on 24hours home item we get to postfragment individual
    //adding links images videos in chat, add group chat functions
    //adding some representation to profiles that are hidden
    //add user can see who saw his post and who liked and disliked, nobody can see that on my profile except me
    //comments are only visible to 1to1 person
    //delete group, add and remove members
    //mvvvm, coroutines, firebase, hilt, flow, room
}