package com.example.testapp.fragment;

import java.util.ArrayList;

import com.example.testapp.R;
import com.example.testapp.modle.ContactModle;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;

import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;

import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.RawContacts;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
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

public class ContactsListFragment extends Fragment implements
        LoaderCallbacks<Cursor> {
    private static final String TAG = "ContactsListFragment";
    private static Context mContext;
    private static ListView mContactsListView;
    static ArrayList<ContactModle> mContacts = null;
    static OnHeadlineSelectedListener mCallback;
    CursorLoader mCursorLoader;
    ContactsListViewAdapter mContactListViewAdapter;
    LoaderManager mLoadermanager;
    SimpleCursorAdapter mSimpleCursorAdapter;

    public interface OnHeadlineSelectedListener {
        /** Called by Fragment1 when a list item is selected */
        public void onItemSelected(int position, String rowId);

        // item 0:Edit,1:Delete
        public void onContactDialogSelected(int item, String rowId, int position, String preContactRowId);

        public void onStartInsertContactActivity();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        mContext = getActivity();

        Log.e(TAG, "onCreate...");

    }

    @Override
    public void onAttach(Activity activity) {
        // TODO Auto-generated method stub
        super.onAttach(activity);
        Log.e(TAG, "onAttach...");
        // mContext = activity.;
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
        Log.e(TAG, "onCreateView...");
        setHasOptionsMenu(true);

        return inflater.inflate(R.layout.contacts_list_fragment_view,
                container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
        Log.e(TAG, "onActivityCreated...");
        mLoadermanager = getLoaderManager();
        String[] uiBindFrom = { Contacts.DISPLAY_NAME_PRIMARY };
        int[] uiBindTo = { android.R.id.text1 };
        mContactsListView = (ListView) ((Activity) mContext)
                .findViewById(R.id.listViewContacts);
        mSimpleCursorAdapter = new SimpleCursorAdapter(mContext,
                android.R.layout.simple_list_item_1, null, uiBindFrom,
                uiBindTo, 0);
        mContactsListView.setAdapter(mSimpleCursorAdapter);

        mContactsListView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> a, View v, int position,
                    long id) {
                // TODO Auto-generated method stub

                int contactId = getContactId((Cursor) mSimpleCursorAdapter
                        .getItem(position));
                Log.e(TAG, "contactID: " + contactId);
                String rowId = getContactRowId(contactId);

                Log.e(TAG, "rowId: " + rowId);
                mCallback.onItemSelected(position, rowId);

            }
        });
        mContactsListView
                .setOnItemLongClickListener(new OnItemLongClickListener() {
                    public boolean onItemLongClick(AdapterView<?> parent,
                            View view, int position, long id) {
                        Cursor cursor = (Cursor) mSimpleCursorAdapter
                                .getItem(position);
                        int contactId = getContactId(cursor);
                        Log.e(TAG, "contactID: " + contactId);
                        String rowId = getContactRowId(contactId);
                        Log.e(TAG, "rowId: " + rowId);
                        String name = getContactDisplayName(cursor);
                        String preContactRowId = null ;
                        if( position > 0){
                            int preContactId = getContactId((Cursor) mSimpleCursorAdapter
                                    .getItem(position-1));
                            preContactRowId = getContactRowId(preContactId);
                        }else if(0 == position && 0 != cursor.getCount()){
                            int preContactId = getContactId((Cursor) mSimpleCursorAdapter
                                    .getItem(position+1));
                            preContactRowId = getContactRowId(preContactId);
                        }
                        
                        contactDialog(name, rowId,
                                position,preContactRowId).show();
                        return true;
                    }
                });
        mLoadermanager.initLoader(1, null, this);
      
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        Log.e(TAG, "onResume...");

    }

    @Override
    public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
        // TODO Auto-generated method stub
        String[] projection = new String[] { ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME };
        mCursorLoader = new CursorLoader(getActivity(),
                ContactsContract.Contacts.CONTENT_URI, projection, null, null,
                ContactsContract.Contacts.DISPLAY_NAME
                        + " COLLATE LOCALIZED ASC");
        return mCursorLoader;

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // TODO Auto-generated method stub
        if (mSimpleCursorAdapter != null && cursor != null)
            mSimpleCursorAdapter.swapCursor(cursor); // swap the new cursor
                                                     // in.
        else
            Log.v(TAG, "OnLoadFinished: mSimpleCursorAdapter is null");

    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) { //??
        // TODO Auto-generated method stub

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.e(TAG, "onCreateOptionsMenu...");
        // TODO Auto-generated method stub
        inflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        // TODO Auto-generated method stub
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {
        case R.id.actionAddContact:
            mCallback.onStartInsertContactActivity();
            break;
        default:
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        Log.e(TAG, "onPause...");

    }

    @Override
    public void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        Log.e(TAG, "onStop...");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub
        super.onSaveInstanceState(outState);
        Log.e(TAG, "onSaveInstanceState...");
    }

    public int getContactId(Cursor cursor) {
        return cursor.getInt(cursor
                .getColumnIndex(ContactsContract.Contacts._ID));
    }

    public String getContactRowId(int contactId) {
        ContentResolver contentResolver = mContext.getContentResolver();
        Cursor rawIdCursor = contentResolver.query(RawContacts.CONTENT_URI,
                null, RawContacts.CONTACT_ID + " = " + contactId, null, null);
        String rowId = "";

        if (rawIdCursor.moveToFirst()) {
            rowId = rawIdCursor.getString(rawIdCursor
                    .getColumnIndex(RawContacts._ID));
        }
        rawIdCursor.close();
        return rowId;
    }

    public String getContactDisplayName(Cursor cursor) {
        return cursor.getString(cursor
                .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
    }

//    public void initContactsListFragment() {
//        if (mContext != null) {
//            // new Thread(mLoadContactsListRunnable).start();
//            // if(!mThreadLoadContactsList.isInterrupted()){
//            // mThreadLoadContactsList.interrupt();
//            // }
//            // mThreadLoadContactsList.start();
//            // mContacts = ContactsImplement.fetchContacts(mContext);
//        } else
//            Log.e(TAG, "mContext==null");
//
//        // setContactsListFragment();
//
//    }

    public Dialog contactDialog(String name, final String rowId,
            final int position, final String preContactRowId) {
        Dialog dialog = new Dialog(mContext);
        Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(name);
        // if(isAdded())

        builder.setItems(
                mContext.getResources().getStringArray(R.array.contactChoice),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {

                        if (mCallback != null)
                            mCallback.onContactDialogSelected(item, rowId,
                                    position,preContactRowId);

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
            TagHold hold;
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

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
       
    }

}
