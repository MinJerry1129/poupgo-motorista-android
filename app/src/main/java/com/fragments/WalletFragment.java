package com.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.adapter.files.WalletHistoryRecycleAdapter;
import com.poupgo.driver.MyWalletHistoryActivity;
import com.poupgo.driver.R;
import com.general.files.ExecuteWebServerUrl;
import com.general.functions.GeneralFunctions;

import com.utils.CommonUtilities;
import com.general.functions.Utils;
import com.utilities.view.ErrorView;
import com.view.MTextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Admin on 27-05-2017.
 */

public class WalletFragment extends Fragment {

    int page = 0;
    View view;
    GeneralFunctions generalFunc;

    MyWalletHistoryActivity myWalletAct;

    ProgressBar loading_transaction_history;
    MTextView noTransactionTxt;
    MTextView transactionsTxt;

    RecyclerView walletHistoryRecyclerView;
    ErrorView errorView;

    ArrayList<HashMap<String, String>> list = new ArrayList<>();

    boolean mIsLoading = false;
    boolean isNextPageAvailable = false;

    String next_page_str = "";

    private WalletHistoryRecycleAdapter wallethistoryRecyclerAdapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_wallet, container, false);

        myWalletAct = (MyWalletHistoryActivity) getActivity();
        generalFunc = myWalletAct.generalFunc;
        loading_transaction_history = (ProgressBar) view.findViewById(R.id.loading_transaction_history);
        noTransactionTxt = (MTextView) view.findViewById(R.id.noTransactionTxt);
        transactionsTxt = (MTextView) view.findViewById(R.id.transactionsTxt);
        walletHistoryRecyclerView = (RecyclerView) view.findViewById(R.id.walletTransactionRecyclerView);
        errorView = (ErrorView) view.findViewById(R.id.errorView);

        list = new ArrayList<>();
        wallethistoryRecyclerAdapter = new WalletHistoryRecycleAdapter(getActContext(), list, generalFunc, false);
        walletHistoryRecyclerView.setAdapter(wallethistoryRecyclerAdapter);

        getHistory();
        return view;
    }

    public Context getActContext() {
        return myWalletAct.getActContext();
    }


    public void removeNextPageConfig() {
        next_page_str = "";
        isNextPageAvailable = false;
        mIsLoading = false;
        wallethistoryRecyclerAdapter.removeFooterView();
    }

    public void closeLoader() {
        if (loading_transaction_history.getVisibility() == View.VISIBLE) {
            loading_transaction_history.setVisibility(View.GONE);
        }
    }

    public void getHistory() {
        noTransactionTxt.setVisibility(View.GONE);

        list = (ArrayList<HashMap<String, String>>) getArguments().getSerializable("getDriverTransactionsHistory");


        String LBL_BALANCE = generalFunc.getJsonValue("balance", "");


        ((MTextView) view.findViewById(R.id.yourBalTxt)).setText(generalFunc.retrieveLangLBl("", "LBL_USER_BALANCE"));

        ((MTextView) view.findViewById(R.id.walletamountTxt)).setText(LBL_BALANCE);


        next_page_str = "0";
        isNextPageAvailable = true;

        wallethistoryRecyclerAdapter = new WalletHistoryRecycleAdapter(getActContext(), list, generalFunc, false);
        walletHistoryRecyclerView.setAdapter(wallethistoryRecyclerAdapter);

        closeLoader();


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Utils.hideKeyboard(getActivity());
    }
}
