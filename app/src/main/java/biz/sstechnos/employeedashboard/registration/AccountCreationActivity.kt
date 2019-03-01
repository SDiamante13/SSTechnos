package biz.sstechnos.employeedashboard.registration

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import biz.sstechnos.employeedashboard.LoginActivity
import biz.sstechnos.employeedashboard.R
import biz.sstechnos.employeedashboard.utils.CookieBarUtil
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import kotlinx.android.synthetic.main.activity_account_creation.*
import org.koin.android.ext.android.inject


class AccountCreationActivity : AppCompatActivity(), OnCompleteListener<AuthResult>, OnSuccessListener<Void> {

    private val auth: FirebaseAuth by inject()
    private val databaseReference: DatabaseReference by inject()

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
                    "Please fill out all requested fields."
                ).show()
            } else if (password_editText.text.toString() != confirmPassword_editText.text.toString()) {
                CookieBarUtil.makeCookie(
                    this@AccountCreationActivity,
                    "Passwords do not match!",
                    "Please confirm your password correctly."
                ).show()
            } else {
                val username = username_editText.text.toString()
                val password = password_editText.text.toString()
                auth.createUserWithEmailAndPassword(username, password).addOnCompleteListener(this)
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
    }

    override fun onComplete(task: Task<AuthResult>) {
        if (task.isSuccessful) {
            Log.d("SSTechnos", "createUserWithEmail:success")
            val user = auth.currentUser!!
            val employeeId = getSharedPreferences("Employee", MODE_PRIVATE)
                .getString("employeeId", " ")

            databaseReference.child("employees").child(employeeId).child("Employee").child("username").setValue(user.email)
                .addOnSuccessListener { Log.d("SSTechnos", "Employee email added to database.") }
                .addOnFailureListener { Log.d("SSTechnos", "" + it.message) }

            user.sendEmailVerification().addOnSuccessListener(this)
        }
    }

    override fun onSuccess(void : Void?) {
        Log.d("SSTechnos", "Email sent.")
        CookieBarUtil.makeCookie(this@AccountCreationActivity,
            "Verification Email Sent!",
            "Please check your email to verify your account.").show()
    }
}
