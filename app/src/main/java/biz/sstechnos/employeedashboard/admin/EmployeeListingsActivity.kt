package biz.sstechnos.employeedashboard.admin

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.MenuItem
import biz.sstechnos.employeedashboard.R
import biz.sstechnos.employeedashboard.entity.Employee
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_employee_listings.*

class EmployeeListingsActivity : AppCompatActivity() {

    private val databaseReference = FirebaseDatabase.getInstance().reference

    var employeeNameList : MutableList<String> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_employee_listings)
        supportActionBar?.title = "Employee Listing"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        loadEmployeeListings()

        add_employee.setOnClickListener {
            startActivity(Intent(this, AddEmployeeActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()

    }

    private fun loadEmployeeListings() {
        val employeeListingsListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var count = 0
                employeeNameList.clear()
                // TODO: How can I get employeeList over to on the Adapter onclick
                dataSnapshot.children.forEach { singleSnapshot ->
                    var employeeSnapshot = singleSnapshot.getValue<Employee>(Employee::class.java)!!
                    var employeeIdFullName = "${employeeSnapshot.employeeId} ${employeeSnapshot.firstName} ${employeeSnapshot.lastName}"
                    employeeNameList.add(employeeIdFullName)
                    Log.d("SSTechnos", "Employee Retrieved: $employeeIdFullName")
                    count++
                }
                // Creates a vertical Layout Manager
                employee_list_view.layoutManager = LinearLayoutManager(baseContext)

                // Access the RecyclerView Adapter and load the data into it
                employee_list_view.adapter = EmployeeAdapter(employeeNameList, baseContext)
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Employee failed, log a message
                Log.w("SSTechnos", "loadPost:onCancelled", databaseError.toException())
            }
        }
        databaseReference.child("employees").addListenerForSingleValueEvent(employeeListingsListener)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        onBackPressed()
        return true
    }
}
