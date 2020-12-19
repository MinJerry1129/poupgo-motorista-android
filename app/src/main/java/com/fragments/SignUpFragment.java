package com.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.text.Html;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import com.poupgo.driver.AppLoignRegisterActivity;
import com.poupgo.driver.BuildConfig;
import com.poupgo.driver.R;
import com.poupgo.driver.SelectCityActivity;
import com.poupgo.driver.SelectCountryActivity;
import com.poupgo.driver.SelectStateActivity;
import com.poupgo.driver.SupportActivity;
import com.general.crop.CropImage;
import com.general.crop.CropImageView;
import com.general.files.ExecuteWebServerUrl;
import com.general.files.ImageFilePath;
import com.general.functions.GeneralFunctions;
import com.general.files.OpenMainProfile;
import com.general.files.SetOnTouchList;
import com.general.files.SetUserData;
import com.rest.RestClient;
import com.squareup.picasso.Picasso;
import com.utilities.general.files.StartActProcess;
import com.utilities.view.CreateRoundedView;
import com.utils.CommonUtilities;
import com.general.functions.Utils;
import com.general.functions.GenerateAlertBox;
import com.view.MButton;
import com.view.MTextView;
import com.view.MaterialRippleLayout;
import com.view.SelectableRoundedImageView;
import com.view.editBox.MaterialEditText;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpFragment extends Fragment {

    View view;
    GenerateAlertBox generateAlert;
    AppLoignRegisterActivity appLoginAct;
    GeneralFunctions generalFunc;

    MaterialEditText fNameBox;
    MaterialEditText lNameBox;
    MaterialEditText emailBox;
    MaterialEditText passwordBox;
    MaterialEditText invitecodeBox;
    static MaterialEditText countryBox;
    MaterialEditText mobileBox;
    MaterialEditText cpfBox;
    MaterialEditText cepBox;
    MaterialEditText stateBox;
    MaterialEditText addressBox;
    MaterialEditText address2Box;
    MaterialEditText cityBox;

    MButton btn_type2;


    // SignUpFragment signUpFrag;
    ImageView inviteQueryImg;

    LinearLayout inviteCodeArea;
    static String vCountryCode = "";
    static String iStateId = "";
    static String iCityId = "";

    static String vPhoneCode = "";
    static boolean isCountrySelected = false;

    String required_str = "";
    String error_email_str = "";

    MTextView signbootomHint, signbtn;

    ImageView countrydropimage, countrydropimagerror;
    CheckBox checkboxTermsCond;
    MTextView txtTermsCond;

    SelectableRoundedImageView userProfileImgView;
    SelectableRoundedImageView editIconImgView;
    RelativeLayout userImgArea;

    public static final int MEDIA_TYPE_IMAGE = 1;
    private static final String IMAGE_DIRECTORY_NAME = "Temp";
    private static final int SELECT_PICTURE = 2;
    private static final int CROP_IMAGE = 3;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private boolean insertImage = false;
    private String selectedImagePath;
    private Spinner spGender;
    private String gender = "";
    LinearLayout llContentArea;
    private Uri fileUri;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        appLoginAct = (AppLoignRegisterActivity) getActivity();
        generalFunc = appLoginAct.generalFunc;
        generateAlert = new GenerateAlertBox(getActContext());
        cpfBox = (MaterialEditText) view.findViewById(R.id.cpfBox);
        cepBox = (MaterialEditText) view.findViewById(R.id.cepBox);
        stateBox = (MaterialEditText) view.findViewById(R.id.stateBox);
        addressBox = (MaterialEditText) view.findViewById(R.id.addressBox);
        address2Box = (MaterialEditText) view.findViewById(R.id.address2Box);
        cityBox = (MaterialEditText) view.findViewById(R.id.cityBox);

        fNameBox = (MaterialEditText) view.findViewById(R.id.fNameBox);
        lNameBox = (MaterialEditText) view.findViewById(R.id.lNameBox);
        emailBox = (MaterialEditText) view.findViewById(R.id.emailBox);
        countryBox = (MaterialEditText) view.findViewById(R.id.countryBox);
        mobileBox = (MaterialEditText) view.findViewById(R.id.mobileBox);
        passwordBox = (MaterialEditText) view.findViewById(R.id.passwordBox);
        invitecodeBox = (MaterialEditText) view.findViewById(R.id.invitecodeBox);
        signbootomHint = (MTextView) view.findViewById(R.id.signbootomHint);
        signbtn = (MTextView) view.findViewById(R.id.signbtn);
        countrydropimage = (ImageView) view.findViewById(R.id.countrydropimage);
        countrydropimagerror = (ImageView) view.findViewById(R.id.countrydropimagerror);
        checkboxTermsCond = (CheckBox) view.findViewById(R.id.checkboxTermsCond);
        txtTermsCond = (MTextView) view.findViewById(R.id.txtTermsCond);

        signbtn.setOnClickListener(new setOnClickList());
        txtTermsCond.setOnClickListener(new setOnClickList());

        vCountryCode = generalFunc.retrieveValue(CommonUtilities.DefaultCountryCode);
        vPhoneCode = generalFunc.retrieveValue(CommonUtilities.DefaultPhoneCode);

        if (!vPhoneCode.equalsIgnoreCase("")) {
            countryBox.setText("+" + vPhoneCode);
            isCountrySelected = true;
        }


        btn_type2 = ((MaterialRippleLayout) view.findViewById(R.id.btn_type2)).getChildView();

        inviteQueryImg = (ImageView) view.findViewById(R.id.inviteQueryImg);

        inviteCodeArea = (LinearLayout) view.findViewById(R.id.inviteCodeArea);

        inviteQueryImg.setColorFilter(Color.parseColor("#CECECE"));

        inviteQueryImg.setOnClickListener(new setOnClickList());

        inviteCodeArea.setVisibility(View.GONE);

        if (generalFunc.isReferralSchemeEnable()) {
            inviteCodeArea.setVisibility(View.VISIBLE);
        }

        userProfileImgView = view.findViewById(R.id.userProfileImgView);
        editIconImgView = view.findViewById(R.id.editIconImgView);
        llContentArea = view.findViewById(R.id.llContent);

        spGender = view.findViewById(R.id.spGender);

        new CreateRoundedView(getResources().getColor(R.color.editBox_primary), Utils.dipToPixels(getActContext(), 15), 0,
                Color.parseColor("#00000000"), editIconImgView);

        editIconImgView.setColorFilter(getResources().getColor(R.color.appThemeColor_TXT_1));

        userProfileImgView.setImageResource(R.mipmap.ic_no_pic_user);
        userImgArea = view.findViewById(R.id.userImgArea);

        userImgArea.setOnClickListener(new setOnClickList());



        removeInput();
        setLabels();

        btn_type2.setId(Utils.generateViewId());
        btn_type2.setOnClickListener(new setOnClickList());

        passwordBox.setTypeface(Typeface.DEFAULT);
        passwordBox.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        passwordBox.setTypeface(generalFunc.getDefaultFont(getActContext()));
        mobileBox.setInputType(InputType.TYPE_CLASS_NUMBER);
        emailBox.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS | InputType.TYPE_CLASS_TEXT);
        cpfBox.setInputType(InputType.TYPE_CLASS_NUMBER);
        stateBox.setInputType(InputType.TYPE_CLASS_TEXT);
        cepBox.setInputType(InputType.TYPE_CLASS_NUMBER);
        addressBox.setInputType(InputType.TYPE_CLASS_TEXT);
        address2Box.setInputType(InputType.TYPE_CLASS_TEXT);
        cityBox.setInputType(InputType.TYPE_CLASS_TEXT);

        fNameBox.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        lNameBox.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        emailBox.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        passwordBox.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        mobileBox.setImeOptions(EditorInfo.IME_ACTION_DONE);

        cpfBox.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        cepBox.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        stateBox.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        addressBox.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        address2Box.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        cityBox.setImeOptions(EditorInfo.IME_ACTION_NEXT);

        countryBox.setShowClearButton(false);


