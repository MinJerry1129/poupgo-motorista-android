package com.poupgo.driver;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.adapter.files.ChatMessagesRecycleAdapter;
import com.general.files.ExecuteWebServerUrl;
import com.general.functions.GeneralFunctions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.utils.CommonUtilities;
import com.general.functions.Utils;
import com.general.functions.GenerateAlertBox;
import com.view.MTextView;

import java.util.ArrayList;
import java.util.HashMap;

public class ChatActivity extends AppCompatActivity {

    Context mContext;
    GeneralFunctions generalFunc;
    String isFrom = "";
    EditText input;

    private ChatMessagesRecycleAdapter chatAdapter;
    private ArrayList<HashMap<String, Object>> chatList;
    private int count = 0;
    ProgressBar LoadingProgressBar;
    HashMap<String, String> data_trip_ada;
    GenerateAlertBox generateAlert;

    DatabaseReference dbRef;
    String userProfileJson;
    String driverImgName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.design_trip_chat_detail_dialog);
        mContext = ChatActivity.this;

        generalFunc = new GeneralFunctions(ChatActivity.this);

        userProfileJson = generalFunc.retrieveValue(CommonUtilities.USER_PROFILE_JSON);
        driverImgName = generalFunc.getJsonValue("vImage", userProfileJson);

        data_trip_ada = new HashMap<>();
        data_trip_ada.put("iFromMemberId", getIntent().getStringExtra("iFromMemberId"));
        data_trip_ada.put("FromMemberImageName", getIntent().getStringExtra("FromMemberImageName"));
        data_trip_ada.put("iTripId", getIntent().getStringExtra("iTripId"));
        data_trip_ada.put("FromMemberName", getIntent().getStringExtra("FromMemberName"));

        dbRef = FirebaseDatabase.getInstance().getReference().child(generalFunc.retrieveValue(CommonUtilities.APP_GCM_SENDER_ID_KEY) + "-chat").child(data_trip_ada.get("iTripId") + "-Trip");

        chatList = new ArrayList<>();
        count = 0;

        show();

    }

    public void tripCancelled(String msg) {

        if (generateAlert != null) {
            generateAlert.closeAlertBox();
        }

        generateAlert = new GenerateAlertBox(ChatActivity.this);

        generateAlert.setCancelable(false);
        generateAlert.setBtnClickList(btn_id -> {
            generateAlert.closeAlertBox();
            generalFunc.saveGoOnlineInfo();
            generalFunc.restartApp(LauncherActivity.class);
        });
        generateAlert.setContentMessage("", msg);
        generateAlert.setPositiveBtn(generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"));

        generateAlert.showAlertBox();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void show() {

        ImageView msgbtn = (ImageView) findViewById(R.id.msgbtn);
        input = (EditText) findViewById(R.id.input);

        input.setHint(generalFunc.retrieveLangLBl("Enter a message", "LBL_ENTER_MSG_TXT"));

        msgbtn.setColorFilter(ContextCompat.getColor(mContext, R.color.lightchatbtncolor), android.graphics.PorterDuff.Mode.SRC_IN);

        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (s.length() == 0) {
                    msgbtn.setColorFilter(ContextCompat.getColor(mContext, R.color.lightchatbtncolor), android.graphics.PorterDuff.Mode.SRC_IN);
                } else {
                    msgbtn.setColorFilter(null);
                }
            }
        });


        (findViewById(R.id.backImgView)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.hideKeyboard(ChatActivity.this);
                onBackPressed();
            }
        });

        msgbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Utils.checkText(input) && Utils.getText(input).length() > 0) {

                    HashMap<String, Object> dataMap = new HashMap<String, Object>();
                    dataMap.put("eUserType", CommonUtilities.app_type);
                    dataMap.put("Text", input.getText().toString().trim());
                    dataMap.put("iTripId", data_trip_ada.get("iTripId"));
                    dataMap.put("driverImageName", driverImgName);
                    dataMap.put("passengerImageName", data_trip_ada.get("FromMemberImageName"));
                    dataMap.put("driverId", generalFunc.getMemberId());
                    dataMap.put("passengerId", data_trip_ada.get("iFromMemberId"));

                    dbRef.push().setValue(dataMap, (databaseError, databaseReference) -> {

                        if (databaseError != null) {
                            Utils.printLog("ERROR - Messaage Chat", ":::" + databaseError.getMessage());
                        } else {
                            sendTripMessageNotification(input.getText().toString().trim());
                            input.setText("");
                        }
                    });

                } else {

                }
            }
        });

        final RecyclerView chatCategoryRecyclerView = (RecyclerView) findViewById(R.id.chatCategoryRecyclerView);

        chatAdapter = new ChatMessagesRecycleAdapter(mContext, chatList, generalFunc, data_trip_ada);
        chatCategoryRecyclerView.setAdapter(chatAdapter);
        chatAdapter.notifyDataSetChanged();

        ((MTextView) findViewById(R.id.titleTxt)).setText(data_trip_ada.get("FromMemberName"));

        dbRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                if (dataSnapshot.getValue() != null && dataSnapshot.getValue() instanceof HashMap) {

                    Utils.printLog("DataConvert", ":::" + dataSnapshot.getValue().toString());

                    HashMap<String, Object> dataMap = (HashMap<String, Object>) dataSnapshot.getValue();
                    chatList.add(dataMap);

                    chatAdapter.notifyDataSetChanged();
                    chatCategoryRecyclerView.scrollToPosition(chatList.size() - 1);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void sendTripMessageNotification(String message) {

        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("type", "SendTripMessageNotification");
        parameters.put("UserType", Utils.userType);
        parameters.put("iFromMemberId", generalFunc.getMemberId());
        parameters.put("iTripId", data_trip_ada.get("iTripId"));
        parameters.put("iToMemberId", data_trip_ada.get("iFromMemberId"));
        parameters.put("tMessage", message);

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(mContext, parameters);
        exeWebServer.setLoaderConfig(mContext, false, generalFunc);
        exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
            @Override
            public void setResponse(String responseString) {

                Utils.printLog("Response", "::" + responseString);

            }
        });
        exeWebServer.execute();
    }

}
