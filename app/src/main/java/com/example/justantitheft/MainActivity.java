package com.example.justantitheft;

import android.*;
import android.Manifest;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static Camera camera;
    public static int cameraId = 0;
    Button savebtn;
    SharedPreferences sharedPreferences;
    EditText email,monitoringEmail,password,deleteEdittext,logedittext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        savebtn= (Button) findViewById(R.id.savebtn);
        savebtn.setOnClickListener(this);
        sharedPreferences=getSharedPreferences("MyData",MODE_PRIVATE);
        email= (EditText) findViewById(R.id.emaileditt1);
        monitoringEmail= (EditText) findViewById(R.id.emaileditt2);
        password= (EditText) findViewById(R.id.passwordeditt);
        deleteEdittext = (EditText) findViewById(R.id.deleditText);
        logedittext= (EditText) findViewById(R.id.logedittext);
        AdminReceiver.context=getApplicationContext();
        AdminReceiver.context=this;

        ComponentName cn=new ComponentName(this, AdminReceiver.class);
        DevicePolicyManager mgr=
                (DevicePolicyManager)getSystemService(DEVICE_POLICY_SERVICE);
        if (!mgr.isAdminActive(cn)) {
            Intent intent=
                    new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, cn);
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                    getString(R.string.device_admin_explanation));
            startActivity(intent);
        }

        if((ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED)&&
                (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)&&
                (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED)&&
                (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_CALL_LOG)!=PackageManager.PERMISSION_GRANTED)){
            ActivityCompat.requestPermissions(this,new String[]{
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.CAMERA,
                    android.Manifest.permission.WRITE_CALL_LOG, Manifest.permission.RECEIVE_BOOT_COMPLETED},101);
        }



        initializeCamera();
        if(isConnected()) {
            Intent mpasIntent = new Intent(getApplicationContext(), MapsService.class);
            startService(mpasIntent);

            Intent pushIntent = new Intent(this, EmailService.class);
            SharedPreferences sharedPreferencess = getSharedPreferences("MyData", MODE_PRIVATE);
            String minemail = sharedPreferencess.getString("moniEmail", "N/A");
            String monpass = sharedPreferencess.getString("password", "N/A");
            if (minemail != "N/A" && monpass != "N/A" && minemail != "" && monpass != "") {
                pushIntent.putExtra("email", minemail);
                pushIntent.putExtra("password", monpass);
                startService(pushIntent);
            }
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("counter", 0);
            editor.commit();
        }
        else {
            Toast.makeText(this,"No network Connection",Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length>0){
            initializeCamera();
        }
    }
    private void initializeCamera(){
        cameraId = findFrontFacingCamera();
        camera= Camera.open(cameraId);
    }
    private int findFrontFacingCamera() {
        int cameraId = -1;
        // Search for the front facing camera
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                cameraId = i;
                break;
            }
        }
        return cameraId;
    }

    @Override
    public void onClick(View view) {
        if(isConnected()){
        SharedPreferences.Editor e=sharedPreferences.edit();
        String emailtext=email.getText().toString();
        String monemail=monitoringEmail.getText().toString();
        String pass=password.getText().toString();
        String delKey=deleteEdittext.getText().toString();
        String dellog=logedittext.getText().toString();

        if(emailtext.equals("")||(monemail.equals(""))||(pass.equals(""))||(delKey.equals(""))||dellog.equals("")){
            Toast.makeText(this,"Please fill all Textboxes",Toast.LENGTH_SHORT).show();
        }
        else
        if(!emailtext.contains("gmail")||!monemail.contains("gmail")){
            Toast.makeText(this,"Please enter a valid G-Mail account",Toast.LENGTH_SHORT).show();
        }else {
            e.putString("email",emailtext);
            e.putString("moniEmail",monemail);
            e.putString("password",pass);
            e.putString("delkey",delKey);
            e.putString("dellog",dellog);
            e.commit();
            Toast.makeText(this,"Saved",Toast.LENGTH_SHORT).show();

        }
        e.commit();
        String minemail=sharedPreferences.getString("moniEmail","N/A");
        String monpass=sharedPreferences.getString("password","N/A");
        if(minemail!="N/A"&&monpass!="N/A"&&minemail!=""&&monpass!=""){
            Intent pushIntent = new Intent(this, EmailService.class);
            pushIntent.putExtra("email", minemail);
            pushIntent.putExtra("password", monpass);
            startService(pushIntent);
             }
        }
        else
        {
            Toast.makeText(this,"No network Connection",Toast.LENGTH_SHORT).show();
        }
    }
    public boolean isConnected(){
        ConnectivityManager cm =
                (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }
}
