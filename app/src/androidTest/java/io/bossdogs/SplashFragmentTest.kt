package io.bossdogs

import android.os.SystemClock
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.airbnb.lottie.LottieAnimationView
import io.bossdogs.ui.list.BreedsFragment
import io.bossdogs.ui.splash.SplashFragment
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SplashFragmentTest {

    companion object {
        private const val SPLASH_DELAY_MS = 3000L
        private const val BUFFER_MS = 500L
        private const val WAIT_MS = SPLASH_DELAY_MS + BUFFER_MS
    }

    private lateinit var scenario: ActivityScenario<MainActivity>

    @Before
    fun setUp() {
        scenario = ActivityScenario.launch(MainActivity::class.java)
    }

    @After
    fun tearDown() {
        scenario.close()
    }

    @Test
    fun appbarHiddenOnSplash() {
        // Verify appbar is hidden on the SplashFragment
        scenario.onActivity { activity ->
            val frag = activity.supportFragmentManager
                .findFragmentById(R.id.fragment_container)

            assert(
                frag is SplashFragment

            ) { "Expected initial fragment to be SplashFragment, but was ${frag?.javaClass?.simpleName}" }


            val isShowing = activity.supportActionBar?.isShowing == false
            assert(
                isShowing
            ) {
                "Expected ActionBar to be hidden during splash, but was showing"
            }
        }
    }


    @Test
    fun loadingAnimationPlays() {
        // Wait for the animation to begin
        SystemClock.sleep(BUFFER_MS)

        // Check it's animating
        onView(withId(R.id.splash_animation))
            .check(matches(isDisplayed()))
            .check { view, _ ->
                val lottie = view as LottieAnimationView
                assert(
                    lottie.isAnimating
                ) {
                    "Expected Lottie animation to be playing"
                }
            }
    }

    @Test
    fun navigationToListIsPerformedAfterTheAnimationCompletes() {
        // Wait for the animation to complete
        SystemClock.sleep(WAIT_MS)

        // Check what the current fragment is (should be list)
        scenario.onActivity { act ->
            val frag = act.supportFragmentManager
                .findFragmentById(R.id.fragment_container)
            assert(
                frag is BreedsFragment
            ) {
                "Expected BreedsFragment after splash navigation, but was $frag"
            }
        }
    }
}