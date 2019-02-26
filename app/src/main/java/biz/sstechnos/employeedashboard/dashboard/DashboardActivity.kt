package biz.sstechnos.employeedashboard.dashboard

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.RelativeLayout
import android.widget.Toast
import biz.sstechnos.employeedashboard.R
import biz.sstechnos.employeedashboard.admin.EmployeeListingsActivity
import biz.sstechnos.employeedashboard.admin.upload.UploadDocumentsActivity
import biz.sstechnos.employeedashboard.employee.TimeSheetActivity
import biz.sstechnos.employeedashboard.employee.ViewDocumentsActivity
import biz.sstechnos.employeedashboard.entity.Employee
import biz.sstechnos.employeedashboard.entity.Role
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.progress_spinner.*
import org.koin.android.ext.android.inject

class DashboardActivity : AppCompatActivity(), ValueEventListener {

    private val databaseReference: DatabaseReference by inject()

    var employeeList = mutableListOf<Employee>()

    lateinit var userEmail : String

    lateinit var employeeListingsContainer : RelativeLayout
    lateinit var viewTimeSheetsContainer : RelativeLayout
    lateinit var uploadDocumentsContainer : RelativeLayout
    lateinit var enterTimesheetsContainer : RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        supportActionBar?.title = "SSTechnos - Home"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        userEmail = intent.getStringExtra("USER_EMAIL")

        progress_spinner.visibility = VISIBLE
        top_view.visibility = GONE

        employeeListingsContainer = employee_listings_container
        viewTimeSheetsContainer = view_timesheets_container
        uploadDocumentsContainer = upload_documents_container
        enterTimesheetsContainer = enter_timesheets_container

        databaseReference.child("employees").addValueEventListener(this)

        //   Admin Buttons
        employee_listings_container.setOnClickListener {
            startActivity(Intent(this, EmployeeListingsActivity::class.java))
        }

        view_timesheets_container.setOnClickListener {
            Toast.makeText(this@DashboardActivity, "Not yet implemented", Toast.LENGTH_SHORT).show()
        }

        upload_documents_container.setOnClickListener {
            startActivity(Intent(this@DashboardActivity, UploadDocumentsActivity::class.java))
        }

        // Employee Buttons
        view_documents_container.setOnClickListener {
            startActivity(Intent(this@DashboardActivity, ViewDocumentsActivity::class.java))
        }

        enter_timesheets_container.setOnClickListener {
            startActivity(Intent(this@DashboardActivity, TimeSheetActivity::class.java))
        }

        project_listings_container.setOnClickListener {
            Toast.makeText(this@DashboardActivity, "Not yet implemented", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDataChange(dataSnapshot : DataSnapshot?) {
        var employeeId = "UNDEFINED"
        var role = Role.EMPLOYEE
        var firstName = ""
        var lastName = ""

        for(singleSnapshot in dataSnapshot!!.children) {
            var employeeSnapshot = singleSnapshot.getValue<Employee>(Employee::class.java)!!
            employeeList.add(employeeSnapshot)

            if (userEmail.equals(employeeSnapshot.username)) {
                employeeId = employeeSnapshot.employeeId
                firstName = employeeSnapshot.firstName
                lastName = employeeSnapshot.lastName
                role = employeeSnapshot.role
            }
        }
        if (employeeId.isNotEmpty()) {
            if (role == Role.EMPLOYEE) {
                employeeListingsContainer.visibility = GONE
                viewTimeSheetsContainer.visibility = GONE
                uploadDocumentsContainer.visibility = GONE
            }

            name_header.text = "$firstName $lastName"

            getSharedPreferences("Employee", MODE_PRIVATE)
                .edit()
                .putString("employeeId", employeeId)
                .apply()

            Log.d("SSTechnos", "Employee: $firstName $lastName $employeeId $role")

            val jsonEmployeeList = Gson().toJson(employeeList)

            getSharedPreferences("EmployeeList", MODE_PRIVATE)
                .edit()
                .putString("employeeList", jsonEmployeeList)
                .apply()

            progress_spinner.visibility = GONE
            top_view.visibility = VISIBLE
        }
    }

    override fun onCancelled(databaseError : DatabaseError?) {
        // Getting Employee failed, log a message
        progress_spinner.visibility = GONE
        top_view.visibility = VISIBLE
        Log.w("SSTechnos", "loadEmployee:onCancelled", databaseError?.toException())
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.settings -> Toast.makeText(this@DashboardActivity, "Setting not implemented", Toast.LENGTH_SHORT).show()
            R.id.log_out -> finish()
            else -> onBackPressed()
        }
        return true
    }
}
