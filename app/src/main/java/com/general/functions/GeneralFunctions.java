package com.general.functions;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AppOpsManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.provider.Settings.SettingNotFoundException;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AlertDialog.Builder;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.braintreepayments.api.models.BinData;
import com.poupgo.driver.ContactUsActivity;
import com.poupgo.driver.LauncherActivity;
import com.poupgo.driver.MyWalletActivity;
import com.poupgo.driver.R;
import com.poupgo.driver.VerifyInfoActivity;
import com.drawRoute.DirectionsJSONParser;
import com.facebook.appevents.AppEventsConstants;
import com.facebook.login.LoginManager;
import com.general.files.DownloadProfileImg;
import com.general.files.ExecuteWebServerUrl;
import com.general.files.FileUtils;
import com.general.files.MyApp;
import com.general.files.getUserData;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.iid.InstanceID;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.analytics.FirebaseAnalytics.Param;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.stripe.android.CustomerSession;
import com.utilities.general.files.StartActProcess;
import com.utilities.view.ErrorView;
import com.utils.CommonUtilities;
import com.view.SelectableRoundedImageView;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.lang.reflect.Field;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;
import java.util.regex.Pattern;

public class GeneralFunctions {
    public static final int MY_PERMISSIONS_REQUEST = 51;
    public static final int MY_SETTINGS_REQUEST = 52;
    AlertDialog cashBalAlertDialog;
    GenerateAlertBox generateSessionAlert;
    Map<String, Object> languageData = null;
    String languageLabels_str = "";
    Context mContext;

    public GeneralFunctions(Context context) {
        this.mContext = context;
        checkForRTL();
    }

    public double round(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        long factor = (long) Math.pow(10.0d, (double) places);
        return ((double) Math.round(value * ((double) factor))) / ((double) factor);
    }

    public String getTimezone() {
        TimeZone tz = TimeZone.getDefault();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(tz.getID());
        stringBuilder.append("");
        return stringBuilder.toString();
    }

    public String wrapHtml(Context context, String html) {
        return context.getString(isRTLmode() ? R.string.html_rtl : R.string.html, new Object[]{html});
    }

    public boolean isReferralSchemeEnable() {
        if (retrieveValue(CommonUtilities.REFERRAL_SCHEME_ENABLE).equals("") || !retrieveValue(CommonUtilities.REFERRAL_SCHEME_ENABLE).equalsIgnoreCase(BinData.YES)) {
            return false;
        }
        return true;
    }

    public boolean isRTLmode() {
        if (retrieveValue(CommonUtilities.LANGUAGE_IS_RTL_KEY).equals("") || !retrieveValue(CommonUtilities.LANGUAGE_IS_RTL_KEY).equals(CommonUtilities.DATABASE_RTL_STR)) {
            return false;
        }
        return true;
    }

    public String retrieveValue(String key) {
        return PreferenceManager.getDefaultSharedPreferences(this.mContext).getString(key, "");
    }

    public static String retrieveValue(String key, Context mContext) {
        return PreferenceManager.getDefaultSharedPreferences(mContext).getString(key, "");
    }

    public void logOUTFrmFB() {
        LoginManager.getInstance().logOut();
    }

    public static Bitmap rotateBitmap(Bitmap bitmap, int orientation) {
        Matrix matrix = new Matrix();
        switch (orientation) {
            case 1:
                return bitmap;
            case 2:
                matrix.setScale(-1.0f, 1.0f);
                break;
            case 3:
                matrix.setRotate(180.0f);
                break;
            case 4:
                matrix.setRotate(180.0f);
                matrix.postScale(-1.0f, 1.0f);
                break;
            case 5:
                matrix.setRotate(90.0f);
                matrix.postScale(-1.0f, 1.0f);
                break;
            case 6:
                matrix.setRotate(90.0f);
                break;
            case 7:
                matrix.setRotate(-90.0f);
                matrix.postScale(-1.0f, 1.0f);
                break;
            case 8:
                matrix.setRotate(-90.0f);
                break;
            default:
                return bitmap;
        }
        try {
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return bmRotated;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return bitmap;
        }
    }

    public void checkForRTL() {
        if (!(this.mContext instanceof Activity)) {
            return;
        }
        if (retrieveValue(CommonUtilities.LANGUAGE_IS_RTL_KEY).equals("") || !retrieveValue(CommonUtilities.LANGUAGE_IS_RTL_KEY).equals(CommonUtilities.DATABASE_RTL_STR)) {
            forceLTRIfSupported((Activity) this.mContext);
            return;
        }
        forceRTLIfSupported((Activity) this.mContext);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("::");
        stringBuilder.append(retrieveValue(CommonUtilities.LANGUAGE_IS_RTL_KEY));
        Utils.printLog("LangMODE", stringBuilder.toString());
        View actView = getCurrentView((Activity) this.mContext);
        if (actView.findViewById(R.id.backImgView) != null && (actView.findViewById(R.id.backImgView) instanceof ImageView)) {
            ImageView backImgView = (ImageView) actView.findViewById(R.id.backImgView);
            if (backImgView.getRotation() != 180.0f) {
                backImgView.setRotation(180.0f);
            }
        }
    }

    public static String convertDecimalPlaceDisplay(double val) {
        return String.format("%.2f", new Object[]{Double.valueOf(val)}).replace(",", FileUtils.HIDDEN_PREFIX);
    }

    public void forceRTLIfSupported(Activity act) {
        if (VERSION.SDK_INT >= 17) {
            act.getWindow().getDecorView().setLayoutDirection(1);
        }
    }

