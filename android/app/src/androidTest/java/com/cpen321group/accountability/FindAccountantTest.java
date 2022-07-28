  package com.cpen321group.accountability;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.view.View;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.lifecycle.Lifecycle;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.cpen321group.accountability.mainscreen.chat.ChatFragment;
import com.cpen321group.accountability.mainscreen.chat.ChattingActivity;

import org.hamcrest.Matcher;
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
    public void sendRequestTest() throws InterruptedException {
        onView(isRoot()).perform(waitFor(2000));
        onView(withId(R.id.chat_recycler))
                .perform(RecyclerViewActions.scrollToPosition(0));
        onView(withId(R.id.chat_recycler)).perform(
                RecyclerViewActions.actionOnItemAtPosition(0, MyViewAction.clickChildViewWithId(R.id.request_button_1)));
        Thread.sleep(5000);
        intended(hasComponent(ChattingActivity.class.getName()));
        onView(withId(R.id.textView)).check(matches(withText("Chat")));
    }

    public static ViewAction waitFor(long delay) {
        return new ViewAction() {
            @Override public Matcher<View> getConstraints() {
                return ViewMatchers.isRoot();
            }

            @Override public String getDescription() {
                return "wait for " + delay + "milliseconds";
            }

            @Override public void perform(UiController uiController, View view) {
                uiController.loopMainThreadForAtLeast(delay);
            }
        };
    }
}
