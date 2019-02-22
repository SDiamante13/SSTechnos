package biz.sstechnos.employeedashboard

import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario.launch
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents.*
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import biz.sstechnos.employeedashboard.dashboard.DashboardActivity
import biz.sstechnos.employeedashboard.registration.RegistrationActivity
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
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
class LoginActivityTest: KoinTest {

    private val scenario = launch(LoginActivity::class.java)

    private val mockFirebaseAuth: FirebaseAuth = mockk()
    private val mockTask: Task<AuthResult> = mockk()
    private val mockUser: FirebaseUser = mockk()

    private val username = "lilJon"
    private val password = "eastside"

    @Before
    fun setUp() {
        loadKoinModules(module(override = true) {
            single("fireBaseAuth") { mockFirebaseAuth }
        })
    }

    @After
    fun tearDown() {
        clearMocks(mockTask, mockUser)
        scenario.moveToState(Lifecycle.State.DESTROYED)
        stopKoin()
    }

    @Test
    fun `successful login launches DashboardActivity`() {
        every { mockTask.isSuccessful } returns true
        every { mockTask.result.user } returns mockUser
        every { mockUser.isEmailVerified } returns true
        every { mockUser.email } returns "eastsideboyz@gmail.com"

        onView(withId(R.id.username_editText)).perform(typeText(username))
        onView(withId(R.id.password_editText)).perform(typeText(password))
        closeSoftKeyboard()

        init()
        scenario.onActivity { activity -> activity.onComplete(mockTask) }
        intended(hasComponent(DashboardActivity::class.java.canonicalName))
        release()
    }

    @Test
    fun `unSuccessful login due to unverified login displays CookieBar about unverified email`() {
        every { mockTask.isSuccessful } returns true
        every { mockTask.result.user } returns mockUser
        every { mockUser.isEmailVerified } returns false

        onView(withId(R.id.username_editText)).perform(typeText(username))
        onView(withId(R.id.password_editText)).perform(typeText(password))
        closeSoftKeyboard()

        scenario.onActivity { activity -> activity.onComplete(mockTask) }

        onView(withId(R.id.tv_title)).check(matches(ViewMatchers.withText("Login attempt unsuccessful!")))
    }

    @Test
    fun `unSuccessful login due to missing fields displays CookieBar about validation error`() {
        onView(withId(R.id.username_editText)).perform(typeText(username))
        closeSoftKeyboard()

        onView(withId(R.id.signIn_button)).perform(click())
        onView(withId(R.id.tv_title)).check(matches(ViewMatchers.withText("Login attempt failed!")))
    }

    @Test
    fun `clicking on Register Button launches RegistrationActivity`() {
        init()
        onView(withId(R.id.register_button)).perform(click())
        intended(hasComponent(RegistrationActivity::class.java.canonicalName))
        release()
    }
}