package com.xasfemr.meiyaya.utils;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Editable;
import android.text.Html;
import android.text.Selection;
import android.text.Spannable;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;

import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.functions.Action1;


public class UIUtil {

    public static final float DARK_ALPHA = .4F;
    public static final float BRIGHT_ALPHA = 1.0F;
    private static String TAG = "UIUtil";

    public static void moveCursor2End(Spannable text) {
        try {
            Selection.setSelection(text, text.length());
        } catch (Exception e) {
            LogUtils.show(TAG, "move cursor to end failure.");
            e.printStackTrace();
        }
    }


    public static void darken(Activity activity) {
        darken(activity, false);
    }

    public static void darken(Activity activity, boolean anim) {
        changeAlpha(activity, DARK_ALPHA, anim);
    }

    public static void brighten(Activity activity) {
        brighten(activity, false);
    }

    public static void brighten(Activity activity, boolean anim) {
        changeAlpha(activity, BRIGHT_ALPHA, anim);
    }

    private static void changeAlpha(Activity activity, float alpha, boolean anim) {
        if (activity == null) {
            LogUtils.show(TAG, "activity is null");
            return;
        }
        WindowManager.LayoutParams layoutParams = activity.getWindow().getAttributes();
        if (anim) {
            float startAlpha = layoutParams.alpha;
            final ValueAnimator animation = ValueAnimator.ofFloat(startAlpha, alpha);
            animation.setDuration(300);
            animation.start();
            animation.addUpdateListener(valueAnimator -> {
                layoutParams.alpha = (Float) valueAnimator.getAnimatedValue();
                activity.getWindow().setAttributes(layoutParams);
            });
            return;
        }
        layoutParams.alpha = alpha;
        activity.getWindow().setAttributes(layoutParams);
    }


//    public static <T> T setTag(RadioGroup radioGroup, List<T> tagList) {
//        return setTag(radioGroup, tagList, CollectionUtil.isEmpty(tagList) ? null : tagList.get(0));
//    }
//
//    public static <T> T setTag(RadioGroup radioGroup, List<T> tagList, T defaultTag) {
//        if (CollectionUtil.isEmpty(tagList) || radioGroup == null) {
//            return defaultTag;
//        }
//        int index = 0;
//        for (int i = 0; i < radioGroup.getChildCount(); i++) {
//            View view = radioGroup.getChildAt(i);
//            if (!(view instanceof RadioButton)) {
//                continue;
//            }
//            RadioButton radioButton = (RadioButton) view;
//            T tag = tagList.size() > index ? tagList.get(index) : null;
//            radioButton.setTag(tag);
//            radioButton.setChecked(tag != null && defaultTag != null && tag.equals(defaultTag));
//            index++;
//        }
//        return defaultTag;
//    }

    public static void setText(EditText editText, String text) {
        if (editText == null) {
            return;
        }
        editText.setText(text);
        moveCursor2End(editText.getText());
    }

    public static String getText(Editable editable) {
        return getText(editable, "");
    }

    public static String getText(Editable editable, String defaultValue) {
        if (editable == null) {
            return defaultValue;
        }
        return editable.toString();
    }

    public static String getText(TextView textView) {
        return getText(textView, "");
    }

    public static String getText(TextView textView, String defaultValue) {
        if (textView == null) {
            return defaultValue;
        }
        CharSequence charSequence = textView.getText();
        if (charSequence == null) {
            return defaultValue;
        }
        return charSequence.toString().trim();
    }

    public static boolean isVisible(View view) {
        return view.getVisibility() == View.VISIBLE;
    }

    public static void switchVisibleOrGone(View view) {
        if (view == null) {
            return;
        }
        view.setVisibility(isVisible(view) ? View.GONE : View.VISIBLE);
    }

    public static void setVisibleOrGone(View view, boolean condition) {
        if (view != null) {
            view.setVisibility(condition ? View.VISIBLE : View.GONE);
        }
    }

    public static void setVisibleOrInvisible(View view, boolean condition) {
        if (view != null) {
            view.setVisibility(condition ? View.VISIBLE : View.INVISIBLE);
        }
    }

    public static void setInvisible(View view) {
        setVisibleOrInvisible(view, true);
    }

