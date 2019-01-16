package biz.sstechnos.employeedashboard.registration

import android.content.Context
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ActivityScenario.launch
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import biz.sstechnos.employeedashboard.R
import biz.sstechnos.employeedashboard.entity.Employee
import biz.sstechnos.employeedashboard.entity.Role
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import io.mockk.every
import io.mockk.mockk
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.dsl.module.module
import org.koin.standalone.StandAloneContext.loadKoinModules
import org.koin.test.KoinTest


@RunWith(AndroidJUnit4::class)
class RegistrationActivityTest : KoinTest {

    private val mockDatabaseReference = mockk<DatabaseReference>(relaxed = true)

    private val mockDataSnapshot = mockk<DataSnapshot>(relaxed = true)

    private val context = getApplicationContext<Context>()

    private lateinit var scenario : ActivityScenario<RegistrationActivity>

    @Before
    fun setUp() {
        loadKoinModules(module(override = true) {
            single { mockDatabaseReference }
        })

        val mockEmployee = Employee("SST501",
            "Sherlock",
            "Holmes",
            "16/05/1923",
            Role.EMPLOYEE,
            "Detective",
            20000.00,
            "")

        every { mockDataSnapshot.child(any()) } returns mockDataSnapshot
        every { mockDataSnapshot.getValue(Employee::class.java) } returns mockEmployee

        scenario = launch(RegistrationActivity::class.java)
    }

    @Test
    fun `validate new user`() {
        onView(withId(R.id.employeeId_editText)).perform(ViewActions.typeText("SST501"))
        onView(withId(R.id.lastName_editText)).perform(ViewActions.typeText("Holmes"))
        // FIXME Find out how to type a number into an editText using espresso
        onView(withId(R.id.dateOfBirth_editText)).perform(ViewActions.typeText("16/05/1923"))

        // TODO this passes but I have no way to accurately assert. Handler is sleeping 7 secs before transistion to next activity
        // TODO Cannot find a way to check if Cookiebar is being shown
//        init()
//        scenario.onActivity { activity -> activity.onDataChange(mockDataSnapshot) }
//        intended(hasComponent(ContactInfoActivity::class.java.canonicalName))
//        release()
//        onView(withText("Account successfully linked!")).check(matches(isDisplayed()))
//        onView(withText("Account successfully linked!"))
//            .check(matches(withEffectiveVisibility(
//                ViewMatchers.Visibility.VISIBLE
//            )))
    }
}