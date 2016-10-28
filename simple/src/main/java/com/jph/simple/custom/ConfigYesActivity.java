package com.jph.simple.custom;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.jph.simple.R;
import com.jph.takephoto.app.TakePhoto;
import com.jph.takephoto.app.TakePhotoActivity;
import com.jph.takephoto.compress.CompressConfig;
import com.jph.takephoto.model.CropOptions;
import com.jph.takephoto.model.TResult;

import java.io.File;

/**
 * Created by Tao on 2016/10/21.
 */

public class ConfigYesActivity extends TakePhotoActivity implements View.OnClickListener{

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
                takePhoto.onPickFromCaptureWithCrop(imageUri,getCropOptions());//getCropOptions()裁剪参数配置
                configCompress(takePhoto);//是否压缩图片方法
                break;
            case R.id.btn_pick_select:
                takePhoto.onPickFromGalleryWithCrop(imageUri,getCropOptions());//从相册中获取图片并裁剪
                configCompress(takePhoto);//是否压缩图片方法
                break;
        }
    }

    /**
     * 裁剪配置设置
     * **/
    private CropOptions getCropOptions(){

        int height= 800;//获取输入高
        int width= 800;//获取输入宽
        boolean withWonCrop=true;//裁剪工具选择 自带true，第三方false
        CropOptions.Builder builder=new CropOptions.Builder();
        builder.setAspectX(width).setAspectY(height);
        builder.setWithOwnCrop(withWonCrop);
        return builder.create();
    }

    /**
     * 压缩配置
     * **/
    private void configCompress(TakePhoto takePhoto){
        /////以下压缩的参数配置////
        int maxSize= 102400;//压缩大小
        int maxPixel= 800;//压缩长宽
        boolean showProgressBar=false;//压缩进度条 true显示false不显示
        CompressConfig config= new CompressConfig.Builder().setMaxPixel(maxSize).setMaxPixel(maxPixel).create();
        takePhoto.onEnableCompress(config,showProgressBar);//压缩图片方法
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
        Glide.with(this).load(result.getImage().getPath()).into(imgShow_task);
    }
}
