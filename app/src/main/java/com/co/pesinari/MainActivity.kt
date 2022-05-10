package com.co.pesinari

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.co.pesinari.mailgun.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestSmsPermission()
        requestAccountsPermission()

        val preferences = MyPreferences.getInstance(applicationContext)
        val editText:EditText = findViewById(R.id.edit_txt_email)

        findViewById<Button>(R.id.save_btn).setOnClickListener {
            if (editText.text != null && editText.text.length > 0 && editText.text.toString()
                    .contains("@")
            ) {
                CoroutineScope(Dispatchers.IO).launch {
                    preferences?.savetoDataStore(editText.text.toString())
                }
                Toast.makeText(this,"Saved Email Successfully", Toast.LENGTH_SHORT).show()
            }
        }

        CoroutineScope(Dispatchers.IO).launch {
            preferences?.getFromDataStore()?.catch { e ->
                e.printStackTrace()
            }?.collect {
                withContext(Dispatchers.Main) {
                    editText.setText(it)
                }
            }
        }

    }


//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//
//        if (checkPermissions()) {
//            sendAnEmail()
//        }
//    }


    private fun checkPermissions(): Boolean {
        var permissions: List<String> = listOf<String>(Manifest.permission.RECEIVE_SMS, Manifest.permission.GET_ACCOUNTS)
        for (permission in permissions) {
            val grant = ContextCompat.checkSelfPermission(this, permission)
            if (grant != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true;
    }

    private fun requestSmsPermission() {
        val permission: String = Manifest.permission.RECEIVE_SMS
        val grant = ContextCompat.checkSelfPermission(this, permission)
        if (grant != PackageManager.PERMISSION_GRANTED) {
            val permissions = arrayOfNulls<String>(1)
            permissions[0] = permission
            ActivityCompat.requestPermissions(this, permissions, 1)
        }
    }

    private fun requestAccountsPermission() {
        val permission: String = Manifest.permission.GET_ACCOUNTS
        val grant = ContextCompat.checkSelfPermission(this, permission)
        if (grant != PackageManager.PERMISSION_GRANTED) {
            val permissions = arrayOfNulls<String>(1)
            permissions[0] = permission
            ActivityCompat.requestPermissions(this, permissions, 2)
        }
    }
}