package com.example.testapp.fragment;

import com.example.testapp.R;
import com.example.testapp.modle.ContactModle;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DetailFragment extends Fragment {
    private static final String TAG = "DetailFragment";
    public final static String FRG2_CONTACT_ID = "contactId";
    String mCurrentPositionID = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // TODO Auto-generated method stub

        if (savedInstanceState != null) {
            mCurrentPositionID = savedInstanceState.getString(FRG2_CONTACT_ID);

        }
        return inflater.inflate(R.layout.detail_fragment_view, container, false);
    }

    @Override
    public void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        Log.i(TAG, "onStart");
        Bundle args = getArguments();
        if (args != null) {
            Log.i(TAG, "args != null");
            String id = args.getString(FRG2_CONTACT_ID);
            updateFrag2View(-1, ContactsImplement.getContact(getActivity(), id));
        } else if (mCurrentPositionID != null) {

            Log.w(TAG, mCurrentPositionID);
            updateFrag2View(-1, ContactsImplement.getContact(getActivity(),
                    mCurrentPositionID));
        }
    }

    @Override
	public void onPrepareOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
    	Log.e(TAG, "onPrepareOptionsMenu");
    	menu.clear();
		super.onPrepareOptionsMenu(menu);
	}

	public void updateFrag2View(int position, ContactModle c) {

        TextView name = (TextView) getActivity()
                .findViewById(R.id.ContactsName);
        TextView phoneNum = (TextView) getActivity().findViewById(
                R.id.ContactsPhoneNum);
        TextView email = (TextView) getActivity().findViewById(
                R.id.ContactsEmail);
       if(c!=null){
           //Log.e(TAG, "ContactModle c!=null");
           name.setText(c.getContactName());
           phoneNum.setText(c.getContactPhoneNum());
           email.setText(c.getContactEmail());
           // Log.i(TAG, c.getContactPhoneNum());
           mCurrentPositionID = c.getId();
       }else{
           //Log.e(TAG, "ContactModle c==null");
           name.setHint(getString(R.string.name));
           phoneNum.setHint(getString(R.string.phoneNum));
           email.setHint(getString(R.string.email));
       }
        
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub
        super.onSaveInstanceState(outState);
        outState.putString(FRG2_CONTACT_ID, mCurrentPositionID);
    }

}
