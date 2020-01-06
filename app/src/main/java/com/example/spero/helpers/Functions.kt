package com.example.spero.helpers

import android.app.Activity
import android.content.Intent
import android.content.Context
import android.graphics.Bitmap
import com.example.spero.api.responses.OrdinaryResponse
import com.example.spero.storage.SharedPrefManager
import org.json.JSONObject
import java.io.*

fun restartActivity(baseContext:Context,activity: Activity){
    val intent = baseContext.packageManager
        .getLaunchIntentForPackage(baseContext.packageName)
    intent!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
    baseContext.startActivity(intent)
    activity.finish()
}

fun getErrorMessageFromJSON(json:String):String{
    return JSONObject(json).getString("message")
}

@Throws(IOException::class,FileNotFoundException::class)
fun bitmapToFile(bitmap:Bitmap,context:Context,filename:String = "avatar") : File{
    val file = File(context.cacheDir, filename)
    file.createNewFile()

    val bos = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 0, bos)
    val bitmapData = bos.toByteArray()

    var fos:FileOutputStream? = null
    try {
        fos = FileOutputStream(file)
    } catch (e:FileNotFoundException) {
        e.printStackTrace()
    }
    try {
        fos!!.write(bitmapData)
        fos.flush()
        fos.close()
    } catch (e:IOException) {
        e.printStackTrace()
    }
    return file
}
