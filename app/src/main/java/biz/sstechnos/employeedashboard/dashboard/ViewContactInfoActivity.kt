package biz.sstechnos.employeedashboard.dashboard

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import biz.sstechnos.employeedashboard.R
import biz.sstechnos.employeedashboard.entity.ContactInfo
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_view_contact_info.*
import org.koin.android.ext.android.inject

class ViewContactInfoActivity : AppCompatActivity() {

    private val databaseReference : DatabaseReference by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_contact_info)
        supportActionBar?.title = "SSTechnos - Contact Info"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val employeeId = getSharedPreferences("Employee", MODE_PRIVATE)
            .getString("employeeId", " ")

        readContactInfoFromDatabase(employeeId)
    }

    private fun readContactInfoFromDatabase(employeeId: String) {
        val viewContactInfoListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.d("SSTechnos", "Reading Employee's contact info from database")
                var contactInfoSnapshot = dataSnapshot.child("employees").child(employeeId).child("ContactInfo").getValue(ContactInfo::class.java)!!
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

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        onBackPressed()
        return true
    }
}
