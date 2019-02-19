package biz.sstechnos.employeedashboard.registration

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import biz.sstechnos.employeedashboard.R
import biz.sstechnos.employeedashboard.entity.Employee
import biz.sstechnos.employeedashboard.utils.CookieBarUtil
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_registration.*
import org.koin.android.ext.android.inject


class RegistrationActivity : AppCompatActivity(), ValueEventListener {

    private val databaseReference: DatabaseReference by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        supportActionBar?.title = "Registration"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        validate_button.setOnClickListener {

            if (employeeId_editText.text.toString().isEmpty() ||
                lastName_editText.text.toString().isEmpty() ||
                dateOfBirth_editText.text.toString().isEmpty()
            ) {
                CookieBarUtil.makeCookie(
                    this@RegistrationActivity,
                    "Employee information required!",
                    "Please fill out all requested fields."
                ).show()
            } else {
                Log.d(
                    "SSTechnos",
                    "Employee: ${employeeId_editText.text} ${lastName_editText.text} ${dateOfBirth_editText.text}"
                )
                databaseReference.addValueEventListener(this)
            }
        }
    }

    override fun onDataChange(dataSnapshot: DataSnapshot?) {
        val employeeId = employeeId_editText.text.toString()
        val lastName = lastName_editText.text.toString()
        val dateOfBirth = dateOfBirth_editText.text.toString()

        if (dataSnapshot != null) {
            var employee: Employee? = dataSnapshot.child("employees").child(employeeId).getValue(Employee::class.java)
            if (employee != null) {
                Log.d("SSTechnos", "Employee: ${employee.employeeId} ${employee.lastName} ${employee.dateOfBirth}")

                if (employee.lastName == lastName && employee.dateOfBirth == dateOfBirth) {
                    getSharedPreferences("Employee", MODE_PRIVATE)
                        .edit()
                        .putString("employeeId", employeeId)
                        .apply()

                        startActivity(Intent(this@RegistrationActivity, ContactInfoActivity::class.java))
                } else {
                    CookieBarUtil.makeCookie(
                        this@RegistrationActivity,
                        "Invalid Account!",
                        "Employee account does not exist." +
                                " Please try again or consult with your manager."
                    ).show()
                }
            }
        }
    }

    override fun onCancelled(databaseError: DatabaseError) {
        // Getting Employee failed, log a message
        Log.w("SSTechnos", "loadPost:onCancelled", databaseError.toException())
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        onBackPressed()
        return true
    }
}
