package com.apb.beacon.wizard;

import android.os.Bundle;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;

import com.apb.beacon.Constants;
import com.apb.beacon.R;
import com.apb.beacon.SoftKeyboard;
import roboguice.activity.RoboFragmentActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

import static android.support.v4.view.ViewPager.SimpleOnPageChangeListener;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

@ContentView(R.layout.wizard_layout)
public class WizardActivity extends RoboFragmentActivity implements ActionButtonStateListener {
    private WizardViewPager viewPager;
    private FragmentStatePagerAdapter pagerAdapter;

    @InjectView(R.id.previous_button)
    Button previousButton;
    @InjectView(R.id.action_button)
    Button actionButton;

    private SimpleOnPageChangeListener pageChangeListener = new ViewPager.SimpleOnPageChangeListener() {
        @Override
        public void onPageSelected(int position) {
            super.onPageSelected(position);
            SoftKeyboard.hide(getApplicationContext(), getCurrentWizardFragment().getView());
            previousButton.setVisibility(position != 0 ? VISIBLE : INVISIBLE);
//            actionButton.setVisibility(position != (pagerAdapter.getCount() - 1) ? VISIBLE : INVISIBLE);
            setActionButtonVisibility(position);
            actionButton.setText(getCurrentWizardFragment().action());
            getCurrentWizardFragment().onFragmentSelected();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        previousButton.setVisibility(INVISIBLE);
        actionButton.setText(getString(R.string.next_action));

        viewPager = (WizardViewPager) findViewById(R.id.wizard_view_pager);
        pagerAdapter = getWizardPagerAdapter();
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(2);
        viewPager.setOnPageChangeListener(pageChangeListener);
    }

    public void setActionButtonVisibility(int pageNumber){
        if(pageNumber == Constants.PAGE_NUMBER_PANIC_BUTTON_TRAINING)
            actionButton.setVisibility(View.INVISIBLE);
        else if(pageNumber == Constants.PAGE_NUMBER_SETUP_CONTACTS_INTRO)
            actionButton.setVisibility(View.INVISIBLE);
        else if(pageNumber == Constants.PAGE_NUMBER_SETUP_CONTACTS_LEARN_MORE)
            actionButton.setVisibility(View.INVISIBLE);
        else if(pageNumber == pagerAdapter.getCount() -1)
            actionButton.setVisibility(View.INVISIBLE);
        else
            actionButton.setVisibility(View.VISIBLE);
    }

    public void performAction(View view) {
        if(getCurrentWizardFragment().performAction()) {
            viewPager.next();
        }
    }

    /*
    skip one fragment in the middle
     */
    public void performActionWithSkip() {
        viewPager.nextWithSkip();
    }

    public void previous(View view) {
        if(viewPager.getCurrentItem() == Constants.PAGE_NUMBER_SETUP_CONTACTS){
            viewPager.previousWithSkip();
        }
//        getCurrentWizardFragment().onBackPressed();
        else{
            viewPager.previous();
        }
    }

    public void previousWithSkip() {
//        getCurrentWizardFragment().onBackPressed();
        viewPager.previousWithSkip();
    }

    @Override
    public void onBackPressed() {
        getCurrentWizardFragment().onBackPressed();
        if(viewPager.isFirstPage()) {
            this.finish();
        }
        else if(viewPager.getCurrentItem() == Constants.PAGE_NUMBER_SETUP_CONTACTS){
            viewPager.previousWithSkip();
        }
        else{
            viewPager.previous();
        }
    }

    private WizardFragment getCurrentWizardFragment() {
        return (WizardFragment) pagerAdapter.getItem(viewPager.getCurrentItem());
    }

    FragmentStatePagerAdapter getWizardPagerAdapter() {
        return new WizardPageAdapter(getSupportFragmentManager());
    }

    @Override
    public void enableActionButton(boolean isEnabled) {
        actionButton.setEnabled(isEnabled);
    }
}
