package biz.sstechnos.employeedashboard

import android.app.Application
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import org.koin.android.ext.android.startKoin
import org.koin.dsl.module.module


class SSTApplication : Application() {

    val dataModule = module {
        single { FirebaseAuth.getInstance() }
        single { FirebaseDatabase.getInstance().reference }
    }

    override fun onCreate() {
        super.onCreate()
        startKoin(this, listOf(dataModule))
    }

}