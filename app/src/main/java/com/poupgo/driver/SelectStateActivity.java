package com.poupgo.driver;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.general.files.ExecuteWebServerUrl;
import com.general.functions.GeneralFunctions;
import com.general.functions.Utils;
import com.utilities.view.ErrorView;
import com.utils.CommonUtilities;
import com.view.MTextView;
import com.view.StateListItem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class SelectStateActivity extends AppCompatActivity {
    ArrayList<StateListItem> items_list_sub_tmp;
    ArrayList<StateListItem> items_list;
    MTextView titleTxt;
    ImageView backImgView;
    GeneralFunctions generalFunc;
    ProgressBar loading;
    ErrorView errorView;
    MTextView noResTxt;
    ListView state_list;

    ImageView searchImgView;
    LinearLayout searcharea;
    View toolbarArea;
    MTextView cancelTxt;
    EditText searchTxt;
    ImageView imageCancel;

    JSONArray stateArr;

    String vCountryCode;
    ArrayAdapter<StateListItem> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_state);


        Intent it = getIntent();

        //Recuperei a string da outra activity
        vCountryCode = it.getStringExtra("vCountryCode");
        generalFunc = new GeneralFunctions(getActContext());


        titleTxt = (MTextView) findViewById(R.id.titleTxt);
        backImgView = (ImageView) findViewById(R.id.backImgView);
        noResTxt = (MTextView) findViewById(R.id.noResTxt);
        loading = (ProgressBar) findViewById(R.id.loading);
        errorView = (ErrorView) findViewById(R.id.errorView);
        state_list = (ListView) findViewById(R.id.country_list);
        searchImgView = (ImageView) findViewById(R.id.searchImgView);
        searcharea = (LinearLayout) findViewById(R.id.searcharea);
        toolbarArea = (View) findViewById(R.id.toolbarArea);
        cancelTxt = (MTextView) findViewById(R.id.cancelTxt);
        searchTxt = (EditText) findViewById(R.id.searchTxt);
        imageCancel = (ImageView) findViewById(R.id.imageCancel);
        searchImgView.setVisibility(View.VISIBLE);
        searchImgView.setOnClickListener(new setOnClickList());
        cancelTxt.setOnClickListener(new setOnClickList());
        imageCancel.setOnClickListener(new setOnClickList());


        items_list = new ArrayList<>();

        state_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // ListView Clicked item index
                int itemPosition = position;

                StateListItem stateListItem;
                // Show Alert
                if (items_list_sub_tmp == null || items_list_sub_tmp.isEmpty()){
                    stateListItem = items_list.get(itemPosition);
                }else{
                    stateListItem = items_list_sub_tmp.get(itemPosition);
                }
                Utils.hideKeyboard(getActContext());

                Bundle bn = new Bundle();

                Intent intent = getIntent();
                intent.putExtra("iStateId", stateListItem.iStateId);
                intent.putExtra("vStateCode", stateListItem.vStateCode);
                setResult(Utils.SELECT_STATE_REQ_CODE, intent);
                finish();

                backImgView.performClick();

            }
        });


        searchTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                filterCountries(charSequence.toString());

            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });


        setLabels();

        backImgView.setOnClickListener(new setOnClickList());
        getStateList();
    }


    public Context getActContext() {
        return SelectStateActivity.this;
    }

    public void setLabels() {
        titleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_SELECT_STATE"));
        searchTxt.setHint(generalFunc.retrieveLangLBl("Search State", "LBL_SEARCH_STATE"));
        cancelTxt.setText(generalFunc.retrieveLangLBl("", "LBL_CANCEL_TXT"));
    }

    public void getStateList() {
        if (errorView.getVisibility() == View.VISIBLE) {
            errorView.setVisibility(View.GONE);
        }
        if (loading.getVisibility() != View.VISIBLE) {
            loading.setVisibility(View.VISIBLE);
        }

        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "getStateList");
        parameters.put("vCountryCode", vCountryCode);


        noResTxt.setVisibility(View.GONE);

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setDataResponseListener(responseString -> {

            noResTxt.setVisibility(View.GONE);

            if (responseString != null && !responseString.equals("")) {

                closeLoader();

                if (generalFunc.checkDataAvail(CommonUtilities.action_str, responseString) == true) {


                    JSONArray stateArr = generalFunc.getJsonArray("data", responseString);

                    for (int j = 0; j < stateArr.length(); j++) {
                        JSONObject subTempJson = generalFunc.getJsonObject(stateArr, j);
                        String iStateId = generalFunc.getJsonValueStr("iStateId", subTempJson);
                        String vStateCode = generalFunc.getJsonValueStr("vStateCode", subTempJson);
                        String vState = generalFunc.getJsonValueStr("vState", subTempJson);
                        items_list.add(new StateListItem(iStateId, vStateCode, vState));
                    }
                    this.stateArr = stateArr;


                    adapter = new ArrayAdapter<StateListItem>(this,
                            android.R.layout.simple_list_item_1, items_list);


                    state_list.setAdapter(adapter);


                } else {
                    noResTxt.setText(generalFunc.retrieveLangLBl("", "LBL_ERROR_TXT"));
                    noResTxt.setVisibility(View.VISIBLE);
                }
            } else {
                generateErrorView();
            }
        });
        exeWebServer.execute();
    }

    private void filterCountries(String searchText) {
       items_list_sub_tmp = new ArrayList<>();
        if (!searchText.trim().equals("")) {
            for (int i = 0; i < items_list.size(); i++) {
                if (items_list.get(i).vState.toUpperCase().contains(searchText.trim().toUpperCase())) {

                    StateListItem stateListItem = new StateListItem(items_list.get(i).iStateId, items_list.get(i).vStateCode, items_list.get(i).vState);
                    items_list_sub_tmp.add(stateListItem);
                    adapter = new ArrayAdapter<StateListItem>(this,
                            android.R.layout.simple_list_item_1, items_list_sub_tmp);
                    state_list.setAdapter(adapter);
                }
            }
        } else {
            adapter = new ArrayAdapter<StateListItem>(this,
                    android.R.layout.simple_list_item_1, items_list);
            state_list.setAdapter(adapter);
        }


    }

    public void closeLoader() {
        if (loading.getVisibility() == View.VISIBLE) {
            loading.setVisibility(View.GONE);
        }
    }

    public void generateErrorView() {

        closeLoader();

        generalFunc.generateErrorView(errorView, "LBL_ERROR_TXT", "LBL_NO_INTERNET_TXT");

        if (errorView.getVisibility() != View.VISIBLE) {
            errorView.setVisibility(View.VISIBLE);
        }
        errorView.setOnRetryListener(() -> getStateList());
    }



    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            Utils.hideKeyboard(getActContext());
            switch (view.getId()) {
                case R.id.backImgView:
                    SelectStateActivity.super.onBackPressed();
                    break;
                case R.id.cancelTxt:
                    toolbarArea.setVisibility(View.VISIBLE);
                    searchTxt.setText("");
                    searcharea.setVisibility(View.GONE);
                    filterCountries("");
                    Utils.hideKeyboard(getActContext());
                    break;
                case R.id.searchImgView:
                    if (stateArr != null) {
                        searcharea.setVisibility(View.VISIBLE);
                        toolbarArea.setVisibility(View.GONE);

                        searchTxt.requestFocus();
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (imm != null) {
                            imm.showSoftInput(searchTxt, InputMethodManager.SHOW_IMPLICIT);
                        }
                    }
                    break;
                case R.id.imageCancel:
                    searchTxt.setText("");
                    break;
            }
        }
    }

}

