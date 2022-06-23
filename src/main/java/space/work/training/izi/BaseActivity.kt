package space.work.training.izi

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class BaseActivity :AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setContentView(R.layout.activity_base)

        val bottomNavigationView=findViewById<BottomNavigationView>(R.id.bottomNavigation)
        val navController=findNavController(R.id.baseActivity)

        bottomNavigationView.setupWithNavController(navController)
    }
}