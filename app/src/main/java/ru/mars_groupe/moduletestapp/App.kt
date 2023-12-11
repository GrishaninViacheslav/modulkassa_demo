package ru.mars_groupe.moduletestapp

import android.app.Application
import ru.modulkassa.pos.integration.ModulKassaClient

class App:Application() {
    companion object {
        lateinit var instance: App
    }

    lateinit var modulKassaClient: ModulKassaClient

    override fun onCreate() {
        super.onCreate()
        instance = this
        modulKassaClient = ModulKassaClient(this)
    }
}