package com.reactnativeuxkit;

import android.app.Activity;
import android.content.DialogInterface;

import android.graphics.Color;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeMap;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import javax.annotation.Nullable;

public class UxKitModule extends ReactContextBaseJavaModule {
    private static final String ERROR_NO_ACTIVITY = "E_NO_ACTIVITY";
    private static final String FRAGMENT_TAG = "UxKit";
    private static final String ARG_HOUR = "hour";
    private static final String ARG_MINUTE = "minute";
    private static final String ARG_MINUTE_INTERVAL = "minuteInterval";
    private static final String ARG_HOUR_INTERVAL = "hourInterval";
    private static final String ARG_TITLE = "title";
    private static final String ARG_COLOR = "color";
    private static final String ACTION_SET = "setAction";
    private static final String ACTION_CANCEL = "cancelAction";
    private static final String ARG_MAX_HOUR = "maxHour";
    private static final String ARG_MAX_MINUTE = "maxMinute";
    private static final String ARG_CANCEL_TEXT = "cancelText";
    private static final String ARG_DARK_THEME = "darkTheme";

    UxKitModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return FRAGMENT_TAG;
    }

    private class TimePickerDialogListener implements TimePickerDialog.OnTimeSetListener, DialogInterface.OnCancelListener {
        private final Promise mPromise;
        private boolean mPromiseResolved = false;

        TimePickerDialogListener(Promise promise) {
            mPromise = promise;
        }

        @Override
        public void onTimeSet(TimePickerDialog view, int hour, int minute, int second) {
            if (!mPromiseResolved && getReactApplicationContext().hasActiveCatalystInstance()) {
                WritableMap result = new WritableNativeMap();
                result.putString("action", ACTION_SET);
                result.putInt(ARG_HOUR, hour);
                result.putInt(ARG_MINUTE, minute);
                mPromise.resolve(result);
                mPromiseResolved = true;
            }
        }

        @Override
        public void onCancel(DialogInterface dialogInterface) {
            if (!mPromiseResolved && getReactApplicationContext().hasActiveCatalystInstance()) {
                WritableMap result = new WritableNativeMap();
                result.putString("action", ACTION_CANCEL);
                mPromise.resolve(result);
                mPromiseResolved = true;
            }
        }
    }

    @ReactMethod
    public void open(@Nullable final ReadableMap options, Promise promise) {
        Activity activity = getCurrentActivity();

        if (activity == null) {
            promise.reject(
                    ERROR_NO_ACTIVITY,
                    "Tried to open a duration picker but there is no Activity");
            return;
        }

        int hour = 0;
        int minute = 0;
        int minuteInterval = 1;
        int hourInterval = 1;
        int maxHour = 23;
        int maxMinute = 60;
        String title = null;
        String color = "#009688";
        String cancelText = "cancel";
        Boolean darkTheme = false;

        if (options != null) {
            if (options.hasKey(ARG_HOUR) && !options.isNull(ARG_HOUR))
                hour = options.getInt(ARG_HOUR);
            if (options.hasKey(ARG_MINUTE) && !options.isNull(ARG_MINUTE))
                minute = options.getInt(ARG_MINUTE);
            if (options.hasKey(ARG_MINUTE_INTERVAL) && !options.isNull(ARG_MINUTE_INTERVAL))
                minuteInterval = options.getInt(ARG_MINUTE_INTERVAL);
            if (options.hasKey(ARG_HOUR_INTERVAL) && !options.isNull(ARG_HOUR_INTERVAL))
                hourInterval = options.getInt(ARG_HOUR_INTERVAL);
            if (options.hasKey(ARG_TITLE) && !options.isNull(ARG_TITLE))
                title = options.getString(ARG_TITLE);
            if (options.hasKey(ARG_COLOR) && !options.isNull(ARG_COLOR))
                color = options.getString(ARG_COLOR);
            if (options.hasKey(ARG_MAX_HOUR) && !options.isNull(ARG_MAX_HOUR))
                maxHour = options.getInt(ARG_MAX_HOUR);
            if (options.hasKey(ARG_MAX_MINUTE) && !options.isNull(ARG_MAX_MINUTE))
                maxMinute = options.getInt(ARG_MAX_MINUTE);
            if (options.hasKey(ARG_CANCEL_TEXT) && !options.isNull(ARG_CANCEL_TEXT))
                cancelText = options.getString(ARG_CANCEL_TEXT);
            if (options.hasKey(ARG_DARK_THEME) && !options.isNull(ARG_DARK_THEME))
                darkTheme = options.getBoolean(ARG_DARK_THEME);
        }

        TimePickerDialogListener listener = new TimePickerDialogListener(promise);
        TimePickerDialog tpd = TimePickerDialog.newInstance(listener, hour, minute, true);
        tpd.setAccentColor(Color.parseColor(color));
        tpd.setTitle(title);
        tpd.enableSeconds(false);
        tpd.setMinTime(0, minuteInterval, 0);
        tpd.setTimeInterval(hourInterval, minuteInterval, 60);
        tpd.show(activity.getFragmentManager(), FRAGMENT_TAG);
        tpd.setMaxTime(maxHour, maxMinute, 0);
        tpd.setCancelText(cancelText);
        tpd.setThemeDark(darkTheme);
        
    }
}
