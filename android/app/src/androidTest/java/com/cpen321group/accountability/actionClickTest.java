package com.cpen321group.accountability;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.lifecycle.Lifecycle;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.ViewAssertion;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.RootMatchers;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.runner.RunWith;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
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
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.not;

import android.app.Activity;
import android.content.ComponentName;
import android.view.View;
import android.widget.DatePicker;

import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.cpen321group.accountability.mainscreen.chat.ChatFragment;
import com.cpen321group.accountability.mainscreen.chat.ChattingActivity;
import com.cpen321group.accountability.mainscreen.dashboard.DashboardFragment;
import com.cpen321group.accountability.welcome.RegisterActivity;
import com.cpen321group.accountability.welcome.WelcomeActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

@RunWith(AndroidJUnit4.class)
@LargeTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class actionClickTest {
    int count = 0;
    @Before
    public void setup(){
        FrontendConstants.userID = "100141214588378665776go";
        FrontendConstants.isAccountant = false;
        FrontendConstants.is_test = 1;
        FrontendConstants.is_subscribed = true;
        count = 0;
    }

    @Rule
    public ActivityScenarioRule<HomeScreenActivity> scenarioRule = new ActivityScenarioRule<>(HomeScreenActivity.class);

    @Test
    public void _a_addGoalTest() throws InterruptedException {
        Thread.sleep(3000);
        onView(withId(R.id.goal_button)).perform(click());
        count ++;
        Thread.sleep(3000);
        onView(withId(R.id.floating_action_button_goal)).perform(click());
        count ++;
        Thread.sleep(1000);
        onView(withId(R.id.goalNameInput)).perform(typeText("addGoalTest"));
        onView(withId(R.id.goalTargetPriceInput)).perform(typeText("1000"));
        onView(ViewMatchers.withId(R.id.goalTargetPriceInput)).perform(closeSoftKeyboard());
        onView(withId(R.id.datePickerButton)).perform(click());
        count ++;
        Thread.sleep(2000);
        onView(isAssignableFrom(DatePicker.class)).perform(setDate(2022,9,30));
        onView(withText("OK")).inRoot(isDialog()).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.goalCreateButton)).perform(click());
        count ++;
        Thread.sleep(2000);
        onView(withText("You have successfully added your new goal")).inRoot(withDecorView(not(getCurrentActivity().getWindow().getDecorView()))) .check(matches(isDisplayed()));
        assert (count<5);
    }

    @Test
    public void _b_updateGoalTest() throws InterruptedException {
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
        assert (count<5);
    }

    @Test
    public void _c_deleteGoalTest() throws InterruptedException {
        Thread.sleep(2000);
        onView(withId(R.id.goal_button)).perform(click());
        count ++;
        Thread.sleep(4000);
        onView(withId(R.id.goalRV))
                .perform(RecyclerViewActions.scrollToPosition(2));
        onView(withId(R.id.goalRV)).perform(
                RecyclerViewActions.actionOnItemAtPosition(2, MyViewAction.clickChildViewWithId(R.id.goalDelete)));
        count ++;
        Thread.sleep(2000);
        onView(withText("You have successfully deleted your selected goal")).inRoot(withDecorView(not(getCurrentActivity().getWindow().getDecorView()))) .check(matches(isDisplayed()));
        assert (count<5);
    }

    @Test
    public void _d_addTransactionTest() throws InterruptedException {
        Thread.sleep(3000);
        onView(withId(R.id.transaction_secondary_button)).perform(click());
        count ++;
        Thread.sleep(3000);
        onView(withId(R.id.floating_action_button_transaction)).perform(click());
        count ++;
        Thread.sleep(1000);

        onView(withId(R.id.transactionNameInput)).perform(typeText("TEST Transaction"));
        onView(withId(R.id.transactionAmountPriceInput)).perform(typeText("20000.05"));
        onView(withId(R.id.transactionAmountPriceInput)).perform(closeSoftKeyboard());

        onView(withId(R.id.transactionCategoryText)).perform(click());
        onView(withText("daily necessities")).inRoot(RootMatchers.isPlatformPopup()).perform(click());

        onView(withId(R.id.datePickerButton)).perform(click());
        count ++;
        Thread.sleep(2000);
        onView(isAssignableFrom(DatePicker.class)).perform(setDate(2022,7,30));
        onView(withText("OK")).inRoot(isDialog()).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.transactionCreateButton)).perform(click());
        count ++;
        Thread.sleep(2000);
        onView(withText("You have successfully added your new transaction")).inRoot(withDecorView(not(getCurrentActivity().getWindow().getDecorView()))) .check(matches(isDisplayed()));
        assert (count<5);
    }

    @Test
    public void _e_deleteTransactionTest() throws InterruptedException {
        Thread.sleep(2000);
        onView(withId(R.id.transaction_secondary_button)).perform(click());
        count ++;
        Thread.sleep(4000);
        onView(withId(R.id.transactionRV))
                .perform(RecyclerViewActions.scrollToPosition(0));
        onView(withId(R.id.transactionRV)).perform(
                RecyclerViewActions.actionOnItemAtPosition(0, MyViewAction.clickChildViewWithId(R.id.transactionDelete)));
        count ++;
        Thread.sleep(2000);
        onView(withText("You have successfully deleted your selected transaction")).inRoot(withDecorView(not(getCurrentActivity().getWindow().getDecorView()))) .check(matches(isDisplayed()));
        assert (count<5);
    }

    @Test
    public void _f_settingTest() throws InterruptedException {
        Thread.sleep(2000);
        onView(withId(R.id.home_settings)).perform(click());
        Thread.sleep(2000);
        count ++;
        assert (count < 5);
    }

    @Test
    public void _g_sendMessageTest() throws InterruptedException {
        Thread.sleep(2000);
        FragmentScenario<ChatFragment> mfragment = FragmentScenario.launchInContainer(ChatFragment.class);
        mfragment.moveToState(Lifecycle.State.STARTED);
        FrontendConstants.userID = "100141214588378665776go";
        count ++;
        onView(isRoot()).perform(WaitforHelper.waitFor(5000));
        onView(withId(R.id.chat_recycler))
                .perform(RecyclerViewActions.scrollToPosition(0));
        onView(withId(R.id.chat_recycler)).perform(
                RecyclerViewActions.actionOnItemAtPosition(0, MyViewAction.clickChildViewWithId(R.id.request_button_1)));
        count ++;
        Thread.sleep(5000);
        onView(withId(R.id.text_view)).perform(typeText("Hi!"));
        onView(withId(R.id.text_view)).perform(closeSoftKeyboard());
        onView(withId(R.id.send_button)).perform(click());
        Thread.sleep(2000);
        count ++;
        assert(count < 5);
    }

    @Test
    public void _h_viewHistoryTest() throws InterruptedException {
        Thread.sleep(2000);
        FragmentScenario<ChatFragment> mfragment = FragmentScenario.launchInContainer(ChatFragment.class);
        mfragment.moveToState(Lifecycle.State.STARTED);
        FrontendConstants.userID = "100141214588378665776go";
        count ++;
        onView(isRoot()).perform(WaitforHelper.waitFor(5000));
        onView(withId(R.id.chat_recycler))
                .perform(RecyclerViewActions.scrollToPosition(0));
        onView(withId(R.id.chat_recycler)).perform(
                RecyclerViewActions.actionOnItemAtPosition(0, MyViewAction.clickChildViewWithId(R.id.history_button)));
        Thread.sleep(2000);
        count ++;
        assert(count < 5);
    }

    @Test
    public void _i_addReviewTest() throws InterruptedException {
        Thread.sleep(2000);
        FragmentScenario<ChatFragment> mfragment = FragmentScenario.launchInContainer(ChatFragment.class);
        mfragment.moveToState(Lifecycle.State.STARTED);
        FrontendConstants.userID = "100141214588378665776go";
        count ++;
        onView(isRoot()).perform(WaitforHelper.waitFor(5000));
        onView(withId(R.id.chat_recycler))
                .perform(RecyclerViewActions.scrollToPosition(0));
        onView(withId(R.id.chat_recycler)).perform(
                RecyclerViewActions.actionOnItemAtPosition(0, MyViewAction.clickChildViewWithId(R.id.review_button)));
        count ++;
        Thread.sleep(5000);
        onView(withId(R.id.floating_action_button_review)).perform(click());
        count ++;
        Thread.sleep(1000);
        onView(withId(R.id.title_text)).perform(typeText("Test"));
        onView(withId(R.id.reviewcontentInput)).perform(typeText("Very nice accountant"));
        onView(ViewMatchers.withId(R.id.reviewcontentInput)).perform(closeSoftKeyboard());
        onView(withId(R.id.rate_text)).perform(click());
        onView(withText("5")).inRoot(withDecorView(not(getCurrentActivity().getWindow().getDecorView()))).check(matches(isDisplayed())).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.create_button)).perform(click());
        count ++;
        Thread.sleep(5000);
        assert(count < 5);
    }


    @Test
    public void _j_addReportTest() throws InterruptedException {
        Thread.sleep(3000);
        onView(withId(R.id.report_gen_button)).perform(click());
        count ++;
        Thread.sleep(3000);
        onView(withId(R.id.floating_action_button_report)).perform(click());
        count ++;
        Thread.sleep(2000);
        onView(withText("OK")).inRoot(isDialog()).perform(click());
        count ++;
        Thread.sleep(3000);
        assert (count<5);
    }

    @Test
    public void _k_updateReportTest() throws InterruptedException {
        Thread.sleep(3000);
        onView(withId(R.id.report_gen_button)).perform(click());
        count ++;
        Thread.sleep(4000);
        onView(withId(R.id.reportRV))
                .perform(RecyclerViewActions.scrollToPosition(0));
        onView(withId(R.id.reportRV)).perform(
                RecyclerViewActions.actionOnItemAtPosition(0, MyViewAction.clickChildViewWithId(R.id.reportCard)));
        Thread.sleep(2000);
        count ++;
        assert (count<5);
    }

    @Test
    public void _l_deleteReportTest() throws InterruptedException {
        Thread.sleep(3000);
        onView(withId(R.id.report_gen_button)).perform(click());
        count ++;
        Thread.sleep(4000);
        onView(withId(R.id.reportRV))
                .perform(RecyclerViewActions.scrollToPosition(2));
        onView(withId(R.id.reportRV)).perform(
                RecyclerViewActions.actionOnItemAtPosition(2, MyViewAction.clickChildViewWithId(R.id.reportDelete)));
        count ++;
        Thread.sleep(2000);
        assert (count<5);
    }
    @Test
    public void _m_userRequestSendMessageTest() throws InterruptedException {
        FrontendConstants.userID = "455937552go";
        FrontendConstants.isAccountant = true;
        Thread.sleep(2000);
        FragmentScenario<ChatFragment> mfragment = FragmentScenario.launchInContainer(ChatFragment.class);
        mfragment.moveToState(Lifecycle.State.STARTED);
        FrontendConstants.userID = "455937552go";
        count ++;
        onView(isRoot()).perform(WaitforHelper.waitFor(5000));
        onView(withId(R.id.chat_recycler))
                .perform(RecyclerViewActions.scrollToPosition(0));
        onView(withId(R.id.chat_recycler)).perform(
                RecyclerViewActions.actionOnItemAtPosition(0, MyViewAction.clickChildViewWithId(R.id.button_accept)));
        count ++;
        Thread.sleep(5000);
        onView(withId(R.id.text_view)).perform(typeText("Hi!"));
        onView(withId(R.id.text_view)).perform(closeSoftKeyboard());
        onView(withId(R.id.send_button)).perform(click());
        Thread.sleep(2000);
        count ++;
        assert(count < 5);
    }

    @Test
    public void _n_userRequestFinishTest() throws InterruptedException {
        FrontendConstants.userID = "455937552go";
        FrontendConstants.isAccountant = true;
        Thread.sleep(2000);
        FragmentScenario<ChatFragment> mfragment = FragmentScenario.launchInContainer(ChatFragment.class);
        mfragment.moveToState(Lifecycle.State.STARTED);
        FrontendConstants.userID = "455937552go";
        count++;
        onView(isRoot()).perform(WaitforHelper.waitFor(5000));
        onView(withId(R.id.chat_recycler))
                .perform(RecyclerViewActions.scrollToPosition(0));
        onView(withId(R.id.chat_recycler)).perform(
                RecyclerViewActions.actionOnItemAtPosition(0, MyViewAction.clickChildViewWithId(R.id.button_finish)));
        count++;
        Thread.sleep(5000);
        assert(count < 5);
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
