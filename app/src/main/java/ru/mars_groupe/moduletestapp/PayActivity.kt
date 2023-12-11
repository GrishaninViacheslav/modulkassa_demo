package ru.mars_groupe.moduletestapp

import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import ru.mars_groupe.moduletestapp.databinding.ActivityPayBinding
import ru.modulkassa.pos.integration.PluginServiceCallbackHolder
import ru.modulkassa.pos.integration.entity.payment.PayRequest
import ru.modulkassa.pos.integration.entity.payment.PayResult
import java.util.UUID

class PayActivity:AppCompatActivity() {

    companion object {
        const val KEY_DATA = "data"
        const val TIMEOUT: Long = 3000
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityPayBinding.inflate(layoutInflater)
        setContentView(binding.root)

        intent.getBundleExtra(KEY_DATA)?.let {
            val payRequest = PayRequest.fromBundle(it)
            binding.amount.text = payRequest.amount.toString()
            Handler().postDelayed(
                {
                    PluginServiceCallbackHolder.getFromIntent(intent, applicationContext)?.get()?.succeeded(
                        PayResult(UUID.randomUUID().toString(), listOf()).toBundle()
                    )

                    // после завершения обработки нужно закрыть активити
                    finish()
                },
                TIMEOUT
            )
        }
    }
}