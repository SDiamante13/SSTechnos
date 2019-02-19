package biz.sstechnos.employeedashboard.admin

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import biz.sstechnos.employeedashboard.R
import biz.sstechnos.employeedashboard.entity.Employee
import biz.sstechnos.employeedashboard.entity.Role
import biz.sstechnos.employeedashboard.utils.CookieBarUtil
import com.google.firebase.database.DatabaseReference
import kotlinx.android.synthetic.main.activity_view_employee.*
import org.koin.android.ext.android.inject


class AddEmployeeActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private val databaseReference : DatabaseReference by inject()

    private var roles = arrayOf(Role.ADMIN, Role.EMPLOYEE)

    private var selectedRole = Role.ADMIN

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_employee)
        supportActionBar?.title = "Add Employee"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setUpSpinnerAdapter()
        add_employee_button.setOnClickListener {
            if(allFieldsValid()) {
                addEmployee()
                CookieBarUtil.makeCookie(this@AddEmployeeActivity, //TODO remove if too fast or change to toast
                    "Employee Added!",
                    "You've added a new employee to the database.").show()
                finish()
            } else {
                CookieBarUtil.makeCookie(this@AddEmployeeActivity,
                    "Missing Fields!",
                    "Please fill out all required fields.").show()
            }
        }
    }

    private fun setUpSpinnerAdapter() {
        roleSpinner!!.onItemSelectedListener = this
        // Create an ArrayAdapter using a simple spinner layout and array
        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, roles)
        // Set layout to use when the list of choices appear
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Set Adapter to Spinner
        roleSpinner!!.adapter = arrayAdapter
    }

    private fun addEmployee() {
        val employee =  Employee(viewEmployeeId_editText.text.toString(),
            viewFirstName_editText.text.toString(),
            viewLastName_editText.text.toString(),
            viewDOB_editText.text.toString(),
            selectedRole,
            viewJobTitle_editText.text.toString(),
            viewSalary_editText.text.toString(), "")

        databaseReference.child("employees").child(employee.employeeId).setValue(employee)
            .addOnSuccessListener { Log.d("SSTechnos", "Employee saved successfully to the database.") }
            .addOnFailureListener { Log.d("SSTechnos", "" + it.message) }
    }

    private fun allFieldsValid() : Boolean {
        return  viewEmployeeId_editText.text.toString().isNotEmpty() &&
                viewFirstName_editText.text.toString().isNotEmpty() &&
                viewLastName_editText.text.toString().isNotEmpty() &&
                viewDOB_editText.text.toString().isNotEmpty() &&
                viewJobTitle_editText.text.toString().isNotEmpty() &&
                viewSalary_editText.text.toString().isNotEmpty()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        selectedRole = if (position == 1) Role.ADMIN else Role.EMPLOYEE
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        onBackPressed()
        return true
    }
}
