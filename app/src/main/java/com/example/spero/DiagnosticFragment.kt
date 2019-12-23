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

        return view
    }

    companion object {

        fun newInstance(diagnosticResponse: DiagnosticResponse): DiagnosticFragment {
            return DiagnosticFragment(diagnosticResponse)
        }
    }
}
