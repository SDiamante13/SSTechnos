package biz.sstechnos.employeedashboard.admin

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import biz.sstechnos.employeedashboard.R
import biz.sstechnos.employeedashboard.entity.Employee
import biz.sstechnos.employeedashboard.entity.Role
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_view_employee.*
import kotlinx.android.synthetic.main.progress_spinner.*
import org.koin.android.ext.android.inject


class ViewEmployeeActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private val databaseReference : DatabaseReference by inject()

    private lateinit var employeeId : String
    private var roles = arrayOf(Role.ADMIN, Role.EMPLOYEE)
    private var selectedRole = Role.ADMIN

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_employee)
        supportActionBar?.title = "View Employee"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onResume() {
        super.onResume()
        employeeId = intent.getStringExtra("EMPLOYEE_ID")
        setupLayout()
        setUpSpinnerAdapter()
        readEmployeeFromDatabase()
    }

    private fun setupLayout() {
        viewEmployeeId_label.visibility = View.VISIBLE
        viewFirstName_label.visibility = View.VISIBLE
        viewLastName_label.visibility = View.VISIBLE
        viewDOB_label.visibility = View.VISIBLE
        viewRole_label.visibility = View.VISIBLE
        viewJobTitle_label.visibility = View.VISIBLE
        viewSalary_label.visibility = View.VISIBLE

        viewEmployeeId_editText.isEnabled = false
        viewFirstName_editText.isEnabled = false
        viewLastName_editText.isEnabled = false
        viewDOB_editText.isEnabled = false
        viewJobTitle_editText.isEnabled = false
        viewSalary_editText.isEnabled = false

        add_employee_button.visibility = View.GONE
        edit_employee_button.visibility = View.VISIBLE
        delete_employee_button.visibility = View.VISIBLE
        saveChanges_employee_button.visibility = View.VISIBLE

        delete_employee_button.setOnClickListener {
            val builder = AlertDialog.Builder(this@ViewEmployeeActivity)
            builder.setTitle("Delete Employee")
            builder.setMessage("Are you sure you want to delete this employee from the database?")
            builder.setPositiveButton("YES") { _, _ ->
                deleteEmployee()
                Toast.makeText(this, "This employee has been deleted.", Toast.LENGTH_SHORT).show()
            }

            builder.setNeutralButton("No") { _, _ ->
                Toast.makeText(applicationContext, "This employee will not be deleted.", Toast.LENGTH_SHORT).show()
            }

            builder.create().show()
        }

        edit_employee_button.setOnClickListener {
            viewFirstName_editText.isEnabled = true
            viewLastName_editText.isEnabled = true
            viewDOB_editText.isEnabled = true
            viewJobTitle_editText.isEnabled = true
            viewSalary_editText.isEnabled = true
        }

        saveChanges_employee_button.setOnClickListener {
            val builder = AlertDialog.Builder(this@ViewEmployeeActivity)
            builder.setTitle("Update Employee")
            builder.setMessage("Are you sure you want to update this employee in the database?")
            builder.setPositiveButton("YES") { _, _ ->
                editEmployee()
                Toast.makeText(this, "This employee has been updated.", Toast.LENGTH_SHORT).show()
            }

            builder.setNeutralButton("No") { _, _ ->
                Toast.makeText(applicationContext, "This employee will not be updated.", Toast.LENGTH_SHORT).show()
                finish()
            }

            builder.create().show()
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

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        selectedRole = if (position == 1) Role.ADMIN else Role.EMPLOYEE
    }

    private fun readEmployeeFromDatabase(){
        val viewEmployeeListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.d("SSTechnos", "Reading employee from database")
                    var employeeSnapshot = dataSnapshot.child("employees").child(employeeId).getValue(Employee::class.java)!!
                    populateFieldsWithEmployeeData(employeeSnapshot)
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("SSTechnos", "loadPost:onCancelled", databaseError.toException())
            }

        }
        databaseReference.addValueEventListener(viewEmployeeListener)
    }

    private fun populateFieldsWithEmployeeData(employee: Employee) {
        viewEmployeeId_editText.text = employeeId.toEditable()
        viewFirstName_editText.text = employee.firstName.toEditable()
        viewLastName_editText.text = employee.lastName.toEditable()
        viewDOB_editText.text = employee.dateOfBirth.toEditable()
        selectedRole = employee.role
        viewJobTitle_editText.text = employee.jobTitle.toEditable()
        viewSalary_editText.text = employee.salary.toString().toEditable()
    }

    private fun deleteEmployee() {
        databaseReference.child("employees").child(employeeId).removeValue().addOnSuccessListener { Log.d("SSTechnos", "Employee successfully deleted from the database.") }.addOnFailureListener { Log.d("SSTechnos", "" + it.message) }
    }

    private fun editEmployee() {
        val editedEmployee =  Employee(viewEmployeeId_editText.text.toString(),
            viewFirstName_editText.text.toString(),
            viewLastName_editText.text.toString(),
            viewDOB_editText.text.toString(),
            selectedRole,
            viewJobTitle_editText.text.toString(),
            viewSalary_editText.text.toString().toDouble(), "")
            databaseReference.child("employees").child(employeeId).setValue(editedEmployee).addOnSuccessListener {
            Log.d("SSTechnos", "Employee successfuly updated in the database.") }.addOnFailureListener { Log.d("SSTechnos", "" + it.message) }
    }

    private fun String.toEditable() : Editable = Editable.Factory.getInstance().newEditable(this)

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        onBackPressed()
        return true
    }
}
