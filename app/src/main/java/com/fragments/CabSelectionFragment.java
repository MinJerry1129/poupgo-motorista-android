package com.fragments;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;

import com.adapter.files.CabTypeAdapter;
import com.general.files.getUserData;
import com.poupgo.driver.FareBreakDownActivity;
import com.poupgo.driver.HailActivity;
import com.poupgo.driver.R;
import com.poupgo.driver.RentalDetailsActivity;
import com.general.files.ExecuteWebServerUrl;
import com.general.functions.GeneralFunctions;
import com.maps.Directions;
import com.maps.DirectionsRequest;
import com.maps.DirectionsResponse;
import com.maps.MapService;
import com.utilities.general.files.StartActProcess;
import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Picasso;
import com.utils.CommonUtilities;
import com.general.functions.Utils;
import com.utilities.view.CreateRoundedView;
import com.general.functions.GenerateAlertBox;
import com.view.MButton;
import com.view.MTextView;
import com.view.MaterialRippleLayout;
import com.view.SelectableRoundedImageView;
import com.view.indicator.AVLoadingIndicatorView;
import com.view.editBox.MaterialEditText;
import com.utilities.view.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class CabSelectionFragment extends Fragment implements CabTypeAdapter.OnItemClickList {


    // public LinearLayout rideBtnContainer;
    public MButton ride_now_btn;
    public int currentPanelDefaultStateHeight = 100;
    public String currentCabGeneralType;
    View view = null;
    static HailActivity mainAct;
    static GeneralFunctions generalFunc;
    String userProfileJson = "";
    RecyclerView carTypeRecyclerView;
    CabTypeAdapter adapter;
    ArrayList<HashMap<String, String>> cabTypeList;
    public ArrayList<HashMap<String, String>> rentalTypeList;
    public ArrayList<HashMap<String, String>> tempCabTypeList = new ArrayList<>();
    ArrayList<HashMap<String, String>> cabCategoryList;
    // MTextView personSizeVTxt;
    // LinearLayout minFareArea;

    String currency_sign = "";
    boolean isKilled = false;
    public String app_type = "Ride";
    LinearLayout paymentArea;
    LinearLayout promoArea;
    View payTypeSelectArea;
    String appliedPromoCode = "";
    boolean isCardValidated = true;
    static MTextView payTypeTxt;
    RadioButton cashRadioBtn;
    static RadioButton cardRadioBtn;

    public int selpos = 0;

    LinearLayout casharea;
    LinearLayout cardarea;
    static ImageView payImgView;
    LinearLayout cashcardarea;
    public int isSelcted = -1;
    String distance = "";
    String time = "";

    AVLoadingIndicatorView loaderView;
    MTextView noServiceTxt;
    boolean isCardnowselcted = false;
    boolean isCardlaterselcted = false;
    boolean dialogShowOnce = true;
    // String RideDeliveryType = "";

    public boolean isroutefound = true;

    String SelectedCarTypeID = "";
    public boolean isclickableridebtn = false;
    boolean ridelaterclick = false;
    boolean ridenowclick = false;
    Location tempDestLocation;
    Location tempPickUpLocation;
    private ExecuteWebServerUrl currentExeTask;

    double tollamount = 0.0;
    String tollcurrancy = "";
    boolean istollIgnore = false;

    android.support.v7.app.AlertDialog alertDialog_surgeConfirm;
    android.support.v7.app.AlertDialog tolltax_dialog;
    boolean isTollCostdilaogshow = false;

    String payableAmount = "";
    String tollskiptxt = "";

    boolean isRouteFail = false;

    boolean isFixFare = false;
    String isDestinationAdded = "Yes";

    public static int RENTAL_REQ_CODE = 1234;
    public String iRentalPackageId = "";

    public ImageView rentalBackImage, rentalinfoimage;
    MTextView rentalPkg;
    public MTextView rentalPkgDesc;
    RelativeLayout rentalarea;
    SelectableRoundedImageView rentPkgImage, rentBackPkgImage;


    public static void setCardSelection() {
        if (generalFunc == null) {
            generalFunc = mainAct.generalFunc;
        }
        payTypeTxt.setText(generalFunc.retrieveLangLBl("", "LBL_CARD"));


        mainAct.setCashSelection(false);

        cardRadioBtn.setChecked(true);

        payImgView.setImageResource(R.mipmap.ic_card_new);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (view != null) {
            return view;
        }

        cabTypeList = new ArrayList<HashMap<String, String>>();
        rentalTypeList = new ArrayList<HashMap<String, String>>();

        view = inflater.inflate(R.layout.fragment_new_cab_selection, container, false);

        mainAct = (HailActivity) getActivity();
        generalFunc = mainAct.generalFunc;
        findRoute();
        // RideDeliveryType = getArguments().getString("RideDeliveryType");

        rentalBackImage = (ImageView) view.findViewById(R.id.rentalBackImage);
        rentalarea = (RelativeLayout) view.findViewById(R.id.rentalarea);
        rentPkgImage = (SelectableRoundedImageView) view.findViewById(R.id.rentPkgImage);
        rentBackPkgImage = (SelectableRoundedImageView) view.findViewById(R.id.rentBackPkgImage);
        rentalBackImage.setOnClickListener(new setOnClickList());
        carTypeRecyclerView = (RecyclerView) view.findViewById(R.id.carTypeRecyclerView);
        loaderView = (AVLoadingIndicatorView) view.findViewById(R.id.loaderView);
        payTypeSelectArea = view.findViewById(R.id.payTypeSelectArea);
        payTypeTxt = (MTextView) view.findViewById(R.id.payTypeTxt);
        ride_now_btn = ((MaterialRippleLayout) view.findViewById(R.id.ride_now_btn)).getChildView();
        noServiceTxt = (MTextView) view.findViewById(R.id.noServiceTxt);

        casharea = (LinearLayout) view.findViewById(R.id.casharea);
        cardarea = (LinearLayout) view.findViewById(R.id.cardarea);
        rentalPkg = (MTextView) view.findViewById(R.id.rentalPkg);
        rentalPkgDesc = (MTextView) view.findViewById(R.id.rentalPkgDesc);
        rentalinfoimage = (ImageView) view.findViewById(R.id.rentalinfoimage);
        rentalPkg.setText(generalFunc.retrieveLangLBl("", "LBL_RENT_A_CAR"));
        rentalPkgDesc.setText(generalFunc.retrieveLangLBl("", "LBL_RENT_PKG_INFO"));


        new CreateRoundedView(getActContext().getResources().getColor(R.color.white), Utils.dipToPixels(getActContext(), 14), 1,
                getActContext().getResources().getColor(R.color.gray), rentBackPkgImage);
        new CreateRoundedView(getActContext().getResources().getColor(R.color.appThemeColor_1), Utils.dipToPixels(getActContext(), 12), 0,
                getActContext().getResources().getColor(R.color.appThemeColor_2), rentPkgImage);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_car);
        Drawable d = new BitmapDrawable(getResources(), bitmap);
        d.setColorFilter(getActContext().getResources().getColor(R.color.appThemeColor_TXT_1), PorterDuff.Mode.MULTIPLY);
        rentPkgImage.setImageDrawable(d);


        casharea.setOnClickListener(new setOnClickList());
        cardarea.setOnClickListener(new setOnClickList());
        rentalPkg.setOnClickListener(new setOnClickList());
        rentPkgImage.setOnClickListener(new setOnClickList());
//        if (generalFunc.isRTLmode()) {
//            rentPkgImage.setScaleX(1);
//            rentPkgImage.setScaleY(-1);
//            rentPkgImage.setRotation(180);
//        }


        paymentArea = (LinearLayout) view.findViewById(R.id.paymentArea);
        promoArea = (LinearLayout) view.findViewById(R.id.promoArea);
        promoArea.setOnClickListener(new setOnClickList());
        cashRadioBtn = (RadioButton) view.findViewById(R.id.cashRadioBtn);
        cardRadioBtn = (RadioButton) view.findViewById(R.id.cardRadioBtn);

        payImgView = (ImageView) view.findViewById(R.id.payImgView);

        cashcardarea = (LinearLayout) view.findViewById(R.id.cashcardarea);

        userProfileJson = mainAct.userProfileJson;

        currency_sign = generalFunc.getJsonValue("CurrencySymbol", userProfileJson);
        app_type = generalFunc.getJsonValue("APP_TYPE", userProfileJson);

        isKilled = false;

        cashRadioBtn.setVisibility(View.VISIBLE);
        payTypeTxt.setText(generalFunc.retrieveLangLBl("", "LBL_CASH_TXT"));
        cardRadioBtn.setVisibility(View.GONE);


        setLabels();

        ride_now_btn.setId(Utils.generateViewId());


        ride_now_btn.setOnClickListener(new setOnClickList());


        configRideLaterBtnArea(false);

        mainAct.sliding_layout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {

                if (isKilled) {
                    return;
                }
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {

                if (isKilled) {
                    return;
                }
//                if (newState == SlidingUpPanelLayout.PanelState.COLLAPSED) {
//                    configRideLaterBtnArea(false);
//                }
            }
        });


        return view;
    }


    public void checkSurgePrice() {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "checkSurgePrice");
        parameters.put("SelectedCarTypeID", "" + SelectedCarTypeID);
        parameters.put("iMemberId", generalFunc.getMemberId());
        parameters.put("UserType", Utils.userType);

        if (!iRentalPackageId.equalsIgnoreCase("")) {
            parameters.put("iRentalPackageId", iRentalPackageId);
        }
        if (mainAct.userLocation != null) {
            parameters.put("PickUpLatitude", "" + mainAct.userLocation.getLatitude());
            parameters.put("PickUpLongitude", "" + mainAct.userLocation.getLongitude());
        }

        if (mainAct.destlat != null && !mainAct.destlat.equalsIgnoreCase("")) {
            parameters.put("DestLatitude", "" + mainAct.destlat);
            parameters.put("DestLongitude", "" + mainAct.destlong);
        }


        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
            @Override
            public void setResponse(String responseString) {

                if (responseString != null && !responseString.equals("")) {


                    generalFunc.sendHeartBeat();

                    boolean isDataAvail = GeneralFunctions.checkDataAvail(CommonUtilities.action_str, responseString);

                    if (isDataAvail == true) {

                        if (generalFunc.getJsonValue("eFlatTrip", responseString).equalsIgnoreCase("Yes")) {

                            openFixChargeDialog(responseString, false);
                        } else {

                            getTollcostValue();
                        }


                    } else {
                        if (generalFunc.getJsonValue("eFlatTrip", responseString).equalsIgnoreCase("Yes")) {
                            openFixChargeDialog(responseString, true);
                        } else {
                            openSurgeConfirmDialog(responseString);
                        }
                    }
                } else {
                    generalFunc.showError();
                }
            }
        });
        exeWebServer.execute();
    }

    public void getTollcostValue() {

        if (isFixFare) {
            callStartTrip();
            return;
        }

        if (generalFunc.retrieveValue(CommonUtilities.ENABLE_TOLL_COST).equalsIgnoreCase("Yes")) {

            String url = CommonUtilities.TOLLURL + generalFunc.retrieveValue(CommonUtilities.TOLL_COST_APP_ID)
                    + "&app_code=" + generalFunc.retrieveValue(CommonUtilities.TOLL_COST_APP_CODE) + "&waypoint0=" + mainAct.userLocation.getLatitude()
                    + "," + mainAct.userLocation.getLongitude() + "&waypoint1=" + mainAct.destlat + "," + mainAct.destlong + "&mode=fastest;car";

            Utils.printLog("Toll URL", "url" + url);
            ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), url, true);
            exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
            exeWebServer.setDataResponseListener(responseString -> {

                if (responseString != null && !responseString.equals("")) {

                    if (generalFunc.getJsonValue("onError", responseString).equalsIgnoreCase("FALSE")) {
                        try {

                            String costs = generalFunc.getJsonValue("costs", responseString);

                            //  String details=generalFunc.getJsonValue("details",c)

                            String currency = generalFunc.getJsonValue("currency", costs);
                            String details = generalFunc.getJsonValue("details", costs);
                            String tollCost = generalFunc.getJsonValue("tollCost", details);
                            if (!currency.equals("") && currency != null) {
                                tollcurrancy = currency;
                            }
                            tollamount = 0.0;
                            if (!tollCost.equals("") && tollCost != null && !tollCost.equals("0.0")) {
                                tollamount = generalFunc.parseDoubleValue(0.0, tollCost);
                            }


                            TollTaxDialog();


                        } catch (Exception e) {

                            TollTaxDialog();
                        }

                    } else {
                        TollTaxDialog();
                    }


                } else {
                    generalFunc.showError();
                }

            });
            exeWebServer.execute();


        } else {
            callStartTrip();
        }

    }


    public void TollTaxDialog() {

        if (!isTollCostdilaogshow) {
            if (tollamount != 0.0 && tollamount != 0 && tollamount != 0.00) {
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActContext());

                LayoutInflater inflater = (LayoutInflater) getActContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View dialogView = inflater.inflate(R.layout.dialog_tolltax, null);

                final MTextView tolltaxTitle = (MTextView) dialogView.findViewById(R.id.tolltaxTitle);
                final MTextView tollTaxMsg = (MTextView) dialogView.findViewById(R.id.tollTaxMsg);
                final MTextView tollTaxpriceTxt = (MTextView) dialogView.findViewById(R.id.tollTaxpriceTxt);
                final MTextView cancelTxt = (MTextView) dialogView.findViewById(R.id.cancelTxt);

                final CheckBox checkboxTolltax = (CheckBox) dialogView.findViewById(R.id.checkboxTolltax);

                checkboxTolltax.setOnCheckedChangeListener((buttonView, isChecked) -> {

                    if (checkboxTolltax.isChecked()) {
                        istollIgnore = true;
                    } else {
                        istollIgnore = false;
                    }
                });


                MButton btn_type2 = ((MaterialRippleLayout) dialogView.findViewById(R.id.btn_type2)).getChildView();
                int submitBtnId = Utils.generateViewId();
                btn_type2.setId(submitBtnId);
                btn_type2.setText(generalFunc.retrieveLangLBl("", "LBL_CONTINUE_BTN"));
                btn_type2.setOnClickListener(v -> {
                    tolltax_dialog.dismiss();
                    isTollCostdilaogshow = true;
                    callStartTrip();
                });


                builder.setView(dialogView);
                tolltaxTitle.setText(generalFunc.retrieveLangLBl("", "LBL_TOLL_ROUTE"));
                tollTaxMsg.setText(generalFunc.retrieveLangLBl("", "LBL_TOLL_PRICE_DESC"));

                String payAmount = payableAmount;
                int pos;
                if (isSelcted == -1) {
                    pos = 0;
                } else {
                    pos = isSelcted;
                }

                if (cabTypeList != null && !SelectedCarTypeID.equals("") && cabTypeList.size() > 0 && !cabTypeList.get(pos).get("SubTotal").equals("") && !cabTypeList.get(pos).get("eRental").equalsIgnoreCase("Yes") /*&& payAmount.equalsIgnoreCase("")*/) {
                    try {
                        payAmount = generalFunc.convertNumberWithRTL(cabTypeList.get(pos).get("SubTotal"));
                    } catch (Exception e) {

                    }
                }

                if (payAmount.equalsIgnoreCase("")) {
                    tollTaxpriceTxt.setText(generalFunc.retrieveLangLBl("Total toll price", "LBL_TOLL_PRICE_TOTAL") + ": " + tollcurrancy + " " + tollamount);
                } else {
                    tollTaxpriceTxt.setText(generalFunc.retrieveLangLBl(
                            "Current Fare", "LBL_CURRENT_FARE") + ": " + payAmount + "\n" + "+" + "\n" +
                            generalFunc.retrieveLangLBl("Total toll price", "LBL_TOLL_PRICE_TOTAL") + ": " + tollcurrancy + " " + tollamount);
                }


                checkboxTolltax.setText(generalFunc.retrieveLangLBl("", "LBL_IGNORE_TOLL_ROUTE"));
                cancelTxt.setText(generalFunc.retrieveLangLBl("", "LBL_CANCEL_TXT"));

                cancelTxt.setOnClickListener(v -> {
                    tolltax_dialog.dismiss();
                });


                tolltax_dialog = builder.create();
                if (generalFunc.isRTLmode() == true) {
                    generalFunc.forceRTLIfSupported(tolltax_dialog);
                }
                tolltax_dialog.setCancelable(false);
                tolltax_dialog.show();
            } else {
                callStartTrip();
            }
        } else {

            callStartTrip();

        }
    }

    public void openFixChargeDialog(String responseString, boolean isSurCharge) {

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActContext());
        builder.setTitle("");
        builder.setCancelable(false);
        LayoutInflater inflater = (LayoutInflater) getActContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.surge_confirm_design, null);
        builder.setView(dialogView);

        MTextView payableAmountTxt;
        MTextView payableTxt;

        ((MTextView) dialogView.findViewById(R.id.headerMsgTxt)).setText(generalFunc.retrieveLangLBl("", generalFunc.retrieveLangLBl("", "LBL_FIX_FARE_HEADER")));


        ((MTextView) dialogView.findViewById(R.id.tryLaterTxt)).setText(generalFunc.retrieveLangLBl("", "LBL_TRY_LATER"));

        payableTxt = (MTextView) dialogView.findViewById(R.id.payableTxt);
        payableAmountTxt = (MTextView) dialogView.findViewById(R.id.payableAmountTxt);
        if (!generalFunc.getJsonValue("fFlatTripPricewithsymbol", responseString).equalsIgnoreCase("")) {
            payableAmountTxt.setVisibility(View.VISIBLE);
            payableTxt.setVisibility(View.GONE);

            if (isSurCharge) {

                payableAmount = generalFunc.getJsonValue("fFlatTripPricewithsymbol", responseString) + " " + "(" + generalFunc.retrieveLangLBl("", "LBL_AT_TXT") + " " +
                        generalFunc.convertNumberWithRTL(generalFunc.getJsonValue("SurgePrice", responseString)) + ")";
                ((MTextView) dialogView.findViewById(R.id.surgePriceTxt)).setText(generalFunc.convertNumberWithRTL(payableAmount));
            } else {
                payableAmount = generalFunc.getJsonValue("fFlatTripPricewithsymbol", responseString);
                ((MTextView) dialogView.findViewById(R.id.surgePriceTxt)).setText(generalFunc.convertNumberWithRTL(payableAmount));

            }
        } else {
            payableAmountTxt.setVisibility(View.GONE);
            payableTxt.setVisibility(View.VISIBLE);

        }

        MButton btn_type2 = ((MaterialRippleLayout) dialogView.findViewById(R.id.btn_type2)).getChildView();
        btn_type2.setText(generalFunc.retrieveLangLBl("", "LBL_ACCEPT_TXT"));
        btn_type2.setId(Utils.generateViewId());

        btn_type2.setOnClickListener(view -> {

            alertDialog_surgeConfirm.dismiss();
            callStartTrip();
        });

        (dialogView.findViewById(R.id.tryLaterTxt)).setOnClickListener(view -> {
            alertDialog_surgeConfirm.dismiss();
        });

        alertDialog_surgeConfirm = builder.create();
        alertDialog_surgeConfirm.setCancelable(false);
        alertDialog_surgeConfirm.setCanceledOnTouchOutside(false);
        if (generalFunc.isRTLmode() == true) {
            generalFunc.forceRTLIfSupported(alertDialog_surgeConfirm);
        }

        alertDialog_surgeConfirm.show();
    }


    public void openSurgeConfirmDialog(String responseString) {

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActContext());
        builder.setTitle("");
        builder.setCancelable(false);
        LayoutInflater inflater = (LayoutInflater) getActContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.surge_confirm_design, null);
        builder.setView(dialogView);

        MTextView payableAmountTxt;
        MTextView payableTxt;


        ((MTextView) dialogView.findViewById(R.id.headerMsgTxt)).setText(generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(CommonUtilities.message_str, responseString)));
        ((MTextView) dialogView.findViewById(R.id.surgePriceTxt)).setText(generalFunc.convertNumberWithRTL(generalFunc.getJsonValue("SurgePrice", responseString)));

        ((MTextView) dialogView.findViewById(R.id.tryLaterTxt)).setText(generalFunc.retrieveLangLBl("", "LBL_TRY_LATER"));
        payableTxt = (MTextView) dialogView.findViewById(R.id.payableTxt);
        payableAmountTxt = (MTextView) dialogView.findViewById(R.id.payableAmountTxt);

        payableTxt.setText(generalFunc.retrieveLangLBl("", "LBL_PAYABLE_AMOUNT"));

        int pos;
        if (isSelcted == -1) {
            pos = 0;
        } else {
            pos = isSelcted;
        }

        if (cabTypeList != null && !SelectedCarTypeID.equals("") && !cabTypeList.get(pos).get("SubTotal").equals("") && !cabTypeList.get(pos).get("eRental").equalsIgnoreCase("Yes")) {
            payableAmountTxt.setVisibility(View.VISIBLE);
            payableTxt.setVisibility(View.GONE);
            payableAmount = generalFunc.convertNumberWithRTL(cabTypeList.get(pos).get("SubTotal"));

            payableAmountTxt.setText(generalFunc.retrieveLangLBl("Approx payable amount", "LBL_APPROX_PAY_AMOUNT") + ": " + payableAmount);
        } else {
            payableAmountTxt.setVisibility(View.GONE);
            payableTxt.setVisibility(View.VISIBLE);

        }


        MButton btn_type2 = ((MaterialRippleLayout) dialogView.findViewById(R.id.btn_type2)).getChildView();
        btn_type2.setText(generalFunc.retrieveLangLBl("", "LBL_ACCEPT_SURGE"));
        btn_type2.setId(Utils.generateViewId());

        btn_type2.setOnClickListener(view -> {
            alertDialog_surgeConfirm.dismiss();
            getTollcostValue();
        });

        (dialogView.findViewById(R.id.tryLaterTxt)).setOnClickListener(view -> {
            alertDialog_surgeConfirm.dismiss();
        });


        alertDialog_surgeConfirm = builder.create();
        alertDialog_surgeConfirm.setCancelable(false);
        alertDialog_surgeConfirm.setCanceledOnTouchOutside(false);
        if (generalFunc.isRTLmode() == true) {
            generalFunc.forceRTLIfSupported(alertDialog_surgeConfirm);
        }

        alertDialog_surgeConfirm.show();
    }

    public void showLoader() {
        loaderView.setVisibility(View.VISIBLE);
        ride_now_btn.setEnabled(false);
        ride_now_btn.setTextColor(Color.parseColor("#BABABA"));
    }

    public void closeLoader() {
        loaderView.setVisibility(View.GONE);

        ride_now_btn.setEnabled(true);
        ride_now_btn.setTextColor(Color.parseColor("#FFFFFF"));
    }

    public void setUserProfileJson() {
        userProfileJson = mainAct.userProfileJson;
    }

    public void checkCardConfig() {
        setUserProfileJson();

        String vStripeCusId = generalFunc.getJsonValue("vStripeCusId", userProfileJson);
        String vBepayAccountId = generalFunc.getJsonValue("vBepayAccountId", userProfileJson);

        if (/*vStripeCusId*/vBepayAccountId.equals("")) {
            // Open CardPaymentActivity
            mainAct.OpenCardPaymentAct(true);
        } else {
            showPaymentBox();
        }
    }

    public void showPaymentBox() {
        android.support.v7.app.AlertDialog alertDialog;
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActContext());
        builder.setTitle("");
        builder.setCancelable(false);
        LayoutInflater inflater = (LayoutInflater) getActContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.input_box_view, null);
        builder.setView(dialogView);

        final MaterialEditText input = (MaterialEditText) dialogView.findViewById(R.id.editBox);
        final MTextView subTitleTxt = (MTextView) dialogView.findViewById(R.id.subTitleTxt);

        Utils.removeInput(input);

        subTitleTxt.setVisibility(View.VISIBLE);
        subTitleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_TITLE_PAYMENT_ALERT"));
        input.setText(generalFunc.getJsonValue("vCreditCard", userProfileJson));

        builder.setPositiveButton(generalFunc.retrieveLangLBl("Confirm", "LBL_BTN_TRIP_CANCEL_CONFIRM_TXT"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setNeutralButton(generalFunc.retrieveLangLBl("Change", "LBL_CHANGE"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                mainAct.OpenCardPaymentAct(true);
            }
        });
        builder.setNegativeButton(generalFunc.retrieveLangLBl("Cancel", "LBL_CANCEL_TXT"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });


        alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    public void setCashSelection() {
        payTypeTxt.setText(generalFunc.retrieveLangLBl("", "LBL_CASH_TXT"));

        isCardValidated = false;
        mainAct.setCashSelection(true);
        cashRadioBtn.setChecked(true);

        payImgView.setImageResource(R.mipmap.ic_cash_new);
    }

    public void setLabels() {
        ride_now_btn.setText(generalFunc.retrieveLangLBl("Start Trip", "LBL_START_TRIP"));
        noServiceTxt.setText(generalFunc.retrieveLangLBl("service not available in this location", "LBL_NO_SERVICE_AVAILABLE_TXT"));
    }


    public void configRideLaterBtnArea(boolean isGone) {
        mainAct.setPanelHeight(232);
    }

    public void setadpterData() {
        try {
            Utils.printLog("cabtypelist", cabTypeList.size() + "");
            if (cabTypeList.size() == 0) {
                ride_now_btn.setEnabled(false);
                ride_now_btn.setTextColor(Color.parseColor("#BABABA"));
            } else {
//                ride_now_btn.setEnabled(true);
//                ride_now_btn.setTextColor(getResources().getColor(R.color.btn_text_color_type2));

            }
            if (adapter == null) {
                selpos = 0;
                adapter = new CabTypeAdapter(getActContext(), cabTypeList, generalFunc);
                adapter.setSelectedVehicleTypeId(SelectedCarTypeID);
                adapter.setOnItemClickList(this);
                carTypeRecyclerView.setAdapter(adapter);
            } else {
                adapter.notifyDataSetChanged();

            }


        } catch (Exception e) {

        }
    }


    public void setShadow() {
        (view.findViewById(R.id.shadowView)).setVisibility(View.VISIBLE);
    }

    public Context getActContext() {
        return mainAct.getActContext();
    }

    @Override
    public void onItemClick(int position) {
        selpos = position;
        SelectedCarTypeID = cabTypeList.get(position).get("iVehicleTypeId");
        ArrayList<HashMap<String, String>> tempList = new ArrayList<>();
        tempList.addAll(cabTypeList);
        adapter.setSelectedVehicleTypeId(SelectedCarTypeID);
        cabTypeList.clear();

        for (int i = 0; i < tempList.size(); i++) {
            HashMap<String, String> map = tempList.get(i);

            if (i != position) {
                map.put("isHover", "false");
            } else if (i == position) {
                if (dialogShowOnce && tempList.get(i).get("isHover").equals("true")) {
                    dialogShowOnce = true;
                } else if (!dialogShowOnce && tempList.get(i).get("isHover").equals("true")) {
                    dialogShowOnce = true;
                } else {
                    dialogShowOnce = false;
                }

                map.put("isHover", "true");
                isSelcted = position;
                if (tempList.get(i).get("eFlatTrip") != null &&
                        !tempList.get(i).get("eFlatTrip").equalsIgnoreCase("") &&
                        tempList.get(i).get("eFlatTrip").equalsIgnoreCase("Yes")) {
                    isFixFare = true;
                } else {
                    isFixFare = false;
                }
            }
            cabTypeList.add(map);
        }

        if (isSelcted == position) {
            // mainAct.showLoader();
            if (dialogShowOnce) {

                dialogShowOnce = false;
                Utils.printLog("allready select", "");
                openFareDetailsDilaog(position);
            }

        } else {
            Utils.printLog("not select", "");
        }


        if (position > (cabTypeList.size() - 1)) {
            return;
        }

        adapter.notifyDataSetChanged();


    }

    public void hidePayTypeSelectionArea() {
        payTypeSelectArea.setVisibility(View.GONE);
        cashcardarea.setVisibility(View.VISIBLE);
        mainAct.setPanelHeight(232);
    }

    public void checkPromoCode(final String promoCode) {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "CheckPromoCode");
        parameters.put("PromoCode", promoCode);
        parameters.put("iUserId", generalFunc.getMemberId());

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setDataResponseListener(responseString -> {
            if (responseString != null && !responseString.equals("")) {

                String action = generalFunc.getJsonValue(CommonUtilities.action_str, responseString);
                if (action.equals("1")) {
                    appliedPromoCode = promoCode;
                    generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_PROMO_APPLIED"));
                } else if (action.equals("01")) {
                    generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_PROMO_USED"));
                } else {
                    generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_PROMO_INVALIED"));
                }
            } else {
                generalFunc.showError();
            }
        });
        exeWebServer.execute();
    }

    public void showPromoBox() {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActContext());
        builder.setTitle(generalFunc.retrieveLangLBl("", "LBL_PROMO_CODE_ENTER_TITLE"));

        LayoutInflater inflater = (LayoutInflater) getActContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.input_box_view, null);
        builder.setView(dialogView);

        final MaterialEditText input = (MaterialEditText) dialogView.findViewById(R.id.editBox);


        if (!appliedPromoCode.equals("")) {
            input.setText(appliedPromoCode);
        }
        builder.setPositiveButton(generalFunc.retrieveLangLBl("OK", "LBL_BTN_OK_TXT"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (input.getText().toString().trim().equals("") && appliedPromoCode.equals("")) {
                    generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_ENTER_PROMO"));
                } else if (input.getText().toString().trim().equals("") && !appliedPromoCode.equals("")) {
                    appliedPromoCode = "";
                    generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_PROMO_REMOVED"));
                } else if (input.getText().toString().contains(" ")) {
                    generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_PROMO_INVALIED"));
                } else {
                    checkPromoCode(input.getText().toString().trim());
                }
            }
        });
        builder.setNegativeButton(generalFunc.retrieveLangLBl("", "LBL_SKIP_TXT"), (dialog, which) -> dialog.cancel());

        android.support.v7.app.AlertDialog alertDialog = builder.create();
        if (generalFunc.isRTLmode() == true) {
            generalFunc.forceRTLIfSupported(alertDialog);
        }
        alertDialog.show();
        alertDialog.setOnCancelListener(dialogInterface -> Utils.hideKeyboard(mainAct));

    }

    public String getAppliedPromoCode() {
        return this.appliedPromoCode;
    }

    public boolean calculateDistnace(Location start, Location end) {


        float distance = start.distanceTo(end);
        Utils.printLog("distance", "::" + distance);
        if (distance > 200) {
            return true;
        } else {
            return false;
        }
    }


    public void findRoute() {
        try {

            showLoader();

            if (this.currentExeTask != null) {
                this.currentExeTask.cancel(true);
                this.currentExeTask = null;
                Utils.runGC();
            }


            LatLng originLocation = new LatLng(mainAct.userLocation.getLatitude(), mainAct.userLocation.getLongitude());
            LatLng destinationLocation = new LatLng(Double.parseDouble(mainAct.destlat), Double.parseDouble(mainAct.destlong));
            MapService.getDirections(originLocation, destinationLocation, new MapService.MapServiceCallback() {
                @Override
                public void onDirectionsSuccess(DirectionsResponse response) {
                    mainAct.hideprogress();
                    isRouteFail = false;
                    ride_now_btn.setEnabled(true);
                    ride_now_btn.setTextColor(getResources().getColor(R.color.btn_text_color_type2));

                    Directions[] polylineDirections = response.polyline;

                    if(polylineDirections != null && polylineDirections.length > 0) {
                        String travelDistance = String.valueOf(response.distance);
                        String travelTime = String.valueOf(response.travelTime);

                        Utils.printLog("Fareurl", ":Data:" + distance + "::" + time);
                        isDestinationAdded = "Yes";
                        getCabdetails(travelDistance, travelTime);

                    }
                }

                @Override
                public void onDirectionsFailed(DirectionsResponse response) {
                    closeLoader();
                    isRouteFail = true;
                    distance = "";
                    time = "";
                    mainAct.destlat = "";
                    mainAct.destlong = "";
                    isDestinationAdded = "No";
                    generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("Route not found", "LBL_DEST_ROUTE_NOT_FOUND"));

                    getCabdetails(null, null);
                }
            });

        } catch (Exception e) {
            Utils.printLog("RouteException", e.toString());
        }
    }

    public void openFareDetailsDilaog(final int pos) {

        if (cabTypeList.get(isSelcted).get("SubTotal") != null) {
            String vehicleIconPath = CommonUtilities.SERVER_URL + "webimages/icons/VehicleType/";
            String vehicleDefaultIconPath = CommonUtilities.SERVER_URL + "webimages/icons/DefaultImg/";
            // final Dialog faredialog = new Dialog(getActContext());
            final BottomSheetDialog faredialog = new BottomSheetDialog(getActContext());

            View contentView = View.inflate(getContext(), R.layout.dailog_faredetails, null);
            faredialog.setContentView(contentView);
            BottomSheetBehavior mBehavior = BottomSheetBehavior.from((View) contentView.getParent());
            mBehavior.setPeekHeight(1500);
            View bottomSheetView = faredialog.getWindow().getDecorView().findViewById(android.support.design.R.id.design_bottom_sheet);
            BottomSheetBehavior.from(bottomSheetView).setHideable(false);
            setCancelable(faredialog, false);


            ImageView imagecar;
            final MTextView carTypeTitle, capacityHTxt, capacityVTxt, fareHTxt, fareVTxt, mordetailsTxt, farenoteTxt, pkgMsgTxt;
            MButton btn_type2;
            int submitBtnId;
            imagecar = (ImageView) faredialog.findViewById(R.id.imagecar);
            carTypeTitle = (MTextView) faredialog.findViewById(R.id.carTypeTitle);
            capacityHTxt = (MTextView) faredialog.findViewById(R.id.capacityHTxt);
            capacityVTxt = (MTextView) faredialog.findViewById(R.id.capacityVTxt);
            fareHTxt = (MTextView) faredialog.findViewById(R.id.fareHTxt);
            fareVTxt = (MTextView) faredialog.findViewById(R.id.fareVTxt);
            pkgMsgTxt = (MTextView) faredialog.findViewById(R.id.pkgMsgTxt);
            mordetailsTxt = (MTextView) faredialog.findViewById(R.id.mordetailsTxt);
            farenoteTxt = (MTextView) faredialog.findViewById(R.id.farenoteTxt);
            btn_type2 = ((MaterialRippleLayout) faredialog.findViewById(R.id.btn_type2)).getChildView();
            submitBtnId = Utils.generateViewId();
            btn_type2.setId(submitBtnId);


            capacityHTxt.setText(generalFunc.retrieveLangLBl("", "LBL_CAPACITY"));
            fareHTxt.setText(generalFunc.retrieveLangLBl("", "LBL_FARE_TXT"));

            mordetailsTxt.setText(generalFunc.retrieveLangLBl("", "LBL_MORE_DETAILS"));
            if (isFixFare) {
                farenoteTxt.setText(generalFunc.retrieveLangLBl("", "LBL_GENERAL_NOTE_FLAT_FARE_EST"));
            } else {
                farenoteTxt.setText(generalFunc.retrieveLangLBl("", "LBL_GENERAL_NOTE_FARE_EST"));
            }
            btn_type2.setText(generalFunc.retrieveLangLBl("", "LBL_DONE"));


            if (cabTypeList.get(selpos).get("eRental") != null && cabTypeList.get(selpos).get("eRental").equalsIgnoreCase("Yes")) {
                mordetailsTxt.setVisibility(View.GONE);

                pkgMsgTxt.setVisibility(View.VISIBLE);
                fareHTxt.setText(generalFunc.retrieveLangLBl("", "LBL_PKG_STARTING_AT"));
                pkgMsgTxt.setText(generalFunc.retrieveLangLBl("", "LBL_RENT_PKG_MSG"));
                farenoteTxt.setText(generalFunc.retrieveLangLBl("", "LBL_RENT_PKG_DETAILS"));
                carTypeTitle.setText(cabTypeList.get(isSelcted).get("vRentalVehicleTypeName"));

            } else {
                carTypeTitle.setText(cabTypeList.get(isSelcted).get("vVehicleTypeName"));
            }
            if (cabTypeList.get(isSelcted).get("SubTotal").equals("")) {
                fareVTxt.setText("--");
            } else {
                fareVTxt.setText(generalFunc.convertNumberWithRTL(cabTypeList.get(isSelcted).get("SubTotal")));
            }
            capacityVTxt.setText(generalFunc.convertNumberWithRTL(cabTypeList.get(isSelcted).get("iPersonSize")) + " " + generalFunc.retrieveLangLBl("", "LBL_PEOPLE_TXT"));

            String imgName = cabTypeList.get(pos).get("vLogo1");
            if (imgName.equals("")) {
                imgName = vehicleDefaultIconPath + "hover_ic_car.png";
            } else {
                imgName = vehicleIconPath + cabTypeList.get(pos).get("iVehicleTypeId") + "/android/" + "xxxhdpi_" +
                        cabTypeList.get(pos).get("vLogo1");

            }

            Picasso.with(getActContext())
                    .load(imgName)
                    .into(imagecar);

            btn_type2.setOnClickListener(v -> {
                dialogShowOnce = true;
                faredialog.dismiss();
            });

            mordetailsTxt.setOnClickListener(v -> {
                dialogShowOnce = true;
                Bundle bn = new Bundle();
                bn.putString("SelectedCar", cabTypeList.get(isSelcted).get("iVehicleTypeId"));
                bn.putString("iUserId", generalFunc.getMemberId());
                bn.putString("distance", distance);
                bn.putString("time", time);
                //  bn.putString("PromoCode", appliedPromoCode);
                bn.putString("vVehicleType", cabTypeList.get(isSelcted).get("vVehicleType"));
                if (mainAct.userLocation != null) {
                    bn.putString("picupLat", mainAct.userLocation.getLatitude() + "");
                    bn.putString("pickUpLong", mainAct.userLocation.getLongitude() + "");
                }
                if (mainAct.destlat != null & !mainAct.destlat.equalsIgnoreCase("")) {
                    bn.putString("destLat", mainAct.destlat + "");
                    bn.putString("destLong", mainAct.destlong + "");
                }

                bn.putBoolean("isFixFare", isFixFare);
                bn.putString("isDestinationAdded", isDestinationAdded);


                new StartActProcess(getActContext()).startActWithData(FareBreakDownActivity.class, bn);
                faredialog.dismiss();
            });

            faredialog.setOnDismissListener(dialog -> {
                dialogShowOnce = true;
            });

            faredialog.show();
        }
    }


    public void setCancelable(Dialog dialogview, boolean cancelable) {
        final Dialog dialog = dialogview;
        View touchOutsideView = dialog.getWindow().getDecorView().findViewById(android.support.design.R.id.touch_outside);
        View bottomSheetView = dialog.getWindow().getDecorView().findViewById(android.support.design.R.id.design_bottom_sheet);

        if (cancelable) {
            touchOutsideView.setOnClickListener(v -> {
                if (dialog.isShowing()) {
                    dialog.cancel();
                }
            });
            BottomSheetBehavior.from(bottomSheetView).setHideable(true);
        } else {
            touchOutsideView.setOnClickListener(null);
            BottomSheetBehavior.from(bottomSheetView).setHideable(false);
        }
    }

    public String getAvailableCarTypesIds() {
        String carTypesIds = "";
        for (int i = 0; i < mainAct.cabTypesArrList.size(); i++) {
            String iVehicleTypeId = generalFunc.getJsonValue("iVehicleTypeId", mainAct.cabTypesArrList.get(i));

            carTypesIds = carTypesIds.equals("") ? iVehicleTypeId : (carTypesIds + "," + iVehicleTypeId);
        }
        return carTypesIds;
    }

    public void getCabdetails(final String distance, final String time) {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "getDriverVehicleDetails");
        parameters.put("iDriverId", generalFunc.getMemberId());
        parameters.put("UserType", "Driver");

        if (mainAct.userLocation != null) {
            parameters.put("StartLatitude", mainAct.userLocation.getLatitude() + "");
            parameters.put("EndLongitude", mainAct.userLocation.getLongitude() + "");
        }
        if (!mainAct.destlat.equalsIgnoreCase("")) {
            parameters.put("DestLatitude", mainAct.destlat + "");
            parameters.put("DestLongitude", mainAct.destlong + "");

        }

        parameters.put("VehicleTypeIds", getAvailableCarTypesIds());
        if (distance != null) {
            parameters.put("distance", distance);
        }
        if (time != null) {
            parameters.put("time", time);
        }

        if (mainAct.userLocation != null) {
            parameters.put("StartLatitude", mainAct.userLocation.getLatitude() + "");
            parameters.put("EndLongitude", mainAct.userLocation.getLongitude() + "");
        }

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActivity(), parameters);

        exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
            @Override
            public void setResponse(String responseString) {

                if (responseString != null && !responseString.equals("")) {

                    closeLoader();

                    if (generalFunc.getJsonValue(CommonUtilities.message_str, responseString).equals("SESSION_OUT")) {
                        generalFunc.notifySessionTimeOut();
                        Utils.runGC();
                        return;
                    }

                    cabTypeList.clear();
                    rentalTypeList.clear();
                    JSONArray messageArray = generalFunc.getJsonArray(CommonUtilities.message_str, responseString);


                    for (int i = 0; i < messageArray.length(); i++) {
                        HashMap<String, String> vehicleTypeMap = new HashMap<String, String>();
                        JSONObject tempObj = generalFunc.getJsonObject(messageArray, i);


                        vehicleTypeMap.put("iVehicleTypeId", generalFunc.getJsonValue("iVehicleTypeId", tempObj.toString()));
                        vehicleTypeMap.put("vVehicleTypeName", generalFunc.getJsonValue("vVehicleTypeName", tempObj.toString()));
                        vehicleTypeMap.put("vRentalVehicleTypeName", generalFunc.getJsonValue("vRentalVehicleTypeName", tempObj.toString()));
                        vehicleTypeMap.put("vLogo", generalFunc.getJsonValue("vLogo", tempObj.toString()));
                        vehicleTypeMap.put("vLogo1", generalFunc.getJsonValue("vLogo1", tempObj.toString()));
                        if (distance != null && time != null) {
                            vehicleTypeMap.put("SubTotal", generalFunc.getJsonValue("SubTotal", tempObj.toString()));
                        } else {
                            vehicleTypeMap.put("SubTotal", generalFunc.getJsonValue("SubTotal", 0 + ""));
                        }
                        vehicleTypeMap.put("iPersonSize", generalFunc.getJsonValue("iPersonSize", tempObj.toString()));
                        vehicleTypeMap.put("eFlatTrip", generalFunc.getJsonValue("eFlatTrip", tempObj.toString()));
                        vehicleTypeMap.put("eRental", generalFunc.getJsonValue("eRental", tempObj.toString()));
                        String eRental = generalFunc.getJsonValue("eRental", tempObj.toString());

                        if (cabTypeList.size() == 0) {
                            vehicleTypeMap.put("isHover", "true");
                        } else {
                            vehicleTypeMap.put("isHover", "false");
                        }
                        vehicleTypeMap.put("eRental", "No");

                        // if (eRental != null && !eRental.equalsIgnoreCase("") && eRental.equalsIgnoreCase("No")) {
                        cabTypeList.add(vehicleTypeMap);
                        //  }
                        if (eRental != null && eRental.equalsIgnoreCase("Yes")) {
                            HashMap<String, String> rentalVehicleTypeMap = (HashMap<String, String>) vehicleTypeMap.clone();
                            rentalVehicleTypeMap.put("SubTotal", generalFunc.getJsonValue("RentalSubtotal", tempObj.toString()));
                            rentalVehicleTypeMap.put("vRentalVehicleTypeName", generalFunc.getJsonValue("vRentalVehicleTypeName", tempObj.toString()));
                            rentalVehicleTypeMap.put("eRental", "Yes");
                            rentalTypeList.add(rentalVehicleTypeMap);
                        }
                        if (cabTypeList.size() > 0) {
                            SelectedCarTypeID = cabTypeList.get(0).get("iVehicleTypeId");
                        }
                        if (i == 0) {
                            if (adapter != null) {
                                adapter.setSelectedVehicleTypeId(SelectedCarTypeID);
                            }
                        }
                    }

                    if (rentalTypeList.size() > 0) {
                        rentalPkg.setVisibility(View.VISIBLE);
                        rentPkgImage.setVisibility(View.VISIBLE);
                        rentalarea.setVisibility(View.VISIBLE);
                        rentBackPkgImage.setVisibility(View.VISIBLE);

                        Runnable r = new Runnable() {

                            @Override
                            public void run() {
                                try {
                                    mainAct.setPanelHeight(270);
                                } catch (Exception e2) {
                                    new Handler().postDelayed(this, 20);
                                }
                            }
                        };
                        new Handler().postDelayed(r, 20);

                        setShadow();
                    }
                    setadpterData();

                }
            }
        });
        exeWebServer.execute();

    }

    public void callStartTrip() {

        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "StartHailTrip");
        parameters.put("iDriverId", generalFunc.getMemberId());
        parameters.put("UserType", "Driver");
        parameters.put("SelectedCarTypeID", SelectedCarTypeID);
        parameters.put("DestLatitude", mainAct.destlat);
        parameters.put("DestLongitude", mainAct.destlong);
        parameters.put("DestAddress", mainAct.Destinationaddress);

        parameters.put("PickUpLatitude", "" + mainAct.userLocation.getLatitude());
        parameters.put("PickUpLongitude", "" + mainAct.userLocation.getLongitude());
        parameters.put("PickUpAddress", "" + mainAct.pickupaddress);
        if (!iRentalPackageId.equalsIgnoreCase("")) {
            parameters.put("iRentalPackageId", "" + iRentalPackageId);
        }

        if (istollIgnore) {
            tollamount = 0;
            tollskiptxt = "Yes";

        } else {
            tollskiptxt = "No";
        }
        parameters.put("fTollPrice", tollamount + "");
        parameters.put("eTollSkipped", tollskiptxt);
        parameters.put("vTollPriceCurrencyCode", tollcurrancy);

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActivity(), parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setCancelAble(false);

        exeWebServer.setDataResponseListener(responseString -> {

            ride_now_btn.setEnabled(true);

            if (responseString != null && !responseString.equals("")) {


                boolean isDataAvail = GeneralFunctions.checkDataAvail(CommonUtilities.action_str, responseString);
                String message = generalFunc.getJsonValue(CommonUtilities.message_str, responseString);
                if (isDataAvail) {
                    (new getUserData(generalFunc, getActContext())).getData();
                } else {

                    if (message.equalsIgnoreCase("DO_RESTART") ||
                            message.equalsIgnoreCase("LBL_SERVER_COMM_ERROR") ||
                            message.equalsIgnoreCase("GCM_FAILED") ||
                            message.equalsIgnoreCase("APNS_FAILED")) {
                        (new getUserData(generalFunc, getActContext())).getData();
                        return;
                    }

                    final GenerateAlertBox generateAlertBox = new GenerateAlertBox(getActContext());
                    generateAlertBox.setCancelable(false);
                    generateAlertBox.setContentMessage("", generalFunc.retrieveLangLBl("", message));
                    generateAlertBox.setBtnClickList(btn_id -> {
                        generateAlertBox.closeAlertBox();

                        if (btn_id == 1) {
                            callStartTrip();

                        } else if (btn_id == 0) {
                            generateAlertBox.closeAlertBox();

                        }
                    });

                    //generateAlertBox.setPositiveBtn(generalFunc.retrieveLangLBl("", "LBL_RETRY_TXT"));
                    generateAlertBox.setNegativeBtn(generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"));


                    generateAlertBox.showAlertBox();

                }

            }
        });
        exeWebServer.execute();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Utils.hideKeyboard(getActivity());
    }

    boolean isRental = false;

    int lstSelectpos = 0;

    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            int i = view.getId();

            if (i == ride_now_btn.getId()) {

                if (SelectedCarTypeID == null || SelectedCarTypeID.equalsIgnoreCase("") || SelectedCarTypeID.equalsIgnoreCase("0")) {
                    return;
                }

                if (isRouteFail) {
                    generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("Route not found", "LBL_DEST_ROUTE_NOT_FOUND"));
                    return;
                }

                if (cabTypeList.get(selpos).get("eRental") != null && !cabTypeList.get(selpos).get("eRental").equalsIgnoreCase("") &&
                        cabTypeList.get(selpos).get("eRental").equalsIgnoreCase("Yes")) {

                    Bundle bn = new Bundle();
                    bn.putString("address", mainAct.pickupaddress);
                    bn.putString("vVehicleType", cabTypeList.get(selpos).get("vRentalVehicleTypeName"));
                    bn.putString("iVehicleTypeId", cabTypeList.get(selpos).get("iVehicleTypeId"));
                    bn.putString("vLogo", cabTypeList.get(selpos).get("vLogo1"));
                    // bn.putString("eta", etaTxt.getText().toString());


                    new StartActProcess(getActContext()).startActForResult(
                            RentalDetailsActivity.class, bn, RENTAL_REQ_CODE);
                    return;
                }

                final GenerateAlertBox generateAlert = new GenerateAlertBox(getActContext());
                generateAlert.setCancelable(false);
                generateAlert.setBtnClickList(btn_id -> {
                    if (btn_id == 0) {
                        generateAlert.closeAlertBox();
                    } else {
                        generateAlert.closeAlertBox();
                        checkSurgePrice();
                    }
                });
                generateAlert.setContentMessage("", generalFunc.retrieveLangLBl("", "LBL_CONFIRM_START_TRIP_TXT"));
                generateAlert.setPositiveBtn(generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"));
                generateAlert.setNegativeBtn(generalFunc.retrieveLangLBl("", "LBL_CANCEL_TXT"));
                generateAlert.showAlertBox();


            } else if (i == R.id.paymentArea) {
                if (payTypeSelectArea.getVisibility() == View.VISIBLE) {
                    hidePayTypeSelectionArea();
                } else {
                    if (generalFunc.getJsonValue("APP_PAYMENT_MODE", userProfileJson).equalsIgnoreCase("Cash-Card")) {
                        mainAct.setPanelHeight(283);
                        payTypeSelectArea.setVisibility(View.VISIBLE);
                        cashcardarea.setVisibility(View.GONE);
                    } else {
                        mainAct.setPanelHeight(283 - 48);
                    }
                }

            } else if (i == R.id.promoArea) {
                showPromoBox();
            } else if (i == R.id.cardarea) {
                hidePayTypeSelectionArea();
                setCashSelection();
                checkCardConfig();
            } else if (i == R.id.casharea) {
                hidePayTypeSelectionArea();
                setCashSelection();
            } else if (i == R.id.rentalBackImage) {
                selpos = 0;
                iRentalPackageId = "";
                cabTypeList = (ArrayList<HashMap<String, String>>) tempCabTypeList.clone();
                tempCabTypeList.clear();
                tempCabTypeList = (ArrayList<HashMap<String, String>>) cabTypeList.clone();
                isRental = false;
                adapter.setSelectedVehicleTypeId(cabTypeList.get(0).get("iVehicleTypeId"));
                SelectedCarTypeID = cabTypeList.get(0).get("iVehicleTypeId");
                adapter.setRentalItem(cabTypeList);
                adapter.notifyDataSetChanged();
                rentalBackImage.setVisibility(View.GONE);
                rentalPkgDesc.setVisibility(View.GONE);
                rentalinfoimage.setVisibility(View.GONE);
                rentalPkg.setVisibility(View.VISIBLE);
                rentBackPkgImage.setVisibility(View.VISIBLE);
                rentPkgImage.setVisibility(View.VISIBLE);
                rentalarea.setVisibility(View.VISIBLE);

                android.view.animation.Animation bottomUp = AnimationUtils.loadAnimation(getActContext(),
                        R.anim.slide_up_anim);
                carTypeRecyclerView.startAnimation(bottomUp);
                Runnable r = new Runnable() {

                    @Override
                    public void run() {
                        try {
                            mainAct.setPanelHeight(270);
                        } catch (Exception e2) {
                            new Handler().postDelayed(this, 20);
                        }
                    }
                };
                new Handler().postDelayed(r, 20);

            } else if (i == R.id.rentalPkg) {
                selpos = 0;
                iRentalPackageId = "";
                tempCabTypeList.clear();
                tempCabTypeList = (ArrayList<HashMap<String, String>>) cabTypeList.clone();
                cabTypeList.clear();
                cabTypeList = (ArrayList<HashMap<String, String>>) rentalTypeList.clone();
                adapter.setRentalItem(cabTypeList);
                isRental = true;
                adapter.setSelectedVehicleTypeId(cabTypeList.get(0).get("iVehicleTypeId"));
                SelectedCarTypeID = cabTypeList.get(0).get("iVehicleTypeId");
                adapter.notifyDataSetChanged();
                iRentalPackageId = "";


                rentalBackImage.setVisibility(View.VISIBLE);
                rentalPkgDesc.setVisibility(View.VISIBLE);
                rentalinfoimage.setVisibility(View.GONE);
                rentalPkg.setVisibility(View.GONE);
                rentBackPkgImage.setVisibility(View.GONE);
                rentPkgImage.setVisibility(View.GONE);

                android.view.animation.Animation bottomUp = AnimationUtils.loadAnimation(getActContext(),
                        R.anim.slide_up_anim);
                carTypeRecyclerView.startAnimation(bottomUp);

                Runnable r = new Runnable() {

                    @Override
                    public void run() {
                        try {
                            mainAct.setPanelHeight(260);
                        } catch (Exception e2) {
                            new Handler().postDelayed(this, 20);
                        }
                    }
                };
                new Handler().postDelayed(r, 20);
            } else if (i == rentPkgImage.getId()) {
                rentalPkg.performClick();

            }
        }
    }

    public void RentalTripHandle() {
        final GenerateAlertBox generateAlert = new GenerateAlertBox(getActContext());
        generateAlert.setCancelable(false);
        generateAlert.setBtnClickList(btn_id -> {
            if (btn_id == 0) {
                generateAlert.closeAlertBox();
                iRentalPackageId = "";
            } else {
                generateAlert.closeAlertBox();
                checkSurgePrice();
            }

        });
        generateAlert.setContentMessage("", generalFunc.retrieveLangLBl("", "LBL_CONFIRM_START_TRIP_TXT"));
        generateAlert.setPositiveBtn(generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"));
        generateAlert.setNegativeBtn(generalFunc.retrieveLangLBl("", "LBL_CANCEL_TXT"));
        generateAlert.showAlertBox();
    }
}
