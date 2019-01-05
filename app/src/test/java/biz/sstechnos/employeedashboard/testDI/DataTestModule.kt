package biz.sstechnos.employeedashboard.testDI

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import io.mockk.mockk
import org.koin.dsl.module.module

val dataTestModule = module {
    factory("mockFirebaseAuth") { mockk<FirebaseAuth>(relaxed = true) }
    factory("mockDatabaseReference") { mockk<DatabaseReference>(relaxed = true) }
}