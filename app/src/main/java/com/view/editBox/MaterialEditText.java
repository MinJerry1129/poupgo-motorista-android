package com.view.editBox;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.PorterDuff.Mode;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.TransformationMethod;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;

import com.poupgo.driver.R;
import com.general.functions.GeneralFunctions;
import com.general.functions.Utils;
import com.nineoldandroids.animation.ArgbEvaluator;
import com.nineoldandroids.animation.ObjectAnimator;

import org.slf4j.Marker;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class MaterialEditText extends AppCompatEditText {
    public static final int FLOATING_LABEL_HIGHLIGHT = 2;
    public static final int FLOATING_LABEL_NONE = 0;
    public static final int FLOATING_LABEL_NORMAL = 1;
    private Typeface accentTypeface;
    private boolean autoValidate;
    private int baseColor;
    private int bottomEllipsisSize;
    private float bottomLines;
    ObjectAnimator bottomLinesAnimator;
    private int bottomSpacing;
    private int bottomTextSize;
    private boolean charactersCountValid;
    private boolean checkCharactersCountAtBeginning;
    private Bitmap[] clearButtonBitmaps;
    private boolean clearButtonClicking;
    private boolean clearButtonTouched;
    private float currentBottomLines;
    private int errorColor;
    private int extraPaddingBottom;
    private int extraPaddingLeft;
    private int extraPaddingRight;
    private int extraPaddingTop;
    private boolean firstShown;
    private boolean floatingLabelAlwaysShown;
    private boolean floatingLabelAnimating;
    private boolean floatingLabelEnabled;
    private float floatingLabelFraction;
    private int floatingLabelPadding;
    private boolean floatingLabelShown;
    private CharSequence floatingLabelText;
    private int floatingLabelTextColor;
    private int floatingLabelTextSize;
    private ArgbEvaluator focusEvaluator = new ArgbEvaluator();
    private float focusFraction;
    private String helperText;
    private boolean helperTextAlwaysShown;
    private int helperTextColor = -1;
    private boolean hideUnderline;
    private boolean highlightFloatingLabel;
    private Bitmap[] iconLeftBitmaps;
    private int iconOuterHeight;
    private int iconOuterWidth;
    private int iconPadding;
    private Bitmap[] iconRightBitmaps;
    private int iconSize;
    OnFocusChangeListener innerFocusChangeListener;
    private int innerPaddingBottom;
    private int innerPaddingLeft;
    private int innerPaddingRight;
    private int innerPaddingTop;
    ObjectAnimator labelAnimator;
    ObjectAnimator labelFocusAnimator;
    private METLengthChecker lengthChecker;
    private Context mContext;
    private int maxCharacters;
    private int minBottomLines;
    private int minBottomTextLines;
    private int minCharacters;
    OnFocusChangeListener outerFocusChangeListener;
    Paint paint = new Paint(1);
    private int primaryColor;
    private boolean showClearButton;
    private boolean singleLineEllipsis;
    private String tempErrorText;
    private ColorStateList textColorHintStateList;
    private ColorStateList textColorStateList;
    StaticLayout textLayout;
    TextPaint textPaint = new TextPaint(1);
    private Typeface typeface;
    private int underlineColor;
    private boolean validateOnFocusLost;
    private List<METValidator> validators;

    public @interface FloatingLabelType {
    }

    public MaterialEditText(Context context) {
        super(context);
        this.mContext = context;
        init(context, null);
    }

    public MaterialEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init(context, attrs);
    }

    @TargetApi(21)
    public MaterialEditText(Context context, AttributeSet attrs, int style) {
        super(context, attrs, style);
        this.mContext = context;
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        Context context2 = context;
        AttributeSet attributeSet = attrs;
        this.iconSize = getPixel(32);
        this.iconOuterWidth = getPixel(32);
        this.iconOuterHeight = getPixel(32);
        this.bottomSpacing = getResources().getDimensionPixelSize(R.dimen.inner_components_spacing);
        this.bottomEllipsisSize = getResources().getDimensionPixelSize(R.dimen.bottom_ellipsis_height);
        TypedArray typedArray = context2.obtainStyledAttributes(attributeSet, R.styleable.MaterialEditText);
        this.textColorStateList = typedArray.getColorStateList(R.styleable.MaterialEditText_met_textColor);
        this.textColorHintStateList = typedArray.getColorStateList(R.styleable.MaterialEditText_met_textColorHint);
        this.baseColor = typedArray.getColor(R.styleable.MaterialEditText_met_baseColor, -16777216);
        TypedValue primaryColorTypedValue = new TypedValue();
        int defaultPrimaryColor;
        try {
            if (VERSION.SDK_INT >= 21) {
                context.getTheme().resolveAttribute(16843827, primaryColorTypedValue, true);
                defaultPrimaryColor = primaryColorTypedValue.data;
                this.primaryColor = typedArray.getColor(R.styleable.MaterialEditText_met_primaryColor, defaultPrimaryColor);
                setFloatingLabelInternal(typedArray.getInt(R.styleable.MaterialEditText_met_floatingLabel, 0));
                this.errorColor = typedArray.getColor(R.styleable.MaterialEditText_met_errorColor, Color.parseColor("#e7492E"));
                this.minCharacters = typedArray.getInt(R.styleable.MaterialEditText_met_minCharacters, 0);
                this.maxCharacters = typedArray.getInt(R.styleable.MaterialEditText_met_maxCharacters, 0);
                this.singleLineEllipsis = typedArray.getBoolean(R.styleable.MaterialEditText_met_singleLineEllipsis, false);
                this.helperText = typedArray.getString(R.styleable.MaterialEditText_met_helperText);
                this.helperTextColor = typedArray.getColor(R.styleable.MaterialEditText_met_helperTextColor, -1);
                this.minBottomTextLines = typedArray.getInt(R.styleable.MaterialEditText_met_minBottomTextLines, 0);
                String fontPathForAccent = typedArray.getString(0);
                if (!(fontPathForAccent == null || isInEditMode())) {
                    this.accentTypeface = getCustomTypeface(fontPathForAccent);
                    this.textPaint.setTypeface(this.accentTypeface);
                }
                String fontPathForView = typedArray.getString(R.styleable.MaterialEditText_met_typeface);
                if (!(fontPathForView == null || isInEditMode())) {
                    this.typeface = getCustomTypeface(fontPathForView);
                    setTypeface(this.typeface);
                }
                this.floatingLabelText = typedArray.getString(R.styleable.MaterialEditText_met_floatingLabelText);
                if (this.floatingLabelText == null) {
                    this.floatingLabelText = getHint();
                }
                this.floatingLabelPadding = typedArray.getDimensionPixelSize(R.styleable.MaterialEditText_met_floatingLabelPadding, this.bottomSpacing);
                this.floatingLabelTextSize = typedArray.getDimensionPixelSize(R.styleable.MaterialEditText_met_floatingLabelTextSize, getResources().getDimensionPixelSize(R.dimen.floating_label_text_size));
                this.floatingLabelTextColor = typedArray.getColor(R.styleable.MaterialEditText_met_floatingLabelTextColor, -1);
                this.floatingLabelAnimating = typedArray.getBoolean(R.styleable.MaterialEditText_met_floatingLabelAnimating, true);
                this.bottomTextSize = typedArray.getDimensionPixelSize(R.styleable.MaterialEditText_met_bottomTextSize, getResources().getDimensionPixelSize(R.dimen.bottom_text_size));
                this.hideUnderline = typedArray.getBoolean(R.styleable.MaterialEditText_met_hideUnderline, false);
                this.underlineColor = typedArray.getColor(R.styleable.MaterialEditText_met_underlineColor, -1);
                this.autoValidate = typedArray.getBoolean(R.styleable.MaterialEditText_met_autoValidate, false);
                this.iconLeftBitmaps = generateIconBitmaps(typedArray.getResourceId(R.styleable.MaterialEditText_met_iconLeft, -1));
                this.iconRightBitmaps = generateIconBitmaps(typedArray.getResourceId(R.styleable.MaterialEditText_met_iconRight, -1));
                this.showClearButton = typedArray.getBoolean(R.styleable.MaterialEditText_met_clearButton, false);
                this.clearButtonBitmaps = generateIconBitmaps((int) R.mipmap.met_ic_clear);
                this.iconPadding = typedArray.getDimensionPixelSize(R.styleable.MaterialEditText_met_iconPadding, getPixel(16));
                this.floatingLabelAlwaysShown = typedArray.getBoolean(R.styleable.MaterialEditText_met_floatingLabelAlwaysShown, false);
                this.helperTextAlwaysShown = typedArray.getBoolean(R.styleable.MaterialEditText_met_helperTextAlwaysShown, false);
                this.validateOnFocusLost = typedArray.getBoolean(R.styleable.MaterialEditText_met_validateOnFocusLost, false);
                this.checkCharactersCountAtBeginning = typedArray.getBoolean(R.styleable.MaterialEditText_met_checkCharactersCountAtBeginning, true);
                typedArray.recycle();
                TypedArray paddingsTypedArray = context2.obtainStyledAttributes(attributeSet, new int[]{16842965, 16842966, 16842967, 16842968, 16842969});
                int padding = paddingsTypedArray.getDimensionPixelSize(0, 0);
                this.innerPaddingLeft = paddingsTypedArray.getDimensionPixelSize(1, padding);
                this.innerPaddingTop = paddingsTypedArray.getDimensionPixelSize(2, padding);
                this.innerPaddingRight = paddingsTypedArray.getDimensionPixelSize(3, padding);
                this.innerPaddingBottom = paddingsTypedArray.getDimensionPixelSize(4, padding);
                paddingsTypedArray.recycle();
                if (VERSION.SDK_INT >= 16) {
                    setBackground(null);
                } else {
                    setBackgroundDrawable(null);
                }
                if (this.singleLineEllipsis) {
                    TransformationMethod transformationMethod = getTransformationMethod();
                    setSingleLine();
                    setTransformationMethod(transformationMethod);
                }
                initMinBottomLines();
                initPadding();
                initText();
                initFloatingLabel();
                initTextWatcher();
                checkCharactersCount();
                if (isRTL()) {
                    setTextDirection(TEXT_DIRECTION_RTL);
                    return;
                }
                return;
            }
            throw new RuntimeException("SDK_INT less than LOLLIPOP");
        } catch (Exception e) {
            int colorPrimaryId = getResources().getIdentifier("colorPrimary", "attr", getContext().getPackageName());
            if (colorPrimaryId != 0) {
                context.getTheme().resolveAttribute(colorPrimaryId, primaryColorTypedValue, true);
                defaultPrimaryColor = primaryColorTypedValue.data;
            } else {
                throw new RuntimeException("colorPrimary not found");
            }
        }
    }

    private void initText() {
        if (TextUtils.isEmpty(getText())) {
            resetHintTextColor();
        } else {
            CharSequence text = getText();
            setText(null);
            resetHintTextColor();
            setText(text);
            setSelection(text.length());
            this.floatingLabelFraction = 1.0f;
            this.floatingLabelShown = true;
        }
        resetTextColor();
    }

    private void initTextWatcher() {
        addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable s) {
                MaterialEditText.this.checkCharactersCount();
                if (MaterialEditText.this.autoValidate) {
                    MaterialEditText.this.validate();
                } else {
                    MaterialEditText.this.setError(null);
                }
                MaterialEditText.this.postInvalidate();
            }
        });
    }

    private Typeface getCustomTypeface(@NonNull String fontPath) {
        return Typeface.createFromAsset(getContext().getAssets(), fontPath);
    }

    public void setIconLeft(@DrawableRes int res) {
        this.iconLeftBitmaps = generateIconBitmaps(res);
        initPadding();
    }

    public void setIconLeft(Drawable drawable) {
        this.iconLeftBitmaps = generateIconBitmaps(drawable);
        initPadding();
    }

    public void setIconLeft(Bitmap bitmap) {
        this.iconLeftBitmaps = generateIconBitmaps(bitmap);
        initPadding();
    }

    public void setIconRight(@DrawableRes int res) {
        this.iconRightBitmaps = generateIconBitmaps(res);
        initPadding();
    }

    public void setIconRight(Drawable drawable) {
        this.iconRightBitmaps = generateIconBitmaps(drawable);
        initPadding();
    }

    public void setIconRight(Bitmap bitmap) {
        this.iconRightBitmaps = generateIconBitmaps(bitmap);
        initPadding();
    }

    public boolean isShowClearButton() {
        return this.showClearButton;
    }

    public void setShowClearButton(boolean show) {
        this.showClearButton = show;
        correctPaddings();
    }

    private Bitmap[] generateIconBitmaps(@DrawableRes int origin) {
        if (origin == -1) {
            return null;
        }
        Options options = new Options();
        int i = 1;
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(), origin, options);
        int size = Math.max(options.outWidth, options.outHeight);
        if (size > this.iconSize) {
            i = size / this.iconSize;
        }
        options.inSampleSize = i;
        options.inJustDecodeBounds = false;
        return generateIconBitmaps(BitmapFactory.decodeResource(getResources(), origin, options));
    }

    private Bitmap[] generateIconBitmaps(Drawable drawable) {
        if (drawable == null) {
            return null;
        }
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return generateIconBitmaps(Bitmap.createScaledBitmap(bitmap, this.iconSize, this.iconSize, false));
    }

    private Bitmap[] generateIconBitmaps(Bitmap origin) {
        if (origin == null) {
            return null;
        }
        Bitmap[] iconBitmaps = new Bitmap[4];
        origin = scaleIcon(origin);
        iconBitmaps[0] = origin.copy(Config.ARGB_8888, true);
        new Canvas(iconBitmaps[0]).drawColor((this.baseColor & ViewCompat.MEASURED_SIZE_MASK) | (Colors.isLight(this.baseColor) ? -16777216 : -1979711488), Mode.SRC_IN);
        iconBitmaps[1] = origin.copy(Config.ARGB_8888, true);
        new Canvas(iconBitmaps[1]).drawColor(this.primaryColor, Mode.SRC_IN);
        iconBitmaps[2] = origin.copy(Config.ARGB_8888, true);
        new Canvas(iconBitmaps[2]).drawColor((this.baseColor & ViewCompat.MEASURED_SIZE_MASK) | (Colors.isLight(this.baseColor) ? 1275068416 : 1107296256), Mode.SRC_IN);
        iconBitmaps[3] = origin.copy(Config.ARGB_8888, true);
        new Canvas(iconBitmaps[3]).drawColor(this.errorColor, Mode.SRC_IN);
        return iconBitmaps;
    }

    private Bitmap scaleIcon(Bitmap origin) {
        int width = origin.getWidth();
        int height = origin.getHeight();
        int size = Math.max(width, height);
        if (size == this.iconSize || size <= this.iconSize) {
            return origin;
        }
        int scaledWidth;
        int scaledHeight;
        if (width > this.iconSize) {
            scaledWidth = this.iconSize;
            scaledHeight = (int) (((float) this.iconSize) * (((float) height) / ((float) width)));
        } else {
            scaledHeight = this.iconSize;
            scaledWidth = (int) (((float) this.iconSize) * (((float) width) / ((float) height)));
        }
        return Bitmap.createScaledBitmap(origin, scaledWidth, scaledHeight, false);
    }

    public float getFloatingLabelFraction() {
        return this.floatingLabelFraction;
    }

    public void setFloatingLabelFraction(float floatingLabelFraction) {
        this.floatingLabelFraction = floatingLabelFraction;
        invalidate();
    }

    public float getFocusFraction() {
        return this.focusFraction;
    }

    public void setFocusFraction(float focusFraction) {
        this.focusFraction = focusFraction;
        invalidate();
    }

    public float getCurrentBottomLines() {
        return this.currentBottomLines;
    }

    public void setCurrentBottomLines(float currentBottomLines) {
        this.currentBottomLines = currentBottomLines;
        initPadding();
    }

    public boolean isFloatingLabelAlwaysShown() {
        return this.floatingLabelAlwaysShown;
    }

    public void setFloatingLabelAlwaysShown(boolean floatingLabelAlwaysShown) {
        this.floatingLabelAlwaysShown = floatingLabelAlwaysShown;
        invalidate();
    }

    public boolean isHelperTextAlwaysShown() {
        return this.helperTextAlwaysShown;
    }

    public void setHelperTextAlwaysShown(boolean helperTextAlwaysShown) {
        this.helperTextAlwaysShown = helperTextAlwaysShown;
        invalidate();
    }

    @Nullable
    public Typeface getAccentTypeface() {
        return this.accentTypeface;
    }

    public void setAccentTypeface(Typeface accentTypeface) {
        this.accentTypeface = accentTypeface;
        this.textPaint.setTypeface(accentTypeface);
        postInvalidate();
    }

    public boolean isHideUnderline() {
        return this.hideUnderline;
    }

    public void setHideUnderline(boolean hideUnderline) {
        this.hideUnderline = hideUnderline;
        initPadding();
        postInvalidate();
    }

    public int getUnderlineColor() {
        return this.underlineColor;
    }

    public void setUnderlineColor(int color) {
        this.underlineColor = color;
        postInvalidate();
    }

    public CharSequence getFloatingLabelText() {
        return this.floatingLabelText;
    }

    public void setFloatingLabelText(@Nullable CharSequence floatingLabelText) {
        this.floatingLabelText = floatingLabelText == null ? getHint() : floatingLabelText;
        postInvalidate();
    }

    public void setBothText(String lablel_str, String hint_str) {
        this.floatingLabelText = lablel_str == null ? getHint() : lablel_str;
        setHint(hint_str);
        postInvalidate();
    }

    public void setBothText(String text_str) {
        this.floatingLabelText = text_str == null ? getHint() : text_str;
        setHint(text_str);
        postInvalidate();
    }

    public int getFloatingLabelTextSize() {
        return this.floatingLabelTextSize;
    }

    public void setFloatingLabelTextSize(int size) {
        this.floatingLabelTextSize = size;
        initPadding();
    }

    public int getFloatingLabelTextColor() {
        return this.floatingLabelTextColor;
    }

    public void setFloatingLabelTextColor(int color) {
        this.floatingLabelTextColor = color;
        postInvalidate();
    }

    public int getBottomTextSize() {
        return this.bottomTextSize;
    }

    public void setBottomTextSize(int size) {
        this.bottomTextSize = size;
        initPadding();
    }

    private int getPixel(int dp) {
        return Utils.dipToPixels(getContext(), (float) dp);
    }

    private void initPadding() {
        this.extraPaddingTop = this.floatingLabelEnabled ? this.floatingLabelTextSize + this.floatingLabelPadding : this.floatingLabelPadding;
        this.textPaint.setTextSize((float) this.bottomTextSize);
        FontMetrics textMetrics = this.textPaint.getFontMetrics();
        this.extraPaddingBottom = ((int) ((textMetrics.descent - textMetrics.ascent) * this.currentBottomLines)) + (this.hideUnderline ? this.bottomSpacing : this.bottomSpacing * 2);
        int i = 0;
        this.extraPaddingLeft = this.iconLeftBitmaps == null ? 0 : this.iconOuterWidth + this.iconPadding;
        if (this.iconRightBitmaps != null) {
            i = this.iconPadding + this.iconOuterWidth;
        }
        this.extraPaddingRight = i;
        correctPaddings();
    }

    private void initMinBottomLines() {
        boolean extendBottom;
        int i = 1;
        if (this.minCharacters <= 0 && this.maxCharacters <= 0 && !this.singleLineEllipsis && this.tempErrorText == null) {
            if (this.helperText == null) {
                extendBottom = false;
                if (this.minBottomTextLines > 0) {
                    i = this.minBottomTextLines;
                } else if (extendBottom) {
                    i = 0;
                }
                this.minBottomLines = i;
                this.currentBottomLines = (float) i;
            }
        }
        extendBottom = true;
        if (this.minBottomTextLines > 0) {
            i = this.minBottomTextLines;
        } else if (extendBottom) {
            i = 0;
        }
        this.minBottomLines = i;
        this.currentBottomLines = (float) i;
    }

    @Deprecated
    public final void setPadding(int left, int top, int right, int bottom) {
        super.setPadding(left, top, right, bottom);
    }

    public void setPaddings(int left, int top, int right, int bottom) {
        this.innerPaddingTop = top;
        this.innerPaddingBottom = bottom;
        this.innerPaddingLeft = left;
        this.innerPaddingRight = right;
        correctPaddings();
    }

    private void correctPaddings() {
        int buttonsWidthLeft = 0;
        int buttonsWidthRight = 0;
        int buttonsWidth = this.iconOuterWidth * getButtonsCount();
        if (isRTL()) {
            buttonsWidthLeft = buttonsWidth;
        } else {
            buttonsWidthRight = buttonsWidth;
        }
        super.setPadding((this.innerPaddingLeft + this.extraPaddingLeft) + buttonsWidthLeft, this.innerPaddingTop + this.extraPaddingTop, (this.innerPaddingRight + this.extraPaddingRight) + buttonsWidthRight, this.innerPaddingBottom + this.extraPaddingBottom);
    }

    private int getButtonsCount() {
        return this.isShowClearButton() ? 1 : 0;
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!this.firstShown) {
            this.firstShown = true;
        }
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            adjustBottomLines();
        }
    }

    private boolean adjustBottomLines() {
        if (getWidth() == 0) {
            return false;
        }
        int destBottomLines;
        textPaint.setTextSize(bottomTextSize);
        if (tempErrorText != null || helperText != null) {
            Layout.Alignment alignment = (getGravity() & Gravity.RIGHT) == Gravity.RIGHT || isRTL() ?
                    Layout.Alignment.ALIGN_OPPOSITE : (getGravity() & Gravity.LEFT) == Gravity.LEFT ?
                    Layout.Alignment.ALIGN_NORMAL : Layout.Alignment.ALIGN_CENTER;
            textLayout = new StaticLayout(tempErrorText != null ? tempErrorText : helperText, textPaint, getWidth() - getBottomTextLeftOffset() - getBottomTextRightOffset() - getPaddingLeft() - getPaddingRight(), alignment, 1.0f, 0.0f, true);
            destBottomLines = Math.max(textLayout.getLineCount(), minBottomTextLines);
        } else {
            destBottomLines = minBottomLines;
        }
        if (bottomLines != destBottomLines) {
            getBottomLinesAnimator(destBottomLines).start();
        }
        bottomLines = destBottomLines;
        return true;
    }

    public int getInnerPaddingTop() {
        return this.innerPaddingTop;
    }

    public int getInnerPaddingBottom() {
        return this.innerPaddingBottom;
    }

    public int getInnerPaddingLeft() {
        return this.innerPaddingLeft;
    }

    public int getInnerPaddingRight() {
        return this.innerPaddingRight;
    }

    private void initFloatingLabel() {
        addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable s) {
                if (!MaterialEditText.this.floatingLabelEnabled) {
                    return;
                }
                if (s.length() == 0) {
                    if (MaterialEditText.this.floatingLabelShown) {
                        MaterialEditText.this.floatingLabelShown = false;
                        MaterialEditText.this.getLabelAnimator().reverse();
                    }
                } else if (!MaterialEditText.this.floatingLabelShown) {
                    MaterialEditText.this.floatingLabelShown = true;
                    MaterialEditText.this.getLabelAnimator().start();
                }
            }
        });
        this.innerFocusChangeListener = new OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (MaterialEditText.this.floatingLabelEnabled && MaterialEditText.this.highlightFloatingLabel) {
                    if (hasFocus) {
                        MaterialEditText.this.getLabelFocusAnimator().start();
                    } else {
                        MaterialEditText.this.getLabelFocusAnimator().reverse();
                        Utils.hideKeyboard(MaterialEditText.this.mContext);
                    }
                }
                if (MaterialEditText.this.validateOnFocusLost && !hasFocus) {
                    MaterialEditText.this.validate();
                }
                if (MaterialEditText.this.outerFocusChangeListener != null) {
                    MaterialEditText.this.outerFocusChangeListener.onFocusChange(v, hasFocus);
                }
            }
        };
        super.setOnFocusChangeListener(this.innerFocusChangeListener);
    }

    public boolean isValidateOnFocusLost() {
        return this.validateOnFocusLost;
    }

    public void setValidateOnFocusLost(boolean validate) {
        this.validateOnFocusLost = validate;
    }

    public void setBaseColor(int color) {
        if (this.baseColor != color) {
            this.baseColor = color;
        }
        initText();
        postInvalidate();
    }

    public void setPrimaryColor(int color) {
        this.primaryColor = color;
        postInvalidate();
    }

    public void setMetTextColor(int color) {
        this.textColorStateList = ColorStateList.valueOf(color);
        resetTextColor();
    }

    public void setMetTextColor(ColorStateList colors) {
        this.textColorStateList = colors;
        resetTextColor();
    }

    private void resetTextColor() {
        if (this.textColorStateList == null) {
            int[][] r2 = new int[2][];
            r2[0] = new int[]{16842910};
            r2[1] = EMPTY_STATE_SET;
            this.textColorStateList = new ColorStateList(r2, new int[]{(this.baseColor & ViewCompat.MEASURED_SIZE_MASK) | -553648128, (this.baseColor & ViewCompat.MEASURED_SIZE_MASK) | 1140850688});
            setTextColor(this.textColorStateList);
            return;
        }
        setTextColor(this.textColorStateList);
    }

    public void setMetHintTextColor(int color) {
        this.textColorHintStateList = ColorStateList.valueOf(color);
        resetHintTextColor();
    }

    public void setMetHintTextColor(ColorStateList colors) {
        this.textColorHintStateList = colors;
        resetHintTextColor();
    }

    private void resetHintTextColor() {
        if (this.textColorHintStateList == null) {
            setHintTextColor((this.baseColor & ViewCompat.MEASURED_SIZE_MASK) | 1140850688);
        } else {
            setHintTextColor(this.textColorHintStateList);
        }
    }

    private void setFloatingLabelInternal(int mode) {
        switch (mode) {
            case 1:
                this.floatingLabelEnabled = true;
                this.highlightFloatingLabel = false;
                return;
            case 2:
                this.floatingLabelEnabled = true;
                this.highlightFloatingLabel = true;
                return;
            default:
                this.floatingLabelEnabled = false;
                this.highlightFloatingLabel = false;
                return;
        }
    }

    public void setFloatingLabel(@FloatingLabelType int mode) {
        setFloatingLabelInternal(mode);
        initPadding();
    }

    public int getFloatingLabelPadding() {
        return this.floatingLabelPadding;
    }

    public void setFloatingLabelPadding(int padding) {
        this.floatingLabelPadding = padding;
        postInvalidate();
    }

    public boolean isFloatingLabelAnimating() {
        return this.floatingLabelAnimating;
    }

    public void setFloatingLabelAnimating(boolean animating) {
        this.floatingLabelAnimating = animating;
    }

    public void setSingleLineEllipsis() {
        setSingleLineEllipsis(true);
    }

    public void setSingleLineEllipsis(boolean enabled) {
        this.singleLineEllipsis = enabled;
        initMinBottomLines();
        initPadding();
        postInvalidate();
    }

    public int getMaxCharacters() {
        return this.maxCharacters;
    }

    public void setMaxCharacters(int max) {
        this.maxCharacters = max;
        initMinBottomLines();
        initPadding();
        postInvalidate();
    }

    public int getMinCharacters() {
        return this.minCharacters;
    }

    public void setMinCharacters(int min) {
        this.minCharacters = min;
        initMinBottomLines();
        initPadding();
        postInvalidate();
    }

    public int getMinBottomTextLines() {
        return this.minBottomTextLines;
    }

    public void setMinBottomTextLines(int lines) {
        this.minBottomTextLines = lines;
        initMinBottomLines();
        initPadding();
        postInvalidate();
    }

    public boolean isAutoValidate() {
        return this.autoValidate;
    }

    public void setAutoValidate(boolean autoValidate) {
        this.autoValidate = autoValidate;
        if (autoValidate) {
            validate();
        }
    }

    public int getErrorColor() {
        return this.errorColor;
    }

    public void setErrorColor(int color) {
        this.errorColor = color;
        postInvalidate();
    }

    public String getHelperText() {
        return this.helperText;
    }

    public void setHelperText(CharSequence helperText) {
        this.helperText = helperText == null ? null : helperText.toString();
        if (adjustBottomLines()) {
            postInvalidate();
        }
    }

    public int getHelperTextColor() {
        return this.helperTextColor;
    }

    public void setHelperTextColor(int color) {
        this.helperTextColor = color;
        postInvalidate();
    }

    public CharSequence getError() {
        return this.tempErrorText;
    }

    public void setError(CharSequence errorText) {
        this.tempErrorText = errorText == null ? null : errorText.toString();
        if (adjustBottomLines()) {
            postInvalidate();
        }
    }

    private boolean isInternalValid() {
        return this.tempErrorText == null && isCharactersCountValid();
    }

    @Deprecated
    public boolean isValid(String regex) {
        if (regex == null) {
            return false;
        }
        return Pattern.compile(regex).matcher(getText()).matches();
    }

    @Deprecated
    public boolean validate(String regex, CharSequence errorText) {
        boolean isValid = isValid(regex);
        if (!isValid) {
            setError(errorText);
        }
        postInvalidate();
        return isValid;
    }

    public boolean validateWith(@NonNull METValidator validator) {
        CharSequence text = getText();
        boolean isValid = validator.isValid(text, text.length() == 0);
        if (!isValid) {
            setError(validator.getErrorMessage());
        }
        postInvalidate();
        return isValid;
    }

    public boolean validate() {
        if (this.validators != null) {
            if (!this.validators.isEmpty()) {
                CharSequence text = getText();
                boolean isEmpty = text.length() == 0;
                boolean isValid = true;
                for (METValidator validator : this.validators) {
                    boolean z = isValid && validator.isValid(text, isEmpty);
                    isValid = z;
                    if (!isValid) {
                        setError(validator.getErrorMessage());
                        break;
                    }
                }
                if (isValid) {
                    setError(null);
                }
                postInvalidate();
                return isValid;
            }
        }
        return true;
    }

    public boolean hasValidators() {
        return (this.validators == null || this.validators.isEmpty()) ? false : true;
    }

    public MaterialEditText addValidator(METValidator validator) {
        if (this.validators == null) {
            this.validators = new ArrayList();
        }
        this.validators.add(validator);
        return this;
    }

    public void clearValidators() {
        if (this.validators != null) {
            this.validators.clear();
        }
    }

    @Nullable
    public List<METValidator> getValidators() {
        return this.validators;
    }

    public void setLengthChecker(METLengthChecker lengthChecker) {
        this.lengthChecker = lengthChecker;
    }

    public void setOnFocusChangeListener(OnFocusChangeListener listener) {
        if (this.innerFocusChangeListener == null) {
            super.setOnFocusChangeListener(listener);
        } else {
            this.outerFocusChangeListener = listener;
        }
    }

    private ObjectAnimator getLabelAnimator() {
        if (this.labelAnimator == null) {
            this.labelAnimator = ObjectAnimator.ofFloat((Object) this, "floatingLabelFraction", 0.0f, 1.0f);
        }
        this.labelAnimator.setDuration(this.floatingLabelAnimating ? 300 : 0);
        return this.labelAnimator;
    }

    public ObjectAnimator getLabelFocusAnimator() {
        if (this.labelFocusAnimator == null) {
            this.labelFocusAnimator = ObjectAnimator.ofFloat((Object) this, "focusFraction", 0.0f, 1.0f);
        }
        return this.labelFocusAnimator;
    }

    private ObjectAnimator getBottomLinesAnimator(float destBottomLines) {
        if (this.bottomLinesAnimator == null) {
            this.bottomLinesAnimator = ObjectAnimator.ofFloat((Object) this, "currentBottomLines", destBottomLines);
        } else {
            this.bottomLinesAnimator.cancel();
            this.bottomLinesAnimator.setFloatValues(destBottomLines);
        }
        return this.bottomLinesAnimator;
    }

    protected void onDraw(@NonNull Canvas canvas) {

        int lineStartY;
        int lineStartY2;
        float xOffset;
        Canvas canvas2 = canvas;
        int startX = getScrollX() + (this.iconLeftBitmaps == null ? 0 : this.iconOuterWidth + this.iconPadding);
        int endX = getScrollX() + (this.iconRightBitmaps == null ? getWidth() : (getWidth() - this.iconOuterWidth) - this.iconPadding);
        int lineStartY3 = (getScrollY() + getHeight()) - getPaddingBottom();
        this.paint.setAlpha(255);
        if (this.iconLeftBitmaps != null) {
            int i = !isInternalValid() ? 3 : !isEnabled() ? 2 : hasFocus() ? 1 : 0;
            canvas2.drawBitmap(this.iconLeftBitmaps[i], (float) (((startX - this.iconPadding) - this.iconOuterWidth) + ((this.iconOuterWidth - this.iconLeftBitmaps[i].getWidth()) / 2)), (float) (((this.bottomSpacing + lineStartY3) - this.iconOuterHeight) + ((this.iconOuterHeight - this.iconLeftBitmaps[i].getHeight()) / 2)), this.paint);
        }
        if (this.iconRightBitmaps != null) {
            int i = !isInternalValid() ? 3 : !isEnabled() ? 2 : hasFocus() ? 1 : 0;
            canvas2.drawBitmap(this.iconRightBitmaps[i], (float) ((this.iconPadding + endX) + ((this.iconOuterWidth - this.iconRightBitmaps[i].getWidth()) / 2)), (float) (((this.bottomSpacing + lineStartY3) - this.iconOuterHeight) + ((this.iconOuterHeight - this.iconRightBitmaps[i].getHeight()) / 2)), this.paint);
        }
        if (hasFocus() && this.showClearButton && !TextUtils.isEmpty(getText())) {
            int buttonLeft;
            this.paint.setAlpha(255);
            if (isRTL()) {
                buttonLeft = startX;
            } else {
                buttonLeft = endX - this.iconOuterWidth;
            }
            Bitmap clearButtonBitmap = this.clearButtonBitmaps[buttonLeft];
            canvas2.drawBitmap(clearButtonBitmap, (float) (buttonLeft + ((this.iconOuterWidth - clearButtonBitmap.getWidth()) / 2)), (float) (((this.bottomSpacing + lineStartY3) - this.iconOuterHeight) + ((this.iconOuterHeight - clearButtonBitmap.getHeight()) / 2)), this.paint);
        }
        if (!this.hideUnderline) {
            lineStartY = lineStartY3 + this.bottomSpacing;
            if (isInternalValid()) {
                lineStartY2 = lineStartY;
                if (!isEnabled()) {
                    this.paint.setColor(this.underlineColor != -1 ? this.underlineColor : (this.baseColor & ViewCompat.MEASURED_SIZE_MASK) | 1140850688);
                    float interval = (float) getPixel(1);
                    float xOffset2 = 0.0f;
                    while (true) {
                        xOffset = xOffset2;
                        if (xOffset >= ((float) getWidth())) {
                            break;
                        }
                        float interval2 = interval;
                        canvas2.drawRect(((float) startX) + xOffset, (float) lineStartY2, (((float) startX) + xOffset) + interval, (float) (getPixel(1) + lineStartY2), this.paint);
                        xOffset2 = xOffset + (interval2 * 3.0f);
                        interval = interval2;
                    }
                } else if (hasFocus()) {
                    this.paint.setColor(this.primaryColor);
                    canvas2.drawRect((float) startX, (float) lineStartY2, (float) endX, (float) (lineStartY2 + getPixel(2)), this.paint);
                } else {
                    this.paint.setColor(this.underlineColor != -1 ? this.underlineColor : (this.baseColor & ViewCompat.MEASURED_SIZE_MASK) | 503316480);
                    canvas2.drawRect((float) startX, (float) lineStartY2, (float) endX, (float) (lineStartY2 + getPixel(1)), this.paint);
                }
            } else {
                this.paint.setColor(this.errorColor);
                lineStartY2 = lineStartY;
                canvas2.drawRect((float) startX, (float) lineStartY, (float) endX, (float) (getPixel(2) + lineStartY), this.paint);
            }
            lineStartY3 = lineStartY2;
        }
        this.textPaint.setTextSize((float) this.bottomTextSize);
        FontMetrics textMetrics = this.textPaint.getFontMetrics();
        float relativeHeight = (-textMetrics.ascent) - textMetrics.descent;
        float bottomTextPadding = (((float) this.bottomTextSize) + textMetrics.ascent) + textMetrics.descent;
        if ((hasFocus() && hasCharactersCounter()) || !isCharactersCountValid()) {
            this.textPaint.setColor(isCharactersCountValid() ? (this.baseColor & ViewCompat.MEASURED_SIZE_MASK) | 1140850688 : this.errorColor);
            String charactersCounterText = getCharactersCounterText();
            canvas2.drawText(charactersCounterText, isRTL() ? (float) startX : ((float) endX) - this.textPaint.measureText(charactersCounterText), ((float) (this.bottomSpacing + lineStartY3)) + relativeHeight, this.textPaint);
        }
        if (this.textLayout != null && (this.tempErrorText != null || ((this.helperTextAlwaysShown || hasFocus()) && !TextUtils.isEmpty(this.helperText)))) {
            TextPaint textPaint = this.textPaint;
            lineStartY = this.tempErrorText != null ? this.errorColor : this.helperTextColor != -1 ? this.helperTextColor : (this.baseColor & ViewCompat.MEASURED_SIZE_MASK) | 1140850688;
            textPaint.setColor(lineStartY);
            canvas.save();
            if (isRTL()) {
                canvas2.translate((float) (endX - this.textLayout.getWidth()), ((float) (this.bottomSpacing + lineStartY3)) - bottomTextPadding);
            } else {
                canvas2.translate((float) (getBottomTextLeftOffset() + startX), ((float) (this.bottomSpacing + lineStartY3)) - bottomTextPadding);
            }
            this.textLayout.draw(canvas2);
            canvas.restore();
        }
        if (!this.floatingLabelEnabled || TextUtils.isEmpty(this.floatingLabelText)) {
        } else {
            int floatingLabelStartY;
            float f;
            this.textPaint.setTextSize((float) this.floatingLabelTextSize);
            this.textPaint.setColor((Integer) this.focusEvaluator.evaluate(this.focusFraction, this.floatingLabelTextColor != -1 ? this.floatingLabelTextColor : (this.baseColor & ViewCompat.MEASURED_SIZE_MASK) | 1140850688, this.primaryColor));
            float floatingLabelWidth = this.textPaint.measureText(this.floatingLabelText.toString());
            if ((getGravity() & 5) != 5) {
                if (!isRTL()) {
                    if ((getGravity() & 3) == 3) {
                        lineStartY2 = startX;
                    } else {
                        lineStartY2 = ((int) (((float) getInnerPaddingLeft()) + ((((float) ((getWidth() - getInnerPaddingLeft()) - getInnerPaddingRight())) - floatingLabelWidth) / 2.0f))) + startX;
                    }
                    xOffset = 1.0f;
                    floatingLabelStartY = (int) ((((float) ((this.innerPaddingTop + this.floatingLabelTextSize) + this.floatingLabelPadding)) - (((float) this.floatingLabelPadding) * (this.floatingLabelAlwaysShown ? 1.0f : this.floatingLabelFraction))) + ((float) getScrollY()));
                    f = ((this.floatingLabelAlwaysShown ? 1.0f : this.floatingLabelFraction) * 255.0f) * ((this.focusFraction * 0.74f) + 0.26f);
                    if (this.floatingLabelTextColor != -1) {
                        xOffset = ((float) Color.alpha(this.floatingLabelTextColor)) / 256.0f;
                    }
                    this.textPaint.setAlpha((int) (f * xOffset));
                    canvas2.drawText(this.floatingLabelText.toString(), (float) lineStartY2, (float) floatingLabelStartY, this.textPaint);
                }
            }
            xOffset = 1.0f;
            f = ((this.floatingLabelAlwaysShown ? 1.0f : this.floatingLabelFraction) * 255.0f) * ((this.focusFraction * 0.74f) + 0.26f);
            if (this.floatingLabelTextColor != -1) {
                xOffset = ((float) Color.alpha(this.floatingLabelTextColor)) / 256.0f;
            }
            this.textPaint.setAlpha((int) (f * xOffset));
        }
        if (hasFocus() && this.singleLineEllipsis && getScrollX() != 0) {
            int i;
            this.paint.setColor(isInternalValid() ? this.primaryColor : this.errorColor);
            float startY = (float) (this.bottomSpacing + lineStartY3);
            if (isRTL()) {
                i = endX;
            } else {
                i = startX;
            }
            lineStartY = isRTL() ? -1 : 1;
            canvas2.drawCircle((float) (((this.bottomEllipsisSize * lineStartY) / 2) + i), ((float) (this.bottomEllipsisSize / 2)) + startY, (float) (this.bottomEllipsisSize / 2), this.paint);
            canvas2.drawCircle((float) ((((this.bottomEllipsisSize * lineStartY) * 5) / 2) + i), ((float) (this.bottomEllipsisSize / 2)) + startY, (float) (this.bottomEllipsisSize / 2), this.paint);
            canvas2.drawCircle((float) ((((this.bottomEllipsisSize * lineStartY) * 9) / 2) + i), ((float) (this.bottomEllipsisSize / 2)) + startY, (float) (this.bottomEllipsisSize / 2), this.paint);
        }
        super.onDraw(canvas);
    }

    private boolean isRTL() {
        return new GeneralFunctions(this.mContext).isRTLmode();
    }

    private int getBottomTextLeftOffset() {
        return isRTL() ? getCharactersCounterWidth() : getBottomEllipsisWidth();
    }

    private int getBottomTextRightOffset() {
        return isRTL() ? getBottomEllipsisWidth() : getCharactersCounterWidth();
    }

    private int getCharactersCounterWidth() {
        return hasCharactersCounter() ? (int) this.textPaint.measureText(getCharactersCounterText()) : 0;
    }

    private int getBottomEllipsisWidth() {
        return this.singleLineEllipsis ? (this.bottomEllipsisSize * 5) + getPixel(4) : 0;
    }

    private void checkCharactersCount() {
        boolean z = true;
        if ((this.firstShown || this.checkCharactersCountAtBeginning) && hasCharactersCounter()) {
            CharSequence text = getText();
            int count = text == null ? 0 : checkLength(text);
            if (count < this.minCharacters || (this.maxCharacters > 0 && count > this.maxCharacters)) {
                z = false;
            }
            this.charactersCountValid = z;
            return;
        }
        this.charactersCountValid = true;
    }

    public boolean isCharactersCountValid() {
        return this.charactersCountValid;
    }

    private boolean hasCharactersCounter() {
        if (this.minCharacters <= 0) {
            if (this.maxCharacters <= 0) {
                return false;
            }
        }
        return true;
    }

    private String getCharactersCounterText() {
        StringBuilder text;
        StringBuilder text2;
        int checkLength;
        if (this.minCharacters <= 0) {
            if (isRTL()) {
                text2 = new StringBuilder();
                text2.append(this.maxCharacters);
                text2.append(" / ");
                checkLength = checkLength(getText());
            } else {
                text2 = new StringBuilder();
                text2.append(checkLength(getText()));
                text2.append(" / ");
                checkLength = this.maxCharacters;
            }
            text2.append(checkLength);
            text = text2;
        } else if (this.maxCharacters <= 0) {
            if (isRTL()) {
                text2 = new StringBuilder();
                text2.append(Marker.ANY_NON_NULL_MARKER);
                text2.append(this.minCharacters);
                text2.append(" / ");
                text2.append(checkLength(getText()));
            } else {
                text2 = new StringBuilder();
                text2.append(checkLength(getText()));
                text2.append(" / ");
                text2.append(this.minCharacters);
                text2.append(Marker.ANY_NON_NULL_MARKER);
            }
            text = text2;
        } else {
            if (isRTL()) {
                text = new StringBuilder();
                text.append(this.maxCharacters);
                text.append("-");
                text.append(this.minCharacters);
                text.append(" / ");
                checkLength = checkLength(getText());
            } else {
                text = new StringBuilder();
                text.append(checkLength(getText()));
                text.append(" / ");
                text.append(this.minCharacters);
                text.append("-");
                checkLength = this.maxCharacters;
            }
            text.append(checkLength);
            return text.toString();
        }
        return text.toString();
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (!this.singleLineEllipsis || getScrollX() <= 0 || event.getAction() != 0 || event.getX() >= ((float) getPixel(20)) || event.getY() <= ((float) ((getHeight() - this.extraPaddingBottom) - this.innerPaddingBottom)) || event.getY() >= ((float) (getHeight() - this.innerPaddingBottom))) {
            if (hasFocus() && this.showClearButton) {
                switch (event.getAction()) {
                    case 0:
                        if (insideClearButton(event)) {
                            this.clearButtonTouched = true;
                            this.clearButtonClicking = true;
                            return true;
                        }
                        break;
                    case 1:
                        if (this.clearButtonClicking) {
                            if (!TextUtils.isEmpty(getText())) {
                                setText(null);
                            }
                            this.clearButtonClicking = false;
                        }
                        if (!this.clearButtonTouched) {
                            this.clearButtonTouched = false;
                            break;
                        }
                        this.clearButtonTouched = false;
                        return true;
                    case 2:
                        break;
                    case 3:
                        this.clearButtonTouched = false;
                        this.clearButtonClicking = false;
                        break;
                    default:
                        break;
                }
                if (this.clearButtonClicking && !insideClearButton(event)) {
                    this.clearButtonClicking = false;
                }
                if (this.clearButtonTouched) {
                    return true;
                }
            }
            return super.onTouchEvent(event);
        }
        setSelection(0);
        return false;
    }

    private boolean insideClearButton(MotionEvent event) {
        int buttonLeft;
        float x = event.getX();
        float y = event.getY();
        int startX = getScrollX() + (this.iconLeftBitmaps == null ? 0 : this.iconOuterWidth + this.iconPadding);
        int endX = getScrollX() + (this.iconRightBitmaps == null ? getWidth() : (getWidth() - this.iconOuterWidth) - this.iconPadding);
        if (isRTL()) {
            buttonLeft = 0;
        } else {
            buttonLeft = endX - this.iconOuterWidth;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("::");
        stringBuilder.append(buttonLeft);
        stringBuilder.append("::");
        stringBuilder.append(endX - this.iconOuterWidth);
        Utils.printLog("buttonLeft", stringBuilder.toString());
        int buttonTop = (((getScrollY() + getHeight()) - getPaddingBottom()) + this.bottomSpacing) - this.iconOuterHeight;
        if (x < ((float) buttonLeft) || x >= ((float) (this.iconOuterWidth + buttonLeft)) || y < ((float) buttonTop) || y >= ((float) (this.iconOuterHeight + buttonTop))) {
            return false;
        }
        return true;
    }

    private int checkLength(CharSequence text) {
        if (this.lengthChecker == null) {
            return text.length();
        }
        return this.lengthChecker.getLength(text);
    }
}
