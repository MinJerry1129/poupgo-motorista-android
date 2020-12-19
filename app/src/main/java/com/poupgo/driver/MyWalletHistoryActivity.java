package com.poupgo.driver;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.adapter.files.ViewPagerAdapter;
import com.fragments.WalletFragment;
import com.general.functions.GeneralFunctions;
import com.general.functions.Utils;
import com.google.gson.Gson;
import com.view.MTextView;
import com.view.MaterialTabs;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Admin on 04-11-2016.
 */
public class MyWalletHistoryActivity extends AppCompatActivity {
    public static int page = 0;
    MTextView titleTxt;
    ImageView backImgView;

    public GeneralFunctions generalFunc;
    CharSequence[] titles;
    List<HashMap<String, String>> credito = new ArrayList<>();
    List<HashMap<String, String>> debito = new ArrayList<>();
    Locale ptBr = new Locale("pt", "BR");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mywallet_history);


        titleTxt = (MTextView) findViewById(R.id.titleTxt);
        backImgView = (ImageView) findViewById(R.id.backImgView);
        generalFunc = new GeneralFunctions(getActContext());
        backImgView.setOnClickListener(new setOnClickList());

        setLabels();

        ViewPager appLogin_view_pager = (ViewPager) findViewById(R.id.appLogin_view_pager);
        MaterialTabs material_tabs = (MaterialTabs) findViewById(R.id.material_tabs);
        final ArrayList<Fragment> fragmentList = new ArrayList<>();

        titles = new CharSequence[]{generalFunc.retrieveLangLBl("", "LBL_ALL"), generalFunc.retrieveLangLBl("", "LBL_MONEY_IN"), generalFunc.retrieveLangLBl("", "LBL_MONEY_OUT")};
        material_tabs.setVisibility(View.VISIBLE);
        fragmentList.add(generateWalletFrag(Utils.Wallet_all));
        fragmentList.add(generateWalletFrag(Utils.Wallet_credit));
        fragmentList.add(generateWalletFrag(Utils.Wallet_debit));


        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), titles, fragmentList);
        appLogin_view_pager.setAdapter(adapter);
        material_tabs.setViewPager(appLogin_view_pager);


        appLogin_view_pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Utils.printLog("onPageScrolled Pos", position + "");
            }

            @Override
            public void onPageSelected(int position) {
                Utils.printLog("onPageSelected Pos", position + "");
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                Utils.printLog("onPageScrollStateChanged state", state + "");


            }
        });

    }

    public WalletFragment generateWalletFrag(String page) {

        WalletFragment frag = new WalletFragment();
        Bundle bn = new Bundle();

        Intent it = getIntent();
        String responseString = it.getStringExtra("getDriverTransactionsHistory");
        ArrayList<HashMap<String, String>> list = new ArrayList<>();

        if (page.equals(Utils.Wallet_credit)) {
            JSONArray arr_transhistory = generalFunc.getJsonArray("data", responseString);
            if (arr_transhistory != null && arr_transhistory.length() > 0) {
                for (int i = 0; i < arr_transhistory.length(); i++) {
                    JSONObject obj_temp = generalFunc.getJsonObject(arr_transhistory, i);

                    if (generalFunc.getJsonValue("type", obj_temp.toString()).equals("Credit")) {
                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put("iBalance", NumberFormat.getCurrencyInstance(ptBr).format(Float.valueOf(generalFunc.getJsonValue("balance", obj_temp.toString()))));
                        map.put("eType", generalFunc.getJsonValue("type", obj_temp.toString()));
                        map.put("iTripId", generalFunc.getJsonValue("trip", obj_temp.toString()));
                        map.put("tDescription", generalFunc.retrieveLangLBl("", generalFunc.getJsonValue("description", obj_temp.toString())));

                        list.add(map);
                    }

                }
            }
            Map data = new HashMap();
            data.put("data", list);
            bn.putSerializable("getDriverTransactionsHistory", list);
        } else if (page.equals(Utils.Wallet_all)) {
            String response = it.getStringExtra("driverBalance");
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("iBalance", NumberFormat.getCurrencyInstance(ptBr).format(Float.valueOf(generalFunc.getJsonValue("totalReceive", response).replaceAll(",",""))));
            map.put("eType", "Credit");
            map.put("iTripId", generalFunc.getJsonValue("trip", ""));
            map.put("tDescription", "Total a Receber");

            list.add(map);

            map = new HashMap<String, String>();

            map.put("iBalance", NumberFormat.getCurrencyInstance(ptBr).format(Float.valueOf(generalFunc.getJsonValue("totalPayable", response).replaceAll(",",""))));
            map.put("eType", "Debit");
            map.put("iTripId", generalFunc.getJsonValue("trip", ""));
            map.put("tDescription", "Total a Pagar");

            list.add(map);

            map = new HashMap<String, String>();

            Float value = Float.valueOf(generalFunc.getJsonValue("total", response).replaceAll(",",""));

            map.put("iBalance", NumberFormat.getCurrencyInstance(ptBr).format(Float.valueOf(generalFunc.getJsonValue("total", response).replaceAll(",",""))));
            map.put("eType", value < 0 ? "Debit" : "Credit");
            map.put("iTripId", "");
            map.put("tDescription", "Total  Geral");

            list.add(map);


            Map data = new HashMap();
            data.put("data", list);
            bn.putSerializable("getDriverTransactionsHistory", list);
        } else {
            JSONArray arr_transhistory = generalFunc.getJsonArray("data", responseString);
            if (arr_transhistory != null && arr_transhistory.length() > 0) {
                for (int i = 0; i < arr_transhistory.length(); i++) {
                    JSONObject obj_temp = generalFunc.getJsonObject(arr_transhistory, i);

                    if (generalFunc.getJsonValue("type", obj_temp.toString()).equals("Debit")) {
                        HashMap<String, String> map = new HashMap<String, String>();

                        map.put("iBalance", NumberFormat.getCurrencyInstance(ptBr).format(Float.valueOf(generalFunc.getJsonValue("balance", obj_temp.toString()).replaceAll(",",""))));
                        map.put("eType", generalFunc.getJsonValue("type", obj_temp.toString()));
                        map.put("iTripId", generalFunc.getJsonValue("trip", obj_temp.toString()));
                        map.put("tDescription", generalFunc.retrieveLangLBl("", generalFunc.getJsonValue("description", obj_temp.toString())));

                        list.add(map);
                    }

                }
            }
            Map data = new HashMap();
            data.put("data", list);
            bn.putSerializable("getDriverTransactionsHistory", list);
        }


        frag.setArguments(bn);

        return frag;
    }


    public void setLabels() {

        titleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_Transaction_HISTORY"));

    }

    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            Utils.hideKeyboard(MyWalletHistoryActivity.this);
            switch (view.getId()) {
                case R.id.backImgView:
                    MyWalletHistoryActivity.super.onBackPressed();
                    break;

            }
        }
    }


    public Context getActContext() {
        return MyWalletHistoryActivity.this;
    }

}