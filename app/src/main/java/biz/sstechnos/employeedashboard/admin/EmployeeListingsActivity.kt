package biz.sstechnos.employeedashboard.admin

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.MenuItem
import biz.sstechnos.employeedashboard.R
import biz.sstechnos.employeedashboard.employee.DocumentAdapter
import biz.sstechnos.employeedashboard.entity.Employee
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_employee_listings.*
import org.koin.android.ext.android.inject

class EmployeeListingsActivity : AppCompatActivity(), ValueEventListener {

    private val databaseReference : DatabaseReference by inject()

    private lateinit var employeeAdapter: EmployeeAdapter

    var employeeNameList : MutableList<String> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_employee_listings)
        supportActionBar?.title = "Employee Listing"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        databaseReference.child("employees").addValueEventListener(this)

        // Creates a vertical Layout Manager
        employee_list_view.layoutManager = LinearLayoutManager(baseContext)

        add_employee.setOnClickListener {
            startActivity(Intent(this, AddEmployeeActivity::class.java))
        }
    }

    override fun onDataChange(employees: DataSnapshot) {
        var accountStatus: String
        employeeNameList.clear()
        for (singleSnapshot in employees.children) {
            accountStatus = "ACTIVE"
            var employeeSnapshot = singleSnapshot.getValue<Employee>(Employee::class.java)!!
            if(employeeSnapshot.username.equals("")) {
                accountStatus = "INACTIVE"
            }
            employeeNameList.add("${employeeSnapshot.employeeId} ${employeeSnapshot.firstName} ${employeeSnapshot.lastName} $accountStatus")
        }
        // TODO consider adding a green checkmark for employees who have created accounts

        employeeAdapter = EmployeeAdapter(employeeNameList)
        employee_list_view.adapter = employeeAdapter
    }

    override fun onCancelled(databaseError: DatabaseError) {
        Log.w("SSTechnos", "loadEmployee:onCancelled", databaseError.toException())
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        onBackPressed()
        return true
    }
}
