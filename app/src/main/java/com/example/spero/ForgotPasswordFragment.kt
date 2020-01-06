package com.example.spero

import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout

import com.example.spero.R
import com.example.spero.api.RetrofitClient
import com.example.spero.api.responses.OrdinaryResponse
import es.dmoral.toasty.Toasty
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.regex.Pattern

class ForgotPasswordFragment : Fragment(), View.OnClickListener {

    private lateinit var loadingScreen: FrameLayout
    private lateinit var etResetPassEmail: EditText
    private lateinit var buttonResetPassSubmit: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity!!.title = getString(R.string.forgot_password)
        return inflater.inflate(R.layout.fragment_forgot_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etResetPassEmail = view.findViewById(R.id.et_reset_pass_email)
        etResetPassEmail.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                resetPassSubmit()
            }

            true

        }

        buttonResetPassSubmit = view.findViewById(R.id.bt_reset_pass_submit)
        buttonResetPassSubmit.setOnClickListener(this)

        loadingScreen = activity!!.findViewById(R.id.auth_loading_screen)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.bt_reset_pass_submit -> resetPassSubmit()
        }
    }

    private fun resetPassSubmit() {
        val email: String = etResetPassEmail.text.toString()

        if (validateEmail(email)) {
            loadingScreen.visibility = View.VISIBLE
            val call = RetrofitClient.getInstance(activity!!).api.forgotPassword(email)
            call.enqueue(object : Callback<OrdinaryResponse> {
                override fun onFailure(call: Call<OrdinaryResponse>, t: Throwable) {
                    loadingScreen.visibility = View.GONE
                    Toasty.error(
                        activity!!,
                        t.message!!,
                        Toasty.LENGTH_LONG
                    ).show()
                }

                override fun onResponse(
                    call: Call<OrdinaryResponse>,
                    response: Response<OrdinaryResponse>
                ) {
                    if (response.body() != null) {
                        Toasty.success(activity!!, response.body()!!.message, Toasty.LENGTH_LONG)
                            .show()
                        activity!!.supportFragmentManager.popBackStack()
                    } else {
                        Toasty.error(
                            activity!!,
                            JSONObject(response.errorBody()!!.string()).getString("message"),
                            Toasty.LENGTH_LONG
                        ).show()
                    }
                    loadingScreen.visibility = View.GONE
                }

            })
        }
    }

    private fun validateEmail(email: String): Boolean {
        if (email.isEmpty()) {
            etResetPassEmail.error = getString(R.string.email_required)
            return false
        } else if (!Pattern.matches(Patterns.EMAIL_ADDRESS.pattern(), email)) {
            etResetPassEmail.error = getString(R.string.wrong_email)
            return false
        }
        return true
    }
}
