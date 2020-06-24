package com.codingbjs.happynote;

import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

public class MainActivity extends AppCompatActivity {

    FirebaseRemoteConfig remoteConfig;
    long newAppVersion = 1;
    long toolbarImgCount = 15;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getRemoteConfig();
    }


    private void getRemoteConfig() {
        remoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .build();
        remoteConfig.setConfigSettingsAsync(configSettings);
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults);

        remoteConfig.fetchAndActivate()
                .addOnCompleteListener(this, new OnCompleteListener<Boolean>() {
                    @Override
                    public void onComplete(@NonNull Task<Boolean> task) {
                        newAppVersion = remoteConfig.getLong("new_app_version");
                        toolbarImgCount = remoteConfig.getLong("toolbar_img_count");
                        checkVersion();
                    }
                });
    }

    private void checkVersion() {
        try {
            PackageInfo pi = getPackageManager().getPackageInfo(getPackageName(), 0);
            long appVersion;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                appVersion = pi.getLongVersionCode();
            } else {
                appVersion = pi.versionCode;
            }

            if (newAppVersion > appVersion) {
                updateDialog();
                return;
            }

            // TODO: 2020-06-24 툴바 이미지 다운로드 메소드 정의
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }



    private void updateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("업데이트 알림.");
        builder.setMessage("최신버전이 등록되었습니다.\n업데이트 하세요.")
                .setCancelable(false)
                .setPositiveButton("업데이트", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                                    Intent intent = new Intent(Intent.ACTION_VIEW);
//                                    intent.setData(Uri.parse("market://details?id=com.codingbjs.happynote"));
//                                    startActivity(intent);
                        Toast.makeText(getApplicationContext(), "업데이트 버튼 클릭됨.", Toast.LENGTH_SHORT).show();
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}