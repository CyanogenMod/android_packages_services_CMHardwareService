package org.cyanogenmod.cmhardware.service;
/*
 * Copyright (C) 2016 The CyanogenMod Project
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


import org.cyanogenmod.hardware.AdaptiveBacklight;
import org.cyanogenmod.hardware.AutoContrast;
import org.cyanogenmod.hardware.ColorBalance;
import org.cyanogenmod.hardware.ColorEnhancement;
import org.cyanogenmod.hardware.DisplayColorCalibration;
import org.cyanogenmod.hardware.DisplayGammaCalibration;
import org.cyanogenmod.hardware.DisplayModeControl;
import org.cyanogenmod.hardware.HighTouchSensitivity;
import org.cyanogenmod.hardware.KeyDisabler;
import org.cyanogenmod.hardware.LongTermOrbits;
import org.cyanogenmod.hardware.PersistentStorage;
import org.cyanogenmod.hardware.SerialNumber;
import org.cyanogenmod.hardware.SunlightEnhancement;
import org.cyanogenmod.hardware.TapToWake;
import org.cyanogenmod.hardware.ThermalMonitor;
import org.cyanogenmod.hardware.TouchscreenHovering;
import org.cyanogenmod.hardware.UniqueDeviceId;
import org.cyanogenmod.hardware.VibratorHW;
import org.cyanogenmod.internal.hardware.CMHardwareInterface;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.io.File;
import java.util.Arrays;

import cyanogenmod.hardware.CMHardwareManager;
import cyanogenmod.hardware.DisplayMode;
import cyanogenmod.hardware.ICMHardwareService;

public class CMHardwareService extends Service {

    private static final String TAG = "CMHardwareService";
    private static final boolean DEBUG = Log.isLoggable(TAG, Log.DEBUG);

    private CMHardwareInterface mCmHwImpl;

    @Override
    public void onCreate() {
        super.onCreate();
        mCmHwImpl = getImpl(this);
    }

    private final IBinder mBinder = new ICMHardwareService.Stub() {

        private boolean isSupported(int feature) {
            return (getSupportedFeatures() & feature) == feature;
        }

        @Override
        public int getSupportedFeatures() {
            return mCmHwImpl.getSupportedFeatures();
        }

        @Override
        public boolean get(int feature) {
            if (!isSupported(feature)) {
                Log.e(TAG, "feature " + feature + " is not supported");
                return false;
            }
            return mCmHwImpl.get(feature);
        }

        @Override
        public boolean set(int feature, boolean enable) {
            if (!isSupported(feature)) {
                Log.e(TAG, "feature " + feature + " is not supported");
                return false;
            }
            return mCmHwImpl.set(feature, enable);
        }

        @Override
        public int[] getDisplayColorCalibration() {
            if (!isSupported(CMHardwareManager.FEATURE_DISPLAY_COLOR_CALIBRATION)) {
                Log.e(TAG, "Display color calibration is not supported");
                return null;
            }
            return mCmHwImpl.getDisplayColorCalibration();
        }

        @Override
        public boolean setDisplayColorCalibration(int[] rgb) {
            if (!isSupported(CMHardwareManager.FEATURE_DISPLAY_COLOR_CALIBRATION)) {
                Log.e(TAG, "Display color calibration is not supported");
                return false;
            }
            if (rgb.length < 3) {
                Log.e(TAG, "Invalid color calibration");
                return false;
            }
            return mCmHwImpl.setDisplayColorCalibration(rgb);
        }

        @Override
        public int getNumGammaControls() {
            if (!isSupported(CMHardwareManager.FEATURE_DISPLAY_GAMMA_CALIBRATION)) {
                Log.e(TAG, "Display gamma calibration is not supported");
                return 0;
            }
            return mCmHwImpl.getNumGammaControls();
        }

        @Override
        public int[] getDisplayGammaCalibration(int idx) {
            if (!isSupported(CMHardwareManager.FEATURE_DISPLAY_GAMMA_CALIBRATION)) {
                Log.e(TAG, "Display gamma calibration is not supported");
                return null;
            }
            return mCmHwImpl.getDisplayGammaCalibration(idx);
        }

        @Override
        public boolean setDisplayGammaCalibration(int idx, int[] rgb) {
            if (!isSupported(CMHardwareManager.FEATURE_DISPLAY_GAMMA_CALIBRATION)) {
                Log.e(TAG, "Display gamma calibration is not supported");
                return false;
            }
            return mCmHwImpl.setDisplayGammaCalibration(idx, rgb);
        }

        @Override
        public int[] getVibratorIntensity() {
            if (!isSupported(CMHardwareManager.FEATURE_VIBRATOR)) {
                Log.e(TAG, "Vibrator is not supported");
                return null;
            }
            return mCmHwImpl.getVibratorIntensity();
        }

        @Override
        public boolean setVibratorIntensity(int intensity) {
            if (!isSupported(CMHardwareManager.FEATURE_VIBRATOR)) {
                Log.e(TAG, "Vibrator is not supported");
                return false;
            }
            return mCmHwImpl.setVibratorIntensity(intensity);
        }

        @Override
        public String getLtoSource() {
            if (!isSupported(CMHardwareManager.FEATURE_LONG_TERM_ORBITS)) {
                Log.e(TAG, "Long term orbits is not supported");
                return null;
            }
            return mCmHwImpl.getLtoSource();
        }

        @Override
        public String getLtoDestination() {
            if (!isSupported(CMHardwareManager.FEATURE_LONG_TERM_ORBITS)) {
                Log.e(TAG, "Long term orbits is not supported");
                return null;
            }
            return mCmHwImpl.getLtoDestination();
        }

        @Override
        public long getLtoDownloadInterval() {
            if (!isSupported(CMHardwareManager.FEATURE_LONG_TERM_ORBITS)) {
                Log.e(TAG, "Long term orbits is not supported");
                return 0;
            }
            return mCmHwImpl.getLtoDownloadInterval();
        }

        @Override
        public String getSerialNumber() {
            if (!isSupported(CMHardwareManager.FEATURE_SERIAL_NUMBER)) {
                Log.e(TAG, "Serial number is not supported");
                return null;
            }
            return mCmHwImpl.getSerialNumber();
        }

        @Override
        public String getUniqueDeviceId() {
            if (!isSupported(CMHardwareManager.FEATURE_UNIQUE_DEVICE_ID)) {
                Log.e(TAG, "Unique device ID is not supported");
                return null;
            }
            return mCmHwImpl.getUniqueDeviceId();
        }

        @Override
        public boolean requireAdaptiveBacklightForSunlightEnhancement() {
            if (!isSupported(CMHardwareManager.FEATURE_SUNLIGHT_ENHANCEMENT)) {
                Log.e(TAG, "Sunlight enhancement is not supported");
                return false;
            }
            return mCmHwImpl.requireAdaptiveBacklightForSunlightEnhancement();
        }

        @Override
        public boolean isSunlightEnhancementSelfManaged() {
            if (!isSupported(CMHardwareManager.FEATURE_SUNLIGHT_ENHANCEMENT)) {
                Log.e(TAG, "Sunlight enhancement is not supported");
                return false;
            }
            return mCmHwImpl.isSunlightEnhancementSelfManaged();
        }

        @Override
        public DisplayMode[] getDisplayModes() {
            if (!isSupported(CMHardwareManager.FEATURE_DISPLAY_MODES)) {
                Log.e(TAG, "Display modes are not supported");
                return null;
            }
            return mCmHwImpl.getDisplayModes();
        }

        @Override
        public DisplayMode getCurrentDisplayMode() {
            if (!isSupported(CMHardwareManager.FEATURE_DISPLAY_MODES)) {
                Log.e(TAG, "Display modes are not supported");
                return null;
            }
            return mCmHwImpl.getCurrentDisplayMode();
        }

        @Override
        public DisplayMode getDefaultDisplayMode() {
            if (!isSupported(CMHardwareManager.FEATURE_DISPLAY_MODES)) {
                Log.e(TAG, "Display modes are not supported");
                return null;
            }
            return mCmHwImpl.getDefaultDisplayMode();
        }

        @Override
        public boolean setDisplayMode(DisplayMode mode, boolean makeDefault) {
            if (!isSupported(CMHardwareManager.FEATURE_DISPLAY_MODES)) {
                Log.e(TAG, "Display modes are not supported");
                return false;
            }
            return mCmHwImpl.setDisplayMode(mode, makeDefault);
        }

        @Override
        public boolean writePersistentBytes(String key, byte[] value) {
            if (key == null || key.length() == 0 || key.length() > 64) {
                Log.e(TAG, "Invalid key: " + key);
                return false;
            }
            // A null value is delete
            if (value != null && (value.length > 4096 || value.length == 0)) {
                Log.e(TAG, "Invalid value: " + (value != null ? Arrays.toString(value) : null));
                return false;
            }
            if (!isSupported(CMHardwareManager.FEATURE_PERSISTENT_STORAGE)) {
                Log.e(TAG, "Persistent storage is not supported");
                return false;
            }
            return mCmHwImpl.writePersistentBytes(key, value);
        }

        @Override
        public byte[] readPersistentBytes(String key) {
            if (key == null || key.length() == 0 || key.length() > 64) {
                Log.e(TAG, "Invalid key: " + key);
                return null;
            }
            if (!isSupported(CMHardwareManager.FEATURE_PERSISTENT_STORAGE)) {
                Log.e(TAG, "Persistent storage is not supported");
                return null;
            }
            return mCmHwImpl.readPersistentBytes(key);
        }


        @Override
        public int getColorBalanceMin() {
            if (isSupported(CMHardwareManager.FEATURE_COLOR_BALANCE)) {
                return mCmHwImpl.getColorBalanceMin();
            }
            return 0;
        }

        @Override
        public int getColorBalanceMax() {
            if (isSupported(CMHardwareManager.FEATURE_COLOR_BALANCE)) {
                return mCmHwImpl.getColorBalanceMax();
            }
            return 0;
        }

        @Override
        public int getColorBalance() {
            if (isSupported(CMHardwareManager.FEATURE_COLOR_BALANCE)) {
                return mCmHwImpl.getColorBalance();
            }
            return 0;
        }

        @Override
        public boolean setColorBalance(int value) {
            if (isSupported(CMHardwareManager.FEATURE_COLOR_BALANCE)) {
                return mCmHwImpl.setColorBalance(value);
            }
            return false;
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private class LegacyCMHardware implements CMHardwareInterface {

        private int mSupportedFeatures = 0;

        public LegacyCMHardware() {
            if (AdaptiveBacklight.isSupported())
                mSupportedFeatures |= CMHardwareManager.FEATURE_ADAPTIVE_BACKLIGHT;
            if (ColorEnhancement.isSupported())
                mSupportedFeatures |= CMHardwareManager.FEATURE_COLOR_ENHANCEMENT;
            if (DisplayColorCalibration.isSupported())
                mSupportedFeatures |= CMHardwareManager.FEATURE_DISPLAY_COLOR_CALIBRATION;
            if (DisplayGammaCalibration.isSupported())
                mSupportedFeatures |= CMHardwareManager.FEATURE_DISPLAY_GAMMA_CALIBRATION;
            if (HighTouchSensitivity.isSupported())
                mSupportedFeatures |= CMHardwareManager.FEATURE_HIGH_TOUCH_SENSITIVITY;
            if (KeyDisabler.isSupported())
                mSupportedFeatures |= CMHardwareManager.FEATURE_KEY_DISABLE;
            if (LongTermOrbits.isSupported())
                mSupportedFeatures |= CMHardwareManager.FEATURE_LONG_TERM_ORBITS;
            if (SerialNumber.isSupported())
                mSupportedFeatures |= CMHardwareManager.FEATURE_SERIAL_NUMBER;
            if (SunlightEnhancement.isSupported())
                mSupportedFeatures |= CMHardwareManager.FEATURE_SUNLIGHT_ENHANCEMENT;
            if (TapToWake.isSupported())
                mSupportedFeatures |= CMHardwareManager.FEATURE_TAP_TO_WAKE;
            if (VibratorHW.isSupported())
                mSupportedFeatures |= CMHardwareManager.FEATURE_VIBRATOR;
            if (TouchscreenHovering.isSupported())
                mSupportedFeatures |= CMHardwareManager.FEATURE_TOUCH_HOVERING;
            if (AutoContrast.isSupported())
                mSupportedFeatures |= CMHardwareManager.FEATURE_AUTO_CONTRAST;
            if (DisplayModeControl.isSupported())
                mSupportedFeatures |= CMHardwareManager.FEATURE_DISPLAY_MODES;
            if (PersistentStorage.isSupported())
                mSupportedFeatures |= CMHardwareManager.FEATURE_PERSISTENT_STORAGE;
            if (ThermalMonitor.isSupported())
                mSupportedFeatures |= CMHardwareManager.FEATURE_THERMAL_MONITOR;
            if (UniqueDeviceId.isSupported())
                mSupportedFeatures |= CMHardwareManager.FEATURE_UNIQUE_DEVICE_ID;
            if (ColorBalance.isSupported())
                mSupportedFeatures |= CMHardwareManager.FEATURE_COLOR_BALANCE;
        }

        public int getSupportedFeatures() {
            return mSupportedFeatures;
        }

        public boolean get(int feature) {
            switch(feature) {
                case CMHardwareManager.FEATURE_ADAPTIVE_BACKLIGHT:
                    return AdaptiveBacklight.isEnabled();
                case CMHardwareManager.FEATURE_COLOR_ENHANCEMENT:
                    return ColorEnhancement.isEnabled();
                case CMHardwareManager.FEATURE_HIGH_TOUCH_SENSITIVITY:
                    return HighTouchSensitivity.isEnabled();
                case CMHardwareManager.FEATURE_KEY_DISABLE:
                    return KeyDisabler.isActive();
                case CMHardwareManager.FEATURE_SUNLIGHT_ENHANCEMENT:
                    return SunlightEnhancement.isEnabled();
                case CMHardwareManager.FEATURE_TAP_TO_WAKE:
                    return TapToWake.isEnabled();
                case CMHardwareManager.FEATURE_TOUCH_HOVERING:
                    return TouchscreenHovering.isEnabled();
                case CMHardwareManager.FEATURE_AUTO_CONTRAST:
                    return AutoContrast.isEnabled();
                case CMHardwareManager.FEATURE_THERMAL_MONITOR:
                    return ThermalMonitor.isEnabled();
                default:
                    Log.e(TAG, "feature " + feature + " is not a boolean feature");
                    return false;
            }
        }

        public boolean set(int feature, boolean enable) {
            switch(feature) {
                case CMHardwareManager.FEATURE_ADAPTIVE_BACKLIGHT:
                    return AdaptiveBacklight.setEnabled(enable);
                case CMHardwareManager.FEATURE_COLOR_ENHANCEMENT:
                    return ColorEnhancement.setEnabled(enable);
                case CMHardwareManager.FEATURE_HIGH_TOUCH_SENSITIVITY:
                    return HighTouchSensitivity.setEnabled(enable);
                case CMHardwareManager.FEATURE_KEY_DISABLE:
                    return KeyDisabler.setActive(enable);
                case CMHardwareManager.FEATURE_SUNLIGHT_ENHANCEMENT:
                    return SunlightEnhancement.setEnabled(enable);
                case CMHardwareManager.FEATURE_TAP_TO_WAKE:
                    return TapToWake.setEnabled(enable);
                case CMHardwareManager.FEATURE_TOUCH_HOVERING:
                    return TouchscreenHovering.setEnabled(enable);
                case CMHardwareManager.FEATURE_AUTO_CONTRAST:
                    return AutoContrast.setEnabled(enable);
                default:
                    Log.e(TAG, "feature " + feature + " is not a boolean feature");
                    return false;
            }
        }

        private int[] splitStringToInt(String input, String delimiter) {
            if (input == null || delimiter == null) {
                return null;
            }
            String strArray[] = input.split(delimiter);
            try {
                int intArray[] = new int[strArray.length];
                for(int i = 0; i < strArray.length; i++) {
                    intArray[i] = Integer.parseInt(strArray[i]);
                }
                return intArray;
            } catch (NumberFormatException e) {
                /* ignore */
            }
            return null;
        }

        private String rgbToString(int[] rgb) {
            StringBuilder builder = new StringBuilder();
            builder.append(rgb[CMHardwareManager.COLOR_CALIBRATION_RED_INDEX]);
            builder.append(" ");
            builder.append(rgb[CMHardwareManager.COLOR_CALIBRATION_GREEN_INDEX]);
            builder.append(" ");
            builder.append(rgb[CMHardwareManager.COLOR_CALIBRATION_BLUE_INDEX]);
            return builder.toString();
        }

        public int[] getDisplayColorCalibration() {
            int[] rgb = splitStringToInt(DisplayColorCalibration.getCurColors(), " ");
            if (rgb == null || rgb.length != 3) {
                Log.e(TAG, "Invalid color calibration string");
                return null;
            }
            int[] currentCalibration = new int[6];
            currentCalibration[CMHardwareManager.COLOR_CALIBRATION_RED_INDEX] = rgb[0];
            currentCalibration[CMHardwareManager.COLOR_CALIBRATION_GREEN_INDEX] = rgb[1];
            currentCalibration[CMHardwareManager.COLOR_CALIBRATION_BLUE_INDEX] = rgb[2];
            currentCalibration[CMHardwareManager.COLOR_CALIBRATION_DEFAULT_INDEX] =
                    DisplayColorCalibration.getDefValue();
            currentCalibration[CMHardwareManager.COLOR_CALIBRATION_MIN_INDEX] =
                    DisplayColorCalibration.getMinValue();
            currentCalibration[CMHardwareManager.COLOR_CALIBRATION_MAX_INDEX] =
                    DisplayColorCalibration.getMaxValue();
            return currentCalibration;
        }

        public boolean setDisplayColorCalibration(int[] rgb) {
            return DisplayColorCalibration.setColors(rgbToString(rgb));
        }

        public int getNumGammaControls() {
            return DisplayGammaCalibration.getNumberOfControls();
        }

        public int[] getDisplayGammaCalibration(int idx) {
            int[] rgb = splitStringToInt(DisplayGammaCalibration.getCurGamma(idx), " ");
            if (rgb == null || rgb.length != 3) {
                Log.e(TAG, "Invalid gamma calibration string");
                return null;
            }
            int[] currentCalibration = new int[5];
            currentCalibration[CMHardwareManager.GAMMA_CALIBRATION_RED_INDEX] = rgb[0];
            currentCalibration[CMHardwareManager.GAMMA_CALIBRATION_GREEN_INDEX] = rgb[1];
            currentCalibration[CMHardwareManager.GAMMA_CALIBRATION_BLUE_INDEX] = rgb[2];
            currentCalibration[CMHardwareManager.GAMMA_CALIBRATION_MIN_INDEX] =
                    DisplayGammaCalibration.getMinValue(idx);
            currentCalibration[CMHardwareManager.GAMMA_CALIBRATION_MAX_INDEX] =
                    DisplayGammaCalibration.getMaxValue(idx);
            return currentCalibration;
        }

        public boolean setDisplayGammaCalibration(int idx, int[] rgb) {
            return DisplayGammaCalibration.setGamma(idx, rgbToString(rgb));
        }

        public int[] getVibratorIntensity() {
            int[] vibrator = new int[5];
            vibrator[CMHardwareManager.VIBRATOR_INTENSITY_INDEX] = VibratorHW.getCurIntensity();
            vibrator[CMHardwareManager.VIBRATOR_DEFAULT_INDEX] = VibratorHW.getDefaultIntensity();
            vibrator[CMHardwareManager.VIBRATOR_MIN_INDEX] = VibratorHW.getMinIntensity();
            vibrator[CMHardwareManager.VIBRATOR_MAX_INDEX] = VibratorHW.getMaxIntensity();
            vibrator[CMHardwareManager.VIBRATOR_WARNING_INDEX] = VibratorHW.getWarningThreshold();
            return vibrator;
        }

        public boolean setVibratorIntensity(int intensity) {
            return VibratorHW.setIntensity(intensity);
        }

        public String getLtoSource() {
            return LongTermOrbits.getSourceLocation();
        }

        public String getLtoDestination() {
            File file = LongTermOrbits.getDestinationLocation();
            return file.getAbsolutePath();
        }

        public long getLtoDownloadInterval() {
            return LongTermOrbits.getDownloadInterval();
        }

        public String getSerialNumber() {
            return SerialNumber.getSerialNumber();
        }

        public String getUniqueDeviceId() {
            return UniqueDeviceId.getUniqueDeviceId();
        }

        public boolean requireAdaptiveBacklightForSunlightEnhancement() {
            return SunlightEnhancement.isAdaptiveBacklightRequired();
        }

        public boolean isSunlightEnhancementSelfManaged() {
            return SunlightEnhancement.isSelfManaged();
        }

        public DisplayMode[] getDisplayModes() {
            return DisplayModeControl.getAvailableModes();
        }

        public DisplayMode getCurrentDisplayMode() {
            return DisplayModeControl.getCurrentMode();
        }

        public DisplayMode getDefaultDisplayMode() {
            return DisplayModeControl.getDefaultMode();
        }

        public boolean setDisplayMode(DisplayMode mode, boolean makeDefault) {
            return DisplayModeControl.setMode(mode, makeDefault);
        }

        public boolean writePersistentBytes(String key, byte[] value) {
            return PersistentStorage.set(key, value);
        }

        public byte[] readPersistentBytes(String key) {
            return PersistentStorage.get(key);
        }

        public int getColorBalanceMin() {
            return ColorBalance.getMinValue();
        }

        public int getColorBalanceMax() {
            return ColorBalance.getMaxValue();
        }

        public int getColorBalance() {
            return ColorBalance.getValue();
        }

        public boolean setColorBalance(int value) {
            return ColorBalance.setValue(value);
        }
    }

    private CMHardwareInterface getImpl(Context context) {
        return new LegacyCMHardware();
    }
}
