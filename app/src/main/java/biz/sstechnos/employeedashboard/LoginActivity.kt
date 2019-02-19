package biz.sstechnos.employeedashboard

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.InputType
import android.util.Log
import android.widget.EditText
import biz.sstechnos.employeedashboard.dashboard.DashboardActivity
import biz.sstechnos.employeedashboard.registration.RegistrationActivity
import biz.sstechnos.employeedashboard.utils.CookieBarUtil
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_login.*
import org.koin.android.ext.android.inject


class LoginActivity : AppCompatActivity(), OnCompleteListener<AuthResult> {

    private val auth : FirebaseAuth by inject(name = "fireBaseAuth")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.hide()

        signIn_button.setOnClickListener {
            if(username_editText.text.toString().isEmpty() || password_editText.text.toString().isEmpty()){
                CookieBarUtil.makeCookie(this@LoginActivity,
                    "Login attempt failed!",
                    "Username and password required.").show()
            } else {
                auth.signInWithEmailAndPassword(username_editText.text.toString(), password_editText.text.toString()).addOnCompleteListener(this)
            }
        }

        register_button.setOnClickListener {
            startActivity(Intent(this, RegistrationActivity::class.java))
        }
        forgot_password.setOnClickListener {
            val builder = AlertDialog.Builder(this@LoginActivity)
            builder.setTitle("Forgot Password?")
            builder.setMessage("Please enter your email address so we can reset your password.")
            val input = EditText(this@LoginActivity)
            input.inputType = InputType.TYPE_CLASS_TEXT
            builder.setView(input)
            builder.setPositiveButton("Submit") { _, _ ->
                if(input.text.isNotEmpty()) {
                    auth.sendPasswordResetEmail(input.text.toString())
                        .addOnSuccessListener { CookieBarUtil.makeCookie(this@LoginActivity,
                            "Check your email!",
                            "We will reset your password.").show() }
                        .addOnFailureListener { CookieBarUtil.makeCookie(this@LoginActivity,
                            "Invalid email address!",
                            it.message.orEmpty()).show()
                        }
                }
            }

            builder.setNeutralButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }

            builder.create().show()
        }
    }

    override fun onComplete(task: Task<AuthResult>) {
        if(task.isSuccessful) {
            Log.d("SSTechnos", "signInWithEmail:success")
            val user = task.result.user
            if(user!!.isEmailVerified) {
                startActivity(Intent(this, DashboardActivity::class.java).putExtra("USER_EMAIL", user.email))
            } else {
                CookieBarUtil.makeCookie(this@LoginActivity,
                    "Login attempt unsuccessful!",
                    "Email is not verified.").show()
            }
        } else {
            Log.w("SSTechnos", "signInWithEmail:failure", task.exception)
            CookieBarUtil.makeCookie(this@LoginActivity,
                "Login attempt failed!",
                "Please provide the correct credentials.").show()
        }
    }
}
