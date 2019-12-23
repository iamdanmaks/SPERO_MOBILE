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
import lecho.lib.hellocharts.model.PieChartData


class DiagnosticFragment(private val diagnosticResponse: DiagnosticResponse) :
    Fragment() {

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

        tvRes.text =
            getString(LocaleHelper.resultStringResources[diagnosticResponse.result.toString()]!!)

        tvNorm.text = diagnosticResponse.normal_probability.toString()
        tvMurm.text = diagnosticResponse.murmur_probability.toString()
        tvExtr.text = diagnosticResponse.extrasystole_probability.toString()
        tvDate.text = diagnosticResponse.checked_on.toString()

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

        return view
    }

    companion object {

        fun newInstance(diagnosticResponse: DiagnosticResponse): DiagnosticFragment {
            return DiagnosticFragment(diagnosticResponse)
        }
    }
}
