package biz.sstechnos.employeedashboard.registration

import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario.launch
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.init
import androidx.test.espresso.intent.Intents.release
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import biz.sstechnos.employeedashboard.LoginActivity
import biz.sstechnos.employeedashboard.R
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import io.mockk.clearMocks
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
class AccountCreationActivityTest: KoinTest {

    private val mockFirebaseAuth: FirebaseAuth = mockk(relaxed = true)
    private val mockDatabaseReference: DatabaseReference = mockk(relaxed = true)
    private val mockAuthTask: Task<AuthResult> = mockk()
    private val void: Void = mockk()

    private val scenario = launch(AccountCreationActivity::class.java)

    @Before
    fun setUp() {
        loadKoinModules(module(override = true) {
            single("fireBaseAuth") { mockFirebaseAuth }
            single("databaseReference") { mockDatabaseReference }
        })
    }

    @After
    fun tearDown() {
        clearMocks(mockAuthTask)
        scenario.moveToState(Lifecycle.State.DESTROYED)
        stopKoin()
    }

    @Test
    fun `successful account creation results in email verification`() {
        every { mockAuthTask.isSuccessful } returns true

        scenario.onActivity { activity -> activity.onComplete(mockAuthTask) }
        scenario.onActivity { activity -> activity.onSuccess(void) }

        onView(withId(R.id.tv_title)).check(matches(withText("Verification Email Sent!")))
    }

    @Test
    fun `when fields are filled out and button is clicked then an account will be created and the user will be sent back to Login Activity`() {
        onView(withId(R.id.username_editText)).perform(typeText("DirtyMyles"))
        onView(withId(R.id.password_editText)).perform(typeText("DGTAllDay"))
        onView(withId(R.id.confirmPassword_editText)).perform(scrollTo(), typeText("DGTAllDay"))

        init()
        onView(withId(R.id.create_button)).perform(scrollTo(), click())
        Intents.intended(IntentMatchers.hasComponent(LoginActivity::class.java.canonicalName))
        release()
    }

    @Test
    fun `when any one field is not filled out show a Cookie Bar explaining the error`() {
        onView(withId(R.id.username_editText)).perform(typeText("Yoyoman"))
        onView(withId(R.id.create_button)).perform(scrollTo(), click())

        onView(withId(R.id.tv_title)).check(matches(withText("Account information required!")))
    }

    @Test
    fun `when the password fields do not match show a Cookie Bar explaining the error`() {
        onView(withId(R.id.username_editText)).perform(typeText("DirtyMyles"))
        onView(withId(R.id.password_editText)).perform(typeText("DGTAllDay"))
        onView(withId(R.id.confirmPassword_editText)).perform(scrollTo(), typeText("DGTAllDay123"))

        onView(withId(R.id.create_button)).perform(scrollTo(), click())

        onView(withId(R.id.tv_title)).check(matches(withText("Passwords do not match!")))
    }
}