    public static void setGone(View view) {
        setVisibleOrGone(view, false);
    }

    public static void setEllipsis(final TextView textView, final int line) {
        ViewTreeObserver observer = textView.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                ViewTreeObserver obs = textView.getViewTreeObserver();
                obs.removeGlobalOnLayoutListener(this);
                if (textView.getLineCount() > line) {
                    int lineEndIndex = textView.getLayout().getLineEnd(line - 1);
                    String text = textView.getText().subSequence(0, lineEndIndex - 3) + "...";
                    textView.setText(text);
                }
            }
        });

    }

    public static <T> boolean isEmpty(List<T> list) {
        if (list == null || list.size() == 0) {
            return true;
        }
        return false;
    }
    public static int getWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    public static int getHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }


    // 将px值转换为sp值
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    public static void showSoftInput(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
    }

    public static void hideSoftInput(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void setOnclickInterval(View view, Action1 action1) {
        RxView.clicks(view)
                .throttleFirst(2000, TimeUnit.MILLISECONDS)
                .subscribe(action1);
    }


    /**
     * Convert a translucent themed Activity
     * {@link android.R.attr#windowIsTranslucent} to a fullscreen opaque
     * Activity.
     * <p>
     * Call this whenever the background of a translucent Activity has changed
     * to become opaque. Doing so will allow the {@link android.view.Surface} of
     * the Activity behind to be released.
     * <p>
     * This call has no effect on non-translucent activities or on activities
     * with the {@link android.R.attr#windowIsFloating} attribute.
     */
    public static void convertActivityFromTranslucent(Activity activity) {
        try {
            Method method = Activity.class.getDeclaredMethod("convertFromTranslucent");
            method.setAccessible(true);
            method.invoke(activity);
        } catch (Throwable t) {
        }
    }

    /**
     * Convert a translucent themed Activity
     * {@link android.R.attr#windowIsTranslucent} back from opaque to
     * translucent following a call to
     * {@link #convertActivityFromTranslucent(Activity)} .
     * <p>
     * Calling this allows the Activity behind this one to be seen again. Once
     * all such Activities have been redrawn
     * <p>
     * This call has no effect on non-translucent activities or on activities
     * with the {@link android.R.attr#windowIsFloating} attribute.
     */
    public static void convertActivityToTranslucent(Activity activity) {
        try {
            Class<?>[] classes = Activity.class.getDeclaredClasses();
            Class<?> translucentConversionListenerClazz = null;
            for (Class clazz : classes) {
                if (clazz.getSimpleName().contains(
                        "TranslucentConversionListener")) {
                    translucentConversionListenerClazz = clazz;
                }
            }
            Method[] methods = Activity.class.getDeclaredMethods();
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                Method method = Activity.class.getDeclaredMethod(
                        "convertToTranslucent",
                        translucentConversionListenerClazz);
                method.setAccessible(true);
                method.invoke(activity, new Object[]{null});
            } else {
                Method method = Activity.class.getDeclaredMethod(
                        "convertToTranslucent",
                        translucentConversionListenerClazz,
                        ActivityOptions.class);
                method.setAccessible(true);
                method.invoke(activity, new Object[]{null, null});
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    /**
     * TexTView 拼接图片
     * @param context
     * @return
     */
    public static Html.ImageGetter spliceTextView(Context context){

        Html.ImageGetter imageGetter =new Html.ImageGetter() {
            @Override
            public Drawable getDrawable(String source) {
                int resId = Integer.parseInt(source);
                Drawable drawable = context.getResources()
                        .getDrawable(resId);
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                        drawable.getIntrinsicHeight());

                return drawable;
            }
        };

        return imageGetter;
    }


    /**
     * 获取屏幕宽高
     * @param context
     * @return
     */
    public static int getScreenSize(Context context,boolean ifHeight) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);

        if (ifHeight){

            return outMetrics.heightPixels;
        }else {

            return outMetrics.widthPixels;
        }
    }


//
//    public static Drawable getSpliceTextView(Context context){
//        Drawable drawable =context.getResources().getDrawable(R.mipmap.icon_time_nor);
//        drawable.setBounds(0,0,drawable.getMinimumWidth(),drawable.getMinimumHeight());
//        return drawable;
//    }
}