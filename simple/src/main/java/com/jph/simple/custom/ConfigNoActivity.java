package com.jph.simple.custom;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.jph.simple.R;
import com.jph.takephoto.app.TakePhoto;
import com.jph.takephoto.app.TakePhotoActivity;
import com.jph.takephoto.compress.CompressConfig;
import com.jph.takephoto.model.TResult;

import java.io.File;

/**
 * Created by Tao on 2016/10/21.
 */

public class ConfigNoActivity extends TakePhotoActivity implements View.OnClickListener{

    private Button btn_pick_take,btn_pick_select;
    private ImageView imgShow_task;
    private TakePhoto takePhoto;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.config_no);
        initView();
        takePhoto = getTakePhoto();//2. 通过getTakePhoto()获取TakePhoto实例进行相关操作。
    }
    private void initView(){
        btn_pick_take = (Button) findViewById(R.id.btn_pick_take);
        btn_pick_select = (Button) findViewById(R.id.btn_pick_select);
        imgShow_task = (ImageView) findViewById(R.id.imgShow_task);
        btn_pick_take.setOnClickListener(this);
        btn_pick_select.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        File file=new File(Environment.getExternalStorageDirectory(), "/temp/"+System.currentTimeMillis() + ".jpg");
        if (!file.getParentFile().exists())file.getParentFile().mkdirs();
        Uri imageUri = Uri.fromFile(file);
        switch (v.getId()){
            case R.id.btn_pick_take:
                takePhoto.onPickFromCapture(imageUri);//从相机获取图片(不裁剪)
                takePhoto.onEnableCompress(null,false);//压缩图片方法
                break;
            case R.id.btn_pick_select:
                takePhoto.onPickFromGallery();//从相册中获取图片（不裁剪）
                takePhoto.onEnableCompress(null,false);//压缩图片方法
                break;
        }
    }


    @Override
    public void takeCancel() {
        super.takeCancel();
    }

    @Override
    public void takeFail(TResult result, String msg) {
        super.takeFail(result, msg);
    }

    @Override
    public void takeSuccess(TResult result) {
        super.takeSuccess(result);
        Log.d("aa","<//>"+result.getImage().getPath());
        Glide.with(this).load(result.getImage().getPath()).into(imgShow_task);
    }




}
