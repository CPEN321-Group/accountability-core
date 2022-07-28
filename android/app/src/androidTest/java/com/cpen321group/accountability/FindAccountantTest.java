package com.cpen321group.accountability;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.lifecycle.Lifecycle;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import com.cpen321group.accountability.mainscreen.chat.ChatFragment;
import com.cpen321group.accountability.mainscreen.chat.ChattingActivity;
import com.cpen321group.accountability.welcome.RegisterActivity;
import com.cpen321group.accountability.welcome.WelcomeActivity;

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

    @Test
    public void chatViewTest(){
        onView(withId(R.id.textView_name)).check(matches(withText("Find An Accountant")));
    }

    @Test
    public void sendRequestTest(){
        onView(withId(R.id.chat_recycler))
                .perform(RecyclerViewActions.scrollToPosition(0));
        onView(withId(R.id.chat_recycler)).perform(
                RecyclerViewActions.actionOnItemAtPosition(0, MyViewAction.clickChildViewWithId(R.id.request_button_1)));
        intended(hasComponent(ChattingActivity.class.getName()));
        onView(withId(R.id.textView)).check(matches(withText("Chat")));
    }
}
