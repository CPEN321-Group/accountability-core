package com.cpen321group.accountability;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import com.cpen321group.accountability.welcome.WelcomeActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ButtonEspressoTest {

    @Rule
    public ActivityTestRule<WelcomeActivity> activityRule =
            new ActivityTestRule<>(WelcomeActivity.class);

    @Test
    public void testLogin() {
        onView(withId(R.id.welcome_register)).perform(click());
        onView(withId(R.id.sidn_in_view)).check(matches(withText("Register")));
    }
}
