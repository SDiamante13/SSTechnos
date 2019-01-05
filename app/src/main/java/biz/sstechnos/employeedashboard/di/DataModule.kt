package biz.sstechnos.employeedashboard.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import org.koin.dsl.module.module

val dataModule = module {
    factory("firebaseAuth") { FirebaseAuth.getInstance() }
    factory("databaseReference") { FirebaseDatabase.getInstance().reference }
}