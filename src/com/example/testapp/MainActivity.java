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
    static ContactsListFragment mContactsListFragment;
    DetailFragment mDetailFragment;
    Boolean mIsLargeScreen = false, mIsCangeMenu = false;
    static final int UPDATECONTACTCODE = 123;
    public static final int INSERTCONTACTCODE = 456;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate");
        setContentView(R.layout.activity_main);
        mContext = getApplicationContext();
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        int width = 0;
        int height = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            display.getSize(size);
            width = size.x;
            height = size.y;
        } else {

            width = display.getWidth();
            height = display.getHeight();
        }
        Log.e(TAG, "width: " + width + "   height: " + height);
        if (width < 1024)
            mIsLargeScreen = false;
        else
            mIsLargeScreen = true;

        if (findViewById(R.id.container) != null) {
            if (savedInstanceState != null) {
                return;
            }

            mContactsListFragment = new ContactsListFragment();
            mContactsListFragment.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, mContactsListFragment).commit();
            Log.e(TAG, " mContactsListFragment = new ContactsListFragment();");
        }

    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        Log.e(TAG, "onStart");

    }

    @Override
    public void onItemSelected(int position, ContactModle c) {
        Log.e(TAG, "onItemSelected");
        mIsCangeMenu = true;
        // TODO Auto-generated method stub
        // Toast.makeText(mContext, String.valueOf(position),
        // Toast.LENGTH_SHORT).show();
        initDetailFragment(position, c);
    }

    @Override
    // item 0:Edit,1:Delete
    public void onContactDialogSelected(int item, ContactModle c, int position) {
        // TODO Auto-generated method stub
        // Toast.makeText(
        // mContext,
        // "onContactDialogSelected: " + item + "\n" + "contact id"
        // + c.getId(), Toast.LENGTH_SHORT).show();

        if (item == 1) {
            Log.e(TAG, "onContactDialogSelected(Delete) id: " + c.getId());
            ArrayList<ContactModle> contacts = ContactsImplement
                    .fetchContacts(mContext);

            ContactsImplement.deleteContact(mContext, c.getId());
            Log.e(TAG, "position: " + position);
            if (position > 0) {
                initDetailFragment(position - 1, contacts.get(position - 1));
                // Log.e(TAG,
                // "get(position-1): "+contacts.get(position-1).getContactName());
                // mDetailFragment.updateFrag2View(position-1,
                // contacts.get(position-1));
            } else {
                contacts = ContactsImplement.fetchContacts(mContext);
                if (contacts.size() != 0) {
                    initDetailFragment(0, contacts.get(0));
                } else
                    initDetailFragment(-1, null);

            }
            mContactsListFragment.initContactsListFragment();

        } else if (item == 0) {
            Log.e(TAG, "onContactDialogSelected(Edit) id: " + c.getId());
            Intent i = new Intent(MainActivity.this,
                    UpdateContactActivity.class);
            i.putExtra("id", c.getId());
            startActivityForResult(i, UPDATECONTACTCODE);

            // ContactsImplement.updateContact(mContext, c);
            // mContactsListFragment.initContactsListFragment();
            //
        }

    }

    @Override
    public void onStartInsertContactActivity() {
        // TODO Auto-generated method stub
        Intent i = new Intent(MainActivity.this, InsertContactActivity.class);
        startActivityForResult(i, INSERTCONTACTCODE);

    }

    public void initDetailFragment(int position, ContactModle c) {
        mDetailFragment = (DetailFragment) getSupportFragmentManager().findFragmentById(
                R.id.detail_fragment);
        if (mDetailFragment != null) {
            mDetailFragment.updateFrag2View(position, c);
        } else {
            Log.i(TAG, "DetailFragment == null");

            // DetailFragment newFragment = new DetailFragment();
            mDetailFragment = new DetailFragment();
            if (c != null) {
                Bundle args = new Bundle();
                args.putString(DetailFragment.FRG2_CONTACT_ID, c.getId());
                mDetailFragment.setArguments(args);
            }

            FragmentTransaction transaction = getSupportFragmentManager()
                    .beginTransaction();
            transaction.replace(R.id.container, mDetailFragment);
            transaction.addToBackStack(null);

            transaction.commit();
        }

        // Log.i(TAG, "DetailFragment != null");

    }

    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(reqCode, resultCode, data);
        Log.v(TAG, "onActivityResult");
        switch (reqCode) {
        case INSERTCONTACTCODE:
            if (resultCode == RESULT_OK) {
                Log.e(TAG, "INSERTCONTACTCODE " + "OK");
                Bundle bundle = data.getExtras();
                String cId = bundle.getString("cId");
                mContactsListFragment.initContactsListFragment();
                Log.e(TAG, "INSERTCONTACTCODE cId:" + cId);
                initDetailFragment(-1, ContactsImplement.getContact(mContext, cId));
            }
            break;
        case UPDATECONTACTCODE:
            if (resultCode == RESULT_OK) {
                Log.e(TAG, "UPDATECONTACTCODE" + "OK");
                Bundle bundle = data.getExtras();
                String cId = bundle.getString("cId");
                mContactsListFragment.initContactsListFragment();
                initDetailFragment(-1, ContactsImplement.getContact(mContext, cId));
            }
            break;
        default:
            break;
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub
        super.onSaveInstanceState(outState);
    }

}
