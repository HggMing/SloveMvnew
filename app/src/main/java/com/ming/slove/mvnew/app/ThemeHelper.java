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

package com.ming.slove.mvnew.app;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.bilibili.magicasakura.utils.ThemeUtils;
import com.ming.slove.mvnew.R;
import com.ming.slove.mvnew.common.widgets.dialog.CardPickerDialog;
import com.ming.slove.mvnew.model.event.ChangeThemeColorEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * 主题颜色切换工具类
 */
public class ThemeHelper {
    public static final int CARD_01 = 0x1;
    public static final int CARD_02 = 0x2;
    public static final int CARD_03 = 0x3;
    public static final int CARD_04 = 0x4;
    public static final int CARD_05 = 0x5;
    public static final int CARD_06 = 0x6;
    public static final int CARD_07 = 0x7;
    public static final int CARD_08 = 0x8;
    private static final String CURRENT_THEME = "theme_current";


    static ThemeUtils.switchColor getSwitchColor() {
        return new ThemeUtils.switchColor() {
            @Override
            public int replaceColorById(Context context, @ColorRes int colorId) {
                if (isDefaultTheme(context)) {
                    return ContextCompat.getColor(context, colorId);
                }
                String colorName = getThemeColorName(context);
                if (colorName != null) {
                    colorId = getThemeColorId(context, colorId, colorName);
                }
                return ContextCompat.getColor(context, colorId);
            }

            private int getThemeColorId(Context context, int colorId, String theme) {
                switch (colorId) {
                    case R.color.theme_color_primary:
                        return context.getResources().getIdentifier(theme, "color", context.getPackageName());
                    case R.color.theme_color_primary_dark:
                        return context.getResources().getIdentifier(theme + "_dark", "color", context.getPackageName());
                    case R.color.theme_color_primary_trans:
                        return context.getResources().getIdentifier(theme + "_trans", "color", context.getPackageName());
                }
                return colorId;
            }

            @Override
            public int replaceColor(Context context, @ColorInt int color) {
                if (isDefaultTheme(context)) {
                    return color;
                }
                String colorName = getThemeColorName(context);
                int colorId = -1;
                if (colorName != null) {
                    colorId = getThemeColorIdByColor(context, color, colorName);
                }
                return colorId != -1 ? ContextCompat.getColor(context, colorId) : color;
            }

            private int getThemeColorIdByColor(Context context, int color, String colorName) {
                switch (color) {
                    case 0xff009688:
                        return context.getResources().getIdentifier(colorName, "color", context.getPackageName());
                    case 0xff00796B:
                        return context.getResources().getIdentifier(colorName + "_dark", "color", context.getPackageName());
                    case 0x99049184:
                        return context.getResources().getIdentifier(colorName + "_trans", "color", context.getPackageName());
                }
                return -1;
            }
        };
    }

    private static String getThemeColorName(Context context) {
        String[] themeColors = context.getResources().getStringArray(R.array.theme_colors);
        switch (getTheme(context)) {
            case CARD_01:
                return themeColors[0];
            case CARD_02:
                return themeColors[1];
            case CARD_03:
                return themeColors[2];
            case CARD_04:
                return themeColors[3];
            case CARD_05:
                return themeColors[4];
            case CARD_06:
                return themeColors[5];
            case CARD_07:
                return themeColors[6];
            case CARD_08:
                return themeColors[7];
            default:
                return themeColors[0];
        }
    }

    private static SharedPreferences getSharePreference(Context context) {
        return context.getSharedPreferences("multiple_theme", Context.MODE_PRIVATE);
    }

    public static void setTheme(Context context, int themeId) {
        getSharePreference(context).edit()
                .putInt(CURRENT_THEME, themeId)
                .apply();
    }

    public static int getTheme(Context context) {
        return getSharePreference(context).getInt(CURRENT_THEME, CARD_01);
    }

    private static boolean isDefaultTheme(Context context) {
        return getTheme(context) == CARD_01;
    }

    public static CardPickerDialog.ClickListener getCardPickerListener(final Context context) {
        return new CardPickerDialog.ClickListener() {
            @Override
            public void onConfirm(int currentTheme) {
                if (ThemeHelper.getTheme(context) != currentTheme) {
                    ThemeHelper.setTheme(context, currentTheme);
                    ThemeUtils.refreshUI(context, new ThemeUtils.ExtraRefreshable() {
                        @Override
                        public void refreshGlobal(Activity activity) {
                            if (Build.VERSION.SDK_INT >= 21) {
                                ActivityManager.TaskDescription taskDescription = new ActivityManager
                                        .TaskDescription(null, null, ThemeUtils.getThemeAttrColor(context, android.R.attr.colorPrimary));
                                activity.setTaskDescription(taskDescription);
                                activity.getWindow().setStatusBarColor(ThemeUtils.getColorById(context, R.color.theme_color_primary));
                            }
                        }

                        @Override
                        public void refreshSpecificView(View view) {
                        }
                    });
                    //通知MainActivity更换主题
                    EventBus.getDefault().post(new ChangeThemeColorEvent());
                }
            }
        };
    }
}
