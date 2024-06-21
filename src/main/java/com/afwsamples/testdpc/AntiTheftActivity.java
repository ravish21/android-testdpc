package com.afwsamples.testdpc;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;


import com.afwsamples.testdpc.util.Constant;
import com.afwsamples.testdpc.util.SharedPrefs;

public class AntiTheftActivity extends Activity {
    private ImageButton privacy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anti_theft);

        privacy = findViewById(R.id.privacy);

        privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String token = SharedPrefs.getStringValue(Constant.FCM_TOKEN, AntiTheftActivity.this);
                    if (!token.isEmpty()) {
                        new AlertDialog.Builder(AntiTheftActivity.this)
                                .setIcon(R.drawable.app_icon)
                                .setTitle(getString(R.string.FCMToken))
                                .setMessage(token)
                                .setCancelable(false)
                                .setNegativeButton("Copy", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                                        ClipData clip = ClipData.newPlainText("Code Copied", token);
                                        clipboard.setPrimaryClip(clip);
                                        Toast.makeText(AntiTheftActivity.this, "Code Copied", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .setPositiveButton(getString(R.string.Ok), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .show();
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}