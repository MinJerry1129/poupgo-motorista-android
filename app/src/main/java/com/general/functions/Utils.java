package com.general.functions;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RecentTaskInfo;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.Build.VERSION;
import android.support.media.ExifInterface;
import android.support.v4.view.ViewCompat;
import android.telephony.TelephonyManager;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.poupgo.driver.BuildConfig;
import com.general.files.FileUtils;
import com.general.files.MyApp;
import com.google.android.gms.maps.model.LatLng;
import com.utils.CommonUtilities;
import com.view.editBox.MaterialEditText;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.lang3.StringUtils;

public class Utils {
    public static final int SELECT_CITY_REQ_CODE = 127;
    public static final int SELECT_STATE_REQ_CODE = 128;
    public static final int ADD_VEHICLE_REQ_CODE = 131;
    public static final int CARD_PAYMENT_REQ_CODE = 130;
    public static final String CabFaretypeFixed = "Fixed";
    public static final String CabFaretypeHourly = "Hourly";
    public static final String CabFaretypeRegular = "Regular";
    public static final String CabGeneralTypeRide_Delivery = "Ride-Delivery";
    public static final String CabGeneralTypeRide_Delivery_UberX = "Ride-Delivery-UberX";
    public static final String CabGeneralType_Deliver = "Deliver";
    public static final String CabGeneralType_Ride = "Ride";
    public static final String CabGeneralType_UberX = "UberX";
    public static String DEVICE_SESSION_ID_KEY = "DEVICE_SESSION_ID";
    public static String DateFormatewithTime = getDetailDateFormat();
    public static String DefaultDatefromate = "yyyy/MM/dd";
    public static final String ENABLE_SOCKET_CLUSTER_KEY = "ENABLE_SOCKET_CLUSTER";
    public static String FETCH_TRIP_STATUS_TIME_INTERVAL_KEY = "FETCH_TRIP_STATUS_TIME_INTERVAL";
    public static final int ImageUpload_DESIREDHEIGHT = 1024;
    public static final int ImageUpload_DESIREDWIDTH = 1024;
    public static final int ImageUpload_MINIMUM_HEIGHT = 256;
    public static final int ImageUpload_MINIMUM_WIDTH = 256;
    public static final int LOCATION_POST_MIN_DISTANCE_IN_MITERS = 5;
    public static final int LOCATION_UPDATE_MIN_DISTANCE_IN_MITERS = 2;
    public static final int MENU_ABOUT_US = 4;
    public static final int MENU_ACCOUNT_VERIFY = 19;
    public static final int MENU_BANK_DETAIL = 21;
    public static final int MENU_BOOKINGS = 2;
    public static final int MENU_CONTACT_US = 5;
    public static final int MENU_EMERGENCY_CONTACT = 18;
    public static final int MENU_FEEDBACK = 3;
    public static final int MENU_HELP = 6;
    public static final int MENU_INVITE_FRIEND = 8;
    public static final int MENU_MANAGE_VEHICLES = 16;
    public static final int MENU_MY_HEATVIEW = 11;
    public static final int MENU_PAYMENT = 10;
    public static final int MENU_POLICY = 12;
    public static final int MENU_PROFILE = 0;
    public static final int MENU_RIDE_HISTORY = 1;
    public static final int MENU_SET_AVAILABILITY = 22;
    public static final int MENU_SIGN_OUT = 7;
    public static final int MENU_SUPPORT = 13;
    public static final int MENU_TRIP_STATISTICS = 17;
    public static final int MENU_WALLET = 9;
    public static final int MENU_WAY_BILL = 20;
    public static final int MENU_YOUR_DOCUMENTS = 15;
    public static final int MENU_YOUR_TRIPS = 14;
    public static final int MY_PROFILE_REQ_CODE = 127;
    public static final int NOTIFICATION_BACKGROUND_ID = 12;
    public static final int NOTIFICATION_ID = 11;
    public static final int OVERLAY_PERMISSION_REQ_CODE = 2542;
    public static String OriginalDateFormate = "yyyy-MM-dd HH:mm:ss";
    public static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    public static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    public static final String PUBNUB_DISABLED_KEY = "PUBNUB_DISABLED";
    public static String PUBSUB_PUBLISH_DRIVER_LOC_DISTANCE_LIMIT = "PUBSUB_PUBLISH_DRIVER_LOC_DISTANCE_LIMIT";
    public static final String Past = "getRideHistory";
    public static final int REQUEST_CODE_GPS_ON = 2425;
    public static final int REQUEST_CODE_NETWOEK_ON = 2430;
    public static final int REQ_OMISE_CODE = 102;
    public static final int REQ_VERIFY_CARD_PIN_CODE = 135;
    public static final String SC_CONNECT_URL_KEY = "SC_CONNECT_URL";
    public static final int SEARCH_DEST_LOC_REQ_CODE = 126;
    public static final int SEARCH_PICKUP_LOC_REQ_CODE = 125;
    public static final int SELECT_COUNTRY_REQ_CODE = 124;
    public static String SESSION_ID_KEY = "APP_SESSION_ID";
    public static String SMS_BODY_KEY = "SMS_BODY";
    public static final String TempImageFolderPath = "TempImages";
    public static final String TempProfileImageName = "temp_pic_img.png";
    public static final int UPLOAD_DOC_REQ_CODE = 132;
    public static final String Upcoming = "checkBookings";
    public static String VERIFICATION_CODE_RESEND_COUNT_KEY = "VERIFICATION_CODE_RESEND_COUNT";
    public static String VERIFICATION_CODE_RESEND_COUNT_RESTRICTION_KEY = "VERIFICATION_CODE_RESEND_COUNT_RESTRICTION";
    public static String VERIFICATION_CODE_RESEND_TIME_IN_SECONDS_KEY = "VERIFICATION_CODE_RESEND_TIME_IN_SECONDS";
    public static final int VERIFY_INFO_REQ_CODE = 129;
    public static final int VERIFY_MOBILE_REQ_CODE = 128;
    public static String WalletApiFormate = "dd-MMM-yyyy";
    public static final String Wallet_all = "All";
    public static final String Wallet_credit = "CREDIT";
    public static final String Wallet_debit = "DEBIT";
    public static String dateFormateInHeaderBar = "EEE, MMM d, yyyy";
    public static String dateFormateInList = "dd-MMM-yyyy";
    public static String dateFormateTimeOnly = "h:mm a";
    public static final float defaultZomLevel = 16.5f;
    public static final String deviceType = "Android";
    public static final int minPasswordLength = 6;
    public static final String pubNubStatus_Connected = "Connected";
    public static final String pubNubStatus_Denied = "DeniedConnection";
    public static final String pubNubStatus_DisConnected = "DisConnected";
    public static final String pubNubStatus_Error_Connection = "ErrorInConnection";
    public static final String pubNub_Update_Loc_Channel_Prefix = "ONLINE_DRIVER_LOC_";
    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);
    public static String storedImageFolderName = "/DriverApp/ProfileImage";
    public static LatLng tempLatlong = null;
    public static final String userType = "Driver";

    public static int dpToPx(float dp, Context context) {
        return dpToPx(dp, context.getResources());
    }

    public static int dpToPx(float dp, Resources resources) {
        return (int) TypedValue.applyDimension(1, dp, resources.getDisplayMetrics());
    }

    public static void printELog(String title, String content) {
        if (BuildConfig.DEBUG) {
            Log.e(title, content);
        }
    }

    public static void printLog(String title, String content) {
        if (BuildConfig.DEBUG) {
            Log.d(title, content);
        }
    }

    public static String getDetailDateFormat() {
        Activity act = MyApp.getInstance().getCurrentAct();
        if (act == null) {
            return "EEE, MMM dd, yyyy 'at' hh:mm aaa";
        }
        String at_str = new GeneralFunctions(act).retrieveLangLBl("at", "LBL_AT_TXT");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("EEE, MMM dd, yyyy '");
        stringBuilder.append(at_str);
        stringBuilder.append("' hh:mm aaa");
        return stringBuilder.toString();
    }

    public static int dipToPixels(Context context, float dipValue) {
        return (int) TypedValue.applyDimension(1, dipValue, context.getResources().getDisplayMetrics());
    }

    public static int getSDKInt() {
        return VERSION.SDK_INT;
    }

    public static int getExifRotation(String path) {
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
    }

    public static boolean isValidImageResolution(String path) {
        Options options = new Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        int width = options.outWidth;
        int height = options.outHeight;
        if (width < 256 || height < 256) {
            return false;
        }
        return true;
    }

    public static double CalculationByLocation(double lat1, double lon1, double lat2, double lon2, String returnType) {
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
        if (returnType.equals("METER")) {
            return (double) meterInDec;
        }
        return ((double) 6371) * lat1_s;
    }

    public static int generateViewId() {
        if (VERSION.SDK_INT >= 17) {
            return View.generateViewId();
        }
        while (true) {
            int result = sNextGeneratedId.get();
            int newValue = result + 1;
            if (newValue > ViewCompat.MEASURED_SIZE_MASK) {
                newValue = 1;
            }
            if (sNextGeneratedId.compareAndSet(result, newValue)) {
                return result;
            }
        }
    }

    public static void runGC() {
    }

    public static void removeInput(EditText editBox) {
        editBox.setInputType(0);
        editBox.setFocusableInTouchMode(false);
        editBox.setFocusable(false);
        editBox.setOnTouchListener(Utils$$Lambda$0.$instance);
    }

    public static boolean checkText(MaterialEditText editBox) {
        if (getText(editBox).trim().equals("")) {
            return false;
        }
        return true;
    }

    public static boolean checkText(String txt) {
        if (!(txt == null || txt.trim().equals(""))) {
            if (!TextUtils.isEmpty(txt)) {
                return true;
            }
        }
        return false;
    }

    public static boolean checkText(EditText editBox) {
        if (getText(editBox).trim().equals("")) {
            return false;
        }
        return true;
    }

    public static String getText(MaterialEditText editBox) {
        return editBox.getText().toString();
    }

    public static String getText(EditText editBox) {
        return editBox.getText().toString();
    }

    public static String getText(TextView txtView) {
        return txtView.getText().toString();
    }

    public static boolean setErrorFields(MaterialEditText editBox, String error) {
        editBox.setError(error);
        return false;
    }

    public static boolean setErrorFields(EditText editBox, String error) {
        editBox.setError(error);
        return false;
    }

    public static void hideKeyboard(Context context) {
        if (context != null && (context instanceof Activity)) {
            hideKeyboard((Activity) context);
        }
    }

    public static void hideKeyboard(Activity act) {
        if (act != null && (act instanceof Activity)) {
            act.getWindow().setSoftInputMode(3);
            act.getWindow().setSoftInputMode(3);
            View view = act.getCurrentFocus();
            if (view != null) {
                ((InputMethodManager) act.getSystemService(act.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    public static void hideKeyPad(Activity act) {
        ((InputMethodManager) act.getSystemService(act.INPUT_METHOD_SERVICE)).toggleSoftInput(1, 0);
    }

    public static void setAppLocal(Context mContext) {
        String googleMapLangCode = new GeneralFunctions(mContext).retrieveValue(CommonUtilities.GOOGLE_MAP_LANGUAGE_CODE_KEY);
        Locale locale = new Locale(googleMapLangCode.trim().equals("") ? "en" : googleMapLangCode, mContext.getResources().getConfiguration().locale.getCountry());
        Locale.setDefault(locale);
        if (VERSION.SDK_INT >= 24) {
            updateResourcesLocale(mContext, locale);
        } else {
            updateResourcesLocaleLegacy(mContext, locale);
        }
    }

    @TargetApi(24)
    private static Context updateResourcesLocale(Context context, Locale locale) {
        Configuration configuration = context.getResources().getConfiguration();
        configuration.setLocale(locale);
        return context.createConfigurationContext(configuration);
    }

    private static Context updateResourcesLocaleLegacy(Context context, Locale locale) {
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.locale = locale;
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        return context;
    }

    public static void sendBroadCast(Context mContext, String action, String message) {
        Intent intent_broad = new Intent(action);
        intent_broad.putExtra(CommonUtilities.passenger_message_arrived_intent_key, message);
        mContext.sendBroadcast(intent_broad);
    }

    public static void sendBroadCast(Context mContext, String action) {
        mContext.sendBroadcast(new Intent(action));
    }

    public static String getAppVersion() {
        return "1";
    }

    public static Intent getPreviousIntent(Context context) {
        Intent newIntent = null;
        int i = 0;
        List<RecentTaskInfo> recentTaskInfos = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getRecentTasks(1024, 0);
        String myPkgNm = context.getPackageName();
        if (!recentTaskInfos.isEmpty()) {
            while (i < recentTaskInfos.size()) {
                RecentTaskInfo recentTaskInfo = (RecentTaskInfo) recentTaskInfos.get(i);
                if (recentTaskInfo.baseIntent.getComponent().getPackageName().equals(myPkgNm)) {
                    newIntent = recentTaskInfo.baseIntent;
                }
                i++;
            }
        }
        return newIntent;
    }

    public static String maskCardNumber(String cardNumber) {
        StringBuffer temp = new StringBuffer();
        for (int i = 0; i < cardNumber.length(); i++) {
            if (i > cardNumber.length() - 5) {
                temp.append(cardNumber.charAt(i));
            } else {
                temp.append("X");
            }
        }
        System.out.println(temp);
        return temp.toString();
    }

    public static int pxToDp(Context context, float pxValue) {
        return Math.round(pxValue / (context.getResources().getDisplayMetrics().xdpi / 160.0f));
    }

    public static boolean isMyServiceRunning(Class<?> serviceClass, Context context) {
        for (RunningServiceInfo service : ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static String getFileExt(String fileName) {
        return fileName.substring(fileName.lastIndexOf(FileUtils.HIDDEN_PREFIX) + 1, fileName.length());
    }

    public static String[] generateImageParams(String key, String content) {
        return new String[]{key, content};
    }

    public static String convertDateToFormat(String format, Date date) {
        return new SimpleDateFormat(format, Locale.US).format(date);
    }

    public static Date convertStringToDate(String format, String date) {
        try {
            return new SimpleDateFormat(format).parse(date);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void setMenuTextColor(MenuItem item, int color) {
        SpannableString s = new SpannableString(item.getTitle());
        s.setSpan(new ForegroundColorSpan(color), 0, s.length(), 0);
        item.setTitle(s);
    }

    public static void setBlurImage(Bitmap bitmap_profile_icon, ImageView profileimageback) {
        profileimageback.setImageBitmap(fastblur(bitmap_profile_icon, 95));
        profileimageback.invalidate();
    }

    public static String getUserDeviceCountryCode(Context context) {
        if (context == null) {
            return "";
        }
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String simCountry = tm.getSimCountryIso();
            if (simCountry != null && simCountry.length() == 2) {
                return simCountry.toLowerCase(Locale.US);
            }
            if (tm.getPhoneType() != 2) {
                String networkCountry = tm.getNetworkCountryIso();
                if (networkCountry != null && networkCountry.length() == 2) {
                    return networkCountry.toLowerCase(Locale.US);
                }
            }
            String countryCode = "";
            try {
                if (VERSION.SDK_INT >= 24) {
                    countryCode = context.getResources().getConfiguration().getLocales().get(0).getCountry();
                } else {
                    countryCode = context.getResources().getConfiguration().locale.getCountry();
                }
            } catch (Exception e) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(":Details:");
                stringBuilder.append(e.getMessage());
                printELog("LocalizedCountryCodeError", stringBuilder.toString());
            }
            return countryCode;
        } catch (Exception e2) {
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append(":Details:");
            stringBuilder2.append(e2.getMessage());
            printELog("TelephonyError", stringBuilder2.toString());
        }
        return "";
    }

    public static Bitmap fastblur(Bitmap sentBitmap, int radius) {
        int width = Math.round(sentBitmap.getWidth());
        int height = Math.round(sentBitmap.getHeight());
        sentBitmap = Bitmap.createScaledBitmap(sentBitmap, width, height, false);

        Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);

        if (radius < 1) {
            return (null);
        }

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int[] pix = new int[w * h];
        Log.e("pix", w + " " + h + " " + pix.length);
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);

        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;

        int r[] = new int[wh];
        int g[] = new int[wh];
        int b[] = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int vmin[] = new int[Math.max(w, h)];

        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int dv[] = new int[256 * divsum];
        for (i = 0; i < 256 * divsum; i++) {
            dv[i] = (i / divsum);
        }

        yw = yi = 0;

        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;

        for (y = 0; y < h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;

            for (x = 0; x < w; x++) {

                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vmin[x]];

                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[(stackpointer) % div];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;

                sir = stack[i + radius];

                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];

                rbs = r1 - Math.abs(i);

                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;

                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }

                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < h; y++) {
                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] = ( 0xff000000 & pix[yi] ) | ( dv[rsum] << 16 ) | ( dv[gsum] << 8 ) | dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vmin[y];

                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi += w;
            }
        }

        Log.e("pix", w + " " + h + " " + pix.length);
        bitmap.setPixels(pix, 0, w, 0, 0, w, h);

        return (bitmap);
    }

    public static float getScreenPixelWidth(Context mContext) {
        return (float) mContext.getResources().getDisplayMetrics().widthPixels;
    }

    public static int getWidthOfBanner(Context mContext, int widthOffsetDpValueInPixel) {
        return (int) (getScreenPixelWidth(mContext) - ((float) widthOffsetDpValueInPixel));
    }

    public static int getHeightOfBanner(Context mContext, int widthOffsetDpValueInPixel, String ratio) {
        if (ratio.equalsIgnoreCase("4:3")) {
            return (int) (((double) getWidthOfBanner(mContext, widthOffsetDpValueInPixel)) / 1.33333333333d);
        }
        return (int) (((double) getWidthOfBanner(mContext, widthOffsetDpValueInPixel)) / 1.77777778d);
    }

    public static String getResizeImgURL(String imgUrl, int width, int height) {
        imgUrl = imgUrl.replace(StringUtils.SPACE, "%20");
        String resizeURL = "http://webprojectsdemo.com/projects/homodriver/".endsWith("/") ? "http://webprojectsdemo.com/projects/homodriver/" : "http://webprojectsdemo.com/projects/homodriver//";
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(resizeURL);
        stringBuilder.append("resizeImg.php?src=");
        stringBuilder.append(imgUrl);
        stringBuilder.append("&w=");
        stringBuilder.append(width);
        stringBuilder.append("&h=");
        stringBuilder.append(height);
        return stringBuilder.toString();
    }

    public static boolean isAppInstalled(Context context, String packageName) {
        try {
            context.getPackageManager().getApplicationInfo(packageName, 0);
            return true;
        } catch (NameNotFoundException e) {
            return false;
        }
    }
}
