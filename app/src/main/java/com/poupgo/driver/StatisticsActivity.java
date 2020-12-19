package com.poupgo.driver;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.general.files.ExecuteWebServerUrl;
import com.general.functions.GeneralFunctions;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.utils.CommonUtilities;
import com.general.functions.Utils;
import com.utilities.view.ErrorView;
import com.view.MTextView;
import com.view.indicator.AVLoadingIndicatorView;

import com.view.editBox.MaterialEditText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class StatisticsActivity extends AppCompatActivity {

    MTextView titleTxt;
    ImageView backImgView;
    GeneralFunctions generalFunc;
    String userProfileJson = "";
    String selectedyear = "";
    // MTextView monthHTxt;
    MTextView noTripHTxt, noTripVTxt;
    MTextView totalearnHTxt, totalearnVTxt;
    MaterialEditText yearBox;

    ArrayList<String> items_txt_year = new ArrayList<String>();
    android.support.v7.app.AlertDialog list_year;
    String TotalEarning = "";
    String TripCount = "";
    ArrayList<String> listData = new ArrayList<>();
//    private LineChart mChart;

    LineChart chart;
    ArrayList<String> monthList = new ArrayList<>();


    AVLoadingIndicatorView loaderView;
    ErrorView errorView;
    FrameLayout yearSelectArea;
    CardView bottomarea;

    LinearLayout chartContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        generalFunc = new GeneralFunctions(getActContext());
        userProfileJson = generalFunc.retrieveValue(CommonUtilities.USER_PROFILE_JSON);
        titleTxt = (MTextView) findViewById(R.id.titleTxt);
        backImgView = (ImageView) findViewById(R.id.backImgView);
        // monthHTxt = (MTextView) findViewById(R.id.monthHTxt);
        noTripHTxt = (MTextView) findViewById(R.id.noTripHTxt);
        noTripVTxt = (MTextView) findViewById(R.id.noTripVTxt);
        totalearnHTxt = (MTextView) findViewById(R.id.totalearnHTxt);
        totalearnVTxt = (MTextView) findViewById(R.id.totalearnVTxt);
        yearBox = (MaterialEditText) findViewById(R.id.yearBox);
        // chart = (LineChart) findViewById(chart1);
        errorView = (ErrorView) findViewById(R.id.errorView);
        loaderView = (AVLoadingIndicatorView) findViewById(R.id.loaderView);
        backImgView.setOnClickListener(new setOnClickList());
        yearBox.getLabelFocusAnimator().start();
        yearSelectArea = (FrameLayout) findViewById(R.id.yearSelectArea);
        bottomarea = (CardView) findViewById(R.id.bottomarea);
        chartContainer = (LinearLayout) findViewById(R.id.chartContainer);


//        chart.getAxisLeft().setDrawLabels(false);
//        chart.getAxisRight().setDrawLabels(false);
//        // chart.getXAxis().setDrawLabels(false);
//        chart.getDescription().setEnabled(false);
//        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
//
//        chart.getLegend().setEnabled(false);
//
//        chart.getAxisRight().setDrawGridLines(false);
//        chart.getAxisLeft().setDrawGridLines(false);
//        chart.getXAxis().setDrawGridLines(false);
        setLabels();
        getChartDetails();
        buildLanguageList();

        Utils.removeInput(yearBox);
        yearBox.setOnTouchListener(new setOnTouchList());
        yearBox.setOnClickListener(new setOnClickList());

    }


    public void generateErrorView() {

        yearSelectArea.setVisibility(View.GONE);
        bottomarea.setVisibility(View.GONE);
        generalFunc.generateErrorView(errorView, "LBL_ERROR_TXT", "LBL_NO_INTERNET_TXT");

        if (errorView.getVisibility() != View.VISIBLE) {
            errorView.setVisibility(View.VISIBLE);
        }
        errorView.setOnRetryListener(new ErrorView.RetryListener() {
            @Override
            public void onRetry() {
                getChartDetails();
            }
        });
    }


