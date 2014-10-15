package com.example.contactsapp.fragment;

import com.example.contactsapp.MainActivity;
import com.example.contactsapp.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.app.AlertDialog.Builder;

public class ContactsListFragment extends Fragment implements
        LoaderCallbacks<Cursor> {

    static OnHeadlineSelectedListener mCallback;
    private static final String TAG = "ContactsListFragment";
    private static Context mContext;
    private static ListView mContactsListView;

    CursorLoader mCursorLoader;
    LoaderManager mLoadermanager;
    SimpleCursorAdapter mSimpleCursorAdapter;
    private Boolean mIsLog = true;
    private static Boolean mIsNewContact = false;
    Boolean mIsDeleteContact = false;
    private static String mSelectedRawId = null;

    public interface OnHeadlineSelectedListener {
        // Called by ContactsListFragment when a list item is selected
        public void onItemSelected(int position, String rowId);

        // item 0:Edit,1:Delete
        public void onContactDialogSelected(int item, String rowId,
                int position, String preContactRowId);

        public void onStartInsertContactActivity();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        if (mIsLog)
            Log.e(TAG, "onCreate...");

    }

    @Override
    public void onAttach(Activity activity) {
        // TODO Auto-generated method stub
        super.onAttach(activity);
        if (mIsLog)
            Log.e(TAG, "onAttach...");
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
        if (mIsLog)
            Log.e(TAG, "onCreateView...");
        setHasOptionsMenu(true);

        return inflater.inflate(R.layout.contacts_list_fragment_view,
                container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
        if (mIsLog)
            Log.e(TAG, "onActivityCreated...");
        mLoadermanager = getLoaderManager();
        String[] uiBindFrom = { Contacts.DISPLAY_NAME_PRIMARY,
                Contacts.PHOTO_ID };
        int[] uiBindTo = { R.id.listViewContactsName,
                R.id.listViewContactsPhoto }; // android.R.id.text1

        mContactsListView = (ListView) ((Activity) mContext)
                .findViewById(R.id.listViewContacts);

        mSimpleCursorAdapter = new SimpleCursorAdapter(mContext,
                R.layout.listview_item, null, uiBindFrom, uiBindTo, 0);
        mContactsListView.setAdapter(mSimpleCursorAdapter);

        mContactsListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        mSimpleCursorAdapter
                .setViewBinder(new SimpleCursorAdapter.ViewBinder() {
                    /**
                     * Binds the Cursor column defined by the specified index to
                     * the specified view
                     */
                    public boolean setViewValue(View view, Cursor cursor,
                            int columnIndex) {

                        if (view.getId() == R.id.listViewContactsPhoto) {

                            int imageDataRow = cursor.getInt(columnIndex);
                            Bitmap image = ContactsImplement.queryContactImage(
                                    getActivity(), imageDataRow);

                            ImageView iv = (ImageView) view
                                    .findViewById(R.id.listViewContactsPhoto);
                            if (null != image) {
                                iv.setImageBitmap(image);

                            } else {
                                iv.setImageResource(R.drawable.ic_person_default);
                            }
                            return true;// true because the data was bound to
                            // the view

                        }

                        return false;
                    }

                });

        mContactsListView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> a, View v, int position,
                    long id) {

                int contactId = getContactId((Cursor) mSimpleCursorAdapter
                        .getItem(position));
                if (mIsLog)
                    Log.e(TAG, "contactID: " + contactId);
                String rowId = getContactRowId(contactId);

                mSelectedRawId = rowId;
                if (mIsLog)
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
                        if (mIsLog)
                            Log.e(TAG, "contactID: " + contactId);
                        String rowId = getContactRowId(contactId);
                        if (mIsLog)
                            Log.e(TAG, "rowId: " + rowId);

                        String name = getContactDisplayName(cursor);
                        String preContactRowId = null;
                        int prePosition = -1;
                        if (position > 0) {
                            prePosition = position - 1;
                            int preContactId = getContactId((Cursor) mSimpleCursorAdapter
                                    .getItem(prePosition));
                            preContactRowId = getContactRowId(preContactId);
                        } else if (0 == position && 0 != cursor.getCount()) {
                            prePosition = position;
                            int preContactId = getContactId((Cursor) mSimpleCursorAdapter
                                    .getItem(prePosition + 1));
                            preContactRowId = getContactRowId(preContactId);
                        }

                        contactDialog(name, rowId, position, preContactRowId,
                                prePosition).show();
                        return true;
                    }
                });
        mLoadermanager.initLoader(1, null, this);

    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if (mIsLog)
            Log.e(TAG, "onResume...");
    }

    @Override
    public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
        // TODO Auto-generated method stub
        String[] projection = new String[] { Contacts._ID,
                Contacts.DISPLAY_NAME, Contacts.PHOTO_ID };
        mCursorLoader = new CursorLoader(getActivity(), Contacts.CONTENT_URI,
                projection, null, null, Contacts.DISPLAY_NAME
                        + " COLLATE LOCALIZED ASC");
        return mCursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // TODO Auto-generated method stub
        if (null != mSimpleCursorAdapter && null != cursor) {
            mSimpleCursorAdapter.swapCursor(cursor); // swap the new cursor in.
        } else {
            if (mIsLog)
                Log.v(TAG, "OnLoadFinished: mSimpleCursorAdapter is null");
        }
        if (mIsLog)
            Log.v(TAG, "OnLoadFinished...");
        if (getmIsNewContact() || mIsDeleteContact) {
            if (null != getmSelectedRowId()) {
                int pos = ContactsImplement.getPositionInList(mContext,
                        getmSelectedRowId());
                Log.v(TAG, "getmSelectedRowId pos:" + pos);
                setCheckedItemBackground(pos);
            }
            mIsNewContact = false;
            mIsDeleteContact = false;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) { // when onBackPressed
        // TODO Auto-generated method stub
        mSimpleCursorAdapter.swapCursor(null);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (mIsLog)
            Log.e(TAG, "onCreateOptionsMenu...");
        // TODO Auto-generated method stub
        if (!MainActivity.mIsLargeScreen)
            menu.clear();
        inflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu, inflater);
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
    public void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub
        super.onSaveInstanceState(outState);

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

    public Dialog contactDialog(String name, final String rowId,
            final int position, final String preContactRowId,
            final int prePosition) {
        Dialog dialog = new Dialog(mContext);
        Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(name);
        // if(isAdded())

        builder.setItems(
                mContext.getResources().getStringArray(R.array.contactChoice),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {

                        if (null != mCallback) {
                            mCallback.onContactDialogSelected(item, rowId,
                                    position, preContactRowId);
                            if (1 == item) { // delete
                                if (mIsLog)
                                    Log.e(TAG, "prePosition: " + prePosition);
                                setmSelectedRowId(preContactRowId);
                                mIsDeleteContact = true;
                            }
                        }

                    }
                });
        dialog = builder.create();
        return dialog;
    }

    public void setCheckedItemBackground(int pos) {
        if (null != mContactsListView && -1 != pos
                && MainActivity.mIsLargeScreen) {
            mContactsListView.smoothScrollToPosition(pos);
            View v = getViewByPosition(pos, mContactsListView);
            // mContactsListView.invalidateViews();
            if (null != v) {
                if (mIsLog)
                    Log.e(TAG, "setCheckedItemBackground: null != v");

                mContactsListView.setItemChecked(pos, true);
            }

        }

    }

    public View getViewByPosition(int pos, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition
                + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }

  
    public String getmSelectedRowId() {
        return mSelectedRawId;
    }

    public void setmSelectedRowId(String mSelectedRawId) {
        Log.v(TAG, "mSelectedRowId: " + mSelectedRawId);
        this.mSelectedRawId = mSelectedRawId;
    }

    public static Boolean getmIsNewContact() {
        return mIsNewContact;
    }

    public static void setmIsNewContact(Boolean mIsNewContact) {
        ContactsListFragment.mIsNewContact = mIsNewContact;
        Log.v(TAG, "mIsNewContact " + mIsNewContact.booleanValue());
    }


}
