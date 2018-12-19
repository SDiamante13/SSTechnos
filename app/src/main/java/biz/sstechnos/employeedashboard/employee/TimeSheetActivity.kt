package biz.sstechnos.employeedashboard.employee

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.util.Log
import android.view.MenuItem
import biz.sstechnos.employeedashboard.R
import biz.sstechnos.employeedashboard.entity.Employee
import biz.sstechnos.employeedashboard.entity.Timesheet
import biz.sstechnos.employeedashboard.entity.Week
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_time_sheet.*
import java.lang.Double.parseDouble
import java.text.DateFormatSymbols
import java.util.*


class TimeSheetActivity : AppCompatActivity() {

    private val databaseReference = FirebaseDatabase.getInstance().reference

    private lateinit var employeeId : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_time_sheet)
        supportActionBar?.title = "Monthly Time Sheet"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        employeeId = getSharedPreferences("Employee", MODE_PRIVATE)
            .getString("employeeId", " ")

        val todaysDate = Calendar.getInstance()
        val months = DateFormatSymbols().months
        val monthIndex = todaysDate.get(Calendar.MONTH)
        val year = todaysDate.get(Calendar.YEAR)
        val timesheetId = "M${monthIndex+1}Y${year-2000}"

        month.text = months[monthIndex]

        loadTimesheet(timesheetId)

        timesheet_saveButton.setOnClickListener {
            val timesheet = createTimesheet()
            databaseReference
                .child("timesheets")
                .child(employeeId)
                .child(timesheetId)
                .setValue(timesheet)
                .addOnSuccessListener { Log.d("SSTechnos", "Timesheet updated.") }
                .addOnFailureListener { Log.d("SSTechnos", "" + it.message) }
        }

        timesheet_submitButton.setOnClickListener {
            sendEmail("email", "pass")
        }



    }

    private fun loadTimesheet(timesheetId : String) {

        val loadTimesheetListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var timesheetSnapshot : Timesheet? = dataSnapshot.child("timesheets").child(employeeId).child(timesheetId).getValue(Timesheet::class.java)
                if (timesheetSnapshot != null) {
                    populateTimesheet(timesheetSnapshot)
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("SSTechnos", "loadTimesheet:onCancelled", databaseError.toException())
            }

        }
        databaseReference.addValueEventListener(loadTimesheetListener)
    }

    private fun populateTimesheet(timesheetSnapshot: Timesheet) {
        week1_monday.text = timesheetSnapshot.weeks[0].mondayHrs.toEditable()
        week1_tuesday.text = timesheetSnapshot.weeks[0].tuesdayHrs.toEditable()
        week1_wednesday.text = timesheetSnapshot.weeks[0].wednesdayHrs.toEditable()
        week1_thursday.text = timesheetSnapshot.weeks[0].thursdayHrs.toEditable()
        week1_friday.text = timesheetSnapshot.weeks[0].fridayHrs.toEditable()
        week1_saturday.text = timesheetSnapshot.weeks[0].saturdayHrs.toEditable()
        week1_sunday.text = timesheetSnapshot.weeks[0].sundayHrs.toEditable()

        week2_monday.text = timesheetSnapshot.weeks[1].mondayHrs.toEditable()
        week2_tuesday.text = timesheetSnapshot.weeks[1].tuesdayHrs.toEditable()
        week2_wednesday.text = timesheetSnapshot.weeks[1].wednesdayHrs.toEditable()
        week2_thursday.text = timesheetSnapshot.weeks[1].thursdayHrs.toEditable()
        week2_friday.text = timesheetSnapshot.weeks[1].fridayHrs.toEditable()
        week2_saturday.text = timesheetSnapshot.weeks[1].saturdayHrs.toEditable()
        week2_sunday.text = timesheetSnapshot.weeks[1].sundayHrs.toEditable()

        week3_monday.text = timesheetSnapshot.weeks[2].mondayHrs.toEditable()
        week3_tuesday.text = timesheetSnapshot.weeks[2].tuesdayHrs.toEditable()
        week3_wednesday.text = timesheetSnapshot.weeks[2].wednesdayHrs.toEditable()
        week3_thursday.text = timesheetSnapshot.weeks[2].thursdayHrs.toEditable()
        week3_friday.text = timesheetSnapshot.weeks[2].fridayHrs.toEditable()
        week3_saturday.text = timesheetSnapshot.weeks[2].saturdayHrs.toEditable()
        week3_sunday.text = timesheetSnapshot.weeks[2].sundayHrs.toEditable()

        week4_monday.text = timesheetSnapshot.weeks[3].mondayHrs.toEditable()
        week4_tuesday.text = timesheetSnapshot.weeks[3].tuesdayHrs.toEditable()
        week4_wednesday.text = timesheetSnapshot.weeks[3].wednesdayHrs.toEditable()
        week4_thursday.text = timesheetSnapshot.weeks[3].thursdayHrs.toEditable()
        week4_friday.text = timesheetSnapshot.weeks[3].fridayHrs.toEditable()
        week4_saturday.text = timesheetSnapshot.weeks[3].saturdayHrs.toEditable()
        week4_sunday.text = timesheetSnapshot.weeks[3].sundayHrs.toEditable()
    }

    private fun createTimesheet(): Timesheet {
        var week1Strings = listOf(
            week1_monday.text.toString().trim(),
            week1_tuesday.text.toString().trim(),
            week1_wednesday.text.toString().trim(),
            week1_thursday.text.toString().trim(),
            week1_friday.text.toString().trim(),
            week1_saturday.text.toString().trim(),
            week1_sunday.text.toString().trim()
        )
        var week1Doubles = mutableListOf<Double>()
        for (index in 0..6) {
            if (week1Strings[index].isNotEmpty()) {
                week1Doubles.add(parseDouble(week1Strings[index]))
            } else {
                week1Doubles.add(0.0)
            }
        }

        val week1 = Week(
            week1Doubles.sum(),
            week1Doubles[0],
            week1Doubles[1],
            week1Doubles[2],
            week1Doubles[3],
            week1Doubles[4],
            week1Doubles[5],
            week1Doubles[6]
        )

        var week2Strings = listOf(
            week2_monday.text.toString().trim(),
            week2_tuesday.text.toString().trim(),
            week2_wednesday.text.toString().trim(),
            week2_thursday.text.toString().trim(),
            week2_friday.text.toString().trim(),
            week2_saturday.text.toString().trim(),
            week2_sunday.text.toString().trim()
        )
        var week2Doubles = mutableListOf<Double>()
        for (index in 0..6) {
            if (week2Strings[index].isNotEmpty()) {
                week2Doubles.add(parseDouble(week2Strings[index]))
            } else {
                week2Doubles.add(0.0)
            }
        }

        val week2 = Week(
            week2Doubles.sum(),
            week2Doubles[0],
            week2Doubles[1],
            week2Doubles[2],
            week2Doubles[3],
            week2Doubles[4],
            week2Doubles[5],
            week2Doubles[6]
        )

        var week3Strings = listOf(
            week3_monday.text.toString().trim(),
            week3_tuesday.text.toString().trim(),
            week3_wednesday.text.toString().trim(),
            week3_thursday.text.toString().trim(),
            week3_friday.text.toString().trim(),
            week3_saturday.text.toString().trim(),
            week3_sunday.text.toString().trim()
        )
        var week3Doubles = mutableListOf<Double>()
        for (index in 0..6) {
            if (week3Strings[index].isNotEmpty()) {
                week3Doubles.add(parseDouble(week3Strings[index]))
            } else {
                week3Doubles.add(0.0)
            }
        }

        val week3 = Week(
            week3Doubles.sum(),
            week3Doubles[0],
            week3Doubles[1],
            week3Doubles[2],
            week3Doubles[3],
            week3Doubles[4],
            week3Doubles[5],
            week3Doubles[6]
        )
        var week4Strings = listOf(
            week4_monday.text.toString().trim(),
            week4_tuesday.text.toString().trim(),
            week4_wednesday.text.toString().trim(),
            week4_thursday.text.toString().trim(),
            week4_friday.text.toString().trim(),
            week4_saturday.text.toString().trim(),
            week4_sunday.text.toString().trim()
        )
        var week4Doubles = mutableListOf<Double>()
        for (index in 0..6) {
            if (week4Strings[index].isNotEmpty()) {
                week4Doubles.add(parseDouble(week4Strings[index]))
            } else {
                week4Doubles.add(0.0)
            }
        }

        val week4 = Week(
            week4Doubles.sum(),
            week4Doubles[0],
            week4Doubles[1],
            week4Doubles[2],
            week4Doubles[3],
            week4Doubles[4],
            week4Doubles[5],
            week4Doubles[6]
        )

        val weekList = mutableListOf(week1, week2, week3, week4)
        val timesheet = Timesheet(
            weekList.sumByDouble { it -> it.totalHrs },
            weekList)
        return timesheet
    }

    private fun sendEmail(email : String, password : String) {
//        BackgroundMail.newBuilder(this)
//    .withUsername(admin.)
//    .withPassword(password)
//    .withSenderName("Steven D")
//    .withMailTo("steven4@vt.edu")
//    .withType(BackgroundMail.TYPE_PLAIN)
//    .withSubject("This is a test email")
//    .withBody("This is a test ")
//    .withSendingMessage(R.string.sending_email)
//    .withOnSuccessCallback(object : BackgroundMail.OnSendingCallback {
//        override fun onSuccess() {
//            Log.d("SSTechnos", "Email sent successfully!!!")
//        }
//
//        override fun onFail(e: Exception) {
//            Log.d("SSTechnos", "${e.message}")
//
//        }
//    })
//    .send()
}

    private fun Double.toEditable() : Editable = Editable.Factory.getInstance().newEditable(this.toString())

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        onBackPressed()
        return true
    }
}
