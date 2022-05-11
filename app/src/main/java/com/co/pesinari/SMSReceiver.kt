package com.co.pesinari

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log
import android.widget.Toast
import com.co.pesinari.mailgun.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.net.HttpURLConnection

class SMSReceiver : BroadcastReceiver() {

    private val TAG:String = "SMSReceiver"
    private val FROM:String = "penasi@mg.pesinari.com"

    override fun onReceive(context: Context?, intent: Intent?) {

        if(context == null || intent == null || intent.action == null){
            return
        }
        if (intent.action != (Telephony.Sms.Intents.SMS_RECEIVED_ACTION)) {
            return
        }

        val smsMessages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
        val messageBuilder = StringBuilder()
        var messageSubject:String = "subject"
        Log.d(TAG, "A SMS is received")
        // for long text , need to iterate
        for (message in smsMessages) {
            messageBuilder.append(message.messageBody)
            messageSubject = message.displayOriginatingAddress
            Log.d(TAG, "SMS body ${message.messageBody}")
        }

        CoroutineScope(Dispatchers.IO).launch {
            MyPreferences.getInstance(context)?.getFromDataStore()?.collect {
                sendAnEmail(it, messageSubject, messageBuilder.toString())
            }
        }
    }

    private fun sendAnEmail(to:String, subject:String, text:String) {
        RetrofitClient.instance
            ?.api
            ?.sendEmail(FROM, to, subject, text)
            ?.enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(call: Call<ResponseBody?>?, response: Response<ResponseBody?>) {
                    if (response.code() == HttpURLConnection.HTTP_OK) {
                        try {
                            val obj = JSONObject(response.body()?.string())
                            Log.d(TAG,  obj.getString("message"))
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>?, t: Throwable) {
                    Log.e(TAG, ""+t.message)
                }
            })
    }
}