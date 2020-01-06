package com.example.spero

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.spero.R
import com.example.spero.api.RetrofitClient
import com.example.spero.adapters.SportStatsPagerAdapter
import com.example.spero.api.responses.DiagnosticResponse
import com.example.spero.helpers.getErrorMessageFromJSON
import es.dmoral.toasty.Toasty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.provider.MediaStore




class HomeFragment : Fragment(), View.OnClickListener {

    private lateinit var sportStatsPager: ViewPager
    private lateinit var pagerAdapter: PagerAdapter
    private lateinit var loadingScreen: FrameLayout
    private var sportStats: List<DiagnosticResponse>? = null
    private lateinit var previous: ImageView
    private lateinit var next: ImageView
    private var currentPage = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        activity!!.title = getString(R.string.spero_home)
        loadingScreen = activity!!.findViewById(R.id.ls_main)
        loadingScreen.visibility = View.VISIBLE
        sportStatsPager = view.findViewById(R.id.pager_sport_stats)

        sportStatsPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                onPageChanged(position)
            }
        })

        previous = view.findViewById(R.id.iv_previous)
        previous.setOnClickListener(this)
        previous.visibility = View.GONE

        next = view.findViewById(R.id.iv_next)
        next.setOnClickListener(this)

        return view
    }

    override fun onStart() {
        super.onStart()
        getSportStatistics()
    }

    private fun onPageChanged(page: Int) {
        currentPage = page
        if (page == 0) {
            previous.visibility = View.GONE
        } else {
            previous.visibility = View.VISIBLE
        }
        if (page == sportStats!!.lastIndex) {
            next.visibility = View.GONE
        } else {
            next.visibility = View.VISIBLE
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.iv_previous -> {
                currentPage -= 1
                onPageChanged(currentPage)
                sportStatsPager.setCurrentItem(currentPage, true)
            }

            R.id.iv_next -> {
                currentPage += 1
                onPageChanged(currentPage)
                sportStatsPager.setCurrentItem(currentPage, true)
            }
        }
    }

    private fun getSportStatistics() {
        val call = RetrofitClient.getInstance(activity!!).api.getDiagnoses()
        call.enqueue(object : Callback<List<DiagnosticResponse>> {
            override fun onFailure(call: Call<List<DiagnosticResponse>>, t: Throwable) {
                loadingScreen.visibility = View.GONE
                Toasty.error(
                    activity!!,
                    t.message!!,
                    Toasty.LENGTH_LONG
                ).show()
            }

            override fun onResponse(
                call: Call<List<DiagnosticResponse>>,
                response: Response<List<DiagnosticResponse>>
            ) {
                if (response.body() != null) {
                    sportStats = response.body()
                    initPager()
                } else {
                    Toasty.error(
                        activity!!,
                        getErrorMessageFromJSON(response.errorBody()!!.string()),
                        Toasty.LENGTH_LONG
                    ).show()
                }
                loadingScreen.visibility = View.GONE
            }

        })
    }

    private fun initPager() {
        pagerAdapter = SportStatsPagerAdapter(activity!!.supportFragmentManager, sportStats!!)
        sportStatsPager.adapter = pagerAdapter
    }
}