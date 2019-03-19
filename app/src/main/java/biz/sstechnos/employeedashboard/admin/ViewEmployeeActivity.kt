package biz.sstechnos.employeedashboard.admin

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.AdapterView
import android.widget.ArrayAdapter
import biz.sstechnos.employeedashboard.R
import biz.sstechnos.employeedashboard.employee.TimeSheetActivity
import biz.sstechnos.employeedashboard.entity.ContactInfo
import biz.sstechnos.employeedashboard.entity.Employee
import biz.sstechnos.employeedashboard.entity.Relationship
import biz.sstechnos.employeedashboard.entity.Role
import biz.sstechnos.employeedashboard.utils.SweetDialogUtil
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_view_employee.*
import kotlinx.android.synthetic.main.layout_contact_info.*
import org.koin.android.ext.android.inject

class ViewEmployeeActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener, ValueEventListener {

    private val databaseReference: DatabaseReference by inject()

    private lateinit var employeeId: String
    private lateinit var username: String
    private var accountStatus: String = "ACTIVE"
    private lateinit var roleArrayAdapter: ArrayAdapter<Role>
    private lateinit var relationshipArrayAdapter: ArrayAdapter<Relationship>
    private lateinit var deleteDialog: SweetAlertDialog
    private lateinit var editDialog: SweetAlertDialog

    private var roles = arrayOf(Role.ADMIN, Role.EMPLOYEE)
    private var relationships = arrayOf(
        Relationship.MOTHER,
        Relationship.FATHER,
        Relationship.SISTER,
        Relationship.BROTHER,
        Relationship.COUSIN,
        Relationship.AUNT,
        Relationship.UNCLE,
        Relationship.WIFE,
        Relationship.HUSBAND,
        Relationship.DAUGHTER,
        Relationship.SON,
        Relationship.FRIEND
    )

    private var selectedRole = Role.ADMIN
    private var selectedRelationship = Relationship.MOTHER


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
        setUpSpinnerAdapters()
        databaseReference.addListenerForSingleValueEvent(this)

        if (accountStatus == "ACTIVE") readContactInfoFromDatabase(employeeId)

        image_button_timesheet.setOnClickListener {
            startActivity(
                Intent(this@ViewEmployeeActivity, TimeSheetActivity::class.java)
                    .putExtra("EMPLOYEE_ID", employeeId)
            )
        }
    }

    override fun onDataChange(dataSnapshot: DataSnapshot) {
        Log.d("SSTechnos", "Reading employee from database")
        var employeeSnapshot =
            dataSnapshot.child("employees").child(employeeId).child("Employee").getValue(Employee::class.java)!!
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
        toggleEditFields(isEnabled = false)

        add_employee_button.visibility = GONE
        edit_employee_button.visibility = VISIBLE
        delete_employee_button.visibility = VISIBLE
        save_employee_button.visibility = VISIBLE

        delete_employee_button.setOnClickListener {
            deleteDialog = SweetDialogUtil.makeSweetDialog(this, SweetAlertDialog.WARNING_TYPE, false,
                getString(R.string.delete_employee_title), getString(R.string.delete_employee_message))
                .setConfirmButton(R.string.yes) {
                    deleteEmployee()
                    finish()
                }
                .setCancelButton(R.string.no) { dialog ->
                    dialog.cancel()
                }

            deleteDialog.show()
        }

        edit_employee_button.setOnClickListener {
            toggleEditFields(isEnabled = true)
        }

        save_employee_button.setOnClickListener {

            // TODO only show dialog if data has been changed or fields have been enabled
            editDialog = SweetDialogUtil.makeSweetDialog(this, SweetAlertDialog.CUSTOM_IMAGE_TYPE,
                true, "", getString(R.string.update_employee_message))
                .setCustomImage(R.drawable.ic_update)
                .setConfirmButton(R.string.yes) {
                    editEmployee()
                    finish()
                }
                .setCancelButton(R.string.no) { dialog -> dialog.cancel() }

            editDialog.show()
        }
    }

    // TODO Load gov id documents to image views using Picasso

    private fun toggleEditFields(isEnabled: Boolean) {
        view_first_name_edit_text.isEnabled = isEnabled
        view_last_name_edit_text.isEnabled = isEnabled
        view_dob_edit_text.isEnabled = isEnabled
        view_job_title_edit_text.isEnabled = isEnabled
        view_role_spinner.isEnabled = isEnabled
        view_salary_edit_text.isEnabled = isEnabled

        email_edit_text.isEnabled = isEnabled
        address_edit_text.isEnabled = isEnabled
        phone_edit_text.isEnabled = isEnabled
        emergency_first_name_edit_text.isEnabled = isEnabled
        emergency_last_name_edit_text.isEnabled = isEnabled
        emergency_phone_number_edit_text.isEnabled = isEnabled
        emergency_phone_number_edit_text.isEnabled = isEnabled
    }

    private fun setUpSpinnerAdapters() {
        view_role_spinner!!.onItemSelectedListener = this
        // Create an ArrayAdapter using a simple spinner layout and array
        roleArrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, roles)
        // Set layout to use when the list of choices appear
        roleArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Set Adapter to Spinner
        view_role_spinner!!.adapter = roleArrayAdapter
        roleArrayAdapter.setNotifyOnChange(true)

        emergency_relationship_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedRelationship = when (position) {
                    0 -> Relationship.MOTHER
                    1 -> Relationship.FATHER
                    2 -> Relationship.SISTER
                    3 -> Relationship.BROTHER
                    4 -> Relationship.COUSIN
                    5 -> Relationship.AUNT
                    6 -> Relationship.UNCLE
                    7 -> Relationship.WIFE
                    8 -> Relationship.HUSBAND
                    9 -> Relationship.DAUGHTER
                    10 -> Relationship.SON
                    else -> Relationship.FRIEND
                }
            }
        }
        relationshipArrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, relationships)
        relationshipArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        emergency_relationship_spinner.adapter = relationshipArrayAdapter
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
        view_role_spinner.setSelection(roleArrayAdapter.getPosition(selectedRole))

        view_job_title_edit_text.text = employee.jobTitle.toEditable()
        view_salary_edit_text.text = employee.salary.toEditable()
    }


    private fun readContactInfoFromDatabase(employeeId: String) {
        val viewContactInfoListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
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
        email_edit_text.text = contactInfoSnapshot.email.toEditable()

        val streetAddress =
            " " + contactInfoSnapshot.address.streetAddress + " " + contactInfoSnapshot.address.city + ", " +
                    contactInfoSnapshot.address.state + " " + contactInfoSnapshot.address.country + " " + contactInfoSnapshot.address.zipCode

        address_edit_text.text = streetAddress.toEditable()

        phone_edit_text.text = contactInfoSnapshot.phoneNumber.toEditable()
        emergency_first_name_edit_text.text = contactInfoSnapshot.emergencyContact.firstName.toEditable()
        emergency_last_name_edit_text.text = contactInfoSnapshot.emergencyContact.lastName.toEditable()
        selectedRelationship = contactInfoSnapshot.emergencyContact.relationship
        emergency_relationship_spinner.setSelection(relationshipArrayAdapter.getPosition(selectedRelationship))
        emergency_phone_number_edit_text.text = contactInfoSnapshot.emergencyContact.phoneNumber.toEditable()

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
            username
        )
        // TODO add address fields to contact layout
//        val contactInfo = ContactInfo(
//            email_edit_text.text.toString(),
//
//            )
        // TODO check for contact info as well

        databaseReference.child("employees").child(employeeId).child("Employee").setValue(editedEmployee)
            .addOnSuccessListener {
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
