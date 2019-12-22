package com.example.spero

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import android.view.MenuItem
import android.view.View
import com.example.spero.DiagnosisFragment
import com.example.spero.HomeFragment
import com.example.spero.UserFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.example.spero.api.RetrofitClient
import com.example.spero.helpers.RetrofitCallback
import com.example.spero.storage.SharedPrefManager
import com.google.android.material.navigation.NavigationView
import okhttp3.ResponseBody
import com.example.spero.R
import androidx.annotation.NonNull
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T



class MainActivity : AppCompatActivity()
    {
    private lateinit var bottomNavigation: BottomNavigationView
    private var fTrans = supportFragmentManager.beginTransaction()
    private var lang: String? = null
    private val localeHelper: LocaleHelper = LocaleHelper()

    private lateinit var fragmentHome: HomeFragment
    private var fragmentUser: UserFragment? = null
    private var fragmentDiagnosis: DiagnosisFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bottomNavigation = findViewById(R.id.bottom_navigation)
        bottomNavigation.setOnNavigationItemSelectedListener(navigationItemChosenListener);

        fragmentHome = HomeFragment()

        fTrans.add(R.id.fl_main, fragmentHome).commit()

        lang = intent.getStringExtra("lang")
    }

        var navigationItemChosenListener: BottomNavigationView.OnNavigationItemSelectedListener =
            BottomNavigationView.OnNavigationItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.navigation_home -> {
                        fragmentHome = HomeFragment()
                        replacePage(fragmentHome)
                        return@OnNavigationItemSelectedListener true
                    }
                    R.id.navigation_diagnosis -> {
                        fragmentDiagnosis = DiagnosisFragment()
                        replacePage(fragmentDiagnosis!!)
                        return@OnNavigationItemSelectedListener true
                    }
                    R.id.navigation_user -> {
                        fragmentUser = UserFragment()
                        replacePage(fragmentUser!!)
                        return@OnNavigationItemSelectedListener true
                    }
                }
                false
            }

    private fun logout() {
        SharedPrefManager.getInstance(this).logout()
        val intent = Intent(this, AuthorizationActivity::class.java)
        intent.putExtra("lang", lang)
        startActivity(intent)
        finish()
    }

    private fun replacePage(fragment: Fragment) {
        fTrans = supportFragmentManager.beginTransaction()
        fTrans.replace(R.id.fl_main, fragment).addToBackStack(null).commit()
    }
}
