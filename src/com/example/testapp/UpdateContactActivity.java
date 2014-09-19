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

public class UpdateContactActivity extends Activity {
    private Context mContext;
    private static final String TAG = "UpdateContactActivity";
    private Menu mMenu;
    EditText mName, mPhoneNum, mEmail;
    String mContactId;
    Boolean mIsMenuEnable;
    private static final String ISMENUENABLED = "ISMENUENABLED";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.edit_contact_file);
        mName = (EditText) findViewById(R.id.editName);
        mPhoneNum = (EditText) findViewById(R.id.editPhoneNum);
        mEmail = (EditText) findViewById(R.id.editEmail);
        Bundle bundle = getIntent().getExtras();

        mContactId = bundle.getString("id");
        ContactModle editedContact = ContactsImplement.getContact(mContext,
                mContactId);
        mName.setText(editedContact.getContactName());
        mPhoneNum.setText(editedContact.getContactPhoneNum());
        mEmail.setText(editedContact.getContactEmail());
        mName.requestFocus();
        if (savedInstanceState != null)
            mIsMenuEnable = savedInstanceState.getBoolean(ISMENUENABLED, true);
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
        Log.e(TAG, "onStart");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // TODO Auto-generated method stub
        getMenuInflater().inflate(R.menu.edit_contact, menu);
        mMenu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // menu.clear();
        if (!mName.getText().toString().equals(""))
            mMenu.getItem(0).setEnabled(true);
        else
            mMenu.getItem(0).setEnabled(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {
        case R.id.actionEditdone:
            ContactModle cMl = getEditContact();
            ContactsImplement.updateContact(mContext, cMl);
            Intent intent = new Intent(UpdateContactActivity.this,
                    MainActivity.class);
            intent.putExtra("cId", cMl.getId());
            setResult(RESULT_OK, intent);
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
        cM.setId(mContactId);
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
            outState.putBoolean(ISMENUENABLED, mMenu.getItem(0).isEnabled());
        }
    }
}
