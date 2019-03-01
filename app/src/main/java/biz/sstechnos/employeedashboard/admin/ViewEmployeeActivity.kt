package biz.sstechnos.employeedashboard.admin

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.View.*
import android.widget.*
import biz.sstechnos.employeedashboard.R
import biz.sstechnos.employeedashboard.employee.TimeSheetActivity
import biz.sstechnos.employeedashboard.entity.ContactInfo
import biz.sstechnos.employeedashboard.entity.Employee
import biz.sstechnos.employeedashboard.entity.Role
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_view_employee.*
import kotlinx.android.synthetic.main.layout_contact_info.*
import org.koin.android.ext.android.inject
import cn.pedant.SweetAlert.SweetAlertDialog

class ViewEmployeeActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener, ValueEventListener {

    private val databaseReference: DatabaseReference by inject()

    private lateinit var employeeId: String
    private lateinit var username: String
    private var accountStatus: String = "ACTIVE"
    private lateinit var arrayAdapter: ArrayAdapter<Role>
    private lateinit var deleteDialog: SweetAlertDialog
    private lateinit var editDialog: SweetAlertDialog

    private var roles = arrayOf(Role.ADMIN, Role.EMPLOYEE)
    private var selectedRole = Role.ADMIN
    
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
        databaseReference.addListenerForSingleValueEvent(this)

        if(accountStatus == "ACTIVE") readContactInfoFromDatabase(employeeId)

        image_button_timesheet.setOnClickListener {
            startActivity(Intent(this@ViewEmployeeActivity, TimeSheetActivity::class.java)
                .putExtra("EMPLOYEE_ID", employeeId))
        }
    }

    override fun onDataChange(dataSnapshot: DataSnapshot) {
        Log.d("SSTechnos", "Reading employee from database")
        var employeeSnapshot = dataSnapshot.child("employees").child(employeeId).child("Employee").getValue(Employee::class.java)!!
        username = employeeSnapshot.username!!
        populateFieldsWithEmployeeData(employeeSnapshot)
    }

    override fun onCancelled(databaseError: DatabaseError?) {
        // Getting Post failed, log a message
        Log.w("SSTechnos", "loadPost:onCancelled", databaseError?.toException())
    }

    private fun setupLayout() {
        view_employee_id_label.visibility = VISIBLE
        view_first_name_label.visibility = VISIBLE
        view_last_name_label.visibility = VISIBLE
        view_dob_label.visibility = VISIBLE
        view_role_label.visibility = VISIBLE
        view_job_title_label.visibility = VISIBLE
        view_salary_label.visibility = VISIBLE

        view_employee_id_edit_text.isEnabled = false
        view_first_name_edit_text.isEnabled = false
        view_last_name_edit_text.isEnabled = false
        view_dob_edit_text.isEnabled = false
        view_job_title_edit_text.isEnabled = false
        view_role_spinner.isEnabled = false
        view_salary_edit_text.isEnabled = false

        add_employee_button.visibility = GONE
        edit_employee_button.visibility = VISIBLE
        delete_employee_button.visibility = VISIBLE
        save_employee_button.visibility = VISIBLE

        delete_employee_button.setOnClickListener {
            deleteDialog = SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText(getString(R.string.delete_employee_title))
                .setContentText(getString(R.string.delete_employee_message))
                .setConfirmButton(R.string.yes) {
                    deleteEmployee()
                    finish()
                }
                .setCancelButton(R.string.no) {
                    dialog -> dialog.cancel()
                }

                deleteDialog.show()
        }

        edit_employee_button.setOnClickListener {
            view_first_name_edit_text.isEnabled = true
            view_last_name_edit_text.isEnabled = true
            view_dob_edit_text.isEnabled = true
            view_job_title_edit_text.isEnabled = true
            view_role_spinner.isEnabled = true
            view_salary_edit_text.isEnabled = true
        }

        save_employee_button.setOnClickListener {
            editDialog = SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE)
                .setTitleText(getString(R.string.update_employee_title))
                .setContentText(getString(R.string.update_employee_message))
                .setConfirmButton(R.string.yes) {
                    editEmployee()
                    finish()
                }
                .setCancelButton(R.string.no) { dialog ->
                    dialog.cancel()
                }
                editDialog.show()
        }
    }

    private fun setUpSpinnerAdapter() {
        view_role_spinner!!.onItemSelectedListener = this
        // Create an ArrayAdapter using a simple spinner layout and array
        arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, roles)
        // Set layout to use when the list of choices appear
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Set Adapter to Spinner
        view_role_spinner!!.adapter = arrayAdapter
        arrayAdapter.setNotifyOnChange(true)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        selectedRole = if (position == 1) Role.ADMIN else Role.EMPLOYEE
    }

    private fun populateFieldsWithEmployeeData(employee: Employee) {
        view_employee_id_edit_text.text = employeeId.toEditable()
        view_first_name_edit_text.text = employee.firstName.toEditable()
        view_last_name_edit_text.text = employee.lastName.toEditable()
        view_dob_edit_text.text = employee.dateOfBirth.toEditable()

        selectedRole = employee.role
        view_role_spinner.setSelection(arrayAdapter.getPosition(selectedRole))

        view_job_title_edit_text.text = employee.jobTitle.toEditable()
        view_salary_edit_text.text = employee.salary.toEditable()
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
        databaseReference.addListenerForSingleValueEvent(viewContactInfoListener)
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
            view_employee_id_edit_text.text.toString(),
            view_first_name_edit_text.text.toString(),
            view_last_name_edit_text.text.toString(),
            view_dob_edit_text.text.toString(),
            view_role_spinner.selectedItem as Role,
            view_job_title_edit_text.text.toString(),
            view_salary_edit_text.text.toString(),
            username)

        databaseReference.child("employees").child(employeeId).child("Employee").setValue(editedEmployee).addOnSuccessListener {
            Log.d("SSTechnos", "Employee successfuly updated in the database.")
        }.addOnFailureListener { Log.d("SSTechnos", "" + it.message) }
    }

    private fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        onBackPressed()
        return true
    }

    fun getDeleteDialog(): SweetAlertDialog {
        return this.deleteDialog
    }

    fun getEditDialog(): SweetAlertDialog {
        return this.editDialog
    }
}
