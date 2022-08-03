package com.cpen321group.accountability;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.not;

import android.app.Activity;
import android.content.ComponentName;
import android.view.View;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.lifecycle.Lifecycle;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.ViewAssertion;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.cpen321group.accountability.mainscreen.chat.AddReviewActivity;
import com.cpen321group.accountability.mainscreen.chat.ChatFragment;
import com.cpen321group.accountability.mainscreen.chat.ChattingActivity;
import com.cpen321group.accountability.mainscreen.chat.HistoryActivity;
import com.cpen321group.accountability.mainscreen.chat.ReviewActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class FindAccountantTest {
    FragmentScenario<ChatFragment> mfragment;
    @Before
    public void setup(){
        FrontendConstants.userID = "100141214588378665776go";
        FrontendConstants.isAccountant = false;
        FrontendConstants.is_test = 1;
        FrontendConstants.is_subscribed = true;
        mfragment = FragmentScenario.launchInContainer(ChatFragment.class);
        mfragment.moveToState(Lifecycle.State.STARTED);
    }

    @Rule
    public IntentsTestRule<FragmentScenario.EmptyFragmentActivity> intentsTestRule =
            new IntentsTestRule(FragmentScenario.EmptyFragmentActivity.class);

    @Test
    public void chatViewTest(){
        onView(withId(R.id.textView_name)).check(matches(withText("Find An Accountant")));
    }

    @Test
    public void chatViewNotSubTest() throws InterruptedException {
        Thread.sleep(1000);
        FrontendConstants.is_subscribed = false;
        mfragment = FragmentScenario.launchInContainer(ChatFragment.class);
        mfragment.moveToState(Lifecycle.State.STARTED);
        Thread.sleep(2000);
        onView(withText("You should subscribed first!")).inRoot(withDecorView(not(getCurrentActivity().getWindow().getDecorView()))) .check(matches(isDisplayed()));
    }

    @Test
    public void sendRequestTest() throws InterruptedException {
        onView(isRoot()).perform(WaitforHelper.waitFor(5000));
        onView(withId(R.id.chat_recycler))
                .perform(RecyclerViewActions.scrollToPosition(0));
        onView(withId(R.id.chat_recycler)).perform(
                RecyclerViewActions.actionOnItemAtPosition(0, MyViewAction.clickChildViewWithId(R.id.request_button_1)));
        Thread.sleep(5000);
        intended(hasComponent(new ComponentName(getApplicationContext(), ChattingActivity.class)));
        //onView(withId(R.id.textView)).check(matches(withText("Chat")));
        onView(withId(R.id.send_button)).check(matches(isDisplayed()));
        onView(withId(R.id.text_view)).check(matches(isDisplayed()));
    }

    @Test
    public void historyTest() throws InterruptedException {
        FrontendConstants.userID = "100141214588378665776go";
        onView(isRoot()).perform(WaitforHelper.waitFor(5000));
        onView(withId(R.id.chat_recycler))
                .perform(RecyclerViewActions.scrollToPosition(0));
        onView(withId(R.id.chat_recycler)).perform(
                RecyclerViewActions.actionOnItemAtPosition(0, MyViewAction.clickChildViewWithId(R.id.history_button)));
        Thread.sleep(3000);
        intended(hasComponent(new ComponentName(getApplicationContext(), HistoryActivity.class)));
        onView(withText("Show History Talk Successfully")).inRoot(withDecorView(not(getCurrentActivity().getWindow().getDecorView()))) .check(matches(isDisplayed()));
    }

    @Test
    public void noHistoryTest() throws InterruptedException {
        FrontendConstants.userID ="Test";
        onView(isRoot()).perform(WaitforHelper.waitFor(5000));
        onView(withId(R.id.chat_recycler))
                .perform(RecyclerViewActions.scrollToPosition(2));
        onView(withId(R.id.chat_recycler)).perform(
                RecyclerViewActions.actionOnItemAtPosition(2, MyViewAction.clickChildViewWithId(R.id.history_button)));
        Thread.sleep(5000);
        intended(hasComponent(new ComponentName(getApplicationContext(), HistoryActivity.class)));
        Thread.sleep(5000);
        onView(withId(R.id.historyView))
                .perform(RecyclerViewActions.scrollToPosition(0));
        onView(withText("Hello")).check(matches(isDisplayed()));
    }

    @Test
    public void reviewTest() throws InterruptedException {
        onView(isRoot()).perform(WaitforHelper.waitFor(5000));
        onView(withId(R.id.chat_recycler))
                          .perform(RecyclerViewActions.scrollToPosition(0));
        onView(withId(R.id.chat_recycler)).perform(
                RecyclerViewActions.actionOnItemAtPosition(0, MyViewAction.clickChildViewWithId(R.id.review_button)));
        Thread.sleep(3000);
        intended(hasComponent(new ComponentName(getApplicationContext(), ReviewActivity.class)));
        Thread.sleep(3000);
        onView(withId(R.id.textView4)).check(matches(not(withText("NaN"))));
    }

    @Test
    public void noReviewTest() throws InterruptedException {
        FrontendConstants.roomID = null;
        onView(isRoot()).perform(WaitforHelper.waitFor(5000));
        onView(withId(R.id.chat_recycler))
                .perform(RecyclerViewActions.scrollToPosition(2));
        onView(withId(R.id.chat_recycler)).perform(
                RecyclerViewActions.actionOnItemAtPosition(2, MyViewAction.clickChildViewWithId(R.id.review_button)));
        Thread.sleep(3000);
        intended(hasComponent(new ComponentName(getApplicationContext(), ReviewActivity.class)));
        Thread.sleep(3000);
        onView(withId(R.id.textView4)).check(matches(withText("NaN")));
    }


    @Test
    public void addReviewTest() throws InterruptedException {
        onView(isRoot()).perform(WaitforHelper.waitFor(2000));
        onView(withId(R.id.chat_recycler))
                .perform(RecyclerViewActions.scrollToPosition(0));
        onView(withId(R.id.chat_recycler)).perform(
                RecyclerViewActions.actionOnItemAtPosition(0, MyViewAction.clickChildViewWithId(R.id.review_button)));
        Thread.sleep(2000);
        onView(withId(R.id.floating_action_button_review)).perform(click());
        Thread.sleep(2000);
        intended(hasComponent(new ComponentName(getApplicationContext(), AddReviewActivity.class)));
        onView(withId(R.id.title_text)).check(matches(isDisplayed()));
        onView(withId(R.id.reviewcontentInput)).check(matches(isDisplayed()));
        onView(withId(R.id.rate_text)).check(matches(isDisplayed()));
        onView(withId(R.id.create_button)).check(matches(isDisplayed()));
    }

    @Test
    public void addWithNoRateTest() throws InterruptedException {
        onView(isRoot()).perform(WaitforHelper.waitFor(5000));
        onView(withId(R.id.chat_recycler))
                .perform(RecyclerViewActions.scrollToPosition(0));
        onView(withId(R.id.chat_recycler)).perform(
                RecyclerViewActions.actionOnItemAtPosition(0, MyViewAction.clickChildViewWithId(R.id.review_button)));
        Thread.sleep(5000);
        onView(withId(R.id.floating_action_button_review)).perform(click());
        onView(withId(R.id.title_text)).perform(typeText("Happy with Accountant"));
        onView(withId(R.id.reviewcontentInput)).perform(typeText("Great!"));
        onView(ViewMatchers.withId(R.id.reviewcontentInput)).perform(closeSoftKeyboard());
        Thread.sleep(3000);
        onView(withId(R.id.create_button)).perform(click());
        onView(withText("Some necessary information missing!")).inRoot(withDecorView(not(getCurrentActivity().getWindow().getDecorView()))) .check(matches(isDisplayed()));
    }

    @Test
    public void addWithNoContentTest() throws InterruptedException {
        onView(isRoot()).perform(WaitforHelper.waitFor(5000));
        onView(withId(R.id.chat_recycler))
                .perform(RecyclerViewActions.scrollToPosition(0));
        onView(withId(R.id.chat_recycler)).perform(
                RecyclerViewActions.actionOnItemAtPosition(0, MyViewAction.clickChildViewWithId(R.id.review_button)));
        Thread.sleep(5000);
        onView(withId(R.id.floating_action_button_review)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.title_text)).perform(typeText("Happy with Accountant"));
        onView(ViewMatchers.withId(R.id.title_text)).perform(closeSoftKeyboard());
        onView(withId(R.id.rate_text)).perform(click());
        onView(withText("4")).inRoot(withDecorView(not(getCurrentActivity().getWindow().getDecorView()))).check(matches(isDisplayed())).perform(click());
        Thread.sleep(2000);
        onView(withId(R.id.create_button)).perform(click());
        onView(withText("Some necessary information missing!")).inRoot(withDecorView(not(getCurrentActivity().getWindow().getDecorView()))) .check(matches(isDisplayed()));
    }

    @Test
    public void addWithNoTitleTest() throws InterruptedException {
        onView(isRoot()).perform(WaitforHelper.waitFor(5000));
        onView(withId(R.id.chat_recycler))
                .perform(RecyclerViewActions.scrollToPosition(0));
        onView(withId(R.id.chat_recycler)).perform(
                RecyclerViewActions.actionOnItemAtPosition(0, MyViewAction.clickChildViewWithId(R.id.review_button)));
        Thread.sleep(5000);
        onView(withId(R.id.floating_action_button_review)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.reviewcontentInput)).perform(typeText("Happy with Accountant"));
        onView(ViewMatchers.withId(R.id.reviewcontentInput)).perform(closeSoftKeyboard());
        onView(withId(R.id.rate_text)).perform(click());
        onView(withText("4")).inRoot(withDecorView(not(getCurrentActivity().getWindow().getDecorView()))).check(matches(isDisplayed())).perform(click());
        Thread.sleep(2000);
        onView(withId(R.id.create_button)).perform(click());
        onView(withText("Some necessary information missing!")).inRoot(withDecorView(not(getCurrentActivity().getWindow().getDecorView()))) .check(matches(isDisplayed()));
    }

    @Test
    public void addSuccessTest() throws InterruptedException {
        onView(isRoot()).perform(WaitforHelper.waitFor(5000));
        onView(withId(R.id.chat_recycler))
                .perform(RecyclerViewActions.scrollToPosition(0));
        onView(withId(R.id.chat_recycler)).perform(
                RecyclerViewActions.actionOnItemAtPosition(0, MyViewAction.clickChildViewWithId(R.id.review_button)));
        Thread.sleep(5000);
        onView(withId(R.id.floating_action_button_review)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.title_text)).perform(typeText("Test"));
        onView(withId(R.id.reviewcontentInput)).perform(typeText("Very nice accountant"));
        onView(ViewMatchers.withId(R.id.reviewcontentInput)).perform(closeSoftKeyboard());
        onView(withId(R.id.rate_text)).perform(click());
        onView(withText("5")).inRoot(withDecorView(not(getCurrentActivity().getWindow().getDecorView()))).check(matches(isDisplayed())).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.create_button)).perform(click());
        Thread.sleep(2000);
        onView(withText("Success!")).inRoot(withDecorView(not(getCurrentActivity().getWindow().getDecorView()))) .check(matches(isDisplayed()));
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
