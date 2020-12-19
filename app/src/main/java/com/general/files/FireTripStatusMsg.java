package com.general.files;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.poupgo.driver.CabRequestedActivity;
import com.poupgo.driver.ChatActivity;
import com.general.functions.GeneralFunctions;
import com.utils.CommonUtilities;
import com.general.functions.Utils;
import com.general.functions.GenerateAlertBox;
import com.poupgo.driver.DriverArrivedActivity;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Created by Admin on 21/03/18.
 */

public class FireTripStatusMsg {
    Context mContext;
    private static String tmp_msg_chk = "";

    public FireTripStatusMsg() {
    }

    public FireTripStatusMsg(Context mContext) {
        this.mContext = mContext;
    }

    public void fireTripMsg(String message) {

        Utils.printLog("fireTripMsg", ":: called");
        if (tmp_msg_chk.equals(message)) {
            return;
        }

        tmp_msg_chk = message;

        String finalMsg = message;

        Utils.printLog("SocketApp", ":msgReceived:" + message);

        if (!GeneralFunctions.isJsonObj(finalMsg)) {
            try {
                finalMsg = new JSONTokener(message).nextValue().toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (!GeneralFunctions.isJsonObj(finalMsg)) {
                finalMsg = finalMsg.replaceAll("^\"|\"$", "");

                if (!GeneralFunctions.isJsonObj(finalMsg)) {
                    finalMsg = message.replaceAll("\\\\", "");

                    finalMsg = finalMsg.replaceAll("^\"|\"$", "");

                    if (!GeneralFunctions.isJsonObj(finalMsg)) {
                        finalMsg = message.replace("\\\"", "\"").replaceAll("^\"|\"$", "");
                    }

                    finalMsg = finalMsg.replace("\\\\\"", "\\\"");
                }
            }
        }

        if (MyApp.getInstance() == null) {
            if (mContext != null) {
                dispatchNotification(finalMsg);
            }
            return;
        }

        if (MyApp.getInstance().getCurrentAct() != null) {
            mContext = MyApp.getInstance().getCurrentAct();
        }

        if (mContext == null) {
            dispatchNotification(finalMsg);
            return;
        }

        GeneralFunctions generalFunc = new GeneralFunctions(mContext);

        JSONObject obj_msg = generalFunc.getJsonObject(finalMsg);

        String tSessionId = generalFunc.getJsonValueStr("tSessionId", obj_msg);

        if (!tSessionId.equals("") && !tSessionId.equals(generalFunc.retrieveValue(Utils.SESSION_ID_KEY))) {
            return;
        }

        if (!GeneralFunctions.isJsonObj(finalMsg)) {
            LocalNotification.dispatchLocalNotification(mContext, message, true);
            generalFunc.showGeneralMessage("", message);
            return;
        }

        boolean isMsgExist = isTripStatusMsgExist(generalFunc, finalMsg, mContext);

        if (isMsgExist == true) {
            return;
        }


        if (mContext instanceof Activity) {
            ((Activity) mContext).runOnUiThread(() -> continueDispatchMsg(generalFunc, obj_msg));
        } else {
            dispatchNotification(finalMsg);
        }


    }

    public boolean isTripStatusMsgExist(GeneralFunctions generalFunc, String msg, Context mContext) {

        JSONObject obj_tmp = generalFunc.getJsonObject(msg);

        if (obj_tmp != null) {
            String message = generalFunc.getJsonValueStr("Message", obj_tmp);

            if (!message.equals("")) {
                String iTripId = generalFunc.getJsonValueStr("iTripId", obj_tmp);


                if (!iTripId.equals("")) {
                    String vTitle = generalFunc.getJsonValueStr("vTitle", obj_tmp);
                    String time = generalFunc.getJsonValueStr("time", obj_tmp);
                    String key = CommonUtilities.TRIP_REQ_CODE_PREFIX_KEY + iTripId + "_" + message;
                    if (message.equals("DestinationAdded")) {
                        String destKey = key;

                        Long newMsgTime = generalFunc.parseLongValue(0, time);

                        String destKeyValueStr = generalFunc.retrieveValue(destKey, mContext);
                        if (!destKeyValueStr.equals("")) {

                            Long destKeyValue = generalFunc.parseLongValue(0, destKeyValueStr);

                            if (newMsgTime > destKeyValue) {
                                generalFunc.removeValue(destKey);
                            } else {
                                return true;
                            }
                        }
                    }

                    String data = generalFunc.retrieveValue(key);

                    if (data.equals("")) {
                        LocalNotification.dispatchLocalNotification(mContext, vTitle, true);
                        if (time.equals("")) {
                            generalFunc.storedata(key, "" + System.currentTimeMillis());
                        } else {
                            generalFunc.storedata(key, "" + time);
                        }
                        return false;
                    } else {
                        return true;
                    }
                } else if (!message.equals("") && message.equalsIgnoreCase("CabRequested")) {
                    String msgCode = generalFunc.getJsonValueStr("MsgCode", obj_tmp);
                    String key = CommonUtilities.DRIVER_REQ_CODE_PREFIX_KEY + msgCode;

                    String data = generalFunc.retrieveValue(key);

                    if (data.equals("")) {
                        generalFunc.storedata(key, "" + System.currentTimeMillis());
                        return false;
                    }
                }
            }

        }

        return false;
    }

    private void continueDispatchMsg(GeneralFunctions generalFunc, JSONObject obj_msg) {
        String messageStr = generalFunc.getJsonValueStr("Message", obj_msg);
        if (messageStr.equals("")) {

            String msgTypeStr = generalFunc.getJsonValueStr("MsgType", obj_msg);
            String messageType_str = generalFunc.getJsonValueStr("MessageType", obj_msg);


            if (msgTypeStr.equalsIgnoreCase("CHAT")) {
                LocalNotification.dispatchLocalNotification(mContext, generalFunc.getJsonValueStr("Msg", obj_msg), false);

                if (MyApp.getInstance().getCurrentAct() instanceof ChatActivity == false) {

                    Bundle bn = new Bundle();

                    bn.putString("iFromMemberId", generalFunc.getJsonValueStr("iFromMemberId", obj_msg));
                    bn.putString("FromMemberImageName", generalFunc.getJsonValueStr("FromMemberImageName", obj_msg));
                    bn.putString("iTripId", generalFunc.getJsonValueStr("iTripId", obj_msg));
                    bn.putString("FromMemberName", generalFunc.getJsonValueStr("FromMemberName", obj_msg));

                    Intent chatActInt = new Intent(MyApp.getInstance().getApplicationContext(), ChatActivity.class);

                    chatActInt.putExtras(bn);

                    chatActInt.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    MyApp.getInstance().getApplicationContext().startActivity(chatActInt);
                }
            }


        } else if (!messageStr.equals("")) {
            String vTitle = generalFunc.getJsonValueStr("vTitle", obj_msg);

//            LocalNotification.dispatchLocalNotification(mContext,vTitle,false);

            if (messageStr.equalsIgnoreCase("TripCancelled") ) {
                generalFunc.saveGoOnlineInfo();


                try {
                    Intent it = new Intent(MyApp.getInstance().getApplicationContext(), DriverArrivedActivity.class);
                    DriverArrivedActivity driver = (DriverArrivedActivity) mContext;
                    it.putExtra("TRIP_DATA",driver.data_trip);
                    it.putExtra("CANCEL",true);
                    it.putExtra("vTitle",vTitle);

                    this.mContext.startActivity(it);
                }catch (Exception e){
                    final GenerateAlertBox generateAlert = new GenerateAlertBox(mContext);
                    generateAlert.setCancelable(false);
                    generateAlert.setBtnClickList(btn_id -> {
                        (new getUserData(generalFunc, mContext)).getData();
                    });
                    generateAlert.setContentMessage("", vTitle);
                    generateAlert.setPositiveBtn(generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"));
                    generateAlert.showAlertBox();
                }




            }else  if(messageStr.equalsIgnoreCase("DestinationAdded")){

                LocalNotification.dispatchLocalNotification(mContext, vTitle, false);

                final GenerateAlertBox generateAlert = new GenerateAlertBox(mContext);
                generateAlert.setCancelable(false);
                generateAlert.setBtnClickList(btn_id -> {
                    (new getUserData(generalFunc, mContext)).getData();
                });
                generateAlert.setContentMessage("", vTitle);
                generateAlert.setPositiveBtn(generalFunc.retrieveLangLBl("", "LBL_BTN_OK_NEW_DESTINATION"));
                generateAlert.showAlertBox();
            } else if (messageStr.equalsIgnoreCase("CabRequested")) {
                if (MyApp.getInstance().mainAct != null && MyApp.getInstance().driverArrivedAct == null && MyApp.getInstance().activeTripAct == null) {
                    dispatchCabRequest(generalFunc, obj_msg.toString());
                } else if (MyApp.getInstance().mainAct == null && MyApp.getInstance().driverArrivedAct == null && MyApp.getInstance().activeTripAct == null) {
                    dispatchCabRequest(generalFunc, obj_msg.toString());
                }
            } else {
                LocalNotification.dispatchLocalNotification(mContext, vTitle, false);
            }

        }
    }

    private void dispatchCabRequest(GeneralFunctions generalFunc, String message) {


        if (generalFunc.containsKey(CommonUtilities.DRIVER_REQ_COMPLETED_MSG_CODE_KEY + (generalFunc.getJsonValue("MsgCode", message)))) {
            return;

        }

        if (generalFunc.getJsonValue("REQUEST_TYPE", message) != null) {
            LocalNotification.dispatchLocalNotification(mContext, generalFunc.retrieveLangLBl("", "LBL_TRIP_USER_WAITING"), true);
        } else {
            LocalNotification.dispatchLocalNotification(mContext, generalFunc.retrieveLangLBl("", "LBL_TRIP_USER_WAITING"), true);
        }

        generalFunc.storedata(CommonUtilities.DRIVER_ACTIVE_REQ_MSG_KEY, message);

        Intent cabReqAct = new Intent(MyApp.getInstance().getApplicationContext(), CabRequestedActivity.class);
        cabReqAct.putExtra("Message", message);
        cabReqAct.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);

        if (MyApp.getInstance() != null && MyApp.getInstance().getApplicationContext() != null) {
            MyApp.getInstance().getApplicationContext().startActivity(cabReqAct);
        } else if (this.mContext != null) {
            this.mContext.startActivity(cabReqAct);
        }

    }


    private void dispatchNotification(String message) {

        Context mLocContext = this.mContext;

        if (mLocContext == null && MyApp.getInstance() != null && MyApp.getInstance().getCurrentAct() == null) {
            mLocContext = MyApp.getInstance().getApplicationContext();
        }

        if (mLocContext != null) {
            GeneralFunctions generalFunc = new GeneralFunctions(mLocContext);


            if (!GeneralFunctions.isJsonObj(message)) {
                LocalNotification.dispatchLocalNotification(mLocContext, message, true);

                return;
            }
            JSONObject obj_msg = generalFunc.getJsonObject(message);

            String message_str = generalFunc.getJsonValueStr("Message", obj_msg);

            if (message_str.equals("")) {
                String msgType_str = generalFunc.getJsonValueStr("MsgType", obj_msg);

                switch (msgType_str) {
                    case "CHAT":
                        generalFunc.storedata("OPEN_CHAT", obj_msg.toString());
                        LocalNotification.dispatchLocalNotification(mLocContext, generalFunc.getJsonValueStr("Msg", obj_msg), false);
                        break;
                }

            } else {
                String title_msg = generalFunc.getJsonValueStr("vTitle", obj_msg);
                switch (message_str) {

                    case "TripCancelled":
                        generalFunc.saveGoOnlineInfo();
                        LocalNotification.dispatchLocalNotification(mLocContext, title_msg, false);
                        break;
                    case "DestinationAdded":
                        LocalNotification.dispatchLocalNotification(mLocContext, title_msg, false);
                        break;
                    case "CabRequested":
                        LocalNotification.dispatchLocalNotification(mLocContext, title_msg, false);
                        if (MyApp.getInstance().mainAct == null) {
                            dispatchCabRequest(generalFunc, message);
                        }

                        break;
                }
            }
        }
    }
}
