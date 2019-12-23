package com.example.spero

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.spero.api.RetrofitClient
import com.example.spero.api.responses.AvatarResponse
import com.example.spero.api.responses.UserResponse
import com.example.spero.helpers.getErrorMessageFromJSON
import de.hdodenhof.circleimageview.CircleImageView
import es.dmoral.toasty.Toasty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.io.InputStream
import java.net.URL


class UserFragment : Fragment() {

    private var user: UserResponse? = null
    private var userAvatar: AvatarResponse? = null
    private lateinit var loadingScreen: FrameLayout
    private lateinit var tvFullName: TextView
    private lateinit var tvUsername: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvDate: TextView
    private lateinit var tvHeight: TextView
    private lateinit var tvWeight: TextView

    private lateinit var avatar: CircleImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_user, container, false)

        avatar = view.findViewById(R.id.civ_profile)

        loadingScreen = activity!!.findViewById(R.id.ls_main)

        tvFullName = view.findViewById(R.id.tv_profile_full_name)
        tvUsername = view.findViewById(R.id.tv_profile_username)
        tvEmail = view.findViewById(R.id.tv_profile_email)
        tvDate = view.findViewById(R.id.tv_profile_birth_date)
        tvHeight = view.findViewById(R.id.tv_profile_height)
        tvWeight = view.findViewById(R.id.tv_profile_weight)

        getUser()

        return view
    }

    private fun getUser() {
        loadingScreen.visibility = View.VISIBLE

        val call = RetrofitClient.getInstance(activity!!).api.getProfile()

        call.enqueue(object : Callback<UserResponse>{
            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                loadingScreen.visibility = View.GONE
                Toasty.error(
                    activity!!,
                    t.message!!,
                    Toasty.LENGTH_LONG
                ).show()
            }

            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                if (response.body() != null) {
                    user = response.body()!!
                    initUserData()
                    if (user!!.avatar) {
                        getAvatar()
                    } else {
                        loadingScreen.visibility = View.GONE
                    }
                } else {
                    Toasty.error(
                        activity!!,
                        getErrorMessageFromJSON(response.errorBody()!!.string()),
                        Toasty.LENGTH_LONG
                    ).show()
                    loadingScreen.visibility = View.GONE
                }
            }

        })
        //Call to get user
        //*****************User fetched successfully*****************
//        user = UserResponse(
//            "email@gmail.com",
//            "stalker",
//            "Eugene",
//            "Ostashko",
//            true,
//            true,
//            "2019-01-01",
//            "2000-05-01",
//            179F,
//            70F,
//            true
//        )
        //*****************User fetched successfully*****************
    }

    private fun getAvatar() {
        val call = RetrofitClient.getInstance(activity!!).api.getAvatar()

        call.enqueue(object : Callback<AvatarResponse>{
            override fun onFailure(call: Call<AvatarResponse>, t: Throwable) {
                loadingScreen.visibility = View.GONE
                Toasty.error(
                    activity!!,
                    t.message!!,
                    Toasty.LENGTH_LONG
                ).show()
            }

            override fun onResponse(
                call: Call<AvatarResponse>,
                response: Response<AvatarResponse>
            ) {
                if (response.body() != null) {
                    userAvatar = response.body()!!
                    if (userAvatar != null) {
                        initUserAvatar()
                    } else {
                        loadingScreen.visibility = View.GONE
                    }
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
        //call to get avatar
        //*****************Avatar fetched successfully*****************
//        userAvatar = AvatarResponse(
//            "success",
//            "https://301-1.ru/uploads/image/mem-so-shrekom-v-bolote-zaebumba_Iw87RIFG7o.jpeg"
//        )
//        if (userAvatar != null) {
//            initUserAvatar()
//        } else {
//            loadingScreen.visibility = View.GONE
//        }
        //*****************Avatar fetched successfully*****************
    }

    private fun initUserData() {
        tvFullName.text =
            String.format(getString(R.string.fullname), user!!.first_name, user!!.second_name)

        tvUsername.text = String.format(getString(R.string.username_templ),user!!.username)

        tvEmail.text = user!!.email
        tvDate.text = user!!.date_of_birth
        tvHeight.text = user!!.height.toString()
        tvWeight.text = user!!.weight.toString()

    }

    private fun initUserAvatar() {
        DownLoadImageTask(avatar, loadingScreen).execute(userAvatar!!.message)
    }

    private class DownLoadImageTask(
        private val imageView: CircleImageView,
        private val loadingScreen: FrameLayout
    ) :
        AsyncTask<String?, Void?, Bitmap?>() {

        override fun onPostExecute(result: Bitmap?) {
            imageView.setImageBitmap(result)
            loadingScreen.visibility = View.GONE
        }

        override fun doInBackground(vararg params: String?): Bitmap? {
            val imageUrl = params[0]
            var logo: Bitmap? = null
            try {
                val `is`: InputStream = URL(imageUrl).openStream()

                logo = BitmapFactory.decodeStream(`is`)
            } catch (e: Exception) { // Catch the download exception
                e.printStackTrace()
            }
            return logo
        }


    }
}
