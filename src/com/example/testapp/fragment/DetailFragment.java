package com.example.testapp.fragment;

import com.example.testapp.MainActivity;
import com.example.testapp.R;
import com.example.testapp.UpdateContactActivity;
import com.example.testapp.modle.ContactModle;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class DetailFragment extends Fragment {
    private static final String TAG = "DetailFragment";
    public final static String FRG2_CONTACT_ID = "contactId";
    String mCurrentPositionID = null;
    private static Boolean mIsLog = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        if (mIsLog)
            Log.e(TAG, "onCreate...");
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // TODO Auto-generated method stub

        if (savedInstanceState != null) {
            mCurrentPositionID = savedInstanceState.getString(FRG2_CONTACT_ID);

        }
        return inflater
                .inflate(R.layout.detail_fragment_view, container, false);
    }

    @Override
    public void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        if (mIsLog)
            Log.e(TAG, "onStart...");
        Bundle args = getArguments();
        if (args != null) {
            if (mIsLog)
                Log.e(TAG, "args != null");
            String id = args.getString(FRG2_CONTACT_ID);
            updateFrag2View(-1, ContactsImplement.getContact(getActivity(), id));
        } else if (mCurrentPositionID != null) {
            if (mIsLog)
                Log.e(TAG, mCurrentPositionID);
            updateFrag2View(-1, ContactsImplement.getContact(getActivity(),
                    mCurrentPositionID));
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Auto-generated method stub
        if (!MainActivity.mIsLargeScreen) {
            if (mIsLog)
                Log.e(TAG, "onCreateOptionsMenu...");

            inflater.inflate(R.menu.contact_choice, menu);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        // TODO Auto-generated method stub
        if (mIsLog)
            Log.e(TAG, "onPrepareOptionsMenu...");
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {
        case R.id.actionDelete:
            if (mIsLog)
                Log.e(TAG, "action Delete");
            ContactsImplement.deleteContact(getActivity(), mCurrentPositionID);
            // MainActivity.mContactsListFragment.initContactsListFragment();
            getActivity().getSupportFragmentManager().popBackStack();

            break;
        case R.id.actionEdit:
            if (mIsLog)
                Log.e(TAG, "action Edit");
            Intent i = new Intent(getActivity(), UpdateContactActivity.class);
            i.putExtra("id", mCurrentPositionID);
            startActivityForResult(i, MainActivity.UPDATECONTACTCODE);
            break;
        default:
            break;
        }
        return super.onOptionsItemSelected(item);

    }

    public void updateFrag2View(int position, ContactModle cm) {
        ImageView photo = (ImageView) getActivity().findViewById(
                R.id.detailContactsPhoto);
        TextView name = (TextView) getActivity()
                .findViewById(R.id.ContactsName);
        TextView phoneNum = (TextView) getActivity().findViewById(
                R.id.ContactsPhoneNum);
        TextView email = (TextView) getActivity().findViewById(
                R.id.ContactsEmail);

        Bitmap image = ContactsImplement.queryContactImage(getActivity(),
                ContactsImplement.getPhotoId(getActivity(), cm.getId()));

        // Log.v(TAG, "cm.getId(): " + cm.getId());
        if (null != image) {
            photo.setImageBitmap(image);
            if (mIsLog)
                Log.v(TAG, "null != image");
        } else {
            if (mIsLog)
                Log.v(TAG, "null == image");
            photo.setImageResource(R.drawable.ic_person_default);
        }
        if (null != cm) {
            // Log.e(TAG, "ContactModle c!=null");
            name.setText(cm.getContactName());
            phoneNum.setText(cm.getContactPhoneNum());
            email.setText(cm.getContactEmail());
            // Log.e(TAG, c.getContactPhoneNum());
            mCurrentPositionID = cm.getId();
        } else {
            if (mIsLog)
                Log.e(TAG, "ContactModle c==null");
            name.setText("");
            phoneNum.setText("");
            email.setText("");
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub
        super.onSaveInstanceState(outState);
        outState.putString(FRG2_CONTACT_ID, mCurrentPositionID);
    }

}
