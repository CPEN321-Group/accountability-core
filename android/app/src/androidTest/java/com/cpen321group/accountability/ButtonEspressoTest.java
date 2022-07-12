package com.cpen321group.accountability;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import com.cpen321group.accountability.mainScreen.dashboard.DashboardFragment;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ButtonEspressoTest {

    @Rule
    public ActivityTestRule<HomeScreenActivity> activityRule =
            new ActivityTestRule<>(HomeScreenActivity.class);

    @Test
    public void listGoesOverTheFold() {
        onView(withId(R.id.transaction_secondary_button))
                .perform(click())
                .check(matches(isDisplayed()));
    }
}
