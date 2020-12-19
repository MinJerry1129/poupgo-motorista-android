package com.poupgo.driver;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.general.files.ExecuteWebServerUrl;
import com.general.files.UpdateDirections;
import com.general.functions.GeneralFunctions;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.maps.Directions;
import com.maps.DirectionsResponse;
import com.maps.MapService;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.utils.CommonUtilities;
import com.general.functions.Utils;
import com.view.MTextView;
import com.view.SelectableRoundedImageView;
import com.utilities.view.simpleratingbar.SimpleRatingBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class RideHistoryDetailActivity extends AppCompatActivity implements OnMapReadyCallback {

    MTextView titleTxt;
    MTextView subTitleTxt;
    ImageView backImgView;

    public GeneralFunctions generalFunc;

    GoogleMap gMap;

    LinearLayout fareDetailDisplayArea;
    private View convertView = null;

    LinearLayout beforeServiceArea, afterServiceArea;
    String before_serviceImg_url = "";
    String after_serviceImg_url = "";
    MTextView cartypeTxt;
    MTextView tipHTxt, tipamtTxt, tipmsgTxt;
    CardView tiparea;
    LinearLayout profilearea;
    ImageView tipPluseImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_history_detail);

        generalFunc = new GeneralFunctions(getActContext());


        titleTxt = (MTextView) findViewById(R.id.titleTxt);
        profilearea = (LinearLayout) findViewById(R.id.profilearea);
        subTitleTxt = (MTextView) findViewById(R.id.subTitleTxt);
        backImgView = (ImageView) findViewById(R.id.backImgView);
        fareDetailDisplayArea = (LinearLayout) findViewById(R.id.fareDetailDisplayArea);
        afterServiceArea = (LinearLayout) findViewById(R.id.afterServiceArea);
        beforeServiceArea = (LinearLayout) findViewById(R.id.beforeServiceArea);
        tipPluseImage = (ImageView) findViewById(R.id.tipPluseImage);
        cartypeTxt = (MTextView) findViewById(R.id.cartypeTxt);

        tipHTxt = (MTextView) findViewById(R.id.tipHTxt);
        tipamtTxt = (MTextView) findViewById(R.id.tipamtTxt);
        tipmsgTxt = (MTextView) findViewById(R.id.tipmsgTxt);

        tiparea = (CardView) findViewById(R.id.tiparea);
        setLabels();
        setData();

        backImgView.setOnClickListener(new setOnClickList());
        subTitleTxt.setOnClickListener(new setOnClickList());
        afterServiceArea.setOnClickListener(new setOnClickList());
        beforeServiceArea.setOnClickListener(new setOnClickList());
    }

    public void setLabels() {
        String tripData = getIntent().getStringExtra("TripData");

        titleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_RECEIPT_HEADER_TXT"));
        subTitleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_GET_RECEIPT_TXT"));


        String headerLable = "", noVal = "", driverhVal = "";

        if (generalFunc.getJsonValue("eType", tripData).equals(Utils.CabGeneralType_UberX)) {
            headerLable = generalFunc.retrieveLangLBl("", "LBL_THANKS_UFX_DRIVER");
            noVal = generalFunc.retrieveLangLBl("", "LBL_SERVICES") + "#";
            driverhVal = generalFunc.retrieveLangLBl("", "LBL_USER");
        } else if (generalFunc.getJsonValue("eType", tripData).equals("Deliver")) {
            headerLable = generalFunc.retrieveLangLBl("", "LBL_THANKS_DELIVERY_DRIVER");
            noVal = generalFunc.retrieveLangLBl("", "LBL_DELIVERY") + "#";
            driverhVal = generalFunc.retrieveLangLBl("", "LBL_SENDER");
        } else {
            headerLable = generalFunc.retrieveLangLBl("", "LBL_THANKS_RIDING_DRIVER");
            noVal = generalFunc.retrieveLangLBl("", "LBL_RIDE") + "#";
            driverhVal = generalFunc.retrieveLangLBl("", "LBL_PASSENGER_TXT");
        }

        ((MTextView) findViewById(R.id.headerTxt)).setText(generalFunc.retrieveLangLBl("", headerLable));

        ((MTextView) findViewById(R.id.rideNoHTxt)).setText(noVal);
        ((MTextView) findViewById(R.id.ratingDriverHTxt)).setText(generalFunc.retrieveLangLBl("", "LBL_RATING"));
        ((MTextView) findViewById(R.id.passengerHTxt)).setText(driverhVal);
        String dateLable = "";
        String pickupHval = "";

        if (generalFunc.getJsonValue("eType", tripData).equals(Utils.CabGeneralType_UberX)) {
            dateLable = generalFunc.retrieveLangLBl("", "LBL_JOB_REQ_DATE");
            pickupHval = generalFunc.retrieveLangLBl("", "LBL_JOB_LOCATION_TXT");
            tipmsgTxt.setText(generalFunc.retrieveLangLBl("Congratulation! You got a tip from the passenger for this trip.", "LBL_TIP_INFO_SHOW_PROVIDER"));
        } else if (generalFunc.getJsonValue("eType", tripData).equals("Deliver")) {
            dateLable = generalFunc.retrieveLangLBl("", "LBL_DELIVERY_REQUEST_DATE");
            pickupHval = generalFunc.retrieveLangLBl("", "LBL_SENDER_LOCATION");
            tipmsgTxt.setText(generalFunc.retrieveLangLBl("Congratulation! You got a tip from the passenger for this trip.", "LBL_TIP_INFO_SHOW_CARRIER"));
        } else {
            dateLable = generalFunc.retrieveLangLBl("", "LBL_TRIP_REQUEST_DATE_TXT");
            pickupHval = generalFunc.retrieveLangLBl("", "LBL_PICKUP_LOCATION_TXT");
            tipmsgTxt.setText(generalFunc.retrieveLangLBl("Congratulation! You got a tip from the passenger for this trip.", "LBL_TIP_INFO_SHOW_DRIVER"));
        }

        ((MTextView) findViewById(R.id.tripdateHTxt)).setText(generalFunc.retrieveLangLBl("", dateLable));
        ((MTextView) findViewById(R.id.pickUpHTxt)).setText(pickupHval);
        if (generalFunc.getJsonValue("eType", tripData).equals("Deliver")) {
            ((MTextView) findViewById(R.id.dropOffHTxt)).setText(generalFunc.retrieveLangLBl("", "LBL_DELIVERY_DETAILS_TXT"));
        } else if (generalFunc.getJsonValue("eType", tripData).equals(Utils.CabGeneralType_Ride)) {
            ((MTextView) findViewById(R.id.dropOffHTxt)).setText(generalFunc.retrieveLangLBl("", "LBL_DEST_LOCATION"));
        } else {
            ((MTextView) findViewById(R.id.dropOffHTxt)).setText(generalFunc.retrieveLangLBl("", "LBL_DEST_LOCATION"));

        }
        ((MTextView) findViewById(R.id.chargesHTxt)).setText(generalFunc.retrieveLangLBl("", "LBL_CHARGES_TXT"));
        ((MTextView) findViewById(R.id.serviceHTxt)).setText(generalFunc.retrieveLangLBl("", "LBL_SERVICE_TXT"));

        tipHTxt.setText(generalFunc.retrieveLangLBl("Tip Amount", "LBL_TIP_AMOUNT"));


    }

    public void setData() {
        String tripData = getIntent().getStringExtra("TripData");

        if (generalFunc.getJsonValue("eHailTrip", tripData).equalsIgnoreCase("yes")) {
            profilearea.setVisibility(View.GONE);
        } else {
            profilearea.setVisibility(View.VISIBLE);
        }

        ((MTextView) findViewById(R.id.rideNoVTxt)).setText(generalFunc.convertNumberWithRTL(generalFunc.getJsonValue("vRideNo", tripData)));
        ((MTextView) findViewById(R.id.namePassengerVTxt)).setText(generalFunc.getJsonValue("vName", tripData) + " " +
                generalFunc.getJsonValue("vLastName", tripData));
        ((MTextView) findViewById(R.id.tripdateVTxt)).setText(generalFunc.getDateFormatedType(generalFunc.getJsonValue("tTripRequestDateOrig", tripData), Utils.OriginalDateFormate, Utils.getDetailDateFormat()));
        ((MTextView) findViewById(R.id.pickUpVTxt)).setText(generalFunc.getJsonValue("tSaddress", tripData));
        if (generalFunc.getJsonValue("eType", tripData).equals("Deliver")) {

            ((MTextView) findViewById(R.id.dropOffVTxt)).setText(generalFunc.retrieveLangLBl("", "LBL_RECEIVER_NAME") + ": " + generalFunc.getJsonValue("vReceiverName", tripData) + "\n\n" +
                    generalFunc.retrieveLangLBl("", "LBL_RECEIVER_LOCATION") + ": " + generalFunc.getJsonValue("tDaddress", tripData) + "\n\n" +
                    generalFunc.retrieveLangLBl("", "LBL_PACKAGE_TYPE_TXT") + ": " + generalFunc.getJsonValue("PackageType", tripData) + "\n\n" +
                    generalFunc.retrieveLangLBl("", "LBL_PACKAGE_DETAILS") + ": " + generalFunc.getJsonValue("tPackageDetails", tripData)
            );
        } else {
            ((MTextView) findViewById(R.id.dropOffVTxt)).setText(generalFunc.getJsonValue("tDaddress", tripData));
        }

        if (generalFunc.getJsonValue("vVehicleCategory", tripData) != null && !generalFunc.getJsonValue("vVehicleCategory", tripData).equals("")) {
            cartypeTxt.setText(generalFunc.getJsonValue("vVehicleCategory", tripData) + "-" + generalFunc.getJsonValue("carTypeName", tripData));
        } else {
            cartypeTxt.setText(generalFunc.getJsonValue("carTypeName", tripData));
        }


        if (generalFunc.getJsonValue("tDaddress", tripData).equals("")) {
            ((MTextView) findViewById(R.id.dropOffVTxt)).setVisibility(View.GONE);
            ((MTextView) findViewById(R.id.dropOffHTxt)).setVisibility(View.GONE);
        }
        if (!generalFunc.getJsonValue("fTipPrice", tripData).equals("0") && !generalFunc.getJsonValue("fTipPrice", tripData).equals("0.0") &&
                !generalFunc.getJsonValue("fTipPrice", tripData).equals("0.00") &&
                !generalFunc.getJsonValue("fTipPrice", tripData).equals("")) {
            tiparea.setVisibility(View.VISIBLE);
            tipPluseImage.setVisibility(View.VISIBLE);

            tipamtTxt.setText(generalFunc.getJsonValue("fTipPrice", tripData));

        } else {
            tiparea.setVisibility(View.GONE);
            tipPluseImage.setVisibility(View.GONE);
        }

        String trip_status_str = generalFunc.getJsonValue("iActive", tripData);
        if (trip_status_str.contains("Canceled")) {
            String cancelLable = "";
            String cancelableReason = generalFunc.getJsonValue("vCancelReason", tripData);

            if (generalFunc.getJsonValue("eCancelledBy", tripData).equalsIgnoreCase("DRIVER")) {
                if (generalFunc.getJsonValue("eType", tripData).equals(Utils.CabGeneralType_UberX)) {
                    cancelLable = generalFunc.retrieveLangLBl("", "LBL_CANCELED_JOB");
                } else if (generalFunc.getJsonValue("eType", tripData).equals("Deliver")) {
                    cancelLable = generalFunc.retrieveLangLBl("", "LBL_CANCELED_DELIVERY_TXT");
                } else {
                    cancelLable = generalFunc.retrieveLangLBl("", "LBL_CANCELED_TRIP_TXT");
                }
            } else {
                if (generalFunc.getJsonValue("eType", tripData).equals(Utils.CabGeneralType_UberX)) {
                    cancelLable = generalFunc.retrieveLangLBl("", "LBL_USER_CANCEL_JOB_TXT");
                } else if (generalFunc.getJsonValue("eType", tripData).equals("Deliver")) {
                    cancelLable = generalFunc.retrieveLangLBl("", "LBL_SENDER_CANCEL_DELIVERY_TXT");
                } else {
                    cancelLable = generalFunc.retrieveLangLBl("", "LBL_PASSENGER_CANCEL_TRIP_TXT");
                }

            }

            ((MTextView) findViewById(R.id.tripStatusTxt)).setText(generalFunc.retrieveLangLBl("", cancelLable));
            (findViewById(R.id.tripDetailArea)).setVisibility(View.VISIBLE);

        } else if (trip_status_str.contains("Finished")) {
            String finishLable = "";
            if (generalFunc.getJsonValue("eType", tripData).equals(Utils.CabGeneralType_UberX)) {
                finishLable = generalFunc.retrieveLangLBl("", "LBL_FINISHED_JOB_TXT");
            } else if (generalFunc.getJsonValue("eType", tripData).equals("Deliver")) {
                finishLable = generalFunc.retrieveLangLBl("", "LBL_FINISHED_DELIVERY_TXT");
            } else {
                finishLable = generalFunc.retrieveLangLBl("", "LBL_FINISHED_TRIP_TXT");
            }

            ((MTextView) findViewById(R.id.tripStatusTxt)).setText(generalFunc.retrieveLangLBl("", finishLable));

            (findViewById(R.id.tripDetailArea)).setVisibility(View.VISIBLE);
            subTitleTxt.setVisibility(View.VISIBLE);
        } else {
            ((MTextView) findViewById(R.id.tripStatusTxt)).setText(trip_status_str);
        }

        if (generalFunc.getJsonValue("vTripPaymentMode", tripData).equals("Cash")) {
            ((MTextView) findViewById(R.id.paymentTypeTxt)).setText(generalFunc.retrieveLangLBl("", "LBL_CASH_PAYMENT_TXT"));
        }else if(generalFunc.getJsonValue("vTripPaymentMode", tripData).equals("Pos")) {
            ((MTextView) findViewById(R.id.paymentTypeTxt)).setText(generalFunc.retrieveLangLBl("", "LBL_POS"));

        }else if(generalFunc.getJsonValue("vTripPaymentMode", tripData).equals("Wallet_Cash")) {
            ((MTextView) findViewById(R.id.paymentTypeTxt)).setText(generalFunc.retrieveLangLBl("", "LBL_VIA_WALLET_CASH"));
        }else if (generalFunc.getJsonValue("vTripPaymentMode", tripData).equals("Voucher")){
            ((MTextView) findViewById(R.id.paymentTypeTxt)).setText(generalFunc.retrieveLangLBl("Voucher", "LBL_VOUCHER_PAYMENT"));
            ((ImageView) findViewById(R.id.paymentTypeImgeView)).setImageResource(R.mipmap.ic_voucher);
        }else {
            ((MTextView) findViewById(R.id.paymentTypeTxt)).setText(generalFunc.retrieveLangLBl("Card Payment", "LBL_CARD_PAYMENT"));
            ((ImageView) findViewById(R.id.paymentTypeImgeView)).setImageResource(R.mipmap.ic_card_new);
        }

        if (generalFunc.getJsonValue("eCancelled", tripData).equals("Yes")) {
            subTitleTxt.setVisibility(View.GONE);
            String cancelledLable = "";
            String cancelableReason = generalFunc.getJsonValue("vCancelReason", tripData);

            if (generalFunc.getJsonValue("eCancelledBy", tripData).equalsIgnoreCase("DRIVER")) {
                if (generalFunc.getJsonValue("eType", tripData).equals(Utils.CabGeneralType_UberX)) {
                    cancelledLable = generalFunc.retrieveLangLBl("", "LBL_PREFIX_JOB_CANCEL_YOU") + " " + cancelableReason;
                } else if (generalFunc.getJsonValue("eType", tripData).equals("Deliver")) {
                    cancelledLable = generalFunc.retrieveLangLBl("", "LBL_PREFIX_DELIVERY_CANCEL_YOU") + " " + cancelableReason;
                } else {
                    cancelledLable = generalFunc.retrieveLangLBl("", "LBL_PREFIX_TRIP_CANCEL_YOU") + " " + cancelableReason;
                }
            } else {
                if (generalFunc.getJsonValue("eType", tripData).equals(Utils.CabGeneralType_UberX)) {
                    cancelledLable = generalFunc.retrieveLangLBl("", "LBL_USER_CANCEL_JOB_TXT");
                } else if (generalFunc.getJsonValue("eType", tripData).equals("Deliver")) {
                    cancelledLable = generalFunc.retrieveLangLBl("", "LBL_SENDER_CANCEL_DELIVERY_TXT");
                } else {
                    cancelledLable = generalFunc.retrieveLangLBl("", "LBL_PASSENGER_CANCEL_TRIP_TXT");
                }
            }

            ((MTextView) findViewById(R.id.tripStatusTxt)).setText(generalFunc.retrieveLangLBl("", cancelledLable));


        }

        ((SimpleRatingBar) findViewById(R.id.ratingBar)).setRating(generalFunc.parseFloatValue(0, generalFunc.getJsonValue("TripRating", tripData)));

        String driverImageName = generalFunc.getJsonValue("vImgName", generalFunc.getJsonValue("PassengerDetails", tripData));
        final ImageView profilebackImage = (ImageView) findViewById(R.id.profileimageback);
        final ImageView driverImageview = (SelectableRoundedImageView) findViewById(R.id.driverImgView);

        Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                if (profilebackImage != null) {
                    Utils.setBlurImage(bitmap, profilebackImage);
                }
                driverImageview.setImageBitmap(bitmap);
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };


        if (driverImageName == null || driverImageName.equals("") || driverImageName.equals("NONE")) {
            ((SelectableRoundedImageView) findViewById(R.id.driverImgView)).setImageResource(R.mipmap.ic_no_pic_user);
        } else {
            driverImageview.setTag(target);
            String image_url = CommonUtilities.SERVER_URL_PHOTOS + "upload/Passenger/" + generalFunc.getJsonValue("iUserId", tripData) + "/"
                    + driverImageName;
            Picasso.with(getActContext())
                    .load(image_url)
                    .placeholder(R.mipmap.ic_no_pic_user)
                    .error(R.mipmap.ic_no_pic_user)
                    .into(target);
        }


        if (generalFunc.getJsonValue("eType", tripData).equalsIgnoreCase("UberX") || generalFunc.getJsonValue("eFareType", tripData).equalsIgnoreCase("Fixed")) {
            findViewById(R.id.card_service_area).setVisibility(View.VISIBLE);
            findViewById(R.id.serviceHTxt).setVisibility(View.GONE);
            findViewById(R.id.photoArea).setVisibility(View.VISIBLE);

            ((MTextView) findViewById(R.id.beforeImgHTxt)).setText(generalFunc.retrieveLangLBl("", "LBL_BEFORE_SERVICE"));
            ((MTextView) findViewById(R.id.afterImgHTxt)).setText(generalFunc.retrieveLangLBl("", "LBL_AFTER_SERVICE"));

            Utils.printLog("vBeforeImage", "::" + generalFunc.getJsonValue("vBeforeImage", tripData));
            if (!TextUtils.isEmpty(generalFunc.getJsonValue("vBeforeImage", tripData))) {
                findViewById(R.id.beforeServiceArea).setVisibility(View.VISIBLE);
                before_serviceImg_url = generalFunc.getJsonValue("vBeforeImage", tripData);

                String vBeforeImage = Utils.getResizeImgURL(before_serviceImg_url, getResources().getDimensionPixelSize(R.dimen.before_after_img_size), getResources().getDimensionPixelSize(R.dimen.before_after_img_size));

                displayPic(vBeforeImage, (ImageView) findViewById(R.id.iv_before_img), "before");

            } else {
                findViewById(R.id.beforeServiceArea).setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(generalFunc.getJsonValue("vAfterImage", tripData))) {
                findViewById(R.id.afterServiceArea).setVisibility(View.VISIBLE);
                after_serviceImg_url = generalFunc.getJsonValue("vAfterImage", tripData);

                String vAfterImage = Utils.getResizeImgURL(after_serviceImg_url, getResources().getDimensionPixelSize(R.dimen.before_after_img_size), getResources().getDimensionPixelSize(R.dimen.before_after_img_size));
                displayPic(vAfterImage, (ImageView) findViewById(R.id.iv_after_img), "after");

            } else {
                findViewById(R.id.afterServiceArea).setVisibility(View.GONE);
            }

            if (TextUtils.isEmpty(generalFunc.getJsonValue("vBeforeImage", tripData)) && TextUtils.isEmpty(generalFunc.getJsonValue("vAfterImage", tripData))) {
                findViewById(R.id.photoArea).setVisibility(View.GONE);
            }


            ((MTextView) findViewById(R.id.pickUpVTxt)).setText(generalFunc.getJsonValue("tSaddress", tripData));
            ((MTextView) findViewById(R.id.serviceTypeVTxt)).setText(generalFunc.getJsonValue("vVehicleCategory", tripData) + " - " + generalFunc.getJsonValue("vVehicleType", tripData));
            ((MTextView) findViewById(R.id.serviceTypeHTxt)).setText(generalFunc.retrieveLangLBl("", "LBL_Car_Type"));


        } else {
            findViewById(R.id.tripDetailArea).setVisibility(View.VISIBLE);
            findViewById(R.id.service_area).setVisibility(View.GONE);
            findViewById(R.id.card_service_area).setVisibility(View.GONE);
            findViewById(R.id.serviceHTxt).setVisibility(View.GONE);
            findViewById(R.id.photoArea).setVisibility(View.GONE);
        }

        boolean FareDetailsArrNew = generalFunc.isJSONkeyAvail("HistoryFareDetailsNewArr", tripData);

        JSONArray FareDetailsArrNewObj = null;
        if (FareDetailsArrNew == true) {
            FareDetailsArrNewObj = generalFunc.getJsonArray("HistoryFareDetailsNewArr", tripData);
        }
        if (FareDetailsArrNewObj != null)
            addFareDetailLayout(FareDetailsArrNewObj);

        subTitleTxt.setVisibility(View.GONE);
    }

    public void displayPic(String image_url, ImageView view, final String imgType) {

        Picasso.with(getActContext())
                .load(image_url)
                .placeholder(R.mipmap.ic_no_icon)
                .into(view, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        if (imgType.equalsIgnoreCase("before")) {
                            findViewById(R.id.before_loading).setVisibility(View.GONE);
                            findViewById(R.id.iv_before_img).setVisibility(View.VISIBLE);
                        } else if (imgType.equalsIgnoreCase("after")) {
                            findViewById(R.id.after_loading).setVisibility(View.GONE);
                            findViewById(R.id.iv_after_img).setVisibility(View.VISIBLE);
                        }

                    }

                    @Override
                    public void onError() {
                        if (imgType.equalsIgnoreCase("before")) {
                            findViewById(R.id.before_loading).setVisibility(View.VISIBLE);
                            findViewById(R.id.iv_before_img).setVisibility(View.GONE);
                        } else if (imgType.equalsIgnoreCase("after")) {
                            findViewById(R.id.after_loading).setVisibility(View.VISIBLE);
                            findViewById(R.id.iv_after_img).setVisibility(View.GONE);

                        }
                    }
                });

    }

    private void addFareDetailLayout(JSONArray jobjArray) {

        if (fareDetailDisplayArea.getChildCount() > 0) {
            fareDetailDisplayArea.removeAllViewsInLayout();
        }

        for (int i = 0; i < jobjArray.length(); i++) {
            JSONObject jobject = generalFunc.getJsonObject(jobjArray, i);
            try {
                addFareDetailRow(jobject.names().getString(0), jobject.get(jobject.names().getString(0)).toString(), jobjArray.length() - 1 == i ? true : false);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private void addFareDetailRow(String row_name, String row_value, boolean isLast) {
        View convertView = null;
        if (row_name.equalsIgnoreCase("eDisplaySeperator")) {
            convertView = new View(getActContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Utils.dipToPixels(getActContext(), 1));
            params.setMarginStart(Utils.dipToPixels(getActContext(), 10));
            params.setMarginEnd(Utils.dipToPixels(getActContext(), 10));
            convertView.setBackgroundColor(Color.parseColor("#dedede"));
            convertView.setLayoutParams(params);
        } else {
            LayoutInflater infalInflater = (LayoutInflater) getActContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.design_fare_deatil_row, null);

            convertView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            convertView.setPaddingRelative(Utils.dipToPixels(getActContext(), 10), 0, Utils.dipToPixels(getActContext(), 10), 0);

            convertView.setMinimumHeight(Utils.dipToPixels(getActContext(), 40));

            MTextView titleHTxt = (MTextView) convertView.findViewById(R.id.titleHTxt);
            MTextView titleVTxt = (MTextView) convertView.findViewById(R.id.titleVTxt);

            titleHTxt.setText(generalFunc.convertNumberWithRTL(row_name));
            titleVTxt.setText(generalFunc.convertNumberWithRTL(row_value));

            titleHTxt.setTextColor(Color.parseColor("#303030"));
            titleVTxt.setTextColor(Color.parseColor("#111111"));
        }

        if (convertView != null)
            fareDetailDisplayArea.addView(convertView);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.gMap = googleMap;

        String tripData = getIntent().getStringExtra("TripData");

        String tStartLat = generalFunc.getJsonValue("tStartLat", tripData);
        String tStartLong = generalFunc.getJsonValue("tStartLong", tripData);
        String tEndLat = generalFunc.getJsonValue("tEndLat", tripData);
        String tEndLong = generalFunc.getJsonValue("tEndLong", tripData);

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        Marker pickUpMarker = null;
        Marker destMarker = null;
        if (!tStartLat.equals("") && !tStartLat.equals("0.0") && !tStartLong.equals("") && !tStartLong.equals("0.0")) {
            LatLng pickUpLoc = new LatLng(generalFunc.parseDoubleValue(0.0, tStartLat), generalFunc.parseDoubleValue(0.0, tStartLong));
            MarkerOptions marker_opt = new MarkerOptions().position(pickUpLoc);
            marker_opt.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_source_marker)).anchor(0.5f, 0.5f);
            pickUpMarker = this.gMap.addMarker(marker_opt);

            builder.include(pickUpLoc);

            gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pickUpLoc, Utils.defaultZomLevel));
        }

        if (generalFunc.getJsonValue("iActive", tripData).equals("Finished")) {
            if (!tEndLat.equals("") && !tEndLat.equals("0.0") && !tEndLong.equals("") && !tEndLong.equals("0.0")) {
                LatLng destLoc = new LatLng(generalFunc.parseDoubleValue(0.0, tEndLat), generalFunc.parseDoubleValue(0.0, tEndLong));
                MarkerOptions marker_opt = new MarkerOptions().position(destLoc);
                marker_opt.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_dest_marker)).anchor(0.5f, 0.5f);
                destMarker = this.gMap.addMarker(marker_opt);

                builder.include(destLoc);

                gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(destLoc, 10));
            }
        }
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;

        gMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), width, Utils.dipToPixels(getActContext(), 200), 100));
        gMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker marker) {
                // TODO Auto-generated method stub
                marker.hideInfoWindow();
                return true;
            }
        });

        if (pickUpMarker != null && destMarker != null) {
            drawRoute(pickUpMarker.getPosition(), destMarker.getPosition());
        }

    }

    public void drawRoute(LatLng pickUpLoc, LatLng destinationLoc) {

        LatLng originLocation = new LatLng(pickUpLoc.latitude, pickUpLoc.longitude);
        LatLng destinationLocation = new LatLng(destinationLoc.latitude, destinationLoc.longitude);

        MapService.getDirections(originLocation, destinationLocation, new MapService.MapServiceCallback() {
            @Override
            public void onDirectionsSuccess(DirectionsResponse response) {
                Directions[] directions = response.polyline;

                if(directions != null && directions.length > 0) {

                    PolylineOptions lineOptions = UpdateDirections.getGoogleRouteOptions(directions, Utils.dipToPixels(getActContext(), 5), getActContext().getResources().getColor(R.color.black));

                    if (lineOptions != null) {
                        gMap.addPolyline(lineOptions);
                    }
                }
            }

            @Override
            public void onDirectionsFailed(DirectionsResponse response) {

            }
        });
    }

    public void sendReceipt() {

        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "getReceipt");
        parameters.put("UserType", CommonUtilities.app_type);
        parameters.put("iTripId", generalFunc.getJsonValue("iTripId", getIntent().getStringExtra("TripData")));

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
            @Override
            public void setResponse(String responseString) {

                Utils.printLog("Response", "::" + responseString);

                if (responseString != null && !responseString.equals("")) {

                    boolean isDataAvail = GeneralFunctions.checkDataAvail(CommonUtilities.action_str, responseString);

                    if (isDataAvail == true) {
                        generalFunc.showGeneralMessage("",
                                generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(CommonUtilities.message_str, responseString)));
                    } else {
                        generalFunc.showGeneralMessage("",
                                generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(CommonUtilities.message_str, responseString)));
                    }
                } else {
                    generalFunc.showError();
                }
            }
        });
        exeWebServer.execute();
    }

    public Context getActContext() {
        return RideHistoryDetailActivity.this;
    }

    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            Utils.hideKeyboard(RideHistoryDetailActivity.this);
            switch (view.getId()) {
                case R.id.backImgView:
                    RideHistoryDetailActivity.super.onBackPressed();
                    break;

                case R.id.subTitleTxt:
                    sendReceipt();
                    break;

            }
        }
    }
}
