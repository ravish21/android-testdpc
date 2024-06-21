package com.afwsamples.testdpc;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import com.afwsamples.testdpc.util.Constant;
import com.afwsamples.testdpc.util.SharedPrefs;
import com.afwsamples.testdpc.util.CommonUtil;

public class SettingActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        checkFRP(this);
    }
    private DevicePolicyManager mDevicePolicyManager;
    private ComponentName mAdminComponentName;

    public void checkFRP(Context context) {
        if (!SharedPrefs.isBooleanSet(context, Constant.IsRegister)) {
            try {
                mDevicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
                mAdminComponentName = DeviceAdminReceiver.getComponentName(context);
                try {
                    Bundle restrictions = mDevicePolicyManager.getApplicationRestrictions(mAdminComponentName, "com.google.android.gms");
                    String account_id = restrictions.getString("factoryResetProtectionAdmin");
                    Log.e("factory", "factoryResetProtectionAdmin " + account_id);
                    if(account_id.equals(Constant.GOOGLE_ACCOUNT_1)){
                        SharedPrefs.setStringValue(Constant.IT_ADMIN_EMAIL, Constant.GOOGLE_ACCOUNT, context);
                        if (!SharedPrefs.isBooleanSet(getApplicationContext(), Constant.IS_AUTO_GRANT)) {
                            new GrantPermissionAsync().execute();
                        } else {
                            load();
                        }
                    } else {
                        if(SharedPrefs.isBooleanSet(context, Constant.IsRemove)){
                            callAppInfo();
                        } else {
                            callContact();
                        }
                    }
                } catch (Exception e) {
                    if(SharedPrefs.isBooleanSet(context, Constant.IsRemove)){
                        callAppInfo();
                    } else {
                        callContact();
                    }
                }
            } catch (Exception e) {
                callContact();
            }
        } else {
            callContact();
        }
    }

    private class GrantPermissionAsync extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected Void doInBackground(Void... voids) {
            //PERMISSION GRANT
            try {
                CommonUtil commonUtil = new CommonUtil(SettingActivity.this);
                ComponentName adminComponent = DeviceAdminReceiver.getComponentName(SettingActivity.this);
                DevicePolicyManager devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
                if (devicePolicyManager.isDeviceOwnerApp(getPackageName())) {
                    //PERMISSION GRANT
                    commonUtil.grantPermission(devicePolicyManager, adminComponent);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void o) {
            super.onPostExecute(o);
            //NEWCHANGE-1
            SharedPrefs.setABoolean(getApplicationContext(), Constant.IS_AUTO_GRANT, true);
            load();
        }
    }

    private void callAppInfo() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
        finish();
    }

    private void callContact() {
        try {
            Intent intent = new Intent(SettingActivity.this, AntiTheftActivity.class);
            startActivity(intent);
        } catch (Exception e){
            e.printStackTrace();
        }
        if (SharedPrefs.isBooleanSet(SettingActivity.this, Constant.IsRegister)) {
            mAdminComponentName = DeviceAdminReceiver.getComponentName(SettingActivity.this);
            mDevicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
            if (mDevicePolicyManager.isDeviceOwnerApp(getPackageName())) {
                mDevicePolicyManager.setStatusBarDisabled(mAdminComponentName, false);

                CommonUtil commonUtil = new CommonUtil(getApplicationContext());
                commonUtil.UpdateVersionPlayerID();
            }
        }
        finish();
    }
    private void load() {
//        RetrofitCall retrofitCall = new RetrofitCall();
//        retrofitCall.GetIMEI(SettingActivity.this, false, false);

        callContact();
    }
}
