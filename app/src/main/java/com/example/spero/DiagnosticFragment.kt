package com.example.spero

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.spero.LocaleHelper
import com.example.spero.R
import com.example.spero.api.responses.DiagnosticResponse
import lecho.lib.hellocharts.view.PieChartView
import lecho.lib.hellocharts.model.SliceValue
import android.graphics.Color
import android.widget.Button
import lecho.lib.hellocharts.model.PieChartData
import android.media.AudioManager
import android.media.MediaPlayer
import android.content.ContentValues.TAG
import android.icu.text.SimpleDateFormat
import android.icu.util.TimeZone
import android.net.Uri
import android.util.Base64
import android.util.Log
import com.example.spero.storage.SharedPrefManager
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.reflect.typeOf


class DiagnosticFragment(private val diagnosticResponse: DiagnosticResponse) :
    Fragment(), View.OnClickListener {

    private lateinit var btPlay: Button
    private var playing: Boolean = false
    private lateinit var media: MediaPlayer

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_diagnostic, container, false)
        val tvRes = view.findViewById<TextView>(R.id.tv_res)
        val tvNorm = view.findViewById<TextView>(R.id.tv_norm)
        val tvMurm = view.findViewById<TextView>(R.id.tv_murm)
        val tvExtr = view.findViewById<TextView>(R.id.tv_extr)
        val tvDate = view.findViewById<TextView>(R.id.tv_date)
        btPlay = view.findViewById<Button>(R.id.bt_play)

        btPlay.setOnClickListener(this)

        tvRes.text =
            getString(LocaleHelper.resultStringResources[diagnosticResponse.result.toString()]!!)

        tvNorm.text = diagnosticResponse.normal_probability.toString()
        tvMurm.text = diagnosticResponse.murmur_probability.toString()
        tvExtr.text = diagnosticResponse.extrasystole_probability.toString()

        /*
        var format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault())
        format.timeZone = TimeZone.getTimeZone("UTC")
        var result = format.parse(diagnosticResponse.checked_on)
*/
        tvDate.text = diagnosticResponse.checked_on.substring(0,10)

        val pieChartView = view.findViewById<PieChartView>(R.id.chart)
        val pieData = ArrayList<SliceValue>()

        pieData.add(SliceValue(
            diagnosticResponse.normal_probability.toFloat(),
            Color.GREEN).setLabel(
            getString(R.string.normal)
        ))

        pieData.add(SliceValue(
            diagnosticResponse.murmur_probability.toFloat(),
            Color.RED).setLabel(
                getString(R.string.murmur)
            ))

        pieData.add(SliceValue(
            diagnosticResponse.extrasystole_probability.toFloat(),
            Color.MAGENTA).setLabel(
            getString(R.string.extrahls)
        ))

        val pieChartData = PieChartData(pieData)

        pieChartData.setHasCenterCircle(true).setCenterText1(
            getString(LocaleHelper.resultStringResources[diagnosticResponse.result.toString()]!!)
        ).setCenterText1FontSize(20).setCenterText1Color(Color.parseColor("#000000"));

        pieChartData.setHasLabels(true).setValueLabelTextSize(14)

        pieChartView.setPieChartData(pieChartData)

        media = MediaPlayer()
        media.setLooping(true)
        media.setAudioStreamType(AudioManager.STREAM_MUSIC)
        val toEncrypt = SharedPrefManager.getInstance(activity!!.baseContext).getAuthToken()!!
        val encoded = Base64.encodeToString(toEncrypt.toByteArray(), Base64.DEFAULT)

        // create header
        val headers = HashMap<String, String>()
        headers.put("Authorization", toEncrypt)

        val uri = Uri.parse(
            "http://54.204.74.32/api/diagnosis/" +
                    diagnosticResponse.public_id + "/media"
        )

        media.setDataSource(activity!!, uri, headers)
        media.prepareAsync()

        //media = MediaPlayer.create(activity!!, R.raw.heart)

        return view
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.bt_play -> {
                println("\t\t\t PRESSED \t\t\t")
                if (!playing){
                    media.start()
                    playing = true
                }
                else {
                    media.pause()
                    playing = false
                }
            }
        }
    }

    companion object {

        fun newInstance(diagnosticResponse: DiagnosticResponse): DiagnosticFragment {
            return DiagnosticFragment(diagnosticResponse)
        }
    }
}
