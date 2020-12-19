package com.poupgo.driver;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.fragments.CabSelectionFragment;
import com.general.functions.GeneralFunctions;
import com.general.files.GetAddressFromLocation;
import com.general.files.GetLocationUpdates;
import com.general.functions.InternetConnection;
import com.utilities.general.files.StartActProcess;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.utils.CommonUtilities;
import com.general.functions.Utils;
import com.utilities.view.CreateRoundedView;
import com.general.functions.GenerateAlertBox;
import com.view.MTextView;
import com.utilities.view.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.Calendar;

public class HailActivity extends AppCompatActivity implements GetLocationUpdates.LocationUpdatesListener, OnMapReadyCallback, GoogleMap.OnCameraChangeListener, GetAddressFromLocation.AddressFound, GenerateAlertBox.HandleAlertBtnClick {

    public GeneralFunctions generalFunc;
    public Location userLocation;
    public String Destinationaddress = "";
    public String destlat = "";
    public String destlong = "";
    public String userProfileJson = "";
    public SlidingUpPanelLayout sliding_layout;
    public boolean iswallet = false;
    public ArrayList<String> cabTypesArrList = new ArrayList<>();
    public CabSelectionFragment cabSelectionFrag;
    public String pickupaddress = "";
    public boolean isAddressEnable;
    public Location destLocation;
    MTextView titleTxt;
    ImageView backImgView;
    GoogleMap gMap;
    SupportMapFragment map;
    LinearLayout destarea;
    MTextView destLocHTxt;
    MTextView destLocTxt;
    ImageView pinImgView;
    GetAddressFromLocation getAddressFromLocation;
    boolean isCashSelected = true;
    View imagemarkerdest2;
    boolean ispickup = false;
    ProgressBar progressBar;
    boolean isdstination = false;
    InternetConnection intCheck;
    GenerateAlertBox generateAlert;
    String alertType = "";
    public static int RENTAL_REQ_CODE = 1234;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hail);

        generalFunc = new GeneralFunctions(getActContext());
        map = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapV2);

        destLocation = new Location("dest");

        userProfileJson = generalFunc.retrieveValue(CommonUtilities.USER_PROFILE_JSON);

        titleTxt = (MTextView) findViewById(R.id.titleTxt);
        backImgView = (ImageView) findViewById(R.id.backImgView);
        backImgView.setOnClickListener(new setOnClickList());


        getAddressFromLocation = new GetAddressFromLocation(getActContext(), generalFunc);
        getAddressFromLocation.setAddressList(this);


        destarea = (LinearLayout) findViewById(R.id.destarea);
        destLocHTxt = (MTextView) findViewById(R.id.destLocHTxt);
        destarea.setOnClickListener(new setOnClickList());
        destLocTxt = (MTextView) findViewById(R.id.destLocTxt);
        pinImgView = (ImageView) findViewById(R.id.pinImgView);

        intCheck = new InternetConnection(this);

        imagemarkerdest2 = (View) findViewById(R.id.imagemarkerdest2);

        sliding_layout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);

        map.getMapAsync(HailActivity.this);

        setLabel();
        destarea.setEnabled(false);
        new CreateRoundedView(getActContext().getResources().getColor(R.color.pickup_req_later_btn), Utils.dipToPixels(getActContext(), 6), 2,
                getActContext().getResources().getColor(R.color.pickup_req_later_btn), imagemarkerdest2);
        progressBar = (ProgressBar) findViewById(R.id.mProgressBar);
        showprogress();


    }


    public void setDefaultAlertBtn() {
        generateAlert.resetBtn();
        generateAlert.setPositiveBtn(generalFunc.retrieveLangLBl("Retry", "LBL_RETRY_TXT"));
        generateAlert.setNegativeBtn(generalFunc.retrieveLangLBl("Cancel", "LBL_CANCEL_TXT"));
    }

    public void showprogress() {
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setIndeterminate(true);
        progressBar.getIndeterminateDrawable().setColorFilter(
                getActContext().getResources().getColor(R.color.appThemeColor_2), android.graphics.PorterDuff.Mode.SRC_IN);

    }

    public void hideprogress() {
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }


    public void setPanelHeight(int value) {

        Utils.printLog("setPanelHeight","::"+value);

        sliding_layout.setPanelHeight(Utils.dipToPixels(getActContext(),value));


    }

    public void OpenCardPaymentAct(boolean fromcabselection) {
        iswallet = true;
        Bundle bn = new Bundle();
        //  bn.putString("UserProfileJson", userProfileJson);
        bn.putBoolean("fromcabselection", fromcabselection);
        new StartActProcess(getActContext()).startActForResult(CardPaymentActivity.class, bn, Utils.CARD_PAYMENT_REQ_CODE);
    }

    public Context getActContext() {
        return HailActivity.this;
    }

    public void setLabel() {
        titleTxt.setText(generalFunc.retrieveLangLBl("Taxi Hail", "LBL_TAXI_HAIL"));
        destLocHTxt.setText(generalFunc.retrieveLangLBl("", "LBL_DROP_AT"));
        destLocTxt.setText(generalFunc.retrieveLangLBl("", "LBL_ADD_DESTINATION_BTN_TXT"));


    }

    public void setCashSelection(boolean isCashSelected) {
        this.isCashSelected = isCashSelected;
    }


    @Override
    public void onAddressFound(String address, double latitude, double longitude) {


        if (pickupaddress.equals("") || pickupaddress == null || pickupaddress.length() == 0) {
            destarea.setEnabled(true);
            pickupaddress = address;
            ispickup = false;
            hideprogress();

        }
        if (isdstination) {
            isdstination = false;

            destLocTxt.setText(address);
            Destinationaddress = address;
            destlat = latitude + "";
            destlong = longitude + "";

            destLocation.setLatitude(latitude);
            destLocation.setLongitude(longitude);

        }


    }

    @Override
    public void handleBtnClick(int btn_id) {

        if (btn_id == 1) {
            generateAlert.closeAlertBox();

        } else {
            generateAlert.closeAlertBox();
        }

    }

    @Override
    public void onLocationUpdate(Location location) {

        this.userLocation = location;
        if (pickupaddress.equals("") || pickupaddress == null || pickupaddress.length() == 0) {
            getAddressFromLocation.setLocation(location.getLatitude(), location.getLongitude());
            getAddressFromLocation.execute();
            ispickup = true;
        }

        if (!Destinationaddress.equals("") && Destinationaddress != null) {
            return;
        }

        CameraPosition cameraPosition = cameraForUserPosition();

        if (cameraPosition != null)
            getMap().moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


        String isGoOnline = generalFunc.retrieveValue(CommonUtilities.GO_ONLINE_KEY);

        if ((isGoOnline != null && !isGoOnline.equals("") && isGoOnline.equals("Yes"))) {
            long lastTripTime = generalFunc.parseLongValue(0, generalFunc.retrieveValue(CommonUtilities.LAST_FINISH_TRIP_TIME_KEY));
            long currentTime = Calendar.getInstance().getTimeInMillis();


            generalFunc.storedata(CommonUtilities.GO_ONLINE_KEY, "No");
            generalFunc.storedata(CommonUtilities.LAST_FINISH_TRIP_TIME_KEY, "0");

        }


    }


    public GoogleMap getMap() {
        return this.gMap;
    }

    public CameraPosition cameraForUserPosition() {
        double currentZoomLevel = getMap().getCameraPosition().zoom;

        if (Utils.defaultZomLevel > currentZoomLevel) {
            currentZoomLevel = Utils.defaultZomLevel;
        }
        if (userLocation != null) {
            CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(this.userLocation.getLatitude(), this.userLocation.getLongitude()))
                    .zoom((float) currentZoomLevel).build();

            return cameraPosition;
        } else {
            return null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (GetLocationUpdates.retrieveInstance() != null) {
            GetLocationUpdates.getInstance().startLocationUpdates(this, this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (GetLocationUpdates.retrieveInstance() != null) {
            GetLocationUpdates.getInstance().stopLocationUpdates(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeLocationUpdates();
    }

    public void removeLocationUpdates() {
        if (GetLocationUpdates.retrieveInstance() != null) {
            GetLocationUpdates.getInstance().stopLocationUpdates(this);
        }

        if (getAddressFromLocation != null) {
            getAddressFromLocation.setAddressList(null);
            getAddressFromLocation = null;
        }

        if (gMap != null) {
            this.gMap.setOnCameraChangeListener(null);
            this.gMap = null;
        }

        this.userLocation = null;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.gMap = googleMap;

        if (generalFunc.checkLocationPermission(true) == true) {
            getMap().setMyLocationEnabled(true);
            getMap().setPadding(0, 0, 0, Utils.dipToPixels(getActContext(), 15));
            getMap().getUiSettings().setTiltGesturesEnabled(false);
            getMap().getUiSettings().setZoomControlsEnabled(false);
            getMap().getUiSettings().setCompassEnabled(false);
            getMap().getUiSettings().setMyLocationButtonEnabled(false);
        }


        getMap().setOnCameraChangeListener(this);

        getMap().setOnMarkerClickListener(marker -> {
            marker.hideInfoWindow();
            return true;
        });

        if (GetLocationUpdates.retrieveInstance() != null) {
            GetLocationUpdates.getInstance().stopLocationUpdates(this);
        }

        GetLocationUpdates.getInstance().startLocationUpdates(this, this);

    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Utils.PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == HailActivity.RESULT_OK) {

                isdstination = true;
                Place place = PlaceAutocomplete.getPlace(getActContext(), data);

                LatLng placeLocation = place.getLatLng();
                Destinationaddress = place.getAddress().toString();
                destlat = placeLocation.latitude + "";
                destlong = placeLocation.longitude + "";
                destLocation.setLatitude(placeLocation.latitude);
                destLocation.setLongitude(placeLocation.longitude);


                gMap.setOnCameraChangeListener(new onGoogleMapCameraChangeList());
                CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(placeLocation, 14.0f);
                addcabselectionFragment();

                if (gMap != null) {
                    gMap.clear();
                    gMap.moveCamera(cu);
                }
                pinImgView.setVisibility(View.VISIBLE);
                destLocTxt.setText(place.getAddress().toString());


            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(getActContext(), data);


                generalFunc.showMessage(generalFunc.getCurrentView(HailActivity.this),
                        status.getStatusMessage());
            } else if (requestCode == HailActivity.RESULT_CANCELED) {

            }
        } else if (requestCode == Utils.SEARCH_DEST_LOC_REQ_CODE) {

            if (resultCode == RESULT_OK && data != null && gMap != null) {

                isdstination = true;
                isAddressEnable = true;
                LatLng placeLocation = new LatLng(generalFunc.parseDoubleValue(0.0, data.getStringExtra("Latitude")), generalFunc.parseDoubleValue(0.0, data.getStringExtra("Longitude")));
                destlat = data.getStringExtra("Latitude");
                destlong = data.getStringExtra("Longitude");


                Destinationaddress = data.getStringExtra("Address");
                destLocTxt.setText(data.getStringExtra("Address"));


                gMap.setOnCameraChangeListener(new onGoogleMapCameraChangeList());
                CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(placeLocation, 14.0f);

                addcabselectionFragment();
                if (gMap != null) {
                    gMap.clear();
                    gMap.moveCamera(cu);
                }

                destlat = data.getStringExtra("Latitude");
                destlong = data.getStringExtra("Longitude");
                pinImgView.setVisibility(View.VISIBLE);


            }
        } else if (requestCode == RENTAL_REQ_CODE) {

            if (resultCode == RESULT_OK) {

                if (cabSelectionFrag != null) {
                    if (data != null && data.getStringExtra("iRentalPackageId") != null)
                        cabSelectionFrag.iRentalPackageId = data.getStringExtra("iRentalPackageId");
                    cabSelectionFrag.RentalTripHandle();
                }
            }

        }


    }

    public void addcabselectionFragment() {


        if (cabSelectionFrag == null) {
            cabSelectionFrag = new CabSelectionFragment();
            Bundle bundle = new Bundle();
            cabSelectionFrag.setArguments(bundle);
            pinImgView.setVisibility(View.VISIBLE);


        }

        if (cabSelectionFrag != null) {
            if (cabSelectionFrag.rentalPkgDesc != null) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        cabSelectionFrag.rentalPkgDesc.setVisibility(View.GONE);

                        if (cabSelectionFrag.rentalBackImage != null) {
                            cabSelectionFrag.rentalBackImage.setVisibility(View.GONE);
                        }
                    }
                });

            }
        }

        super.onPostResume();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.dragView, cabSelectionFrag).commit();

    }

    public class onGoogleMapCameraChangeList implements GoogleMap.OnCameraChangeListener {

        @Override
        public void onCameraChange(CameraPosition cameraPosition) {

            Utils.printLog("Camera", "changed");
            LatLng center = gMap.getCameraPosition().target;


            if (!isAddressEnable) {
                getAddressFromLocation.setLocation(center.latitude, center.longitude);
                getAddressFromLocation.execute();
                destLocTxt.setText(generalFunc.retrieveLangLBl("", "LBL_SELECTING_LOCATION_TXT"));
                destlat = center.latitude + "";
                destlong = center.longitude + "";
            } else {
                isAddressEnable = false;
            }

            if (cabSelectionFrag != null) {
                isdstination = true;
                showprogress();
                cabSelectionFrag.findRoute();
            }
        }
    }

    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            int id = view.getId();
            Utils.hideKeyboard(HailActivity.this);
            switch (view.getId()) {
                case R.id.backImgView:
                    onBackPressed();
                    break;
            }

            if (id == destarea.getId()) {

                if (userLocation == null) {
                    return;
                }
                Bundle bn = new Bundle();
                bn.putString("locationArea", "dest");
                bn.putDouble("lat", userLocation.getLatitude());
                bn.putDouble("long", userLocation.getLongitude());
                new StartActProcess(getActContext()).startActForResult(SearchLocationActivity.class, bn, Utils.SEARCH_DEST_LOC_REQ_CODE);
            }
        }
    }
}
