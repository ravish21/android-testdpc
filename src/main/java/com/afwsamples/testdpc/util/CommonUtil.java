package com.afwsamples.testdpc.util;

import android.Manifest;
import android.accounts.Account;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.WallpaperManager;
import android.app.admin.DevicePolicyManager;
import android.app.admin.FactoryResetProtectionPolicy;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.os.UserManager;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.telephony.SmsManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;
//import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static android.app.admin.DevicePolicyManager.PERMISSION_GRANT_STATE_GRANTED;
import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.NOTIFICATION_SERVICE;
import static android.os.UserManager.DISALLOW_ADD_MANAGED_PROFILE;
import static android.os.UserManager.DISALLOW_ADD_USER;
import static android.os.UserManager.DISALLOW_DEBUGGING_FEATURES;
import static android.os.UserManager.DISALLOW_FACTORY_RESET;
import static android.os.UserManager.DISALLOW_MOUNT_PHYSICAL_MEDIA;
import static android.os.UserManager.DISALLOW_SAFE_BOOT;
import static android.os.UserManager.DISALLOW_SET_WALLPAPER;
import static android.os.UserManager.DISALLOW_USB_FILE_TRANSFER;

import static com.afwsamples.testdpc.common.PackageInstallationUtils.ACTION_INSTALL_COMPLETE;
//import static com.afwsamples.testdpc.receiver.SimChangedReceiver.checkSIMOffline;

import org.json.JSONObject;

//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;

public class CommonUtil  {
    private Context context;
    public CommonUtil(Context context) {
        this.context = context;
    }

