package com.general.functions;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

final /* synthetic */ class GeneralFunctions$$Lambda$2 implements OnClickListener {
    private final GeneralFunctions arg$1;
    private final Bundle arg$2;
    private final Context arg$3;

    GeneralFunctions$$Lambda$2(GeneralFunctions generalFunctions, Bundle bundle, Context context) {
        this.arg$1 = generalFunctions;
        this.arg$2 = bundle;
        this.arg$3 = context;
    }

    public void onClick(View view) {
        this.arg$1.lambda$buildLowBalanceMessage$2$GeneralFunctions(this.arg$2, this.arg$3, view);
    }
}
