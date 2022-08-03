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
import android.content.ComponentName;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.TimePicker;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.lifecycle.Lifecycle;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.ViewAssertion;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.contrib.PickerActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.cpen321group.accountability.mainscreen.chat.ChatFragment;
import com.cpen321group.accountability.mainscreen.chat.ReviewActivity;
import com.cpen321group.accountability.mainscreen.dashboard.DashboardFragment;
import com.cpen321group.accountability.mainscreen.dashboard.functionpack.DatePickerFragment;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

@RunWith(AndroidJUnit4.class)
@LargeTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
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
    public void _a_clickGoalButtonTest() throws InterruptedException {
        Thread.sleep(3000);
        onView(withId(R.id.goal_button)).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.floating_action_button_goal)).check(matches(isDisplayed()));
        onView(withId(R.id.goalRV)).check(matches(isDisplayed()));
    }

    @Test
    public void _b_createButtonTest() throws InterruptedException {
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
    public void _c_checkNameInputTest() throws InterruptedException {
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
    public void _d_checkPriceInputTest() throws InterruptedException {
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
    public void _e_checkDatePickerTest() throws InterruptedException {
        Thread.sleep(3000);
        onView(withId(R.id.goal_button)).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.floating_action_button_goal)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.datePickerButton)).perform(click());
        Thread.sleep(5000);
        onView(isAssignableFrom(DatePicker.class)).check(matches(isDisplayed()));
    }

    @Test
    public void _f_createWithNoDateTest() throws InterruptedException {
        Thread.sleep(3000);
        onView(withId(R.id.goal_button)).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.floating_action_button_goal)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.goalNameInput)).perform(typeText("Study"));
        onView(withId(R.id.goalTargetPriceInput)).perform(typeText("20000"));
        onView(ViewMatchers.withId(R.id.goalTargetPriceInput)).perform(closeSoftKeyboard());
        Thread.sleep(1000);
        onView(withId(R.id.goalCreateButton)).perform(click());
        onView(withText("Some necessary information missing!")).inRoot(withDecorView(not(getCurrentActivity().getWindow().getDecorView()))) .check(matches(isDisplayed()));
    }

    @Test
    public void _g_createWithNoNameInputTest() throws InterruptedException {
        Thread.sleep(3000);
        onView(withId(R.id.goal_button)).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.floating_action_button_goal)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.goalTargetPriceInput)).perform(typeText("20000"));
        onView(ViewMatchers.withId(R.id.goalTargetPriceInput)).perform(closeSoftKeyboard());
        onView(withId(R.id.datePickerButton)).perform(click());
        Thread.sleep(2000);
        onView(isAssignableFrom(DatePicker.class)).perform(setDate(2022,8,30));
        onView(withText("OK")).inRoot(isDialog()).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.goalCreateButton)).perform(click());
        onView(withText("Some necessary information missing!")).inRoot(withDecorView(not(getCurrentActivity().getWindow().getDecorView()))) .check(matches(isDisplayed()));
    }

    @Test
    public void _h_createSuccessTest() throws InterruptedException {
        Thread.sleep(3000);
        onView(withId(R.id.goal_button)).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.floating_action_button_goal)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.goalNameInput)).perform(typeText("Study"));
        onView(withId(R.id.goalTargetPriceInput)).perform(typeText("20000"));
        onView(ViewMatchers.withId(R.id.goalTargetPriceInput)).perform(closeSoftKeyboard());
        onView(withId(R.id.datePickerButton)).perform(click());
        Thread.sleep(2000);
        onView(isAssignableFrom(DatePicker.class)).perform(setDate(2022,8,30));
        onView(withText("OK")).inRoot(isDialog()).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.goalCreateButton)).perform(click());
        Thread.sleep(2000);
        onView(withText("You have successfully added your new goal")).inRoot(withDecorView(not(getCurrentActivity().getWindow().getDecorView()))) .check(matches(isDisplayed()));
    }
    @Test
    public void _i_createWithNoPriceInputTest() throws InterruptedException {
        Thread.sleep(3000);
        onView(withId(R.id.goal_button)).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.floating_action_button_goal)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.goalNameInput)).perform(typeText("20000"));
        onView(ViewMatchers.withId(R.id.goalNameInput)).perform(closeSoftKeyboard());
        onView(withId(R.id.datePickerButton)).perform(click());
        Thread.sleep(2000);
        onView(isAssignableFrom(DatePicker.class)).perform(setDate(2022,8,30));
        onView(withText("OK")).inRoot(isDialog()).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.goalCreateButton)).perform(click());
        onView(withText("Some necessary information missing!")).inRoot(withDecorView(not(getCurrentActivity().getWindow().getDecorView()))) .check(matches(isDisplayed()));
    }

    @Test
    public void _j_deleteTest() throws InterruptedException {
        Thread.sleep(2000);
        onView(withId(R.id.goal_button)).perform(click());
        Thread.sleep(4000);
        onView(withId(R.id.goalRV))
                .perform(RecyclerViewActions.scrollToPosition(2));
        onView(withId(R.id.goalRV)).perform(
                RecyclerViewActions.actionOnItemAtPosition(2, MyViewAction.clickChildViewWithId(R.id.goalDelete)));
        Thread.sleep(2000);
        onView(withText("You have successfully deleted your selected goal")).inRoot(withDecorView(not(getCurrentActivity().getWindow().getDecorView()))) .check(matches(isDisplayed()));
    }

    @Test
    public void _k_newSavingButtonTest() throws InterruptedException {
        Thread.sleep(2000);
        onView(withId(R.id.goal_button)).perform(click());
        Thread.sleep(4000);
        onView(withId(R.id.goalRV))
                .perform(RecyclerViewActions.scrollToPosition(0));
        onView(withId(R.id.goalRV)).perform(
                RecyclerViewActions.actionOnItemAtPosition(0, MyViewAction.clickChildViewWithId(R.id.goalSave)));
        Thread.sleep(2000);
        onView(withId(R.id.goalCurrentPriceInput)).check(matches(isDisplayed()));
        onView(withId(R.id.goalUpdateButton)).check(matches(isDisplayed()));
    }

    @Test
    public void _l_newSavingPriceInputTest() throws InterruptedException {
        Thread.sleep(2000);
        onView(withId(R.id.goal_button)).perform(click());
        Thread.sleep(4000);
        onView(withId(R.id.goalRV))
                .perform(RecyclerViewActions.scrollToPosition(0));
        onView(withId(R.id.goalRV)).perform(
                RecyclerViewActions.actionOnItemAtPosition(0, MyViewAction.clickChildViewWithId(R.id.goalSave)));
        Thread.sleep(2000);
        onView(withId(R.id.goalCurrentPriceInput)).perform(click());
        assert (isSoftKeyboardShown());
    }

    @Test
    public void _m_newSavingWithNoPriceInputTest() throws InterruptedException {
        Thread.sleep(2000);
        onView(withId(R.id.goal_button)).perform(click());
        Thread.sleep(4000);
        onView(withId(R.id.goalRV))
                .perform(RecyclerViewActions.scrollToPosition(0));
        onView(withId(R.id.goalRV)).perform(
                RecyclerViewActions.actionOnItemAtPosition(0, MyViewAction.clickChildViewWithId(R.id.goalSave)));
        Thread.sleep(2000);
        onView(withId(R.id.goalUpdateButton)).perform(click());
        onView(withText("Some necessary information missing!")).inRoot(withDecorView(not(getCurrentActivity().getWindow().getDecorView()))) .check(matches(isDisplayed()));
    }

    @Test
    public void _n_newSavingSuccessTest() throws InterruptedException {
        Thread.sleep(2000);
        onView(withId(R.id.goal_button)).perform(click());
        Thread.sleep(4000);
        onView(withId(R.id.goalRV))
                .perform(RecyclerViewActions.scrollToPosition(0));
        onView(withId(R.id.goalRV)).perform(
                RecyclerViewActions.actionOnItemAtPosition(0, MyViewAction.clickChildViewWithId(R.id.goalSave)));
        Thread.sleep(2000);
        onView(withId(R.id.goalCurrentPriceInput)).perform(typeText("2000"));
        onView(ViewMatchers.withId(R.id.goalCurrentPriceInput)).perform(closeSoftKeyboard());
        onView(withId(R.id.goalUpdateButton)).perform(click());
        Thread.sleep(2000);
        onView(withText("You have successfully updated your selected goal")).inRoot(withDecorView(not(getCurrentActivity().getWindow().getDecorView()))) .check(matches(isDisplayed()));
    }

    @Test
    public void _o_clickGoalButtonWithNoGoalTest() throws InterruptedException {
        FrontendConstants.userID = "12345678go";
        Thread.sleep(3000);
        onView(withId(R.id.goal_button)).perform(click());
        Thread.sleep(2000);
        onView(withId(R.id.floating_action_button_goal)).check(matches(isDisplayed()));
        onView(withId(R.id.goalRV)).check(matches(isDisplayed()));
        onView(withText("You don't have any goal set.")).inRoot(withDecorView(not(getCurrentActivity().getWindow().getDecorView()))) .check(matches(isDisplayed()));
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
