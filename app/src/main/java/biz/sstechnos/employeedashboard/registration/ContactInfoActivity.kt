package biz.sstechnos.employeedashboard.registration

import android.content.Intent
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import biz.sstechnos.employeedashboard.R
import biz.sstechnos.employeedashboard.entity.AddressInfo
import biz.sstechnos.employeedashboard.entity.ContactInfo
import biz.sstechnos.employeedashboard.entity.EmergencyContact
import biz.sstechnos.employeedashboard.entity.Relationship
import biz.sstechnos.employeedashboard.utils.CookieBarUtil
import com.google.android.gms.common.api.Result
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_contact_info.*
import org.koin.android.ext.android.inject

class ContactInfoActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener, OnCompleteListener<Void> {

    private val databaseReference : DatabaseReference by inject()

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
        Relationship.FRIEND)

    private lateinit var selectedRelationship : Relationship

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_info)
        supportActionBar?.title = "Contact Info Form"
        setUpRelationshipSpinnerAdapter()

        submit_button.setOnClickListener {
            if(email_editText.text.toString().isEmpty() ||
                country_editText.text.toString().isEmpty() ||
                city_editText.text.toString().isEmpty() ||
                state_editText.text.toString().isEmpty() ||
                streetAddress_editText.text.toString().isEmpty() ||
                zipCode_editText.text.toString().isEmpty() ||
                phoneNumber_editText.text.toString().isEmpty() ||
                emergencyContact_firstName_editText.text.toString().isEmpty() ||
                emergencyContact_lastName_editText.text.toString().isEmpty() ||
                emergencyContact_phoneNumber_editText.text.toString().isEmpty()) {
                CookieBarUtil.makeCookie(this@ContactInfoActivity,
                    "Employee contact information required!",
                    "Please fill out all requested fields.").show()
            } else {
                var email = email_editText.text.toString()
                var address = AddressInfo(
                    country_editText.text.toString(),
                    city_editText.text.toString(),
                    state_editText.text.toString(),
                    streetAddress_editText.text.toString(),
                    zipCode_editText.text.toString()
                )
                var phoneNumber = phoneNumber_editText.text.toString()
                var emergencyContact = EmergencyContact(
                    emergencyContact_firstName_editText.text.toString(),
                    emergencyContact_lastName_editText.text.toString(),
                    selectedRelationship,
                    emergencyContact_phoneNumber_editText.text.toString()
                )

                var contactInfo = ContactInfo(email, address, phoneNumber, emergencyContact)

                val employeeId = getSharedPreferences("Employee", MODE_PRIVATE)
                    .getString("employeeId", " ")

                databaseReference.child("employees").child(employeeId).child("ContactInfo").setValue(contactInfo).addOnCompleteListener(this)
            }
        }
    }

    override fun onComplete(task: Task<Void>) {
        if(task.isSuccessful) {
            Log.d("SSTechnos", "Employee's contact info successfully added..")
            startActivity(Intent(applicationContext, UploadIdCardActivity::class.java))
        } else {
            Log.d("SSTechnos", "" + task.exception)
            CookieBarUtil.makeCookie(this@ContactInfoActivity,
                "Upload failed!",
                "Failed to upload your contact info. Please try again.").show()
        }
    }

    private fun setUpRelationshipSpinnerAdapter() {
        relationshipSpinner!!.onItemSelectedListener = this
        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, relationships)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        relationshipSpinner!!.adapter = arrayAdapter
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }

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
