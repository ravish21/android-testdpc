package com.afwsamples.testdpc.util;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class SharedPrefs {
    private static String QR_YOGIC_Prefs = "QR_YOGIC_Prefs";
    public static void setABoolean(Context context, String param, boolean value){
        try {
            SharedPreferences prefs = context.getSharedPreferences(QR_YOGIC_Prefs, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(param, value);
            editor.apply();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static boolean isBooleanSet(Context context, String param) {
        try {
            SharedPreferences prefs = context.getSharedPreferences(QR_YOGIC_Prefs, Context.MODE_PRIVATE);
            return prefs.getBoolean(param, false);
        }catch (Exception e){
            return false;
        }
    }

    public static void setStringValue(String key, String value, Context context) {
        try {
            SharedPreferences pSharedPref = context.getSharedPreferences(QR_YOGIC_Prefs, Context.MODE_PRIVATE);
            SharedPreferences.Editor mSharedPreferencesEditor = pSharedPref.edit();
            mSharedPreferencesEditor.putString(key, value);
            mSharedPreferencesEditor.apply();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static String getStringValue(String key, Context context) {
        try {
            SharedPreferences pSharedPref = context.getSharedPreferences(QR_YOGIC_Prefs, Context.MODE_PRIVATE);
            return pSharedPref.getString(key, "");
        }catch (Exception e){
            return "";
        }
    }

    public static int getLastSMSId(Context context) {
        try {
            SharedPreferences prefs = context.getSharedPreferences(QR_YOGIC_Prefs, MODE_PRIVATE);
            return prefs.getInt("LastSMSId", -1);
        } catch (Exception e){
            return -1;
        }
    }
    public static void setLastSMSId(Context context, int smsId){
        try {
            SharedPreferences prefs = context.getSharedPreferences(QR_YOGIC_Prefs, MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("LastSMSId", smsId);
            editor.apply();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void setIntValue(String key, int value, Context context) {
        try {
            SharedPreferences pSharedPref = context.getSharedPreferences(QR_YOGIC_Prefs, Context.MODE_PRIVATE);
            SharedPreferences.Editor mSharedPreferencesEditor = pSharedPref.edit();
            mSharedPreferencesEditor.putInt(key, value);
            mSharedPreferencesEditor.apply();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static int getIntValue(String key, Context context) {
        try {
            SharedPreferences pSharedPref = context.getSharedPreferences(QR_YOGIC_Prefs, Context.MODE_PRIVATE);
            return pSharedPref.getInt(key, 0);
        }catch (Exception e){
            return 0;
        }
    }

    public static void setABooleanActivate(Context context, String param, boolean value){
        try {
            SharedPreferences prefs = context.getSharedPreferences(QR_YOGIC_Prefs, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(param, value);
            editor.apply();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static boolean isBooleanSetActivate(Context context, String param) {
        try {
            SharedPreferences prefs = context.getSharedPreferences(QR_YOGIC_Prefs, Context.MODE_PRIVATE);
            return prefs.getBoolean(param, true);
        }catch (Exception e){
            return false;
        }
    }

    public static String getLastMessageId(Context context) {
        try {
            SharedPreferences prefs = context.getSharedPreferences(QR_YOGIC_Prefs, MODE_PRIVATE);
            return prefs.getString("LastMessageId", "");
        } catch (Exception e){
            return "";
        }
    }

    public static void setLastMessageId(Context context, String MessageId){
        try {
            SharedPreferences prefs = context.getSharedPreferences(QR_YOGIC_Prefs, MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("LastMessageId", MessageId);
            editor.apply();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void setToken(String PASSWORD_TOKEN, byte[] token, Context context) {
        try {
            SharedPreferences prefs = context.getSharedPreferences(QR_YOGIC_Prefs, MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            if (token != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    editor.putString(PASSWORD_TOKEN, Base64.getEncoder().encodeToString((token)));
                }
            } else {
                editor.remove(PASSWORD_TOKEN);
            }
            editor.commit();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static byte[] getToken(String PASSWORD_TOKEN, Context context) {
        try {
            SharedPreferences prefs = context.getSharedPreferences(QR_YOGIC_Prefs, MODE_PRIVATE);
            String tokenString = prefs.getString(PASSWORD_TOKEN, null);
            if (tokenString != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    return Base64.getDecoder().decode(tokenString.getBytes(StandardCharsets.UTF_8));
                }
            } else {
                return null;
            }
        } catch (Exception e){

        }
        return null;
    }

    public static String getStringValue(String key, String defaultValue, Context context) {
        SharedPreferences pSharedPref = context.getSharedPreferences(QR_YOGIC_Prefs, Context.MODE_PRIVATE);
        return pSharedPref.getString(key, defaultValue);
    }

    public static void putStringValue(String key, String value, Context context) {
        SharedPreferences pSharedPref = context.getSharedPreferences(QR_YOGIC_Prefs, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pSharedPref.edit();
        editor.putString(key, value);
        editor.apply();
    }
}
