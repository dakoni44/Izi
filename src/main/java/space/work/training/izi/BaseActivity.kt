package space.work.training.izi

import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BaseActivity :AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setContentView(R.layout.activity_base)
        println("BaseActivity created")

        val bottomNavigationView=findViewById<BottomNavigationView>(R.id.bottomNavigation)
        val navController=Navigation.findNavController(this,R.id.navHostFragment)

        bottomNavigationView.setupWithNavController(navController)
    }
}