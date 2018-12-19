package biz.sstechnos.employeedashboard.registration

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import biz.sstechnos.employeedashboard.R
import biz.sstechnos.employeedashboard.entity.Employee
import biz.sstechnos.employeedashboard.utils.CookieBarUtil
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_registration.*


class RegistrationActivity : AppCompatActivity() {

    val handler : Handler = Handler()

    private val databaseReference = FirebaseDatabase.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        supportActionBar?.title = "Registration"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        validate_button.setOnClickListener {

            if(employeeId_editText.text.toString().isEmpty() ||
                lastName_editText.text.toString().isEmpty() ||
                dateOfBirth_editText.text.toString().isEmpty()) {
                CookieBarUtil.makeCookie(this@RegistrationActivity,
                    "Employee information required!",
                    "Please fill out all requested fields.").show()
            } else {
                Log.d("SSTechnos", "Employee: ${employeeId_editText.text} ${lastName_editText.text} ${dateOfBirth_editText.text}")
                verifyEmployeeInfo(employeeId_editText.text.toString(), lastName_editText.text.toString(), dateOfBirth_editText.text.toString())
            }
        }


    }

    private fun verifyEmployeeInfo(employeeId: String, lastName: String, dateOfBirth: String) {
        val verifyEmployeeListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var employee = dataSnapshot.child("employees").child(employeeId).getValue(Employee::class.java)!!
                Log.d("SSTechnos", "Employee: ${employee.employeeId} ${employee.lastName} ${employee.dateOfBirth}")
                if(employee.lastName == lastName && employee.dateOfBirth == dateOfBirth) {
                    getSharedPreferences("Employee", MODE_PRIVATE)
                        .edit()
                        .putString("employeeId", employeeId)
                        .apply()
                    CookieBarUtil.makeCookie(this@RegistrationActivity,
                        "Account successfully linked!",
                        "Your account has been verified." +
                        " Please proceed to the next account creation form.").show()
                    handler.postDelayed({ startActivity(Intent(this@RegistrationActivity, ContactInfoActivity::class.java)) }, 7000)
                } else {
                    CookieBarUtil.makeCookie(this@RegistrationActivity,
                        "Invalid Account!",
                        "Employee account does not exist." +
                                " Please try again or consult with your manager.").show()
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Employee failed, log a message
                Log.w("SSTechnos", "loadPost:onCancelled", databaseError.toException())
            }
        }
        databaseReference.addValueEventListener(verifyEmployeeListener)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacksAndMessages(null)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        onBackPressed()
        return true
    }
}
