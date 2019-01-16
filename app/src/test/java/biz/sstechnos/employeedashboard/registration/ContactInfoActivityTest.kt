package biz.sstechnos.employeedashboard.registration

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.*
import androidx.test.espresso.intent.Intents.*
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import biz.sstechnos.employeedashboard.R
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import io.mockk.every
import io.mockk.mockk
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.dsl.module.module
import org.koin.standalone.StandAloneContext
import org.koin.standalone.StandAloneContext.stopKoin
import org.koin.test.KoinTest


@RunWith(AndroidJUnit4::class)
class ContactInfoActivityTest : KoinTest {
    private val mockDatabaseReference = mockk<DatabaseReference>(relaxed = true)
    private lateinit var scenario : ActivityScenario<ContactInfoActivity>
    private val mockTask = mockk<Task<Void>>(relaxed = true)


    @Before
    fun setUp() {
        StandAloneContext.loadKoinModules(module(override = true) {
            single("mockDatabaseReference") { mockDatabaseReference }
        })
        every { mockDatabaseReference.child(any()) } returns mockDatabaseReference
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun `when onComplete is called with a successful task then start the next activity`() {
        every { mockTask.isSuccessful } returns true

        scenario = ActivityScenario.launch(ContactInfoActivity::class.java)

        init()
        scenario.onActivity { activity -> activity.onComplete(mockTask) }
        intended(hasComponent(UploadIdCardActivity::class.java.canonicalName))
        release()
    }

    @Test
    fun `when all fields are not filled out and the button is clicked then display Cookiebar telling user that all required fields must be filled out`() {
        scenario = ActivityScenario.launch(ContactInfoActivity::class.java)

        onView(withId(R.id.submit_button)).perform(scrollTo(), click())
        onView(withId(R.id.tv_title)).check(matches(withText("Employee contact information required!")))
    }

    @Test
    fun `when onComplete is called with an unsuccessful task then display a Cookie Bar`() {
        every { mockTask.isSuccessful } returns false

        scenario = ActivityScenario.launch(ContactInfoActivity::class.java)
        scenario.onActivity { activity -> activity.onComplete(mockTask) }
        onView(withId(R.id.tv_title)).check(matches(withText("Upload failed!")))
    }
}