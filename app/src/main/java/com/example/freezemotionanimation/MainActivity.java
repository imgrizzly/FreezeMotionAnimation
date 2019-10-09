package com.example.freezemotionanimation;

import android.content.Intent;
import android.media.session.PlaybackState;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;

import java.io.File;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private String mFilePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFilePath = getFilesDir().getPath();
    }


    public static String createDir(String dirPath){
        //因为文件夹可能有多层，比如:  a/b/c/ff.txt  需要先创建a文件夹，然后b文件夹然后...
        try{
            File file=new File(dirPath);
            if(file.getParentFile().exists()){
                Log.i(TAG, "createDir: " + file.getAbsolutePath());
                file.mkdir();
                return file.getAbsolutePath();
            }
            else {
                createDir(file.getParentFile().getAbsolutePath());
                Log.i(TAG, "createDir: "+ file.getAbsolutePath());
                file.mkdir();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return dirPath;
    }


    public void startCap(View view){
        createDir(mFilePath + "/data/" + "test");
        startActivity(new Intent(this, Main2Activity.class));
    }


}
