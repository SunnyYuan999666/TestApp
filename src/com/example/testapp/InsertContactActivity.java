package com.example.testapp;

import java.io.File;

import com.example.testapp.fragment.ContactsImplement;
import com.example.testapp.modle.ContactModle;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.ImageColumns;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

public class InsertContactActivity extends Activity {
    private Context mContext;
    private static final String IS_MENU_ENABLED = "isMenuEnable";
    private static final String TAG = "InsertContactActivity";
    private static final int CAMERA_PIC_REQUEST = 222;
    private static final int GALLERY_REQUEST = 111;
    private static final int CROP_REQUEST = 135;
    private Menu mMenu;
    Boolean mIsPhotoExist = false;
    EditText mName, mPhoneNum, mEmail;
    ImageView mPhoto;
    Boolean mIsMenuEnable;
    Boolean mIsFromNotification = false;
    Bitmap mPhotoBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        mContext = this;
        Log.e(TAG, "onCreate...");
        setContentView(R.layout.edit_contact_file);
        mName = (EditText) findViewById(R.id.editName);
        mPhoneNum = (EditText) findViewById(R.id.editPhoneNum);
        mEmail = (EditText) findViewById(R.id.editEmail);
        mPhoto = (ImageView) findViewById(R.id.editViewPhoto);
        Bundle bundle = getIntent().getExtras();
        if (null != bundle) {
            Log.e(TAG, "null != bundle");
            String phoneN = bundle.getString("data");
            if (phoneN != null) {
                Log.e(TAG, "mIsFromNotification = true");
                mIsFromNotification = true;
                mPhoneNum.setText(phoneN);
            } else
                mIsFromNotification = false;

        }

