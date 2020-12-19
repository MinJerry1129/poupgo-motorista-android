package com.view.editBox;

import android.support.annotation.NonNull;

public abstract class METValidator {
    protected String errorMessage;

    public abstract boolean isValid(@NonNull CharSequence charSequence, boolean z);

    public METValidator(@NonNull String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @NonNull
    public String getErrorMessage() {
        return this.errorMessage;
    }

    public void setErrorMessage(@NonNull String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
