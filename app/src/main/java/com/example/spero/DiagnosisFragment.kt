package com.example.spero

import android.content.Context
import android.graphics.Color
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.view.isVisible
import lecho.lib.hellocharts.model.PieChartData
import lecho.lib.hellocharts.model.SliceValue
import lecho.lib.hellocharts.view.PieChartView
import java.util.*
import kotlin.concurrent.schedule

class DiagnosisFragment : Fragment(), View.OnClickListener {

    private lateinit var btPlay: Button
    private lateinit var btConnect: Button
    private var ready: Boolean = false
    private var playing: Boolean = false
    private lateinit var media: MediaPlayer
    private lateinit var pieChartView: PieChartView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_diagnosis, container, false)
        activity!!.title = getString(R.string.spero_diagnosis)

        btPlay = view.findViewById<Button>(R.id.bt_play)
        btConnect = view.findViewById<Button>(R.id.bt_connect)

        btPlay.setOnClickListener(this)
        btConnect.setOnClickListener(this)

        media = MediaPlayer.create(activity, R.raw.heart)

        pieChartView = view.findViewById<PieChartView>(R.id.chart)
        val pieData = ArrayList<SliceValue>()

        pieData.add(
            SliceValue(
                0.57f,
                Color.GREEN).setLabel(
                getString(R.string.normal)
            ))

        pieData.add(
            SliceValue(
                0.17f,
                Color.RED).setLabel(
                getString(R.string.murmur)
            ))

        pieData.add(
            SliceValue(
                0.26f,
                Color.MAGENTA).setLabel(
                getString(R.string.extrahls)
            ))

        val pieChartData = PieChartData(pieData)

        pieChartData.setHasCenterCircle(true).setCenterText1(
            getString(R.string.normal)
        ).setCenterText1FontSize(20).setCenterText1Color(Color.parseColor("#000000"));

        pieChartData.setHasLabels(true).setValueLabelTextSize(14)

        pieChartView.setPieChartData(pieChartData)

        return view
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.bt_play -> {
                if (!playing && ready){
                    media.start()
                    playing = true
                }
                else {
                    media.pause()
                    playing = false
                }
            }
            R.id.bt_connect -> {
                ready = true

                val loadingScreen = activity!!.findViewById<View>(R.id.ls_main)
                loadingScreen.visibility = View.VISIBLE

                Handler().postDelayed({
                    btConnect.visibility = View.INVISIBLE
                    btPlay.visibility = View.VISIBLE
                    pieChartView.visibility = View.VISIBLE
                    loadingScreen.visibility = View.GONE
                }, 35000)
            }
        }
    }
}
