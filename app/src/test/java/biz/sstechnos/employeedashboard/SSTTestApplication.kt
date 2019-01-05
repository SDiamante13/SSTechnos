package biz.sstechnos.employeedashboard

import android.app.Application
import org.koin.android.ext.android.startKoin

class SSTTestApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin(this, emptyList())
    }
}