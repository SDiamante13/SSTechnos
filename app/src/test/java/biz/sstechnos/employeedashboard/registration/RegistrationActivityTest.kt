package biz.sstechnos.employeedashboard.registration

import android.widget.EditText
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario.launch
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.intent.Intents.*
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import biz.sstechnos.employeedashboard.R
import biz.sstechnos.employeedashboard.entity.Employee
import biz.sstechnos.employeedashboard.entity.Role
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
import org.koin.standalone.StandAloneContext.loadKoinModules
import org.koin.standalone.StandAloneContext.stopKoin
import org.koin.test.KoinTest


@RunWith(AndroidJUnit4::class)
class RegistrationActivityTest: KoinTest {

    private val mockDatabaseReference: DatabaseReference = mockk()

    private val mockDataSnapshot: DataSnapshot = mockk()

    private val scenario = launch(RegistrationActivity::class.java)

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
            "20000",
            "")

        every { mockDataSnapshot.child(any()) } returns mockDataSnapshot
        every { mockDataSnapshot.getValue(Employee::class.java) } returns mockEmployee
    }

    @After
    fun tearDown() {
        clearAllMocks()
        scenario.moveToState(Lifecycle.State.DESTROYED)
        stopKoin()
    }

    @Test
    fun `validate new user`() {
        onView(withId(R.id.employeeId_editText)).perform(ViewActions.typeText("SST501"))
        onView(withId(R.id.lastName_editText)).perform(ViewActions.typeText("Holmes"))
        scenario.onActivity { activity ->
            val dateOfBirth = activity.findViewById<EditText>(R.id.dateOfBirth_editText)
            dateOfBirth.text.append("16/05/1923")
        }

        init()
        scenario.onActivity { activity -> activity.onDataChange(mockDataSnapshot) }
        intended(hasComponent(ContactInfoActivity::class.java.canonicalName))
        release()
    }

    @Test
    fun `invalidate user prompts CookieBar with error message`() {
        onView(withId(R.id.employeeId_editText)).perform(ViewActions.typeText("SST609"))
        onView(withId(R.id.lastName_editText)).perform(ViewActions.typeText("James"))
        scenario.onActivity { activity ->
            val dateOfBirth = activity.findViewById<EditText>(R.id.dateOfBirth_editText)
            dateOfBirth.text.append("25/10/1979")
        }

        scenario.onActivity { activity -> activity.onDataChange(mockDataSnapshot) }
        onView(withId(R.id.tv_title)).check(ViewAssertions.matches(ViewMatchers.withText("Invalid Account!")))
    }
}