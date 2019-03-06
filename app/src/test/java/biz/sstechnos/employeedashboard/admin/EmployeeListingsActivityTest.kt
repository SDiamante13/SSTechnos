package biz.sstechnos.employeedashboard.admin

import android.support.v7.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ActivityScenario.launch
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.Intents.*
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import biz.sstechnos.employeedashboard.R
import biz.sstechnos.employeedashboard.entity.Employee
import biz.sstechnos.employeedashboard.entity.Role
import com.google.common.truth.Truth.assertThat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import kotlinx.android.synthetic.main.activity_employee_listings.*
import kotlinx.android.synthetic.main.employee_list_item.view.*
import org.hamcrest.Matchers.allOf
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.dsl.module.module
import org.koin.standalone.StandAloneContext.loadKoinModules
import org.koin.standalone.StandAloneContext.stopKoin

@RunWith(AndroidJUnit4::class)
class EmployeeListingsActivityTest {

    private val mockDatabaseReference: DatabaseReference = mockk(relaxed = true)
    private val mockDataSnapshot: DataSnapshot = mockk()
    private val mockChildren = mutableListOf(mockDataSnapshot, mockDataSnapshot, mockDataSnapshot)

    private lateinit var scenario: ActivityScenario<EmployeeListingsActivity>

    val mockEmployee1 = Employee(
        "SST501",
        "Sherlock",
        "Holmes",
        "16/05/1923",
        Role.EMPLOYEE,
        "Detective",
        "100000",
        "sholmes401@gmail.com"
    )

    val mockEmployee2 = Employee(
        "SST502",
        "Tim",
        "Allen",
        "16/05/1958",
        Role.ADMIN,
        "Actor",
        "30000",
        ""
    )

    val mockEmployee3 = Employee(
        "SST503",
        "Jon",
        "Hamm",
        "16/05/1967",
        Role.EMPLOYEE,
        "Actor",
        "85000",
        "GoHammOrGoHome@gmail.com"
    )

    @Before
    fun setUp() {
        loadKoinModules(module(override = true) {
            single("databaseReference") { mockDatabaseReference }
        })

        every { mockDataSnapshot.child(any()) } returns mockDataSnapshot
        every { mockDataSnapshot.getValue(Employee::class.java) } returnsMany mutableListOf(
            mockEmployee1,
            mockEmployee2,
            mockEmployee3
        )

        every { mockDataSnapshot.children } returns mockChildren

        scenario = launch(EmployeeListingsActivity::class.java)
    }

    @After
    fun tearDown() {
        clearAllMocks()
        stopKoin()
    }

    @Test
    fun `employee list is loaded from database`() {
        scenario.onActivity { activity ->
            activity.onDataChange(mockDataSnapshot)
            val adapter = activity.employee_list_view.adapter!!
            assertThat(adapter.itemCount).isEqualTo(3)

            var viewHolder = activity.employee_list_view.findViewHolderForAdapterPosition(0)!!
            assertThat(viewHolder.itemView.employee_member.text.toString())
                .isEqualTo("${mockEmployee1.employeeId} ${mockEmployee1.firstName} ${mockEmployee1.lastName} ACTIVE")

            viewHolder = activity.employee_list_view.findViewHolderForAdapterPosition(1)!!
            assertThat(viewHolder.itemView.employee_member.text.toString())
                .isEqualTo("${mockEmployee2.employeeId} ${mockEmployee2.firstName} ${mockEmployee2.lastName} INACTIVE")

            viewHolder = activity.employee_list_view.findViewHolderForAdapterPosition(2)!!
            assertThat(viewHolder.itemView.employee_member.text.toString())
                .isEqualTo("${mockEmployee3.employeeId} ${mockEmployee3.firstName} ${mockEmployee3.lastName} ACTIVE")

        }
    }

    @Test
    fun `clicking on Add Employee will launch AddEmployeeActivity`() {
        init()
        onView(withId(R.id.add_employee)).perform(click())
        intended(IntentMatchers.hasComponent(AddEmployeeActivity::class.java.canonicalName))
        release()
    }

    @Test
    fun `clicking on an employee launches ViewEmployeeActivity`() {
        init()
        scenario.onActivity { activity ->
            activity.onDataChange(mockDataSnapshot)

            val viewHolder = activity.employee_list_view.findViewHolderForAdapterPosition(0)!!
            viewHolder.itemView.performClick()
        }
        intended(IntentMatchers.hasComponent(ViewEmployeeActivity::class.java.canonicalName))
        release()

//        init() TODO use for demo
//        onView(withId(R.id.employee_list_view)).perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click())))
//        intended(IntentMatchers.hasComponent(ViewEmployeeActivity::class.java.canonicalName))
//        release()
    }
}