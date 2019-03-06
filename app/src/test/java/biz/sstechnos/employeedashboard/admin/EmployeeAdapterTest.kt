package biz.sstechnos.employeedashboard.admin

import android.content.Intent
import android.widget.FrameLayout
import com.google.common.truth.Truth.assertThat
import com.google.firebase.database.DatabaseReference
import io.mockk.clearAllMocks
import io.mockk.mockk
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.dsl.module.module
import org.koin.standalone.StandAloneContext.loadKoinModules
import org.koin.standalone.StandAloneContext.stopKoin
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class EmployeeAdapterTest {

    private val mockDatabaseReference: DatabaseReference = mockk()

    val employeeNameList = mutableListOf("SST101 Vinny Tore ACTIVE", "SST102 Sunny Bolt INACTIVE", "SST103 Terry Gamble ACTIVE")

    val employeeAdapter = EmployeeAdapter(employeeNameList)

    lateinit var employeeViewHolder: EmployeeViewHolder
    lateinit var activity: EmployeeListingsActivity

    @Before
    fun setUp() {
        loadKoinModules(module(override = true) {
            single("databaseReference") { mockDatabaseReference }
        })

        activity = Robolectric.buildActivity(EmployeeListingsActivity::class.java, Intent()).get()

        employeeViewHolder = employeeAdapter.onCreateViewHolder(FrameLayout(activity), 0)
    }

    @After
    fun tearDown() {
        clearAllMocks()
        stopKoin()
    }

    @Test
    fun `adapter should have 3 items initially`() {
        assertThat(employeeAdapter.items.size).isEqualTo(3)
    }

    @Test
    fun `view holder has correctly displayed fields`() {
        employeeAdapter.onBindViewHolder(employeeViewHolder, 0)
        assertThat(employeeViewHolder.employee.text.toString()).isEqualTo(employeeNameList[0])

        employeeAdapter.onBindViewHolder(employeeViewHolder, 1)
        assertThat(employeeViewHolder.employee.text.toString()).isEqualTo(employeeNameList[1])

        employeeAdapter.onBindViewHolder(employeeViewHolder, 2)
        assertThat(employeeViewHolder.employee.text.toString()).isEqualTo(employeeNameList[2])
    }
}