package com.utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.general.files.FireTripStatusMsg;
import com.general.files.MyApp;
import com.general.functions.GeneralFunctions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static com.facebook.FacebookSdk.getApplicationContext;

public class CustomChildEventListener implements ChildEventListener {
    GeneralFunctions generalFunctions;
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public DatabaseReference rootRef;

    public CustomChildEventListener(GeneralFunctions generalFunctions, DatabaseReference rootRef) {
        this.generalFunctions = generalFunctions;
        this.rootRef = rootRef;
    }

    @Override
    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        CabRequest cabRequest = dataSnapshot.getValue(CabRequest.class);
        String key = dataSnapshot.getKey();
        Log.v("FelipeH", key + cabRequest.getMetadata());

        if (generalFunctions.getMemberId().toString().equals(cabRequest.getDriver_ids())) {


            Calendar dataFinal = Calendar.getInstance();
            try {
                dataFinal.setTime(format.parse(cabRequest.getCreated_at()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            long diferenca = (System.currentTimeMillis() - dataFinal.getTimeInMillis()) / 1000;


            if (diferenca > 15) {
                rootRef.child("cab_requests").child(key).removeValue();
            } else {
                rootRef.child("cab_requests").child(key).removeValue();
                new FireTripStatusMsg(MyApp.getInstance() != null ? MyApp.getInstance().getCurrentAct() : getApplicationContext()).fireTripMsg(cabRequest.getMetadata());

            }

        }
    }

    @Override
    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {


    }

    @Override
    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

    }

    @Override
    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }

    public void remove() {
        rootRef.removeEventListener(this);
    }
}
