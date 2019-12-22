package com.example.spero.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.spero.api.responses.DiagnosticResponse
import com.example.spero.DiagnosticFragment


class SportStatsPagerAdapter(
    fm: FragmentManager?,
    private val diagnosticResponses: List<DiagnosticResponse>
) :
    FragmentPagerAdapter(fm!!, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    override fun getItem(position: Int): Fragment {
        return DiagnosticFragment.newInstance(diagnosticResponses[position])
    }

    override fun getCount(): Int {
        return diagnosticResponses.size
    }
}