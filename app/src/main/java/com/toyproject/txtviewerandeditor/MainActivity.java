package com.toyproject.txtviewerandeditor;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.toyproject.txtviewerandeditor.databinding.ActivityMainBinding;
import com.toyproject.txtviewerandeditor.databinding.ContentMainBinding;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.appBarMain.toolbar);

        drawerLayout = binding.drawerLayout;
        navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_viewer_and_editor, R.id.nav_file_explorer, R.id.nav_setting)
                .setOpenableLayout(drawerLayout)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String presentTheme = sharedPreferences.getString(getString(R.string.theme), null);
        setTheme(presentTheme, editor);
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                popUpAlertDialogPermission();
            }
        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!(shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)
                        & shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE))) {
                    popUpAlertDialogCantUseWithoutPermission();
                } else {
                    popUpAlertDialogPermission();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        // NavigationDrawer가 열려있다면 BackButton이 눌렸을 때 닫음
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers();
        } else {
            super.onBackPressed();
        }
    }

    public void popUpAlertDialogPermission() {
        /*
        // layout을 통해 AlertDialog를 구현하는 경우 사용
        LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.alert_dialog_permission, null);

        Button dialogButton = (Button) view.findViewById(R.id.button_dialog);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, Uri.parse("package:" + BuildConfig.APPLICATION_ID));
                startActivity(intent);
            }
        });
        */
        
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("권한필요!")
                .setMessage("txt 파일을 열기 위해 파일 접근 권한이 필요합니다.");
        builder.setNegativeButton("싫어요", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finishAffinity();
                /*
                System.runFinalization();
                System.exit(0);
                 */
            }
        });
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, Uri.parse("package:" + BuildConfig.APPLICATION_ID));
                    startActivity(intent);
                } else {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                }
            }
        });
        builder.setCancelable(false);
        builder.show();
    }

    public void popUpAlertDialogCantUseWithoutPermission() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("권한필요!")
                .setMessage("Don't ask again을 선택 하셨습니다.\n" +
                        "파일 접근 권한 없이는 어플 사용이 불가 합니다.\n" +
                        "어플 사용을 위해 Settings>>Apps>>txtViewerAndEditor에 가셔서 파일 접근 권한을 설정해 주세요.");
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finishAffinity();
                /*
                System.runFinalization();
                System.exit(0);
                 */
            }
        });
        builder.setCancelable(false);
        builder.show();
    }

    public void setTheme(String presentTheme, SharedPreferences.Editor editor) {
        ContentMainBinding contentMainBinding = binding.appBarMain.contentMain;

        Toolbar toolbar;
        ConstraintLayout constraintLayout;

        toolbar = binding.appBarMain.toolbar;
        constraintLayout = contentMainBinding.getRoot();

        if (presentTheme == null) {
            presentTheme = getString(R.string.theme_dark);
            editor.putString(getString(R.string.theme), getString(R.string.theme_dark));
            editor.apply();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (presentTheme.equals(getString(R.string.theme_dark))) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else if (presentTheme.equals(getString(R.string.theme_light))) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        } else {
            if (presentTheme.equals(getString(R.string.theme_dark))) {
                toolbar.setBackgroundColor(getColor(R.color.primary_dark));
                constraintLayout.setBackgroundColor(getColor(R.color.background_dark));
                getWindow().setStatusBarColor(getColor(R.color.primary_variant_dark));
                navigationView.setBackgroundColor(getColor(R.color.background_dark));
                navigationView.setItemTextColor(getColorStateList(R.color.color_state_list_dark));
                navigationView.setItemIconTintList(getColorStateList(R.color.color_state_list_dark));
                navigationView.setItemBackground(getDrawable(R.drawable.nav_view_item_background_dark));
                //setTheme(R.style.Theme_Dark_TxtViewerAndEditor);
            } else if (presentTheme.equals(getString(R.string.theme_light))) {
                toolbar.setBackgroundColor(getColor(R.color.primary_light));
                constraintLayout.setBackgroundColor(getColor(R.color.background_light));
                getWindow().setStatusBarColor(getColor(R.color.primary_variant_light));
                navigationView.setBackgroundColor(getColor(R.color.background_light));
                navigationView.setItemTextColor(getColorStateList(R.color.color_state_list_light));
                navigationView.setItemIconTintList(getColorStateList(R.color.color_state_list_light));
                navigationView.setItemBackground(getDrawable(R.drawable.nav_view_item_background_light));
                //setTheme(R.style.Theme_Light_TxtViewerAndEditor);
            }
        }
    }
}