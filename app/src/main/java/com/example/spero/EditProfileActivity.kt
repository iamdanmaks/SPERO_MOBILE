package com.example.spero

import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.spero.api.RetrofitClient
import com.example.spero.api.models.UserData
import com.example.spero.api.requests.EditProfileRequest
import com.example.spero.api.responses.OrdinaryResponse
import com.example.spero.helpers.bitmapToFile
import com.example.spero.helpers.getErrorMessageFromJSON
import de.hdodenhof.circleimageview.CircleImageView
import es.dmoral.toasty.Toasty
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException


class EditProfileActivity : AppCompatActivity() {

    companion object {
        const val USER_DATA_KEY = "user_data_key"
        const val RESULT_LOAD_IMG = 0
    }

    private lateinit var userData: UserData
    private lateinit var etName: EditText
    private lateinit var etSurname: EditText
    private lateinit var etHeight: EditText
    private lateinit var etWeight: EditText
    private lateinit var etUsername: EditText
    private lateinit var btEditProfile: Button
    private lateinit var loadingScreen: FrameLayout
    private lateinit var civAvatar: CircleImageView
    private var avatar: Bitmap? = null
    private var mp = MediaPlayer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        title = getString(R.string.edit_profile)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        userData = intent.getSerializableExtra(USER_DATA_KEY) as UserData

        etName = findViewById(R.id.et_edit_profile_name)
        etName.setHint(userData.name)

        etSurname = findViewById(R.id.et_edit_profile_surname)
        etSurname.setHint(userData.surname)

        etHeight = findViewById(R.id.et_edit_profile_height)
        etHeight.setHint(userData.height.toString())

        etWeight = findViewById(R.id.et_edit_profile_weight)
        etWeight.setHint(userData.weight.toString())

        etUsername = findViewById(R.id.et_edit_profile_username)
        etUsername.setHint(userData.username)

        loadingScreen = findViewById(R.id.ls_edit_profile)
        loadingScreen.visibility = View.VISIBLE

        civAvatar = findViewById(R.id.civ_edit_profile_avatar)

        val parts = userData.avatar.split("/v").toTypedArray()
        val rnds = (1..1000000).random().toString()
        val link = parts[0] + "/v" + rnds + parts[1]

        DownLoadImageTask(civAvatar, loadingScreen).execute(
            link
        )

        civAvatar.setOnClickListener {
            val photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG)
        }

        btEditProfile = findViewById(R.id.bt_edit_profile)
        btEditProfile.setOnClickListener {
            editProfilePressed()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            val selectedImage: Uri = data!!.data!!
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(
                    contentResolver,
                    selectedImage
                )
                avatar = bitmap
                civAvatar.setImageBitmap(bitmap)
            } catch (e: IOException) {
                Toasty.error(this, getString(R.string.image_not_picked), Toasty.LENGTH_LONG).show()
            }
        } else {
            Toasty.error(this, getString(R.string.image_not_picked), Toasty.LENGTH_LONG).show()
        }
    }

    fun getRealPathFromURI(contentUri: Uri): String {

        // can post image
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = managedQuery(
            contentUri,
            proj, // WHERE clause selection arguments (none)
            null, null, null
        )// Which columns to return
        // WHERE clause; which rows to return (all rows)
        // Order-by clause (ascending by name)
        val column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()

        return cursor.getString(column_index)
    }

    private fun editProfilePressed() {
        val name = etName.text.toString()
        val surname = etSurname.text.toString()
        val height = etHeight.text.toString()
        val weight = etWeight.text.toString()
        val username = etUsername.text.toString()

        var heightF: Float? = null
        var weightF: Float? = null
        var nameF: String? = null
        var surnameF: String? = null
        var usernameF: String? = null

        if (height != "")
            heightF = height.toFloat()

        if (weight != "")
            weightF = weight.toFloat()

        if (name != "")
            nameF = name

        if (surname != "")
            surnameF = surname

        if (username != "")
            usernameF = username

        val request = EditProfileRequest(nameF, surnameF, heightF, weightF, usernameF)

        if (validateRequest(request)) {
            val call = RetrofitClient.getInstance(this).api.editProfile(request)

            call.enqueue(object : Callback<OrdinaryResponse> {
                override fun onFailure(call: Call<OrdinaryResponse>, t: Throwable) {
                    loadingScreen.visibility = View.GONE
                    Toasty.error(
                        this@EditProfileActivity,
                        t.message!!,
                        Toasty.LENGTH_LONG
                    ).show()
                }

                override fun onResponse(
                    call: Call<OrdinaryResponse>,
                    response: Response<OrdinaryResponse>
                ) {
                    if (response.body() != null) {
                        Toasty.success(
                            this@EditProfileActivity,
                            response.body()!!.message,
                            Toasty.LENGTH_LONG
                        ).show()
                        if (avatar != null) {
                            sendAvatar()
                        }
                        else {
                            loadingScreen.visibility = View.GONE
                        }
                    } else {
                        Toasty.error(
                            this@EditProfileActivity,
                            getErrorMessageFromJSON(response.errorBody()!!.string()),
                            Toasty.LENGTH_LONG
                        ).show()
                        loadingScreen.visibility = View.GONE
                    }
                }

            })
        }
    }

    private fun sendAvatar() {
        val f = bitmapToFile(avatar!!,this)
        val reqFile: RequestBody =
            RequestBody.create(MediaType.parse("image/*"), f)
        val avatarBody =
            MultipartBody.Part.createFormData("avatar", f.name, reqFile)

        val call = RetrofitClient.getInstance(this).api.editAvatar(avatarBody)

        call.enqueue(object : Callback<OrdinaryResponse>{
            override fun onFailure(call: Call<OrdinaryResponse>, t: Throwable) {
                loadingScreen.visibility = View.GONE
                Toasty.error(
                    this@EditProfileActivity,
                    t.message!!,
                    Toasty.LENGTH_LONG
                ).show()
            }

            override fun onResponse(
                call: Call<OrdinaryResponse>,
                response: Response<OrdinaryResponse>
            ) {
                if (response.body() != null) {

                } else {
                    Toasty.error(
                        this@EditProfileActivity,
                        getErrorMessageFromJSON(response.errorBody()!!.string()),
                        Toasty.LENGTH_LONG
                    ).show()
                }
                loadingScreen.visibility = View.GONE
            }

        })
    }

    private fun validateRequest(request: EditProfileRequest): Boolean {
        //Validate request
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            android.R.id.home -> finish()
        }

        return super.onOptionsItemSelected(item)
    }


}