    public void forceLTRIfSupported(Activity act) {
        if (VERSION.SDK_INT >= 17) {
            act.getWindow().getDecorView().setLayoutDirection(0);
        }
    }

    public void forceRTLIfSupported(AlertDialog alertDialog) {
        if (VERSION.SDK_INT >= 17) {
            alertDialog.getWindow().getDecorView().setLayoutDirection(1);
        }
    }

    public void forceRTLIfSupported(Dialog alertDialog) {
        if (VERSION.SDK_INT >= 17) {
            alertDialog.getWindow().getDecorView().setLayoutDirection(1);
        }
    }

    public void forceLTRIfSupported(Dialog alertDialog) {
        if (VERSION.SDK_INT >= 17) {
            alertDialog.getWindow().getDecorView().setLayoutDirection(0);
        }
    }

    public JSONObject getJsonObject(String data) {
        try {
            return new JSONObject(data);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public JSONObject getJsonObject(String key, JSONObject obj) {
        try {
            return obj.getJSONObject(key);
        } catch (JSONException e) {
            return null;
        }
    }


    private String getResId(String resName) {
        String value = "";
        try {
            Field idField = R.string.class.getDeclaredField(resName);
            value = this.mContext.getString(idField.getInt(idField));

            return value;
        } catch (Exception e) {
            //e.printStackTrace();
            return resName;
        }
    }

    public String retrieveLangLBl(String orig, String label) {

        return  getResId(label);
    }

    public List<Object> toList(JSONArray array) {
        List<Object> list = new ArrayList();
        for (int i = 0; i < array.length(); i++) {
            Object value = null;
            try {
                value = array.get(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (value != null) {
                if (value instanceof JSONArray) {
                    value = toList((JSONArray) value);
                } else if (value instanceof JSONObject) {
                    value = toMap((JSONObject) value);
                }
                list.add(value);
            }
        }
        return list;
    }

    public Map<String, Object> toMap(JSONObject object) {
        Map<String, Object> map = new HashMap();
        Iterator<String> keysItr = object.keys();
        while (keysItr.hasNext()) {
            String key = (String) keysItr.next();
            Object value = getJsonValue(key, object);
            if (value instanceof JSONArray) {
                value = toList((JSONArray) value);
            } else if (value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            map.put(key, value);
        }
        return map;
    }

    public Object getJsonValue(String key, JSONObject response) {
        if (response != null) {
            try {
                Object value_str = response.get(key);
                if (!(value_str == null || value_str.equals("null") || value_str.equals(""))) {
                    return value_str;
                }
            } catch (JSONException e) {
                e.printStackTrace();
                return "";
            }
        }
        return "";
    }

    public String getJsonValueStr(String key, JSONObject response) {
        if (response != null) {
            try {
                String value_str = "";
                if (response.has(key)) {
                    value_str = response.getString(key);
                }
                if (!(value_str == null || value_str.equals("null") || value_str.equals(""))) {
                    return value_str;
                }
            } catch (JSONException e) {
                return "";
            }
        }
        return "";
    }

    public String generateDeviceToken() {
        if (!checkPlayServices()) {
            return "";
        }
        InstanceID instanceID = InstanceID.getInstance(this.mContext);
        String GCMregistrationId = "";
        try {
            GCMregistrationId = FirebaseInstanceId.getInstance().getToken(retrieveValue(CommonUtilities.APP_GCM_SENDER_ID_KEY), FirebaseMessaging.INSTANCE_ID_SCOPE);
        } catch (IOException e) {
            e.printStackTrace();
            GCMregistrationId = "";
        }
        return GCMregistrationId;
    }

    public boolean checkPlayServices() {
        final GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        final int result = googleAPI.isGooglePlayServicesAvailable(this.mContext);
        if (result == 0) {
            return true;
        }
        if (googleAPI.isUserResolvableError(result)) {
            ((Activity) this.mContext).runOnUiThread(new Runnable() {
                public void run() {
                    googleAPI.getErrorDialog((Activity) GeneralFunctions.this.mContext, result, Utils.PLAY_SERVICES_RESOLUTION_REQUEST).show();
                }
            });
        }
        return false;
    }

    public static boolean checkDataAvail(String key, String response) {
        try {
            String action_str = new JSONObject(response).getString(key);
            if (action_str.equals("") || action_str.equals(AppEventsConstants.EVENT_PARAM_VALUE_NO) || !action_str.equals("1")) {
                return false;
            }
            return true;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void removeValue(String key) {
        Editor editor = PreferenceManager.getDefaultSharedPreferences(this.mContext).edit();
        editor.remove(key);
        editor.commit();
    }

    public boolean containsKey(String key) {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(this.mContext);
        Editor editor = mPrefs.edit();
        if (mPrefs.getString(key, null) != null) {
            return true;
        }
        return false;
    }

    public void storedata(String key, String data) {
        Editor editor = PreferenceManager.getDefaultSharedPreferences(this.mContext).edit();
        editor.putString(key, data);
        editor.commit();
    }

    public static void storeData(String key, String data, MyApp mContext) {
        Editor editor = PreferenceManager.getDefaultSharedPreferences(mContext).edit();
        editor.putString(key, data);
        editor.commit();
    }

    public void storeUserData(String memberId) {
        storedata(CommonUtilities.iMemberId_KEY, memberId);
        storedata(CommonUtilities.isUserLogIn, "1");
    }

    public String addSemiColonToPrice(String value) {
        return NumberFormat.getNumberInstance(Locale.US).format((long) parseIntegerValue(0, value));
    }

    public String getMemberId() {
        if (isUserLoggedIn()) {
            return retrieveValue(CommonUtilities.iMemberId_KEY);
        }
        return "";
    }

    public void logOutUser() {
        MyApp.getInstance().removePubSub();
        removeValue(CommonUtilities.iMemberId_KEY);
        removeValue(CommonUtilities.isUserLogIn);
        removeValue(CommonUtilities.DEFAULT_CURRENCY_VALUE);
        removeValue(CommonUtilities.USER_PROFILE_JSON);
        removeValue(CommonUtilities.WORKLOCATION);
    }

    public boolean isUserLoggedIn() {
        String isUserLoggedIn_str = PreferenceManager.getDefaultSharedPreferences(this.mContext).getString(CommonUtilities.isUserLogIn, "");
        if (isUserLoggedIn_str.equals("") || !isUserLoggedIn_str.equals("1")) {
            return false;
        }
        return true;
    }

    public static boolean isJsonObj(String json) {
        try {
            JSONObject obj_check = new JSONObject(json);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static Object getTypeOfJson(String data) {
        try {
            Object json = new JSONTokener(data).nextValue();
            if (json instanceof JSONObject) {
                return new JsonObject();
            }
            if (json instanceof JSONArray) {
                return new JsonArray();
            }
            return null;
        } catch (Exception e) {
        }
        return null;
    }

    public Object getValueFromJsonArr(JSONArray arr, int position) {
        try {
            return arr.get(position);
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }

    public String getJsonValue(String key, String response) {
        if (response != null) {
            try {
                JSONObject obj_temp = new JSONObject(response);
                if (!obj_temp.isNull(key)) {
                    String value_str = obj_temp.getString(key);
                    if (!(value_str == null || value_str.equals("null") || value_str.equals(""))) {
                        return value_str;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                return "";
            }
        }
        return "";
    }

    public boolean isLanguageLabelsAvail() {
        String languageLabels_str = PreferenceManager.getDefaultSharedPreferences(this.mContext).getString(CommonUtilities.languageLabelsKey, null);
        if (languageLabels_str == null || languageLabels_str.equals("")) {
            return false;
        }
        return true;
    }

    public JSONArray getJsonArray(String key, String response) {
        try {
            return new JSONObject(response).getJSONArray(key);
        } catch (JSONException e) {
            return null;
        }
    }

    public JSONArray getJsonArray(String key, JSONObject obj) {
        try {
            return obj.getJSONArray(key);
        } catch (JSONException e) {
            return null;
        }
    }

    public JSONArray getJsonArray(String response) {
        try {
            return new JSONArray(response);
        } catch (JSONException e) {
            return null;
        }
    }

    public JSONObject getJsonObject(JSONArray arr, int position) {
        try {
            return arr.getJSONObject(position);
        } catch (JSONException e) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("::");
            stringBuilder.append(e.toString());
            Utils.printLog("getJsonObject", stringBuilder.toString());
            return null;
        }
    }

    public Object getJsonValue(JSONArray arr, int position) {
        try {
            return arr.get(position);
        } catch (JSONException e) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("::");
            stringBuilder.append(e.toString());
            Utils.printLog("getJsonObject", stringBuilder.toString());
            return null;
        }
    }

    public boolean isJSONkeyAvail(String key, String response) {
        try {
            JSONObject json_obj = new JSONObject(response);
            if (!json_obj.has(key) || json_obj.isNull(key)) {
                return false;
            }
            return true;
        } catch (JSONException e) {
            return false;
        }
    }

    public JSONObject getJsonObject(String key, String response) {
        try {
            JSONObject value_str = new JSONObject(response).getJSONObject(key);
            if (value_str == null || value_str.equals("null") || value_str.equals("")) {
                return null;
            }
            return value_str;
        } catch (JSONException e) {
            return null;
        }
    }

    public boolean isJSONArrKeyAvail(String key, String response) {
        try {
            if (new JSONObject(response).optJSONArray(key) != null) {
                return true;
            }
            return false;
        } catch (JSONException e) {
            return false;
        }
    }

    public static Float parseFloatValue(float defaultValue, String strValue) {
        try {
            return Float.valueOf(Float.parseFloat(strValue));
        } catch (Exception e) {
            return Float.valueOf(defaultValue);
        }
    }

    public static Double parseDoubleValue(double defaultValue, String strValue) {
        try {
            return Double.valueOf(Double.parseDouble(strValue.replace(",", "")));
        } catch (Exception e) {
            return Double.valueOf(defaultValue);
        }
    }

    public static int parseIntegerValue(int defaultValue, String strValue) {
        try {
            return Integer.parseInt(strValue);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static long parseLongValue(long defaultValue, String strValue) {
        try {
            return Long.parseLong(strValue);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public void sendHeartBeat() {
        this.mContext.sendBroadcast(new Intent("com.google.android.intent.action.GTALK_HEARTBEAT"));
        this.mContext.sendBroadcast(new Intent("com.google.android.intent.action.MCS_HEARTBEAT"));
    }

    public boolean isEmailValid(String email) {
        if (Pattern.compile("[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,20}", 2).matcher(email.trim()).matches()) {
            return true;
        }
        return false;
    }

    public void generateErrorView(ErrorView errorView, String title, String subTitle) {
        errorView.setConfig(ErrorView.Config.create().title("").titleColor(this.mContext.getResources().getColor(17170444)).subtitle(retrieveLangLBl("", subTitle)).retryText(retrieveLangLBl("Retry", "LBL_RETRY_TXT")).retryTextColor(this.mContext.getResources().getColor(R.color.error_view_retry_btn_txt_color)).build());
    }

    public void showError() {
        String lable;
        String str;
        InternetConnection intCheck = new InternetConnection(this.mContext);
        if (intCheck.isNetworkConnected() || intCheck.check_int()) {
            lable = "Please try again.";
            str = "LBL_TRY_AGAIN_TXT";
        } else {
            lable = "No Internet Connection";
            str = "LBL_NO_INTERNET_TXT";
        }
        lable = retrieveLangLBl(lable, str);
        GenerateAlertBox generateAlert = new GenerateAlertBox(this.mContext);
        generateAlert.setContentMessage("", lable);
        generateAlert.setPositiveBtn(retrieveLangLBl("Ok", "LBL_BTN_OK_TXT"));
        generateAlert.showAlertBox();
    }

    public Typeface getDefaultFont(Context context) {
        return Typeface.createFromAsset(context.getAssets(), "fonts/roboto_light.ttf");
    }

    public void showGeneralMessage(String title, String message) {
        if (message != null) {
            try {
                if (message.equals("SESSION_OUT")) {
                    notifySessionTimeOut();
                    Utils.runGC();
                    return;
                }
            } catch (Exception e) {
            }
        }
        GenerateAlertBox generateAlert = new GenerateAlertBox(this.mContext);
        generateAlert.setContentMessage(title, message);
        generateAlert.setPositiveBtn(retrieveLangLBl("Ok", "LBL_BTN_OK_TXT"));
        generateAlert.showAlertBox();
    }

    public void showGeneralMessage(String title, String message, String positiveButton, String negativeButton, GeneralFunctions$OnAlertButtonClickListener onAlertButtonClickListener) {
        if (message != null) {
            try {
                if (message.equals("SESSION_OUT")) {
                    notifySessionTimeOut();
                    Utils.runGC();
                    return;
                }
            } catch (Exception e) {
            }
        }
        GenerateAlertBox generateAlert = new GenerateAlertBox(this.mContext);
        generateAlert.setContentMessage(title, message);
        generateAlert.setNegativeBtn(negativeButton);
        generateAlert.setPositiveBtn(positiveButton);
        generateAlert.setBtnClickList(new GeneralFunctions$$Lambda$0(generateAlert, onAlertButtonClickListener));
        generateAlert.showAlertBox();
    }

    static final /* synthetic */ void lambda$showGeneralMessage$0$GeneralFunctions(GenerateAlertBox generateAlert, GeneralFunctions$OnAlertButtonClickListener onAlertButtonClickListener, int btn_id) {
        generateAlert.closeAlertBox();
        if (onAlertButtonClickListener != null) {
            onAlertButtonClickListener.onAlertButtonClick(btn_id);
        }
    }

    public void buildLowBalanceMessage(Context context, String message, Bundle bn, int layout, int id_addNowTxtArea, int id_skipTxtArea, int id_titileTxt) {
        Builder var10 = new Builder(context);
        LayoutInflater var11 = (LayoutInflater)context.getSystemService("layout_inflater");
        View var12 = var11.inflate(layout, (ViewGroup)null);
        var10.setView(var12);
        com.view.MTextView var13 = var12.findViewById(id_addNowTxtArea);
        com.view.MTextView var14 = var12.findViewById(com.general.files.R.id.msgTxt);
        com.view.MTextView var15 = var12.findViewById(id_skipTxtArea);
        com.view.MTextView var16 = var12.findViewById(id_titileTxt);
        var16.setText(this.retrieveLangLBl("", "LBL_LOW_BALANCE"));
        if (this.getJsonValue("APP_PAYMENT_MODE", bn.getString("UserProfileJson")).equalsIgnoreCase("Cash")) {
            var13.setText(this.retrieveLangLBl("", "LBL_CONTACT_US_TXT"));
        } else {
            var13.setText(this.retrieveLangLBl("", "LBL_ADD_NOW"));
        }

        var15.setText(this.retrieveLangLBl("", "LBL_BTN_OK_TXT"));
        var14.setText(message);
        var15.setOnClickListener((var1) -> {
            this.cashBalAlertDialog.dismiss();
        });
        var13.setOnClickListener((var5) -> {
            this.cashBalAlertDialog.dismiss();
            if (this.getJsonValue("APP_PAYMENT_MODE", bn.getString("UserProfileJson")).equalsIgnoreCase("Cash")) {
                (new StartActProcess(context)).startAct(ContactUsActivity.class);
            } else {
                (new StartActProcess(context)).startActWithData(MyWalletActivity.class, bn);
            }

        });
        this.cashBalAlertDialog = var10.create();
        this.cashBalAlertDialog.setCancelable(false);
        if (this.isRTLmode()) {
            this.forceRTLIfSupported(this.cashBalAlertDialog);
        }

        this.cashBalAlertDialog.show();
    }

    final /* synthetic */ void lambda$buildLowBalanceMessage$1$GeneralFunctions(View view) {
        this.cashBalAlertDialog.dismiss();
    }

    final /* synthetic */ void lambda$buildLowBalanceMessage$2$GeneralFunctions(Bundle bn, Context context, View view) {
        this.cashBalAlertDialog.dismiss();
        if (getJsonValue("APP_PAYMENT_MODE", bn.getString("UserProfileJson")).equalsIgnoreCase("Cash")) {
            new StartActProcess(context).startAct(ContactUsActivity.class);
        } else {
            new StartActProcess(context).startActWithData(MyWalletActivity.class, bn);
        }
    }

    public static DecimalFormat decimalFormat() {
        DecimalFormat df = new DecimalFormat("#.00");
        DecimalFormatSymbols sym = DecimalFormatSymbols.getInstance();
        sym.setDecimalSeparator(ClassUtils.PACKAGE_SEPARATOR_CHAR);
        df.setDecimalFormatSymbols(sym);
        return df;
    }

    public boolean isLocationEnabled() {
        int locationMode = 0;
        if (VERSION.SDK_INT < 19) {
            return true ^ TextUtils.isEmpty(Secure.getString(this.mContext.getContentResolver(), "location_providers_allowed"));
        }
        try {
            locationMode = Secure.getInt(this.mContext.getContentResolver(), "location_mode");
        } catch (SettingNotFoundException e) {
            e.printStackTrace();
        }
        boolean statusOfGPS = ((LocationManager) this.mContext.getSystemService(Param.LOCATION)).isProviderEnabled("gps");
        if (locationMode == 0 || !statusOfGPS) {
            return false;
        }
        return true;
    }

    public boolean checkLocationPermission(boolean isPermissionDialogShown) {
        int permissionCheck_fine = ContextCompat.checkSelfPermission(this.mContext, "android.permission.ACCESS_FINE_LOCATION");
        int permissionCheck_coarse = ContextCompat.checkSelfPermission(this.mContext, "android.permission.ACCESS_COARSE_LOCATION");
        if (permissionCheck_fine == 0) {
            if (permissionCheck_coarse == 0) {
                return true;
            }
        }
        if (!isPermissionDialogShown) {
            ActivityCompat.requestPermissions((Activity) this.mContext, new String[]{"android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION"}, 51);
        }
        return false;
    }

    public boolean isStoragePermissionGranted() {
        if (VERSION.SDK_INT < 23 || ContextCompat.checkSelfPermission(this.mContext, "android.permission.WRITE_EXTERNAL_STORAGE") == 0) {
            return true;
        }
        if (this.mContext instanceof Activity) {
            ActivityCompat.requestPermissions((Activity) this.mContext, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 51);
        }
        return false;
    }

    public boolean isCameraPermissionGranted() {
        if (VERSION.SDK_INT < 23 || ContextCompat.checkSelfPermission(this.mContext, "android.permission.CAMERA") == 0) {
            return true;
        }
        if (this.mContext instanceof Activity) {
            ActivityCompat.requestPermissions((Activity) this.mContext, new String[]{"android.permission.CAMERA"}, 51);
        }
        return false;
    }

    public String[] generateImageParams(String key, String content) {
        return new String[]{key, content};
    }

    public String getApp_Type() {
        return retrieveValue(CommonUtilities.APP_TYPE);
    }

    public boolean isAllPermissionGranted(boolean openDialog) {
        int permissionCheck_fine = ContextCompat.checkSelfPermission(this.mContext, "android.permission.ACCESS_FINE_LOCATION");
        int permissionCheck_coarse = ContextCompat.checkSelfPermission(this.mContext, "android.permission.ACCESS_COARSE_LOCATION");
        int permissionCheck_storage = ContextCompat.checkSelfPermission(this.mContext, "android.permission.WRITE_EXTERNAL_STORAGE");
        int permissionCheck_camera = ContextCompat.checkSelfPermission(this.mContext, "android.permission.CAMERA");
        if (permissionCheck_fine == 0) {
            if (permissionCheck_coarse == 0) {
                return true;
            }
        }
        if (openDialog) {
            ActivityCompat.requestPermissions((Activity) this.mContext, new String[]{"android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION"}, 51);
        }
        return false;
    }

    public boolean isPermisionGranted() {
        int permissionCheck_storage = ContextCompat.checkSelfPermission(this.mContext, "android.permission.WRITE_EXTERNAL_STORAGE");
        int permissionCheck_camera = ContextCompat.checkSelfPermission(this.mContext, "android.permission.CAMERA");
        if (permissionCheck_storage == 0 && permissionCheck_camera == 0) {
            return true;
        }
        return false;
    }

    public boolean isCameraStoragePermissionGranted() {
        int permissionCheck_storage = ContextCompat.checkSelfPermission(this.mContext, "android.permission.WRITE_EXTERNAL_STORAGE");
        int permissionCheck_camera = ContextCompat.checkSelfPermission(this.mContext, "android.permission.CAMERA");
        if (permissionCheck_storage == 0) {
            if (permissionCheck_camera == 0) {
                return true;
            }
        }
        ActivityCompat.requestPermissions((Activity) this.mContext, new String[]{"android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE"}, 51);
        return false;
    }

    public void openSettings() {
        if (this.mContext instanceof Activity) {
            Utils.hideKeyboard((Activity) this.mContext);
            Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.fromParts("package", "com.homodriver.driver", null));
            ((Activity) this.mContext).startActivityForResult(intent, 52);
        }
    }

    public void notifySessionTimeOut() {
        this.generateSessionAlert = new GenerateAlertBox(this.mContext);
        this.generateSessionAlert.setContentMessage(retrieveLangLBl("", "LBL_BTN_TRIP_CANCEL_CONFIRM_TXT"), retrieveLangLBl("Your session is expired. Please login again.", "LBL_SESSION_TIME_OUT"));
        this.generateSessionAlert.setPositiveBtn(retrieveLangLBl("Ok", "LBL_BTN_OK_TXT"));
        this.generateSessionAlert.setCancelable(false);
        this.generateSessionAlert.setBtnClickList(new GeneralFunctions$$Lambda$3(this));
        this.generateSessionAlert.showSessionOutAlertBox();
    }

    final /* synthetic */ void lambda$notifySessionTimeOut$3$GeneralFunctions(int btn_id) {
        if (btn_id == 1) {
            logOutUser();
            restartApp();
        }
    }

    public GenerateAlertBox notifyRestartApp() {
        GenerateAlertBox generateAlert = new GenerateAlertBox(this.mContext);
        generateAlert.setContentMessage(retrieveLangLBl("", "LBL_BTN_TRIP_CANCEL_CONFIRM_TXT"), retrieveLangLBl("In order to apply changes restarting app is required. Please wait.", "LBL_NOTIFY_RESTART_APP_TO_CHANGE"));
        generateAlert.setPositiveBtn(retrieveLangLBl("Ok", "LBL_BTN_OK_TXT"));
        generateAlert.showAlertBox();
        return generateAlert;
    }

    public GenerateAlertBox notifyRestartApp(String title, String contentMsg) {
        GenerateAlertBox generateAlert = new GenerateAlertBox(this.mContext);
        generateAlert.setContentMessage(title, contentMsg);
        generateAlert.setPositiveBtn(retrieveLangLBl("Ok", "LBL_BTN_OK_TXT"));
        generateAlert.showAlertBox();
        return generateAlert;
    }

    public void getHasKey(Context act) {
        try {
            for (Signature signature : act.getPackageManager().getPackageInfo(act.getPackageName(), 64).signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Utils.printLog("hash key", new String(Base64.encode(md.digest(), 0)));
            }
        } catch (NameNotFoundException e1) {
            Utils.printLog("name not found", e1.toString());
        } catch (NoSuchAlgorithmException e) {
            Utils.printLog("no such an algorithm", e.toString());
        } catch (Exception e2) {
            Utils.printLog(CustomerSession.EXTRA_EXCEPTION, e2.toString());
        }
    }

    public void restartwithGetDataApp() {
        new getUserData(this, this.mContext).getData();
    }

    public void restartApp(Class<LauncherActivity> activityClass) {
        Utils.printLog("restartApp", ":: called");
        new StartActProcess(this.mContext).startAct(activityClass);
        ((Activity) this.mContext).setResult(0);
        try {
            ActivityCompat.finishAffinity((Activity) this.mContext);
        } catch (Exception e) {
        }
        Utils.runGC();
    }

    public void restartApp() {
        restartApp(LauncherActivity.class);
    }

    public void freeMemory() {
        Utils.runGC();
    }

    public String getDateFormatedType(String date, String originalformate, String targateformate) {
        String convertdate = "";
        SimpleDateFormat original_formate = new SimpleDateFormat(originalformate);
        try {
            convertdate = new SimpleDateFormat(targateformate).format(original_formate.parse(date));
            Utils.printLog("ConvertDate:", convertdate);
            return convertdate;
        } catch (ParseException e) {
            e.printStackTrace();
            return convertdate;
        }
    }

    public View getCurrentView(Activity act) {
        return act.getWindow().getDecorView().findViewById(android.R.id.content);
    }

    public void showMessage(View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
    }

    public double CalculationByLocation(double lat1, double lon1, double lat2, double lon2) {
        double lat1_s = lat1;
        double lat2_d = lat2;
        double lon1_s = lon1;
        double lon2_d = lon2;
        double dLat = Math.toRadians(lat2_d - lat1_s);
        double dLon = Math.toRadians(lon2_d - lon1_s);
        lon1_s = (Math.sin(dLat / 2.0d) * Math.sin(dLat / 2.0d)) + (((Math.cos(Math.toRadians(lat1_s)) * Math.cos(Math.toRadians(lat2_d))) * Math.sin(dLon / 2.0d)) * Math.sin(dLon / 2.0d));
        lat1_s = Math.asin(Math.sqrt(lon1_s)) * 2.0d;
        lon2_d = ((double) 6371) * lat1_s;
        double km = lon2_d / 1.0d;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km)).intValue();
        double meter = lon2_d % 1000.0d;
        int meterInDec = Integer.valueOf(newFormat.format(meter)).intValue();
        return ((double) 6371) * lat1_s;
    }

    public String getSelectedCarTypeData(String selectedCarTypeId, String jsonArrKey, String dataKey, String json) {
        JSONArray arr = getJsonArray(jsonArrKey, json);
        for (int i = 0; i < arr.length(); i++) {
            JSONObject tempObj = getJsonObject(arr, i);
            if (getJsonValue("iVehicleTypeId", tempObj.toString()).equals(selectedCarTypeId)) {
                return getJsonValue(dataKey, tempObj.toString());
            }
        }
        return "";
    }

    public PolylineOptions getGoogleRouteOptions(String directionJson, int width, int color) {
        Exception e;
        int i;
        PolylineOptions lineOptions = new PolylineOptions();
        int j;
        try {
            try {
                List<List<HashMap<String, String>>> routes_list = new DirectionsJSONParser().parse(new JSONObject(directionJson));
                ArrayList<LatLng> points = new ArrayList();
                if (routes_list.size() > 0) {
                    j = 0;
                    List<HashMap<String, String>> path = (List) routes_list.get(0);
                    while (j < path.size()) {
                        HashMap<String, String> point = (HashMap) path.get(j);
                        points.add(new LatLng(Double.parseDouble((String) point.get("lat")), Double.parseDouble((String) point.get("lng"))));
                        j++;
                    }
                    lineOptions.addAll(points);
                    try {
                        lineOptions.width((float) width);
                    } catch (Exception e2) {
                        e = e2;
                        i = color;
                        return null;
                    }
                    try {
                        lineOptions.color(color);
                        return lineOptions;
                    } catch (Exception e3) {
                        e = e3;
                        return null;
                    }
                }
                j = width;
                i = color;
                return null;
            } catch (Exception e4) {
                e = e4;
                j = width;
                i = color;
                return null;
            }
        } catch (Exception e5) {
            e = e5;
            String str = directionJson;
            j = width;
            i = color;
            return null;
        }
    }

    public void checkProfileImage(SelectableRoundedImageView userProfileImgView, String userProfileJson, String imageKey) {
        String vImgName_str = getJsonValue(imageKey, userProfileJson);
        if (!(vImgName_str == null || vImgName_str.equals(""))) {
            if (!vImgName_str.equals("NONE")) {
                Context context = this.mContext;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("http://webprojectsdemo.com/projects/homodriver/webimages/upload/Driver/");
                stringBuilder.append(getMemberId());
                stringBuilder.append("/");
                stringBuilder.append(vImgName_str);
                new DownloadProfileImg(context, userProfileImgView, stringBuilder.toString(), vImgName_str).execute(new String[0]);
                return;
            }
        }
        userProfileImgView.setImageResource(R.mipmap.ic_no_pic_user);
    }

    public void checkProfileImage(SelectableRoundedImageView userProfileImgView, String userProfileJson, String imageKey, ImageView profilebackimage) {
        String vImgName_str = getJsonValue(imageKey, userProfileJson);
        if (!(vImgName_str == null || vImgName_str.equals(""))) {
            if (!vImgName_str.equals("NONE")) {
                Context context = this.mContext;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("http://webprojectsdemo.com/projects/homodriver/webimages/upload/Driver/");
                stringBuilder.append(getMemberId());
                stringBuilder.append("/");
                stringBuilder.append(vImgName_str);
                new DownloadProfileImg(context, userProfileImgView, stringBuilder.toString(), vImgName_str, profilebackimage).execute(new String[0]);
                return;
            }
        }
        userProfileImgView.setImageResource(R.mipmap.ic_no_pic_user);
    }

    public void verifyMobile(Bundle bn, Fragment fragment) {
        GenerateAlertBox generateAlert = new GenerateAlertBox(this.mContext);
        generateAlert.setCancelable(false);
        generateAlert.setBtnClickList(new GeneralFunctions$$Lambda$4(this, generateAlert, fragment, bn));
        generateAlert.setContentMessage("", retrieveLangLBl("", "LBL_VERIFY_MOBILE_CONFIRM_MSG"));
        generateAlert.setPositiveBtn(retrieveLangLBl("", "LBL_BTN_OK_TXT"));
        generateAlert.setNegativeBtn(retrieveLangLBl("", "LBL_CANCEL_TXT"));
        generateAlert.showAlertBox();
    }

    final /* synthetic */ void lambda$verifyMobile$4$GeneralFunctions(GenerateAlertBox generateAlert, Fragment fragment, Bundle bn, int btn_id) {
        generateAlert.closeAlertBox();
        if (btn_id != 0) {
            if (fragment == null) {
                new StartActProcess(this.mContext).startActForResult(VerifyInfoActivity.class, bn, 128);
            } else {
                new StartActProcess(this.mContext).startActForResult(fragment, VerifyInfoActivity.class, 128, bn);
            }
        }
    }

    public java.lang.String decodeFile(java.lang.String r11, int r12, int r13, java.lang.String r14) {
        return "";
    }

    public boolean isServiceRunning(Class<?> serviceClass) {
        for (RunningServiceInfo service : ((ActivityManager) this.mContext.getSystemService("activity")).getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void saveGoOnlineInfo() {
        storedata(CommonUtilities.GO_ONLINE_KEY, BinData.YES);
        String str = CommonUtilities.LAST_FINISH_TRIP_TIME_KEY;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("");
        stringBuilder.append(Calendar.getInstance().getTimeInMillis());
        storedata(str, stringBuilder.toString());
    }

    public String getLocationUpdateChannel() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(Utils.pubNub_Update_Loc_Channel_Prefix);
        stringBuilder.append(getMemberId());
        return stringBuilder.toString();
    }

    public String buildLocationJson(Location location) {
        if (location == null) {
            return "";
        }
        try {
            Utils.printLog("buildLocationJson", ":: called");
            JSONObject obj = new JSONObject();
            obj.put("MsgType", "LocationUpdate");
            obj.put("iDriverId", getMemberId());
            obj.put("vLatitude", location.getLatitude());
            obj.put("vLongitude", location.getLongitude());
            obj.put("ChannelName", getLocationUpdateChannel());
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(System.currentTimeMillis());
            stringBuilder.append("");
            obj.put("LocTime", stringBuilder.toString());
            return obj.toString();
        } catch (Exception e) {
            return "";
        }
    }

    public String buildLocationJson(Location location, String msgType) {
        if (location == null) {
            return "";
        }
        try {
            JSONObject obj = new JSONObject();
            obj.put("MsgType", msgType);
            obj.put("iDriverId", getMemberId());
            obj.put("vLatitude", location.getLatitude());
            obj.put("vLongitude", location.getLongitude());
            obj.put("ChannelName", getLocationUpdateChannel());
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(System.currentTimeMillis());
            stringBuilder.append("");
            obj.put("LocTime", stringBuilder.toString());
            return obj.toString();
        } catch (Exception e) {
            return "";
        }
    }

    public String buildRequestCancelJson(String iUserId, String vMsgCode) {
        try {
            JSONObject obj = new JSONObject();
            obj.put("MsgType", "TripRequestCancel");
            obj.put("Message", "TripRequestCancel");
            obj.put("iDriverId", getMemberId());
            obj.put("iUserId", iUserId);
            obj.put("iTripId", vMsgCode);
            return obj.toString();
        } catch (Exception e) {
            return "";
        }
    }

    public String convertNumberWithRTL(String data) {
        String result = "";
        try {
            NumberFormat nf = NumberFormat.getInstance(new Locale(retrieveValue(CommonUtilities.LANGUAGE_CODE_KEY)));
            if (!(data == null || data.equals(""))) {
                for (int i = 0; i < data.length(); i++) {
                    char c = data.charAt(i);
                    StringBuilder stringBuilder;
                    if (Character.isDigit(c)) {
                        stringBuilder = new StringBuilder();
                        stringBuilder.append(result);
                        stringBuilder.append(nf.format((long) Integer.parseInt(String.valueOf(c))));
                        result = stringBuilder.toString();
                        Utils.printLog("result", result);
                    } else {
                        stringBuilder = new StringBuilder();
                        stringBuilder.append(result);
                        stringBuilder.append(c);
                        result = stringBuilder.toString();
                    }
                }
            }
            Utils.printLog("result", result);
            return result;
        } catch (Exception e) {
            Utils.printLog("Exception umber ", e.toString());
            return result;
        }
    }

    public void deleteTripStatusMessages() {
        for (Entry<String, ?> entry : PreferenceManager.getDefaultSharedPreferences(this.mContext).getAll().entrySet()) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append((String) entry.getKey());
            stringBuilder.append(": ");
            stringBuilder.append(entry.getValue().toString());
            Utils.printLog("map values", stringBuilder.toString());
            if (((String) entry.getKey()).contains(CommonUtilities.TRIP_REQ_CODE_PREFIX_KEY)) {
                Long CURRENTmILLI = Long.valueOf(System.currentTimeMillis() - DateUtils.MILLIS_PER_DAY);
                if (CURRENTmILLI.longValue() >= parseLongValue(0, entry.getValue().toString())) {
                    removeValue((String) entry.getKey());
                }
            }
        }
    }


    public boolean canDrawOverlayViews(Context con) {
        if (VERSION.SDK_INT >= 21 && VERSION.SDK_INT != 26) {
            if (VERSION.SDK_INT != 27) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("::");
                stringBuilder.append(VERSION.SDK_INT);
                Utils.printLog("SDK_VERSION", stringBuilder.toString());
                try {
                    return Settings.canDrawOverlays(con);
                } catch (NoSuchMethodError e) {
                    Utils.printLog("DrawOverlayException", e.toString());
                    return canDrawOverlaysUsingReflection(con);
                }
            }
        }
        return true;
    }

    public static boolean canDrawOverlaysUsingReflection(Context context) {
        boolean z = false;
        try {
            AppOpsManager manager = (AppOpsManager) context.getSystemService("appops");
            if (((Integer) AppOpsManager.class.getMethod("checkOp", new Class[]{Integer.TYPE, Integer.TYPE, String.class}).invoke(manager, new Object[]{Integer.valueOf(24), Integer.valueOf(Binder.getCallingUid()), context.getApplicationContext().getPackageName()})).intValue() == 0) {
                z = true;
            }
            return z;
        } catch (Exception e) {
            return false;
        }
    }

    public JSONObject getJsonObjectFromString(String json) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(json);
            Utils.printLog("Api", jsonObject.toString());
            return jsonObject;
        } catch (Throwable th) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Could not parse malformed JSON: \"");
            stringBuilder.append(json);
            stringBuilder.append("\"");
            Utils.printLog("Api", stringBuilder.toString());
            return jsonObject;
        }
    }

    public void logoutFromDevice(Context context, GeneralFunctions generalFunc, String from) {
        HashMap<String, String> parameters = new HashMap();
        parameters.put("type", "callOnLogout");
        parameters.put("iMemberId", getMemberId());
        parameters.put("UserType", Utils.userType);
        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(context, parameters);
        exeWebServer.setLoaderConfig(context, true, generalFunc);
        exeWebServer.setDataResponseListener(new GeneralFunctions$$Lambda$5(this));
        exeWebServer.execute();
    }

    final /* synthetic */ void lambda$logoutFromDevice$5$GeneralFunctions(String responseString) {
        if (responseString == null || responseString.equals("")) {
            showError();
            return;
        }
        if (checkDataAvail(CommonUtilities.action_str, responseString)) {
            logOutUser();
            restartApp();
        } else {
            showGeneralMessage("", retrieveLangLBl("", getJsonValue(CommonUtilities.message_str, responseString)));
        }
    }

    public String formatUpto2Digit(float discount) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("");
        stringBuilder.append(((double) Math.round(((double) discount) * 100.0d)) / 100.0d);
        return stringBuilder.toString();
    }

    public String formatUpto2Digit(double discount) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("");
        stringBuilder.append(((double) Math.round(discount * 100.0d)) / 100.0d);
        return stringBuilder.toString();
    }

    public Bundle createChatBundle(JSONObject obj_msg) {
        Bundle bn = new Bundle();
        bn.putString("iFromMemberId", getJsonValueStr("iFromMemberId", obj_msg));
        bn.putString("FromMemberImageName", getJsonValueStr("FromMemberImageName", obj_msg));
        bn.putString("iTripId", getJsonValueStr("iTripId", obj_msg));
        bn.putString("FromMemberName", getJsonValueStr("FromMemberName", obj_msg));
        bn.putString("vBookingNo", getJsonValueStr("vBookingNo", obj_msg));
        return bn;
    }
}
