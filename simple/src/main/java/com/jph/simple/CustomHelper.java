package com.jph.simple;

import android.net.Uri;
import android.os.Environment;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.jph.takephoto.app.TakePhoto;
import com.jph.takephoto.compress.CompressConfig;
import com.jph.takephoto.model.CropOptions;

import java.io.File;


/**
 * - 支持通过相机拍照获取图片
 * - 支持从相册选择图片
 * - 支持从文件选择图片
 * - 支持多图选择
 * - 支持批量图片裁切
 * - 支持批量图片压缩
 * - 支持对图片进行压缩
 * - 支持对图片进行裁剪
 * - 支持对裁剪及压缩参数自定义
 * - 提供自带裁剪工具(可选)
 * - 支持智能选取及裁剪异常处理
 * - 支持因拍照Activity被回收后的自动恢复
 * Author: crazycodeboy
 * Date: 2016/9/21 0007 20:10
 * Version:3.0.0
 * 技术博文：http://www.cboy.me
 * GitHub:https://github.com/crazycodeboy
 * Eamil:crazycodeboy@gmail.com
 */
public class CustomHelper{
    private View rootView;
    private RadioGroup rgCrop,rgCompress,rgFrom,rgCropSize,rgCropTool,rgShowProgressBar;
    private EditText etCropHeight,etCropWidth,etLimit,etSize,etPx;
    public static CustomHelper of(View rootView){
        return new CustomHelper(rootView);
    }
    private CustomHelper(View rootView) {
        this.rootView = rootView;
        init();
    }
    private void init(){
        rgCrop= (RadioGroup) rootView.findViewById(R.id.rgCrop);//是否裁切
        rgCompress= (RadioGroup) rootView.findViewById(R.id.rgCompress);//是否压缩
        rgCropSize= (RadioGroup) rootView.findViewById(R.id.rgCropSize);//裁剪尺寸/比例
        rgFrom= (RadioGroup) rootView.findViewById(R.id.rgFrom);//从哪选择图片
        rgShowProgressBar= (RadioGroup) rootView.findViewById(R.id.rgShowProgressBar);//压缩进度条
        rgCropTool= (RadioGroup) rootView.findViewById(R.id.rgCropTool);//裁切工具
        etCropHeight= (EditText) rootView.findViewById(R.id.etCropHeight);
        etCropWidth= (EditText) rootView.findViewById(R.id.etCropWidth);
        etLimit= (EditText) rootView.findViewById(R.id.etLimit);//最多选取照片数
        etSize= (EditText) rootView.findViewById(R.id.etSize);//压缩大小
        etPx= (EditText) rootView.findViewById(R.id.etPx);//压缩长宽

    }

    public void onClick(View view,TakePhoto takePhoto) {
        File file=new File(Environment.getExternalStorageDirectory(), "/temp/"+System.currentTimeMillis() + ".jpg");
        if (!file.getParentFile().exists())file.getParentFile().mkdirs();
        Uri imageUri = Uri.fromFile(file);

        configCompress(takePhoto);//是否压缩图片方法
        switch (view.getId()){
            case R.id.btnPickBySelect://选择照片
                int limit= Integer.parseInt(etLimit.getText().toString());//获取选择照片数量
                if(limit>1){
                    if(rgCrop.getCheckedRadioButtonId()==R.id.rbCropYes){//是否裁剪  是
                        takePhoto.onPickMultipleWithCrop(limit,getCropOptions());//图片多选，并裁切
                    }else {
                        takePhoto.onPickMultiple(limit);
                    }
                    return;
                }
                if(rgFrom.getCheckedRadioButtonId()==R.id.rbFile){//文件
                    if(rgCrop.getCheckedRadioButtonId()==R.id.rbCropYes){//是否裁剪  是
                        takePhoto.onPickFromDocumentsWithCrop(imageUri,getCropOptions());//从文件中获取图片并裁剪
                    }else {
                        takePhoto.onPickFromDocuments();//从文件中获取图片（不裁剪）
                    }
                    return;
                }else {//相册
                    if(rgCrop.getCheckedRadioButtonId()==R.id.rbCropYes){//是否裁剪  是
                        takePhoto.onPickFromGalleryWithCrop(imageUri,getCropOptions());//从相册中获取图片并裁剪
                    }else {
                        takePhoto.onPickFromGallery();//从相册中获取图片（不裁剪）
                    }
                }
                break;
            case R.id.btnPickByTake://拍照
                if(rgCrop.getCheckedRadioButtonId()==R.id.rbCropYes){//是否裁剪是
                    takePhoto.onPickFromCaptureWithCrop(imageUri,getCropOptions());//getCropOptions()裁剪参数配置
                }else {//否
                    takePhoto.onPickFromCapture(imageUri);
                }
                break;
            default:
                break;
        }
    }
    /**
     * 压缩配置
     * **/
    private void configCompress(TakePhoto takePhoto){
        if(rgCompress.getCheckedRadioButtonId()!=R.id.rbCompressYes){//是否压缩  否
            takePhoto.onEnableCompress(null,false);//压缩图片方法
            return ;
        }
        /////以下压缩的参数配置////
        int maxSize= Integer.parseInt(etSize.getText().toString());//压缩大小
        int maxPixel= Integer.parseInt(etPx.getText().toString());//压缩长宽
        boolean showProgressBar=rgShowProgressBar.getCheckedRadioButtonId()==R.id.rbShowYes? true:false;//压缩进度条 true显示false不显示
        CompressConfig config= new CompressConfig.Builder().setMaxPixel(maxSize).setMaxPixel(maxPixel).create();
        takePhoto.onEnableCompress(config,showProgressBar);//压缩图片方法
    }
    /**
     * 裁剪配置设置
     * **/
    private CropOptions getCropOptions(){
        if(rgCrop.getCheckedRadioButtonId()!=R.id.rbCropYes)return null;//是否裁剪  是
        int height= Integer.parseInt(etCropHeight.getText().toString());//获取输入高
        int width= Integer.parseInt(etCropWidth.getText().toString());//获取输入宽
        boolean withWonCrop=rgCropTool.getCheckedRadioButtonId()==R.id.rbCropOwn? true:false;//裁剪工具选择 自带true，第三方false

        CropOptions.Builder builder=new CropOptions.Builder();

        if(rgCropSize.getCheckedRadioButtonId()==R.id.rbAspect){//宽高自定义设置(输入)
            builder.setAspectX(width).setAspectY(height);
        }else {//系统自带宽高
            builder.setOutputX(width).setOutputY(height);
        }
        builder.setWithOwnCrop(withWonCrop);
        return builder.create();
    }

}