        mName.requestFocus();
        if (savedInstanceState != null)
            mIsMenuEnable = savedInstanceState.getBoolean(IS_MENU_ENABLED,
                    false);

    }

    @Override
    protected void onNewIntent(Intent intent) {
        // TODO Auto-generated method stub
        super.onNewIntent(intent);
        Log.e(TAG, "onNewIntent...");
        Bundle bundle = intent.getExtras();
        if (null != bundle) {
            Log.e(TAG, "null != bundle");
            String phoneN = bundle.getString("data");
            if (phoneN != null) {
                Log.e(TAG, "mIsFromNotification = true");
                mIsFromNotification = true;
                mPhoneNum.setText(phoneN);
            } else
                mIsFromNotification = false;

        }

    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        Log.e(TAG, "onResume...");
        Bundle bundle = getIntent().getExtras();
        if (null != bundle) {
            Log.e(TAG, "null != bundle");
            String phoneN = bundle.getString("data");
            if (phoneN != null) {
                mIsFromNotification = true;
                mPhoneNum.setText(phoneN);
                bundle.putString("data", null);
            } else
                mIsFromNotification = false;

        }
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();

        mName.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                    int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                    int after) {
            }

            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
                if (mMenu != null) {
                    if (!mName.getText().toString().equals("")) {
                        mMenu.getItem(0).setEnabled(true);
                    } else if (mMenu != null)
                        mMenu.getItem(0).setEnabled(false);
                }

            }
        });
        mPhoto.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                photoSelectionDialog().show();

            }
        });
        Log.e(TAG, "onStart...");
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        Log.e(TAG, "onStop...");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // TODO Auto-generated method stub
        Log.e(TAG, "onCreateOptionsMenu...");
        getMenuInflater().inflate(R.menu.edit_contact, menu);
        mMenu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Log.e(TAG, "onPrepareOptionsMenu...");
        // menu.clear();
        if (mIsMenuEnable != null)
            menu.getItem(0).setEnabled(mIsMenuEnable);
        else
            menu.getItem(0).setEnabled(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {
        case R.id.actionEditdone:
            ContactModle cml = getEditContact();
            String newContactRowId = ContactsImplement.insertContactID(
                    mContext, cml, mPhotoBitmap);
            Intent intent = new Intent(InsertContactActivity.this,
                    MainActivity.class);
            intent.putExtra("cId", newContactRowId);
            Log.e(TAG, "actionEditdone cId:" + newContactRowId);

            if (mIsFromNotification) {
                Log.e(TAG, "mIsFromNotification = true");
                intent.putExtra("isInsertContactActivity", true);
                startActivity(intent);
            } else {
                Log.e(TAG, "mIsFromNotification = false");
                setResult(RESULT_OK, intent);
            }

            finish();

            break;
        case R.id.actionEditcancel:
            finish();
            break;
        default:
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    public Dialog photoSelectionDialog() {
        Dialog dialog = new Dialog(mContext);
        Builder builder = new AlertDialog.Builder(mContext);
        int arryId = 0;
        if (mIsPhotoExist)
            arryId = R.array.photoExistSelection;
        else
            arryId = R.array.photoSelection;
        builder.setItems(mContext.getResources().getStringArray(arryId),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        switch (item) {
                        case 0:
                            Intent camera_intent = new Intent(
                                    android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(camera_intent,
                                    CAMERA_PIC_REQUEST);

                            break;
                        case 1:
                            Intent intent = new Intent(
                                    Intent.ACTION_PICK,
                                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(intent, GALLERY_REQUEST);
                            break;
                        case 2:
                            mPhoto.setImageResource(R.drawable.ic_person_default);
                            mPhotoBitmap = null;
                            mIsPhotoExist = false;
                            break;
                        default:
                            break;

                        }
                    }
                });
        dialog = builder.create();
        return dialog;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
        case CAMERA_PIC_REQUEST:
            if (resultCode == RESULT_OK) {
                if (data.hasExtra("data")) {
                    // retrieve the bitmap from the intent
                    Bitmap cameraBitmap= (Bitmap) data.getExtras().get("data");
                    String urlStr = MediaStore.Images.Media.insertImage(getContentResolver(), cameraBitmap, "" , "");
                    if(null != urlStr){
                        Uri uri = Uri.parse(urlStr);
                        
                        stratActivityCropIntent(uri);
                    }
                }
            }
            break;
        case GALLERY_REQUEST:
            if (resultCode == RESULT_OK) {

                // try to retrieve the image using the data from the intent
                Cursor cursor = getContentResolver().query(data.getData(),
                        null, null, null, null);
                if (null != cursor) {
                    /*
                     * if the query worked the cursor will not be null, so we
                     * assume the normal gallery was used to choose the picture
                     */
                    cursor.moveToFirst(); // if not doing this
                    int idx = cursor.getColumnIndex(ImageColumns.DATA);
                    String fileSrc = cursor.getString(idx);
                    File file = new File(fileSrc);
                    Uri uri = Uri.fromFile(file);
                    stratActivityCropIntent(uri);
                }
                cursor.close();
            }

            break;
        case CROP_REQUEST:
            if (resultCode == RESULT_OK) {
                Bundle extras = data.getExtras();
                if (extras != null) {
                    mPhotoBitmap = extras.getParcelable("data");
                    mPhoto.setImageBitmap(mPhotoBitmap);
                    mIsPhotoExist = true;
                }

            }
            break;
        default:
            break;
        }
    }
    private void stratActivityCropIntent(Uri uri) {
        Intent cropIntent = new Intent(
                "com.android.camera.action.CROP");
        cropIntent.setDataAndType(uri, "image/*");
        cropIntent.putExtra("crop", "true");
        cropIntent.putExtra("aspectX", 1);
        cropIntent.putExtra("aspectY", 1);
//        cropIntent.putExtra("outputX", 960);
//        cropIntent.putExtra("outputY", 960);
        cropIntent.putExtra("noFaceDetection", true);
        cropIntent.putExtra("return-data", true);
        startActivityForResult(cropIntent, CROP_REQUEST);

    }

    public ContactModle getEditContact() {
        ContactModle cM = new ContactModle();
        if (!mName.getText().toString().equals("")) {
            cM.setContactName(mName.getText().toString());
        }
        if (!mPhoneNum.getText().toString().equals("")) {
            cM.setContactPhoneNum(mPhoneNum.getText().toString());
        }
        if (!mEmail.getText().toString().equals("")) {
            cM.setContactEmail(mEmail.getText().toString());
        }

        return cM;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub
        super.onSaveInstanceState(outState);
        if (mMenu != null) {
            outState.putBoolean(IS_MENU_ENABLED, mMenu.getItem(0).isEnabled());
        }
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        Intent intent = new Intent(InsertContactActivity.this,
                MainActivity.class);
        startActivity(intent);
        finish();

    }

}
