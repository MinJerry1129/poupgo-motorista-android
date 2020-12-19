package com.general.functions;

import android.os.Bundle;
import android.support.v4.app.Fragment;

final /* synthetic */ class GeneralFunctions$$Lambda$4 implements GenerateAlertBox.HandleAlertBtnClick {
    private final GeneralFunctions arg$1;
    private final GenerateAlertBox arg$2;
    private final Fragment arg$3;
    private final Bundle arg$4;

    GeneralFunctions$$Lambda$4(GeneralFunctions generalFunctions, GenerateAlertBox generateAlertBox, Fragment fragment, Bundle bundle) {
        this.arg$1 = generalFunctions;
        this.arg$2 = generateAlertBox;
        this.arg$3 = fragment;
        this.arg$4 = bundle;
    }

    public void handleBtnClick(int i) {
        this.arg$1.lambda$verifyMobile$4$GeneralFunctions(this.arg$2, this.arg$3, this.arg$4, i);
    }
}
