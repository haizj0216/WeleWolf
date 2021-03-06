/*
 * Copyright (C) 2015 The AndroidPhoneTeacher Project
 */
package com.buang.welewolf.modules.utils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.buang.welewolf.R;
import com.buang.welewolf.base.database.bean.UserItem;
import com.buang.welewolf.base.utils.ImageUtil;
import com.buang.welewolf.widgets.AddClassView;
import com.buang.welewolf.widgets.numberpicker.NumberPicker;
import com.buang.welewolf.widgets.numberpicker.NumberPicker.OnValueChangeListener;
import com.hyena.framework.app.adapter.SingleTypeAdapter;
import com.hyena.framework.app.fragment.bean.MenuItem;
import com.hyena.framework.utils.BaseApp;
import com.hyena.framework.utils.ImageFetcher;
import com.hyena.framework.utils.UIUtils;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@SuppressLint("ClickableViewAccessibility")
public class DialogUtils {

    /**
     * 获得Debug显示的对话框
     *
     * @param context
     * @param content
     * @return
     */
    public static Dialog getDebugLogDialog(Context context, String title,
                                           String content) {
        View view = View.inflate(context, com.buang.welewolf.R.layout.dialog_debug, null);
        TextView textView = (TextView) view.findViewById(com.buang.welewolf.R.id.debug_txt);
        textView.setText(content);
        return getCommonDialog(context, view, title, null, null, null);
    }

