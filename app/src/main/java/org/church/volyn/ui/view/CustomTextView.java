package org.church.volyn.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.widget.TextView;


import java.util.HashMap;
import java.util.Map;

import org.church.volyn.R;

public class CustomTextView extends TextView {

    private static final String TAG = CustomTextView.class.getSimpleName();
    private static final Map<String, Typeface> TYPEFACE_MAP = new HashMap<String, Typeface>();

    private static final String DEFAULT_FONT_NAME = "HelveticaNeueCyr-Light.otf";

    private Typeface mTypeface;

    public CustomTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public CustomTextView(Context context) {
        super(context);
        init(null);
    }

    @Override
    public void setTypeface(Typeface tf, int style) {
        super.setTypeface(tf, style);
    }

    private void init(AttributeSet attrs) {
        if (isInEditMode()) return;

        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CustomTextView);
            String fontName = a.getString(R.styleable.CustomTextView_fontName);
            if (fontName == null) {
                fontName = DEFAULT_FONT_NAME;
            }
            if (!TYPEFACE_MAP.containsKey(fontName)) {
                try {
                    Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/" + fontName);
                    TYPEFACE_MAP.put(fontName, typeface);
                } catch (Exception e) {
                    TYPEFACE_MAP.put(fontName, null);
                }
            }
            Typeface typeface = TYPEFACE_MAP.get(fontName);
            if (typeface != null) {
                mTypeface = typeface;
                setTypeface(typeface);
            }
            a.recycle();
        }
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.text = getText().toString();
        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        setText(ss.text);
    }


    private static class SavedState extends BaseSavedState {

        private String text;

        private SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            text = (in.readString());
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeString(text);
        }

        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

}
