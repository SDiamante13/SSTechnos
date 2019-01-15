package biz.sstechnos.employeedashboard

import android.app.Application
import biz.sstechnos.employeedashboard.di.dataModule
import org.koin.android.ext.android.startKoin


open class SSTApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin(this, listOf(dataModule))
    }

}