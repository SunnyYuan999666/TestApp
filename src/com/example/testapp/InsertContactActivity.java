package com.example.testapp;

import com.example.testapp.fragment.ContactsImplement;
import com.example.testapp.modle.ContactModle;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

public class InsertContactActivity extends Activity {
    private Context mContext;
    private static final String IS_MENU_ENABLED = "isMenuEnable";
    private static final String TAG = "InsertContactActivity";
    private Menu mMenu;
    EditText mName, mPhoneNum, mEmail;
    Boolean mIsMenuEnable;
    Boolean mIsFromNotification = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        mContext = this;
        Log.e(TAG, "onCreate...");
        Log.w(TAG, "Task id: " +  getTaskId());  
        setContentView(R.layout.edit_contact_file);
        mName = (EditText) findViewById(R.id.editName);
        mPhoneNum = (EditText) findViewById(R.id.editPhoneNum);
        mEmail = (EditText) findViewById(R.id.editEmail);
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
            String newContactId = ContactsImplement.insertContactID(mContext,
                    cml);
            Intent intent = new Intent(InsertContactActivity.this,
                    MainActivity.class);
            intent.putExtra("cId", newContactId);
            Log.e(TAG, "actionEditdone cId:" + newContactId);

            if (mIsFromNotification) {
                Log.e(TAG, "mIsFromNotification = true");
                intent.putExtra("isInsertContactActivity", true);
                startActivity(intent);
            }else{
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
