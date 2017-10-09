package com.example.justantitheft;


import android.content.Context;
import android.hardware.Camera;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PhotoHandler implements Camera.PictureCallback {

    private Context context;
    public PhotoHandler(Context context){
        this.context=context;
    }

    @Override
    public void onPictureTaken(byte[] bytes, Camera camera) {
        File pictureFileDir = getDir();

        if (!pictureFileDir.exists() && !pictureFileDir.mkdirs()) {

            pictureFileDir.mkdir();
            return;

        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyymmddhhmmss");
        String date = dateFormat.format(new Date());
        String photoFile = "Picture_" + date + ".jpg";

        String filename = pictureFileDir.getPath() + File.separator + "1.jpg";
        File pictureFile = new File(filename);

        FileOutputStream fos = null;
        try {
            fos=new FileOutputStream(pictureFile);
            fos.write(bytes);
            fos.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private File getDir() {

        File sdDir;
        if (isExternalStorageReadable()) {
             sdDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        } else{
            sdDir=new File("/storage/emulated/0/Download");
        }

        return new File(sdDir,"Camera API");
    }
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }
}
