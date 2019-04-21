package com.jsync.notificationlistener;



import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnPermission, btnHide;
    private TextView txtStatus;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtStatus = (TextView) findViewById(R.id.txt_status);
        btnHide = (Button) findViewById(R.id.btn_hide);
        btnPermission = (Button) findViewById(R.id.btn_permission);

        btnPermission.setOnClickListener(this);
        btnHide.setOnClickListener(this);

    }


    private boolean isNotificationAccessEnabled(){
        return Settings.Secure.getString(getContentResolver(),"enabled_notification_listeners").
                contains(getApplicationContext().getPackageName());
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.btn_hide:
                PackageManager p = getPackageManager();
                ComponentName componentName = new ComponentName(this,MainActivity.class);
                p.setComponentEnabledSetting(componentName,PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

                break;

            case R.id.btn_permission:
                if (isNotificationAccessEnabled())
                {
                    Log.i("Notif","Permission granted");
                    txtStatus.setText("Permission is granted now you can hide");
                    btnHide.setEnabled(true);
                    btnPermission.setVisibility(View.GONE);
                }else{
                    startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS));
                }
                break;
        }
    }
}
