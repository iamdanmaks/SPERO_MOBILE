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
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.view.Menu
import android.widget.AdapterView
import com.example.spero.helpers.restartActivity
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
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

        override fun onCreateOptionsMenu(menu: Menu): Boolean {
            menuInflater.inflate(R.menu.main_menu, menu)
            val item: MenuItem = menu.findItem(R.id.spin_languages)

            val adapter: ArrayAdapter<String> =
                ArrayAdapter(
                    this,
                    R.layout.spinner_item,
                    LocaleHelper.languages
                )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            val spinLanguages: Spinner = item.actionView as Spinner

            val current = LocaleHelper.languages.indexOf(lang)

            spinLanguages.adapter = adapter


            spinLanguages.setSelection(current)

            spinLanguages.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View,
                    position: Int, id: Long
                ) {
                    if (position != current) {
                        localeHelper.setLocale(baseContext,LocaleHelper.languages[position])
                        restartActivity(baseContext, this@MainActivity)
                    }
                }

                override fun onNothingSelected(arg0: AdapterView<*>?) {}
            }

            return true
        }

        override fun onOptionsItemSelected(item: MenuItem): Boolean {

            when (item.itemId) {

                R.id.ll_logout -> {
                    logout()
                    return true
                }
            }

            return true
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

        fun onClick(v: View?) {
            when (v!!.id) {
                R.id.ll_logout -> logout()
            }
        }

        private fun initSpinner() {
            val adapter: ArrayAdapter<String> =
                ArrayAdapter(
                    this,
                    R.layout.support_simple_spinner_dropdown_item,
                    LocaleHelper.languages
                )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            val spinLanguages: Spinner = findViewById(R.id.spin_languages)

            val current = LocaleHelper.languages.indexOf(lang)

            spinLanguages.adapter = adapter


            spinLanguages.setSelection(current)

            spinLanguages.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View,
                    position: Int, id: Long
                ) {
                    if (position != current) {
                        lang = LocaleHelper.languages[position]
                        localeHelper.setLocale(baseContext, lang)
                        restartActivity(baseContext,this@MainActivity)
                    }
                }

                override fun onNothingSelected(arg0: AdapterView<*>?) {}
            }
        }
}
