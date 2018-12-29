package biz.sstechnos.employeedashboard.dashboard

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View.GONE
import android.view.View.VISIBLE
import biz.sstechnos.employeedashboard.R
import biz.sstechnos.employeedashboard.admin.EmployeeListingsActivity
import biz.sstechnos.employeedashboard.employee.TimeSheetActivity
import biz.sstechnos.employeedashboard.entity.Employee
import biz.sstechnos.employeedashboard.entity.Role
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.progress_spinner.*
import org.koin.android.ext.android.inject

class DashboardActivity : AppCompatActivity() {

    private val auth : FirebaseAuth by inject()
    private val databaseReference : DatabaseReference by inject()

    var employeeList = mutableListOf<Employee>()

    private lateinit var user: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        supportActionBar?.title = "SSTechnos - Home"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        progress_spinner.visibility = VISIBLE

        user = auth.currentUser!!

        loadAllEmployees()

        progress_spinner.visibility = GONE

        //   Admin Buttons
        employee_listings_container.setOnClickListener {
            startActivity(Intent(this, EmployeeListingsActivity::class.java))
            progress_spinner.visibility = VISIBLE
        }

        view_timesheets_container.setOnClickListener {

        }

        upload_documents_container.setOnClickListener {

        }

        // Employee Buttons
        view_documents_container.setOnClickListener {

        }

        enter_timesheets_container.setOnClickListener {
            startActivity(Intent(this@DashboardActivity, TimeSheetActivity::class.java))
            progress_spinner.visibility = VISIBLE
        }

        project_listings_container.setOnClickListener {

        }
    }

    private fun loadAllEmployees() {
        val employeeListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var employeeId = "UNDEFINED"
                var role = Role.EMPLOYEE
                var firstName = ""
                var lastName = ""

                dataSnapshot.children.forEach { singleSnapshot ->
                    var employeeSnapshot = singleSnapshot.getValue<Employee>(Employee::class.java)!!
                    employeeList.add(employeeSnapshot)

                    if (user.email.equals(employeeSnapshot.username)) {
                        employeeId = employeeSnapshot.employeeId
                        firstName = employeeSnapshot.firstName
                        lastName = employeeSnapshot.lastName
                        role = employeeSnapshot.role
                    }
                }
                if (employeeId.isNotEmpty()) {
                    if (role == Role.EMPLOYEE) {
                        employee_listings_container.visibility = GONE
                        view_timesheets_container.visibility = GONE
                        upload_documents_container.visibility = GONE
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
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Employee failed, log a message
                Log.w("SSTechnos", "loadEmployee:onCancelled", databaseError.toException())
            }
        }
        databaseReference.child("employees").addValueEventListener(employeeListener)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.settings -> return true
            R.id.contact_info -> startActivity(Intent(this, ViewContactInfoActivity::class.java))
            else -> onBackPressed()
        }
        return true
    }
}
