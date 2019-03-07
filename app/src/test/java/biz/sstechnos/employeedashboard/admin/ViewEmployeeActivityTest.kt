package biz.sstechnos.employeedashboard.admin

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ActivityScenario.launch
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents.*
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import biz.sstechnos.employeedashboard.R
import biz.sstechnos.employeedashboard.employee.TimeSheetActivity
import biz.sstechnos.employeedashboard.entity.Employee
import biz.sstechnos.employeedashboard.entity.Role
import cn.pedant.SweetAlert.SweetAlertDialog.BUTTON_CANCEL
import cn.pedant.SweetAlert.SweetAlertDialog.BUTTON_CONFIRM
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.dsl.module.module
import org.koin.standalone.StandAloneContext.loadKoinModules
import org.koin.standalone.StandAloneContext.stopKoin
import org.koin.test.KoinTest

@RunWith(AndroidJUnit4::class)
class ViewEmployeeActivityTest: KoinTest {

    private val mockDatabaseReference: DatabaseReference = mockk()
    private val mockDataSnapshot: DataSnapshot = mockk()
    private val mockTask: Task<Void> = mockk()

    private val mockEmployee = Employee(
        "SST501",
        "Sherlock",
        "Holmes",
        "16/05/1923",
        Role.EMPLOYEE,
        "Detective",
        "200000",
        ""
    )

    private val intent: Intent = Intent().putExtra("EMPLOYEE_ID", mockEmployee.employeeId)
        .putExtra("ACCOUNT_STATUS", "ACTIVE")
        .setClassName("biz.sstechnos.employeedashboard.admin",
            "biz.sstechnos.employeedashboard.admin.ViewEmployeeActivity")

    private lateinit var scenario: ActivityScenario<ViewEmployeeActivity>

    @Before
    fun setUp() {
        loadKoinModules(module(override = true) {
            single("databaseReference") { mockDatabaseReference }
        })

        mockEverything()

        scenario = launch(intent)
    }

    @After
    fun tearDown() {
        clearAllMocks()
        stopKoin()
    }

    @Test
    fun `onDataChange populates fields with employee read from database`() {
        scenario.onActivity { activity -> activity.onDataChange(mockDataSnapshot) }

        onView(withId(R.id.view_employee_id_edit_text)).check(matches(withText(mockEmployee.employeeId)))
        onView(withId(R.id.view_first_name_edit_text)).check(matches(withText(mockEmployee.firstName)))
        onView(withId(R.id.view_last_name_edit_text)).check(matches(withText(mockEmployee.lastName)))
        onView(withId(R.id.view_dob_edit_text)).check(matches(withText(mockEmployee.dateOfBirth)))
        onView(withId(R.id.view_job_title_edit_text)).check(matches(withText(mockEmployee.jobTitle)))
        onView(withId(R.id.view_salary_edit_text)).check(matches(withText(mockEmployee.salary)))
    }

    @Test
    fun `clicking on time sheet button takes user to TimeSheetActivity`() {
        scenario.onActivity { activity -> activity.onDataChange(mockDataSnapshot) }

        init()
        onView(withId(R.id.image_button_timesheet)).perform(click())
        intended(hasComponent(TimeSheetActivity::class.java.canonicalName))
        release()
    }

    @Test
    fun `delete employee launches dialog and clicking yes removes employee from database`() {
        scenario.onActivity { activity -> activity.onDataChange(mockDataSnapshot) }

        onView(withId(R.id.delete_employee_button)).perform(scrollTo(), click())

        scenario.onActivity { activity -> activity.getDeleteDialog().getButton(BUTTON_CONFIRM).performClick() }

        verify(exactly = 1) { mockDatabaseReference.removeValue() }
    }

    @Test
    fun `delete employee launches dialog and clicking no does not remove employee from database`() {
        scenario.onActivity { activity -> activity.onDataChange(mockDataSnapshot) }

        onView(withId(R.id.delete_employee_button)).perform(scrollTo(), click())

        scenario.onActivity { activity -> activity.getDeleteDialog().getButton(BUTTON_CANCEL).performClick() }

        verify(exactly = 0) { mockDatabaseReference.removeValue() }
    }

    @Test
    fun `edit employee button enables all fields except for employee id`() {
        scenario.onActivity { activity -> activity.onDataChange(mockDataSnapshot) }

        onView(withId(R.id.view_employee_id_edit_text)).check(matches(not(isEnabled())))
        onView(withId(R.id.view_first_name_edit_text)).check(matches(not(isEnabled())))
        onView(withId(R.id.view_last_name_edit_text)).check(matches(not(isEnabled())))
        onView(withId(R.id.view_dob_edit_text)).check(matches(not(isEnabled())))
        onView(withId(R.id.view_job_title_edit_text)).check(matches(not(isEnabled())))
        onView(withId(R.id.view_role_spinner)).check(matches(not(isEnabled())))
        onView(withId(R.id.view_salary_edit_text)).check(matches(not(isEnabled())))

        onView(withId(R.id.edit_employee_button)).perform(scrollTo(), click())

        onView(withId(R.id.view_employee_id_edit_text)).check(matches(not(isEnabled())))
        onView(withId(R.id.view_first_name_edit_text)).check(matches(isEnabled()))
        onView(withId(R.id.view_last_name_edit_text)).check(matches(isEnabled()))
        onView(withId(R.id.view_dob_edit_text)).check(matches(isEnabled()))
        onView(withId(R.id.view_job_title_edit_text)).check(matches(isEnabled()))
        onView(withId(R.id.view_role_spinner)).check(matches(isEnabled()))
        onView(withId(R.id.view_salary_edit_text)).check(matches(isEnabled()))
    }

    @Test
    fun `save employee button saves edits in database`() {
        scenario.onActivity { activity -> activity.onDataChange(mockDataSnapshot) }

        val editedEmployee = Employee(
            "SST501",
            "Sherlock",
            "Holmes",
            "16/05/1923",
            Role.EMPLOYEE,
            "QA Lead",
            "246000",
            ""
        )

        onView(withId(R.id.edit_employee_button)).perform(scrollTo(), click())

        onView(withId(R.id.view_job_title_edit_text)).perform(replaceText(editedEmployee.jobTitle))
        onView(withId(R.id.view_salary_edit_text)).perform(replaceText(editedEmployee.salary))

        onView(withId(R.id.save_employee_button)).perform(scrollTo(), click())

        scenario.onActivity { activity -> activity.getEditDialog().getButton(BUTTON_CONFIRM).performClick() }

        verify(exactly = 1) { mockDatabaseReference.setValue(editedEmployee) }
    }

    // TODO add tests for editing contact info

    private fun mockEverything() {
        every { mockDataSnapshot.child(any()) } returns mockDataSnapshot
        every { mockDataSnapshot.getValue(Employee::class.java) } returns mockEmployee

        every { mockDatabaseReference.addListenerForSingleValueEvent(any()) } returns mockk()
        every { mockDatabaseReference.removeValue() } returns mockTask
        every { mockDatabaseReference.setValue(any()) } returns mockTask
        every { mockDatabaseReference.child(any()) } returns mockDatabaseReference

        every { mockTask.addOnSuccessListener(any()) } returns mockTask
        every { mockTask.addOnFailureListener(any()) } returns mockTask
    }
}