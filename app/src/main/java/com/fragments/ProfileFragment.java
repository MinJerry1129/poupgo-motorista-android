package com.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.poupgo.driver.MyProfileActivity;
import com.poupgo.driver.R;
import com.general.functions.GeneralFunctions;
import com.general.functions.Utils;
import com.view.MTextView;
import com.view.editBox.MaterialEditText;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {
    View view;
    MyProfileActivity myProfileAct;
    GeneralFunctions generalFunc;
    String userProfileJson = "";
    MaterialEditText fNameBox;
    MaterialEditText lNameBox;
    MaterialEditText emailBox;
    MaterialEditText mobileBox;
    MaterialEditText machineBox;
    MaterialEditText acceptCash;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        myProfileAct = (MyProfileActivity) getActivity();
        generalFunc = myProfileAct.generalFunc;
        userProfileJson = myProfileAct.userProfileJson;

        fNameBox = (MaterialEditText) view.findViewById(R.id.fNameBox);
        lNameBox = (MaterialEditText) view.findViewById(R.id.lNameBox);
        emailBox = (MaterialEditText) view.findViewById(R.id.emailBox);
        mobileBox = (MaterialEditText) view.findViewById(R.id.mobileBox);

        acceptCash = (MaterialEditText) view.findViewById(R.id.acceptCash);

        machineBox = (MaterialEditText) view.findViewById(R.id.machineBox);


        removeInput();
        setLabels();


        setData();

        myProfileAct.changePageTitle(generalFunc.retrieveLangLBl("", "LBL_PROFILE_TITLE_TXT"));


        return view;
    }

    public void setLabels() {

        fNameBox.setBothText(generalFunc.retrieveLangLBl("", "LBL_FIRST_NAME_HEADER_TXT"));
        lNameBox.setBothText(generalFunc.retrieveLangLBl("", "LBL_LAST_NAME_HEADER_TXT"));
        emailBox.setBothText(generalFunc.retrieveLangLBl("", "LBL_EMAIL_LBL_TXT"));
        mobileBox.setBothText(generalFunc.retrieveLangLBl("", "LBL_MOBILE_NUMBER_HEADER_TXT"));

        machineBox.setBothText(generalFunc.retrieveLangLBl("", "LBL_HAVE_MACHINE"));
        acceptCash.setBothText(generalFunc.retrieveLangLBl("", "LBL_RECEIVE_CARD"));
        ((MTextView) view.findViewById(R.id.serviceDesHTxtView)).setText(generalFunc.retrieveLangLBl("Service Description", "LBL_SERVICE_DESCRIPTION"));
    }

    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            Utils.hideKeyboard(getActivity());
            int i = view.getId();
            Bundle bn = new Bundle();

        }
    }

    public void removeInput() {
        Utils.removeInput(fNameBox);
        Utils.removeInput(lNameBox);
        Utils.removeInput(emailBox);
        Utils.removeInput(mobileBox);

        Utils.removeInput(machineBox);
        Utils.removeInput(acceptCash);

        fNameBox.setHideUnderline(true);
        lNameBox.setHideUnderline(true);
        emailBox.setHideUnderline(true);
        mobileBox.setHideUnderline(true);

        machineBox.setHideUnderline(true);
        acceptCash.setHideUnderline(true);
    }

    public void setData() {

        fNameBox.setText(generalFunc.getJsonValue("vName", userProfileJson));
        lNameBox.setText(generalFunc.getJsonValue("vLastName", userProfileJson));
        emailBox.setText(generalFunc.getJsonValue("vEmail", userProfileJson));
        machineBox.setText(generalFunc.getJsonValue("eAcceptPOS", userProfileJson).equals("Yes") ? "Sim" : "Não");
        acceptCash.setText(generalFunc.getJsonValue("eNotAcceptCash", userProfileJson).equals("Yes") ? "Sim" : "Não");

        if (generalFunc.getJsonValue("tProfileDescription", userProfileJson).equals("")) {
            ((MTextView) (view.findViewById(R.id.serviceDesVTxtView))).setText("----");
        } else {
            ((MTextView) (view.findViewById(R.id.serviceDesVTxtView))).setText(generalFunc.getJsonValue("tProfileDescription", userProfileJson));
        }
        mobileBox.setText("+" + generalFunc.getJsonValue("vCode", userProfileJson) + generalFunc.getJsonValue("vPhone", userProfileJson));


        fNameBox.getLabelFocusAnimator().start();
        lNameBox.getLabelFocusAnimator().start();
        emailBox.getLabelFocusAnimator().start();
        mobileBox.getLabelFocusAnimator().start();

        mobileBox.getLabelFocusAnimator().start();
        acceptCash.getLabelFocusAnimator().start();
        machineBox.getLabelFocusAnimator().start();
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Utils.hideKeyboard(getActivity());
    }
}
