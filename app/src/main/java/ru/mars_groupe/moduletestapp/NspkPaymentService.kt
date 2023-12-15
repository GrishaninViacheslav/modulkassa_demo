package ru.mars_groupe.moduletestapp

import android.content.Intent
import android.util.Log
import ru.modulkassa.pos.integration.PluginServiceCallbackHolder
import ru.modulkassa.pos.integration.core.OperationHandler
import ru.modulkassa.pos.integration.core.PluginService
import ru.modulkassa.pos.integration.core.handler.CancelOperationHandler
import ru.modulkassa.pos.integration.core.handler.PayOperationHandler
import ru.modulkassa.pos.integration.core.handler.ReconciliationOperationHandler
import ru.modulkassa.pos.integration.core.handler.RefundOperationHandler
import ru.modulkassa.pos.integration.entity.payment.CancelRequest
import ru.modulkassa.pos.integration.entity.payment.CancelResult
import ru.modulkassa.pos.integration.entity.payment.PayRequest
import ru.modulkassa.pos.integration.entity.payment.ReconciliationResult
import ru.modulkassa.pos.integration.entity.payment.RefundRequest
import ru.modulkassa.pos.integration.entity.payment.RefundResult
import kotlin.concurrent.thread

class NspkPaymentService : PluginService() {
    override fun createHandlers(): List<OperationHandler> {
        return listOf(
            object : PayOperationHandler() {
                override fun handlePay(
                    payRequest: PayRequest,
                    callback: PluginServiceCallbackHolder
                ) {
                    Log.d("DEBUG", "NspkPaymentService handlePay(payRequest = $payRequest)")
                    val payIntent = Intent(applicationContext, PayActivity::class.java)
                    callback.putToIntent(payIntent)
                    payIntent.putExtra(PayActivity.KEY_DATA, payRequest.toBundle())
                    payIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(payIntent)
                }
            },
            /**
             * Возврат
             */
            object : RefundOperationHandler() {
                override fun handleRefund(
                    refundRequest: RefundRequest,
                    callback: PluginServiceCallbackHolder
                ) {
                    callback.get().succeeded(RefundResult(listOf()).toBundle())
                }
            },
            object : CancelOperationHandler() {
                override fun handleCancel(
                    cancelRequest: CancelRequest,
                    callback: PluginServiceCallbackHolder
                ) {
                    thread() {
                        Thread.sleep(5_000)
                        callback.get().succeeded(CancelResult(listOf()).toBundle())
                    }
                }
            },
            object : ReconciliationOperationHandler() {
                override fun handleReconciliation(callback: PluginServiceCallbackHolder) {
                    callback.get().succeeded(ReconciliationResult(listOf()).toBundle())
                }
            }
        )
    }
}