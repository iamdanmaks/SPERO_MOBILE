package com.example.spero

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.spero.R
import com.example.spero.api.RetrofitClient
import com.example.spero.api.requests.RegisterRequest
import com.example.spero.api.responses.LoginResponse
import com.example.spero.helpers.CustomRegex
import es.dmoral.toasty.Toasty
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.time.temporal.ChronoField
import java.util.*
import java.util.regex.Pattern


class RegisterFragment : Fragment(), View.OnClickListener {

    private lateinit var tvLogin: TextView
    private lateinit var fTrans: FragmentTransaction
    private var loginFragment: LoginFragment? = null
    private lateinit var loadingScreen: FrameLayout
    private lateinit var etName: EditText
    private lateinit var etSurname: EditText
    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var etEmail: EditText
    private lateinit var etHeight: EditText
    private lateinit var etWeight: EditText
    private lateinit var etDateOfBirth: EditText
    private lateinit var ivDate: ImageView
    private lateinit var btSubmit: Button


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity!!.title = getString(R.string.jsports_register)
        loadingScreen = activity!!.findViewById(R.id.auth_loading_screen)

        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvLogin = view.findViewById(R.id.tv_login)
        tvLogin.setOnClickListener(this)

        etName = view.findViewById(R.id.et_register_first_name)
        etSurname = view.findViewById(R.id.et_register_second_name)
        etUsername = view.findViewById(R.id.et_register_username)
        etPassword = view.findViewById(R.id.et_register_password)
        etConfirmPassword = view.findViewById(R.id.et_register_confirm_password)
        etEmail = view.findViewById(R.id.et_register_email)
        etHeight = view.findViewById(R.id.et_register_height)
        etWeight = view.findViewById(R.id.et_register_weight)
        etDateOfBirth = view.findViewById(R.id.et_register_date)

        ivDate = view.findViewById(R.id.iv_register_date)
        ivDate.setOnClickListener(this)

        btSubmit = view.findViewById(R.id.bt_register)
        btSubmit.setOnClickListener(this)

        fTrans = activity!!.supportFragmentManager.beginTransaction()
        loginFragment = LoginFragment()
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.tv_login -> fTrans.replace(
                R.id.fl_auth,
                loginFragment!!
            ).addToBackStack(null).commit()

            R.id.bt_register -> register()

            R.id.iv_register_date -> pickDate()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun pickDate() {
        val calendar = Calendar.getInstance()
        val cYear = calendar.get(Calendar.YEAR)
        val cMonth = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog = DatePickerDialog(
            activity!!,
            OnDateSetListener { _, year, month, day ->
                etDateOfBirth
                    .setText("$year-${if (month < 10) "0" else ""}${(month + 1)}-${if (day < 10) "0" else ""}$day")
            },
            cYear,
            cMonth,
            dayOfMonth
        )
        datePickerDialog.show()
    }
    private fun register() {
        val name: String = etName.text.toString()
        val surname: String = etSurname.text.toString()
        val username: String = etUsername.text.toString()
        val password: String = etPassword.text.toString()
        val password_confirm = etConfirmPassword.text.toString()
        val email: String = etEmail.text.toString()
        val height: Float? =
            if (etHeight.text.toString().isEmpty()) null else etHeight.text.toString().toFloat()
        val weight: Float? =
            if (etWeight.text.toString().isEmpty()) null else etWeight.text.toString().toFloat()
        val date: String = etDateOfBirth.text.toString()
        val registerRequest = RegisterRequest(
            email,
            password,
            password_confirm,
            name,
            surname,
            username,
            date,
            height,
            weight
        )
        if (validateRegisterRequest(registerRequest)) {
            loadingScreen.visibility = View.VISIBLE
            val call = RetrofitClient.getInstance(activity!!).api.register(registerRequest)
            call.enqueue(object : Callback<LoginResponse> {
                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    loadingScreen.visibility = View.GONE
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
    private fun validateRegisterRequest(registerRequest: RegisterRequest): Boolean {
        val usernameRegex = Regex(CustomRegex.USERNAME)
        val dateRegex = Regex(CustomRegex.DATE)
        val countryCodes = Locale.getISOCountries()
        val countries: MutableSet<String> = mutableSetOf()
        for (countryCode in countryCodes) {
            val obj = Locale("", countryCode)
            countries.add(obj.displayCountry)
        }
        val current = LocalDate.now()
        val given =
            if (registerRequest.date_of_birth != null) LocalDate.parse(registerRequest.date_of_birth) else null
        when {
            registerRequest.first_name.isEmpty() -> {
                etName.error = getString(R.string.name_required)
                return false
            }
            registerRequest.second_name.isEmpty() -> {
                etSurname.error = getString(R.string.surname_required)
                return false
            }
            registerRequest.first_name.length > 50 -> {
                etName.error = getString(R.string.name_long)
                return false
            }
            registerRequest.username.isEmpty() -> {
                etUsername.error = getString(R.string.username_empty)
                return false
            }
            registerRequest.username.length < 6 -> {
                etUsername.error = getString(R.string.username_short)
                return false
            }
            registerRequest.username.length > 25 -> {
                etUsername.error = getString(R.string.username_long)
                return false
            }
            !usernameRegex.matches(registerRequest.username) -> {
                etUsername.error = getString(R.string.username_wrong)
                return false
            }
            registerRequest.password.isEmpty() -> {
                etPassword.error = getString(R.string.password_required)
                return false
            }
            registerRequest.password.length < 6 -> {
                etPassword.error = getString(R.string.password_short)
                return false
            }
            registerRequest.password.length > 30 -> {
                etPassword.error = getString(R.string.password_long)
                return false
            }
            registerRequest.password != etConfirmPassword.text.toString() -> {
                etConfirmPassword.error = getString(R.string.passwords_not_match)
                return false
            }
            registerRequest.email.isEmpty() -> {
                etEmail.error = getString(R.string.email_required)
                return false
            }
            !Pattern.matches(Patterns.EMAIL_ADDRESS.pattern(), registerRequest.email) -> {
                etEmail.error = getString(R.string.email_wrong)
                return false
            }
            registerRequest.height != null && registerRequest.height > 250 -> {
                etHeight.error = getString(R.string.height_large)
                return false
            }
            registerRequest.height != null && registerRequest.height < 56 -> {
                etHeight.error = getString(R.string.height_small)
                return false
            }
            registerRequest.weight != null && registerRequest.weight > 200 -> {
                etWeight.error = getString(R.string.weight_large)
                return false
            }
            registerRequest.weight != null && registerRequest.weight < 20 -> {
                etWeight.error = getString(R.string.weight_small)
                return false
            }
            registerRequest.date_of_birth != null && !dateRegex.matches(registerRequest.date_of_birth) -> {
                etDateOfBirth.error = getString(R.string.date_format_wrong)
                return false
            }
            registerRequest.date_of_birth != null && current.minusYears(4).isBefore(given) -> {
                etDateOfBirth.error = getString(R.string.date_small)
                return false
            }
            registerRequest.date_of_birth != null && current.minusYears(100).isAfter(given) -> {
                etDateOfBirth.error = getString(R.string.date_large)
                return false
            }
        }
        return true
    }
}
