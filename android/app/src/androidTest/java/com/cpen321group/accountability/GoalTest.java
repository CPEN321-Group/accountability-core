package com.cpen321group.accountability;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.PickerActions.setDate;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.not;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.lifecycle.Lifecycle;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.ViewAssertion;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.contrib.PickerActions;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.cpen321group.accountability.mainscreen.chat.ChatFragment;
import com.cpen321group.accountability.mainscreen.dashboard.DashboardFragment;
import com.cpen321group.accountability.mainscreen.dashboard.functionpack.DatePickerFragment;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class GoalTest {
    FragmentScenario<DashboardFragment> mfragment;
    @Before
    public void setup(){
        FrontendConstants.userID = "100141214588378665776go";
        FrontendConstants.isAccountant = false;
        FrontendConstants.is_test = 1;
        mfragment = FragmentScenario.launchInContainer(DashboardFragment.class);
        mfragment.moveToState(Lifecycle.State.STARTED);
    }

    @Rule
    public IntentsTestRule<FragmentScenario.EmptyFragmentActivity> intentsTestRule =
            new IntentsTestRule(FragmentScenario.EmptyFragmentActivity.class);
    @Test
    public void clickGoalButtonTest() throws InterruptedException {
        Thread.sleep(3000);
        onView(withId(R.id.goal_button)).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.floating_action_button_goal)).check(matches(isDisplayed()));
        onView(withId(R.id.goalRV)).check(matches(isDisplayed()));
    }

    @Test
    public void createButtonTest() throws InterruptedException {
        Thread.sleep(3000);
        onView(withId(R.id.goal_button)).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.floating_action_button_goal)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.goalNameInput)).check(matches(isDisplayed()));
        onView(withId(R.id.goalTargetPriceInput)).check(matches(isDisplayed()));
        onView(withId(R.id.datePickerButton)).check(matches(isDisplayed()));
        onView(withId(R.id.goalCreateButton)).check(matches(isDisplayed()));
    }

    @Test
    public void checkNameInputTest() throws InterruptedException {
        Thread.sleep(3000);
        onView(withId(R.id.goal_button)).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.floating_action_button_goal)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.goalNameInput)).perform(click());
        Thread.sleep(5000);
        assert (isSoftKeyboardShown());
    }

    @Test
    public void checkPriceInputTest() throws InterruptedException {
        Thread.sleep(3000);
        onView(withId(R.id.goal_button)).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.floating_action_button_goal)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.goalTargetPriceInput)).perform(click());
        Thread.sleep(5000);
        assert (isSoftKeyboardShown());
    }

    @Test
    public void checkDatePickerTest() throws InterruptedException {
        Thread.sleep(3000);
        onView(withId(R.id.goal_button)).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.floating_action_button_goal)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.datePickerButton)).perform(click());
        Thread.sleep(5000);
        onView(withText("OK")).inRoot(isDialog()).check(matches(isDisplayed()));
        onView(withClassName(Matchers.equalTo(Dialog.class.getName()))).inRoot(isDialog()).check(matches(isDisplayed()));
        //onView(withText("30")).inRoot(isDialog()).perform(click());
    }

    private boolean isSoftKeyboardShown() {
        final InputMethodManager imm = (InputMethodManager) getCurrentActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        return imm.isAcceptingText();
    }

    private Activity getCurrentActivity() {
        final Activity[] activity = new Activity[1];
        onView(isRoot()).check(new ViewAssertion() {
            @Override
            public void check(View view, NoMatchingViewException noViewFoundException) {
                activity[0] = (Activity) view.findViewById(android.R.id.content).getContext();
            }
        });
        return activity[0];
    }

}
