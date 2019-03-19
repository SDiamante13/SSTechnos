package biz.sstechnos.employeedashboard.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import org.koin.dsl.module.module

val dataModule = module {
    single("firebaseAuth") { FirebaseAuth.getInstance() }
    single("databaseReference") { FirebaseDatabase.getInstance().reference }
    single("storageReference") { FirebaseStorage.getInstance().reference }
}