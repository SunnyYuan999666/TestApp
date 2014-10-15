package com.example.contactsapp.fragment;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;

import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;
import android.util.Log;

import com.example.caontactsapp.modle.ContactModle;

public class ContactsImplement {
    // static ArrayList<ContactModle> mContacts = null;
    private static final String TAG = "ContactsImplement";
    final static String[] mInfo = new String[] { ContactsContract.Contacts._ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.Contacts.HAS_PHONE_NUMBER };
    private static Boolean mIsLog = false;

    public static int getPhotoId(Context c, String rowId) {
        int PhotoId = 0;
        String contactId = getContactId(c, rowId);
        if (mIsLog)
            Log.v(TAG, "rowId: " + rowId + "  contactId: " + contactId);
        Cursor cursor = c.getContentResolver().query(Contacts.CONTENT_URI,
                new String[] { Contacts.PHOTO_ID }, Contacts._ID + "=?", // Contacts._ID
                new String[] { contactId }, null);
        if (cursor.moveToFirst()) {
            PhotoId = cursor.getInt(cursor.getColumnIndex(Contacts.PHOTO_ID));
        }
        if (mIsLog)
            Log.v(TAG, "PhotoId: " + PhotoId);
        cursor.close();
        return PhotoId;
    }

    public static Bitmap queryContactImage(Context c, int imageDataRow) {
        Cursor cursor = c.getContentResolver().query(
                ContactsContract.Data.CONTENT_URI,
                new String[] { ContactsContract.CommonDataKinds.Photo.PHOTO },
                ContactsContract.Data._ID + "=?",
                new String[] { Integer.toString(imageDataRow) }, null);
        byte[] imageBytes = null;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                imageBytes = cursor.getBlob(0);
            }

        }
        cursor.close();
        if (imageBytes != null) {
            return BitmapFactory.decodeByteArray(imageBytes, 0,
                    imageBytes.length);
        }
        return null;

    }

    public static byte[] Bitmap2Bytes(Bitmap bm) {
        if (null == bm)
            return null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    public static String getContactId(Context context, String rowId) {
        ContentResolver contentResolver = context.getContentResolver();
        Cursor contactCursor = contentResolver.query(RawContacts.CONTENT_URI,
                new String[] { RawContacts.CONTACT_ID }, RawContacts._ID
                        + " = " + rowId, null, null);
        String contactId = "";

        if (contactCursor.moveToFirst()) {
            contactId = contactCursor.getString(contactCursor
                    .getColumnIndex(RawContacts.CONTACT_ID));
        }
        contactCursor.close();
        return contactId;
    }

    public static String insertContactID(Context c, ContactModle cm,
            Bitmap photoBitmap) {

        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
        int rawContactId = ops.size();
        if (mIsLog)
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

        ops.add(ContentProviderOperation
                .newInsert(Data.CONTENT_URI)
                .withValueBackReference(Data.RAW_CONTACT_ID, rawContactId)
                .withValue(
                        ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Photo.PHOTO,
                        Bitmap2Bytes(photoBitmap)).build());

        ContentProviderResult[] res;
        try {
            res = c.getContentResolver().applyBatch(ContactsContract.AUTHORITY,
                    ops);
            Uri myContactUri = res[0].uri;
            int lastSlash = myContactUri.toString().lastIndexOf("/");
            int length = myContactUri.toString().length();
            String contactRowID = (String) myContactUri.toString().subSequence(
                    lastSlash + 1, length);
            return contactRowID;
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
        if (mIsLog)
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

    public static void updateContact(Context c, ContactModle newCM,
            Bitmap photoBitmap) {
        if (mIsLog)
            Log.e(TAG, "updateContact id: " + newCM.getId());
        // ContactModle preCM = getContact(c, newCM.getId());
        Boolean isNewPhoto = false;
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
        if (mIsLog)
            Log.v(TAG, "updateContact: null != photoBitmap");
        int photoId = getPhotoId(c, newCM.getId());
        if (0 != photoId) { // update
            if (mIsLog)
                Log.v(TAG, "photoId: " + photoId);
            ops.add(ContentProviderOperation
                    .newUpdate(Data.CONTENT_URI)
                    .withSelection(
                            Data.RAW_CONTACT_ID + " = ? AND " + Data.MIMETYPE
                                    + " = ?",
                            new String[] {
                                    String.valueOf(newCM.getId()),
                                    ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE })
                    .withValue(ContactsContract.CommonDataKinds.Photo.PHOTO,
                            Bitmap2Bytes(photoBitmap)).build());
        } else { // new
            isNewPhoto = true;
        }

        try {
            c.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
            if (mIsLog)
                Log.v(TAG, "updateContact ok!");
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (isNewPhoto)
            newContactPhoto(c, newCM.getId(), photoBitmap);

    }

    public static void newContactPhoto(Context context, String rowId,
            Bitmap photoBitmap) {
        ArrayList<ContentProviderOperation> opsNew = new ArrayList<ContentProviderOperation>();
        opsNew.add(ContentProviderOperation
                .newInsert(ContactsContract.Data.CONTENT_URI)
                .withValue(ContactsContract.Data.RAW_CONTACT_ID, rowId)
                .withValue(
                        ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Photo.PHOTO,
                        Bitmap2Bytes(photoBitmap)).build());

        try {
            if (mIsLog)
                Log.v(TAG, "new PHOTO ok!");
            context.getContentResolver().applyBatch(ContactsContract.AUTHORITY,
                    opsNew);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public static ContactModle getContact(Context c, String id) {
        if (mIsLog)
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

    public static String rawIdOfExistPhoneNum(Context context, String pNum) {
        String rawContactsId = null;
        ContentResolver contentResolver = context.getContentResolver();
        Cursor contactCursor = contentResolver.query(
                ContactsContract.Contacts.CONTENT_URI, new String[] {
                        ContactsContract.Contacts._ID,
                        ContactsContract.Contacts.HAS_PHONE_NUMBER }, null,
                null, ContactsContract.Contacts.DISPLAY_NAME
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
                        rawContactsId = rawIdCursor.getString(rawIdCursor
                                .getColumnIndex(RawContacts._ID));
                    }
                    rawIdCursor.close();

                }
                phoneNumCursor.close();

            }

        }
        contactCursor.close();
        return rawContactsId;
    }

    public static int getPositionInList(Context ctx, String rowId) {
        int pos = 0;
        ContentResolver contentResolver = ctx.getContentResolver();

        Cursor rawCursor = contentResolver.query(RawContacts.CONTENT_URI,
                new String[] { RawContacts._ID,RawContacts.DELETED }, null, null,
                ContactsContract.Contacts.DISPLAY_NAME
                        + " COLLATE LOCALIZED ASC");
        // rawCursor.moveToFirst();
        String newRowId = "";
        Log.v(TAG, "rawCursor.getCount(): " + rawCursor.getCount());
        while (rawCursor.moveToNext()) {

            newRowId = rawCursor.getString(rawCursor
                    .getColumnIndex(RawContacts._ID));
            if (rowId.equals(newRowId)) {
                // pos = rawCursor.getPosition();
                rawCursor.close();
                return pos;
            }
            String isDelete = rawCursor.getString(rawCursor
                    .getColumnIndex(RawContacts.DELETED));
            if (!"1".equals(isDelete))
                pos++;
        }
        rawCursor.close();
        return pos;
    }

    // public static ArrayList<ContactModle> fetchContacts(Context c) {
    // mContacts = new ArrayList<ContactModle>();
    //
    // ContentResolver contentResolver = c.getContentResolver();
    // Cursor contactCursor = contentResolver.query(
    // ContactsContract.Contacts.CONTENT_URI, mInfo, null, null,
    // ContactsContract.Contacts.DISPLAY_NAME
    // + " COLLATE LOCALIZED ASC");
    // if (mIsLog)
    // Log.e(TAG, "fetchContactsf: " + contactCursor.getCount());
    //
    // while (contactCursor.moveToNext()) {
    // ContactModle cm = new ContactModle();
    // int contactID = contactCursor.getInt(contactCursor
    // .getColumnIndex(ContactsContract.Contacts._ID));
    //
    // Cursor rawIdCursor = contentResolver.query(RawContacts.CONTENT_URI,
    // null, RawContacts.CONTACT_ID + " = " + contactID, null,
    // null);
    // if (rawIdCursor.moveToFirst()) {
    // String rowId = rawIdCursor.getString(rawIdCursor
    // .getColumnIndex(RawContacts._ID));
    // // Log.e(TAG, "row id: "+rowId);
    //
    // cm.setId(rowId);
    //
    // }
    // rawIdCursor.close();
    //
    // // cM.setId(contactCursor.getString(contactCursor
    // // .getColumnIndex(ContactsContract.Contacts._ID))); //
    // // ContactsContract.Contacts._ID
    //
    // cm.setContactName(contactCursor.getString(contactCursor
    // .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));
    //
    // // Log.e(TAG, cM.getId() + ":");
    // // Log.e(TAG, cM.getContactName());
    //
    // int hasPN = Integer
    // .parseInt(contactCursor.getString(contactCursor
    // .getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)));
    //
    // if (1 == hasPN) {
    //
    // Cursor phoneNumCursor = contentResolver.query(
    // ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
    // null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID
    // + " = " + contactID, null, null);
    // phoneNumCursor.moveToFirst();
    // cm.setContactPhoneNum(phoneNumCursor.getString(phoneNumCursor
    // .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
    // // Log.e(TAG, cM.getContactPhoneNum());
    // phoneNumCursor.close();
    //
    // }
    // Cursor emailCursor = contentResolver.query(
    // ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
    // ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = "
    // + contactID, null, null);
    // if (emailCursor.moveToNext()) {
    // // Log.e(TAG,
    // // "emailCursor.getCount(): "+emailCursor.getCount());
    // // emailCursor.moveToFirst();
    // String cEmail = emailCursor
    // .getString(emailCursor
    // .getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS));
    // if (cEmail != null) {
    // cm.setContactEmail(emailCursor.getString(emailCursor
    // .getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS)));
    //
    // // Log.e(TAG, cM.getContactEmail());
    //
    // }
    // }
    // emailCursor.close();
    //
    // mContacts.add(cm);
    // }
    // contactCursor.close();
    // return mContacts;
    // }

}