    @SuppressLint("HardwareIds")
    public String getUniqueIMEIId(Context context) {
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (telephonyManager != null) {
//                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
//                    return "";
//                }
                String imei1 = "", imei2 = "";
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                    try {
                        imei1 = telephonyManager.getImei(0);
                        imei2 = telephonyManager.getImei(1);
                    } catch (Exception e) {
                        try {
                            imei1 = telephonyManager.getDeviceId(0);
                            imei2 = telephonyManager.getDeviceId(1);
                        } catch (Exception e2) {
                            imei1 = "";
                            imei2 = "";
                        }
                    }
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    imei1 = telephonyManager.getImei(0);
                    imei2 = telephonyManager.getImei(1);
                } else {
                    imei1 = telephonyManager.getDeviceId(0);
                    imei2 = telephonyManager.getDeviceId(1);
                }
                if (imei1 != null && !imei1.isEmpty()) {
                    SharedPrefs.setStringValue(Constant.IMEI, imei1, context);
                    SharedPrefs.setStringValue(Constant.IMEI_2, imei2, context);
                    return imei1;
                } else {
                    return "";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void hideOrShowAppIconContact(Context context, boolean isHide, boolean isNewIcon) {
        PackageManager pm = context.getPackageManager();
        if (isHide) {
            pm.setComponentEnabledSetting(
                    new ComponentName(context,
                            "com.yogicastrafive.SettingActivityAlias1"),
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP);

            pm.setComponentEnabledSetting(
                    new ComponentName(context,
                            "com.yogicastrafive.SettingActivityAlias"),
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP);
        } else {
            if (isNewIcon) {
                pm.setComponentEnabledSetting(
                        new ComponentName(context,
                                "com.yogicastrafive.SettingActivityAlias1"),
                        PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                        PackageManager.DONT_KILL_APP);

                pm.setComponentEnabledSetting(
                        new ComponentName(context,
                                "com.yogicastrafive.SettingActivityAlias"),
                        PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                        PackageManager.DONT_KILL_APP);
            } else {
                pm.setComponentEnabledSetting(
                        new ComponentName(context,
                                "com.yogicastrafive.SettingActivityAlias1"),
                        PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                        PackageManager.DONT_KILL_APP);

                pm.setComponentEnabledSetting(
                        new ComponentName(context,
                                "com.yogicastrafive.SettingActivityAlias"),
                        PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                        PackageManager.DONT_KILL_APP);
            }
        }

    }

    public static void hideOrShowAppIcon(Context context, boolean isHide, boolean isFinish) {
       /* try {
            PackageManager p = context.getPackageManager();
            ComponentName componentName = new ComponentName(context, RegistrationActivity.class);
            if (isHide) {
                p.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            } else {
                p.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
            }
            if (isFinish) {
                ((Activity) context).finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    public boolean isBatteryOptimizationDisabled(Context context) {
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if (Build.VERSION.SDK_INT >= 26) {
            assert powerManager != null;
            return powerManager.isIgnoringBatteryOptimizations(context.getPackageName());
        } else {
            return true;
        }
    }

    private static final String KIOSK_PREFERENCE_FILE = "kiosk_preference_file";
    private static final String[] KIOSK_USER_RESTRICTIONS = {
            DISALLOW_SAFE_BOOT,
            DISALLOW_FACTORY_RESET,
            DISALLOW_ADD_USER,
            DISALLOW_MOUNT_PHYSICAL_MEDIA,
            DISALLOW_USB_FILE_TRANSFER,
            DISALLOW_DEBUGGING_FEATURES
    };
    private ComponentName mAdminComponentName;
    private DevicePolicyManager mDevicePolicyManager;

    public void grantPermission(DevicePolicyManager devicePolicyManager, ComponentName adminComponent) {
        try {
            String[] permissions = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_PERMISSIONS).requestedPermissions;
            for (String permission : permissions) {
                try {
                    boolean success = devicePolicyManager.setPermissionGrantState(adminComponent,
                            context.getPackageName(), permission, PERMISSION_GRANT_STATE_GRANTED);
                    if (!success) {
                        Log.e("GMAIL", "Failed to auto grant permission to self: " + permission);
                    } else {
                        Log.e("GMAIL", "Success to auto grant permission to self: " + permission);
                    }
                } catch (Exception e) {
                    Log.e("GMAIL", "Failed to auto grant permission to self: " + e.getMessage());
                }

                if (permission.equals("android.permission.ACCESS_BACKGROUND_LOCATION")) {
                    break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setDefaultKioskPolicies(boolean active, Context context) {
      /*  try {
            mAdminComponentName = DeviceAdminReceiver.getComponentName(context);
            mDevicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
            if (mDevicePolicyManager.isDeviceOwnerApp(context.getPackageName())) {
                // restore or save previous configuration
                if (active) {
                    saveCurrentConfiguration();
                    setUserRestriction(DISALLOW_SAFE_BOOT, active);
                    setUserRestriction(DISALLOW_FACTORY_RESET, active);
                    setUserRestriction(DISALLOW_ADD_USER, active);
                    setUserRestriction(DISALLOW_MOUNT_PHYSICAL_MEDIA, active);
                    setUserRestriction(DISALLOW_USB_FILE_TRANSFER, active);
                    setUserRestriction(DISALLOW_DEBUGGING_FEATURES, active);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        setUserRestriction(DISALLOW_ADD_MANAGED_PROFILE, active);
                    }
                    //Enable System App
                    EnableSystemApps();
                    //Google Account Verification After Factory Reset
                    setGoogleAccountVerification(false, context);
                    mDevicePolicyManager.setUninstallBlocked(mAdminComponentName, context.getPackageName(), true);
                } else {
                    restorePreviousConfiguration(context);
                    mDevicePolicyManager.setUninstallBlocked(mAdminComponentName, context.getPackageName(), false);
                }

                // enable STAY_ON_WHILE_PLUGGED_IN
                enableStayOnWhilePluggedIn(active);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    private final String key = "disableFactoryResetProtectionAdmin";
    private final String key1 = "factoryResetProtectionAdmin";
    // GIVEN an app which the restriction is set to
    private final String app = "com.google.android.gms";

    public void setGoogleAccountVerification(boolean active, Context context) {
        try {
            new GetAccountIDTask(null, active, context).execute();
        } catch (Exception e) {
            Log.e("GMAIL", "WRONG");
        }
    }

    public void getLauncherPackage() {
        try {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            ResolveInfo resolveInfo = context.getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
            String currentLauncherPackageName = null, currentLauncherActivity = null;
            try {
                currentLauncherPackageName = resolveInfo.activityInfo.packageName;
                //currentLauncherActivity = resolveInfo.activityInfo.name;
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.e("Launcher 1 pkg", currentLauncherPackageName);
            //Log.e("Launcher 1 act", currentLauncherActivity);
            if (currentLauncherPackageName != null) {
                if (!currentLauncherPackageName.toLowerCase().equals(context.getPackageName().toLowerCase())) {
                    saveDefaultLauncher(currentLauncherPackageName);
                }
            } else {
                final PackageManager packageManager = context.getPackageManager();
                for (final ResolveInfo resolveInfo1 : packageManager.queryIntentActivities(
                        new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME), PackageManager.MATCH_DEFAULT_ONLY)) {
                    currentLauncherPackageName = resolveInfo1.activityInfo.packageName;
                    if (currentLauncherPackageName != null) {
                        if (!currentLauncherPackageName.toLowerCase().equals(context.getPackageName().toLowerCase()))  //if this activity is not in our activity (in other words, it's another default home screen)
                        {
                            try {
                                //currentLauncherActivity = resolveInfo.activityInfo.name;
                                Log.e("Launcher 2 pkg", currentLauncherPackageName);
                                //Log.e("Launcher 2 act", currentLauncherActivity);
                                saveDefaultLauncher(currentLauncherPackageName);
                                break;
                            } catch (Exception e) {
                                Log.e("Launcher Error 2", e.getMessage());
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveDefaultLauncher(String currentLauncherName) {
        SharedPrefs.setStringValue(Constant.DEFAULT_LAUNCHER_PACKAGE_NAME, currentLauncherName, context);
        //SharedPrefs.setStringValue(Constant.DEFAULT_LAUNCHER_ACTIVITY_NAME, currentLauncherName, context);
    }

    public void setLauncherPackage() {
        String LauncherPackageName = SharedPrefs.getStringValue(Constant.DEFAULT_LAUNCHER_PACKAGE_NAME, context);
        //String LauncherActivityName = SharedPrefs.getStringValue(Constant.DEFAULT_LAUNCHER_ACTIVITY_NAME, context);
        if (!LauncherPackageName.isEmpty()) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.setPackage(LauncherPackageName);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            try {
                context.startActivity(intent);
                Log.e("Launcher 3 pkg", LauncherPackageName);
                //Log.e("Launcher 3 act", LauncherActivityName);
            } catch (Exception e) {
                Log.e("Launcher Error 3", e.getMessage());
            }
        } else {
            Intent intent = null;
            final PackageManager packageManager = context.getPackageManager();
            for (final ResolveInfo resolveInfo : packageManager.queryIntentActivities(
                    new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME)
                    , PackageManager.MATCH_DEFAULT_ONLY)) {
                if (!context.getPackageName().equals(resolveInfo.activityInfo.packageName))  //if this activity is not in our activity (in other words, it's another default home screen)
                {
                    try {
                        intent = packageManager.getLaunchIntentForPackage(resolveInfo.activityInfo.packageName);
                        Log.e("Launcher 4 pkg", resolveInfo.activityInfo.packageName);
                        //Log.e("Launcher 4 act", resolveInfo.activityInfo.name);
                        if (intent != null) {
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                            break;
                        }
                    } catch (Exception e) {
                        Log.e("Launcher Error 4", e.getMessage());
                    }
                }
            }
        }
    }

    private class GetAccountIDTask extends AsyncTask<String, String, String> {
        private boolean active;
        private Context context;

        public GetAccountIDTask(Account[] accounts, boolean active, Context context) {
            this.active = active;
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                String accountId = Constant.GOOGLE_ACCOUNT_1;
                String accountEmail = Constant.GOOGLE_ACCOUNT;
                SharedPrefs.setStringValue(Constant.IT_ADMIN_EMAIL, accountEmail, context);
                try {
                    List<String> acc = new ArrayList<>();
                    acc.add(accountId);

                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R) {
                        if (!active) {
                            mDevicePolicyManager.setFactoryResetProtectionPolicy(mAdminComponentName,
                                    new FactoryResetProtectionPolicy.Builder()
                                            .setFactoryResetProtectionAccounts(acc)
                                            .setFactoryResetProtectionEnabled(true)
                                            .build());
                            FactoryResetProtectionPolicy frp = mDevicePolicyManager.getFactoryResetProtectionPolicy(mAdminComponentName);
                            List<String> account = frp.getFactoryResetProtectionAccounts();
                            if (account.size() <= 0) {
                                loadFRP(active, accountId);
                            }
                        } else {
                            mDevicePolicyManager.setFactoryResetProtectionPolicy(mAdminComponentName, null);
                        }
                    } else {
                        loadFRP(active, accountId);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //block removal of admin account
                if (!active) {
                    try {
                        hideOrShowAppIcon(context, true, true);

                        hideOrShowAppIconContact(context, false, true);

//                        checkSIMOffline(context, SimAlertType.FETCH);

                        SharedPrefs.setABoolean(context, Constant.HIDING, true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    hideOrShowAppIcon(context, true, false);

                    hideOrShowAppIconContact(context, false, false);

                    SharedPrefs.setABoolean(context, Constant.IsRemove, true);

                    mDevicePolicyManager.clearDeviceOwnerApp(context.getPackageName());
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("GMAIL", "Exception " + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }

    private void loadFRP(boolean active, String accountId) {
        Bundle restrictions = new Bundle();
        restrictions.putString(key1, accountId);
        restrictions.putString(key1, Constant.GOOGLE_ACCOUNT_1);
        restrictions.putBoolean(key, active);
        try {
            mDevicePolicyManager.setApplicationRestrictions(mAdminComponentName, app, restrictions);
            Log.e("GMAIL", "setApplicationRestrictions " + key1);
        } catch (Exception e) {
            Log.e("GMAIL", "setApplicationRestrictions " + key1 + " " + e.getMessage());
        }
        try {
            // THEN the restrictions should be set correctly
            Bundle actualRestrictions = mDevicePolicyManager.getApplicationRestrictions(mAdminComponentName, app);
            // send broadcast
            Intent broadcastIntent = new Intent("com.google.android.gms.auth.FRP_CONFIG_CHANGED");
            broadcastIntent.setPackage("com.google.android.gms");
            broadcastIntent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
            context.sendBroadcast(broadcastIntent);
            if (actualRestrictions.getString(key1, "").equals(accountId)) {
                Log.e("GMAIL", "SUCCESS");
            } else {
                Log.e("GMAIL", "FAILURE");
            }
        } catch (Exception e) {
            Log.e("GMAIL", "com.google.android.gms.auth.FRP_CONFIG_CHANGED " + e.getMessage());
        }
    }

    public void setPersistentPreferredActivity(boolean active) {
        /*try {
            mAdminComponentName = DeviceAdminReceiver.getComponentName(context);
            mDevicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
            if (mDevicePolicyManager.isDeviceOwnerApp(context.getPackageName())) {
                mDevicePolicyManager.setStatusBarDisabled(mAdminComponentName, active);

                try {
                    if (active) {
                        getLauncherPackage();

                        EnableHomeLauncherApp();
                    } else {
                        DisableHomeLauncherApp();

                        setLauncherPackage();
                    }
                } catch (Exception e) {
                    Log.e("Launcher 6 Error", e.getMessage());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    public void DisableHomeLauncherApp() {
       /* try {
            PackageManager packageManager = context.getPackageManager();
            ComponentName componentName = new ComponentName(context, KioskActivity.class);
            packageManager.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

            Intent selector = new Intent(Intent.ACTION_MAIN);
            selector.addCategory(Intent.CATEGORY_HOME);
            selector.addCategory(Intent.CATEGORY_DEFAULT);
            selector.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            selector.setComponent(componentName);
            context.startActivity(selector);
            Log.e("Launcher 6", "clear my launcher");

            if (mDevicePolicyManager == null || mAdminComponentName == null) {
                mAdminComponentName = DeviceAdminReceiver.getComponentName(context);
                mDevicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
            }
            mDevicePolicyManager.clearPackagePersistentPreferredActivities(
                    mAdminComponentName, context.getPackageName());
            Log.e("Launcher 6", "clear my launcher");
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    private void EnableHomeLauncherApp() {
        /*try {
            PackageManager packageManager = context.getPackageManager();
            ComponentName componentName = new ComponentName(context, KioskActivity.class);
            packageManager.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);

            Intent selector = new Intent(Intent.ACTION_MAIN);
            selector.addCategory(Intent.CATEGORY_HOME);
            selector.addCategory(Intent.CATEGORY_DEFAULT);
            selector.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            selector.setComponent(componentName);
            context.startActivity(selector);
            Log.e("Launcher 6", "set my launcher");

            IntentFilter intentFilter = new IntentFilter(Intent.ACTION_MAIN);
            intentFilter.addCategory(Intent.CATEGORY_HOME);
            intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
            // set KIOSK activity as home intent receiver so that it is started
            // on reboot
            mDevicePolicyManager.addPersistentPreferredActivity(
                    mAdminComponentName, intentFilter, new ComponentName(
                            context.getPackageName(), KioskActivity.class.getName()));
            Log.e("Launcher 6", "set my launcher");
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    @TargetApi(Build.VERSION_CODES.N)
    private void saveCurrentConfiguration() {
       /* if (Util.SDK_INT >= Build.VERSION_CODES.N) {
            Bundle settingsBundle = mDevicePolicyManager.getUserRestrictions(mAdminComponentName);
            SharedPreferences.Editor editor = context.getSharedPreferences(KIOSK_PREFERENCE_FILE,
                    MODE_PRIVATE).edit();

            for (String userRestriction : KIOSK_USER_RESTRICTIONS) {
                boolean currentSettingValue = settingsBundle.getBoolean(userRestriction);
                editor.putBoolean(userRestriction, currentSettingValue);
            }
            editor.apply();
        }*/
    }

    private void restorePreviousConfiguration(Context context) {
        /*if (Util.SDK_INT >= Build.VERSION_CODES.N) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(KIOSK_PREFERENCE_FILE,
                    MODE_PRIVATE);

            for (String userRestriction : KIOSK_USER_RESTRICTIONS) {
                boolean prevSettingValue = sharedPreferences.getBoolean(userRestriction, false);
                setUserRestriction(userRestriction, prevSettingValue);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                setUserRestriction(DISALLOW_ADD_MANAGED_PROFILE, false);
            }
            //Google Account Verification After Factory Reset
            setGoogleAccountVerification(true, context);
        }*/
    }

    private void setUserRestriction(String restriction, boolean disallow) {
        try {
            if (disallow) {
                mDevicePolicyManager.addUserRestriction(mAdminComponentName, restriction);
            } else {
                mDevicePolicyManager.clearUserRestriction(mAdminComponentName, restriction);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void enableStayOnWhilePluggedIn(boolean enabled) {
        try {
            if (enabled) {
                mDevicePolicyManager.setGlobalSetting(
                        mAdminComponentName,
                        Settings.Global.STAY_ON_WHILE_PLUGGED_IN,
                        Integer.toString(BatteryManager.BATTERY_PLUGGED_AC
                                | BatteryManager.BATTERY_PLUGGED_USB
                                | BatteryManager.BATTERY_PLUGGED_WIRELESS));
            } else {
                mDevicePolicyManager.setGlobalSetting(
                        mAdminComponentName,
                        Settings.Global.STAY_ON_WHILE_PLUGGED_IN,
                        "0"
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            // set this Activity as a lock task package
            mDevicePolicyManager.setLockTaskPackages(mAdminComponentName, enabled ? new String[]{context.getPackageName()} : new String[]{});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void EnableSystemApps() {
        try {
            PackageManager mPackageManager = context.getPackageManager();
            // Disabled system apps list = {All system apps} - {Enabled system apps}
            final List<String> disabledSystemApps = new ArrayList<String>();
            int packageFlags;
            if (Build.VERSION.SDK_INT < 24) {
                //noinspection deprecation
                packageFlags = PackageManager.GET_UNINSTALLED_PACKAGES;
            } else {
                packageFlags = PackageManager.MATCH_UNINSTALLED_PACKAGES;
            }
            // This list contains both enabled and disabled apps.
            List<ApplicationInfo> allApps = mPackageManager.getInstalledApplications(
                    packageFlags);
        /*List<ApplicationInfo> allApps = mPackageManager.getInstalledApplications(
                PackageManager.GET_UNINSTALLED_PACKAGES);*/
            Collections.sort(allApps, new ApplicationInfo.DisplayNameComparator(mPackageManager));
            // This list contains all enabled apps.
            List<ApplicationInfo> enabledApps =
                    mPackageManager.getInstalledApplications(0 /* Default flags */);
            Set<String> enabledAppsPkgNames = new HashSet<String>();
            for (ApplicationInfo applicationInfo : enabledApps) {
                enabledAppsPkgNames.add(applicationInfo.packageName);
                Log.e("EnableSystemApps", applicationInfo.packageName);
            }

            //STORE THIS INSTALL APPS IN SHARED PREFS
            //SharedPrefs.setSetOfStringValue(Constant.Pre_Install_App, enabledAppsPkgNames, context);

            for (ApplicationInfo applicationInfo : allApps) {
                // Interested in disabled system apps only.
                if (!enabledAppsPkgNames.contains(applicationInfo.packageName)
                        && (applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                    disabledSystemApps.add(applicationInfo.packageName);
                }
            }

            if (!disabledSystemApps.isEmpty()) {
                for (String packageName : disabledSystemApps) {
                    mDevicePolicyManager.enableSystemApp(mAdminComponentName, packageName);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    public static List<AppModel> getAllApps(Activity activity) {
//        PackageManager mPackageManager = activity.getPackageManager();
//        // Disabled All apps list = {All apps} - {Enabled All apps}
//        final List<AppModel> disabledAllApps = new ArrayList<AppModel>();
//        int flags = PackageManager.GET_META_DATA |
//                PackageManager.GET_SHARED_LIBRARY_FILES |
//                PackageManager.GET_UNINSTALLED_PACKAGES;
//        // This list contains both enabled and disabled apps.
//        List<ApplicationInfo> allApps = mPackageManager.getInstalledApplications(/*PackageManager.GET_UNINSTALLED_PACKAGES*/flags);
//        Collections.sort(allApps, new ApplicationInfo.DisplayNameComparator(mPackageManager));
//        // This list contains all enabled apps.
//        List<ApplicationInfo> enabledApps =
//                mPackageManager.getInstalledApplications(0 /* Default flags */);
//        Set<String> enabledAppsPkgNames = new HashSet<String>();
//        for (ApplicationInfo applicationInfo : enabledApps) {
//            enabledAppsPkgNames.add(applicationInfo.packageName);
//        }
//
//        for (ApplicationInfo applicationInfo : allApps) {
//            // Interested in disabled system apps only.
//            /*if (!enabledAppsPkgNames.contains(applicationInfo.packageName) &&
//                    !applicationInfo.packageName.equals(activity.getPackageName())
//                    && ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0 ||
//                    (applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 1 ||
//                    (applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) == 0 ||
//                    (applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0)) {
//                AppModel appModel = new AppModel();
//                String appName = applicationInfo.loadLabel(activity.getPackageManager()).toString();
//                appModel.setAppName(appName);
//                appModel.setPackageName(applicationInfo.packageName);
//                Drawable icon = applicationInfo.loadIcon(activity.getPackageManager());
//                appModel.setAppIcon(icon);
//                disabledAllApps.add(appModel);
//            }*/
//        }
//        return disabledAllApps;
//    }

    public boolean isOnline(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = null;
            if (cm != null) {
                netInfo = cm.getActiveNetworkInfo();
                return netInfo != null && netInfo.isConnectedOrConnecting();
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private int sim = 1;

 /*   public void sendSMS(String send_msg, String mobile_no) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        ArrayList<PendingIntent> sentPendingIntents = new ArrayList<PendingIntent>();
        PendingIntent sentPI = PendingIntent.getBroadcast(context, 0, new Intent("SMS_SENT"), 0);
        // SEND BroadcastReceiver
        final BroadcastReceiver sendSMS = new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        context.getApplicationContext().unregisterReceiver(this);
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        if (sim == 2) {
                            sendFromSim2(send_msg, mobile_no);
                        }
                        context.getApplicationContext().unregisterReceiver(this);
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        if (sim == 2) {
                            sendFromSim2(send_msg, mobile_no);
                        }
                        context.getApplicationContext().unregisterReceiver(this);
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        if (sim == 2) {
                            sendFromSim2(send_msg, mobile_no);
                        }
                        context.getApplicationContext().unregisterReceiver(this);
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        if (sim == 2) {
                            sendFromSim2(send_msg, mobile_no);
                        }
                        context.getApplicationContext().unregisterReceiver(this);
                        break;
                }
            }
        };
        context.getApplicationContext().registerReceiver(sendSMS, new IntentFilter("SMS_SENT"));

        SubscriptionManager localSubscriptionManager = SubscriptionManager.from(context);
        if (localSubscriptionManager.getActiveSubscriptionInfoCount() > 1) {
            List localList = localSubscriptionManager.getActiveSubscriptionInfoList();
            SubscriptionInfo simInfo1 = (SubscriptionInfo) localList.get(0);
            sim = 2;
            ArrayList<String> mSMSMessage = SmsManager.getSmsManagerForSubscriptionId(simInfo1.getSubscriptionId()).divideMessage(send_msg);
            for (int i = 0; i < mSMSMessage.size(); i++) {
                sentPendingIntents.add(i, sentPI);
            }
            SmsManager.getSmsManagerForSubscriptionId(simInfo1.getSubscriptionId()).sendMultipartTextMessage(Constant.COUNTRY_CODE + mobile_no, null, mSMSMessage, sentPendingIntents, null);
        } else if (localSubscriptionManager.getActiveSubscriptionInfoCount() == 1) {
            List localList = localSubscriptionManager.getActiveSubscriptionInfoList();
            SubscriptionInfo simInfo1 = (SubscriptionInfo) localList.get(0);
            sim = 1;
            ArrayList<String> mSMSMessage = SmsManager.getSmsManagerForSubscriptionId(simInfo1.getSubscriptionId()).divideMessage(send_msg);
            for (int i = 0; i < mSMSMessage.size(); i++) {
                sentPendingIntents.add(i, sentPI);
            }
            SmsManager.getSmsManagerForSubscriptionId(simInfo1.getSubscriptionId()).sendMultipartTextMessage(Constant.COUNTRY_CODE + mobile_no, null, mSMSMessage, sentPendingIntents, null);
        }
    }*/


    private void sendFromSim2(String send_msg, String mobile_no) {
        SubscriptionManager localSubscriptionManager = SubscriptionManager.from(context);
       /* if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }*/
        if (localSubscriptionManager.getActiveSubscriptionInfoCount() > 1) {
            List localList = localSubscriptionManager.getActiveSubscriptionInfoList();
            SubscriptionInfo simInfo2 = (SubscriptionInfo) localList.get(1);

            ArrayList<PendingIntent> sentPendingIntents = new ArrayList<PendingIntent>();
            PendingIntent sentPI = PendingIntent.getBroadcast(context, 0, new Intent(
                    "SMS_SENT"), 0);
            // SEND BroadcastReceiver
            final BroadcastReceiver sendSMS = new BroadcastReceiver() {
                @Override
                public void onReceive(Context arg0, Intent arg1) {
                    switch (getResultCode()) {
                        case Activity.RESULT_OK:
                            context.getApplicationContext().unregisterReceiver(this);
                            break;
                        case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                            context.getApplicationContext().unregisterReceiver(this);
                            break;
                        case SmsManager.RESULT_ERROR_NO_SERVICE:
                            context.getApplicationContext().unregisterReceiver(this);
                            break;
                        case SmsManager.RESULT_ERROR_NULL_PDU:
                            context.getApplicationContext().unregisterReceiver(this);
                            break;
                        case SmsManager.RESULT_ERROR_RADIO_OFF:
                            context.getApplicationContext().unregisterReceiver(this);
                            break;
                    }
                }
            };
            context.getApplicationContext().registerReceiver(sendSMS, new IntentFilter("SMS_SENT"));
            ArrayList<String> mSMSMessage = SmsManager.getSmsManagerForSubscriptionId(simInfo2.getSubscriptionId()).divideMessage(send_msg);
            for (int i = 0; i < mSMSMessage.size(); i++) {
                sentPendingIntents.add(i, sentPI);
            }
            SmsManager.getSmsManagerForSubscriptionId(simInfo2.getSubscriptionId()).sendMultipartTextMessage(Constant.COUNTRY_CODE + mobile_no, null, mSMSMessage, sentPendingIntents, null);
        }
    }

    public void UpdateVersionPlayerID() {
       /* try {
            if (SharedPrefs.isBooleanSet(context, Constant.IsRegister)) {
                int fl_version = Integer.parseInt(BuildConfig.VERSION_NAME);
                int old_fl_version = SharedPrefs.getIntValue(Constant.FL_VERSION, context);
                Log.e("FCM", "fl_version - " + fl_version + " fl_version - " + old_fl_version);
                if (fl_version > old_fl_version) {
                    RetrofitCall retrofitCall = new RetrofitCall();
                    retrofitCall.update_version_playerid(context, fl_version);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    public void Tracking(Context context, boolean flag) {
        try {
            SharedPrefs.setABoolean(context, Constant.TRACKING, flag);
            String status = "0";
            if (flag) {
                status = "1";
                Log.e("FCM Firebase", "TRACKINGON");
            } else {
                status = "0";
                Log.e("FCM Firebase", "TRACKINGOFF");
            }

           /* if (flag) {
                Intent iService = new Intent(context, MonitoringService.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(iService);
                } else {
                    context.startService(iService);
                }
            }else {
                Intent iService = new Intent(context, MonitoringService.class);
                context.stopService(iService);
            }

            RetrofitCall retrofitCall = new RetrofitCall();
            retrofitCall.set_tracking_status(context, status);*/

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void simTrackingOffline(Context context, boolean flag) {
        try {
            SharedPrefs.setABoolean(context, Constant.SIM_TRACKING_OFFLINE, flag);
            String status = "0";

/*
            if (flag) {
                status = "1";
                Log.e("FCM Firebase", "SIMTRACKINGOFFLINEON");
                checkSIMOffline(context, SimAlertType.SMS);
            } else {
                status = "0";
                Log.e("FCM Firebase", "SIMTRACKINGOFFLINEOFF");
            }

            RetrofitCall retrofitCall = new RetrofitCall();
            retrofitCall.set_sim_offline_tracking_status(context, status);*/

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void simTracking(Context context, boolean flag) {
        try {
            SharedPrefs.setABoolean(context, Constant.SIM_TRACKING, flag);
            String status = "0";
            if (flag) {
                status = "1";
                Log.e("FCM Firebase", "SIMTRACKINGON");
//                checkSIMOffline(context, SimAlertType.API);
            } else {
                status = "0";
                Log.e("FCM Firebase", "SIMTRACKINGOFF");
            }

          /*  RetrofitCall retrofitCall = new RetrofitCall();
            retrofitCall.set_sim_tracking_status(context, status);*/

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadLocation(Context context) {
       /* try {
            SimpleDateFormat curTimeFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a", new Locale(Constant.LANG));
            final String currentDateTime = curTimeFormat.format(new Date());
            LocationGetter locationGetter = new LocationGetter(context);
            Coordinates coordinates = locationGetter.getLocation(30000, 5000);
            Log.e("FCM", "coordinates - " + coordinates.getLatitude() + "," + coordinates.getLongitude());
            if (coordinates.getLatitude() != null && coordinates.getLongitude() != null) {
                String imei = getUniqueIMEIId(context);
                msg = "IMEI : " + imei + "\n" + "Location is http://www.google.com/maps/place/" + coordinates.getLatitude() + "," + coordinates.getLongitude()
                        +  "\nYour Location is : " + "\nLatitude is " + coordinates.getLatitude() + "," + "\nLongitude is " + coordinates.getLongitude() + "\n" + "\nLocation time : " + currentDateTime;
                String senderNumber = SharedPrefs.getStringValue(Constant.Mobile, context);
                sendSMS(msg, senderNumber);
            }
        } catch (Exception e) {
            Log.e("FCM", "loadLocation Exception - " + e.getMessage());
        }*/
    }


    private LocationManager locationManager;
    private boolean flag = false;

   /* public void sendLocation() {
        try {
            Log.e("FCM", "sendLocation");
            DevicePolicyManager dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
            if (dpm.isDeviceOwnerApp(context.getPackageName())) {
                ComponentName componentName = new ComponentName(context, DeviceAdminReceiver.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    dpm.setLocationEnabled(componentName, true);
                } else {
                    dpm.setSecureSetting(componentName, Settings.Secure.LOCATION_MODE, String.valueOf(Settings.Secure.LOCATION_MODE_HIGH_ACCURACY));
                }
            }
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                sendData(context);
                return;
            }

            if (Looper.myLooper() == null) {
                Looper.prepare();
            }

            flag = false;
            Handler handler1 = new Handler();
            handler1.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!flag) {
                        Log.e("FCM", "handler1");
                        sendData(context);
                    }
                }
            }, 20000);

            PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            @SuppressLint("InvalidWakeLockTag") PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Wakelock");
            try {
                wakeLock.acquire();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, this);
            } else {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 10, this);
            }

            try {
                wakeLock.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            Log.e("FCM", "Exception 1 " + e.getMessage());
            sendData(context);
        }
    }*/

   /* private void sendData(Context context) {
        Location location = getLastBestLocation(context);
//        sendLocationData(location, context);
    }*/

    /*private void sendLocationData(Location location, Context context) {
        try {

            try {
                if (locationManager != null) {
                    locationManager.removeUpdates(this);
                }
            } catch (Exception e) {
                Log.e("FCM", "Exception 2 " + e.getMessage());
            }


            String lat, lng;
            if (location != null) {
                try {
                    lat = String.valueOf(location.getLatitude());
                    lng = String.valueOf(location.getLongitude());
                    RetrofitCall retrofitCall = new RetrofitCall();
                    retrofitCall.set_location(context, lat, lng);
                    Log.e("FCM", "set_location");
                } catch (Exception e) {
                    if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10, 60000, this);
                        if (locationManager != null) {
                            Location location1 = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                            if (location1 != null) {
                                onLocationChanged(location1);
                            }
                        }
                    } else {
                        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10, 60000, this);
                        if (locationManager != null) {
                            Location location1 = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location1 != null) {
                                onLocationChanged(location1);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e("FCM", "Exception 3 " + e.getMessage());
        }
    }*/


  /*  @Override
    public void onLocationChanged(@NonNull Location location) {
        try {
            flag = true;
//            sendLocationData(location, context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/


  /*  private Location getLastBestLocation(Context context) {
        try {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return null;
            }
            Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Location locationNet = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            long GPSLocationTime = 0;
            if (null != locationGPS) {
                GPSLocationTime = locationGPS.getTime();
            }

            long NetLocationTime = 0;

            if (null != locationNet) {
                NetLocationTime = locationNet.getTime();
            }

            if (0 < GPSLocationTime - NetLocationTime) {
                return locationGPS;
            } else {
                return locationNet;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }*/

/*    private class sendNotification extends AsyncTask<String, Void, Bitmap> {

        private Context context;
        private String notification_body, notification_title;

        public sendNotification(Context context, String notification_body, String notification_title) {
            this.context = context;
            this.notification_body = notification_body;
            this.notification_title = notification_title;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            InputStream in;
            try {
                Log.e("ACCOUNT", "doInBackground");
                in = new URL(BuildConfig.CARD + notification_body).openStream();
                Bitmap bmp = BitmapFactory.decodeStream(in);
                return bmp;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap big_bitmap_image) {
            super.onPostExecute(big_bitmap_image);
            try {
                Log.e("ACCOUNT", "show notification");
                Intent intent = new Intent(context, AdvertiseActivity.class);
                SharedPrefs.setStringValue(Constant.Path, BuildConfig.CARD + notification_body, context);
                SharedPrefs.setStringValue(Constant.Title, notification_title, context);
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                stackBuilder.addNextIntentWithParentStack(intent);
                PendingIntent pendingintent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE);

                NotificationCompat.BigPictureStyle style = new NotificationCompat.BigPictureStyle()
                        .bigPicture(big_bitmap_image)
                        .setSummaryText(notification_title);

                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, "100")
                        .setContentTitle(notification_title)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setLargeIcon(big_bitmap_image)
                        .setTicker("Advertise Ticker!")
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setStyle(style)
                        .setContentIntent(pendingintent)
                        .setAutoCancel(true)
                        .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        .setOngoing(true);

                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel channel = new NotificationChannel("100", "Advertise", NotificationManager.IMPORTANCE_HIGH);
                    channel.setDescription(notification_title);
                    notificationManager.createNotificationChannel(channel);
                }
                notificationManager.notify(1002, notificationBuilder.build());

            } catch (Exception e) {
                Log.e("ACCOUNT", "Exception - " + e.getMessage());
            }
        }
    }*/

    public void generateCode(String code, Context context) {
        try {
            String Iterate = SharedPrefs.getStringValue(Constant.ITERATE, context);
            if (Iterate.isEmpty()) {
                Iterate = "0";
            }
            int i = Integer.parseInt(Iterate) + 1;
            Log.e("FCM", "Iterate - " + i);
            SharedPrefs.setStringValue(Constant.ITERATE, String.valueOf(i), context);
            Log.e("FCM", "code - " + code);
            long New_code = (Long.parseLong(code) / (i * 10000000)) + (i * 100000);
            Log.e("FCM", "New_code - " + New_code);
            SharedPrefs.setStringValue(Constant.UNLOCK_CODE, String.valueOf(New_code), context);
            SharedPrefs.setStringValue(Constant.CODE, code, context);
        } catch (Exception e) {
            Toast.makeText(context, "Exception 2 - " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

   /* public void loadEmiDetails(Context context, ArrayList<EmiResult> data) {
        try {
            if (!SharedPrefs.isBooleanSet(context, Constant.EMI_LOAD)) {
                AppDatabase db = DatabaseClient.getInstance(context).getAppDatabase();
                new InsertEmiDetailsDB(context, data, db).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    /*private class InsertEmiDetailsDB extends AsyncTask<String, String, String> {
        private ArrayList<EmiResult> data;
        private AppDatabase db;
        private Context context;

        public InsertEmiDetailsDB(Context context, ArrayList<EmiResult> data, AppDatabase db) {
            this.data = data;
            this.db = db;
            this.context = context;
        }

        @Override
        protected String doInBackground(String... strings) {
            for (int i = 0; i < data.size(); i++) {
                try {
                    Date emiDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").parse(data.get(i).getEmi_date());
                    assert emiDate != null;
                    long Emi_date = emiDate.getTime();
                    for (int j = 2; j >= 0; j--) {
                        long Subtract_date;
                        if (j != 0) {
                            Subtract_date = Emi_date - j * 24 * 60 * 60 * 1000;
                        } else {
                            Subtract_date = Emi_date;
                        }
                        Date SubtractDate = new Date(Subtract_date);
                        String SubtractDateStr = new SimpleDateFormat("dd-MM-yyyy  HH:mm:ss").format(SubtractDate);

                        Emi emi = new Emi();
                        emi.setId(data.get(i).getId());
                        emi.setEmi_amount(data.get(i).getEmi_amount());
                        emi.setStatus(data.get(i).getStatus());
                        emi.setEmi_date(SubtractDateStr);
                        db.emiDAO().insertAll(emi);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            SharedPrefs.setABoolean(context, Constant.EMI_LOAD, true);
            startService(context, true);
        }
    }*/

 /*   public void startService(Context context, boolean flag) {
        try {
            Intent i = new Intent(context, BackgroundService.class);
            if (isMyServiceRunning(BackgroundService.class, context)) {
                if (flag) {
                    context.stopService(i);
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(i);
                } else {
                    context.startService(i);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void get_payment_status(Context context) {
        try {
            String id = SharedPrefs.getStringValue(Constant.EMI_ID, context);
            Call<EmiStatusPost> call = RetrofitClient.getPostService().get_payment_status("", id, BuildConfig.Authorization);
            call.enqueue(new Callback<EmiStatusPost>() {
                @Override
                public void onResponse(@NonNull Call<EmiStatusPost> call, @NonNull Response<EmiStatusPost> response) {
                    try {
                        EmiStatusPost response1 = null;
                        if (response.isSuccessful()) {
                            response1 = response.body();
                            assert response1 != null;
                            if (response1.getResponse() != null) {
                                if (response1.getResponse().equalsIgnoreCase("success")) {
                                    if (response1.getData() != null && response1.getData().size() > 0) {
                                        if (response1.getData().get(0).getStatus().equals("0")) {
                                            sendNotify(context);
                                        } else {
                                            deleteEmi(context, id);
                                        }
                                    } else {
                                        sendNotify(context);
                                    }
                                } else {
                                    sendNotify(context);
                                }
                            } else {
                                sendNotify(context);
                            }
                        } else {
                            sendNotify(context);
                        }
                    } catch (Exception e) {
                        Log.e("FCM", "Exception - " + e.getMessage());
                        sendNotify(context);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<EmiStatusPost> call, @NonNull Throwable t) {
                    Log.e("FCM", "Exception - " + t.getMessage());
                    sendNotify(context);
                }
            });
        } catch (Exception e) {
            Log.e("FCM", "Exception - " + e.getMessage());
            sendNotify(context);
        }
    }

    private void deleteEmi(Context context, String id) {
        try {
            AppDatabase db = DatabaseClient.getInstance(context).getAppDatabase();
            if (id == null) {
                int index = SharedPrefs.getIntValue(Constant.EMI_INDEX, context);
                db.emiDAO().deleteEmiByIndex(index);
            } else {
                db.emiDAO().deleteEmiByID(id);
            }
            startService(context, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendNotify(Context context) {
        try {
            String EMI_AMT = SharedPrefs.getStringValue(Constant.EMI_AMT, context);
            ShowNotification(context, context.getString(R.string.EMIAlert), context.getString(R.string.EMIMsgEng), context.getString(R.string.EMIMsgHin), EMI_AMT, 5001);
            loadAlert(context, 5001);
            deleteEmi(context, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private TextToSpeech tts;
    private TextToSpeech tts1;

    public void ShowNotification(Context context, String title, String msg, String msgHin, String EMIAMT, int notify_id) {
        try {
            Intent intent;
            if (notify_id == 5003) {
                intent = new Intent(context, SettingActivity.class);
            } else {
                intent = new Intent(context, AlertActivity.class);
            }
            Log.e("FCM", "NOTIFICATION_ID - " + notify_id);
            Log.e("FCM", "EMIAMT - " + EMIAMT);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("NOTIFICATION_ID", notify_id);
            //intent.putExtra("EMIAMT", EMIAMT);
            PendingIntent pendingintent = PendingIntent.getActivity(context, notify_id, intent, PendingIntent.FLAG_IMMUTABLE);
            RemoteViews contentView = new RemoteViews(context.getPackageName(), R.layout.custom_notification_layout);
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

            contentView.setTextViewText(R.id.notification_title, title);
            contentView.setTextViewText(R.id.notification_desc, msg);
            if (notify_id == 5001) {
                contentView.setViewVisibility(R.id.RetailerInfo, View.GONE);
                contentView.setViewVisibility(R.id.emi_amount, View.VISIBLE);
                contentView.setTextViewText(R.id.emi_amount, context.getString(R.string.EMIPayAmout) + EMIAMT);
            } else {
                boolean flag = false;
                contentView.setViewVisibility(R.id.emi_amount, View.GONE);
                String Mobile = SharedPrefs.getStringValue(Constant.Mobile, context);
                if (Mobile != null && !Mobile.isEmpty() &&
                        !Mobile.equals("null")) {
                    contentView.setTextViewText(R.id.Call, Mobile);
                    flag = true;
                }
                String FULLNAME = SharedPrefs.getStringValue(Constant.FULLNAME, context);
                if (FULLNAME != null && !FULLNAME.isEmpty() && !FULLNAME.equals("null")) {
                    String COMPANYNAME = SharedPrefs.getStringValue(Constant.COMPANYNAME, context);
                    if (COMPANYNAME != null && !COMPANYNAME.isEmpty() && !COMPANYNAME.equals("null")) {
                        contentView.setTextViewText(R.id.Company, FULLNAME + ", " + COMPANYNAME);
                    } else {
                        contentView.setTextViewText(R.id.Company, FULLNAME);
                    }
                    flag = true;
                }
                if (flag) {
                    contentView.setViewVisibility(R.id.RetailerInfo, View.VISIBLE);
                } else {
                    contentView.setViewVisibility(R.id.RetailerInfo, View.GONE);
                }
            }

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "500");
            builder.setContent(contentView);
            builder.setSmallIcon(R.drawable.app_icon);
            builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
            builder.setPriority(NotificationCompat.PRIORITY_MAX);
            builder.setContentIntent(pendingintent);
            builder.setAutoCancel(notify_id == 5001);
            builder.setOngoing(true);
            Notification note = builder.build();
            note.defaults |= Notification.DEFAULT_VIBRATE;
            note.defaults |= Notification.DEFAULT_SOUND;
            note.defaults |= Notification.DEFAULT_LIGHTS;
            note.bigContentView = contentView;
            if (notify_id != 5001) {
                note.flags = Notification.FLAG_ONGOING_EVENT;
            }
            builder.setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});
            if (Build.VERSION.SDK_INT >= 26) {
                NotificationChannel channel = new NotificationChannel("500", title, NotificationManager.IMPORTANCE_DEFAULT);
                channel.setDescription(msg);
                notificationManager.createNotificationChannel(channel);
            }
            notificationManager.notify(notify_id, note);

            tts = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    try {
                        Log.e("FCM", "onInit - " + status);
                        if (status == TextToSpeech.SUCCESS) {
                            tts.setLanguage(Locale.ENGLISH);
                            Log.e("FCM", "showToast - " + msg);
                            tts.speak(msg, TextToSpeech.QUEUE_FLUSH, null, null);
                        }
                    } catch (Exception e) {
                        Log.e("FCM", "onInit Exception - " + e.getMessage());
                    }
                }
            });

            tts1 = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    try {
                        Log.e("FCM", "onInit - " + status);
                        if (status == TextToSpeech.SUCCESS) {
                            tts1.setLanguage(Locale.forLanguageTag("hin"));
                            Log.e("FCM", "showToast - " + msgHin);
                            tts1.speak(msgHin, TextToSpeech.QUEUE_FLUSH, null, null);
                        }
                    } catch (Exception e) {
                        Log.e("FCM", "onInit Exception - " + e.getMessage());
                    }
                }
            });
        } catch (Exception e) {
            Log.e("FCM", "ShowNotification Exception - " + e.getMessage());
        }
    }

    public void HideNotification(Context context, int notify_id) {
        try {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            notificationManager.cancel(notify_id);
        } catch (Exception e) {
            Log.e("FCM", "HideNotification Exception - " + e.getMessage());
        }
    }

    private void loadAlert(Context context, int notify_id) {
        try {
            Intent i = new Intent(context, AlertActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.putExtra("NOTIFICATION_ID", notify_id);
            context.startActivity(i);
        } catch (Exception e) {
            try {
                Intent i = new Intent(context, AlertActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("NOTIFICATION_ID", notify_id);
                context.startActivity(i);
            } catch (Exception e1) {
                Log.e("FCM", "loadAlert Exception - " + e1.getMessage());
            }
        }
    }

    private AlertDialog finalAlertDialog = null;*/

/*    public void loadDialog(Activity activity, AlertDialog alertDialog) {
        try {
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(activity, R.style.TransparentDialog);
            LayoutInflater inflater = activity.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.qr_code_layout, null);
            alertBuilder.setView(dialogView);
            ImageView QRIcon = dialogView.findViewById(R.id.QRIcon);
            AppCompatButton OK = dialogView.findViewById(R.id.OK);
            String QR_PIC = SharedPrefs.getStringValue(Constant.QR_PIC, activity);
            if (QR_PIC != null && !QR_PIC.isEmpty() && !QR_PIC.equals("null")) {
                Log.e("FCM", "loadDialog - " + QR_PIC);
                Glide.with(activity).load(BuildConfig.QRRETAILER + QR_PIC).error(R.drawable.app_icon).into(QRIcon);
            }

            OK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finalAlertDialog.dismiss();
                }
            });

            alertDialog = alertBuilder.create();
            alertDialog.setCancelable(true);
            alertDialog.show();
            finalAlertDialog = alertDialog;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void SendStatus(Context context, String status) {
        try {
            if (isOnline(context)) {
                if (!status.isEmpty()) {
                    String imei = getUniqueIMEIId(context);
                    RetrofitCall retrofitCall = new RetrofitCall();
                    retrofitCall.set_lock_status(imei, status);
                }
            }
        } catch (Exception e) {
            Log.e("FCM", "Exception - " + e.getMessage());
        }
    }

    public void onCommand(JSONObject json, Context context) {
        try {
            if (SharedPrefs.isBooleanSet(context, Constant.IsRegister)) {
                if (json.has("Command")) {
                    String Command = json.getString("Command");
                    Log.e("FCM", Command);
                    if (Command.equalsIgnoreCase("LOCATION")) {
                        sendLocation();
                    } else if (Command.equalsIgnoreCase("TRACKINGON")) {
                        Tracking(context, true );
                    } else if (Command.equalsIgnoreCase("TRACKINGOFF")) {
                        Tracking(context, false );
                    } else if (Command.equalsIgnoreCase("SIMTRACKINGON")) {
                        simTracking(context, true );
                    } else if (Command.equalsIgnoreCase("SIMTRACKINGOFF")) {
                        simTracking(context, false );
                    } else if (Command.equalsIgnoreCase("SIMTRACKINGOFFLINEON")) {
                        simTrackingOffline(context, true );
                    } else if (Command.equalsIgnoreCase("SIMTRACKINGOFFLINEOFF")) {
                        simTrackingOffline(context, false );
                    } else if (Command.equalsIgnoreCase("LOCK")) {
                        Lock(context, true, null);
                    } else if (Command.equalsIgnoreCase("UNLOCK")) {
                        Lock(context, false, null);
                    } else if (Command.equalsIgnoreCase("UNCLAIM")) {
                        UNCLAIM(context, null);
                    } else if (Command.equalsIgnoreCase("APPLOCK")) {
                        AppLock(context, true, null);
                    } else if (Command.equalsIgnoreCase("APPUNLOCK")) {
                        AppLock(context, false, null);
                    } else if (Command.equalsIgnoreCase("SETWALLPAPER")) {
                        setWallpaper(context, true, null);
                    } else if (Command.equalsIgnoreCase("REMOVEWALLPAPER")) {
                        RemoveWallpaper(context, true, null);
                    } else if (Command.equalsIgnoreCase("CAMERALOCK")) {
                        CameraLock(context, true, null);
                    } else if (Command.equalsIgnoreCase("CAMERAUNLOCK")) {
                        CameraLock(context, false, null);
                    } else if (Command.equalsIgnoreCase("SCREENLOCK")) {
                        String PIN = json.getString("PIN");
                        ScreenLock(context, true, null, PIN);
                    } else if (Command.equalsIgnoreCase("REMOVESCREENLOCK")) {
                        ScreenLock(context, false, null, null);
                    } else if (Command.equalsIgnoreCase("EMIALERT")) {
                        String EMIAMT = json.getString("EMIAMT");
                        if (!EMIAMT.isEmpty()) {
                            SharedPrefs.setStringValue(Constant.EMI_AMT_NOTIFY, EMIAMT, context);
                        }
                        alertEmi(EMIAMT);
                    } else if (Command.equalsIgnoreCase("SIMINFO")) {
                        checkSIMOffline(context, SimAlertType.FETCH);
                    } else if (json.has("UPDATE")) {
                        Log.e("FCM Firebase", "UPDATE");
                        String version = json.getString("UPDATE");
                        int ver = Integer.parseInt(version);
                        if (ver > BuildConfig.VERSION_CODE) {
                            Log.e("FCM Firebase", "UPDATE-ok");
                            new UpdateApp(context).execute(BuildConfig.APK);
                        }
                    } else if (json.has("ICON")) {
                        String ICON = json.getString("ICON");
                        if (ICON.equalsIgnoreCase("SHOW")) {
                            hideOrShowAppIcon(context, false, false);
                        } else if (ICON.equalsIgnoreCase("HIDE")) {
                            hideOrShowAppIcon(context, true, false);
                        }
                    } else if (Command.equalsIgnoreCase("REBOOT")) {
                        REBOOT(context, null);
                    } else if (Command.equalsIgnoreCase("USBON")) {
                        USBON(context, true, null);
                    } else if (Command.equalsIgnoreCase("USBOFF")) {
                        USBON(context, false, null);
                    } else if (Command.equalsIgnoreCase("CALLLOCK")) {
                        SharedPrefs.setABoolean(context, Constant.CALLLOCK, true);
                        ShowNotification(context, context.getString(R.string.CallLock), context.getString(R.string.CallLockMsg), context.getString(R.string.CallLockMsgHin), "", 5004);
                        CALLLOCK(context, true, null);
                    } else if (Command.equalsIgnoreCase("CALLUNLOCK")) {
                        SharedPrefs.setABoolean(context, Constant.CALLLOCK, false);
                        HideNotification(context, 5004);
                        CALLLOCK(context, false, null);
                    }

                    if(json.has("Event_id")){
                        String Event_id = json.getString("Event_id");
                        Log.e("FCM", Event_id);
                        RetrofitCall retrofitCall = new RetrofitCall();
                        retrofitCall.update_event_logs(Event_id);
                    }



                } else if (json.has("notification_title") && json.has("notification_body")) {
                    String notification_title = json.getString("notification_title");
                    String notification_body = json.getString("notification_body");
                    Log.e("ACCOUNT", "notification_title - " + notification_title);
                    Log.e("ACCOUNT", "notification_body - " + notification_body);
                    new sendNotification(context, notification_body, notification_title).execute();
                }
            }else {
                if (json.has("Command")) {
                    String Command = json.getString("Command");
                    if (Command.equalsIgnoreCase("REGISTER")) {
                        if (json.has("Event_id")) {
                            String Event_id = json.getString("Event_id");
                            Log.e("FCM", Event_id);
                            RetrofitCall retrofitCall = new RetrofitCall();
                            retrofitCall.update_event_logs(Event_id);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("FCM Firebase", "ERROR-" + e.getMessage());
        }

    }

    private void USBON(Context context, boolean flag, String senderNumber) {
        try {
            String status = "0";
            if (flag) {
                status = "1";
            } else {
                status = "0";
            }

            USB(context, flag);

            if (isOnline(context)) {
                String imei = getUniqueIMEIId(context);
                RetrofitCall retrofitCall = new RetrofitCall();
                retrofitCall.set_usb_status(imei, status);
            }

            if (flag) {
                if (senderNumber != null) {
                    sendSMS("Successfully USB ON", senderNumber);
                }
            } else {
                if (senderNumber != null) {
                    sendSMS("Successfully USB OFF", senderNumber);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void USB(Context context, boolean flag) {
        try {
            ComponentName adminComponent = new ComponentName(context, DeviceAdminReceiver.class);
            final DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
            if (devicePolicyManager.isAdminActive(adminComponent)) {
                if (flag) {
                    devicePolicyManager.clearUserRestriction(adminComponent, DISALLOW_MOUNT_PHYSICAL_MEDIA);
                    devicePolicyManager.clearUserRestriction(adminComponent, DISALLOW_USB_FILE_TRANSFER);
                    devicePolicyManager.clearUserRestriction(adminComponent, DISALLOW_DEBUGGING_FEATURES);
                    Log.e("FCM", "addUserRestriction");
                } else {
                    devicePolicyManager.addUserRestriction(adminComponent, DISALLOW_MOUNT_PHYSICAL_MEDIA);
                    devicePolicyManager.addUserRestriction(adminComponent, DISALLOW_USB_FILE_TRANSFER);
                    devicePolicyManager.addUserRestriction(adminComponent, DISALLOW_DEBUGGING_FEATURES);
                    Log.e("FCM", "clearUserRestriction");
                }
            }
        } catch (Exception e) {
            Log.e("FCM", "Exception - " + flag);
        }
    }

    private void CALLLOCK(Context context, boolean flag, String senderNumber) {
        try {
            String status = "0";
            if (flag) {
                status = "1";
            } else {
                status = "0";
            }

            CallLock(context, flag);

            if (isOnline(context)) {
                String imei = getUniqueIMEIId(context);
                RetrofitCall retrofitCall = new RetrofitCall();
                retrofitCall.set_call_status(imei, status);
            }

            if (flag) {
                if (senderNumber != null) {
                    sendSMS("Successfully CALLLOCK", senderNumber);
                }
            } else {
                if (senderNumber != null) {
                    sendSMS("Successfully CALLUNLOCK", senderNumber);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void CallLock(Context context, boolean flag) {
        try {
            ComponentName adminComponent = new ComponentName(context, DeviceAdminReceiver.class);
            final DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
            if (devicePolicyManager.isAdminActive(adminComponent)) {
                if (flag) {
                    devicePolicyManager.addUserRestriction(adminComponent, UserManager.DISALLOW_OUTGOING_CALLS);
                    Log.e("FCM", "addUserRestriction");
                } else {
                    devicePolicyManager.clearUserRestriction(adminComponent, UserManager.DISALLOW_OUTGOING_CALLS);

                    Log.e("FCM", "clearUserRestriction");
                }
            }
        } catch (Exception e) {
            Log.e("FCM", "Exception - " + flag);
        }
    }

    private void REBOOT(Context context, String senderNumber) {
        try {
            ComponentName adminComponent = new ComponentName(context, DeviceAdminReceiver.class);
            final DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
            if (devicePolicyManager.isAdminActive(adminComponent)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    devicePolicyManager.reboot(adminComponent);
                    Log.e("FCM", "reboot:"+senderNumber);
                    if (senderNumber != null) {
                        sendSMS("Successfully Rebooting", senderNumber);
                    }
                    devicePolicyManager.reboot(adminComponent);
                }
            }
        } catch (Exception e) {
            Log.e("FCM", "Exception - " + e.getMessage());
        }
    }

    public void lockMyDevice(Context context, String status) {
        try {
            ComponentName adminComponent = new ComponentName(context, DeviceAdminReceiver.class);
            final DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
            if (devicePolicyManager.isAdminActive(adminComponent)) {
                devicePolicyManager.lockNow();

                try {
                    Intent i = new Intent(context, KioskActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(i);
                } catch (Exception e) {
                    try {
                        Intent i = new Intent(context, KioskActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(i);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }

                SendStatus(context, status);
            }
        } catch (Exception e) {
            Log.e("FCM", "LOCK ERROR");
        }
    }

    public class UpdateApp extends AsyncTask<String, Void, Void> {

        private Context context;
        private File outputFile = null;

        public UpdateApp(Context context) {
            this.context = context;
        }

        @Override
        protected Void doInBackground(String... arg0) {
            try {
                URL url = new URL(arg0[0]);
                HttpURLConnection c = (HttpURLConnection) url.openConnection();
                c.setRequestMethod("GET");
                c.setDoOutput(true);
                c.connect();

                File file = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    Log.e("SMS COMMAND SMSRECEIVER", "Android 11");
                    file = new File(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)));
                } else {
                    Log.e("SMS COMMAND SMSRECEIVER", "Android 10");
                    file = new File(context.getExternalFilesDir(null).getAbsolutePath() + File.separator + Environment.DIRECTORY_DOWNLOADS);
                }
                file.mkdirs();
                outputFile = new File(file, "update.apk");
                if (outputFile.exists()) {
                    outputFile.delete();
                }
                FileOutputStream fos = new FileOutputStream(outputFile);

                InputStream is = c.getInputStream();

                byte[] buffer = new byte[1024];
                int len1 = 0;
                while ((len1 = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len1);
                }
                fos.close();
                is.close();

                Log.e("SMS COMMAND SMSRECEIVER", "Downloaded");
            } catch (Exception e) {
                Log.e("SMS COMMAND SMSRECEIVER", "Downloaded error! " + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            InstallAPK(context, outputFile);
        }
    }

    public void InstallAPK(Context context, File apk_file) {
        try {
            Log.e("SMS COMMAND SMSRECEIVER", "Installing");
            File apkfile = new File(apk_file.getAbsolutePath());
            if (apkfile.exists()) {

                FileInputStream in = new FileInputStream(apkfile);

                PackageInstaller packageInstaller = context.getPackageManager().getPackageInstaller();
                PackageInstaller.SessionParams params = new PackageInstaller.SessionParams(
                        PackageInstaller.SessionParams.MODE_FULL_INSTALL);
                params.setAppPackageName(context.getPackageName());
                // set params
                int sessionId = packageInstaller.createSession(params);
                PackageInstaller.Session session = packageInstaller.openSession(sessionId);
                OutputStream out = session.openWrite("COSU", 0, -1);
                byte[] buffer = new byte[65536];

                int c;
                while ((c = in.read(buffer)) != -1) {
                    out.write(buffer, 0, c);
                }
                session.fsync(out);
                in.close();
                out.close();
                SharedPrefs.setABoolean(context, Constant.IsUpdate, true);
                SyncAlarmReceiver.setupAlarm(context);
                if(SharedPrefs.getStringValue(Constant.LAST_SYNC_TIME,"",context).equalsIgnoreCase("")) {
                    SharedPrefs.setStringValue(Constant.LAST_SYNC_TIME, "" + System.currentTimeMillis(), context);
                }
                session.commit(createIntentSender(context, sessionId));
                Handler handler = new Handler();
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        apk_file.delete();
                    }
                };
                handler.postDelayed(runnable, 60000);
            } else {
                Log.e("SMS COMMAND SMSRECEIVER", "File Not Found error! ");
            }
        } catch (Exception e) {
            Log.e("SMS COMMAND SMSRECEIVER", "Installing error! " + e.getMessage());
        }
    }

    private IntentSender createIntentSender(Context context, int sessionId) {
        Log.e("SMS COMMAND SMSRECEIVER", "Installed");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                sessionId,
                new Intent(ACTION_INSTALL_COMPLETE),
                0);
        return pendingIntent.getIntentSender();
    }

    private WindowManager wm;
    private View mView;
    private Runnable runnable;

    @SuppressLint({"InvalidWakeLockTag", "MissingPermission"})
    private void setWallpaper(Context context, boolean online, String senderNumber) {
        SharedPrefs.setABoolean(context, Constant.WALLPAPER, true);
        WallpaperManager wallManager = WallpaperManager.getInstance(context);
        try {
            wallManager.clear();

            wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int width = size.x;
            int height = size.y;

            mView = LayoutInflater.from(context).inflate(R.layout.wallpaper_layout, null);
            LinearLayout linearLayout1 = mView.findViewById(R.id.layout);
            TextView Message = mView.findViewById(R.id.Message);
            TextView Call = mView.findViewById(R.id.Call);
            TextView Company = mView.findViewById(R.id.Company);
            Message.setText(context.getString(R.string.EMIMsg));
            String Mobile = SharedPrefs.getStringValue(Constant.Mobile, context);
            if (Mobile != null && !Mobile.isEmpty() &&
                    !Mobile.equals("null")) {
                Call.setText(Mobile);
            }
            String FULLNAME = SharedPrefs.getStringValue(Constant.FULLNAME, context);
            if (FULLNAME != null && !FULLNAME.isEmpty() && !FULLNAME.equals("null")) {
                String COMPANYNAME = SharedPrefs.getStringValue(Constant.COMPANYNAME, context);
                if (COMPANYNAME != null && !COMPANYNAME.isEmpty() && !COMPANYNAME.equals("null")) {
                    Company.setText(FULLNAME + ", " + COMPANYNAME);
                } else {
                    Company.setText(FULLNAME);
                }
            }

            int layout_param = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                layout_param = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            }
            WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT, layout_param,
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                    PixelFormat.TRANSLUCENT);
            params.gravity = Gravity.TOP;
            wm.addView(mView, params);

            Log.e("FCM", "handler");
            Handler handler = new Handler();
            runnable = new Runnable() {
                @SuppressLint("ResourceType")
                public void run() {

                    Bitmap bitmap = getBitmapFromView(linearLayout1, height, width);
                    try {
                        if (bitmap != null) {
                            wallManager.setBitmap(bitmap);
                        } else {
                            wallManager.setResource(R.drawable.wallpaper_bg);
                        }
                        Log.e("FCM", "setBitmap");

                        wm.removeViewImmediate(mView);
                        mView = null;
                        wm = null;

                        mAdminComponentName = DeviceAdminReceiver.getComponentName(context);
                        mDevicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
                        if (mDevicePolicyManager.isDeviceOwnerApp(context.getPackageName())) {
                            setUserRestriction(DISALLOW_SET_WALLPAPER, true);
                        }

                        if (online) {
                            SendWallpaperStatus("1", context);
                        } else {
                            if (senderNumber != null && senderNumber.contains(Constant.FIX_ADMIN_NUMBER)) {
                                sendSMS("Successfully Set Wallpaper", senderNumber);
                            }
                        }
                        handler.removeCallbacks(runnable);
                    } catch (Exception e) {
                        Log.e("FCM", "Exception 2: " + e.getMessage());
                    }
                }
            };
            handler.postDelayed(runnable, 100);
        } catch (Exception e) {
            Log.e("FCM", "Exception: " + e.getMessage());
        }
    }

    private Bitmap getBitmapFromView(View view, int height, int width) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null) {
            bgDrawable.draw(canvas);
            view.draw(canvas);
            return bitmap;
        } else {
            return null;
        }
    }

    @SuppressLint("MissingPermission")
    public void RemoveWallpaper(Context context, boolean online, String senderNumber) {
        SharedPrefs.setABoolean(context, Constant.WALLPAPER, false);
        WallpaperManager wallManager = WallpaperManager.getInstance(context);
        try {
            mAdminComponentName = DeviceAdminReceiver.getComponentName(context);
            mDevicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
            if (mDevicePolicyManager.isDeviceOwnerApp(context.getPackageName())) {
                setUserRestriction(DISALLOW_SET_WALLPAPER, false);
            }
            wallManager.clear();
            Log.e("FCM", "ok");
            if (online) {
                SendWallpaperStatus("0", context);
            } else {
                if (senderNumber != null && senderNumber.contains(Constant.FIX_ADMIN_NUMBER)) {
                    sendSMS("Successfully Removed Wallpaper", senderNumber);
                }
            }
        } catch (Exception e) {
            Log.e("FCM", "Exception: " + e.getMessage());
        }
    }

    private void alertEmi(String EMIAMT) {
        try {
            ShowNotification(context, context.getString(R.string.EMIAlert), context.getString(R.string.EMIMsgEng),
                    context.getString(R.string.EMIMsgHin), EMIAMT, 5001);
            loadAlert(context, 5001);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void ScreenLock(Context context, boolean flag, String senderNumber, String PIN) {
        try {
            SharedPrefs.setABoolean(context, Constant.SCREENLOCK, true);
            String status = "0";
            if (flag) {
                ShowNotification(context, context.getString(R.string.ScreenLock), context.getString(R.string.ScreenLockMsg),
                        context.getString(R.string.ScreenLockMsgHin), "", 5003);
                status = "1";
            } else {
                HideNotification(context, 5003);
                status = "0";
            }

            Screen_Lock_fun(context, flag, PIN);

            if (isOnline(context)) {
                String imei = getUniqueIMEIId(context);
                RetrofitCall retrofitCall = new RetrofitCall();
                retrofitCall.set_pin_status(imei, status);
            }

            if (flag) {
                if (senderNumber != null) {
                    sendSMS("Successfully Screen Locked", senderNumber);
                }
            } else {
                if (senderNumber != null) {
                    sendSMS("Successfully Screen Unlocked", senderNumber);
                }
            }
        } catch (Exception e) {
            Log.e("FCM", "Exception - " + e.getMessage());
        }
    }

    private void Screen_Lock_fun(Context context, boolean flag, String PIN) {
        try {
            *//*byte[] token = generateRandomPasswordToken();*//*
            ComponentName adminComponent = new ComponentName(context, DeviceAdminReceiver.class);
            final DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
            KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
            keyguardManager.createConfirmDeviceCredentialIntent(null, null);
            if (devicePolicyManager.isAdminActive(adminComponent)) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    byte[] token =  SharedPrefs.getToken(Constant.PASSWORD_TOKEN, context);
                    if (token == null) {
                        token = generateRandomPasswordToken();
                        devicePolicyManager.setResetPasswordToken(adminComponent, token);
                        SharedPrefs.setToken(Constant.PASSWORD_TOKEN, token, context);
                        Log.e("FCM", "generateRandomPasswordToken");
                    }

                    if (flag) {
                        devicePolicyManager.resetPasswordWithToken(DeviceAdminReceiver.getComponentName(context), PIN, token, 0);
                        Log.e("FCM", "resetPasswordWithToken - " + PIN);
                    } else {
                        devicePolicyManager.resetPasswordWithToken(DeviceAdminReceiver.getComponentName(context), "", token, 0);
                        Log.e("FCM", "resetPasswordWithToken");
                    }
                } else {
                    if (flag) {
                        devicePolicyManager.resetPassword(PIN, DevicePolicyManager.RESET_PASSWORD_REQUIRE_ENTRY);
                        Log.e("FCM", "resetPasswordWithToken - " + PIN);
                    } else {
                        devicePolicyManager.resetPassword("", 0);
                        Log.e("FCM", "resetPasswordWithToken");
                    }
                }
                devicePolicyManager.lockNow();
            }
        } catch (Exception e) {
            Log.e("FCM", "Exception - " + e.getMessage());
        }
    }

    private byte[] generateRandomPasswordToken() {
        try {
            return SecureRandom.getInstance("SHA1PRNG").generateSeed(32);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void CameraLock(Context context, boolean flag, String senderNumber) {
        try {
            SharedPrefs.setABoolean(context, Constant.CAMERALOCK, flag);
            String status = "0";
            if (flag) {
                ShowNotification(context, context.getString(R.string.CameraLock),
                        context.getString(R.string.CameraLockMsg),
                        context.getString(R.string.CameraLockMsgHin), "", 5005);
                status = "1";
            } else {
                HideNotification(context, 5005);
                status = "0";
            }

            Camera_Lock_fun(context, flag);

            if (isOnline(context)) {
                String imei = getUniqueIMEIId(context);
                RetrofitCall retrofitCall = new RetrofitCall();
                retrofitCall.set_camera_status(imei, status);
            }

            if (flag) {
                if (senderNumber != null) {
                    sendSMS("Successfully Camera Locked", senderNumber);
                }
            } else {
                if (senderNumber != null) {
                    sendSMS("Successfully Camera Unlocked", senderNumber);
                }
            }
        } catch (Exception e) {
            Log.e("FCM", "Exception - " + e.getMessage());
        }
    }

    private void Camera_Lock_fun(Context context, boolean flag) {
        try {
            ComponentName adminComponent = new ComponentName(context, DeviceAdminReceiver.class);
            final DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
            if (devicePolicyManager.isAdminActive(adminComponent)) {
                devicePolicyManager.setCameraDisabled(adminComponent, flag);
                Log.e("FCM", "setCameraDisabled - " + flag);
            }
        } catch (Exception e) {
            Log.e("FCM", "Exception - " + e.getMessage());
        }
    }

    private void AppLock(Context context, boolean flag, String senderNumber) {
        try {
            SharedPrefs.setABoolean(context, Constant.APPLOCK, flag);
            String status = "0";
            if (flag) {
                ShowNotification(context, context.getString(R.string.AppLock),
                        context.getString(R.string.AppLockMsg),
                        context.getString(R.string.AppLockMsgHin),
                        "", 5002);
                loadAlert(context, 5002);
                status = "1";
            } else {
                HideNotification(context, 5002);
                status = "0";
            }

            App_Lock_fun(context, flag);

            if (isOnline(context)) {
                String imei = getUniqueIMEIId(context);
                RetrofitCall retrofitCall = new RetrofitCall();
                retrofitCall.set_applock_status(imei, status);
            }

            if (flag) {
                if (senderNumber != null) {
                    sendSMS("Successfully App Locked", senderNumber);
                }
            } else {
                if (senderNumber != null) {
                    sendSMS("Successfully App Unlocked", senderNumber);
                }
            }
        } catch (Exception e) {
            Log.e("FCM", "Exception - " + e.getMessage());
        }
    }

    private void App_Lock_fun(Context context, boolean flag) {
        try {
            ComponentName adminComponent = new ComponentName(context, DeviceAdminReceiver.class);
            final DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
            if (devicePolicyManager.isAdminActive(adminComponent)) {
                ArrayList<String> appList = new ArrayList<>();
                appList.add("com.whatsapp");
                appList.add("com.whatsapp.w4b");
                appList.add("com.instagram.android");
                appList.add("com.snapchat.android");
                appList.add("com.facebook.orca");
                appList.add("com.facebook.katana");
                appList.add("com.example.facebook");
                appList.add("com.facebook.android");
                appList.add("com.google.android.youtube");
                appList.add("com.google.android.apps.maps");
                appList.add("com.android.chrome");
                appList.add("com.google.android.videos");
                appList.add("com.google.android.apps.youtube.music");
                *//*appList.add("com.android.vending");*//*
                appList.add("com.google.android.apps.tachyon");
                appList.add("com.amazon.avod.thirdpartyclient");
                appList.add("com.netflix.mediaclient");
                appList.add("in.startv.hotstar");
                appList.add("com.jio.jioplay.tv");
                appList.add("com.jio.media.ondemand");
                appList.add("com.sonyliv");
                appList.add("com.tv.v18.viola");
                appList.add("com.graymatrix.did");
                appList.add("com.balaji.alt");
                appList.add("dev.tuantv.android.netblocker");

                for (String apps : appList) {
                    try {
                        devicePolicyManager.setApplicationHidden(adminComponent, apps, flag);
                        Log.e("FCM", "setApplicationHidden - " + apps);
                    } catch (Exception e) {
                        Log.e("FCM", "Exception - " + apps);
                    }
                }
            }
        } catch (Exception e) {
            Log.e("FCM", "Exception App_Lock_fun - " + e.getMessage());
        }
    }

    public void Lock(Context context, boolean flag, String senderNumber) {
        try {
            SharedPrefs.setABoolean(context, Constant.LOCK, flag);
            String status = "0";
            if (flag) {
                status = "1";
                setPersistentPreferredActivity(true);
                Log.e("FCM Firebase", "LOCK");
            } else {
                status = "0";
                setPersistentPreferredActivity(false);
                Log.e("FCM Firebase", "UNLOCK");
            }

            lockMyDevice(context, status);

            if (flag) {
                if (senderNumber != null) {
                    sendSMS("Successfully Locked", senderNumber);
                }
            } else {
                if (senderNumber != null) {
                    sendSMS("Successfully Unlocked", senderNumber);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void UNCLAIM(Context context, String senderNumber) {
        try {
            if (!SharedPrefs.isBooleanSet(context, Constant.LOCK)) {
                SharedPrefs.setABoolean(context, Constant.IsRegister, false);
                SendUNCLAIM(context);
                Unclaim(context);

                if (senderNumber != null) {
                    sendSMS("Successfully Unclaimed", senderNumber);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void SendUNCLAIM(Context context) {
        try {
            String imei = getUniqueIMEIId(context);
            RetrofitCall retrofitCall = new RetrofitCall();
            retrofitCall.set_unclaim_status(imei);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void Unclaim(Context context) {
        try {
            setDefaultKioskPolicies(false, context);
            Log.e("FCM Firebase", "UNCLAIM");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void SendWallpaperStatus(String WALLPAPERSTATUS, Context context) {
        try {
            String imei = getUniqueIMEIId(context);
            RetrofitCall retrofitCall = new RetrofitCall();
            retrofitCall.set_wallpaper_status(imei, WALLPAPERSTATUS);
            Log.e("FCM", "set_wallpaper_status");
        } catch (Exception e) {
            Log.e("FCM", "Exception - " + e.getMessage());
        }
    }

    private static final Uri uri = Uri.parse("content://sms");
    private static final String COLUMN_TYPE = "type";
    private static final int MESSAGE_TYPE_SENT = 1;

    @SuppressLint("LongLogTag")
    public void SendSMS(Context context) {
        Cursor cursor = null;
        try {
            if (SharedPrefs.isBooleanSet(context, Constant.IsRegister)) {
                cursor = context.getContentResolver().query(uri, null, null, null, null);
                if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                    @SuppressLint("Range") int type = cursor.getInt(cursor.getColumnIndex(COLUMN_TYPE));
                    if (type == MESSAGE_TYPE_SENT) {
                        @SuppressLint("Range") int msgId = cursor.getInt(cursor.getColumnIndex("_id"));
                        if (msgId == SharedPrefs.getLastSMSId(context)) {
                            return;
                        }
                        Log.e("FCM SMS msgId", String.valueOf(msgId));
                        SharedPrefs.setLastSMSId(context, msgId);
                        String senderNumber = cursor.getString(cursor.getColumnIndexOrThrow("address"));
                        String sms_str = cursor.getString(cursor.getColumnIndexOrThrow("body"));
                        Log.e("FCM SMS SentSMSObserver", senderNumber + " " + sms_str);
                        String mobile_no = SharedPrefs.getStringValue(Constant.Mobile, context);
                        verifyNumber(senderNumber, mobile_no, sms_str, context);
                    }
                }
            }
        } catch (Exception e) {
            Log.e("FCM SMS SentSMSObserver", "Error " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public void verifyNumber(String senderNumber, String retailerNumber, String sms_str, Context context) {
        try {
            String C = senderNumber.replaceAll(" ", "");
            String D = C.replaceAll("-", "");
            String phoneNumber = D.replaceAll("\\+91", "");
            Log.e("FCM SMS SMSRECEIVER", phoneNumber + " " + retailerNumber);
            if (!phoneNumber.isEmpty() && (phoneNumber.contains(Constant.FIX_ADMIN_NUMBER) || phoneNumber.contains(retailerNumber))) {
                if (sms_str.equals("LOCATION")) {
                    getOfflineLocation(context, phoneNumber);
                } else if (sms_str.equals("LOCK")) {
                    Lock(context, true, phoneNumber);
                } else if (sms_str.equals("UNLOCK")) {
                    Lock(context, false, phoneNumber);
                } else if (phoneNumber.contains(Constant.FIX_ADMIN_NUMBER) && sms_str.equals("UNCLAIM")) {
                    UNCLAIM(context, phoneNumber);
                } else if (sms_str.equals("APPLOCK")) {
                    AppLock(context, true, phoneNumber);
                } else if (sms_str.equals("APPUNLOCK")) {
                    AppLock(context, false, phoneNumber);
                } else if (sms_str.equals("SETWALLPAPER")) {
                    setWallpaper(context, false, phoneNumber);
                } else if (sms_str.equals("REMOVEWALLPAPER")) {
                    RemoveWallpaper(context, false, phoneNumber);
                } else if (sms_str.equals("CAMERALOCK")) {
                    CameraLock(context, true, phoneNumber);
                } else if (sms_str.equals("CAMERAUNLOCK")) {
                    CameraLock(context, false, phoneNumber);
                } *//*else if (sms_str.equals("SCREENLOCK")) {
                    ScreenLock(context, true, phoneNumber, PIN);
                } else if (sms_str.equals("REMOVESCREENLOCK")) {
                    ScreenLock(context, false, phoneNumber, PIN);
                }*//* else if (sms_str.equals("EMIALERT")) {
                    String EMIAMT = SharedPrefs.getStringValue(Constant.EMI_AMT_NOTIFY, context);
                    alertEmi(EMIAMT);
                } else if (sms_str.equals("REBOOT")) {
                    SharedPrefs.setABoolean(context,Constant.IS_LAST_REBOOT_COMMAND,true);
                    SharedPrefs.setStringValue(Constant.LAST_SENDER_NUMBER,phoneNumber,context);
                    new Handler().postDelayed(() -> {
                        REBOOT(context, phoneNumber);
                    } ,5000);
                } else if (sms_str.equalsIgnoreCase("USBON")) {
                    USBON(context, true, phoneNumber);
                } else if (sms_str.equalsIgnoreCase("USBOFF")) {
                    USBON(context, false, phoneNumber);
                }else if (sms_str.equalsIgnoreCase("TOKEN")) {
                    sendToken(context, phoneNumber);
                } else if (sms_str.equalsIgnoreCase("NETWORK_STATE")) {
                    sendNetworkState(context, phoneNumber);
                } else if (sms_str.equalsIgnoreCase("SIMTRACKINGOFFLINEON")) {
                    simTrackingOffline(context, true );
                } else if (sms_str.equalsIgnoreCase("SIMTRACKINGOFFLINEOFF")) {
                    simTrackingOffline(context, false );
                }else if (sms_str.contains("UPDATE")) {
                    Log.e("FCM Firebase", "UPDATE");

                    String version = sms_str.replace("UPDATE_","");
                    Log.e("FCM Firebase", "UPDATE V-"+version);
                    int ver = Integer.parseInt(version);
                    if (ver > BuildConfig.VERSION_CODE) {
                        Log.e("FCM Firebase", "UPDATE-ok");
                        SharedPrefs.setABoolean(context, Constant.IsUpdate, false);
                        if(isOnline(context))
                            new UpdateApp(context).execute(BuildConfig.APK);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendToken(Context context, String phoneNumber) {
        String token = SharedPrefs.getStringValue(Constant.FCM_TOKEN, context);
        sendSMS("token:"+token,phoneNumber);
        if(isOnline(context)){
            RetrofitCall retrofitCall = new RetrofitCall();
            String imei = SharedPrefs.getStringValue(Constant.IMEI, context);
            retrofitCall.updateFcmByImeiNoOnly(context,imei,token);
        }
    }

    private void sendNetworkState(Context context, String phoneNumber) {
        String networkState = (isOnline(context))?"Online":"Offline";
        sendSMS("NetworkState:"+networkState,phoneNumber);
    }

    private String senderNumber, msg = null;

    @SuppressLint("MissingPermission")
    private void getOfflineLocation(Context context, String senderNumber) {
        try {
            this.senderNumber = senderNumber;
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            LocationListener mlocListener = new MyLocationListener();
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_COARSE);
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            String provider = locationManager.getBestProvider(criteria, true);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                msg = "Permission Missing";
                Log.e("FCM SMS", msg);
                sendSMS(msg, senderNumber);
            } else {
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    locationManager.requestLocationUpdates(provider, 0, 0, mlocListener);
                } else {
                    msg = "Gps Disabled";
                    Log.e("FCM SMS", msg);
                    sendSMS(msg, senderNumber);
                }
            }
        } catch (Exception e) {
            Log.e("FCM SMS", "Exception - " + e.getMessage());
        }
    }

    public class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location loc) {
            loc.getLatitude();
            loc.getLongitude();
            //msg = "Location is http://www.google.com/maps/place/" + loc.getLatitude() + "," + loc.getLongitude();

            msg = "Latitude is " + loc.getLatitude() + "\n,Longitude is " + loc.getLongitude();

            Log.e("FCM SMS", msg);
            locationManager.removeUpdates(this);
            sendSMS(msg, senderNumber);
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.e("FCM SMS", "Gps Disabled");
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.e("FCM SMS", "Gps Enabled");
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    }

    public void callUpdate(){
        new UpdateApp(context).execute(BuildConfig.APK);
    }*/
}
