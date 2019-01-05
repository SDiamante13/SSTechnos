package biz.sstechnos.employeedashboard.dashboard

import android.content.Context
import android.content.Intent
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ActivityScenario.launch
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.intent.Intents.*
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import biz.sstechnos.employeedashboard.LoginActivity
import biz.sstechnos.employeedashboard.R
import biz.sstechnos.employeedashboard.admin.EmployeeListingsActivity
import biz.sstechnos.employeedashboard.admin.ViewEmployeeActivity
import biz.sstechnos.employeedashboard.employee.TimeSheetActivity
import biz.sstechnos.employeedashboard.testDI.dataTestModule
import com.google.common.truth.Truth.assertThat
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import io.mockk.mockk
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.standalone.StandAloneContext.loadKoinModules
import org.koin.standalone.StandAloneContext.stopKoin
import org.koin.standalone.inject
import org.koin.test.KoinTest


@RunWith(AndroidJUnit4::class)
class DashboardActivityTest : KoinTest{

    private val database : DatabaseReference by inject("mockDatabaseReference")
    private lateinit var mockDataSnapshot : DataSnapshot

    private val context = ApplicationProvider.getApplicationContext<Context>()

    private lateinit var scenario : ActivityScenario<DashboardActivity>
    private lateinit var loginContext : Context
    private lateinit var email : String

    @Before
    fun setUp() {
        loadKoinModules(dataTestModule)
        FirebaseApp.initializeApp(context)

        mockDataSnapshot = mockk(relaxed = true)

        val loginScenario = launch(LoginActivity::class.java)
        loginScenario.onActivity { activity -> loginContext = activity.applicationContext  }
        email = "email@test.com"
        scenario = launch(Intent(loginContext, DashboardActivity::class.java).putExtra("USER_EMAIL", email))
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun `current user has correct email`() {
        scenario.onActivity {
                activity ->
            assertThat(activity.userEmail).isEqualTo(email)
        }
    }

    @Test
    fun `employee role only shows employee container buttons`() {
        scenario.onActivity {
                activity ->
            activity.onDataChange(mockDataSnapshot)
            assertThat(activity.employeeListingsContainer.visibility).isEqualTo(GONE)
            assertThat(activity.viewTimeSheetsContainer.visibility).isEqualTo(GONE)
            assertThat(activity.uploadDocumentsContainer.visibility).isEqualTo(GONE)
        }
    }

    @Test
    fun `admin role shows all buttons`() {
        scenario.onActivity {
                activity ->
            assertThat(activity.employeeListingsContainer.visibility).isEqualTo(VISIBLE)
            assertThat(activity.viewTimeSheetsContainer.visibility).isEqualTo(VISIBLE)
            assertThat(activity.uploadDocumentsContainer.visibility).isEqualTo(VISIBLE)
        }
    }

    @Test
    fun `clicking on employee listings button goes to EmployeeListingsActivity`() {
        init()
        scenario.onActivity { activity -> activity.employeeListingsContainer.performClick() }
        intended(hasComponent(EmployeeListingsActivity::class.java.canonicalName))
        release()
    }

    @Test
    fun `clicking on enter timesheet button goes to TimeSheetActivity`() {
        init()
        scenario.onActivity { activity -> activity.enterTimesheetsContainer.performClick() }
        intended(hasComponent(TimeSheetActivity::class.java.canonicalName))
        release()
    }
}