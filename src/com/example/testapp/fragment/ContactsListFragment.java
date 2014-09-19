package com.example.testapp.fragment;

import java.util.ArrayList;


import com.example.testapp.R;
import com.example.testapp.modle.ContactModle;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.app.AlertDialog.Builder;

public class ContactsListFragment extends Fragment {
    private static final String TAG = "ContactsListFragment";
    private Context mContext;
    private ListView mContactsListView;
    static ArrayList<ContactModle> mContacts = null;
    OnHeadlineSelectedListener mCallback;

    public interface OnHeadlineSelectedListener {
        /** Called by Fragment1 when a list item is selected */
        public void onItemSelected(int position, ContactModle c);
        //item 0:Edit,1:Delete
        public void onContactDialogSelected(int item, ContactModle c,int position); 
        
        public void onStartInsertContactActivity();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState); 
        mContext = getActivity();
        //mContactsListView = (ListView) getActivity().findViewById(
                //R.id.listViewContacts);
       
        Log.e(TAG, "onCreate");

    }

    @Override
    public void onAttach(Activity activity) {
        // TODO Auto-generated method stub
        super.onAttach(activity);
        Log.e(TAG, "onAttach");
        try {
            mCallback = (OnHeadlineSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // TODO Auto-generated method stub
    	 Log.e(TAG, "onCreateView");
    	 setHasOptionsMenu(true);
        return inflater.inflate(R.layout.contacts_list_fragment_view, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
        Log.e(TAG, "onActivityCreated");
        initContactsListFragment();

    }
    

    @Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    	 Log.e(TAG, "onCreateOptionsMenu");
		// TODO Auto-generated method stub
    	inflater.inflate(R.menu.main, menu);
    	super.onCreateOptionsMenu(menu,inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.action_settings:
			// ContactsImplement.deleteContact(mContext, Integer.toString(5));
		    initContactsListFragment();
			break;
		case R.id.actionAddContact:
		    mCallback.onStartInsertContactActivity();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		super.onPrepareOptionsMenu(menu);
	}

	public void initContactsListFragment() {
	    ArrayList<ContactModle> mcTest = ContactsImplement.fetchContacts2(mContext);
        mContacts = ContactsImplement.fetchContacts(mContext);
        if(((Activity) mContext).findViewById(R.id.listViewContacts) != null){
        	mContactsListView = (ListView) getActivity().findViewById(
                    R.id.listViewContacts);
        	 Log.e(TAG, "R.id.listViewContacts) != null");
        }
       
        if (mContacts != null && getActivity()!=null) {
            Log.w(TAG, "mContacts != null&& getActivity()!=null");
           

            ContactsListViewAdapter contactListViewAdapter = new ContactsListViewAdapter(
                    getActivity(), mContacts);
            mContactsListView.setAdapter(contactListViewAdapter);
            mContactsListView.setOnItemClickListener(new OnItemClickListener() {
                public void onItemClick(AdapterView<?> a, View v, int position,
                        long id) {
                    // TODO Auto-generated method stub
                    mCallback.onItemSelected(position, mContacts.get(position));
                    // Log.i(TAG, mContacts.get(position).getContactName());
                    // Log.i(TAG, mContacts.get(position).getContactPhoneNum());
                    // mContactsListView.setItemChecked(position, true)

                }
            });
            mContactsListView
                    .setOnItemLongClickListener(new OnItemLongClickListener() {
                        public boolean onItemLongClick(AdapterView<?> parent,
                                View view, int position, long id) {
//                            Toast.makeText(mContext,
//                                    "LongClicked: " + position,
//                                    Toast.LENGTH_SHORT).show();
                            
                            ContactModle c = mContacts.get(position);
                            contactDialog(c, position).show();
                            return true;
                        }
                    });
        } else
            Log.w(TAG, "no contacts");

    }

    public Dialog contactDialog(final ContactModle cM,final int position) {
        Dialog dialog = new Dialog(mContext);
        Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(cM.getContactName());

        builder.setItems(getResources().getStringArray(R.array.contactChoice),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
//                        Toast.makeText(mContext,
//                              "Dialog: " + item,
//                              Toast.LENGTH_SHORT).show();
                        mCallback.onContactDialogSelected(item, cM,position);
                        
                    }
                });
        dialog = builder.create();  
        return dialog;
    }

    public class ContactsListViewAdapter extends BaseAdapter {
        private LayoutInflater ly = null;
        private ArrayList<ContactModle> contactsList = new ArrayList<ContactModle>();

        public ContactsListViewAdapter(Context context,
                ArrayList<ContactModle> data) {
            ly = LayoutInflater.from(context);
            this.contactsList = data;
        }

        public int getCount() {
            if (contactsList == null)
                return 0;
            else
                return contactsList.size();
        }

        public Object getItem(int arg0) {
            return arg0;
        }

        public long getItemId(int arg0) {
            return arg0;
        }

        @Override
        public View getView(int i, View cv, ViewGroup arg2) {
            // TODO Auto-generated method stub
            TagHold hold ;
            ContactModle cM = contactsList.get(i);
            if (cv == null) {
               
                hold = new TagHold();
                cv = ly.inflate(R.layout.listview_item, null);

                hold.name = (TextView) cv
                        .findViewById(R.id.listViewContactsName);
                // hold.phoneNum = (TextView) cv
                // .findViewById(R.id.ContactsPhoneNum);
                // hold.email = (TextView) cv.findViewById(R.id.ContactsEmail);

                
                // hold.phoneNum.setText(cM.getContactPhoneNum());
                // hold.email.setText(cM.getContactEmail());
                cv.setTag(hold);
            } else {
                hold = (TagHold) cv.getTag();
            }
            hold.name.setText(cM.getContactName());

            return cv;

        }

        public final class TagHold {
            public TextView name;
            // public TextView phoneNum;
            // public TextView email;

        }
    }

}
