/*
 * Copyright (C) 2017 Hanks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.basic_common.widget.edittext;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.Editable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatEditText;

import com.example.basic_common.R;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * fix EditText lineHeight and cursor effect when set lineSpaceExtra or lineSpaceMult
 * Created by hanks on 16/7/2.
 */
public class LineHeightEditText extends AppCompatEditText {

    private float mSpacingMult = 1f;
    private float mSpacingAdd = 0f;
    private TextWatcher textWatcher;
    private int cursorColor = Color.RED;
    private int cursorWidth = 6;
    private int cursorHeight = 60;

    public LineHeightEditText(Context context) {
        this(context, null);
    }

    public LineHeightEditText(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.editTextStyle);
    }

    public LineHeightEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        // init
        TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.LineHeightEditText, defStyleAttr, 0);
        cursorColor = a.getColor(R.styleable.LineHeightEditText_cursorColor, getColorAccent(context));
        cursorHeight = a.getDimensionPixelSize(R.styleable.LineHeightEditText_cursorHeight, (int) (1.25 * getTextSize()));
        cursorWidth = a.getDimensionPixelSize(R.styleable.LineHeightEditText_cursorWidth, 6);
        a.recycle();

        getLineSpacingAddAndLineSpacingMult();
        setTextCursorDrawable();
        listenTextChange();
    }

    private int getColorAccent(Context context) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.colorAccent, typedValue, true);
        return typedValue.data;
    }

    private void listenTextChange() {
        addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (textWatcher != null) {
                    textWatcher.beforeTextChanged(s, start, count, after);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setLineSpacing(0f, 1f);
                setLineSpacing(mSpacingAdd, mSpacingMult);
                if (textWatcher != null) {
                    textWatcher.onTextChanged(s, start, before, count);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (textWatcher != null) {
                    textWatcher.afterTextChanged(s);
                }
            }
        });
    }

    private void setTextCursorDrawable() {
        try {
            Method method = TextView.class.getDeclaredMethod("createEditorIfNeeded");
            method.setAccessible(true);
            method.invoke(this);
            Field field1 = TextView.class.getDeclaredField("mEditor");
            Field field2 = Class.forName("android.widget.Editor").getDeclaredField("mCursorDrawable");
            field1.setAccessible(true);
            field2.setAccessible(true);
            Object arr = field2.get(field1.get(this));
            Array.set(arr, 0, new LineSpaceCursorDrawable(getCursorColor(), getCursorWidth(), getCursorHeight()));
            Array.set(arr, 1, new LineSpaceCursorDrawable(getCursorColor(), getCursorWidth(), getCursorHeight()));
        } catch (Exception ignored) {
        }
    }

    private void getLineSpacingAddAndLineSpacingMult() {
        try {
            Field mSpacingAddField = TextView.class.getDeclaredField("mSpacingAdd");
            Field mSpacingMultField = TextView.class.getDeclaredField("mSpacingMult");
            mSpacingAddField.setAccessible(true);
            mSpacingMultField.setAccessible(true);
            mSpacingAdd = mSpacingAddField.getFloat(this);
            mSpacingMult = mSpacingMultField.getFloat(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getCursorColor() {
        return cursorColor;
    }

    public void setCursorColor(int cursorColor) {
        this.cursorColor = cursorColor;
        setTextCursorDrawable();
        invalidate();
    }

    public int getCursorHeight() {
        return cursorHeight;
    }

    public void setCursorHeight(int cursorHeight) {
        this.cursorHeight = cursorHeight;
        setTextCursorDrawable();
        invalidate();
    }

    public int getCursorWidth() {
        return cursorWidth;
    }

    public void setCursorWidth(int cursorWidth) {
        this.cursorWidth = cursorWidth;
        setTextCursorDrawable();
        invalidate();
    }

    /**
     * Adds a TextWatcher to the list of those whose methods are called
     * whenever this TextView's text changes.
     *
     * @param textWatcher TextWatcher
     */
    public void addTextWatcher(TextWatcher textWatcher) {
        this.textWatcher = textWatcher;
    }

}
