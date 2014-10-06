package com.example.testapp.modle;

public class ContactModle {
    private String mId; //Row_ID
    private String mContactName;
    private String mContactPhoneNum;
    private String mContactEmail;

    public String getId() {
        return mId;
    }

    public void setId(String Id) {
        this.mId = Id;
    }

    public String getContactName() {
        return mContactName;
    }

    public void setContactName(String contactName) {
        this.mContactName = contactName;
    }

    public String getContactPhoneNum() {
        return mContactPhoneNum;
    }

    public void setContactPhoneNum(String contacPhoneNum) {
        this.mContactPhoneNum = contacPhoneNum;
    }

    public String getContactEmail() {
        return mContactEmail;
    }

    public void setContactEmail(String contacEmail) {
        this.mContactEmail = contacEmail;
    }

}
