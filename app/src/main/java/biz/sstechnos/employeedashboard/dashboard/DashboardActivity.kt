package biz.sstechnos.employeedashboard.dashboard

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import biz.sstechnos.employeedashboard.R
import biz.sstechnos.employeedashboard.admin.EmployeeListingsActivity
import biz.sstechnos.employeedashboard.employee.TimeSheetActivity
import biz.sstechnos.employeedashboard.entity.Employee
import biz.sstechnos.employeedashboard.entity.Role
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.progress_spinner.*

class DashboardActivity : AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()
    private val databaseReference = FirebaseDatabase.getInstance().reference

    private var employeeList: MutableList<Employee> = mutableListOf()

    private lateinit var user: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        supportActionBar?.title = "SSTechnos - Home"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        user = auth.currentUser!!

        loadAllEmployees()

        //   Admin Buttons
        employee_listings_container.setOnClickListener {
            startActivity(Intent(this, EmployeeListingsActivity::class.java))
            progress_spinner.visibility = View.VISIBLE
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
            progress_spinner.visibility = View.VISIBLE
        }

        project_listings_container.setOnClickListener {

        }
    }

    private fun loadAllEmployees() {
        val employeeListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var employeeId = "UNDEFINED"
                var role = Role.EMPLOYEE
                employeeList.clear()
                dataSnapshot.children.forEach { singleSnapshot ->
                    var employeeSnapshot = singleSnapshot.getValue<Employee>(Employee::class.java)!!
                    employeeList.add(employeeSnapshot)
                    if (user.email.equals(employeeSnapshot.username)) {
                        employeeId = employeeSnapshot.employeeId
                        role = employeeSnapshot.role
                    }
                }
                if (employeeId.isNotEmpty()) {
                    getSharedPreferences("Employee", MODE_PRIVATE)
                        .edit()
                        .putString("employeeId", employeeId)
                        .apply()

                    if (role == Role.EMPLOYEE) {
                        employee_listings_container.visibility = GONE
                        view_timesheets_container.visibility = GONE
                        upload_documents_container.visibility = GONE
                    }

                    Log.d("SSTechnos", "Employee Id: $employeeId $role")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Employee failed, log a message
                Log.w("SSTechnos", "loadEmployee:onCancelled", databaseError.toException())
            }
        }
        databaseReference.child("employees").addListenerForSingleValueEvent(employeeListener)
    }

    override fun onResume() {
        super.onResume()
        progress_spinner.visibility = GONE
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
