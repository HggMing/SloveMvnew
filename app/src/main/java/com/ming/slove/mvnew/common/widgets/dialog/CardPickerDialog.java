/*
 * Copyright (C) 2016 Bilibili
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ming.slove.mvnew.common.widgets.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.ming.slove.mvnew.R;
import com.ming.slove.mvnew.app.ThemeHelper;


public class CardPickerDialog extends DialogFragment implements View.OnClickListener {
    public static final String TAG = "CardPickerDialog";
    ImageView[] mCards = new ImageView[8];
    Button mConfirm;
    Button mCancel;

    private int mCurrentTheme;
    private ClickListener mClickListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.ThemeDialog);
        mCurrentTheme = ThemeHelper.getTheme(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_theme_picker, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mCancel = (Button) view.findViewById(R.id.button_cancel);
        mConfirm = (Button) view.findViewById(R.id.button_ok);
        mCards[0] = (ImageView) view.findViewById(R.id.theme_01);
        mCards[1] = (ImageView) view.findViewById(R.id.theme_02);
        mCards[2] = (ImageView) view.findViewById(R.id.theme_03);
        mCards[3] = (ImageView) view.findViewById(R.id.theme_04);
        mCards[4] = (ImageView) view.findViewById(R.id.theme_05);
        mCards[5] = (ImageView) view.findViewById(R.id.theme_06);
        mCards[6] = (ImageView) view.findViewById(R.id.theme_07);
        mCards[7] = (ImageView) view.findViewById(R.id.theme_08);
        setImageButtons(mCurrentTheme);
        for (ImageView card : mCards) {
            card.setOnClickListener(this);
        }
        mCancel.setOnClickListener(this);
        mConfirm.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_ok:
                if (mClickListener != null) {
                    mClickListener.onConfirm(mCurrentTheme);
                }
            case R.id.button_cancel:
                dismiss();
                break;
            case R.id.theme_01:
                mCurrentTheme = ThemeHelper.CARD_01;
                setImageButtons(mCurrentTheme);
                break;
            case R.id.theme_02:
                mCurrentTheme = ThemeHelper.CARD_02;
                setImageButtons(mCurrentTheme);
                break;
            case R.id.theme_03:
                mCurrentTheme = ThemeHelper.CARD_03;
                setImageButtons(mCurrentTheme);
                break;
            case R.id.theme_04:
                mCurrentTheme = ThemeHelper.CARD_04;
                setImageButtons(mCurrentTheme);
                break;
            case R.id.theme_05:
                mCurrentTheme = ThemeHelper.CARD_05;
                setImageButtons(mCurrentTheme);
                break;
            case R.id.theme_06:
                mCurrentTheme = ThemeHelper.CARD_06;
                setImageButtons(mCurrentTheme);
                break;
            case R.id.theme_07:
                mCurrentTheme = ThemeHelper.CARD_07;
                setImageButtons(mCurrentTheme);
                break;
            case R.id.theme_08:
                mCurrentTheme = ThemeHelper.CARD_08;
                setImageButtons(mCurrentTheme);
                break;
            default:
                break;
        }
    }

    private void setImageButtons(int currentTheme) {
        mCards[0].setSelected(currentTheme == ThemeHelper.CARD_01);
        mCards[1].setSelected(currentTheme == ThemeHelper.CARD_02);
        mCards[2].setSelected(currentTheme == ThemeHelper.CARD_03);
        mCards[3].setSelected(currentTheme == ThemeHelper.CARD_04);
        mCards[4].setSelected(currentTheme == ThemeHelper.CARD_05);
        mCards[5].setSelected(currentTheme == ThemeHelper.CARD_06);
        mCards[6].setSelected(currentTheme == ThemeHelper.CARD_07);
        mCards[7].setSelected(currentTheme == ThemeHelper.CARD_08);
    }

    public void setClickListener(ClickListener clickListener) {
        mClickListener = clickListener;
    }

    public interface ClickListener {
        void onConfirm(int currentTheme);
    }
}
