package biz.sstechnos.employeedashboard.admin

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import biz.sstechnos.employeedashboard.R
import biz.sstechnos.employeedashboard.entity.Employee
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_employee_listings.*

class EmployeeListingsActivity : AppCompatActivity() {

    var employeeNameList : MutableList<String> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_employee_listings)
        supportActionBar?.title = "Employee Listing"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        var employeeList = retrieveEmployeeList()

        for(employee in employeeList) {
            employeeNameList.add("${employee.employeeId} ${employee.firstName} ${employee.lastName}")
        }

        // Creates a vertical Layout Manager
        employee_list_view.layoutManager = LinearLayoutManager(baseContext)

        // Access the RecyclerView Adapter and load the data into it
        employee_list_view.adapter = EmployeeAdapter(employeeNameList, baseContext)


        add_employee.setOnClickListener {
            startActivity(Intent(this, AddEmployeeActivity::class.java))
        }
    }

    private fun retrieveEmployeeList(): MutableList<Employee> {
        var employeeListJson = getSharedPreferences("EmployeeList", MODE_PRIVATE)
            .getString("employeeList", " ")
        return Gson().fromJson(employeeListJson, object : TypeToken<MutableList<Employee>>() {}.type)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        onBackPressed()
        return true
    }
}