    /**
     * 发送email
     *
     * @param context
     * @param hint
     * @param listener
     * @return
     */
    public static Dialog getSendEmailDialog(Context context, String hint,
                                            final OnFillDialogBtnClickListener listener) {
        final Dialog dialog = createDialog(context);
        View view = View.inflate(context, com.buang.welewolf.R.layout.dialog_send_email, null);
        final EditText edit = (EditText) view.findViewById(com.buang.welewolf.R.id.dialog_send_edit);
        TextView cancel = (TextView) view.findViewById(com.buang.welewolf.R.id.dialog_common_cancel);
        TextView confirm = (TextView) view.findViewById(com.buang.welewolf.R.id.dialog_common_confirm);
        if (!TextUtils.isEmpty(hint)) {
            edit.setText(hint);
//			Selection.setSelection(edit.getText(), edit.getText().length());
            edit.setSelection(hint.length());
        }

        cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(dialog, false, edit.getText().toString());
            }
        });
        confirm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(dialog, true, edit.getText().toString());
            }
        });
        dialog.setContentView(view);
        return dialog;
    }

    /**
     * 红包活动任务弹窗
     *
     * @param context
     * @param iconUrl
     * @param title
     * @param desc
     * @return
     */
    public static Dialog getActivityTaskDialog(Context context, String iconUrl, String title, String desc) {
        final Dialog dialog = createDialog(context);
        View view = View.inflate(context, com.buang.welewolf.R.layout.dialog_activity_task_progress, null);
        float width = BaseApp.getAppContext().getResources().getDisplayMetrics().widthPixels;
        int padding = (int) (UIUtils.dip2px(50) * width / 720);
        view.setPadding(padding, 0, padding, 0);
        ImageView mIcon = (ImageView) view.findViewById(com.buang.welewolf.R.id.icon);
        TextView mTitle = (TextView) view.findViewById(com.buang.welewolf.R.id.title);
        TextView mDesc = (TextView) view.findViewById(com.buang.welewolf.R.id.content);

        ImageFetcher.getImageFetcher().loadImage(iconUrl, mIcon, com.buang.welewolf.R.drawable.profile_icon_default);
        mTitle.setText(title);
        mDesc.setText(desc);

        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                try {
                    dialog.dismiss();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        });
        if (dialog.getWindow() != null
                && dialog.getWindow().getDecorView() != null) {
            dialog.getWindow().getDecorView()
                    .setOnTouchListener(new OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            try {
                                dialog.dismiss();
                            } catch (Throwable e) {
                                e.printStackTrace();
                            }
                            return false;
                        }
                    });
        }
        return dialog;
    }


    /**
     * 适配年级数据
     *
     * @param gradePicker
     * @param isMore
     */
    private static void initGradePicker(NumberPicker gradePicker, boolean isMore) {
        List<String> grades = new ArrayList<String>();
        grades.add("一年级");
        grades.add("二年级");
        grades.add("三年级");
        if (isMore) {
            grades.add("四年级");
        }
        gradePicker.setValue(0);
        gradePicker.setMinValue(0);
        gradePicker.setMaxValue(grades.size() - 1, false);
        gradePicker
                .setDisplayedValues(grades.toArray(new String[grades.size()]));
    }


    /**
     * 适配年级数据
     */
    private static void initSubjectPicker(NumberPicker subjectPicker) {
        List<String> grades = new ArrayList<String>();
        grades.add("数学");
        grades.add("语文");
        grades.add("英语");
        grades.add("物理");
        grades.add("化学");
        grades.add("生物");
        grades.add("历史");
        grades.add("地理");
        grades.add("政治");
        grades.add("信息技术");

        subjectPicker.setValue(0);
        subjectPicker.setMinValue(0);
        subjectPicker.setMaxValue(grades.size() - 1, false);
        subjectPicker.setDisplayedValues(grades.toArray(new String[grades
                .size()]));
    }


    /**
     * 发布比赛日期选择
     *
     * @param context
     * @param time
     * @param listener
     * @return
     */
    public static Dialog getAssignDateDialog(final Context context, long time, final int type, int startClock, int endClock,
                                             final OnDataTimePickerSelectListener listener) {
        View view = View.inflate(context, com.buang.welewolf.R.layout.dialog_assign_datapicker, null);
        final NumberPicker monthPicker = (NumberPicker) view
                .findViewById(com.buang.welewolf.R.id.dialog_makehomework_month);
        final NumberPicker dayPicker = (NumberPicker) view
                .findViewById(com.buang.welewolf.R.id.dialog_makehomework_day);
        final Calendar calendar = Calendar.getInstance(Locale.getDefault());
        final String title = String.valueOf(calendar.get(Calendar.YEAR));
        final Dialog dialog = createDialog(context);
        final View layout = View.inflate(context, com.buang.welewolf.R.layout.dialog_common, null);

        int width = BaseApp.getAppContext().getResources().getDisplayMetrics().widthPixels;
        int padding = UIUtils.dip2px(35) * width / 720;
        layout.setPadding(padding, 0, padding, 0);

        ViewGroup container = (ViewGroup) layout
                .findViewById(com.buang.welewolf.R.id.dialog_common_container);
        container.addView(view);
        dialog.setContentView(layout);
        View titlePanel = layout
                .findViewById(com.buang.welewolf.R.id.dialog_common_title_container);
        final TextView titleTxt = (TextView) layout
                .findViewById(com.buang.welewolf.R.id.dialog_common_title);
        titleTxt.setText(title + " 年");
        if (time <= 0) {
            time = System.currentTimeMillis();
        }

        if (type == ConstantsUtils.TYPE_DATA_PICKER_END || type == ConstantsUtils.TYPE_DATA_PICKER_START || type == ConstantsUtils.TYPE_DATE_SINGLE_TEST_GAME_PICK) {
            initMonthNumberPicker(monthPicker, time, new OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                    if (oldVal == monthPicker.getMinValue() && newVal == monthPicker.getMaxValue()) {
                        int year = calendar.get(Calendar.YEAR);
                        titleTxt.setText(String.valueOf(year) + " 年");
                    } else if (oldVal == monthPicker.getMaxValue() && newVal == monthPicker.getMinValue()) {
                        int year = calendar.get(Calendar.YEAR) + 1;
                        titleTxt.setText(String.valueOf(year) + " 年");
                    }
                }
            });
            initDayNumberPicker(dayPicker, time);
        } else {
            initLimitedHourNunberPicker(monthPicker, time, startClock, endClock);
            initMinuteNumberPicker(dayPicker, time);
        }


        String confirmTxt = "确定";
        String cancelTxt = "取消";

        dialog.setCanceledOnTouchOutside(true);
        final View devider = layout.findViewById(com.buang.welewolf.R.id.dialog_common_devider);

        dialog.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                try {
                    dialog.dismiss();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        });
        if (dialog.getWindow() != null
                && dialog.getWindow().getDecorView() != null) {
            dialog.getWindow().getDecorView()
                    .setOnTouchListener(new OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            try {
                                dialog.dismiss();
                            } catch (Throwable e) {
                                e.printStackTrace();
                            }
                            return false;
                        }
                    });
        }

        final long finalTime = time;
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener == null || dialog == null) {
                    return;
                }
                int id = v.getId();
                switch (id) {
                    case com.buang.welewolf.R.id.dialog_common_confirm: {
                        Calendar calendar = Calendar.getInstance();
                        Calendar startC = Calendar.getInstance();
                        startC.setTime(new Date(finalTime));
                        if (type == ConstantsUtils.TYPE_DATA_PICKER_END || type == ConstantsUtils.TYPE_DATA_PICKER_START || type == ConstantsUtils.TYPE_DATE_SINGLE_TEST_GAME_PICK) {
                            String yt = titleTxt.getText().toString().trim().split(" ")[0];
                            calendar.set(Calendar.YEAR, Integer.parseInt(yt));
                            calendar.set(Calendar.MONTH, monthPicker.getValue());
                            calendar.set(Calendar.DAY_OF_MONTH,
                                    dayPicker.getValue());
//							if (calendar.get(Calendar.MONTH) < startC.get(Calendar.MONTH) ||
//									(calendar.get(Calendar.MONTH) == startC.get(Calendar.MONTH) &&  calendar.get(Calendar.DAY_OF_MONTH) < startC.get(Calendar.DAY_OF_MONTH))) {
//								ToastUtils.showShortToast(context, "无法选择过去的时间");
//								return;
//							}
//							calendar.set(Calendar.HOUR_OF_DAY, startC.get(Calendar.HOUR_OF_DAY));
//							calendar.set(Calendar.MINUTE, startC.get(Calendar.MINUTE));
//							calendar.set(Calendar.SECOND, startC.get(Calendar.SECOND));
//							calendar.set(Calendar.MILLISECOND, startC.get(Calendar.MILLISECOND));
//							if (type == ConstantsUtils.TYPE_DATA_PICKER_END && calendar.getTime().getTime() < finalTime + startTime) {
//								ToastUtils.showShortToast(context, "最少选择7天时间");
//								return;
//							}
                        } else {
                            calendar.set(Calendar.HOUR_OF_DAY,
                                    monthPicker.getValue());
                            calendar.set(Calendar.MINUTE, Integer.parseInt(dayPicker.getDisplayedValues()[dayPicker.getValue()]));
                            calendar.set(Calendar.SECOND, 0);
//							if (calendar.get(Calendar.HOUR_OF_DAY) < 6 || calendar.get(Calendar.HOUR_OF_DAY) > 22 ||
//									(calendar.get(Calendar.MINUTE) > 0 && (calendar.get(Calendar.HOUR_OF_DAY) == 6 || calendar.get(Calendar.HOUR_OF_DAY) == 22))) {
//								ToastUtils.showShortToast(context, "比赛时间段为6：00-22：00");
//								return;
//							}
//							if (type == ConstantsUtils.TYPE_TIME_PICKER_END) {
//								int startH = startC.get(Calendar.HOUR_OF_DAY);
//								int startM = startC.get(Calendar.MINUTE);
//								if (monthPicker.getValue() < startH || (startH == monthPicker.getValue() && startM + startTime> calendar.get(Calendar.MINUTE))) {
//									ToastUtils.showShortToast(context, "最少选择30分钟");
//									return;
//								}
//							}
                        }
                        long time = calendar.getTime().getTime();

                        if (listener != null) {
                            listener.onSelectDateTime(time);
                        }
                    }
                    break;
                    case com.buang.welewolf.R.id.dialog_common_cancel:
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        break;
                    default:
                        break;
                }
            }
        };

        List<View> visableView = new ArrayList<View>();
        TextView confirmBtn = (TextView) layout
                .findViewById(com.buang.welewolf.R.id.dialog_common_confirm);
        if (!TextUtils.isEmpty(confirmTxt)) {
            confirmBtn.setText(confirmTxt);
            confirmBtn.setVisibility(View.VISIBLE);
            confirmBtn.setOnClickListener(onClickListener);
            visableView.add(confirmBtn);
        } else {
            confirmBtn.setVisibility(View.GONE);
            devider.setVisibility(View.GONE);
        }

        TextView cancelBtn = (TextView) layout
                .findViewById(com.buang.welewolf.R.id.dialog_common_cancel);
        if (!TextUtils.isEmpty(cancelTxt)) {
            cancelBtn.setText(cancelTxt);
            cancelBtn.setVisibility(View.VISIBLE);
            cancelBtn.setOnClickListener(onClickListener);
            visableView.add(cancelBtn);
        } else {
            cancelBtn.setVisibility(View.GONE);
            devider.setVisibility(View.GONE);
        }
        View optContainer = layout
                .findViewById(com.buang.welewolf.R.id.dialog_common_opt_container);
        if (TextUtils.isEmpty(confirmTxt) && TextUtils.isEmpty(cancelTxt)) {
            optContainer.setVisibility(View.GONE);
        }

        return dialog;
    }

    /**
     * 获得日期选择对话框
     *
     * @param context
     * @param listener
     * @return
     */
    public static Dialog getDatePickerDialog(final Context context, long time,
                                             final OnDataTimePickerSelectListener listener) {
        View view = View.inflate(context, com.buang.welewolf.R.layout.dialog_datapicker, null);
        final NumberPicker minutePicker = (NumberPicker) view
                .findViewById(com.buang.welewolf.R.id.dialog_makehomework_minute);
        final NumberPicker monthPicker = (NumberPicker) view
                .findViewById(com.buang.welewolf.R.id.dialog_makehomework_month);
        final NumberPicker dayPicker = (NumberPicker) view
                .findViewById(com.buang.welewolf.R.id.dialog_makehomework_day);
        final NumberPicker hourPicker = (NumberPicker) view
                .findViewById(com.buang.welewolf.R.id.dialog_makehomework_hour);
        final Calendar calendar = Calendar.getInstance(Locale.getDefault());

        final String title = String.valueOf(calendar.get(Calendar.YEAR));
        final Dialog dialog = createDialog(context);
        final View layout = View.inflate(context, com.buang.welewolf.R.layout.dialog_common, null);

        int width = BaseApp.getAppContext().getResources().getDisplayMetrics().widthPixels;
        int padding = UIUtils.dip2px(35) * width / 720;
        layout.setPadding(padding, 0, padding, 0);

        ViewGroup container = (ViewGroup) layout
                .findViewById(com.buang.welewolf.R.id.dialog_common_container);
        container.addView(view);
        dialog.setContentView(layout);
        View titlePanel = layout
                .findViewById(com.buang.welewolf.R.id.dialog_common_title_container);
        final TextView titleTxt = (TextView) layout
                .findViewById(com.buang.welewolf.R.id.dialog_common_title);
        titleTxt.setText(title + " 年");
        if (time <= 0) {
            time = System.currentTimeMillis() / 1000l;
        }
        initMonthNumberPicker(monthPicker, time * 1000, new OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                if (oldVal == monthPicker.getMinValue() && newVal == monthPicker.getMaxValue()) {
                    int year = calendar.get(Calendar.YEAR);
                    titleTxt.setText(String.valueOf(year) + " 年");
                } else if (oldVal == monthPicker.getMaxValue() && newVal == monthPicker.getMinValue()) {
                    int year = calendar.get(Calendar.YEAR) + 1;
                    titleTxt.setText(String.valueOf(year) + " 年");
                }
            }
        });
        initDayNumberPicker(dayPicker, time * 1000);
        initHourNunberPicker(hourPicker, time * 1000);
        initMinuteNumberPicker(minutePicker, time * 1000);

        String confirmTxt = "确定";
        String cancelTxt = "取消";

        dialog.setCanceledOnTouchOutside(true);
        final View devider = layout.findViewById(com.buang.welewolf.R.id.dialog_common_devider);

        dialog.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                try {
                    dialog.dismiss();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        });
        if (dialog.getWindow() != null
                && dialog.getWindow().getDecorView() != null) {
            dialog.getWindow().getDecorView()
                    .setOnTouchListener(new OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            try {
                                dialog.dismiss();
                            } catch (Throwable e) {
                                e.printStackTrace();
                            }
                            return false;
                        }
                    });
        }

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener == null || dialog == null) {
                    return;
                }

                int id = v.getId();
                switch (id) {
                    case com.buang.welewolf.R.id.dialog_common_confirm: {
                        Calendar calendar = Calendar.getInstance();
                        String yt = titleTxt.getText().toString().trim().split(" ")[0];
                        calendar.set(Calendar.YEAR, Integer.parseInt(yt));
                        calendar.set(Calendar.MONTH, monthPicker.getValue());
                        calendar.set(Calendar.DAY_OF_MONTH,
                                dayPicker.getValue());
                        calendar.set(Calendar.HOUR_OF_DAY,
                                hourPicker.getValue());
                        calendar.set(Calendar.MINUTE, Integer.parseInt(minutePicker.getDisplayedValues()[minutePicker.getValue()]));
                        calendar.set(Calendar.SECOND, 0);
                        if (calendar.getTime().getTime() < System.currentTimeMillis()) {
                            ToastUtils.showShortToast(context, "无法选择过去的时间");
                            return;
                        }
                        long time = calendar.getTime().getTime() / 1000;
                        if (listener != null) {
                            listener.onSelectDateTime(time);
                        }
                    }
                    break;
                    case com.buang.welewolf.R.id.dialog_common_cancel:
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        break;
                    default:
                        break;
                }
            }
        };

        List<View> visableView = new ArrayList<View>();
        TextView confirmBtn = (TextView) layout
                .findViewById(com.buang.welewolf.R.id.dialog_common_confirm);
        if (!TextUtils.isEmpty(confirmTxt)) {
            confirmBtn.setText(confirmTxt);
            confirmBtn.setVisibility(View.VISIBLE);
            confirmBtn.setOnClickListener(onClickListener);
            visableView.add(confirmBtn);
        } else {
            confirmBtn.setVisibility(View.GONE);
            devider.setVisibility(View.GONE);
        }

        TextView cancelBtn = (TextView) layout
                .findViewById(com.buang.welewolf.R.id.dialog_common_cancel);
        if (!TextUtils.isEmpty(cancelTxt)) {
            cancelBtn.setText(cancelTxt);
            cancelBtn.setVisibility(View.VISIBLE);
            cancelBtn.setOnClickListener(onClickListener);
            visableView.add(cancelBtn);
        } else {
            cancelBtn.setVisibility(View.GONE);
            devider.setVisibility(View.GONE);
        }
        View optContainer = layout
                .findViewById(com.buang.welewolf.R.id.dialog_common_opt_container);
        if (TextUtils.isEmpty(confirmTxt) && TextUtils.isEmpty(cancelTxt)) {
            optContainer.setVisibility(View.GONE);
        }

        return dialog;
    }

    /**
     * 获得出生日期选择对话框
     *
     * @param context
     * @param listener
     * @return
     */
    public static Dialog getBirthdayPickerDialog(Context context, long time,
                                                 final OnDataTimePickerSelectListener listener) {
        View view = View.inflate(context, com.buang.welewolf.R.layout.dialog_datapicker_birthday,
                null);
        final NumberPicker yearPicker = (NumberPicker) view
                .findViewById(com.buang.welewolf.R.id.dialog_makehomework_year);
        final NumberPicker monthPicker = (NumberPicker) view
                .findViewById(com.buang.welewolf.R.id.dialog_makehomework_month);
        final NumberPicker dayPicker = (NumberPicker) view
                .findViewById(com.buang.welewolf.R.id.dialog_makehomework_day);
        if (time == 0) {
            time = System.currentTimeMillis() / 1000;
        }
        initYearNumberPicker(yearPicker, time * 1000);
        initMonthNumberPicker(monthPicker, time * 1000, null);
        initDayNumberPicker(dayPicker, time * 1000);
        Dialog dialog = getCommonDialog(context, view, "选择日期", "确定", "取消",
                new OnDialogButtonClickListener() {

                    @Override
                    public void onItemClick(Dialog dialog, int btnId) {
                        if (btnId == BUTTON_CONFIRM) {
                            Calendar calendar = Calendar.getInstance();
                            calendar.set(Calendar.YEAR, yearPicker.getValue());
                            calendar.set(Calendar.MONTH, monthPicker.getValue());
                            calendar.set(Calendar.DAY_OF_MONTH,
                                    dayPicker.getValue());
                            long time = calendar.getTime().getTime() / 1000;
                            if (listener != null) {
                                listener.onSelectDateTime(time);
                            }
                        }
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                    }

                });
        return dialog;
    }

    public static interface OnDataTimePickerSelectListener {

        public void onSelectDateTime(long time);
    }

    /**
     * 初始化年份选择器 （老师最大年龄距今90岁）
     *
     * @param yearPicker
     */
    private static void initYearNumberPicker(NumberPicker yearPicker, long time) {
        Calendar calendar = Calendar.getInstance();
        int yearMax = calendar.get(Calendar.YEAR);
        int yearMin = 1925;//生日从1970计算
        yearPicker.setMinValue(yearMin);
        yearPicker.setMaxValue(yearMax);
        List<String> yearList = new ArrayList<String>();
        for (int i = yearMin; i <= yearMax; i++) {
            yearList.add(i + "");
        }
        yearPicker.setDisplayedValues(yearList.toArray(new String[yearList
                .size()]));
        calendar.setTimeInMillis(time);
        yearPicker.setValue(calendar.get(Calendar.YEAR));
    }

    /**
     * 初始化月选择器
     *
     * @param monthPicker
     */
    private static void initMonthNumberPicker(NumberPicker monthPicker,
                                              long time, OnValueChangeListener listener) {
        Calendar calendar = Calendar.getInstance();
        int minValue = calendar.getMinimum(Calendar.MONTH);
        int maxValue = calendar.getMaximum(Calendar.MONTH);
        monthPicker.setMinValue(minValue);
        monthPicker.setMaxValue(maxValue);
        List<String> monthList = new ArrayList<String>();
        DecimalFormat format = new DecimalFormat("00");
        for (int i = minValue; i <= maxValue; i++) {
            monthList.add(format.format((i + 1)) + "月");
        }
        monthPicker.setDisplayedValues(monthList.toArray(new String[monthList
                .size()]));
        calendar.setTime(new Date(time));
        monthPicker.setValue(calendar.get(Calendar.MONTH));
        monthPicker.setOnValueChangedListener(listener);
    }

    /**
     * 初始化日期选择器
     *
     * @param dayPicker
     */
    private static void initDayNumberPicker(NumberPicker dayPicker, long dayMore) {
        Calendar calendar = Calendar.getInstance();
        int minValue = calendar.getMinimum(Calendar.DAY_OF_MONTH);
        int maxValue = calendar.getMaximum(Calendar.DAY_OF_MONTH);
        dayPicker.setMinValue(minValue);
        dayPicker.setMaxValue(maxValue);

        List<String> monthList = new ArrayList<String>();
        DecimalFormat format = new DecimalFormat("00");
        for (int i = minValue; i <= maxValue; i++) {
            monthList.add(format.format(i) + "日");
        }
        dayPicker.setDisplayedValues(monthList.toArray(new String[monthList
                .size()]));
        calendar.setTime(new Date(dayMore));
        dayPicker.setValue(calendar.get(Calendar.DAY_OF_MONTH));
    }

    private static void initLimitedHourNunberPicker(NumberPicker hourPicker, long time, int start, int end) {
        Calendar calendar = Calendar.getInstance();
        int minValue = start;
        int maxValue = end;
        hourPicker.setMinValue(minValue);
        hourPicker.setMaxValue(maxValue);

        List<String> hourList = new ArrayList<String>();
        DecimalFormat format = new DecimalFormat("00");
        for (int i = minValue; i <= maxValue; i++) {
            hourList.add(format.format(i));
        }
        hourPicker.setDisplayedValues(hourList.toArray(new String[hourList
                .size()]));
//		calendar.add(Calendar.DAY_OF_MONTH, 1);
        calendar.setTime(new Date(time));
        hourPicker.setValue(calendar.get(Calendar.HOUR_OF_DAY));
    }

    /**
     * 初始化小时选择器
     *
     * @param hourPicker
     */
    private static void initHourNunberPicker(NumberPicker hourPicker, long time) {
        Calendar calendar = Calendar.getInstance();
        int minValue = calendar.getMinimum(Calendar.HOUR_OF_DAY);
        int maxValue = calendar.getMaximum(Calendar.HOUR_OF_DAY);
        hourPicker.setMinValue(minValue);
        hourPicker.setMaxValue(maxValue);

        List<String> hourList = new ArrayList<String>();
        DecimalFormat format = new DecimalFormat("00");
        for (int i = minValue; i <= maxValue; i++) {
            hourList.add(format.format(i));
        }
        hourPicker.setDisplayedValues(hourList.toArray(new String[hourList
                .size()]));
//		calendar.add(Calendar.DAY_OF_MONTH, 1);
        calendar.setTime(new Date(time));
        hourPicker.setValue(calendar.get(Calendar.HOUR_OF_DAY));
    }

    private static void initMinuteNumberPicker(NumberPicker minutePicker, long time) {
        Calendar calendar = Calendar.getInstance();
        int minValue = calendar.getMinimum(Calendar.MINUTE);
        int maxValue = calendar.getMaximum(Calendar.MINUTE);
        minutePicker.setMinValue(0);
        minutePicker.setMaxValue(11);
//		List<String> hourList = new ArrayList<String>();
//		DecimalFormat format = new DecimalFormat("00");
//		for (int i = minValue; i < maxValue;) {
//			hourList.add(format.format(i));
//			i += 5;
//		}
        calendar.setTime(new Date(time));
        minutePicker.setDisplayedValues(new String[]{"00", "05", "10", "15", "20", "25", "30", "35", "40", "45", "50", "55",});
        int minute = calendar.get(Calendar.MINUTE);
        int index = 0;
        if (minute <= 5 && minute > 0) {
            index = 1;
        } else if (minute > 5 && minute <= 10) {
            index = 2;
        } else if (minute > 10 && minute <= 15) {
            index = 3;
        } else if (minute > 15 && minute <= 20) {
            index = 4;
        } else if (minute > 20 && minute <= 25) {
            index = 5;
        } else if (minute > 25 && minute <= 30) {
            index = 6;
        } else if (minute > 30 && minute <= 35) {
            index = 7;
        } else if (minute > 35 && minute <= 40) {
            index = 8;
        } else if (minute > 40 && minute <= 45) {
            index = 9;
        } else if (minute > 45 && minute <= 50) {
            index = 10;
        } else if (minute > 50 && minute <= 55) {
            index = 11;
        } else if (minute > 55 && minute <= 60) {
            index = 0;
        }
        minutePicker.setValue(index);
    }


    public static Dialog getNewMessageDialog(Context context, String title, int txtGravity,
                                             String confirmTxt, String cancelTxt, String msg, int msgGravity,
                                             final OnDialogButtonClickListener listener) {
        return getNewMessageDialog(context, 0, title, txtGravity, confirmTxt, cancelTxt, msg, msgGravity, listener);
    }


    public static Dialog getNewMessageDialog(Context context, int id, String title, String desc,
                                             String cancelTxt, String confirmTxt,
                                             final OnDialogButtonClickListener listener) {
        return getNewMessageDialog(context, id, title, Gravity.CENTER, confirmTxt, cancelTxt, desc, Gravity.CENTER, listener);
    }


    public static Dialog getNewMessageDialog(Context context, int id, String title, int txtGravity,
                                             String confirmTxt, String cancelTxt, String msg, int msgGravity,
                                             final OnDialogButtonClickListener listener) {
        final Dialog dialog = createDialog(context);
        final View layout = View.inflate(context, com.buang.welewolf.R.layout.dialog_new_message, null);

        int width = BaseApp.getAppContext().getResources().getDisplayMetrics().widthPixels;
        int padding = UIUtils.dip2px(35) * width / 720;
        layout.setPadding(padding, padding, padding, padding);

        dialog.setContentView(layout);

        TextView titleTxt = (TextView) layout
                .findViewById(com.buang.welewolf.R.id.dialog_message_title);
        if (TextUtils.isEmpty(title)) {
            titleTxt.setVisibility(View.GONE);
        } else {
            titleTxt.setText(title);
            if (txtGravity != 0) {
                titleTxt.setGravity(txtGravity);
            }
        }

        TextView msgTxt = (TextView) layout
                .findViewById(com.buang.welewolf.R.id.dialog_message_msg);
        if (TextUtils.isEmpty(msg)) {
            msgTxt.setVisibility(View.GONE);
        } else {
            msgTxt.setText(msg);
            if (msgGravity != 0) {
                msgTxt.setGravity(msgGravity);
            }
        }

        if (id != 0) {
            ((ImageView) layout.findViewById(com.buang.welewolf.R.id.dialog_message_icon)).setImageResource(id);
        }

        dialog.setCanceledOnTouchOutside(true);

        dialog.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                try {
                    dialog.dismiss();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        });
        if (dialog.getWindow() != null
                && dialog.getWindow().getDecorView() != null) {
            dialog.getWindow().getDecorView()
                    .setOnTouchListener(new OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            try {
                                dialog.dismiss();
                            } catch (Throwable e) {
                                e.printStackTrace();
                            }
                            return false;
                        }
                    });
        }

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener == null || dialog == null) {
                    return;
                }

                int id = v.getId();
                switch (id) {
                    case com.buang.welewolf.R.id.dialog_message_button_confirm:
                        listener.onItemClick(dialog,
                                OnDialogButtonClickListener.BUTTON_CONFIRM);
                        break;
                    case com.buang.welewolf.R.id.dialog_message_button_cancel:
                        listener.onItemClick(dialog,
                                OnDialogButtonClickListener.BUTTON_CANCEL);
                        break;
                    default:
                        break;
                }
            }
        };

        List<View> visableView = new ArrayList<View>();
        TextView confirmBtn = (TextView) layout
                .findViewById(com.buang.welewolf.R.id.dialog_message_button_confirm);
        if (!TextUtils.isEmpty(confirmTxt)) {
            confirmBtn.setText(confirmTxt);
            confirmBtn.setVisibility(View.VISIBLE);
            confirmBtn.setOnClickListener(onClickListener);
            visableView.add(confirmBtn);
        } else {
            confirmBtn.setVisibility(View.GONE);
        }

        TextView cancelBtn = (TextView) layout
                .findViewById(com.buang.welewolf.R.id.dialog_message_button_cancel);
        if (!TextUtils.isEmpty(cancelTxt)) {
            cancelBtn.setText(cancelTxt);
            cancelBtn.setVisibility(View.VISIBLE);
            cancelBtn.setOnClickListener(onClickListener);
            visableView.add(cancelBtn);
        } else {
            cancelBtn.setVisibility(View.GONE);
        }
        View optContainer = layout
                .findViewById(com.buang.welewolf.R.id.dialog_message_button_layout);
        if (TextUtils.isEmpty(confirmTxt) && TextUtils.isEmpty(cancelTxt)) {
            optContainer.setVisibility(View.GONE);
        }
        return dialog;
    }

    /**
     * 带图片通用对话框
     *
     * @param context
     * @param title
     * @param desc
     * @param cancelTxt
     * @param confirmTxt
     * @param listener
     * @return
     */
    public static Dialog getImageCommonDialog(Context context, int id, String title, String desc,
                                              String cancelTxt, String confirmTxt,
                                              final OnDialogButtonClickListener listener) {
        final Dialog dialog = createDialog(context);
        final View layout = View.inflate(context, com.buang.welewolf.R.layout.dialog_common_has_img, null);

        int width = BaseApp.getAppContext().getResources().getDisplayMetrics().widthPixels;
        int padding = UIUtils.dip2px(35) * width / 720;
        layout.setPadding(padding, padding, padding, padding);

        dialog.setContentView(layout);

        ImageView imageView = (ImageView) layout.findViewById(com.buang.welewolf.R.id.dialog_common_image);
        if (id != 0) {
            imageView.setImageResource(id);
        } else {
            imageView.setVisibility(View.GONE);
        }

        TextView titleTxt = (TextView) layout
                .findViewById(com.buang.welewolf.R.id.dialog_common_title);
        if (TextUtils.isEmpty(title)) {
            titleTxt.setVisibility(View.GONE);
        } else {
            titleTxt.setText(title);
        }

        TextView msgTxt = (TextView) layout
                .findViewById(com.buang.welewolf.R.id.dialog_common_desc);
        if (TextUtils.isEmpty(desc)) {
            msgTxt.setVisibility(View.GONE);
        } else {
            msgTxt.setText(desc);
        }

        dialog.setCanceledOnTouchOutside(true);

        dialog.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                try {
                    dialog.dismiss();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        });
        if (dialog.getWindow() != null
                && dialog.getWindow().getDecorView() != null) {
            dialog.getWindow().getDecorView()
                    .setOnTouchListener(new OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            try {
                                dialog.dismiss();
                            } catch (Throwable e) {
                                e.printStackTrace();
                            }
                            return false;
                        }
                    });
        }

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener == null || dialog == null) {
                    return;
                }

                int id = v.getId();
                switch (id) {
                    case com.buang.welewolf.R.id.dialog_button_confirm:
                        listener.onItemClick(dialog,
                                OnDialogButtonClickListener.BUTTON_CONFIRM);
                        break;
                    case com.buang.welewolf.R.id.dialog_button_cancel:
                        listener.onItemClick(dialog,
                                OnDialogButtonClickListener.BUTTON_CANCEL);
                        break;
                    default:
                        break;
                }
            }
        };

        TextView confirmBtn = (TextView) layout
                .findViewById(com.buang.welewolf.R.id.dialog_button_confirm);
        if (!TextUtils.isEmpty(confirmTxt)) {
            confirmBtn.setText(confirmTxt);
            confirmBtn.setVisibility(View.VISIBLE);
            confirmBtn.setOnClickListener(onClickListener);
        } else {
            confirmBtn.setVisibility(View.GONE);
        }

        TextView cancelBtn = (TextView) layout
                .findViewById(com.buang.welewolf.R.id.dialog_button_cancel);
        if (!TextUtils.isEmpty(cancelTxt)) {
            cancelBtn.setText(cancelTxt);
            cancelBtn.setVisibility(View.VISIBLE);
            cancelBtn.setOnClickListener(onClickListener);
        } else {
            cancelBtn.setVisibility(View.GONE);
        }
        View optContainer = layout
                .findViewById(com.buang.welewolf.R.id.dialog_button_layout);
        if (TextUtils.isEmpty(confirmTxt) && TextUtils.isEmpty(cancelTxt)) {
            optContainer.setVisibility(View.GONE);
        }
        return dialog;
    }

    /**
     * 获得通用消息对话框
     *
     * @param context
     * @param title
     * @param confirmTxt
     * @param cancelTxt
     * @param msg
     * @param listener
     * @return
     */
    public static Dialog getMessageDialog(Context context, String title,
                                          String confirmTxt, String cancelTxt, String msg,
                                          OnDialogButtonClickListener listener) {
        if (context == null) {
            return null;
        }
        View view = View.inflate(context, com.buang.welewolf.R.layout.dialog_message, null);
        TextView textView = (TextView) view
                .findViewById(com.buang.welewolf.R.id.dialog_message_txt);
        textView.setText(msg);
        Dialog dialog = getCommonDialog(context, view, title, confirmTxt,
                cancelTxt, listener);
        return dialog;
    }

    /**
     * 带选中的list对话框
     *
     * @param context
     * @param title
     * @param items
     * @param listener
     * @return
     */
    public static Dialog getSelectListDialog(final Context context,
                                             String title, final int index, List<MenuItem> items,
                                             OnItemClickListener listener) {
        View view = View.inflate(context, com.buang.welewolf.R.layout.dialog_select_list, null);
        ListView listView = (ListView) view
                .findViewById(com.buang.welewolf.R.id.dialog_common_list);
        SingleTypeAdapter<MenuItem> adapter = new SingleTypeAdapter<MenuItem>(
                context) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                ViewHolder holder = null;
                if (convertView == null) {
                    convertView = View.inflate(context,
                            com.buang.welewolf.R.layout.dialog_paper_filter_item, null);
                    holder = new ViewHolder();
                    holder.mName = (TextView) convertView
                            .findViewById(com.buang.welewolf.R.id.paper_filter_item_name);
                    holder.mSelect = (ImageView) convertView
                            .findViewById(com.buang.welewolf.R.id.paper_filter_item_select);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }
                holder.mName.setText(getItem(position).title);

                if (position == index) {
                    holder.mSelect.setVisibility(View.VISIBLE);
                    holder.mName.setTextColor(mContext.getResources().getColor(com.buang.welewolf.R.color.color_main_app));
                } else {
                    holder.mName.setTextColor(Color.parseColor("#5f5f5f"));
                    holder.mSelect.setVisibility(View.GONE);
                }
                return convertView;
            }

            class ViewHolder {
                TextView mName;
                ImageView mSelect;
            }
        };
        adapter.setItems(items);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(listener);
        return getCommonDialog(context, view, title, null, null, null);
    }

    /**
     * 获得列表对话框
     *
     * @param context
     * @param title
     * @param items
     * @param listener
     * @return
     */
    public static Dialog getListDialog(Context context, String title,
                                       List<MenuItem> items, OnItemClickListener listener) {
        View view = View.inflate(context, com.buang.welewolf.R.layout.dialog_common_list, null);
        ListView listView = (ListView) view
                .findViewById(com.buang.welewolf.R.id.dialog_common_list);
        DialogListAdapter adapter = new DialogListAdapter(context);
        adapter.setItems(items);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(listener);
        return getCommonDialog(context, view, title, null, null, null);
    }

    /**
     * 批改新手引导
     *
     * @param context
     * @param height
     * @param type
     * @param listener
     * @return
     */
    public static Dialog getCorrectGuideDialog(Activity context, int height,
                                               int type, OnClickListener listener) {
        View view = View.inflate(context, com.buang.welewolf.R.layout.dialog_correct_guide, null);
        View multiGuide = view.findViewById(com.buang.welewolf.R.id.correct_guide_multi_layout);
        ImageView mCorrectView = (ImageView) view
                .findViewById(com.buang.welewolf.R.id.img_guide_correct_2);
        if (type == 0) {
            multiGuide.setVisibility(View.GONE);
            mCorrectView.setVisibility(View.VISIBLE);
        } else {
            multiGuide.setVisibility(View.VISIBLE);
            mCorrectView.setVisibility(View.GONE);

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) multiGuide
                    .getLayoutParams();
            params.topMargin = height;
            multiGuide.setLayoutParams(params);
        }
        view.setOnClickListener(listener);
        Dialog dialog = new Dialog(context, com.buang.welewolf.R.style.IphoneDialog);
        dialog.setContentView(view);
        return dialog;
    }

    public static Dialog getVirtualDialog(Activity activity, int resId, int top, int height, final OnClickListener listener) {
        View view;
        if (top == 0) {
            view = View.inflate(activity, com.buang.welewolf.R.layout.dialog_virtual_layout_new, null);
            ImageView virtualImg = (ImageView) view.findViewById(com.buang.welewolf.R.id.virtual_image);
            LinearLayout.LayoutParams imgParams = (LinearLayout.LayoutParams) virtualImg.getLayoutParams();
            imgParams.height = height;
            virtualImg.setLayoutParams(imgParams);

            ImageView textImg = (ImageView) view.findViewById(com.buang.welewolf.R.id.virtual_image_text_up);
            textImg.setImageResource(resId);
        } else {
            view = View.inflate(activity, com.buang.welewolf.R.layout.dialog_virtual_layout, null);
            ImageView virtualImg = (ImageView) view.findViewById(com.buang.welewolf.R.id.virtual_image);
            LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT
                    , LinearLayout.LayoutParams.WRAP_CONTENT);
            imgParams.height = height;
            virtualImg.setLayoutParams(imgParams);

            ImageView textImg = (ImageView) view.findViewById(com.buang.welewolf.R.id.virtual_image_text_up);
            LinearLayout.LayoutParams upParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT
                    , LinearLayout.LayoutParams.WRAP_CONTENT);
            upParams.height = top;
            textImg.setLayoutParams(upParams);

            ImageView downImg = (ImageView) view.findViewById(com.buang.welewolf.R.id.virtual_image_text_down);
            if (resId != 0) {
                downImg.setImageResource(resId);
            }
        }

        final Dialog dialog = new Dialog(activity, com.buang.welewolf.R.style.VirtualDialog);
        dialog.setContentView(view);
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (listener != null) {
                    listener.onClick(v);
                }
            }
        });
        return dialog;
    }

    public static Dialog getGuideDialog(Activity activity, int resId, int textId, RelativeLayout.LayoutParams params, OnClickListener listener) {
        View view = View.inflate(activity, com.buang.welewolf.R.layout.dialog_guide_layout, null);

        ImageView virtualImg = (ImageView) view.findViewById(com.buang.welewolf.R.id.guide_image);
        virtualImg.setImageResource(resId);
        virtualImg.setLayoutParams(params);

        ImageView textImg = (ImageView) view.findViewById(com.buang.welewolf.R.id.guide_image_text);
        if (textId != 0) {
            textImg.setImageResource(textId);
            textImg.setVisibility(View.VISIBLE);
        } else {
            textImg.setVisibility(View.GONE);
        }
        view.setOnClickListener(listener);
        Dialog dialog = new Dialog(activity, com.buang.welewolf.R.style.IphoneDialog);
        dialog.setContentView(view);

        return dialog;
    }

    public static Dialog getVirtualDialog(Activity activity, int resId, int textId, RelativeLayout.LayoutParams params, OnTouchListener listener) {
        View view = View.inflate(activity, com.buang.welewolf.R.layout.dialog_guide_layout, null);

        ImageView virtualImg = (ImageView) view.findViewById(com.buang.welewolf.R.id.guide_image);
        virtualImg.setImageResource(resId);
        virtualImg.setLayoutParams(params);

        ImageView textImg = (ImageView) view.findViewById(com.buang.welewolf.R.id.guide_image_text);
        if (textId != 0) {
            textImg.setImageResource(textId);
            textImg.setVisibility(View.VISIBLE);
        } else {
            textImg.setVisibility(View.GONE);
        }
        view.setOnTouchListener(listener);
        Dialog dialog = new Dialog(activity, com.buang.welewolf.R.style.VirtualDialog);
        dialog.setContentView(view);

        return dialog;
    }

    /**
     * 拍照出题弹窗口
     *
     * @param context
     * @param listener
     * @return
     * @author weilei
     */
    @SuppressWarnings("deprecation")
    public static PopupWindow getPhotoAssignPopupWindow(Context context,
                                                        final OnPopupWindowClickListener listener) {
        View view = View.inflate(context, com.buang.welewolf.R.layout.dialog_photo_assign_pop,
                null);
        TextView mAlbum = (TextView) view
                .findViewById(com.buang.welewolf.R.id.assign_photo_from_album);
        TextView mCamera = (TextView) view
                .findViewById(com.buang.welewolf.R.id.assign_photo_from_camera);
        TextView mCancel = (TextView) view
                .findViewById(com.buang.welewolf.R.id.assign_photo_cancel);
        View mEmpty = view.findViewById(com.buang.welewolf.R.id.assign_photo_empty);
        mAlbum.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                listener.onItemClick(OnPopupWindowClickListener.BUTTON_CONFIRM);
            }
        });
        mCamera.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                listener.onItemClick(OnPopupWindowClickListener.BUTTON_OTHER);
            }
        });
        mCancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                listener.onItemClick(OnPopupWindowClickListener.BUTTON_CANCEL);
            }
        });
        mEmpty.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                listener.onItemClick(OnPopupWindowClickListener.BUTTON_CANCEL);
            }
        });

        PopupWindow popupWindow = new PopupWindow(view,
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setAnimationStyle(com.buang.welewolf.R.style.PopupWindowDialogWithDim);
        return popupWindow;

    }

    /**
     * 显示更多列表弹出框
     *
     * @param context
     * @param width
     * @param items
     * @param listener
     * @return
     */
    @SuppressWarnings("deprecation")
    public static PopupWindow getMoreListPopupWindow(Context context,
                                                     int width, List<MenuItem> items, OnItemClickListener listener) {
        View view = View.inflate(context, com.buang.welewolf.R.layout.dialog_popup_list, null);
        ListView listView = (ListView) view
                .findViewById(com.buang.welewolf.R.id.dialog_popup_list);
        DialogListAdapter adapter = new DialogListAdapter(context) {
            @Override
            public int getLayoutId() {
                return com.buang.welewolf.R.layout.dialog_popup_list_item;
            }
        };
        adapter.setItems(items);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(listener);

        PopupWindow popupWindow = new PopupWindow(view, width,
                LayoutParams.WRAP_CONTENT, true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setAnimationStyle(com.buang.welewolf.R.style.PopuplistAnimation);
        return popupWindow;
    }

    /**
     * 活动提示对话框
     *
     * @param context
     * @param picUrl
     * @param listener
     * @return
     */
    public static Dialog getActivityDialog(final Activity context,
                                           String picUrl, final OnClickListener listener, final OnLoadCompleteListener loadCompleteListener) {
        View layout = View.inflate(context, com.buang.welewolf.R.layout.dialog_activity, null);

        final Dialog dialog = createDialog(context);
        dialog.setContentView(layout);
        dialog.setCanceledOnTouchOutside(false);
        final ImageView imageView = (ImageView) layout
                .findViewById(com.buang.welewolf.R.id.dialog_activity_img);

        ImageFetcher.getImageFetcher().loadImage(picUrl, "",
                new ImageFetcher.ImageFetcherListener() {
                    @Override
                    public void onLoadComplete(String imageUrl, Bitmap bitmap,
                                               Object object) {
                        if (bitmap == null)
                            return;
                        Bitmap bm = null;
                        int width = (int) (UIUtils.getWindowWidth(context) * 0.85f);
                        int maxWidth = width > 1200 ? 1200 : width;
                        int height = (int) (maxWidth * bitmap.getHeight() / bitmap.getWidth());
                        bm = ImageUtil.getResizedBitmap(bitmap, maxWidth,
                                height);

                        if (bm != null && !bm.isRecycled()) {
                            imageView.setImageBitmap(bm);
                            if (loadCompleteListener != null) {
                                loadCompleteListener.onLoadComplete();
                            }
                        }
                    }
                });

        imageView.setOnClickListener(listener);

        View deleteImg = layout.findViewById(com.buang.welewolf.R.id.dialog_activity_delete);
        deleteImg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                UmengConstant.reportUmengEvent(UmengConstant.EVENT_ACTIVITY_DIALOG_CLOSE, null);
                dialog.dismiss();
            }
        });

        return dialog;
    }

    /**
     * 分享提示框
     *
     * @param context
     * @param listener
     * @return
     */
    public static Dialog getShareDialog(final Activity context,
                                        List<MenuItem> items,
                                        final OnDialogButtonClickListener listener,
                                        OnItemClickListener itemClickListener) {
        View layout = View.inflate(context, com.buang.welewolf.R.layout.dialog_share_gridview,
                null);

        final Dialog dialog = createDialog(context);
        dialog.setContentView(layout);
        dialog.setCanceledOnTouchOutside(true);

        if (dialog.getWindow() != null
                && dialog.getWindow().getDecorView() != null) {
            dialog.getWindow().getDecorView()
                    .setOnTouchListener(new OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            try {
                                dialog.dismiss();
                            } catch (Throwable e) {
                                e.printStackTrace();
                            }
                            return false;
                        }
                    });
        }
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener == null || dialog == null) {
                    return;
                }

                int id = v.getId();
                switch (id) {
                    case com.buang.welewolf.R.id.dialog_share_confirm:
                        listener.onItemClick(dialog,
                                OnDialogButtonClickListener.BUTTON_CONFIRM);
                        break;
                    case com.buang.welewolf.R.id.dialog_share_cancel:
                        listener.onItemClick(dialog,
                                OnDialogButtonClickListener.BUTTON_CANCEL);
                        break;
                    default:
                        break;
                }
            }
        };

        GridView mGridView = (GridView) layout
                .findViewById(com.buang.welewolf.R.id.dialog_share_gridview);
        TextView confirmTxt = (TextView) layout
                .findViewById(com.buang.welewolf.R.id.dialog_share_confirm);
        TextView cancelTxt = (TextView) layout
                .findViewById(com.buang.welewolf.R.id.dialog_share_cancel);
        confirmTxt.setOnClickListener(onClickListener);
        cancelTxt.setOnClickListener(onClickListener);

        SingleTypeAdapter<MenuItem> mAdapter = new SingleTypeAdapter<MenuItem>(
                context) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                MenuItem item = getItem(position);
                TextView textView = new TextView(context);
                textView.setText(item.title);
                textView.setTextSize(10);
                textView.setTextColor(Color.parseColor("#525252"));
                Drawable icon = context.getResources().getDrawable(item.icon);
                icon.setBounds(0, 0, icon.getMinimumWidth(),
                        icon.getMinimumHeight());
                textView.setCompoundDrawables(null, icon, null, null);
                textView.setGravity(Gravity.CENTER);
                textView.setCompoundDrawablePadding(UIUtils.dip2px(7));
                return textView;
            }
        };
        mAdapter.setItems(items);
        mGridView.setAdapter(mAdapter);
        mGridView.setOnItemClickListener(itemClickListener);

        return dialog;
    }

    /**
     * 分享提示框
     */
    public static Dialog getShareHomeworkDialog(final Activity context,
                                                List<MenuItem> items, String title,
                                                OnItemClickListener itemClickListener) {
        View layout = View.inflate(context, com.buang.welewolf.R.layout.dialog_sharehomework_view,
                null);

        final Dialog dialog = createDialog(context);
        dialog.setContentView(layout);
        dialog.setCanceledOnTouchOutside(true);

        if (dialog.getWindow() != null
                && dialog.getWindow().getDecorView() != null) {
            dialog.getWindow().getDecorView()
                    .setOnTouchListener(new OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            try {
                                dialog.dismiss();
                            } catch (Throwable e) {
                                e.printStackTrace();
                            }
                            return false;
                        }
                    });
        }

        TextView mTtitle = (TextView) layout.findViewById(com.buang.welewolf.R.id.title);
        if (!TextUtils.isEmpty(title)) {
            mTtitle.setText(title);
        }

        GridView mGridView = (GridView) layout.findViewById(com.buang.welewolf.R.id.dialog_share_gridview);

        SingleTypeAdapter<MenuItem> mAdapter = new SingleTypeAdapter<MenuItem>(
                context) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                MenuItem item = getItem(position);
                TextView textView = new TextView(context);
                textView.setText(item.title);
                textView.setTextSize(10);
                textView.setTextColor(Color.parseColor("#525252"));
                Drawable icon = context.getResources().getDrawable(item.icon);
                icon.setBounds(0, 0, icon.getMinimumWidth(),
                        icon.getMinimumHeight());
                textView.setCompoundDrawables(null, icon, null, null);
                textView.setGravity(Gravity.CENTER);
                textView.setCompoundDrawablePadding(UIUtils.dip2px(10));
                return textView;
            }
        };
        mAdapter.setItems(items);
        mGridView.setNumColumns(items.size());
        mGridView.setAdapter(mAdapter);
        mGridView.setOnItemClickListener(itemClickListener);

        return dialog;
    }

    /**
     * 班级转移对话框
     *
     * @param context
     * @param title
     * @param confirmTxt
     * @param cancelTxt
     * @param message
     * @param icon
     * @param listener
     * @return
     */
    public static Dialog getClassTransferDialog(final Context context,
                                                String title, String confirmTxt, String cancelTxt, String message,
                                                int icon, final OnDialogButtonClickListener listener) {
        {
            View layout = View.inflate(context,
                    com.buang.welewolf.R.layout.dialog_class_transfer_tips, null);

            int width = BaseApp.getAppContext().getResources()
                    .getDisplayMetrics().widthPixels;
            int padding = UIUtils.dip2px(35) * width / 720;
            layout.setPadding(padding, 0, padding, 0);

            final Dialog dialog = createDialog(context);
            dialog.setContentView(layout);
            dialog.setCanceledOnTouchOutside(false);
            final View devider = layout
                    .findViewById(com.buang.welewolf.R.id.dialog_common_devider);

            TextView mTitle = (TextView) layout
                    .findViewById(com.buang.welewolf.R.id.dialog_correct_title);
            if (TextUtils.isEmpty(title))
                mTitle.setText(message);
            else {
                SpannableString sp = new SpannableString(message);
                int index = message.indexOf(title);
                sp.setSpan(
                        new ForegroundColorSpan(context.getResources().getColor(com.buang.welewolf.R.color.color_main_app)),
                        index, index + title.length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                mTitle.setText(sp);
            }

            ImageView mImg = (ImageView) layout
                    .findViewById(com.buang.welewolf.R.id.dialog_title_icon);
            mImg.setImageResource(icon);

            View.OnClickListener onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener == null || dialog == null) {
                        return;
                    }

                    int id = v.getId();
                    switch (id) {
                        case com.buang.welewolf.R.id.dialog_common_confirm:
                            listener.onItemClick(dialog,
                                    OnDialogButtonClickListener.BUTTON_CONFIRM);
                            break;
                        case com.buang.welewolf.R.id.dialog_common_cancel:
                            listener.onItemClick(dialog,
                                    OnDialogButtonClickListener.BUTTON_CANCEL);
                            break;
                        default:
                            break;
                    }
                }
            };

            List<View> visableView = new ArrayList<View>();
            TextView confirmBtn = (TextView) layout
                    .findViewById(com.buang.welewolf.R.id.dialog_common_confirm);
            if (!TextUtils.isEmpty(confirmTxt)) {
                confirmBtn.setText(confirmTxt);
                confirmBtn.setVisibility(View.VISIBLE);
                confirmBtn.setOnClickListener(onClickListener);
                visableView.add(confirmBtn);
            } else {
                confirmBtn.setVisibility(View.GONE);
                devider.setVisibility(View.GONE);
            }

            TextView cancelBtn = (TextView) layout
                    .findViewById(com.buang.welewolf.R.id.dialog_common_cancel);
            if (!TextUtils.isEmpty(cancelTxt)) {
                cancelBtn.setText(cancelTxt);
                cancelBtn.setVisibility(View.VISIBLE);
                cancelBtn.setOnClickListener(onClickListener);
                visableView.add(cancelBtn);
            } else {
                cancelBtn.setVisibility(View.GONE);
                devider.setVisibility(View.GONE);
            }
            View optContainer = layout
                    .findViewById(com.buang.welewolf.R.id.dialog_common_opt_container);
            if (TextUtils.isEmpty(confirmTxt) && TextUtils.isEmpty(cancelTxt)) {
                optContainer.setVisibility(View.GONE);
            }
            return dialog;
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    public static Dialog getCheckboxDialog(final Context context, String title,
                                           String confirmTxt, String cancelTxt, String message, int icon,
                                           final OnFillDialogBtnClickListener listener) {
        View layout = View.inflate(context,
                com.buang.welewolf.R.layout.dialog_correct_markrmd_tips, null);

        int width = BaseApp.getAppContext().getResources().getDisplayMetrics().widthPixels;
        int padding = UIUtils.dip2px(35) * width / 720;
        layout.setPadding(padding, 0, padding, 0);

        final Dialog dialog = createDialog(context);
        dialog.setContentView(layout);
        dialog.setCanceledOnTouchOutside(true);
        final View devider = layout.findViewById(com.buang.welewolf.R.id.dialog_common_devider);

        TextView mTitle = (TextView) layout
                .findViewById(com.buang.welewolf.R.id.dialog_correct_title);
        mTitle.setText(title);

        ImageView mImg = (ImageView) layout
                .findViewById(com.buang.welewolf.R.id.dialog_correct_icon);
        mImg.setImageResource(icon);

        final CheckBox checkView = (CheckBox) layout
                .findViewById(com.buang.welewolf.R.id.dialog_correct_markrmd);
        dialog.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                try {
                    dialog.dismiss();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        });
        if (dialog.getWindow() != null
                && dialog.getWindow().getDecorView() != null) {
            dialog.getWindow().getDecorView()
                    .setOnTouchListener(new OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            try {
                                dialog.dismiss();
                            } catch (Throwable e) {
                                e.printStackTrace();
                            }
                            return false;
                        }
                    });
        }

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener == null || dialog == null) {
                    return;
                }

                int id = v.getId();
                switch (id) {
                    case com.buang.welewolf.R.id.dialog_common_confirm:
                        listener.onItemClick(dialog, true,
                                String.valueOf(checkView.isChecked()));
                        break;
                    case com.buang.welewolf.R.id.dialog_common_cancel:
                        listener.onItemClick(dialog, false,
                                String.valueOf(checkView.isChecked()));
                        break;
                    default:
                        break;
                }
            }
        };

        List<View> visableView = new ArrayList<View>();
        TextView confirmBtn = (TextView) layout
                .findViewById(com.buang.welewolf.R.id.dialog_common_confirm);
        if (!TextUtils.isEmpty(confirmTxt)) {
            confirmBtn.setText(confirmTxt);
            confirmBtn.setVisibility(View.VISIBLE);
            confirmBtn.setOnClickListener(onClickListener);
            visableView.add(confirmBtn);
        } else {
            confirmBtn.setVisibility(View.GONE);
            devider.setVisibility(View.GONE);
        }

        TextView cancelBtn = (TextView) layout
                .findViewById(com.buang.welewolf.R.id.dialog_common_cancel);
        if (!TextUtils.isEmpty(cancelTxt)) {
            cancelBtn.setText(cancelTxt);
            cancelBtn.setVisibility(View.VISIBLE);
            cancelBtn.setOnClickListener(onClickListener);
            visableView.add(cancelBtn);
        } else {
            cancelBtn.setVisibility(View.GONE);
            devider.setVisibility(View.GONE);
        }
        View optContainer = layout
                .findViewById(com.buang.welewolf.R.id.dialog_common_opt_container);
        if (TextUtils.isEmpty(confirmTxt) && TextUtils.isEmpty(cancelTxt)) {
            optContainer.setVisibility(View.GONE);
        }
        return dialog;
    }

    public static Dialog getFillBlackDialog(final Activity context,
                                            String title, String okString, String cancelString,
                                            String srcReslut, int type, final OnFillDialogBtnClickListener listener) {
        View view = View.inflate(context, com.buang.welewolf.R.layout.dialog_fill_black, null);
        final EditText blackText = (EditText) view
                .findViewById(com.buang.welewolf.R.id.result_text);
        if (type != -1)
            blackText.setInputType(type);
        if (type == InputType.TYPE_TEXT_VARIATION_PASSWORD) {
            blackText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
        blackText.clearFocus();
        com.hyena.framework.utils.UIUtils.hideInputMethod(context);
        if (!TextUtils.isEmpty(srcReslut)) {
            blackText.setHint(srcReslut);
        }
        blackText.setHintTextColor(Color.parseColor("#c6c6c6"));
        Dialog dialog = getCommonDialog(context, view, title, okString,
                cancelString, new OnDialogButtonClickListener() {
                    @Override
                    public void onItemClick(Dialog dialog, int btnId) {
                        boolean ret = false;
                        if (btnId == 0) {
                            ret = true;
                        }
                        if (listener != null) {
                            listener.onItemClick(dialog, ret, blackText
                                    .getText().toString().trim());
                        }
                    }
                });
        dialog.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                com.hyena.framework.utils.UIUtils.hideInputMethod(context);
            }
        });
        return dialog;
    }

    /**
     * 同学页班级选择器
     *
     * @param context
     * @param items
     * @param listener
     * @return
     * @author weilei
     */
    @SuppressWarnings("deprecation")
    @SuppressLint("ViewHolder")
    public static PopupWindow getClassSelectPopWindow(final Context context,
                                                      final int index, final List<MenuItem> items,
                                                      OnItemClickListener listener, OnClickListener clickListener) {
        View view = View.inflate(context, com.buang.welewolf.R.layout.dialog_class_select, null);
        ListView listView = (ListView) view
                .findViewById(com.buang.welewolf.R.id.dialog_popup_list);
        SingleTypeAdapter<MenuItem> adapter = new SingleTypeAdapter<MenuItem>(
                context) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = View.inflate(context,
                        com.buang.welewolf.R.layout.dialog_class_select_item, null);
                TextView textView = (TextView) view
                        .findViewById(com.buang.welewolf.R.id.item_text);
                TextView numView = (TextView) view.findViewById(com.buang.welewolf.R.id.item_num);
                MenuItem item = getItem(position);
                textView.setText(item.title);
                numView.setText(" (" + item.desc + ")");
                if (position == index) {
                    textView.setTextColor(context.getResources().getColor(com.buang.welewolf.R.color.color_main_app));
                    numView.setTextColor(context.getResources().getColor(com.buang.welewolf.R.color.color_main_app));
                }
                return view;
            }
        };
        adapter.setItems(items);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(listener);
        view.setOnClickListener(clickListener);

        PopupWindow popupWindow = new PopupWindow(view,
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        return popupWindow;

    }

    /**
     * 语音解析pop
     *
     * @param context
     * @param listener
     * @return
     * @author weilei
     */
    public static Dialog getRecorderGuidePop(final Activity context,
                                             int height, OnClickListener listener) {
        RelativeLayout view = new RelativeLayout(context);
        view.setBackgroundColor(context.getResources().getColor(
                com.buang.welewolf.R.color.color_black_50));
        ImageView img = new ImageView(context);
        img.setImageResource(com.buang.welewolf.R.drawable.bt_guide_recorder_analyizer_1);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        if (height < 500) {
            img.setImageResource(com.buang.welewolf.R.drawable.bt_guide_recorder_analyizer_2);
        } else if (height > UIUtils.getWindowHeight(context)) {
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        } else {
            params.topMargin = UIUtils.px2dip(height - img.getHeight());
        }
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        view.addView(img, params);
        view.setOnClickListener(listener);

        Dialog dialog = new Dialog(context, com.buang.welewolf.R.style.IphoneDialog);
        dialog.setContentView(view);
        return dialog;

    }

    public static Dialog getRecorderCorrectDialog(final Activity context,
                                                  int height, boolean isVoice, OnClickListener listener) {
        RelativeLayout view = new RelativeLayout(context);
        view.setBackgroundColor(context.getResources().getColor(
                com.buang.welewolf.R.color.color_black_50));
        if (isVoice) {
            ImageView img = new ImageView(context);
            img.setImageResource(com.buang.welewolf.R.drawable.bt_guide_recorder_correct_1);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);

            params.topMargin = UIUtils.px2dip(height - img.getHeight());
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            view.addView(img, params);
        } else {
            ImageView img1 = new ImageView(context);
            img1.setImageResource(com.buang.welewolf.R.drawable.bt_guide_recorder_correct_2);
            RelativeLayout.LayoutParams params1 = (RelativeLayout.LayoutParams) new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            params1.alignWithParent = true;
            params1.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            params1.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            view.addView(img1, params1);
        }

        view.setOnClickListener(listener);

        Dialog dialog = new Dialog(context, com.buang.welewolf.R.style.IphoneDialog);
        dialog.setContentView(view);
        return dialog;

    }

    /**
     * 添加班级对话框
     *
     * @param context
     * @param title
     * @param confirmTxt
     * @param cancelTxt
     * @param listener
     * @return
     * @author weilei
     */
    public static Dialog getAddClassDialog(final Context context, String title,
                                           String confirmTxt, String cancelTxt,
                                           final OnFillDialogBtnClickListener listener) {
        final AddClassView view = (AddClassView) View.inflate(context,
                com.buang.welewolf.R.layout.dialog_add_class, null);

        Dialog dialog = getCommonDialog(context, view, title, confirmTxt,
                cancelTxt, new OnDialogButtonClickListener() {

                    @Override
                    public void onItemClick(Dialog dialog, int btnId) {
                        boolean ret = false;
                        if (btnId == 0) {
                            ret = true;
                        }
                        if (listener != null) {
                            if (!TextUtils.isEmpty(view.getGradeName())
                                    && !TextUtils.isEmpty(view.getClassName())) {
                                String result = view.getGradeName() + ":"
                                        + view.getClassName();
                                listener.onItemClick(dialog, ret, result);
                            } else {
                                listener.onItemClick(dialog, ret, null);
                            }

                        }
                    }
                });
        return dialog;
    }

    /**
     * 通用对话框
     *
     * @param context    上下文
     * @param view       要展现的View
     * @param title      标题
     * @param confirmTxt 确认
     * @param cancelTxt  取消
     * @param listener   按钮点击事件
     * @return
     */
    @SuppressLint("ClickableViewAccessibility")
    public static Dialog getCommonDialog(final Context context, View view,
                                         String title, String confirmTxt, String cancelTxt,
                                         final OnDialogButtonClickListener listener) {
        final Dialog dialog = createDialog(context);
        final View layout = View.inflate(context, com.buang.welewolf.R.layout.dialog_common, null);

        int width = BaseApp.getAppContext().getResources().getDisplayMetrics().widthPixels;
        int padding = UIUtils.dip2px(25) * width / 720;
        layout.setPadding(padding, padding, padding, padding);

        ViewGroup container = (ViewGroup) layout
                .findViewById(com.buang.welewolf.R.id.dialog_common_container);
        container.addView(view);
        dialog.setContentView(layout);
        View titlePanel = layout
                .findViewById(com.buang.welewolf.R.id.dialog_common_title_container);
        if (TextUtils.isEmpty(title)) {
            titlePanel.setVisibility(View.GONE);
        } else {
            TextView titleTxt = (TextView) layout
                    .findViewById(com.buang.welewolf.R.id.dialog_common_title);
            titleTxt.setText(title);
        }
        dialog.setCanceledOnTouchOutside(true);
        final View devider = layout.findViewById(com.buang.welewolf.R.id.dialog_common_devider);

        dialog.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                try {
                    dialog.dismiss();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        });
        if (dialog.getWindow() != null
                && dialog.getWindow().getDecorView() != null) {
            dialog.getWindow().getDecorView()
                    .setOnTouchListener(new OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            try {
                                dialog.dismiss();
                            } catch (Throwable e) {
                                e.printStackTrace();
                            }
                            return false;
                        }
                    });
        }

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener == null || dialog == null) {
                    return;
                }

                int id = v.getId();
                switch (id) {
                    case com.buang.welewolf.R.id.dialog_common_confirm:
                        listener.onItemClick(dialog,
                                OnDialogButtonClickListener.BUTTON_CONFIRM);
                        break;
                    case com.buang.welewolf.R.id.dialog_common_cancel:
                        listener.onItemClick(dialog,
                                OnDialogButtonClickListener.BUTTON_CANCEL);
                        break;
                    default:
                        break;
                }
            }
        };

        List<View> visableView = new ArrayList<View>();
        TextView confirmBtn = (TextView) layout
                .findViewById(com.buang.welewolf.R.id.dialog_common_confirm);
        if (!TextUtils.isEmpty(confirmTxt)) {
            confirmBtn.setText(confirmTxt);
            confirmBtn.setVisibility(View.VISIBLE);
            confirmBtn.setOnClickListener(onClickListener);
            visableView.add(confirmBtn);
        } else {
            confirmBtn.setVisibility(View.GONE);
            devider.setVisibility(View.GONE);
        }

        TextView cancelBtn = (TextView) layout
                .findViewById(com.buang.welewolf.R.id.dialog_common_cancel);
        if (!TextUtils.isEmpty(cancelTxt)) {
            cancelBtn.setText(cancelTxt);
            cancelBtn.setVisibility(View.VISIBLE);
            cancelBtn.setOnClickListener(onClickListener);
            visableView.add(cancelBtn);
        } else {
            cancelBtn.setVisibility(View.GONE);
            devider.setVisibility(View.GONE);
        }
        View optContainer = layout
                .findViewById(com.buang.welewolf.R.id.dialog_common_opt_container);
        if (TextUtils.isEmpty(confirmTxt) && TextUtils.isEmpty(cancelTxt)) {
            optContainer.setVisibility(View.GONE);
        }
        return dialog;
    }

    /**
     * 搜索对话框
     *
     * @param activity
     * @param title
     * @param hint
     * @param listener
     * @return
     */
    public static Dialog getSearchDialog(Activity activity, String title, String hint, final OnFillDialogBtnClickListener listener) {
        final Dialog dialog = createDialog(activity);
        View view = View.inflate(activity, R.layout.dialog_welewolf_search, null);
        dialog.setContentView(view);

        TextView titleView = (TextView) view.findViewById(R.id.tvTitle);
        titleView.setText(title);

        final EditText editText = (EditText) view.findViewById(R.id.evEdit);
        editText.setHint(hint);

        view.findViewById(R.id.bvCancel).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        view.findViewById(R.id.bvConfirm).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null && TextUtils.isEmpty(editText.getText().toString())) {
                    listener.onItemClick(dialog, true, editText.getText().toString());
                }
            }
        });

        return dialog;
    }


    /**
     * 帮助普通模式规则
     *
     * @param activity
     * @return
     */
    public static Dialog getNormalDialog(Activity activity) {
        final Dialog dialog = new Dialog(activity, com.buang.welewolf.R.style.VirtualDialog);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        dialog.getWindow().setWindowAnimations(com.buang.welewolf.R.style.DialogAnim);
        View view = View.inflate(activity, R.layout.dialog_welewolf_help_normal, null);
        dialog.setContentView(view);

        view.findViewById(R.id.blClose).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        return dialog;
    }

    /**
     * 狼人杀词典
     *
     * @param activity
     * @return
     */
    @TargetApi(Build.VERSION_CODES.GINGERBREAD_MR1)
    public static Dialog getHelpRoleDialog(Activity activity) {
        final Dialog dialog = new Dialog(activity, com.buang.welewolf.R.style.VirtualDialog);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        dialog.getWindow().setWindowAnimations(com.buang.welewolf.R.style.DialogAnim);
        View view = View.inflate(activity, R.layout.dialog_welewolf_help_role, null);
        dialog.setContentView(view);

        view.findViewById(R.id.blClose).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        return dialog;
    }

    /**
     * 进阶模式介绍
     *
     * @param activity
     * @return
     */
    public static Dialog getHelpAdvanceDialog(Activity activity) {
        final Dialog dialog = new Dialog(activity, com.buang.welewolf.R.style.VirtualDialog);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        dialog.getWindow().setWindowAnimations(com.buang.welewolf.R.style.DialogAnim);
        View view = View.inflate(activity, R.layout.dialog_welewolf_help_advance, null);
        dialog.setContentView(view);

        view.findViewById(R.id.blClose).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        return dialog;
    }

    /**
     * 弹出对话框点击效果
     *
     * @author yangzc
     */
    public static interface OnDialogButtonClickListener {
        public static final int BUTTON_CONFIRM = 0;
        public static final int BUTTON_CANCEL = 1;
        public static final int BUTTON_OTHER = 2;

        public void onItemClick(Dialog dialog, int btnId);
    }

    public static interface OnDialogListCheckListener<T> {
        public void onItemCheck(List<T> t);
    }

    public static interface OnPopupWindowClickListener {
        public static final int BUTTON_CONFIRM = 0;
        public static final int BUTTON_CANCEL = 1;
        public static final int BUTTON_OTHER = 2;

        public void onItemClick(int btnId);
    }

    public static interface OnLoadCompleteListener {
        public void onLoadComplete();
    }

    public static interface OnFillDialogBtnClickListener {
        public void onItemClick(Dialog dialog, boolean isConfirm, String resutl);
    }

    /**
     * 创建Dialog
     *
     * @param context
     * @return
     */
    private static Dialog createDialog(Context context) {
        Dialog dialog = new Dialog(context, com.buang.welewolf.R.style.IphoneDialog);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        dialog.getWindow().setWindowAnimations(com.buang.welewolf.R.style.DialogAnim);
        return dialog;
    }

    private static class DialogListAdapter extends
            SingleTypeAdapter<MenuItem> {
        public DialogListAdapter(Context context) {
            super(context);
        }

        public int getLayoutId() {
            return com.buang.welewolf.R.layout.dialog_common_list_item;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = View.inflate(mContext, getLayoutId(), null);
                holder = new ViewHolder();
                holder.mIcon = (ImageView) convertView
                        .findViewById(com.buang.welewolf.R.id.dialog_common_list_item_icon);
                holder.mTitleTxt = (TextView) convertView
                        .findViewById(com.buang.welewolf.R.id.dialog_common_list_item_title);
                holder.mDescTxt = (TextView) convertView
                        .findViewById(com.buang.welewolf.R.id.dialog_common_list_item_desc);
                holder.mDevider = convertView
                        .findViewById(com.buang.welewolf.R.id.dialog_common_list_item_devider);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            MenuItem item = getItem(position);
            if (item.icon > 0) {
                holder.mIcon.setVisibility(View.VISIBLE);
                holder.mIcon.setImageResource(item.icon);
            } else {
                holder.mIcon.setVisibility(View.GONE);
            }
            holder.mTitleTxt.setText(item.title);
            if (!TextUtils.isEmpty(item.desc)) {
                holder.mDescTxt.setVisibility(View.VISIBLE);
                holder.mDescTxt.setText(item.desc);
            } else {
                holder.mDescTxt.setVisibility(View.GONE);
            }
            if (position == getCount() - 1) {
                holder.mDevider.setVisibility(View.GONE);
            } else {
                holder.mDevider.setVisibility(View.VISIBLE);
            }
            return convertView;
        }

        private class ViewHolder {
            public ImageView mIcon;
            public TextView mTitleTxt;
            public TextView mDescTxt;
            public View mDevider;
        }
    }

}
