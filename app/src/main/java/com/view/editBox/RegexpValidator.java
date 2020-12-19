package com.view.editBox;

import android.support.annotation.NonNull;

import java.util.regex.Pattern;

public class RegexpValidator extends METValidator {
    private Pattern pattern;

    public RegexpValidator(@NonNull String errorMessage, @NonNull String regex) {
        super(errorMessage);
        this.pattern = Pattern.compile(regex);
    }

    public RegexpValidator(@NonNull String errorMessage, @NonNull Pattern pattern) {
        super(errorMessage);
        this.pattern = pattern;
    }

    public boolean isValid(@NonNull CharSequence text, boolean isEmpty) {
        return this.pattern.matcher(text).matches();
    }
}
