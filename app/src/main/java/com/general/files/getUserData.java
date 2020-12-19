package com.general.files;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;

import com.poupgo.driver.LauncherActivity;
import com.general.functions.GeneralFunctions;
import com.utils.CommonUtilities;
import com.general.functions.Utils;
import com.general.functions.GenerateAlertBox;

import java.util.HashMap;

/**
 * Created by Admin on 19-06-2017.
 */

public class getUserData {

    GeneralFunctions generalFunc;
    Context mContext;

    public getUserData(GeneralFunctions generalFunc, Context mContext) {
        this.generalFunc = generalFunc;
        this.mContext = mContext;
    }

    public void getData() {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "getDetail");
        parameters.put("iUserId", generalFunc.getMemberId());
        parameters.put("vDeviceType", Utils.deviceType);
        parameters.put("UserType", CommonUtilities.app_type);
        parameters.put("AppVersion", Utils.getAppVersion());

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(mContext, parameters);
        exeWebServer.setLoaderConfig(mContext, true, generalFunc);
        exeWebServer.setIsDeviceTokenGenerate(true, "vDeviceToken", generalFunc);
        exeWebServer.setDataResponseListener(responseString -> {


            if (responseString != null && !responseString.equals("")) {

                boolean isDataAvail = GeneralFunctions.checkDataAvail(CommonUtilities.action_str, responseString);

                String message = generalFunc.getJsonValue(CommonUtilities.message_str, responseString);

                if (message.equals("SESSION_OUT")) {
                    generalFunc.notifySessionTimeOut();
                    Utils.runGC();
                    return;
                }

                if (isDataAvail == true) {
                    generalFunc.storedata(CommonUtilities.USER_PROFILE_JSON, generalFunc.getJsonValue(CommonUtilities.message_str, responseString));
                    new OpenMainProfile(mContext,
                            generalFunc.getJsonValue(CommonUtilities.message_str, responseString), true, generalFunc).startProcess();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                ActivityCompat.finishAffinity((Activity) mContext);
                                Utils.runGC();
                            } catch (Exception e) {

                            }
                        }
                    }, 300);


                } else {
                    if (!generalFunc.getJsonValue("isAppUpdate", responseString).trim().equals("")
                            && generalFunc.getJsonValue("isAppUpdate", responseString).equals("true")) {

                    } else {

                        if (generalFunc.getJsonValue(CommonUtilities.message_str, responseString).equalsIgnoreCase("LBL_CONTACT_US_STATUS_NOTACTIVE_COMPANY") ||
                                generalFunc.getJsonValue(CommonUtilities.message_str, responseString).equalsIgnoreCase("LBL_ACC_DELETE_TXT") ||
                                generalFunc.getJsonValue(CommonUtilities.message_str, responseString).equalsIgnoreCase("LBL_CONTACT_US_STATUS_NOTACTIVE_DRIVER")) {

                            GenerateAlertBox alertBox = generalFunc.notifyRestartApp("", generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(CommonUtilities.message_str, responseString)));
                            alertBox.setCancelable(false);
                            alertBox.setBtnClickList(btn_id -> {

                                if (btn_id == 1) {
//                                            generalFunc.logoutFromDevice(mContext,generalFunc,"getUserData");
                                    MyApp.getInstance().removePubSub();
                                    generalFunc.logOutUser();
                                    generalFunc.restartApp(LauncherActivity.class);
                                }
                            });
                            return;
                        }

                    }

                    generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_TRY_AGAIN_TXT"), "", generalFunc.retrieveLangLBl("", "LBL_RETRY_TXT"), buttonId -> generalFunc.restartApp(LauncherActivity.class));
                }
            }else{

                generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_TRY_AGAIN_TXT"), "", generalFunc.retrieveLangLBl("", "LBL_RETRY_TXT"), buttonId -> generalFunc.restartApp(LauncherActivity.class));
            }
        });
        exeWebServer.execute();
    }
}
