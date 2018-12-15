package com.hi.boardify;

import android.content.Context;
import android.media.Image;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

// Purpose of DataHolder is to hold any persistent information that is needed to pass across a few activities,
// In this case we use it to pass around our userID as well as uploadCount for firebase Storage naming.
// Take note that DataHolder makes use of the Singleton Design Pattern to ensure that we don't have overlapping information that conflict
public class DataHolder {
    private static DataHolder data_holder = null;

    private String userID;
    int uploadCount = 1;
    private DataHolder(){

    }

    public static DataHolder getInstance() {
        if (data_holder == null) {
            data_holder = new DataHolder();
            return data_holder;
        } else {
            return data_holder;
        }
    }
    public void addUserID(String id){
        userID = id;
    }
    public String getUserID(){
        return userID;
    }

    public void adduploadCount(){
        uploadCount +=1;
    }
    public void decuploadCount(){
        uploadCount -=1;
    }
}
