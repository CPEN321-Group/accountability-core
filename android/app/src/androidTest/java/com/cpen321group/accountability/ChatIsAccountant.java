package com.cpen321group.accountability;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
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
public class ChatIsAccountant {
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
