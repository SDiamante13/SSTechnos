package biz.sstechnos.employeedashboard.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import org.koin.dsl.module.module

val dataModule = module {
    single("firebaseAuth") { FirebaseAuth.getInstance() }
    single("databaseReference") { FirebaseDatabase.getInstance().reference }
}