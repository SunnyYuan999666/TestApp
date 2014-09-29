package com.example.testapp.fragment;

import java.util.ArrayList;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.PhoneLookup;
import android.provider.ContactsContract.RawContacts;
import android.util.Log;

import com.example.testapp.modle.ContactModle;

public class ContactsImplement {
    static ArrayList<ContactModle> mContacts = null;
    private static final String TAG = "ContactsImplement";
    final static String[] mInfo = new String[] { ContactsContract.Contacts._ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.Contacts.HAS_PHONE_NUMBER };

    public static String insertContactID(Context c, ContactModle cm) {

        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
        int rawContactId = ops.size();
        Log.e(TAG, "InsertContact--");
        ops.add(ContentProviderOperation
                .newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build());

        ops.add(ContentProviderOperation.newInsert(Data.CONTENT_URI)
                .withValueBackReference(Data.RAW_CONTACT_ID, rawContactId)
                .withValue(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE)
                .withValue(StructuredName.DISPLAY_NAME, cm.getContactName())
                .build());

        ops.add(ContentProviderOperation
                .newInsert(Data.CONTENT_URI)
                .withValueBackReference(Data.RAW_CONTACT_ID, rawContactId)
                .withValue(
                        ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER,
                        cm.getContactPhoneNum()).build());

        ops.add(ContentProviderOperation
                .newInsert(Data.CONTENT_URI)
                .withValueBackReference(Data.RAW_CONTACT_ID, rawContactId)

                .withValue(
                        ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Email.DATA,
                        cm.getContactEmail())
                .withValue(ContactsContract.CommonDataKinds.Email.TYPE,
                        Email.TYPE_WORK).build());
        ContentProviderResult[] res;
        try {
            res = c.getContentResolver().applyBatch(ContactsContract.AUTHORITY,
                    ops);
            Uri myContactUri = res[0].uri;
            int lastSlash = myContactUri.toString().lastIndexOf("/");
            int length = myContactUri.toString().length();
            int contactID = Integer.parseInt((String) myContactUri.toString()
                    .subSequence(lastSlash + 1, length));
            return String.valueOf(contactID);
            // ops.clear();
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    public static void deleteContact(Context c, String id) {
        Log.e(TAG, "DeleteContact id: " + id);

        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

        ops.add(ContentProviderOperation.newDelete(RawContacts.CONTENT_URI)
                .withSelection(RawContacts._ID + "=?", new String[] { id }) // RawContacts._ID
                .build());
        try {
            c.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public static void updateContact(Context c, ContactModle newCM) {
        Log.e(TAG, "updateContact id: " + newCM.getId());
        // ContactModle preCM = getContact(c, newCM.getId());
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

        // Name
        ops.add(ContentProviderOperation
                .newUpdate(Data.CONTENT_URI)
                .withSelection(
                        Data.RAW_CONTACT_ID + " = ? AND " + Data.MIMETYPE
                                + " = ?",
                        new String[] { String.valueOf(newCM.getId()),
                                StructuredName.CONTENT_ITEM_TYPE })
                .withValue(StructuredName.DISPLAY_NAME, newCM.getContactName())
                .build());

        // Phone Number
        ops.add(ContentProviderOperation
                .newUpdate(Data.CONTENT_URI)
                .withSelection(
                        Data.RAW_CONTACT_ID + " = ? AND " + Data.MIMETYPE
                                + " = ?",
                        new String[] { String.valueOf(newCM.getId()),
                                Phone.CONTENT_ITEM_TYPE })
                .withValue(Phone.NUMBER, newCM.getContactPhoneNum()).build());

        // Email Address
        ops.add(ContentProviderOperation
                .newUpdate(Data.CONTENT_URI)
                .withSelection(
                        Data.RAW_CONTACT_ID + " = ? AND " + Data.MIMETYPE
                                + " = ?",
                        new String[] { String.valueOf(newCM.getId()),
                                Email.CONTENT_ITEM_TYPE })
                .withValue(Email.DATA, newCM.getContactEmail()).build());

        try {
            c.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public static ContactModle getContact(Context c, String id) {
        Log.e(TAG, "getContact");
        ContactModle cm = new ContactModle();
        ContentResolver contentResolver = c.getContentResolver();

        Cursor rawIdCursor = contentResolver.query(RawContacts.CONTENT_URI,
                null, RawContacts._ID + " = " + id, null, null);

        if (rawIdCursor.moveToFirst()) {
            String contactId = rawIdCursor.getString(rawIdCursor
                    .getColumnIndex(RawContacts.CONTACT_ID));

            cm.setId(id);

            Cursor contactCursor = contentResolver.query(
                    android.provider.ContactsContract.Contacts.CONTENT_URI,
                    mInfo, ContactsContract.Contacts._ID + " = " + contactId,
                    null, null);
            if (contactCursor.moveToFirst()) {

                cm.setContactName(contactCursor.getString(contactCursor
                        .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));

                int hasPN = Integer
                        .parseInt(contactCursor.getString(contactCursor
                                .getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)));
                if (1 == hasPN) {

                    Cursor phoneNumCursor = contentResolver.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                                    + " = " + contactId, null, null);
                    phoneNumCursor.moveToFirst();
                    cm.setContactPhoneNum(phoneNumCursor.getString(phoneNumCursor
                            .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
                    // Log.i(TAG, cM.getContactPhoneNum());
                    phoneNumCursor.close();

                }
                Cursor emailCursor = contentResolver.query(
                        ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                        null, ContactsContract.CommonDataKinds.Email.CONTACT_ID
                                + " = " + contactId, null, null);
                if (emailCursor.moveToNext()) {
                    // Log.e(TAG,
                    // "emailCursor.getCount(): "+emailCursor.getCount());
                    // emailCursor.moveToFirst();
                    String cEmail = emailCursor
                            .getString(emailCursor
                                    .getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS));
                    if (cEmail != null) {
                        cm.setContactEmail(emailCursor.getString(emailCursor
                                .getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS)));

                        // Log.e(TAG, cM.getContactEmail());

                    }
                }
                emailCursor.close();
            }
            contactCursor.close();

        }
        rawIdCursor.close();

        return cm;
    }

    public static String rowIdOfExistPhoneNum(Context context, String pNum) {
        ContentResolver contentResolver = context.getContentResolver();
        Cursor contactCursor = contentResolver.query(
                ContactsContract.Contacts.CONTENT_URI,
                new String[] { ContactsContract.Contacts._ID,ContactsContract.Contacts.HAS_PHONE_NUMBER }, null, null,
                ContactsContract.Contacts.DISPLAY_NAME
                        + " COLLATE LOCALIZED ASC");
        contactCursor.getCount();
        while (contactCursor.moveToNext()) {
            int hasPN = Integer
                    .parseInt(contactCursor.getString(contactCursor
                            .getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)));
            if (1 == hasPN) {
                int contactID = contactCursor.getInt(contactCursor
                        .getColumnIndex(ContactsContract.Contacts._ID));

                Cursor phoneNumCursor = contentResolver.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                                + " = " + contactID, null, null);
                phoneNumCursor.moveToFirst();
                if (pNum.equals(phoneNumCursor.getString(phoneNumCursor
                        .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)))) {
                    Cursor rawIdCursor = contentResolver.query(
                            RawContacts.CONTENT_URI, null,
                            RawContacts.CONTACT_ID + " = " + contactID, null,
                            null);
                    if (rawIdCursor.moveToFirst()) {

                        return rawIdCursor.getString(rawIdCursor
                                .getColumnIndex(RawContacts._ID));

                    }
                    rawIdCursor.close();

                }
                phoneNumCursor.close();

            }

        }
        contactCursor.close();
        return null;
    }

    public static ArrayList<ContactModle> fetchContacts(Context c) {
        mContacts = new ArrayList<ContactModle>();

        ContentResolver contentResolver = c.getContentResolver();
        Cursor contactCursor = contentResolver.query(
                ContactsContract.Contacts.CONTENT_URI, mInfo, null, null,
                ContactsContract.Contacts.DISPLAY_NAME
                        + " COLLATE LOCALIZED ASC");
        Log.e(TAG, "fetchContactsf: " + contactCursor.getCount());

        while (contactCursor.moveToNext()) {
            ContactModle cm = new ContactModle();
            int contactID = contactCursor.getInt(contactCursor
                    .getColumnIndex(ContactsContract.Contacts._ID));

            Cursor rawIdCursor = contentResolver.query(RawContacts.CONTENT_URI,
                    null, RawContacts.CONTACT_ID + " = " + contactID, null,
                    null);
            if (rawIdCursor.moveToFirst()) {
                String rowId = rawIdCursor.getString(rawIdCursor
                        .getColumnIndex(RawContacts._ID));
                // Log.e(TAG, "row id: "+rowId);

                cm.setId(rowId);

            }
            rawIdCursor.close();

            // cM.setId(contactCursor.getString(contactCursor
            // .getColumnIndex(ContactsContract.Contacts._ID))); //
            // ContactsContract.Contacts._ID

            cm.setContactName(contactCursor.getString(contactCursor
                    .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));

            // Log.e(TAG, cM.getId() + ":");
            // Log.e(TAG, cM.getContactName());

            int hasPN = Integer
                    .parseInt(contactCursor.getString(contactCursor
                            .getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)));

            if (1 == hasPN) {

                Cursor phoneNumCursor = contentResolver.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                                + " = " + contactID, null, null);
                phoneNumCursor.moveToFirst();
                cm.setContactPhoneNum(phoneNumCursor.getString(phoneNumCursor
                        .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
                // Log.e(TAG, cM.getContactPhoneNum());
                phoneNumCursor.close();

            }
            Cursor emailCursor = contentResolver.query(
                    ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                    ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = "
                            + contactID, null, null);
            if (emailCursor.moveToNext()) {
                // Log.e(TAG,
                // "emailCursor.getCount(): "+emailCursor.getCount());
                // emailCursor.moveToFirst();
                String cEmail = emailCursor
                        .getString(emailCursor
                                .getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS));
                if (cEmail != null) {
                    cm.setContactEmail(emailCursor.getString(emailCursor
                            .getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS)));

                    // Log.e(TAG, cM.getContactEmail());

                }
            }
            emailCursor.close();

            mContacts.add(cm);
        }
        contactCursor.close();
        return mContacts;
    }

}