//    private void resetViewport(float top) {
//        // Reset viewport height range to (0,100)
//        final Viewport v = new Viewport(chart.getMaximumViewport());
//        v.bottom = 0;
//        v.top = top;
//        v.left = 0;
//        v.right = monthList.size() - 1;
//        chart.setMaximumViewport(v);
//        chart.setCurrentViewport(v);
//    }

    private void generateData() {

        LayoutInflater inflater = (LayoutInflater) getActContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.linechart_view, null);
        LineChart chart = (LineChart) view.findViewById(R.id.chart1);
        view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        this.chart = chart;
        chart.getAxisLeft().setDrawLabels(false);
        chart.getViewPortHandler().setMaximumScaleX(10);
        chart.getViewPortHandler().setMaximumScaleY(5);
        chart.setPinchZoom(true);

        chart.getAxisRight().setDrawLabels(false);
        // chart.getXAxis().setDrawLabels(false);
        chart.getDescription().setEnabled(false);
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        chart.getAxisRight().setEnabled(false);
        chart.getAxisLeft().setEnabled(false);
        chart.getLegend().setEnabled(false);

        chart.getAxisRight().setDrawGridLines(false);
        chart.getAxisLeft().setDrawGridLines(false);
        chart.getXAxis().setDrawGridLines(false);


        if (chartContainer.getChildCount() > 0) {
            chartContainer.removeAllViewsInLayout();
        }


        chartContainer.addView(view);

        ArrayList<Entry> values = new ArrayList<Entry>();

        for (int i = 0; i < listData.size(); i++) {

            Utils.printLog("ListData", "::" + listData.get(i));

            values.add(new Entry(i, generalFunc.parseFloatValue(0, listData.get(i)), getResources().getDrawable(R.drawable.chart_circle_24dp)));
        }


        LineDataSet set1;

        if (chart.getData() != null &&
                chart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) chart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();
        } else {
            // create a dataset and give it a type
            set1 = new LineDataSet(values, "DataSet 1");

            set1.setDrawIcons(false);

            // set the line to be drawn like this "- - - - - -"

            //   set1.enableDashedLine(10f, 5f, 0f);
            //set1.enableDashedHighlightLine(10f, 5f, 0f);
            set1.setColor(getResources().getColor(R.color.appThemeColor_1));
            set1.setCircleColor(getResources().getColor(R.color.appThemeColor_1));
            set1.setLineWidth(2f);
            set1.setCircleRadius(5f);
            set1.setDrawCircleHole(false);
            set1.setValueTextSize(12f);
            set1.setDrawFilled(true);
            //  set1.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            set1.setFormLineWidth(1f);
            set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
            set1.setDrawHorizontalHighlightIndicator(false);
            set1.setDrawVerticalHighlightIndicator(false);
            set1.setFormSize(15.f);

            if (com.github.mikephil.charting.utils.Utils.getSDKInt() >= 18) {
                // fill drawable only supported on api level 18 and above
                Drawable drawable = ContextCompat.getDrawable(this, R.drawable.fade_red);
                set1.setFillDrawable(drawable);
            } else {
                set1.setFillColor(Color.BLACK);
            }

            ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
            dataSets.add(set1); // add the datasets

            // create a data object with the datasets
            LineData data = new LineData(dataSets);

            // set data
            chart.setData(data);
        }


    }


    public void setLabels() {
        titleTxt.setText(generalFunc.retrieveLangLBl("Trip Statistics", "LBL_TRIP_STATISTICS_TXT"));
        totalearnHTxt.setText(generalFunc.retrieveLangLBl("", "LBL_TOTAL_EARNINGS"));
        noTripHTxt.setText(generalFunc.retrieveLangLBl("", "LBL_NUMBER_OF_TRIPS"));
        yearBox.setBothText(generalFunc.retrieveLangLBl("", "LBL_YEAR"), generalFunc.retrieveLangLBl("", "LBL_CHOOSE_YEAR"));
    }

    public Context getActContext() {
        return StatisticsActivity.this;
    }

    public void buildLanguageList() {


        CharSequence[] cs_languages_txt = items_txt_year.toArray(new CharSequence[items_txt_year.size()]);

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActContext());

        builder.setTitle(getSelectYearText());

        builder.setItems(cs_languages_txt, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                // Do something with the selection

                if (list_year != null) {
                    list_year.dismiss();
                }

                yearBox.setText(items_txt_year.get(item));
                selectedyear = items_txt_year.get(item).toString();
                getChartDetails();

            }
        });

        list_year = builder.create();

        if (generalFunc.isRTLmode() == true) {
            generalFunc.forceRTLIfSupported(list_year);
        }

    }

    public String getSelectYearText() {
        return ("" + generalFunc.retrieveLangLBl("", "LBL_CHOOSE_YEAR"));
    }

    private void setData() {
        totalearnVTxt.setText(TotalEarning);
        noTripVTxt.setText((TripCount));
        yearBox.setText(selectedyear);
        buildLanguageList();

    }

    public void getChartDetails() {

        loaderView.setVisibility(View.VISIBLE);
        if (errorView.getVisibility() == View.VISIBLE) {
            errorView.setVisibility(View.GONE);
        }


        final HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "getYearTotalEarnings");
        parameters.put("iDriverId", generalFunc.getMemberId());
        parameters.put("UserType", CommonUtilities.app_type);
        parameters.put("year", selectedyear);


        final ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
            @Override
            public void setResponse(String responseString) {


                if (responseString != null && !responseString.equals("")) {
                    yearSelectArea.setVisibility(View.VISIBLE);
                    bottomarea.setVisibility(View.VISIBLE);

                    if (generalFunc.checkDataAvail(CommonUtilities.action_str, responseString) == true) {
                        TripCount = generalFunc.getJsonValue("TripCount", responseString);
                        String MaxEarning = generalFunc.getJsonValue("MaxEarning", responseString);
                        TotalEarning = generalFunc.getJsonValue("TotalEarning", responseString);
                        selectedyear = generalFunc.getJsonValue("CurrentYear", responseString);
                        JSONArray YearMonthArr = generalFunc.getJsonArray("YearMonthArr", responseString);
                        listData.clear();
                        monthList.clear();
                        items_txt_year.clear();
                        for (int j = 0; j < YearMonthArr.length(); j++) {
                            JSONObject jsonObject = generalFunc.getJsonObject(YearMonthArr, j);

                            monthList.add(generalFunc.getJsonValue("CurrentMonth", jsonObject.toString()));

                            listData.add(jsonObject.optString("TotalEarnings"));
                        }
                        JSONArray yeararray = generalFunc.getJsonArray("YearArr", responseString);
                        for (int i = 0; i < yeararray.length(); i++) {
                            items_txt_year.add(generalFunc.convertNumberWithRTL((String) generalFunc.getValueFromJsonArr(yeararray, i)));
                        }

                        setData();

                        generateData();

                        //  chart.setViewportCalculationEnabled(false);

                        if (MaxEarning.equals("0")) {
                            MaxEarning = "1";
                        }
                        //chart.setVisibility(View.VISIBLE);
                        loaderView.setVisibility(View.GONE);
                        //resetViewport(generalFunc.parseFloatValue(0, MaxEarning));

                        if (chart != null) {
                            XAxis xAxis = chart.getXAxis();
                            //  xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                            xAxis.setDrawGridLines(false);
                            xAxis.setLabelCount(12, false);
                            xAxis.setAxisMinimum(0);
                            xAxis.setDrawLabels(true);

                            xAxis.setGranularity(1);
                            xAxis.setTextColor(getResources().getColor(R.color.appThemeColor_1));
                            xAxis.setValueFormatter(new IAxisValueFormatter() {
                                @Override
                                public String getFormattedValue(float value, AxisBase axis) {
                                    return monthList.get((int) value);
                                }
                            });
                        }

                    } else {

                    }
                } else {
                    generateErrorView();
                    loaderView.setVisibility(View.GONE);
                }
            }


        });
        exeWebServer.execute();
    }


    public class setOnTouchList implements View.OnTouchListener {

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_UP && !view.hasFocus()) {
                view.performClick();
            }
            return true;
        }
    }

    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            int i = view.getId();
            Utils.hideKeyboard(StatisticsActivity.this);

            if (i == R.id.backImgView) {
                StatisticsActivity.super.onBackPressed();
            } else if (i == R.id.yearBox) {
                list_year.show();

            }
        }
    }
}
