package com.example.testapp.fragment;

import java.util.ArrayList;

import com.example.testapp.R;
import com.example.testapp.modle.ContactModle;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.app.AlertDialog.Builder;

public class ContactsListFragment extends Fragment implements
        LoaderCallbacks<Cursor> {
    private static final String TAG = "ContactsListFragment";
    public final static String ITEM_CLICK_POSITION = "itemClickPosition";
    private static Context mContext;
    private static ListView mContactsListView;
    static ArrayList<ContactModle> mContacts = null;
    static OnHeadlineSelectedListener mCallback;
    CursorLoader mCursorLoader;
    ContactsListViewAdapter mContactListViewAdapter;
    LoaderManager mLoadermanager;
    SimpleCursorAdapter mSimpleCursorAdapter;
    View mPreListViewItemClick;
    int mItemClickPosition = -1;
    int mPreItemClickPosition = -1;
    private Boolean mIsLog = true;

    public interface OnHeadlineSelectedListener {
        /** Called by Fragment1 when a list item is selected */
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
        mContactsListView.setSelector(R.drawable.contacts_list_item_bg);

        mSimpleCursorAdapter = new SimpleCursorAdapter(mContext,
                R.layout.listview_item, null, uiBindFrom, uiBindTo, 0); // android.R.layout.simple_list_item_1
        mContactsListView.setAdapter(mSimpleCursorAdapter);
        // mContactsListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
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
                            // Log.v(TAG, "imageDataRow: "+imageDataRow);
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
                // TODO Auto-generated method stub
                if (null != mPreListViewItemClick
                        && mPreItemClickPosition != position)
                    mPreListViewItemClick.setBackgroundColor(Color.TRANSPARENT);

                int contactId = getContactId((Cursor) mSimpleCursorAdapter
                        .getItem(position));
                if (mIsLog)
                    Log.e(TAG, "contactID: " + contactId);
                String rowId = getContactRowId(contactId);
                if (mIsLog)
                    Log.e(TAG, "rowId: " + rowId);

                mItemClickPosition = position;
                // mPreListViewItemClick = v;

                mCallback.onItemSelected(position, rowId);
            }
        });
        mContactsListView
                .setOnItemLongClickListener(new OnItemLongClickListener() {
                    public boolean onItemLongClick(AdapterView<?> parent,
                            View view, int position, long id) {
                        if (null != mPreListViewItemClick
                                && mPreItemClickPosition != position)
                            mPreListViewItemClick
                                    .setBackgroundColor(Color.TRANSPARENT);

                        Cursor cursor = (Cursor) mSimpleCursorAdapter
                                .getItem(position);
                        int contactId = getContactId(cursor);
                        if (mIsLog)
                            Log.e(TAG, "contactID: " + contactId);
                        String rowId = getContactRowId(contactId);
                        if (mIsLog)
                            Log.e(TAG, "rowId: " + rowId);
                        mCallback.onItemSelected(position, rowId);
                        String name = getContactDisplayName(cursor);
                        String preContactRowId = null;
                        int prePosition = -1;
                        if (position > 0) {
                            prePosition = position - 1;
                            int preContactId = getContactId((Cursor) mSimpleCursorAdapter
                                    .getItem(prePosition));
                            preContactRowId = getContactRowId(preContactId);
                        } else if (0 == position && 0 != cursor.getCount()) {
                            prePosition = position + 1;
                            int preContactId = getContactId((Cursor) mSimpleCursorAdapter
                                    .getItem(prePosition));
                            preContactRowId = getContactRowId(preContactId);
                        }

                        contactDialog(name, rowId, position, preContactRowId,
                                prePosition).show();
                        return true;
                    }
                });
        if (savedInstanceState != null) {
            mItemClickPosition = savedInstanceState.getInt(ITEM_CLICK_POSITION,
                    -1);
            if (mIsLog)
                Log.e(TAG, "mItemClickPosition: " + mItemClickPosition);

        }
        mLoadermanager.initLoader(1, null, this);

    }

    @Override
    public void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        if (mIsLog)
            Log.e(TAG, "onStart...");
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if (mIsLog)
            Log.e(TAG, "onResume...");
        if (null != mContactsListView && -1 != mItemClickPosition) {
            if (mIsLog)
                Log.v(TAG, "mItemClickPosition: " + mItemClickPosition);
            // mContactsListView.setSelection(mItemClickPosition);
            // setCheckedItemBackground(mItemClickPosition);

        }
        // if(null != mContactsListView && -1 != mItemClickPosition){
        // setCheckedItemBackground(mItemClickPosition);
        // Log.v(TAG, "null != mContactsListView && -1 != mItemClickPosition");
        //
        // }

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
        if (mSimpleCursorAdapter != null && cursor != null) {
            mSimpleCursorAdapter.swapCursor(cursor); // swap the new cursor
                                                     // in.
        } else {
            if (mIsLog)
                Log.v(TAG, "OnLoadFinished: mSimpleCursorAdapter is null");
        }
        if (mIsLog)
            Log.v(TAG, "OnLoadFinished...");
        if (null != mContactsListView && -1 != mItemClickPosition) {
            setCheckedItemBackground(mItemClickPosition);
        }
        // View v = getViewByPosition(mItemClickPosition, mContactsListView);
        // Log.v(TAG, "null != mContactsListView && -1 != mItemClickPosition");
        // if (null != v) {
        // v.setBackgroundResource(R.color.red);
        // Log.v(TAG, "null != v mItemClickPosition: "
        // + mItemClickPosition);
        // }
        // }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) { // when onBackPressed
        // TODO Auto-generated method stub
        // Log.v(TAG, "onLoaderReset...");
        mSimpleCursorAdapter.swapCursor(null);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (mIsLog)
            Log.e(TAG, "onCreateOptionsMenu...");
        // TODO Auto-generated method stub
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
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        if (mIsLog)
            Log.e(TAG, "onPause...");

    }

    @Override
    public void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        if (mIsLog)
            Log.e(TAG, "onStop...");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub
        super.onSaveInstanceState(outState);
        if (mIsLog)
            Log.e(TAG, "onSaveInstanceState...");
        outState.putInt(ITEM_CLICK_POSITION, mItemClickPosition);
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
                            if (1 == item) {
                                Log.e(TAG, "prePosition: " + prePosition);

                                mPreItemClickPosition = prePosition;
                                // mContactsListView.setSelection(prePosition);
                                setCheckedItemBackground(prePosition);
                            }
                        }

                    }
                });
        dialog = builder.create();
        return dialog;
    }

    public void setCheckedItemBackground(int pos) {

        View v = getViewByPosition(pos, mContactsListView);
        if (null != v) {
            if (mIsLog)
                Log.e(TAG, "setCheckedItemBackground: null != v");
            v.setBackgroundResource(R.color.purple_list_item);
            mPreListViewItemClick = v;
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
