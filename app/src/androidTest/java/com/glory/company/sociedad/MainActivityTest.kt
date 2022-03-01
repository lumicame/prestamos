package com.glory.company.sociedad


import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun mainActivityTest() {
        val bottomNavigationItemView = onView(
allOf(withId(R.id.navigation_favorites), withContentDescription("Rutas"),
childAtPosition(
childAtPosition(
withId(R.id.navigation),
0),
1),
isDisplayed()))
        bottomNavigationItemView.perform(click())
        
        val bottomNavigationItemView2 = onView(
allOf(withId(R.id.navigation_nearby), withContentDescription("Notas"),
childAtPosition(
childAtPosition(
withId(R.id.navigation),
0),
2),
isDisplayed()))
        bottomNavigationItemView2.perform(click())
        
        val bottomNavigationItemView3 = onView(
allOf(withId(R.id.navigation_favorites), withContentDescription("Rutas"),
childAtPosition(
childAtPosition(
withId(R.id.navigation),
0),
1),
isDisplayed()))
        bottomNavigationItemView3.perform(click())
        
        val bottomNavigationItemView4 = onView(
allOf(withId(R.id.navigation_favorites), withContentDescription("Rutas"),
childAtPosition(
childAtPosition(
withId(R.id.navigation),
0),
1),
isDisplayed()))
        bottomNavigationItemView4.perform(click())
        
        val linearLayout = onView(
allOf(withId(R.id.container),
childAtPosition(
childAtPosition(
withId(R.id.recycler),
0),
0),
isDisplayed()))
        linearLayout.perform(click())
        
        val floatingActionButton = onView(
allOf(withId(R.id.fab_add),
childAtPosition(
childAtPosition(
withId(android.R.id.content),
0),
2),
isDisplayed()))
        floatingActionButton.perform(click())
        
        val appCompatTextView = onView(
allOf(withId(R.id.loand_add_client),
childAtPosition(
childAtPosition(
withClassName(`is`("android.widget.LinearLayout")),
3),
0),
isDisplayed()))
        appCompatTextView.perform(click())
        
        val appCompatEditText = onView(
allOf(withId(R.id.loand_add_capital),
childAtPosition(
childAtPosition(
withClassName(`is`("androidx.cardview.widget.CardView")),
0),
4),
isDisplayed()))
        appCompatEditText.perform(replaceText("100000"), closeSoftKeyboard())
        
        val appCompatEditText2 = onView(
allOf(withId(R.id.loand_add_percentage),
childAtPosition(
childAtPosition(
withClassName(`is`("android.widget.LinearLayout")),
5),
0),
isDisplayed()))
        appCompatEditText2.perform(replaceText("1"), closeSoftKeyboard())
        
        val appCompatEditText3 = onView(
allOf(withId(R.id.loand_add_day),
childAtPosition(
childAtPosition(
withClassName(`is`("android.widget.LinearLayout")),
6),
1),
isDisplayed()))
        appCompatEditText3.perform(replaceText("1"), closeSoftKeyboard())
        
        val appCompatEditText4 = onView(
allOf(withId(R.id.loand_add_dues),
childAtPosition(
childAtPosition(
withClassName(`is`("android.widget.LinearLayout")),
7),
1),
isDisplayed()))
        appCompatEditText4.perform(replaceText("1"), closeSoftKeyboard())
        
        val appCompatTextView2 = onView(
allOf(withId(R.id.loand_add_date),
childAtPosition(
childAtPosition(
withClassName(`is`("androidx.cardview.widget.CardView")),
0),
8),
isDisplayed()))
        appCompatTextView2.perform(click())
        
        val appCompatButton = onView(
allOf(withId(android.R.id.button1), withText("Aceptar"),
childAtPosition(
childAtPosition(
withClassName(`is`("android.widget.ScrollView")),
0),
3)))
        appCompatButton.perform(scrollTo(), click())
        
        val appCompatButton2 = onView(
allOf(withId(R.id.Btn_register), withText("Guardar"),
childAtPosition(
childAtPosition(
withClassName(`is`("android.widget.ScrollView")),
0),
3)))
        appCompatButton2.perform(scrollTo(), click())
        }
    
    private fun childAtPosition(
            parentMatcher: Matcher<View>, position: Int): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position)
            }
        }
    }
    }
