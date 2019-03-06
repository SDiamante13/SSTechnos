package biz.sstechnos.employeedashboard.dashboard

import android.content.Intent
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ActivityScenario.launch
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.intent.Intents.*
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import biz.sstechnos.employeedashboard.R
import biz.sstechnos.employeedashboard.admin.EmployeeListingsActivity
import biz.sstechnos.employeedashboard.admin.upload.UploadDocumentsActivity
import biz.sstechnos.employeedashboard.employee.TimeSheetActivity
import biz.sstechnos.employeedashboard.employee.ViewDocumentsActivity
import biz.sstechnos.employeedashboard.entity.Employee
import biz.sstechnos.employeedashboard.entity.Role
import com.google.common.truth.Truth.assertThat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import kotlinx.android.synthetic.main.activity_dashboard.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.dsl.module.module
import org.koin.standalone.StandAloneContext.loadKoinModules
import org.koin.standalone.StandAloneContext.stopKoin
import org.koin.test.KoinTest


@RunWith(AndroidJUnit4::class)
class DashboardActivityTest: KoinTest {

    private val mockDatabaseReference: DatabaseReference = mockk()
    private val mockDataSnapshot: DataSnapshot = mockk()
    private val mockChildren = mutableListOf(mockDataSnapshot, mockDataSnapshot)

    private val adminUser = Employee(
        "SST501",
        "Sherlock",
        "Holmes",
        "16/05/1923",
        Role.ADMIN,
        "Detective",
        "100000",
        "sholmes401@gmail.com"
    )

    private val employeeUser = Employee(
        "SST701",
        "Test",
        "User",
        "17/08/1991",
        Role.EMPLOYEE,
        "Developer",
        "55000",
        "email@test.com"
    )


    private lateinit var scenario: ActivityScenario<DashboardActivity>

    private var intent: Intent = Intent()

    @Before
    fun setUp() {
        loadKoinModules(module(override = true) {
            single("databaseReference") { mockDatabaseReference }
        })

        mockEverything()
    }

    @After
    fun tearDown() {
        clearAllMocks()
        stopKoin()
    }

    @Test
    fun `employee role only shows employee container buttons`() {
        startActivityWithUsername(employeeUser.username!!)

        scenario.onActivity { activity ->
            assertThat(activity.employee_listings_container.visibility).isEqualTo(GONE)
            assertThat(activity.view_timesheets_container.visibility).isEqualTo(GONE)
            assertThat(activity.upload_documents_container.visibility).isEqualTo(GONE)
        }
    }

    @Test
    fun `admin role shows all buttons`() {
        startActivityWithUsername(adminUser.username!!)

        scenario.onActivity { activity ->
            assertThat(activity.employee_listings_container.visibility).isEqualTo(VISIBLE)
            assertThat(activity.view_timesheets_container.visibility).isEqualTo(VISIBLE)
            assertThat(activity.upload_documents_container.visibility).isEqualTo(VISIBLE)
        }
    }

    @Test
    fun `clicking on employee listings button goes to EmployeeListingsActivity`() {
        startActivityWithUsername(adminUser.username!!)

        init()
        onView(withId(R.id.employee_listings_container)).perform(click())
        intended(hasComponent(EmployeeListingsActivity::class.java.canonicalName))
        release()
    }

    @Test
    fun `clicking on upload documents button goes to UploadDocumentsActivity`() {
        startActivityWithUsername(adminUser.username!!)

        init()
        onView(withId(R.id.upload_documents_container)).perform(click())
        intended(hasComponent(UploadDocumentsActivity::class.java.canonicalName))
        release()
    }

    @Test
    fun `clicking on enter time sheet button goes to TimeSheetActivity`() {
        startActivityWithUsername(employeeUser.username!!)

        init()
        onView(withId(R.id.enter_timesheets_container)).perform(click())
        intended(hasComponent(TimeSheetActivity::class.java.canonicalName))
        release()
    }

    @Test
    fun `clicking on view documents button goes to ViewDocumentsActivity`() {
        startActivityWithUsername(adminUser.username!!)

        init()
        onView(withId(R.id.view_documents_container)).perform(click())
        intended(hasComponent(ViewDocumentsActivity::class.java.canonicalName))
        release()
    }

    private fun mockEverything() {
        every { mockDatabaseReference.child(any()) } returns mockDatabaseReference
        every { mockDatabaseReference.addValueEventListener(any()) } returns mockk()
        every { mockDataSnapshot.child(any()) } returns mockDataSnapshot
        every { mockDataSnapshot.getValue(Employee::class.java) } returnsMany mutableListOf(adminUser, employeeUser)
        every { mockDataSnapshot.children } returns mockChildren
    }

    private fun startActivityWithUsername(email: String) {
        intent.putExtra("USER_EMAIL", email)
            .setClassName(
                "biz.sstechnos.employeedashboard.dashboard",
                "biz.sstechnos.employeedashboard.dashboard.DashboardActivity"
            )

        scenario = launch(intent)
        scenario.onActivity { activity ->
            activity.onDataChange(mockDataSnapshot)
        }
    }
}