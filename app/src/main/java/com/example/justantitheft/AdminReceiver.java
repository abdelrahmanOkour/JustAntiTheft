package com.example.justantitheft;

import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.CallLog;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by 3BOOD on 12/23/2016.
 */

public class AdminReceiver extends DeviceAdminReceiver {

    private Camera camera;
    private int cameraId = 0;
    public static Context context;
    public ArrayList<File> files = new ArrayList<>();
    public static String userEmail;

    public static void setContext(Context cont) {
        context = cont;
    }


    public static void dellog() {
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.WRITE_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        context.getContentResolver().delete(CallLog.Calls.CONTENT_URI, null, null);
    }

    @Override
    public void onPasswordFailed(Context context, Intent intent) {

        SharedPreferences s=this.context.getSharedPreferences("MyData",Context.MODE_PRIVATE);
        int counter=s.getInt("counter",0);
        SharedPreferences.Editor editor=s.edit();
        if(counter==2) {
            if(isConnected()) {
                initializeCamera();
                camera.startPreview();
                camera.takePicture(null, null, new PhotoHandler(context));


                final String username = s.getString("email", "");
                final String filePath;
                if (isExternalStorageReadable()) {
                    filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath()
                            + File.separator + "Camera API" + File.separator + "1.jpg";
                } else {
                    filePath = "/storage/emulated/0/Download/1.jpg";
                }
                File file = new File(filePath);
                final String ltd = s.getString("lat", "");
                final String lng = s.getString("lon", "");

                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        EmailSender sender = new EmailSender(username, filePath, new LatLng(Double.parseDouble(ltd), Double.parseDouble(lng)));
                        sender.sendEmail();

                    }
                });
                t.start();
                counter = 0;
                editor.putInt("counter", counter);
                editor.commit();
            }
        }
        else {
            counter++;
            editor.putInt("counter",counter);
            editor.commit();
        }
    }

    private void initializeCamera(){
        cameraId = findFrontFacingCamera();
        camera=Camera.open(cameraId);

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
    public void onEnabled(Context context, Intent intent) {
        ComponentName cn=new ComponentName(context, AdminReceiver.class);
        DevicePolicyManager mgr=
                (DevicePolicyManager)context.getSystemService(Context.DEVICE_POLICY_SERVICE);

        mgr.setPasswordQuality(cn,
                DevicePolicyManager.PASSWORD_QUALITY_ALPHANUMERIC);

        onPasswordChanged(context, intent);
    }
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    public boolean isConnected(){
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }
}
