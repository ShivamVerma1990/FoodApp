package com.example.foodaap.utill

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.android.volley.toolbox.Volley
import com.example.foodaap.Module.SessionManager
import com.example.foodaap.R
import com.example.foodaap.fragment.*
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {
lateinit var txta:TextView
    lateinit var txtc:TextView
   lateinit var drawerLayout: DrawerLayout
    lateinit var coordinatorLayout: CoordinatorLayout
    lateinit var appBar: AppBarLayout
    lateinit var toolbar: Toolbar
    lateinit var frame: FrameLayout
    lateinit var navigationView: NavigationView
    lateinit var sessionManager: SessionManager
    lateinit var sharedPreferences: SharedPreferences
 var previousMenuItem:MenuItem?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)
        appBar = findViewById(R.id.appBar)
        frame = findViewById(R.id.frame)
        toolbar = findViewById(R.id.toolbar)

        coordinatorLayout=findViewById(R.id.coordinatorLayout)

        sessionManager= SessionManager(this)
        sharedPreferences=this.getSharedPreferences(sessionManager.PREF_NAME,sessionManager.PRIVATE_MODE)
        setUpToolbar()

        openHome()


        navigationView.setNavigationItemSelectedListener {
if(previousMenuItem!=null) {
    previousMenuItem?.isChecked = false

}
            it.isCheckable=true
    it.isChecked=true
previousMenuItem=it
val ko= Runnable { drawerLayout.closeDrawer(GravityCompat.START) }
Handler().postDelayed(ko,100)



       when(it.itemId)
{
    R.id.home ->{
        openHome()
    drawerLayout.closeDrawers()
    }
        R.id.profile ->{
            supportFragmentManager.beginTransaction()
                .replace(R.id.frame,ProfileFragment())
                .commit()
            drawerLayout.closeDrawers()
supportActionBar?.title="Profile"
        }
         R.id.favriout ->{
             supportFragmentManager.beginTransaction()
                 .replace(R.id.frame, FavrioutFragment())
                 .commit()
             drawerLayout.closeDrawers()
             supportActionBar?.title="Favriout Restaurents"
         }
         R.id.Order ->{
             supportFragmentManager.beginTransaction()
                 .replace(R.id.frame, OrderFragment())
                 .commit()
             drawerLayout.closeDrawers()
             supportActionBar?.title="Order History"
         }
           R.id.Faq ->{
               supportFragmentManager.beginTransaction()
                   .replace(R.id.frame, FaqFragment())
                   .commit()
               drawerLayout.closeDrawers()
               supportActionBar?.title="FAQs"
           }
           R.id.LogOut ->{
               it.isCheckable=false
               it.isChecked=false
               val dialog=AlertDialog.Builder(this@MainActivity)
              dialog.setTitle("Confirmation")
               dialog.setMessage("Are you sure you want exit?")
               dialog.setPositiveButton("ok"){_,_->
              sessionManager.setLogin(false)
                   sharedPreferences.edit().clear().apply()
                   startActivity(Intent(this@MainActivity,
                       LoginActivity::class.java))
                   Volley.newRequestQueue(this@MainActivity).cancelAll(this::class.java.simpleName)
                   ActivityCompat.finishAffinity(this)

               }
dialog.setNegativeButton("cancel"){_,_->
    dialog.setCancelable(true)
}
dialog.create().show()

           }
}


       return@setNavigationItemSelectedListener true
   }




















































        val convertView=LayoutInflater.from(this@MainActivity).inflate(R.layout.home,null)
val txta:TextView=convertView.findViewById(R.id.txta)
        val txtc:TextView=convertView.findViewById(R.id.txtc)
        txta.text=sharedPreferences.getString("user_name",null)
        txtc.text= "+91-${sharedPreferences.getString("user_mobile_number", null)}"
navigationView.addHeaderView(convertView)



        val actionBarDrawerToggle = ActionBarDrawerToggle(
            this@MainActivity,
            drawerLayout, R.string.open_drawer,
            R.string.close_drawer
        )
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()


    }

    fun setUpToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "FOOD APP"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        return super.onOptionsItemSelected(item)

    }



    private fun openHome(){
        val fragment = HomeFragment()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame, fragment)
        transaction.commit()
        supportActionBar?.title = "All Restaurants"
        navigationView.setCheckedItem(R.id.home)

    }

    override fun onBackPressed() {
        val frag=supportFragmentManager.findFragmentById(R.id.frame)
    when(frag)
    {
        !is HomeFragment->openHome()
     else -> super.onBackPressed()

    }


    }


}