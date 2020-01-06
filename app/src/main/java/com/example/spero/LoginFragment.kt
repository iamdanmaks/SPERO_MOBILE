package com.example.spero

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.spero.R
import com.example.spero.MainActivity
import com.example.spero.api.RetrofitClient
import com.example.spero.api.requests.LoginRequest
import com.example.spero.api.responses.LoginResponse
import com.example.spero.storage.SharedPrefManager
import es.dmoral.toasty.Toasty
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginFragment : Fragment(), View.OnClickListener {
    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.tv_register -> swapFragmentTo(registerFragment)
            R.id.bt_login -> login()
            R.id.tv_forgot_pass -> swapFragmentTo(forgotPasswordFragment)
        }
    }

    private var tvRegister: TextView? = null
    private var fTrans: FragmentTransaction? = null
    private var registerFragment: RegisterFragment? = null
    private var etEmail: EditText? = null
    private var etPassword: EditText? = null
    private var btLogin: Button? = null
    private var tvForgotPass: TextView? = null
    private var loadingScreen: FrameLayout? = null
    private var forgotPasswordFragment: ForgotPasswordFragment? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity!!.title = getString(R.string.jsports_login)
        fTrans = activity!!.supportFragmentManager.beginTransaction()
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tvRegister = view.findViewById(R.id.tv_register)
        tvRegister!!.setOnClickListener(this)

        etEmail = view.findViewById(R.id.et_login_email)
        etPassword = view.findViewById(R.id.et_login_password)

        etPassword!!.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                login()
            }

            true

        }

        btLogin = view.findViewById(R.id.bt_login)
        btLogin!!.setOnClickListener(this)

        tvForgotPass = view.findViewById(R.id.tv_forgot_pass)
        tvForgotPass!!.setOnClickListener(this)

        loadingScreen = activity!!.findViewById(R.id.auth_loading_screen)

        registerFragment = RegisterFragment()
        forgotPasswordFragment = ForgotPasswordFragment()
    }

    private fun swapFragmentTo(fragment: Fragment?) {
        fTrans!!.replace(R.id.fl_auth, fragment!!).addToBackStack(null).commit()
    }

    private fun login() {
        val username = etEmail!!.text.toString()
        val password = etPassword!!.text.toString()

        if (validateCredentials(username, password)) {
            loadingScreen!!.visibility = View.VISIBLE
            val request = LoginRequest(username, password)
            val call = RetrofitClient.getInstance(activity!!).api.login(request)
            call.enqueue(object : Callback<LoginResponse> {
                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    loadingScreen!!.visibility = View.GONE
                    Toasty.error(
                        activity!!,
                        t.message!!,
                        Toasty.LENGTH_LONG
                    ).show()
                }

                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {
                    if (response.body() != null) {
                        SharedPrefManager.getInstance(activity!!).saveToken(response.body()!!.Authorization)
                        val intent = Intent(activity!!, MainActivity::class.java)
                        startActivity(intent)
                        activity!!.finish()
                    } else {
                        Toasty.error(
                            activity!!,
                            JSONObject(response.errorBody()!!.string()).getString("message"),
                            Toasty.LENGTH_LONG
                        ).show()
                    }
                    loadingScreen!!.visibility = View.GONE
                }

            })
        }
    }

    private fun validateCredentials(email: String, password: String): Boolean {

        val emailRegex = Regex(
            """^[a-zA-Z0-9.!#${'$'}%&’*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\.[a-zA-Z0-9-]+)*${'$'}"""
        )

        val passwordRegex = Regex(
            """(?=.*\d)(?=.*[a-z])(?=.*[A-Z])(?=.*\W)"""
        )

        when {
            email.isEmpty() -> {
                etEmail!!.error = getString(R.string.username_required)
                return false
            }
            !emailRegex.matches(email) -> {
                etEmail!!.error = getString(R.string.username_wrong)
                return false
            }
        }

        when {
            password.isEmpty() -> {
                etPassword!!.error = getString(R.string.password_required)
                return false
            }
            password.length < 6 -> {
                etPassword!!.error = getString(R.string.password_short)
                return false
            }
            password.length > 30 -> {
                etPassword!!.error = getString(R.string.password_long)
                return false
            }

        }
        return true
    }


}