//        buildLanguageList();

        return view;
    }


    public void removeInput() {
        Utils.removeInput(countryBox);
        Utils.removeInput(cityBox);
        Utils.removeInput(stateBox);

        countryBox.setOnTouchListener(new SetOnTouchList());

        countryBox.setOnClickListener(new setOnClickList());

        cityBox.setOnTouchListener(new SetOnTouchList());

        cityBox.setOnClickListener(new setOnClickList());

        stateBox.setOnTouchListener(new SetOnTouchList());

        stateBox.setOnClickListener(new setOnClickList());


    }


    public void setLabels() {

        fNameBox.setBothText(generalFunc.retrieveLangLBl("", "LBL_FIRST_NAME_HEADER_TXT"));
        lNameBox.setBothText(generalFunc.retrieveLangLBl("", "LBL_LAST_NAME_HEADER_TXT"));
        emailBox.setBothText(generalFunc.retrieveLangLBl("", "LBL_EMAIL_LBL_TXT"));
        countryBox.setBothText(generalFunc.retrieveLangLBl("", "LBL_COUNTRY_TXT"));
        mobileBox.setBothText(generalFunc.retrieveLangLBl("", "LBL_MOBILE_NUMBER_HEADER_TXT"));
        passwordBox.setBothText(generalFunc.retrieveLangLBl("", "LBL_PASSWORD_LBL_TXT"));
        cpfBox.setBothText(generalFunc.retrieveLangLBl("", "LBL_TAXID_TXT"));
        cepBox.setBothText(generalFunc.retrieveLangLBl("", "LBL_ZIP_CODE_SIGNUP"));
        stateBox.setBothText(generalFunc.retrieveLangLBl("", "LBL_STATE_TXT"));
        addressBox.setBothText(generalFunc.retrieveLangLBl("", "LBL_ADDRESS_SIGNUP"));
        address2Box.setBothText(generalFunc.retrieveLangLBl("", "LBL_ADDRESS2_SIGNUP"));
        cityBox.setBothText(generalFunc.retrieveLangLBl("", "LBL_CITY_TXT"));

        signbootomHint.setText(generalFunc.retrieveLangLBl("", "LBL_ALREADY_HAVE_ACC"));
        signbtn.setText(generalFunc.retrieveLangLBl("", "LBL_HEADER_TOPBAR_SIGN_IN_TXT"));

        if (generalFunc.retrieveValue(CommonUtilities.MOBILE_VERIFICATION_ENABLE_KEY).equals("Yes")) {
            btn_type2.setText(generalFunc.retrieveLangLBl("", "LBL_BTN_NEXT_TXT"));
        } else {
            btn_type2.setText(generalFunc.retrieveLangLBl("", "LBL_BTN_REGISTER_TXT"));
        }

        required_str = generalFunc.retrieveLangLBl("", "LBL_FEILD_REQUIRD_ERROR_TXT");
        error_email_str = generalFunc.retrieveLangLBl("", "LBL_FEILD_EMAIL_ERROR_TXT");

        String attrString1 = generalFunc.retrieveLangLBl("", "LBL_TERMS_CONDITION_PREFIX");
        String attrString2 = generalFunc.retrieveLangLBl("", "LBL_TERMS_PRIVACY");

        String htmlString = "<u><font color=" + getActContext().getResources().getColor(R.color.appThemeColor_1) + ">" + attrString2 + "</font></u>";
        txtTermsCond.setText(Html.fromHtml(attrString1 + " " + htmlString));

        invitecodeBox.setBothText(generalFunc.retrieveLangLBl("", "LBL_INVITE_CODE_HINT"), generalFunc.retrieveLangLBl("", "LBL_INVITE_CODE_HINT"));
        List<String> list = new ArrayList<String>();
        list.add("Selecione");
        list.add("Masculino");
        list.add("Feminino");



        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActContext(),
                android.R.layout.simple_spinner_item, list){
            @Override
            public boolean isEnabled(int position) {
                if(position == 0){
                    // Disabilita a primeira posição (hint)
                    return false;
                } else {
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;

                if(position == 0){

                    // Deixa o hint com a cor cinza ( efeito de desabilitado)
                    tv.setTextColor(Color.GRAY);

                }else {
                    tv.setTextColor(Color.BLACK);
                }

                return view;            }
        };
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spGender.setAdapter(dataAdapter);
        spGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 1){

                    gender = "Male";
                }else if(position == 2){
                    gender = "Female";
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {

            if (!isAdded()) {
                return;
            }

            int i = view.getId();
            if (i == btn_type2.getId()) {
                Utils.hideKeyboard(appLoginAct);
                checkData();
            } else if (i == R.id.countryBox) {
                new StartActProcess(getActivity()).startActForResult(SelectCountryActivity.class, Utils.SELECT_COUNTRY_REQ_CODE);
            } else if (i == R.id.cityBox) {
                if (iStateId == null || iStateId.isEmpty()){
                    Snackbar.make(getActivity().findViewById(R.id.llContent),
                            "Informe um estado para continuar!", Snackbar.LENGTH_LONG).show();
                }else{
                    Intent intent = new Intent(getContext(),
                            SelectCityActivity.class);
                    intent.putExtra("iStateId", iStateId);
                    startActivityForResult(intent, Utils.SELECT_CITY_REQ_CODE);
                }



            } else if (i == R.id.stateBox) {

                Intent intent = new Intent(getContext(),
                        SelectStateActivity.class);
                intent.putExtra("vCountryCode", vCountryCode);
                startActivityForResult(intent, Utils.SELECT_STATE_REQ_CODE);

            } else if (i == inviteQueryImg.getId()) {
                generalFunc.showGeneralMessage(generalFunc.retrieveLangLBl(" What is Referral / Invite Code ?", "LBL_REFERAL_SCHEME_TXT"),
                        generalFunc.retrieveLangLBl("", "LBL_REFERAL_SCHEME"));

            } else if (i == signbtn.getId()) {
                Utils.hideKeyboard(appLoginAct);
                appLoginAct.titleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_SIGN_IN_TXT"));
                appLoginAct.signheaderHint.setText(generalFunc.retrieveLangLBl("", "LBL_SIGN_IN_WITH_SOC_ACC"));
                appLoginAct.hadnleFragment(new SignInFragment());

            } else if (i == txtTermsCond.getId()) {

                Bundle bn = new Bundle();
                bn.putBoolean("islogin", true);
                new StartActProcess(getActContext()).startActWithData(SupportActivity.class, bn);

            }else if (i == R.id.userImgArea) {
                if (generalFunc.isCameraStoragePermissionGranted()) {
                    new ImageSourceDialog().run();
                } else {
                    generalFunc.showMessage(llContentArea, "Allow this app to use camera.");
                }


            }

        }
    }


    public void checkData() {
        Utils.hideKeyboard(getActContext());

        String noWhiteSpace = generalFunc.retrieveLangLBl("Password should not contain whitespace.", "LBL_ERROR_NO_SPACE_IN_PASS");
        String pass_length = generalFunc.retrieveLangLBl("Password must be", "LBL_ERROR_PASS_LENGTH_PREFIX")
                + " " + Utils.minPasswordLength + " " + generalFunc.retrieveLangLBl("or more character long.", "LBL_ERROR_PASS_LENGTH_SUFFIX");

        boolean cpfEntered = Utils.checkText(cpfBox) ? true : Utils.setErrorFields(cpfBox, required_str);
        boolean cepEntered = Utils.checkText(cepBox) ? true : Utils.setErrorFields(cepBox, required_str);
        boolean addressEntered = Utils.checkText(addressBox) ? true : Utils.setErrorFields(addressBox, required_str);
        boolean address2Entered = Utils.checkText(address2Box) ? true : Utils.setErrorFields(address2Box, required_str);
        boolean stateEntered = Utils.checkText(cepBox) ? true : Utils.setErrorFields(stateBox, required_str);
        boolean cityEntered = Utils.checkText(cityBox) ? true : Utils.setErrorFields(cityBox, required_str);

        boolean fNameEntered = Utils.checkText(fNameBox) ? true : Utils.setErrorFields(fNameBox, required_str);
        boolean lNameEntered = Utils.checkText(lNameBox) ? true : Utils.setErrorFields(lNameBox, required_str);
        boolean emailEntered = Utils.checkText(emailBox) ?
                (generalFunc.isEmailValid(Utils.getText(emailBox)) ? true : Utils.setErrorFields(emailBox, error_email_str))
                : Utils.setErrorFields(emailBox, required_str);
        boolean mobileEntered = Utils.checkText(mobileBox) ? true : Utils.setErrorFields(mobileBox, required_str);
        boolean countryEntered = isCountrySelected ? true : false;
        boolean passwordEntered = Utils.checkText(passwordBox) ?
                (Utils.getText(passwordBox).contains(" ") ? Utils.setErrorFields(passwordBox, noWhiteSpace)
                        : (Utils.getText(passwordBox).length() >= Utils.minPasswordLength ? true : Utils.setErrorFields(passwordBox, pass_length)))
                : Utils.setErrorFields(passwordBox, required_str);

        if (countryBox.getText().length() == 0) {
            countryEntered = false;
        }

        if (!countryEntered) {

            Utils.setErrorFields(countryBox, required_str);
            countrydropimagerror.setVisibility(View.VISIBLE);
            countrydropimage.setVisibility(View.GONE);
        } else {
            countrydropimage.setVisibility(View.VISIBLE);
            countrydropimagerror.setVisibility(View.GONE);

        }
        if (gender.equals("")){
            Snackbar.make(getActivity().findViewById(R.id.llContent),
                    "Selecione um genero!", Snackbar.LENGTH_LONG).show();
            return;
        }
        if (!insertImage) {
            Snackbar.make(getActivity().findViewById(R.id.llContent),
                    "Adicione uma foto !", Snackbar.LENGTH_LONG).show();
            return;
        }

        if (mobileEntered) {
            mobileEntered = mobileBox.length() >= 3 ? true : Utils.setErrorFields(mobileBox, generalFunc.retrieveLangLBl("", "LBL_INVALID_MOBILE_NO"));
        }
        if (cpfEntered) {
            if (cpfBox.getText().toString().length() > 11) {
                cpfBox.setText("");
                Snackbar.make(getActivity().findViewById(R.id.llContent),
                        "C.P.F inválido!", Snackbar.LENGTH_LONG).show();
                return;
            } else {
                if (!validaCpf(cpfBox.getText().toString())) {
                    cpfBox.setText("");
                    Snackbar.make(getActivity().findViewById(R.id.llContent),
                            "C.P.F inválido!", Snackbar.LENGTH_LONG).show();
                    return;
                }
            }

        } else {
            return;
        }

        if (!fNameEntered || !lNameEntered || !emailEntered || !mobileEntered || !countryEntered || !passwordEntered
                || !cepEntered || !stateEntered || !addressEntered || !address2Entered || !cityEntered) {
            return;
        }

        if (!checkboxTermsCond.isChecked()) {
            generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_ACCEPT_TERMS_PRIVACY_ALERT"));
            return;
        }

        btn_type2.setEnabled(false);

        if (generalFunc.retrieveValue(CommonUtilities.MOBILE_VERIFICATION_ENABLE_KEY).equals("Yes")) {
            checkUserExist();
        } else {
            registerUser();
        }
    }

    public boolean validaCpf(String CPF) {
        // considera-se erro CPF's formados por uma sequencia de numeros iguais
        if (CPF.equals("00000000000") ||
                CPF.equals("11111111111") ||
                CPF.equals("22222222222") || CPF.equals("33333333333") ||
                CPF.equals("44444444444") || CPF.equals("55555555555") ||
                CPF.equals("66666666666") || CPF.equals("77777777777") ||
                CPF.equals("88888888888") || CPF.equals("99999999999") ||
                (CPF.length() != 11))
            return (false);

        char dig10, dig11;
        int sm, i, r, num, peso;

        // "try" - protege o codigo para eventuais erros de conversao de tipo (int)
        try {
            // Calculo do 1o. Digito Verificador
            sm = 0;
            peso = 10;
            for (i = 0; i < 9; i++) {
                // converte o i-esimo caractere do CPF em um numero:
                // por exemplo, transforma o caractere '0' no inteiro 0
                // (48 eh a posicao de '0' na tabela ASCII)
                num = (int) (CPF.charAt(i) - 48);
                sm = sm + (num * peso);
                peso = peso - 1;
            }

            r = 11 - (sm % 11);
            if ((r == 10) || (r == 11))
                dig10 = '0';
            else dig10 = (char) (r + 48); // converte no respectivo caractere numerico

            // Calculo do 2o. Digito Verificador
            sm = 0;
            peso = 11;
            for (i = 0; i < 10; i++) {
                num = (int) (CPF.charAt(i) - 48);
                sm = sm + (num * peso);
                peso = peso - 1;
            }

            r = 11 - (sm % 11);
            if ((r == 10) || (r == 11))
                dig11 = '0';
            else dig11 = (char) (r + 48);

            // Verifica se os digitos calculados conferem com os digitos informados.
            if ((dig10 == CPF.charAt(9)) && (dig11 == CPF.charAt(10)))
                return (true);
            else return (false);
        } catch (InputMismatchException erro) {
            return (false);
        }
    }

    public void registerUser() {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "signup");
        parameters.put("vFirstName", Utils.getText(fNameBox));
        parameters.put("vLastName", Utils.getText(lNameBox));
        parameters.put("vEmail", Utils.getText(emailBox));
        parameters.put("vPhone", Utils.getText(mobileBox));
        parameters.put("vPassword", Utils.getText(passwordBox));
        parameters.put("PhoneCode", vPhoneCode);
        parameters.put("CountryCode", vCountryCode);
        parameters.put("vDeviceType", Utils.deviceType);
        parameters.put("vInviteCode", Utils.getText(invitecodeBox));
        parameters.put("UserType", Utils.userType);
        parameters.put("vCurrency", generalFunc.retrieveValue(CommonUtilities.DEFAULT_CURRENCY_VALUE));
        parameters.put("vLang", generalFunc.retrieveValue(CommonUtilities.LANGUAGE_CODE_KEY));
        parameters.put("vDoc", Utils.getText(cpfBox));
        parameters.put("vState", iStateId);
        parameters.put("vZip", Utils.getText(cepBox));
        parameters.put("vCaddress", Utils.getText(addressBox));
        parameters.put("vCaddress2", Utils.getText(address2Box));
        parameters.put("vCity", iCityId);
        parameters.put("eGender", gender);

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setIsDeviceTokenGenerate(true, "vDeviceToken", generalFunc);
        exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
            @Override
            public void setResponse(String responseString) {
                btn_type2.setEnabled(true);
                Utils.printLog("Response", "::" + responseString);

                if (responseString != null && !responseString.equals("")) {

                    boolean isDataAvail = GeneralFunctions.checkDataAvail(CommonUtilities.action_str, responseString);
                    String tSessionId = "";
                    JSONObject obj_userProfile = null;
                    String message = "";
                    try {
                        message = new JSONObject(responseString).getString("message");
                        obj_userProfile = generalFunc.getJsonObject(message);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    tSessionId = generalFunc.getJsonValueStr("tSessionId", obj_userProfile);

                    Log.v("FelipeTeste", tSessionId);


                    if (isDataAvail == true) {
                        new SetUserData(responseString, generalFunc, getActContext(), true);
                        generalFunc.storedata(CommonUtilities.USER_PROFILE_JSON, generalFunc.getJsonValue(CommonUtilities.message_str, responseString));
                        execute(tSessionId, message);

                        new OpenMainProfile(getActContext(),
                                generalFunc.getJsonValue(CommonUtilities.message_str, responseString), false, generalFunc).startProcess();
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

    public void checkUserExist() {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "isUserExist");
        parameters.put("Email", Utils.getText(emailBox));
        parameters.put("Phone", Utils.getText(mobileBox));

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
            @Override
            public void setResponse(String responseString) {

                Utils.printLog("Response", "::" + responseString);
                btn_type2.setEnabled(true);
                if (responseString != null && !responseString.equals("")) {

                    boolean isDataAvail = GeneralFunctions.checkDataAvail(CommonUtilities.action_str, responseString);

                    if (isDataAvail == true) {
                        //  notifyVerifyMobile();
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

    public void notifyVerifyMobile() {
        Bundle bn = new Bundle();
        bn.putString("MOBILE", vPhoneCode + Utils.getText(mobileBox));
        bn.putString("msg", "DO_PHONE_VERIFY");
        // generalFunc.verifyMobile(bn, signUpFrag);
    }

    public Context getActContext() {
        return appLoginAct.getActContext();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Utils.SELECT_COUNTRY_REQ_CODE && resultCode == appLoginAct.RESULT_OK && data != null) {

            vCountryCode = data.getStringExtra("vCountryCode");
            vPhoneCode = data.getStringExtra("vPhoneCode");
            isCountrySelected = true;
            countryBox.setTextColor(getResources().getColor(R.color.black));
        } else if (requestCode == Utils.VERIFY_MOBILE_REQ_CODE && resultCode == appLoginAct.RESULT_OK) {
            String MSG_TYPE = data == null ? "" : (data.getStringExtra("MSG_TYPE") == null ? "" : data.getStringExtra("MSG_TYPE"));
            if (!MSG_TYPE.equals("EDIT_PROFILE")) {
                registerUser();
            }
        } else if (requestCode == Utils.SELECT_STATE_REQ_CODE && data != null) {

            iStateId = data.getStringExtra("iStateId");
            stateBox.setTextColor(getResources().getColor(R.color.black));
            stateBox.setText(data.getStringExtra("vStateCode"));

        }
        else if (requestCode == Utils.SELECT_CITY_REQ_CODE && data != null) {

            iCityId = data.getStringExtra("iCityId");
            cityBox.setTextColor(getResources().getColor(R.color.black));
            cityBox.setText(data.getStringExtra("vCity"));

        } else if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            cropImage(fileUri, fileUri);

        }else if(requestCode == SELECT_PICTURE){
            try {
                Uri cropPictureUrl = Uri.fromFile(getOutputMediaFile(MEDIA_TYPE_IMAGE));
                String realPathFromURI = new ImageFilePath().getPath(getActContext(), data.getData());
                File file = new File(realPathFromURI == null ? getImageUrlWithAuthority(getActContext(), data.getData()) : realPathFromURI);
                if (file.exists()) {
                    if (Build.VERSION.SDK_INT > 23) {
                        cropImage(FileProvider.getUriForFile(getActContext(), getApplicationContext().getPackageName() + ".provider", file), cropPictureUrl);
                    } else {
                        cropImage(Uri.fromFile(file), cropPictureUrl);
                    }

                } else {
                    cropImage(data.getData(), cropPictureUrl);
                }

            } catch (Exception e) {

                e.printStackTrace();
            }

        }else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == appLoginAct.RESULT_OK) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            Uri resultUri = result.getUri();
            generalFunc.storedata("vImgName", resultUri.toString());

            Picasso.with(getActContext()).load(resultUri)
                    .fit().into(userProfileImgView);
            insertImage = true;
            selectedImagePath = new ImageFilePath().getPath(getActContext(), resultUri);

        }
    }

    private void cropImage(final Uri sourceImage, Uri destinationImage) {

        try {
            CropImage.activity(sourceImage)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setDoneButtonText(generalFunc.retrieveLangLBl("Done", "LBL_DONE"))
                    .setCancelButtonText(generalFunc.retrieveLangLBl("Cancel", "LBL_CANCEL_TXT"))
                    .setMultiTouchEnabled(false)
                    .setAspectRatio(2048, 2048)
                    .setNoOutputImage(false)
                    .start(getContext(), this);
        } catch (Exception e) {

        }
    }

    private boolean isDeviceSupportCamera() {
        if (getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            return true;
        } else {
            return false;
        }
    }
    public static Uri writeToTempImageAndGetPathUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, Utils.TempProfileImageName, null);
        return Uri.parse(path);
    }

    public static String getImageUrlWithAuthority(Context context, Uri uri) {
        InputStream is = null;
        if (uri.getAuthority() != null) {
            try {
                is = context.getContentResolver().openInputStream(uri);
                Bitmap bmp = BitmapFactory.decodeStream(is);
                return writeToTempImageAndGetPathUri(context, bmp).toString();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


        }

        return null;
    }
    public void chooseFromGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);

    }

    private File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
        } else {
            return null;
        }


        return mediaFile;
    }

    public Uri getOutputMediaFileUri(int type) {
//        return Uri.fromFile(getOutputMediaFile(type));

        return FileProvider.getUriForFile(getActContext(), BuildConfig.APPLICATION_ID + ".provider", getOutputMediaFile(type));
    }

    public void chooseFromCamera() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    String responseString = "";

    public String[] generateImageParams(String key, String content) {
        String[] tempArr = new String[2];
        tempArr[0] = key;
        tempArr[1] = content;

        return tempArr;
    }

    public void execute(String tSessionId, String message) {

        ArrayList<String[]> paramsList = new ArrayList<>();
        paramsList.add(generateImageParams("iMemberId", generalFunc.getMemberId()));
        paramsList.add(generateImageParams("tSessionId", tSessionId));
        paramsList.add(generateImageParams("GeneralUserType", CommonUtilities.app_type));
        paramsList.add(generateImageParams("GeneralMemberId", generalFunc.getMemberId()));
        paramsList.add(generateImageParams("type", "uploadImage"));



        String filePath = generalFunc.decodeFile(selectedImagePath, Utils.ImageUpload_DESIREDWIDTH,
                Utils.ImageUpload_DESIREDHEIGHT, Utils.TempProfileImageName);


        File file = new File(filePath);


        MultipartBody.Part filePart = MultipartBody.Part.createFormData("vImage", Utils.TempProfileImageName, RequestBody.create(MediaType.parse("multipart/form-data"), file));


        HashMap<String, RequestBody> dataParams = new HashMap<>();

        for (int i = 0; i < paramsList.size(); i++) {
            String[] arrData = paramsList.get(i);

            dataParams.put(arrData[0], RequestBody.create(MediaType.parse("text/plain"), arrData[1]));
        }
        Call<Object> call = RestClient.getClient().uploadData(filePart, dataParams);

        call.enqueue(new Callback<Object>() {

            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.isSuccessful()) {
                    responseString = RestClient.getGSONBuilder().toJson(response.body());
                    generalFunc.storedata(CommonUtilities.USER_PROFILE_JSON, generalFunc.getJsonValue(CommonUtilities.message_str, responseString));
                } else {
                    responseString = "";
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {

                responseString = "";
            }

        });

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case GeneralFunctions.MY_PERMISSIONS_REQUEST: {
                if (generalFunc.isPermisionGranted()) {
                    new ImageSourceDialog().run();
                }
                break;

            }
        }
    }

    class ImageSourceDialog implements Runnable {

        @Override
        public void run() {
            // TODO Auto-generated method stub

            final Dialog dialog_img_update = new Dialog(getActContext(), R.style.ImageSourceDialogStyle);

            dialog_img_update.setContentView(R.layout.design_image_source_select);

            MTextView chooseImgHTxt = (MTextView) dialog_img_update.findViewById(R.id.chooseImgHTxt);
            chooseImgHTxt.setText(generalFunc.retrieveLangLBl("Choose Category", "LBL_CHOOSE_CATEGORY"));

            SelectableRoundedImageView cameraIconImgView = (SelectableRoundedImageView) dialog_img_update.findViewById(R.id.cameraIconImgView);
            SelectableRoundedImageView galleryIconImgView = (SelectableRoundedImageView) dialog_img_update.findViewById(R.id.galleryIconImgView);

            ImageView closeDialogImgView = (ImageView) dialog_img_update.findViewById(R.id.closeDialogImgView);

            closeDialogImgView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    if (dialog_img_update != null) {
                        dialog_img_update.cancel();
                    }
                }
            });

            new CreateRoundedView(getResources().getColor(R.color.appThemeColor_Dark_1), Utils.dipToPixels(getActContext(), 25), 0,
                    Color.parseColor("#00000000"), cameraIconImgView);

            cameraIconImgView.setColorFilter(getResources().getColor(R.color.appThemeColor_TXT_1));

            new CreateRoundedView(getResources().getColor(R.color.appThemeColor_Dark_1), Utils.dipToPixels(getActContext(), 25), 0,
                    Color.parseColor("#00000000"), galleryIconImgView);

            galleryIconImgView.setColorFilter(getResources().getColor(R.color.appThemeColor_TXT_1));


            cameraIconImgView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    if (dialog_img_update != null) {
                        dialog_img_update.cancel();
                    }

                    if (!isDeviceSupportCamera()) {
                        generalFunc.showMessage(llContentArea, generalFunc.retrieveLangLBl("", "LBL_NOT_SUPPORT_CAMERA_TXT"));
                    } else {
                        chooseFromCamera();
                    }

                }
            });

            galleryIconImgView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    if (dialog_img_update != null) {
                        dialog_img_update.cancel();
                    }

                    chooseFromGallery();


                }
            });

            dialog_img_update.setCanceledOnTouchOutside(true);

            Window window = dialog_img_update.getWindow();
            window.setGravity(Gravity.BOTTOM);

            window.setLayout(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            dialog_img_update.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            dialog_img_update.show();

        }

    }

    public static void setdata(int requestCode, int resultCode, Intent data) {

        if (requestCode == Utils.SELECT_COUNTRY_REQ_CODE && data != null) {

            vCountryCode = data.getStringExtra("vCountryCode");
            vPhoneCode = data.getStringExtra("vPhoneCode");
            isCountrySelected = true;

            countryBox.setText("+" + vPhoneCode);
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Utils.hideKeyboard(appLoginAct);
    }
}