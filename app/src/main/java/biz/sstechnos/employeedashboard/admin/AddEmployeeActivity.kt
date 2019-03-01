package biz.sstechnos.employeedashboard.admin

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.widget.AdapterView
import android.widget.ArrayAdapter
import biz.sstechnos.employeedashboard.R
import biz.sstechnos.employeedashboard.entity.Employee
import biz.sstechnos.employeedashboard.entity.Role
import biz.sstechnos.employeedashboard.utils.CookieBarUtil
import com.google.firebase.database.DatabaseReference
import kotlinx.android.synthetic.main.activity_view_employee.*
import kotlinx.android.synthetic.main.layout_contact_info.*
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

        contact_info_layout.visibility = GONE
        image_button_timesheet.visibility = GONE

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
        view_role_spinner!!.onItemSelectedListener = this
        // Create an ArrayAdapter using a simple spinner layout and array
        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, roles)
        // Set layout to use when the list of choices appear
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Set Adapter to Spinner
        view_role_spinner!!.adapter = arrayAdapter
    }

    private fun addEmployee() {
        val employee =  Employee(view_employee_id_edit_text.text.toString(),
            view_first_name_edit_text.text.toString(),
            view_last_name_edit_text.text.toString(),
            view_dob_edit_text.text.toString(),
            selectedRole,
            view_job_title_edit_text.text.toString(),
            view_salary_edit_text.text.toString(), "")

        databaseReference.child("employees").child(employee.employeeId).child("Employee").setValue(employee)
            .addOnSuccessListener { Log.d("SSTechnos", "Employee saved successfully to the database.") }
            .addOnFailureListener { Log.d("SSTechnos", "" + it.message) }
    }

    private fun allFieldsValid() : Boolean {
        return  view_employee_id_edit_text.text.toString().isNotEmpty() &&
                view_first_name_edit_text.text.toString().isNotEmpty() &&
                view_last_name_edit_text.text.toString().isNotEmpty() &&
                view_dob_edit_text.text.toString().isNotEmpty() &&
                view_job_title_edit_text.text.toString().isNotEmpty() &&
                view_salary_edit_text.text.toString().isNotEmpty()
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
