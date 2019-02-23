package biz.sstechnos.employeedashboard.admin

import android.content.Intent
import androidx.lifecycle.Lifecycle
import biz.sstechnos.employeedashboard.entity.Employee
import biz.sstechnos.employeedashboard.entity.Role
import com.google.common.truth.Truth.assertThat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.dsl.module.module
import org.koin.standalone.StandAloneContext
import org.koin.test.KoinTest
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ViewEmployeeActivityTest : KoinTest {

    private val mockDatabaseReference: DatabaseReference = mockk(relaxed = true)
    private val mockDataSnapshot: DataSnapshot = mockk()

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

    private lateinit var activity: ViewEmployeeActivity

    @Before
    fun setUp() {
        StandAloneContext.loadKoinModules(module(override = true) {
            single("databaseReference") { mockDatabaseReference }
        })

        every { mockDataSnapshot.child(any()) } returns mockDataSnapshot
        every { mockDataSnapshot.getValue(Employee::class.java) } returns mockEmployee

        activity = Robolectric.buildActivity(
            ViewEmployeeActivity::class.java,
            Intent().putExtra("EMPLOYEE_ID", mockEmployee.employeeId)
        )
            .create().start().resume()
            .get()
    }

    @After
    fun tearDown() {
        clearAllMocks()
        StandAloneContext.stopKoin()
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