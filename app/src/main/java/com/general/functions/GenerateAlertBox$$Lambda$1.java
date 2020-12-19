package com.general.functions;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

final /* synthetic */ class GenerateAlertBox$$Lambda$1 implements OnClickListener {
    private final GenerateAlertBox arg$1;

    GenerateAlertBox$$Lambda$1(GenerateAlertBox generateAlertBox) {
        this.arg$1 = generateAlertBox;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$setPositiveBtn$1$GenerateAlertBox(dialogInterface, i);
    }
}
