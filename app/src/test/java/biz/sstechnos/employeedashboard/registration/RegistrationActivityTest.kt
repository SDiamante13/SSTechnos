package biz.sstechnos.employeedashboard.registration

import android.content.Context
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ActivityScenario.launch
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.*
import androidx.test.espresso.intent.Intents.*
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import biz.sstechnos.employeedashboard.R
import biz.sstechnos.employeedashboard.entity.Employee
import biz.sstechnos.employeedashboard.entity.Role
import biz.sstechnos.employeedashboard.testDI.dataTestModule
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import io.mockk.every
import io.mockk.mockk
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.standalone.StandAloneContext
import org.koin.standalone.inject
import org.koin.test.KoinTest


@RunWith(AndroidJUnit4::class)
class RegistrationActivityTest : KoinTest {

    private val mockDatabaseReference : DatabaseReference by inject(name = "mockDatabaseReference")

    private lateinit var scenario : ActivityScenario<RegistrationActivity>

    private lateinit var mockDataSnapshot : DataSnapshot
    private lateinit var mockie : Employee


    private val context = ApplicationProvider.getApplicationContext<Context>()

    @Before
    fun setUp() {
        StandAloneContext.loadKoinModules(dataTestModule)
        FirebaseApp.initializeApp(context)

        val mockEmployee = Employee("SST501",
            "Sherlock",
            "Holmes",
            "16/05/1923",
            Role.EMPLOYEE,
            "Detective",
            20000.00,
            "")

        mockDataSnapshot = mockk(relaxed = true)
        mockie = mockk(relaxed = true)
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