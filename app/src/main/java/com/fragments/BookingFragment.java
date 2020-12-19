package com.fragments;


import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.adapter.files.MyBookingsRecycleAdapter;
import com.general.files.getUserData;
import com.poupgo.driver.HistoryActivity;
import com.poupgo.driver.R;
import com.general.files.ExecuteWebServerUrl;
import com.general.functions.GeneralFunctions;
import com.utils.CommonUtilities;
import com.general.functions.Utils;
import com.utilities.view.ErrorView;
import com.general.functions.GenerateAlertBox;
import com.view.MTextView;
import com.view.editBox.MaterialEditText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class BookingFragment extends Fragment implements MyBookingsRecycleAdapter.OnItemClickListener {


    View view;

    ProgressBar loading_my_bookings;
    MTextView noRidesTxt;

    RecyclerView myBookingsRecyclerView;
    ErrorView errorView;

    MyBookingsRecycleAdapter myBookingsRecyclerAdapter;

    ArrayList<HashMap<String, String>> list;

    boolean mIsLoading = false;
    boolean isNextPageAvailable = false;

    String next_page_str = "";

    GeneralFunctions generalFunc;

    HistoryActivity myBookingAct;
    String type = "";
    String userProfileJson = "";
    String app_type = "";
    String APP_TYPE = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_booking, container, false);

        loading_my_bookings = (ProgressBar) view.findViewById(R.id.loading_my_bookings);
        noRidesTxt = (MTextView) view.findViewById(R.id.noRidesTxt);
        myBookingsRecyclerView = (RecyclerView) view.findViewById(R.id.myBookingsRecyclerView);
        errorView = (ErrorView) view.findViewById(R.id.errorView);
        myBookingAct = (HistoryActivity) getActivity();
        generalFunc = myBookingAct.generalFunc;
        type = getArguments().getString("type");
        list = new ArrayList<>();
        myBookingsRecyclerAdapter = new MyBookingsRecycleAdapter(getActContext(), list, type, generalFunc, false);
        myBookingsRecyclerView.setAdapter(myBookingsRecyclerAdapter);
        myBookingsRecyclerAdapter.setOnItemClickListener(this);
        userProfileJson = generalFunc.retrieveValue(CommonUtilities.USER_PROFILE_JSON);
        app_type = generalFunc.getJsonValue("APP_TYPE", userProfileJson);
        APP_TYPE = generalFunc.getJsonValue("APP_TYPE", generalFunc.retrieveValue(CommonUtilities.USER_PROFILE_JSON));


        myBookingsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int visibleItemCount = recyclerView.getLayoutManager().getChildCount();
                int totalItemCount = recyclerView.getLayoutManager().getItemCount();
                int firstVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();

                int lastInScreen = firstVisibleItemPosition + visibleItemCount;
                if ((lastInScreen == totalItemCount) && !(mIsLoading) && isNextPageAvailable == true) {

                    mIsLoading = true;
                    myBookingsRecyclerAdapter.addFooterView();


                    getBookingsHistory(true);


                } else if (isNextPageAvailable == false) {
                    myBookingsRecyclerAdapter.removeFooterView();
                }
            }
        });

        getBookingsHistory(false);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Utils.printLog("BookingFragment", "::onresume called");
        getBookingsHistory(false);
    }

    public boolean isDeliver() {
        if (getArguments().getString("BOOKING_TYPE").equals(Utils.CabGeneralType_Deliver)) {
            return true;
        }
        return false;
    }

    @Override
    public void onCancelBookingClickList(View v, int position) {


        confirmCancelBooking(list.get(position).get("iCabBookingId"), list.get(position));

    }

    @Override
    public void onTripStartClickList(View v, int position) {
        String contentMsg = "";


        if (list.get(position).get("eTypeVal").equalsIgnoreCase(Utils.CabGeneralType_UberX)) {
            contentMsg = generalFunc.retrieveLangLBl("", "LBL_CONFIRM_START_JOB");
        } else if (list.get(position).get("eTypeVal").equalsIgnoreCase(Utils.CabGeneralType_Ride)) {
            contentMsg = generalFunc.retrieveLangLBl("", "LBL_CONFIRM_START_TRIP_TXT");
        } else {
            contentMsg = generalFunc.retrieveLangLBl("", "LBL_CONFIRM_START_DELIVERY");

        }

        buildMsgOnStartTripBtn(list.get(position).get("iCabBookingId"), contentMsg);
    }

    public void confirmCancelBooking(final String iCabBookingId, HashMap<String, String> list) {
        final android.support.v7.app.AlertDialog alertDialog;
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActContext());
        if (type.equalsIgnoreCase("Pending")) {

            builder.setTitle(generalFunc.retrieveLangLBl("Decline Job", "LBL_DECLINE_BOOKING"));

        } else {
            if (list.get("eTypeVal").equalsIgnoreCase(Utils.CabGeneralType_Ride)) {
                builder.setTitle(generalFunc.retrieveLangLBl("Cancel Booking", "LBL_CANCEL_TRIP"));
            } else if (list.get("eTypeVal").equalsIgnoreCase(Utils.CabGeneralType_UberX)) {
                builder.setTitle(generalFunc.retrieveLangLBl("", "LBL_CANCEL_JOB"));
            } else {
                builder.setTitle(generalFunc.retrieveLangLBl("", "LBL_CANCEL_DELIVERY"));
            }

        }

        LayoutInflater inflater = (LayoutInflater) getActContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.input_box_view, null);


        final MaterialEditText reasonBox = (MaterialEditText) dialogView.findViewById(R.id.editBox);

        reasonBox.setSingleLine(false);
        reasonBox.setMaxLines(5);

        reasonBox.setBothText(generalFunc.retrieveLangLBl("Reason", "LBL_REASON"), generalFunc.retrieveLangLBl("Enter your reason", "LBL_ENTER_REASON"));


        builder.setView(dialogView);
        builder.setPositiveButton(generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setNegativeButton(generalFunc.retrieveLangLBl("", "LBL_CANCEL_TXT"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        alertDialog = builder.create();
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Utils.checkText(reasonBox) == false) {
                    reasonBox.setError(generalFunc.retrieveLangLBl("", "LBL_FEILD_REQUIRD_ERROR_TXT"));
                    return;
                }

                alertDialog.dismiss();

                if (list.get("eTypeVal").equalsIgnoreCase(Utils.CabGeneralType_UberX)) {
                    if (type.equalsIgnoreCase("Pending")) {
                        declineBooking(iCabBookingId, Utils.getText(reasonBox));
                    } else {
                        cancelBooking(iCabBookingId, Utils.getText(reasonBox), true);
                    }
                } else {
                    cancelBooking(iCabBookingId, Utils.getText(reasonBox), false);

                }

            }
        });

        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
    }

    public void acceptBooking(String iCabBookingId, String eConfirmByProvider) {

        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "UpdateBookingStatus");
        parameters.put("UserType", CommonUtilities.app_type);
        parameters.put("iDriverId", generalFunc.getMemberId());
        parameters.put("iCabBookingId", iCabBookingId);
        parameters.put("eStatus", "Accepted");
        parameters.put("eConfirmByProvider", eConfirmByProvider);

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setDataResponseListener(responseString -> {

            if (responseString != null && !responseString.equals("")) {

                boolean isDataAvail = GeneralFunctions.checkDataAvail(CommonUtilities.action_str, responseString);

                if (isDataAvail == true) {
                    list.clear();
                    myBookingsRecyclerAdapter.notifyDataSetChanged();


                    final GenerateAlertBox generateAlert = new GenerateAlertBox(getActContext());
                    generateAlert.setCancelable(false);
                    generateAlert.setBtnClickList(btn_id -> {
                        generateAlert.closeAlertBox();

                        getBookingsHistory(false);
                    });
                    generateAlert.setContentMessage("", generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(CommonUtilities.message_str, responseString)));
                    generateAlert.setPositiveBtn(generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"));

                    generateAlert.showAlertBox();
                } else {

                    String BookingFound = generalFunc.getJsonValue("BookingFound", responseString);

                    if (BookingFound.equalsIgnoreCase("Yes")) {

                        GenerateAlertBox alertBox = new GenerateAlertBox(getActContext());
                        alertBox.setCancelable(false);
                        alertBox.setContentMessage("", generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(CommonUtilities.message_str, responseString)));
                        alertBox.setPositiveBtn(generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"));
                        alertBox.setNegativeBtn(generalFunc.retrieveLangLBl("", "LBL_CANCEL_TXT"));
                        alertBox.setBtnClickList(new GenerateAlertBox.HandleAlertBtnClick() {
                            @Override
                            public void handleBtnClick(int btn_id) {
                                if (btn_id == 0) {
                                    alertBox.closeAlertBox();
                                } else if (btn_id == 1) {
                                    acceptBooking(iCabBookingId, "Yes");
                                    alertBox.closeAlertBox();
                                }
                            }
                        });
                        alertBox.showAlertBox();
                    } else {
                        generalFunc.showGeneralMessage("",
                                generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(CommonUtilities.message_str, responseString)));
                    }
                }

            } else {
                generalFunc.showError();
            }
        });
        exeWebServer.execute();
    }

    public void declineBooking(String iCabBookingId, String reason) {

        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "UpdateBookingStatus");
        parameters.put("UserType", CommonUtilities.app_type);
        parameters.put("iUserId", generalFunc.getMemberId());
        parameters.put("iCabBookingId", iCabBookingId);
        parameters.put("vCancelReason", reason);
        parameters.put("eStatus", "Declined");

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setDataResponseListener(responseString -> {

            if (responseString != null && !responseString.equals("")) {

                boolean isDataAvail = GeneralFunctions.checkDataAvail(CommonUtilities.action_str, responseString);

                if (isDataAvail == true) {
                    list.clear();
                    myBookingsRecyclerAdapter.notifyDataSetChanged();


                    final GenerateAlertBox generateAlert = new GenerateAlertBox(getActContext());
                    generateAlert.setCancelable(false);
                    generateAlert.setBtnClickList(btn_id -> {
                        generateAlert.closeAlertBox();
                        getBookingsHistory(false);
                    });
                    generateAlert.setContentMessage("", generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(CommonUtilities.message_str, responseString)));
                    generateAlert.setPositiveBtn(generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"));

                    generateAlert.showAlertBox();
                } else {
                    generalFunc.showGeneralMessage("",
                            generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(CommonUtilities.message_str, responseString)));
                }

            } else {
                generalFunc.showError();
            }
        });
        exeWebServer.execute();
    }

    public void cancelBooking(String iCabBookingId, String reason, boolean isUfx) {

        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "cancelBooking");
        parameters.put("UserType", CommonUtilities.app_type);
        parameters.put("iUserId", generalFunc.getMemberId());
        parameters.put("iCabBookingId", iCabBookingId);
        parameters.put("Reason", reason);
        if (!isUfx) {
            parameters.put("DataType", "PENDING");

        }

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setDataResponseListener(responseString -> {

            if (responseString != null && !responseString.equals("")) {

                boolean isDataAvail = GeneralFunctions.checkDataAvail(CommonUtilities.action_str, responseString);

                if (isDataAvail == true) {
                    list.clear();
                    myBookingsRecyclerAdapter.notifyDataSetChanged();
                    getBookingsHistory(false);
                    generalFunc.showGeneralMessage("",
                            generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(CommonUtilities.message_str, responseString)));
                } else {
                    generalFunc.showGeneralMessage("",
                            generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(CommonUtilities.message_str, responseString)));
                }

            } else {
                generalFunc.showError();
            }
        });
        exeWebServer.execute();
    }

    public void buildMsgOnStartTripBtn(final String iCabBookingId, String contentMsg) {
        final GenerateAlertBox generateAlert = new GenerateAlertBox(getActContext());
        generateAlert.setCancelable(false);
        generateAlert.setBtnClickList(btn_id -> {
            if (btn_id == 0) {
                generateAlert.closeAlertBox();
            } else {
                if (type.equalsIgnoreCase("Pending")) {
                    acceptBooking(iCabBookingId, "No");
                } else {
                    startTrip(iCabBookingId);
                }
            }

        });
        if (type.equalsIgnoreCase("Pending")) {

            generateAlert.setContentMessage("", generalFunc.retrieveLangLBl("Are you sure? You want to accept this job.", "LBL_CONFIRM_ACCEPT_JOB"));
        } else {

            generateAlert.setContentMessage("", contentMsg);
        }
        generateAlert.setPositiveBtn(generalFunc.retrieveLangLBl("", "LBL_BTN_YES_TXT"));
        generateAlert.setNegativeBtn(generalFunc.retrieveLangLBl("", "LBL_BTN_NO_TXT"));
        generateAlert.showAlertBox();
    }

    public void startTrip(String iCabBookingId) {

        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "GenerateTrip");
        parameters.put("UserType", CommonUtilities.app_type);
        parameters.put("DriverID", generalFunc.getMemberId());
        parameters.put("iCabBookingId", iCabBookingId);
        parameters.put("GoogleServerKey", generalFunc.retrieveValue(CommonUtilities.GOOGLE_SERVER_ANDROID_DRIVER_APP_KEY));

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setDataResponseListener(responseString -> {

            Utils.printLog("Response", "::" + responseString);

            if (responseString != null && !responseString.equals("")) {

                boolean isDataAvail = GeneralFunctions.checkDataAvail(CommonUtilities.action_str, responseString);

                if (isDataAvail == true) {
                    (new getUserData(generalFunc, getActContext())).getData();
                } else {
                    String message = generalFunc.getJsonValue(CommonUtilities.message_str, responseString);

                    if(message.equalsIgnoreCase("DO_RESTART")){
                        (new getUserData(generalFunc, getActContext())).getData();
                        return;
                    }

                    if(generalFunc.getJsonValue("DO_RELOAD", responseString).equalsIgnoreCase("YES")){
                        generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", message), generalFunc.retrieveLangLBl("Ok", "LBL_BTN_OK_TXT"), "", buttonId -> {

                            list.clear();
                            myBookingsRecyclerAdapter.notifyDataSetChanged();
                            getBookingsHistory(false);

                        });
                        return;
                    }

                    generalFunc.showGeneralMessage("",
                            generalFunc.retrieveLangLBl("", message));
                }

            } else {
                generalFunc.showError();
            }
        });
        exeWebServer.execute();
    }


    public void getBookingsHistory(final boolean isLoadMore) {
        if (errorView != null) {
            if (errorView.getVisibility() == View.VISIBLE) {
                errorView.setVisibility(View.GONE);
            }
        }

        if (loading_my_bookings != null) {
            if (loading_my_bookings.getVisibility() != View.VISIBLE && isLoadMore == false) {
                loading_my_bookings.setVisibility(View.VISIBLE);
            }
        }

        final HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "checkBookings");
        parameters.put("iDriverId", generalFunc.getMemberId());
        parameters.put("UserType", CommonUtilities.app_type);
        parameters.put("bookingType", getArguments().getString("BOOKING_TYPE"));
        parameters.put("DataType", type);
        if (isLoadMore == true) {
            parameters.put("page", next_page_str);
        }

        noRidesTxt.setVisibility(View.GONE);

        final ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setDataResponseListener(responseString -> {

            noRidesTxt.setVisibility(View.GONE);

            Utils.printLog("responseString", ":" + responseString);

            if (responseString != null && !responseString.equals("")) {

                closeLoader();

                if (generalFunc.checkDataAvail(CommonUtilities.action_str, responseString) == true) {

                    list.clear();
                    String nextPage = generalFunc.getJsonValue("NextPage", responseString);
                    JSONArray arr_rides = generalFunc.getJsonArray(CommonUtilities.message_str, responseString);

                    if (arr_rides != null && arr_rides.length() > 0) {
                        for (int i = 0; i < arr_rides.length(); i++) {
                            JSONObject obj_temp = generalFunc.getJsonObject(arr_rides, i);
                            HashMap<String, String> map = new HashMap<String, String>();
                            map.put("dBooking_date", generalFunc.getJsonValueStr("dBooking_date", obj_temp));
                            map.put("vSourceAddresss", generalFunc.getJsonValueStr("vSourceAddresss", obj_temp));
                            map.put("tDestAddress", generalFunc.getJsonValueStr("tDestAddress", obj_temp));
                            map.put("vBookingNo", generalFunc.getJsonValueStr("vBookingNo", obj_temp));
                            map.put("eStatus", generalFunc.getJsonValueStr("eStatus", obj_temp));
                            map.put("iCabBookingId", generalFunc.getJsonValueStr("iCabBookingId", obj_temp));
                            map.put("dBooking_dateOrig", generalFunc.getJsonValueStr("dBooking_dateOrig", obj_temp));

                            if (generalFunc.getJsonValueStr("selectedtime", obj_temp) != null) {
                                map.put("selectedtime", generalFunc.getJsonValueStr("selectedtime", obj_temp));
                            }

                            map.put("eTypeVal",generalFunc.getJsonValueStr("eType", obj_temp));
                            if (generalFunc.getJsonValueStr("eType", obj_temp).equalsIgnoreCase("deliver")) {
                                map.put("eType", generalFunc.retrieveLangLBl("Delivery", "LBL_DELIVERY"));
                            } else if (generalFunc.getJsonValueStr("eType", obj_temp).equalsIgnoreCase(Utils.CabGeneralType_Ride)) {
                                map.put("eType", generalFunc.retrieveLangLBl("Delivery", "LBL_RIDE"));
                            } else {

                                map.put("eType", generalFunc.retrieveLangLBl("", "LBL_SERVICES"));
                            }

                            map.put("eFareType", generalFunc.getJsonValueStr("eFareType", obj_temp));
                            map.put("appType", APP_TYPE);

                            if (map.get("eStatus").equals("Completed")) {
                                map.put("eStatus", generalFunc.retrieveLangLBl("", "LBL_ASSIGNED"));
                            } else if (map.get("eStatus").equals("Cancel")) {
                                map.put("eStatus", generalFunc.retrieveLangLBl("", "LBL_CANCELLED"));
                            }

                            if (generalFunc.getJsonValueStr("eCancelBy", obj_temp).equals("Driver")) {
                                map.put("eStatus", generalFunc.retrieveLangLBl("", "LBL_CANCELLED_BY_DRIVER"));
                            }

                            if (generalFunc.getJsonValueStr("eType", obj_temp).equalsIgnoreCase(Utils.CabGeneralType_Ride)) {
                                map.put("LBL_BOOKING_NO", generalFunc.retrieveLangLBl("", "LBL_BOOKING"));
                                map.put("LBL_START_TRIP", generalFunc.retrieveLangLBl("", "LBL_BEGIN_TRIP"));
                                map.put("LBL_CANCEL_TRIP", generalFunc.retrieveLangLBl("", "LBL_CANCEL_TRIP"));
                                map.put("LBL_PICK_UP_LOCATION", generalFunc.retrieveLangLBl("", "LBL_PICK_UP_LOCATION"));
                                map.put("LBL_DEST_LOCATION", generalFunc.retrieveLangLBl("", "LBL_DEST_LOCATION"));


                            } else {

                                map.put("LBL_BOOKING_NO", generalFunc.retrieveLangLBl("Delivery No", "LBL_DELIVERY_NO"));
                                map.put("LBL_START_TRIP", generalFunc.retrieveLangLBl("Start Delivery", "LBL_BEGIN_DELIVERY"));
                                map.put("LBL_CANCEL_TRIP", generalFunc.retrieveLangLBl("Cancel Delivery", "LBL_CANCEL_DELIVERY"));
                                map.put("LBL_PICK_UP_LOCATION", generalFunc.retrieveLangLBl("Sender Location", "LBL_SENDER_LOCATION"));
                                map.put("LBL_DEST_LOCATION", generalFunc.retrieveLangLBl("Receiver's Location", "LBL_RECEIVER_LOCATION"));
                            }


                            if (generalFunc.getJsonValueStr("eType", obj_temp).equalsIgnoreCase(Utils.CabGeneralType_UberX)) {
                                map.put("LBL_ACCEPT_JOB", generalFunc.retrieveLangLBl("Accept Job", "LBL_ACCEPT_JOB"));
                                map.put("LBL_DECLINE_JOB", generalFunc.retrieveLangLBl("Decline job", "LBL_DECLINE_JOB"));
                                map.put("LBL_START_TRIP", generalFunc.retrieveLangLBl("", "LBL_BEGIN_JOB"));
                                map.put("LBL_CANCEL_TRIP", generalFunc.retrieveLangLBl("Cancel job", "LBL_CANCEL_JOB"));

                                map.put("SelectedCategory", generalFunc.getJsonValueStr("SelectedCategory", obj_temp));
                                map.put("SelectedVehicle", generalFunc.getJsonValueStr("SelectedVehicle", obj_temp));
                                map.put("LBL_PICK_UP_LOCATION", generalFunc.retrieveLangLBl("", "LBL_JOB_LOCATION_TXT"));
                                map.put("LBL_DEST_LOCATION", generalFunc.retrieveLangLBl("", "LBL_DEST_LOCATION"));


                            } else {
                                map.put("SelectedCategory", generalFunc.getJsonValueStr("vVehicleType", obj_temp));
                            }


                            map.put("LBL_Status", generalFunc.retrieveLangLBl("", "LBL_Status"));
                            map.put("JSON", obj_temp.toString());

                            map.put("LBL_JOB_LOCATION_TXT", generalFunc.retrieveLangLBl("", "LBL_JOB_LOCATION_TXT"));


                            list.add(map);

                        }
                    }

                    if (!nextPage.equals("") && !nextPage.equals("0")) {
                        next_page_str = nextPage;
                        isNextPageAvailable = true;
                    } else {
                        removeNextPageConfig();
                    }

                    myBookingsRecyclerAdapter.notifyDataSetChanged();

                } else {
                    if (list.size() == 0) {
                        removeNextPageConfig();
                        noRidesTxt.setText(generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(CommonUtilities.message_str, responseString)));
                        noRidesTxt.setVisibility(View.VISIBLE);
                    }
                }
            } else {
                if (isLoadMore == false) {
                    removeNextPageConfig();
                    generateErrorView();
                }

            }

            mIsLoading = false;
        });
        exeWebServer.execute();
    }

    public void removeNextPageConfig() {
        next_page_str = "";
        isNextPageAvailable = false;
        mIsLoading = false;
        myBookingsRecyclerAdapter.removeFooterView();
    }

    public void closeLoader() {
        if (loading_my_bookings.getVisibility() == View.VISIBLE) {
            loading_my_bookings.setVisibility(View.GONE);
        }
    }

    public void generateErrorView() {

        closeLoader();

        generalFunc.generateErrorView(errorView, "LBL_ERROR_TXT", "LBL_NO_INTERNET_TXT");

        if (errorView.getVisibility() != View.VISIBLE) {
            errorView.setVisibility(View.VISIBLE);
        }
        errorView.setOnRetryListener(new ErrorView.RetryListener() {
            @Override
            public void onRetry() {
                getBookingsHistory(false);
            }
        });
    }

    public Context getActContext() {
        return myBookingAct.getActContext();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Utils.hideKeyboard(getActivity());
    }
}
