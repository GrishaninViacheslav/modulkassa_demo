package ru.mars_groupe.moduletestapp

import android.content.ComponentName
import android.content.Context
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ru.mars_groupe.moduletestapp.databinding.ActivityMainBinding
import ru.modulkassa.pos.integration.core.action.ActionCallback
import ru.modulkassa.pos.integration.core.action.GetCheckInfoAction
import ru.modulkassa.pos.integration.entity.check.Check
import ru.modulkassa.pos.integration.entity.check.CheckInfoRequest
import ru.modulkassa.pos.integration.entity.payment.PayRequest
import ru.modulkassa.pos.integration.intent.ModulKassaServiceIntent
import ru.modulkassa.pos.integration.service.IModulKassa

class PayActivity : AppCompatActivity() {

    /**
     * Как при оплате получить уникальные коды товаров из текущей корзины?
     */
    private fun getBasketInventCodes() {
        val payRequest = PayRequest.fromBundle(
            intent.getBundleExtra(KEY_DATA) ?: throw RuntimeException("Не передан PayRequest")
        )
        Log.d("DEBUG", "MainActivity payRequest = $payRequest")
        GetCheckInfoAction(
            CheckInfoRequest(checkId = payRequest.checkId)
        ).execute(
            modulkassa ?: throw RuntimeException("modulkassa is null"),
            object : ActionCallback<Check> {
                override fun succeed(result: Check?) {
                    runOnUiThread {
                        Log.d("DEBUG", "GetCheckInfoAction succeed(result = $result)")
                        Toast.makeText(this@PayActivity, "Check Info: $result", Toast.LENGTH_LONG)
                            .show()
                        binding.message.text = "Check Info: $result"
                    }
                }

                override fun failed(message: String, extra: Map<String, Any>?) {
                    runOnUiThread {
                        Log.d("DEBUG", "GetCheckInfoAction failed(message = $message)")
                        Toast.makeText(this@PayActivity, "$message", Toast.LENGTH_LONG).show()
                        binding.message.text = message
                    }
                }
            })
    }

    companion object {
        const val KEY_DATA = "data"
    }

    private var modulkassa: IModulKassa? = null

    private val connection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            modulkassa = null
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            modulkassa = IModulKassa.Stub.asInterface(service)
            getBasketInventCodes()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        connectToService()
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

    private lateinit var binding: ActivityMainBinding
}