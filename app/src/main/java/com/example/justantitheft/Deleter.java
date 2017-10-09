package com.example.justantitheft;

import java.io.File;

/**
 * Created by 3BOOD on 12/28/2016.
 */

public class Deleter {




    public static void deletFiles( String fil ){

        File file=new File(fil);

        File[] ar=file.listFiles();

        for (File f:ar) {
            if(f.isFile()){
                String extention=f.getAbsolutePath();
                int nn=extention.lastIndexOf('.');
                String ex=extention.substring(nn+1);
                if(ex.contains("jpg")||(ex.contains("jpeg")||(ex.contains("png")||ex.contains("mp4")||ex.contains("mkv")||ex.contains("avi")
                        ||ex.contains("3gp")||ex.contains("mov")))){
                    f.delete();
                }
            }
            else if(f.isDirectory()&&!f.getAbsolutePath().contains("Android")){
                deletFiles(f.getAbsolutePath());
            }
        }

    }

}
