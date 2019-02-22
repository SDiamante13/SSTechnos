package biz.sstechnos.employeedashboard.dashboard

import android.content.Context
import android.content.Intent
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ActivityScenario.launch
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.intent.Intents.*
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.ext.junit.runners.AndroidJUnit4
import biz.sstechnos.employeedashboard.LoginActivity
import biz.sstechnos.employeedashboard.admin.EmployeeListingsActivity
import biz.sstechnos.employeedashboard.employee.TimeSheetActivity
import com.google.common.truth.Truth.assertThat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import io.mockk.clearMocks
import io.mockk.mockk
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.dsl.module.module
import org.koin.standalone.StandAloneContext.loadKoinModules
import org.koin.standalone.StandAloneContext.stopKoin
import org.koin.test.KoinTest


@RunWith(AndroidJUnit4::class)
class DashboardActivityTest: KoinTest{

    private val mockDatabaseReference: DatabaseReference = mockk(relaxed = true)
    private val mockDataSnapshot: DataSnapshot = mockk(relaxed = true)

    private lateinit var scenario : ActivityScenario<DashboardActivity>
    private lateinit var loginContext : Context
    private lateinit var email : String

    @Before
    fun setUp() {
        loadKoinModules(module(override = true) {
            single("databaseReference") { mockDatabaseReference }
        })

        val loginScenario = launch(LoginActivity::class.java)
        loginScenario.onActivity { activity -> loginContext = activity.applicationContext  }
        email = "email@test.com"
        scenario = launch(Intent(loginContext, DashboardActivity::class.java).putExtra("USER_EMAIL", email))
    }

    @After
    fun tearDown() {
        clearMocks()
        scenario.moveToState(Lifecycle.State.DESTROYED)
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
    fun `clicking on enter time sheet button goes to TimeSheetActivity`() {
        init()
        scenario.onActivity { activity -> activity.enterTimesheetsContainer.performClick() }
        intended(hasComponent(TimeSheetActivity::class.java.canonicalName))
        release()
    }
}