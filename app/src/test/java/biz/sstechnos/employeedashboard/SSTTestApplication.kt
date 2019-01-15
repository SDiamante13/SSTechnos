package biz.sstechnos.employeedashboard

import biz.sstechnos.employeedashboard.testDI.dataTestModule
import org.koin.android.ext.android.startKoin

class SSTTestApplication : SSTApplication() {

    override fun onCreate() {
        super.onCreate()
        startKoin(this, listOf(dataTestModule))
    }
}