package biz.sstechnos.employeedashboard.registration

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.util.Log
import biz.sstechnos.employeedashboard.LoginActivity
import biz.sstechnos.employeedashboard.R
import biz.sstechnos.employeedashboard.utils.CookieBarUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_account_creation.*


class AccountCreationActivity : AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()
    private val databaseReference = FirebaseDatabase.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_creation)
        supportActionBar?.title = "Account Creation"

        create_button.setOnClickListener {
            if (username_editText.text.toString().isEmpty() ||
                password_editText.text.toString().isEmpty() ||
                confirmPassword_editText.text.toString().isEmpty()
            ) {
                CookieBarUtil.makeCookie(
                    this@AccountCreationActivity,
                    "Account information required!",
                    "Please fill out all requested fields.").show()
            } else {
                createUserAccount(username_editText.text.toString(), password_editText.text.toString())
                Handler().postDelayed({ startActivity(Intent(this, LoginActivity::class.java)) }, 7000)
            }
        }
    }

    private fun createUserAccount(username: String, password: String) {
        auth.createUserWithEmailAndPassword(username, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d("SSTechnos", "createUserWithEmail:success")
                    val user = auth.currentUser
                    val employeeId = getSharedPreferences("Employee", MODE_PRIVATE)
                        .getString("employeeId", " ")
                    databaseReference.child("employees").child(employeeId).child("username").setValue(user?.email)
                        .addOnSuccessListener { Log.d("SSTechnos", "Employee email added to database.") }
                        .addOnFailureListener { Log.d("SSTechnos", "" + it.message) }
                    user?.sendEmailVerification()
                        ?.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Log.d("SSTechnos", "Email sent.")
                                CookieBarUtil.makeCookie(this@AccountCreationActivity,
                                    "Verification Email Sent!",
                                    "Please check your email inbox at: ${user.email}").show()
                            }
                        }
                } else {
                    Log.w("SSTechnos", "createUserWithEmail:failure", task.exception)
                }
            }
    }

    override fun onResume() {
        super.onResume()
    }
}
