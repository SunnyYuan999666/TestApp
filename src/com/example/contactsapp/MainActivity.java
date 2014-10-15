package com.example.contactsapp;

import com.example.caontactsapp.modle.ContactModle;
import com.example.contactsapp.fragment.ContactsImplement;
import com.example.contactsapp.fragment.ContactsListFragment;
import com.example.contactsapp.fragment.DetailFragment;
import com.example.contactsapp.R;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends ActionBarActivity implements
        ContactsListFragment.OnHeadlineSelectedListener {

    private Context mContext;
    private static final String TAG = "MAINACTIVITY";
    private static final String RAWID_OF_EXIST_PHONE_NUM = "rawIdOfExistPhoneNum";
    private static final String IS_INSERT_CONTACT_ACTIVITY = "isInsertContactActivity";
    public static ContactsListFragment mContactsListFragment;
    DetailFragment mDetailFragment;
    public static Boolean mIsLargeScreen = false;
    Boolean mIsChangeMenu = false;
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
        }

        initListFragment(savedInstanceState);

        Bundle bundle = getIntent().getExtras();
        setDetailFragmentFromNotification(bundle);
        setDetailFragmentFromInsertActivity(bundle);

    }

    @Override
    protected void onNewIntent(Intent intent) {
        // TODO Auto-generated method stub
        super.onNewIntent(intent);
        if (mIsLog)
            Log.e(TAG, "onNewIntent...");
        Bundle bundle = intent.getExtras();
        setDetailFragmentFromNotification(bundle);
        setDetailFragmentFromInsertActivity(bundle);

    }

    @Override
    public void onItemSelected(int position, String rowId) {
        if (mIsLog)
            Log.e(TAG, "onItemSelected");

        mIsChangeMenu = true;
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

    public void setDetailFragmentFromInsertActivity(Bundle bundle) {
        if (null != bundle) {
            Log.e(TAG, "setDetailFragmentFromInsertActivity: null != bundle");
            
            mIsFromInsertContactActivity = bundle.getBoolean(
                    IS_INSERT_CONTACT_ACTIVITY, false);
            
            if (mIsFromInsertContactActivity) {
                String cId = bundle.getString("cId");
                Log.e(TAG, "mIsFromInsertContactActivity == true -> cId:" + cId);
                initDetailFragment(-1,
                        ContactsImplement.getContact(mContext, cId));
                bundle.putBoolean(IS_INSERT_CONTACT_ACTIVITY, false);
            }

        } else {
            mIsFromInsertContactActivity = false;
            if (mIsLog)
                Log.e(TAG, "onNewIntent: mIsFromInsertContactActivity == false");
        }

    }

    public void setDetailFragmentFromNotification(Bundle bundle) {
        if (null != bundle) {
            Log.e(TAG, "setDetailFragmentFromNotification: null != bundle");
            String rowIdOfExistPhoneNum = bundle
                    .getString(RAWID_OF_EXIST_PHONE_NUM);
            if (null != rowIdOfExistPhoneNum) {
                initDetailFragment(-1, ContactsImplement.getContact(mContext,
                        rowIdOfExistPhoneNum));
                bundle.putString(RAWID_OF_EXIST_PHONE_NUM, null);

            }
        }

    }

    public void initListFragment(Bundle savedInstanceState) {
        // Create an instance of ContactsListFragment
        mContactsListFragment = new ContactsListFragment();

        // One-pane
        if (null != findViewById(R.id.container)) {
            if (null != savedInstanceState) {
                return;
            }
            mContactsListFragment.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, mContactsListFragment).commit();
        }
    }

    public void initDetailFragment(int position, ContactModle cm) {
        // Capture the Detail fragment from the activity layout
        mDetailFragment = (DetailFragment) getSupportFragmentManager()
                .findFragmentById(R.id.detail_fragment);
        // If Detail frag is available, we're in two-pane layout...
        if (null != mDetailFragment) {
            mDetailFragment.updateDetailFragView(position, cm);
        } else {
            // If the frag is not available, we're in the one-pane layout and
            // must swap frags...
            mDetailFragment = new DetailFragment();
            if (null != cm) {
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

                Bundle bundle = data.getExtras();
                String cId = bundle.getString("cId");
                // mContactsListFragment.initContactsListFragment();
                if (mIsLog)
                    Log.e(TAG, "INSERTCONTACTCODE cId:" + cId);
                initListFragment(getIntent().getExtras());
                if (null != mContactsListFragment) {
                    if (mIsLog)
                        Log.e(TAG, "null != mContactsListFragment");
                    mContactsListFragment.setmIsNewContact(true);
                    mContactsListFragment.setmSelectedRowId(cId);
                }

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
                initListFragment(getIntent().getExtras());
                if (null != mContactsListFragment) {
                    if (mIsLog)
                        Log.e(TAG, "null != mContactsListFragment");
                    mContactsListFragment.setmIsNewContact(true);
                    mContactsListFragment.setmSelectedRowId(cId);
                }
                initDetailFragment(-1,
                        ContactsImplement.getContact(mContext, cId));
            }
            break;
        default:
            break;
        }

    }

    // @Override
    // protected void onResumeFragments() {
    // // TODO Auto-generated method stub
    // super.onResumeFragments();
    // if (mIsLog)
    // Log.e(TAG, "onResumeFragments...");
    // }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        if (mIsLog)
            Log.e(TAG, "onPause...");
        if (mIsFromInsertContactActivity) {
            System.exit(0);
        }
        super.onPause();

    }

}
