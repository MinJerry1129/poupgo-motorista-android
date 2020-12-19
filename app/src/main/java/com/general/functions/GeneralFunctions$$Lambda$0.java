package com.general.functions;


final /* synthetic */ class GeneralFunctions$$Lambda$0 implements GenerateAlertBox.HandleAlertBtnClick {
    private final GenerateAlertBox arg$1;
    private final GeneralFunctions$OnAlertButtonClickListener arg$2;

    GeneralFunctions$$Lambda$0(GenerateAlertBox generateAlertBox, GeneralFunctions$OnAlertButtonClickListener onAlertButtonClickListener) {
        this.arg$1 = generateAlertBox;
        this.arg$2 = onAlertButtonClickListener;
    }

    public void handleBtnClick(int i) {
        GeneralFunctions.lambda$showGeneralMessage$0$GeneralFunctions(this.arg$1, this.arg$2, i);
    }
}
