package com.general.functions;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

final /* synthetic */ class GenerateAlertBox$$Lambda$2 implements OnItemClickListener {
    private final GenerateAlertBox.OnItemClickListener arg$1;

    GenerateAlertBox$$Lambda$2(GenerateAlertBox.OnItemClickListener onItemClickListener) {
        this.arg$1 = onItemClickListener;
    }

    public void onItemClick(AdapterView adapterView, View view, int i, long j) {
        GenerateAlertBox.lambda$createList$2$GenerateAlertBox(this.arg$1, adapterView, view, i, j);
    }
}
