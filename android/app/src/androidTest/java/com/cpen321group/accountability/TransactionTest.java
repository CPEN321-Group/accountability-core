package com.cpen321group.accountability;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.PickerActions.setDate;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.lifecycle.Lifecycle;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.ViewAssertion;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.espresso.matcher.RootMatchers;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.cpen321group.accountability.mainscreen.dashboard.DashboardFragment;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

@RunWith(AndroidJUnit4.class)
@LargeTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TransactionTest {
    FragmentScenario<DashboardFragment> mfragment;
    @Before
    public void setup(){
        FrontendConstants.userID = "113718834621290930015go";
        FrontendConstants.isAccountant = false;
        FrontendConstants.is_test = 1;
        mfragment = FragmentScenario.launchInContainer(DashboardFragment.class);
        mfragment.moveToState(Lifecycle.State.STARTED);
    }

    @Rule
    public IntentsTestRule<FragmentScenario.EmptyFragmentActivity> intentsTestRule =
            new IntentsTestRule(FragmentScenario.EmptyFragmentActivity.class);
    @Test
    public void _a_clickTransactionButtonTest() throws InterruptedException {
        Thread.sleep(3000);
        onView(withId(R.id.transaction_secondary_button)).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.floating_action_button_transaction)).check(matches(isDisplayed()));
        onView(withId(R.id.transactionRV)).check(matches(isDisplayed()));
    }

    @Test
    public void _b_createTransactionTest() throws InterruptedException {
        Thread.sleep(3000);
        onView(withId(R.id.transaction_secondary_button)).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.floating_action_button_transaction)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.transactionNameInput)).check(matches(isDisplayed()));
        onView(withId(R.id.transactionAmountPriceInput)).check(matches(isDisplayed()));
        onView(withId(R.id.transactionCategoryText)).check(matches(isDisplayed()));
        onView(withId(R.id.datePickerButton)).check(matches(isDisplayed()));
        onView(withId(R.id.transactionCreateButton)).check(matches(isDisplayed()));
    }

    @Test
    public void _c_checkNameInputTest() throws InterruptedException {
        Thread.sleep(3000);
        onView(withId(R.id.transaction_secondary_button)).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.floating_action_button_transaction)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.transactionNameInput)).perform(click());
        Thread.sleep(5000);
        assert (isSoftKeyboardShown());
    }

    @Test
    public void _d_checkPriceInputTest() throws InterruptedException {
        Thread.sleep(3000);
        onView(withId(R.id.transaction_secondary_button)).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.floating_action_button_transaction)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.transactionAmountPriceInput)).perform(click());
        Thread.sleep(5000);
        assert (isSoftKeyboardShown());
    }

    @Test
    public void _e_checkTransactionCategoryTest() throws InterruptedException {
        Thread.sleep(3000);
        onView(withId(R.id.transaction_secondary_button)).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.floating_action_button_transaction)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.transactionCategoryText)).perform(click());
        Thread.sleep(5000);
        onView(withText("daily necessities")).inRoot(RootMatchers.isPlatformPopup()).check(matches(isDisplayed()));
        onView(withText("food/drinks")).inRoot(RootMatchers.isPlatformPopup()).check(matches(isDisplayed()));
        onView(withText("transportation")).inRoot(RootMatchers.isPlatformPopup()).check(matches(isDisplayed()));
        onView(withText("housing")).inRoot(RootMatchers.isPlatformPopup()).check(matches(isDisplayed()));
        onView(withText("education")).inRoot(RootMatchers.isPlatformPopup()).check(matches(isDisplayed()));
        onView(withText("bills")).inRoot(RootMatchers.isPlatformPopup()).check(matches(isDisplayed()));
        onView(withText("others")).inRoot(RootMatchers.isPlatformPopup()).check(matches(isDisplayed()));
    }

    @Test
    public void _f_checkDatePickerTest() throws InterruptedException {
        Thread.sleep(3000);
        onView(withId(R.id.transaction_secondary_button)).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.floating_action_button_transaction)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.datePickerButton)).perform(click());
        Thread.sleep(5000);
        onView(isAssignableFrom(DatePicker.class)).check(matches(isDisplayed()));
    }

    @Test
    public void _g_createWithNoDateTest() throws InterruptedException {
        Thread.sleep(3000);
        onView(withId(R.id.transaction_secondary_button)).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.floating_action_button_transaction)).perform(click());
        Thread.sleep(1000);

        onView(withId(R.id.transactionNameInput)).perform(typeText("TEST Transaction"));
        onView(withId(R.id.transactionAmountPriceInput)).perform(typeText("20000.05"));
        onView(withId(R.id.transactionAmountPriceInput)).perform(closeSoftKeyboard());

        onView(withId(R.id.transactionCategoryText)).perform(click());
        onView(withText("daily necessities")).inRoot(RootMatchers.isPlatformPopup()).perform(click());

        onView(withId(R.id.transactionCreateButton)).perform(click());
        onView(withText("Some necessary information missing!")).inRoot(withDecorView(not(getCurrentActivity().getWindow().getDecorView()))) .check(matches(isDisplayed()));
    }

    @Test
    public void _h_createWithNoNameInputTest() throws InterruptedException {
        Thread.sleep(3000);
        onView(withId(R.id.transaction_secondary_button)).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.floating_action_button_transaction)).perform(click());
        Thread.sleep(1000);

        onView(withId(R.id.transactionAmountPriceInput)).perform(typeText("20000.05"));
        onView(withId(R.id.transactionAmountPriceInput)).perform(closeSoftKeyboard());

        onView(withId(R.id.transactionCategoryText)).perform(click());
        onView(withText("daily necessities")).inRoot(RootMatchers.isPlatformPopup()).perform(click());

        onView(withId(R.id.datePickerButton)).perform(click());
        Thread.sleep(2000);
        onView(isAssignableFrom(DatePicker.class)).perform(setDate(2022,8,30));
        onView(withText("OK")).inRoot(isDialog()).perform(click());
        Thread.sleep(1000);

        onView(withId(R.id.transactionCreateButton)).perform(click());
        onView(withText("Some necessary information missing!")).inRoot(withDecorView(not(getCurrentActivity().getWindow().getDecorView()))) .check(matches(isDisplayed()));
    }

    @Test
    public void _i_createWithNoPriceInputTest() throws InterruptedException {
        Thread.sleep(3000);
        onView(withId(R.id.transaction_secondary_button)).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.floating_action_button_transaction)).perform(click());
        Thread.sleep(1000);

        onView(withId(R.id.transactionNameInput)).perform(typeText("TEST Transaction"));
        onView(withId(R.id.transactionAmountPriceInput)).perform(closeSoftKeyboard());

        onView(withId(R.id.transactionCategoryText)).perform(click());
        onView(withText("daily necessities")).inRoot(RootMatchers.isPlatformPopup()).perform(click());

        onView(withId(R.id.datePickerButton)).perform(click());
        Thread.sleep(2000);
        onView(isAssignableFrom(DatePicker.class)).perform(setDate(2022,8,30));
        onView(withText("OK")).inRoot(isDialog()).perform(click());
        Thread.sleep(1000);

        onView(withId(R.id.transactionCreateButton)).perform(click());
        onView(withText("Some necessary information missing!")).inRoot(withDecorView(not(getCurrentActivity().getWindow().getDecorView()))) .check(matches(isDisplayed()));
    }

    @Test
    public void _j_createWithNoCategoryInputTest() throws InterruptedException {
        Thread.sleep(3000);
        onView(withId(R.id.transaction_secondary_button)).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.floating_action_button_transaction)).perform(click());
        Thread.sleep(1000);

        onView(withId(R.id.transactionNameInput)).perform(typeText("TEST Transaction"));
        onView(withId(R.id.transactionAmountPriceInput)).perform(typeText("20000.05"));
        onView(withId(R.id.transactionAmountPriceInput)).perform(closeSoftKeyboard());

        onView(withId(R.id.datePickerButton)).perform(click());
        Thread.sleep(2000);
        onView(isAssignableFrom(DatePicker.class)).perform(setDate(2022,8,30));
        onView(withText("OK")).inRoot(isDialog()).perform(click());
        Thread.sleep(1000);

        onView(withId(R.id.transactionCreateButton)).perform(click());
        onView(withText("Some necessary information missing!")).inRoot(withDecorView(not(getCurrentActivity().getWindow().getDecorView()))) .check(matches(isDisplayed()));
    }

    @Test
    public void _k_createSuccessTest() throws InterruptedException {
        Thread.sleep(3000);
        onView(withId(R.id.transaction_secondary_button)).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.floating_action_button_transaction)).perform(click());
        Thread.sleep(1000);

        onView(withId(R.id.transactionNameInput)).perform(typeText("TEST Transaction"));
        onView(withId(R.id.transactionAmountPriceInput)).perform(typeText("20000.05"));
        onView(withId(R.id.transactionAmountPriceInput)).perform(closeSoftKeyboard());

        onView(withId(R.id.transactionCategoryText)).perform(click());
        onView(withText("daily necessities")).inRoot(RootMatchers.isPlatformPopup()).perform(click());

        onView(withId(R.id.datePickerButton)).perform(click());
        Thread.sleep(2000);
        onView(isAssignableFrom(DatePicker.class)).perform(setDate(2022,8,1));
        onView(withText("OK")).inRoot(isDialog()).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.transactionCreateButton)).perform(click());
        Thread.sleep(2000);
        onView(withText("You have successfully added your new transaction")).inRoot(withDecorView(not(getCurrentActivity().getWindow().getDecorView()))) .check(matches(isDisplayed()));
        Thread.sleep(2000);
    }


    @Test
    public void _l_deleteTest() throws InterruptedException {
        Thread.sleep(2000);
        onView(withId(R.id.transaction_secondary_button)).perform(click());
        Thread.sleep(4000);
        onView(withId(R.id.transactionRV))
                .perform(RecyclerViewActions.scrollToPosition(3));
        onView(withId(R.id.transactionRV)).perform(
                RecyclerViewActions.actionOnItemAtPosition(3, MyViewAction.clickChildViewWithId(R.id.transactionDelete)));
        Thread.sleep(2000);
        onView(withText("You have successfully deleted your selected transaction")).inRoot(withDecorView(not(getCurrentActivity().getWindow().getDecorView()))) .check(matches(isDisplayed()));
    }

    @Test
    public void _m_clickTransactionButtonWithNoTransactionTest() throws InterruptedException {
        FrontendConstants.userID = "12345678go";
        Thread.sleep(3000);
        onView(withId(R.id.transaction_secondary_button)).perform(click());
        Thread.sleep(2000);
        onView(withId(R.id.floating_action_button_transaction)).check(matches(isDisplayed()));
        onView(withId(R.id.transactionRV)).check(matches(isDisplayed()));
        onView(withText("You don't have any transaction records.")).inRoot(withDecorView(not(getCurrentActivity().getWindow().getDecorView()))) .check(matches(isDisplayed()));
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
