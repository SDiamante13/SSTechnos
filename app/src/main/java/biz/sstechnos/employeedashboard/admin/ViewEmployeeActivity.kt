package biz.sstechnos.employeedashboard.admin

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.View.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import biz.sstechnos.employeedashboard.R
import biz.sstechnos.employeedashboard.employee.TimeSheetActivity
import biz.sstechnos.employeedashboard.entity.ContactInfo
import biz.sstechnos.employeedashboard.entity.Employee
import biz.sstechnos.employeedashboard.entity.Role
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_view_employee.*
import kotlinx.android.synthetic.main.layout_contact_info.*
import kotlinx.android.synthetic.main.progress_spinner.*
import org.koin.android.ext.android.inject


class ViewEmployeeActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener, ValueEventListener {

    private val databaseReference: DatabaseReference by inject()

    private lateinit var employeeId: String
    private var accountStatus: String = "ACTIVE"
    private lateinit var arrayAdapter: ArrayAdapter<Role>

    private var roles = arrayOf(Role.ADMIN, Role.EMPLOYEE)
    private var selectedRole = Role.ADMIN

    lateinit var viewEmployeeId: EditText
    lateinit var viewFirstName: EditText
    lateinit var viewLastName: EditText
    lateinit var viewDOB: EditText
    lateinit var viewJobTitle: EditText
    lateinit var viewSalary: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_employee)
        supportActionBar?.title = "View Employee"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val intent: Intent = intent
        employeeId = intent.getStringExtra("EMPLOYEE_ID")
        accountStatus = intent.getStringExtra("ACCOUNT_STATUS")
        setupLayout()
        setUpSpinnerAdapter()
        databaseReference.addValueEventListener(this)

        if(accountStatus.equals("ACTIVE")) readContactInfoFromDatabase(employeeId)

        image_button_timesheet.setOnClickListener {
            startActivity(Intent(this@ViewEmployeeActivity, TimeSheetActivity::class.java)
                .putExtra("EMPLOYEE_ID", employeeId))
        }
    }

    override fun onDataChange(dataSnapshot: DataSnapshot) {
        Log.d("SSTechnos", "Reading employee from database")
        var employeeSnapshot = dataSnapshot.child("employees").child(employeeId).getValue(Employee::class.java)!!
        populateFieldsWithEmployeeData(employeeSnapshot)
    }

    override fun onCancelled(databaseError: DatabaseError?) {
        // Getting Post failed, log a message
        Log.w("SSTechnos", "loadPost:onCancelled", databaseError?.toException())
    }

    private fun setupLayout() {
        viewEmployeeId = viewEmployeeId_editText
        viewFirstName = viewFirstName_editText
        viewLastName = viewLastName_editText
        viewDOB = viewDOB_editText
        viewJobTitle = viewJobTitle_editText
        viewSalary = viewSalary_editText

        viewEmployeeId_label.visibility = VISIBLE
        viewFirstName_label.visibility = VISIBLE
        viewLastName_label.visibility = VISIBLE
        viewDOB_label.visibility = VISIBLE
        viewRole_label.visibility = VISIBLE
        viewJobTitle_label.visibility = VISIBLE
        viewSalary_label.visibility = VISIBLE

        viewEmployeeId.isEnabled = false
        viewFirstName.isEnabled = false
        viewLastName.isEnabled = false
        viewDOB.isEnabled = false
        viewJobTitle.isEnabled = false
        roleSpinner.isEnabled = false
        viewSalary.isEnabled = false

        add_employee_button.visibility = GONE
        edit_employee_button.visibility = VISIBLE
        delete_employee_button.visibility = VISIBLE
        saveChanges_employee_button.visibility = VISIBLE

        delete_employee_button.setOnClickListener {
            val builder = AlertDialog.Builder(this@ViewEmployeeActivity)
            builder.setTitle("Delete Employee")
            builder.setMessage("Are you sure you want to delete this employee from the database?")
            builder.setPositiveButton("YES") { _, _ ->
                deleteEmployee()
                // TODO Consider finishing activity here and don't show toast
                Toast.makeText(this, "This employee has been deleted.", Toast.LENGTH_SHORT).show()
            }

            builder.setNeutralButton("No") { _, _ ->
                Toast.makeText(applicationContext, "This employee will not be deleted.", Toast.LENGTH_SHORT).show()
            }

            builder.create().show()
        }

        edit_employee_button.setOnClickListener {
            viewFirstName.isEnabled = true
            viewLastName.isEnabled = true
            viewDOB.isEnabled = true
            viewJobTitle.isEnabled = true
            roleSpinner.isEnabled = true
            viewSalary.isEnabled = true
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
        arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, roles)
        // Set layout to use when the list of choices appear
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Set Adapter to Spinner
        roleSpinner!!.adapter = arrayAdapter
        arrayAdapter.setNotifyOnChange(true)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        selectedRole = if (position == 1) Role.ADMIN else Role.EMPLOYEE
    }

    private fun populateFieldsWithEmployeeData(employee: Employee) {
        viewEmployeeId.text = employeeId.toEditable()
        viewFirstName.text = employee.firstName.toEditable()
        viewLastName.text = employee.lastName.toEditable()
        viewDOB.text = employee.dateOfBirth.toEditable()

        selectedRole = employee.role
        roleSpinner.setSelection(arrayAdapter.getPosition(selectedRole))

        viewJobTitle.text = employee.jobTitle.toEditable()
        viewSalary.text = employee.salary.toEditable()
    }


    private fun readContactInfoFromDatabase(employeeId: String) {
        val viewContactInfoListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) { //TODO this breaks if the employee has not created an account
                Log.d("SSTechnos", "Reading Employee's contact info from database")
                var contactInfoSnapshot = dataSnapshot.child("employees")
                    .child(employeeId)
                    .child("ContactInfo")
                    .getValue(ContactInfo::class.java)!!
                populateFieldsWithContactInfo(contactInfoSnapshot)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("SSTechnos", "loadPost:onCancelled", databaseError.toException())
            }
        }
        databaseReference.addValueEventListener(viewContactInfoListener)
    }

    private fun populateFieldsWithContactInfo(contactInfoSnapshot: ContactInfo) {
        email_text.text = contactInfoSnapshot.email
        address_text.text = " " + contactInfoSnapshot.address.streetAddress + " " +
                contactInfoSnapshot.address.city + ", " +
                contactInfoSnapshot.address.state + " " + contactInfoSnapshot.address.country + " " +
                contactInfoSnapshot.address.zipCode
        phoneNumber_text.text = contactInfoSnapshot.phoneNumber
        emergencyContact_firstName_text.text = contactInfoSnapshot.emergencyContact.firstName
        emergencyContact_lastName_text.text = contactInfoSnapshot.emergencyContact.lastName
        emergencyContact_relationship_text.text = contactInfoSnapshot.emergencyContact.relationship.toString()
        emergencyContact_phoneNumber_text.text = contactInfoSnapshot.emergencyContact.phoneNumber

    }

    private fun deleteEmployee() {
        databaseReference.child("employees").child(employeeId).removeValue()
            .addOnSuccessListener { Log.d("SSTechnos", "Employee successfully deleted from the database.") }
            .addOnFailureListener { Log.d("SSTechnos", "" + it.message) }
    }

    private fun editEmployee() {
        val editedEmployee = Employee(
            viewEmployeeId.text.toString(),
            viewFirstName.text.toString(),
            viewLastName.text.toString(),
            viewDOB.text.toString(),
            selectedRole,
            viewJobTitle.text.toString(),
            viewSalary.text.toString(), ""
        )
        databaseReference.child("employees").child(employeeId).setValue(editedEmployee).addOnSuccessListener {
            Log.d("SSTechnos", "Employee successfuly updated in the database.")
        }.addOnFailureListener { Log.d("SSTechnos", "" + it.message) }
    }

    private fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        onBackPressed()
        return true
    }
}
