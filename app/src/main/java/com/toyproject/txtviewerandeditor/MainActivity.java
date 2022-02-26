package com.toyproject.txtviewerandeditor;

import android.Manifest;
import android.app.AlertDialog;
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

import androidx.annotation.NonNull;
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
import com.toyproject.txtviewerandeditor.moduel.dialog_layout_manager.BuilderThemeInit;

// TODO: 2022-02-17 테마 다듬기 
public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    String theme;

    ContentMainBinding contentMainBinding;
    ConstraintLayout constraintLayout;
    private DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        theme = initTheme(sharedPreferences.getString(getString(R.string.theme), null), sharedPreferences.edit());

        // Dark Theme 을 지원하는 버전이면  Activity 시작 전 Theme 변경 후 재시작
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            setThemeFromAPI29(theme);

        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.appBarMain.toolbar);

        contentMainBinding = binding.appBarMain.contentMain;
        constraintLayout = contentMainBinding.getRoot();
        drawerLayout = binding.drawerLayout;
        navigationView = binding.navView;
        toolbar = binding.appBarMain.toolbar;

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            setThemeUnderAPI29(theme);
        }

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_viewer_and_editor, R.id.nav_file_explorer, R.id.nav_setting)
                .setOpenableLayout(drawerLayout)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // 권한 획득
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                popUpAlertDialogPermission();
            }
        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            popUpAlertDialogPermission();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        // NavigationDrawer 가 열려있다면 BackButton 이 눌렸을 때 닫음
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // 권한 거부 시 'Don't ask again' 을 체크한 상태이면 안내 후 종료
        if(grantResults[0] == PackageManager.PERMISSION_DENIED) {
            if (!(shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)
                    & shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE))) {
                popUpAlertDialogCantUseWithoutPermission();
            } else {
                finishAffinity();
                /*
                System.runFinalization();
                System.exit(0);
                 */
            }
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
        // 권한 획득이 필요함을 안내하는 AlertDialog
        android.app.AlertDialog.Builder builder = BuilderThemeInit.init(this);
        builder.setTitle("Need permission!")
                .setMessage("Need all file access permission to open txt files.");
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finishAffinity();
                /*
                System.runFinalization();
                System.exit(0);
                 */
            }
        });
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
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
        android.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void popUpAlertDialogCantUseWithoutPermission() {
        // 'Don't ask again' 을 선택하고 권한 Deny 시 권한없인 어플 사용 불가함을 안내하는 AlertDialog
        android.app.AlertDialog.Builder builder = BuilderThemeInit.init(this);
        builder.setTitle("Need Permission!")
                .setMessage("You choose 'Don't ask again'.\n" +
                        "You can't use this application without 'all file access' permission.\n" +
                        "To use this application, please go to 'Settings>>Apps>>txtViewerAndEditor' and allow the permission.");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
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
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public String initTheme(String presentTheme, SharedPreferences.Editor editor) {
        if (presentTheme == null) {
            presentTheme = getString(R.string.theme_dark);
            editor.putString(getString(R.string.theme), getString(R.string.theme_dark));
            editor.apply();
        }

        return presentTheme;
    }

    public void setThemeFromAPI29(String presentTheme) {
        if (presentTheme.equals(getString(R.string.theme_dark))) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else if (presentTheme.equals(getString(R.string.theme_light))) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

    }
    public void setThemeUnderAPI29(String presentTheme) {
        if (presentTheme.equals(getString(R.string.theme_dark))) {
            getWindow().setStatusBarColor(getColor(R.color.primary_variant_dark));

            toolbar.setBackgroundColor(getColor(R.color.primary_dark));

            constraintLayout.setBackgroundColor(getColor(R.color.background_dark));

            navigationView.setBackgroundColor(getColor(R.color.nav_background_dark));
            navigationView.setItemTextColor(getColorStateList(R.color.color_state_list_dark));
            navigationView.setItemIconTintList(getColorStateList(R.color.color_state_list_dark));
            navigationView.setItemBackground(getDrawable(R.drawable.nav_view_item_background_dark));
            //setTheme(R.style.Theme_Dark_TxtViewerAndEditor);
        } else if (presentTheme.equals(getString(R.string.theme_light))) {
            getWindow().setStatusBarColor(getColor(R.color.primary_variant_light));

            toolbar.setBackgroundColor(getColor(R.color.primary_light));

            constraintLayout.setBackgroundColor(getColor(R.color.background_light));

            navigationView.setBackgroundColor(getColor(R.color.nav_background_light));
            navigationView.setItemTextColor(getColorStateList(R.color.color_state_list_light));
            navigationView.setItemIconTintList(getColorStateList(R.color.color_state_list_light));
            navigationView.setItemBackground(getDrawable(R.drawable.nav_view_item_background_light));
            //setTheme(R.style.Theme_Light_TxtViewerAndEditor);
        }
    }

}