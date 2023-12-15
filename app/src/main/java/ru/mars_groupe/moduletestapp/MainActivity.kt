package ru.mars_groupe.moduletestapp

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    companion object {
        const val PERMISSION_REQUEST = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkPermissions()
    }

    private fun checkPermissions() {
        val checkInfo =
            ContextCompat.checkSelfPermission(this, "ru.modulkassa.pos.permission.CHECK_INFO")
        val kktInfo =
            ContextCompat.checkSelfPermission(this, "ru.modulkassa.pos.permission.KKT_INFO")
        val printCheck =
            ContextCompat.checkSelfPermission(this, "ru.modulkassa.pos.permission.PRINT_CHECK")

        val listPermissionsNeeded = mutableListOf<String>()

        if (checkInfo != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add("ru.modulkassa.pos.permission.CHECK_INFO")
        }
        if (kktInfo != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add("ru.modulkassa.pos.permission.KKT_INFO")
        }
        if (printCheck != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add("ru.modulkassa.pos.permission.PRINT_CHECK")
        }
        if (listPermissionsNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                listPermissionsNeeded.toTypedArray(),
                PERMISSION_REQUEST
            )
        }
    }
}