package biz.sstechnos.employeedashboard.registration

import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario.launch
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents.*
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import biz.sstechnos.employeedashboard.R
import com.google.android.gms.tasks.Task
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
class ContactInfoActivityTest: KoinTest {
    private val scenario = launch(ContactInfoActivity::class.java)

    private val mockDatabaseReference: DatabaseReference = mockk()
    private val mockTask: Task<Void> = mockk()

    @Before
    fun setUp() {
        loadKoinModules(module(override = true) {
            single("mockDatabaseReference") { mockDatabaseReference }
        })

        every { mockDatabaseReference.child(any()) } returns mockDatabaseReference
    }

    @After
    fun tearDown() {
        clearMocks(mockTask)
        scenario.moveToState(Lifecycle.State.DESTROYED)
        stopKoin()
    }

    @Test
    fun `when onComplete is called with a successful task then start the next activity`() {
        every { mockTask.isSuccessful } returns true

        init()
        scenario.onActivity { activity -> activity.onComplete(mockTask) }
        intended(hasComponent(UploadIdCardActivity::class.java.canonicalName))
        release()
    }

    @Test
    fun `when all fields are not filled out and the button is clicked then display CookieBar telling user that all required fields must be filled out`() {
        onView(withId(R.id.submit_button)).perform(scrollTo(), click())
        onView(withId(R.id.tv_title)).check(matches(withText("Employee contact information required!")))
    }

    @Test
    fun `when onComplete is called with an unsuccessful task then display a CookieBar`() {
        every { mockTask.isSuccessful } returns false
        every { mockTask.exception } returns Exception("Firebase upload failed", Throwable())

        scenario.onActivity { activity -> activity.onComplete(mockTask) }
        onView(withId(R.id.tv_title)).check(matches(withText("Upload failed!")))
    }
}