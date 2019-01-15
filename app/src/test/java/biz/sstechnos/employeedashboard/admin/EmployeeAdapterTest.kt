package biz.sstechnos.employeedashboard.admin

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.widget.FrameLayout
import biz.sstechnos.employeedashboard.entity.Employee
import com.google.common.truth.Truth.assertThat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.mockk.every
import io.mockk.mockk
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyString
import org.robolectric.Robolectric
import org.robolectric.Robolectric.buildActivity
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class EmployeeAdapterTest {

    private lateinit var activity : EmployeeListingsActivity

    private lateinit var employeeAdapter : EmployeeAdapter
    private lateinit var employeeViewHolder : EmployeeViewHolder
    private lateinit var gson : Gson


    @Before
    fun setUp() {

        val employeeNameList = mutableListOf("SST505 Sam Harris",
            "SST506 Dave Stanford", "SST508 Tim Sans", "SST907 Juniper Octavious")
        employeeAdapter = EmployeeAdapter(employeeNameList)

        gson = mockk(relaxed = true)

//        activity = buildActivity(EmployeeListingsActivity::class.java, Intent())
//            .create().start().resume()
//            .get()

//        every { gson.fromJson("blah", Employee.class) } returns mutableListOf<Employee>()
    }

    @Test
    fun `adapter holds list of 4 items`() {
        assertThat(employeeAdapter.itemCount).isEqualTo(4)
    }

    // TODO learn to mock Gson().toJson call or mock context to pass that into FrameLayout during onCreateViewHolder

//    @Test
//    fun `view holder displays correct data at position 0`() {
//        employeeViewHolder = employeeAdapter.onCreateViewHolder(FrameLayout(activity), 0)
//        employeeAdapter.onBindViewHolder(employeeViewHolder, 0)
//        assertThat(employeeViewHolder.employee.text).isEqualTo("SST505 Sam Harris")
//    }
//
//    @Test
//    fun `view holder displays correct data at position 1`() {
//        employeeViewHolder = employeeAdapter.onCreateViewHolder(FrameLayout(activity), 1)
//        employeeAdapter.onBindViewHolder(employeeViewHolder, 1)
//        assertThat(employeeViewHolder.employee.text).isEqualTo("SST506 Dave Stanford")
//    }
//
//    @Test
//    fun `view holder displays correct data at position 2`() {
//        employeeViewHolder = employeeAdapter.onCreateViewHolder(FrameLayout(activity), 2)
//        employeeAdapter.onBindViewHolder(employeeViewHolder, 2)
//        assertThat(employeeViewHolder.employee.text).isEqualTo("SST 508 Tim Sans")
//    }
//
//    @Test
//    fun `view holder displays correct data at position 3`() {
//        employeeViewHolder = employeeAdapter.onCreateViewHolder(FrameLayout(activity), 3)
//        employeeAdapter.onBindViewHolder(employeeViewHolder, 3)
//        assertThat(employeeViewHolder.employee.text).isEqualTo("SST907 Juniper Octavious")
//    }

}