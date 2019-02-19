package biz.sstechnos.employeedashboard.admin

import android.content.Context
import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ActivityScenario.launch
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import biz.sstechnos.employeedashboard.R
import biz.sstechnos.employeedashboard.entity.Employee
import biz.sstechnos.employeedashboard.entity.Role
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import io.mockk.every
import io.mockk.mockk
import kotlinx.android.synthetic.main.activity_view_employee.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.dsl.module.module
import org.koin.standalone.StandAloneContext
import org.koin.test.KoinTest
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import com.google.common.truth.Truth.assertThat



@RunWith(RobolectricTestRunner::class)
class ViewEmployeeActivityTest : KoinTest {

    private val mockDatabaseReference = mockk<DatabaseReference>(relaxed = true)
    private val mockDataSnapshot = mockk<DataSnapshot>(relaxed = true)

    private lateinit var activity : ViewEmployeeActivity
    private lateinit var employeeId : String

    val mockEmployee = Employee("SST501",
        "Sherlock",
        "Holmes",
        "16/05/1923",
        Role.EMPLOYEE,
        "Detective",
        "200000",
        "")

    @Before
    fun setUp() {
        StandAloneContext.loadKoinModules(module(override = true) {
            single("databaseReference") { mockDatabaseReference }
        })

        every { mockDataSnapshot.child(any()) } returns mockDataSnapshot
        every { mockDataSnapshot.getValue(Employee::class.java) } returns mockEmployee

        activity = Robolectric.buildActivity(ViewEmployeeActivity::class.java, Intent().putExtra("EMPLOYEE_ID", mockEmployee.employeeId))
            .create().start().resume()
            .get()

    }

    @Test
    fun `onDataChange populates fields with employee read from database`() {
        activity.onDataChange(mockDataSnapshot)
        assertThat(activity.viewEmployeeId.text.toString()).isEqualTo(mockEmployee.employeeId)
        assertThat(activity.viewFirstName.text.toString()).isEqualTo(mockEmployee.firstName)
        assertThat(activity.viewLastName.text.toString()).isEqualTo(mockEmployee.lastName)
        assertThat(activity.viewDOB.text.toString()).isEqualTo(mockEmployee.dateOfBirth)
        assertThat(activity.viewJobTitle.text.toString()).isEqualTo(mockEmployee.jobTitle)
        assertThat(activity.viewSalary.text.toString()).isEqualTo(mockEmployee.salary)
    }


}