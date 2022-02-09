package com.toyproject.txtviewerandeditor;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.provider.Settings;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.toyproject.txtviewerandeditor.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    // TODO: 2022-02-03 "back"버튼 클릭시 네비게이션뷰 닫기
    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.appBarMain.toolbar);

        // TODO: 2022-02-06 모든 파일 접근 권한 설명 및 요청하는 popup 창 구현
        // TODO: 2022-02-06 권한 획득이 안드로이드 11미만 버전에서도 작동하는지 확인
        //권한을 가지고 있지 않다면 모든 파일 접근 권한 획득할 수 있도록 함
        if (!Environment.isExternalStorageManager()) {
            /*
            LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.alert_dialog_permission, null);
            */

            /*
            Button dialogButton = (Button) view.findViewById(R.id.button_dialog);
            dialogButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, Uri.parse("package:" + BuildConfig.APPLICATION_ID));
                    startActivity(intent);
                }
            });*/

            AlertDialog.Builder alertDialogPermission = new AlertDialog.Builder(MainActivity.this);
            alertDialogPermission.setTitle("권한필요!")
                    .setMessage("txt 파일을 열기 위해 파일 접근 권한이 필요합니다.");
            alertDialogPermission.setNegativeButton("싫어요", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finishAffinity();
                    System.runFinalization();
                    System.exit(0);
                }
            });
            alertDialogPermission.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, Uri.parse("package:" + BuildConfig.APPLICATION_ID));
                    startActivity(intent);
                }
            });
            alertDialogPermission.show();
        }

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_view_and_edit, R.id.nav_file_explore, R.id.nav_setting)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
     */

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!Environment.isExternalStorageManager()) {
            AlertDialog.Builder alertDialogPermission = new AlertDialog.Builder(MainActivity.this);
            alertDialogPermission.setTitle("권한필요!")
                    .setMessage("txt 파일을 열기 위해 파일 접근 권한이 필요합니다.");
            alertDialogPermission.setNegativeButton("싫어요", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finishAffinity();
                    System.runFinalization();
                    System.exit(0);
                }
            });
            alertDialogPermission.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, Uri.parse("package:" + BuildConfig.APPLICATION_ID));
                    startActivity(intent);
                }
            });
            alertDialogPermission.show();
        }
    }
}