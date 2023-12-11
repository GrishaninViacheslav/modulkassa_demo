package ru.mars_groupe.moduletestapp

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import ru.modulkassa.pos.integration.core.action.ActionCallback
import ru.modulkassa.pos.integration.core.action.PrintTextAction
import ru.modulkassa.pos.integration.entity.check.Employee
import ru.modulkassa.pos.integration.entity.check.ReportLine
import ru.modulkassa.pos.integration.entity.check.ReportLineType
import ru.modulkassa.pos.integration.entity.check.TextReport
import ru.modulkassa.pos.integration.intent.ModulKassaServiceIntent
import ru.modulkassa.pos.integration.service.IModulKassa

class MainActivity : AppCompatActivity() {

    companion object {
        const val PERMISSION_REQUEST = 100
        const val SHIFT_ACTION_REQUEST_CODE = 101
    }

    private var modulkassa: IModulKassa? = null

    private val connection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            modulkassa = null
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            modulkassa = IModulKassa.Stub.asInterface(service)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val btn = findViewById<Button>(R.id.printBtn)

        btn.setOnClickListener {
            val lines = ArrayList<ReportLine>().apply {
                add(ReportLine("     Тестовая печать     ", ReportLineType.TEXT))
                add(ReportLine("     Тестовая печать     ", ReportLineType.QR))
            }
            modulkassa?.let { service ->
                PrintTextAction(TextReport(lines)).execute(service,
                    object : ActionCallback<Boolean> {
                        override fun failed(message: String, extra: Map<String, Any>?) {
                            //TODO
                        }

                        override fun succeed(result: Boolean?) {
                            //TODO
                        }
                    })
            }
        }
        checkPermissions()
        connectToService()

        startActivityForResult(
            App.instance.modulKassaClient.shiftManager()
                .createOpenShiftIntent(Employee(name = "Иванов Иван")),
            SHIFT_ACTION_REQUEST_CODE
        )
    }

    private fun connectToService() {
        val serviceIntent = ModulKassaServiceIntent()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }
        bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            SHIFT_ACTION_REQUEST_CODE -> handleShiftActionAnswer(resultCode, data)
        }
    }

    private fun handleShiftActionAnswer(resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            //TODO
        } else {
            val resultError = App.instance.modulKassaClient.shiftManager()
                .parseShiftActionResult(data ?: Intent())
            //TODO
        }
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
        if(listPermissionsNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                listPermissionsNeeded.toTypedArray(),
                PERMISSION_REQUEST
            )
        }
    }
}