package com.example.testapp;

import java.io.IOException;
import java.util.ArrayList;

import com.example.testapp.fragment.ContactsImplement;
import com.example.testapp.fragment.ContactsListFragment;
import com.example.testapp.fragment.DetailFragment;
import com.example.testapp.modle.ContactModle;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity implements
        ContactsListFragment.OnHeadlineSelectedListener {
    private Context mContext;
    private static final String TAG = "MAINACTIVITY";
    public static ContactsListFragment mContactsListFragment;
    DetailFragment mDetailFragment;
    public static Boolean mIsLargeScreen = false;
    Boolean mIsCangeMenu = false;
    Boolean mIsFromInsertContactActivity = false;
    public static final int UPDATECONTACTCODE = 123;
    public static final int INSERTCONTACTCODE = 456;
    private Boolean mIsLog = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mIsLog)
            Log.e(TAG, "onCreate...");
        // Log.w(TAG, "Task id: " + getTaskId());
        setContentView(R.layout.activity_main);
        mContext = getApplicationContext();

        if (((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_XLARGE)
                || ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE)) {
            mIsLargeScreen = true;
        } else
            mIsLargeScreen = false;

        mContactsListFragment = new ContactsListFragment();
        if (findViewById(R.id.container) != null) {
            if (savedInstanceState != null) {
                return;
            }

            mContactsListFragment.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, mContactsListFragment).commit();
            if (mIsLog)
                Log.e(TAG,
                        " mContactsListFragment = new ContactsListFragment();");
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        // TODO Auto-generated method stub
        super.onNewIntent(intent);
        if (mIsLog)
            Log.e(TAG, "onNewIntent...");
        Bundle bundle = intent.getExtras();
        if (null != bundle) {
            Log.e(TAG, "onNewIntent: null != bundle");
            mIsFromInsertContactActivity = bundle.getBoolean(
                    "isInsertContactActivity", false);
            if (mIsFromInsertContactActivity) {
                String cId = bundle.getString("cId");
                Log.e(TAG, "mIsFromInsertContactActivity == true -> cId:" + cId);
                // mContactsListFragment.initContactsListFragment();
                initDetailFragment(-1,
                        ContactsImplement.getContact(mContext, cId));
                bundle.putBoolean("isInsertContactActivity", false);
            }

        } else {
            mIsFromInsertContactActivity = false;
            if (mIsLog)
                Log.e(TAG, "onNewIntent: mIsFromInsertContactActivity == false");
        }
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        if (mIsLog)
            Log.e(TAG, "onStart...");

    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if (mIsLog)
            Log.e(TAG, "onResume...");
    }

    @Override
    public void onItemSelected(int position, String rowId) {
        Log.e(TAG, "onItemSelected");
        if (mIsLog)
            mIsCangeMenu = true;
        // TODO Auto-generated method stub

        initDetailFragment(position,
                ContactsImplement.getContact(mContext, rowId));
    }

    @Override
    // item 0:Edit,1:Delete
    public void onContactDialogSelected(int item, String rowId, int position,
            String preContactRowId) {
        // TODO Auto-generated method stub

        if (item == 1) {
            if (mIsLog)
                Log.e(TAG, "onContactDialogSelected(Delete) id: " + rowId);

            ContactsImplement.deleteContact(mContext, rowId);
            if (mIsLog)
                Log.e(TAG, "position: " + position);
            if (mIsLargeScreen) {
                if (null != preContactRowId) {
                    initDetailFragment(-1, ContactsImplement.getContact(
                            mContext, preContactRowId));

                } else
                    initDetailFragment(-1, null);

            }

        } else if (item == 0) {
            if (mIsLog)
                Log.e(TAG, "onContactDialogSelected(Edit) id: " + rowId);
            Intent i = new Intent(MainActivity.this,
                    UpdateContactActivity.class);
            i.putExtra("id", rowId);
            startActivityForResult(i, UPDATECONTACTCODE);
        }

    }

    @Override
    public void onStartInsertContactActivity() {
        // TODO Auto-generated method stub
        Intent i = new Intent(MainActivity.this, InsertContactActivity.class);
        startActivityForResult(i, INSERTCONTACTCODE);

    }

    public void initDetailFragment(int position, ContactModle cm) {
        // Capture the Detail fragment from the activity layout
        mDetailFragment = (DetailFragment) getSupportFragmentManager()
                .findFragmentById(R.id.detail_fragment);
        // If Detail frag is available, we're in two-pane layout...
        if (mDetailFragment != null) {
            mDetailFragment.updateFrag2View(position, cm);
        } else {
            // If the frag is not available, we're in the one-pane layout and
            // must swap frags...
            if (mIsLog)
                Log.e(TAG, "DetailFragment == null");

            // DetailFragment newFragment = new DetailFragment();
            mDetailFragment = new DetailFragment();
            if (cm != null) {
                Bundle args = new Bundle();
                args.putString(DetailFragment.FRG2_CONTACT_ID, cm.getId());
                mDetailFragment.setArguments(args);
            }

            FragmentTransaction transaction = getSupportFragmentManager()
                    .beginTransaction();
            transaction.replace(R.id.container, mDetailFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }

    }

    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(reqCode, resultCode, data);
        if (mIsLog)
            Log.e(TAG, "onActivityResult...");
        switch (reqCode) {
        case INSERTCONTACTCODE:
            if (resultCode == RESULT_OK) {
                if (mIsLog)
                    Log.e(TAG, "INSERTCONTACTCODE " + "OK");
                Bundle bundle = data.getExtras();
                String cId = bundle.getString("cId");
                // mContactsListFragment.initContactsListFragment();
                if (mIsLog)
                    Log.e(TAG, "INSERTCONTACTCODE cId:" + cId);
                initDetailFragment(-1,
                        ContactsImplement.getContact(mContext, cId));
            }
            break;
        case UPDATECONTACTCODE:
            if (resultCode == RESULT_OK) {
                if (mIsLog)
                    Log.e(TAG, "UPDATECONTACTCODE" + "OK");
                Bundle bundle = data.getExtras();
                String cId = bundle.getString("cId");
                // mContactsListFragment.initContactsListFragment();
                initDetailFragment(-1,
                        ContactsImplement.getContact(mContext, cId));
            }
            break;
        default:
            break;
        }

    }

    @Override
    protected void onResumeFragments() {
        // TODO Auto-generated method stub
        super.onResumeFragments();
        if (mIsLog)
            Log.e(TAG, "onResumeFragments...");
        // Bundle bundle = getIntent().getExtras();
        // if (null != bundle) {
        // Log.e(TAG, "onResumeFragments: null != bundle");
        // mIsFromInsertContactActivity = bundle.getBoolean(
        // "isInsertContactActivity", false);
        // if (mIsFromInsertContactActivity) {
        // String cId = bundle.getString("cId");
        // Log.e(TAG, "mIsFromInsertContactActivity == true -> cId:" + cId);
        //
        // mContactsListFragment.initContactsListFragment();
        //
        // initDetailFragment(-1,
        // ContactsImplement.getContact(mContext, cId));
        // bundle.putBoolean("isInsertContactActivity", false);
        // }
        //
        // }else{
        // mIsFromInsertContactActivity= false;
        // Log.e(TAG, "mIsFromInsertContactActivity == false" );
        // }
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        if (mIsFromInsertContactActivity) {
            System.exit(0);
        }
        super.onPause();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub
        super.onSaveInstanceState(outState);
        if (mIsLog)
            Log.e(TAG, "onSaveInstanceState...");
    }

}
