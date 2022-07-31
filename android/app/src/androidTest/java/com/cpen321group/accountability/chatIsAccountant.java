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


import androidx.fragment.app.testing.FragmentScenario;
import androidx.lifecycle.Lifecycle;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.cpen321group.accountability.mainscreen.chat.ChatFragment;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class chatIsAccountant {
    FragmentScenario<ChatFragment> mfragment;
    @Before
    public void setup(){
        FrontendConstants.userID = "455937552go";
        FrontendConstants.isAccountant = true;
        FrontendConstants.is_test = 1;
        mfragment = FragmentScenario.launchInContainer(ChatFragment.class);
        mfragment.moveToState(Lifecycle.State.STARTED);
    }

    @Test
    public void chatViewAccountantTest() throws InterruptedException {
        Thread.sleep(2000);
        onView(withId(R.id.textView_name)).check(matches(withText("User Request")));
    }
}
