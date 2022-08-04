package com.cpen321group.accountability;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.cpen321group.accountability.welcome.register.RegisterActivity;
import com.cpen321group.accountability.welcome.WelcomeActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ButtonEspressoTest {

    @Rule
    public IntentsTestRule<WelcomeActivity> intentsTestRule =
            new IntentsTestRule<>(WelcomeActivity.class);

    @Test
    public void testRegisterButton() {
        onView(withId(R.id.welcome_register)).perform(click());

        // Activity under test is now finished.
        intended(hasComponent(RegisterActivity.class.getName()));
        onView(withId(R.id.sidn_in_view)).check(matches(withText("Register")));
    }
}
