package com.example.basic_common.camerax;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.ContentValues;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Size;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.FocusMeteringAction;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.core.VideoCapture;
import androidx.camera.core.impl.utils.executor.CameraXExecutors;
import androidx.camera.extensions.BeautyPreviewExtender;
import androidx.camera.extensions.NightImageCaptureExtender;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;
import com.google.common.util.concurrent.ListenableFuture;
import java.nio.ByteBuffer;

/**
 * Description:
 * Created by WuQuan on 2021/3/23.
 */
public class CameraXImp {
    private static final String CAPTURED_FILE_NAME = "captured_picture";
    private static final String RECORDED_FILE_NAME = "recorded_video";
    private static final String RECORDED_FILE_NAME_END = "video/mp4";
    private static final String TAG= CameraXImp.class.getName();

    public static class Build{

        private Application mContext;
        private Activity mActivity;
        private LifecycleOwner mLifecycleOwner;
        private PreviewView mPreviewView;
        private ProcessCameraProvider mCameraProvider;
        private Preview mPreview;
        private Camera mCamera;
        private ImageCapture mImageCapture;
        private ImageAnalysis mImageAnalysis;
        private VideoCapture mVideoCapture;
        private boolean isBack = true;
        private int recCount, picCount;
        private boolean isAnalyzing;

        public Build(Activity activity){
            this.mActivity=activity;
            this.mContext=activity.getApplication();
        }

        public Build setLifecycleOwner(LifecycleOwner lifecycleOwner){
            this.mLifecycleOwner=lifecycleOwner;
            return this;
        }

        public Build setPreView(PreviewView previewView){
            this.mPreviewView=previewView;
            return this;
        }


        public Build create(){
            //0.初始化
            init();
            //1.创建摄像头预览
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                setupCamera();
            }
            //2.摄像头感知生命周期
            return this;
        }


        public void init(){
            if(mPreviewView==null){
                throw new NullPointerException("before init() need setPreView");
            }
            if(mLifecycleOwner==null){
                throw new NullPointerException("before init() need setLifecycleOwner");
            }
            //1.镜头触摸聚焦
                mPreviewView.setOnTouchListener((v, event) -> {
                    FocusMeteringAction action = new FocusMeteringAction.Builder(mPreviewView.getMeteringPointFactory().createPoint(event.getX(), event.getY())).build();
                    try {
                            Log.d(TAG, "Focus camera:"+(int) event.getX()+"  ,Y="+(int) event.getY());
                        mCamera.getCameraControl().startFocusAndMetering(action);
                    } catch (Exception e) {
                        Log.e(TAG, "Error focus camera");
                    }
                    return false;
                });
        }


        @RequiresApi(api = android.os.Build.VERSION_CODES.LOLLIPOP)
        private void setupCamera() {
            ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(mContext);
            cameraProviderFuture.addListener(() -> {
                try {
                    mCameraProvider = cameraProviderFuture.get();
                    bindPreview(mCameraProvider);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, ContextCompat.getMainExecutor(mContext));
        }

        @RequiresApi(api = android.os.Build.VERSION_CODES.LOLLIPOP)
        private void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {
            bindPreview(cameraProvider, false);
        }

        @RequiresApi(api = android.os.Build.VERSION_CODES.LOLLIPOP)
        @SuppressLint("RestrictedApi")
        private void bindPreview(@NonNull ProcessCameraProvider cameraProvider, boolean isVideo ) {
            //1.预览配置
            Preview.Builder previewBuilder = new Preview.Builder();

            //2.图像配置(拍照需要用到)
            ImageCapture.Builder captureBuilder = new ImageCapture.Builder()
                    .setTargetRotation(mPreviewView.getDisplay().getRotation());

            //3.前后置摄像头配置
            CameraSelector cameraSelector = isBack ? CameraSelector.DEFAULT_BACK_CAMERA
                    : CameraSelector.DEFAULT_FRONT_CAMERA;

            //4.图片分析配置(可以生成二维码等)
            mImageAnalysis = new ImageAnalysis.Builder()
                    .setTargetRotation(mPreviewView.getDisplay().getRotation())
                    .setTargetResolution(new Size(720, 1440))
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build();
            //5.视频录制配置
            mVideoCapture = new VideoCapture.Builder()
                    .setTargetRotation(mPreviewView.getDisplay().getRotation())
                    .setVideoFrameRate(25)
                    .setBitRate(3 * 1024 * 1024)
                    .build();

            setPreviewExtender(previewBuilder, cameraSelector);
            mPreview = previewBuilder.build();

            setCaptureExtender(captureBuilder, cameraSelector);
            mImageCapture =  captureBuilder.build();

            cameraProvider.unbindAll();
            if (isVideo) {
                mCamera = cameraProvider.bindToLifecycle(mLifecycleOwner, cameraSelector, mPreview, mVideoCapture);
            } else {
                mCamera = cameraProvider.bindToLifecycle(mLifecycleOwner, cameraSelector, mPreview, mImageCapture, mImageAnalysis);
            }
            mPreview.setSurfaceProvider(mPreviewView.getSurfaceProvider());
        }


        public void destory(){
            if(mCameraProvider!=null){
                mCameraProvider.unbindAll();
            }
        }
        /**
         * 翻转摄像头
         */
        @RequiresApi(api = android.os.Build.VERSION_CODES.LOLLIPOP)
        public void rolloverCamera() {
            if (mCameraProvider != null) {
                isBack = !isBack;
                bindPreview(mCameraProvider);
                if (mImageAnalysis != null) {
                    mImageAnalysis.clearAnalyzer();
                }
            }
        }


        /**
         * 进行拍照
         */
        @SuppressLint("RestrictedApi")
        private void takenPictureInternal() {
            Log.d(TAG, "takenPictureInternal isExternal:");
            final ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, CAPTURED_FILE_NAME
                    + "_" + picCount++);
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");

            ImageCapture.OutputFileOptions outputFileOptions =new ImageCapture.OutputFileOptions.Builder(mContext.getContentResolver(), MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues).build();
            if (mImageCapture != null) {
                mImageCapture.takePicture(outputFileOptions, CameraXExecutors.mainThreadExecutor(),
                        new ImageCapture.OnImageSavedCallback() {
                            @Override
                            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                                Log.d(TAG, "outputFileResults:" + outputFileResults.getSavedUri().getPath() + " picCount:" + picCount);
                            }

                            @Override
                            public void onError(@NonNull ImageCaptureException exception) {
                                Log.d(TAG, "onError:" + exception.getImageCaptureError());
                            }
                        });
            }
        }

        /**
         * 图像分析 (比如制作图片二维码)
         */
        @SuppressLint("RestrictedApi")
        public void pictureAnalyz() {
            if (mImageAnalysis == null) {
                return;
            }

            if (!isAnalyzing) {
                Log.d(TAG, "setAnalyzer()");
                mImageAnalysis.setAnalyzer(CameraXExecutors.mainThreadExecutor(), image -> {
                    Log.d(TAG, "analyze() image:" + image);
                    analyzeQRCode(image);
                });
            } else {
                Log.d(TAG, "clearAnalyzer()");
                mImageAnalysis.clearAnalyzer();
            }
            isAnalyzing = !isAnalyzing;
//            binding.qrCodeZone.setVisibility(isAnalyzing ? View.VISIBLE : View.GONE);
        }

        private void analyzeQRCode(@NonNull ImageProxy imageProxy) {
            ByteBuffer byteBuffer = imageProxy.getPlanes()[0].getBuffer();
            byte[] data = new byte[byteBuffer.remaining()];
            byteBuffer.get(data);

//            int width = imageProxy.getWidth(), height = imageProxy.getHeight();
//            PlanarYUVLuminanceSource source = new PlanarYUVLuminanceSource(
//                    data, width, height, 0, 0, width, height, false);
//            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
//
//            Result result;
//            try {
//                result = multiFormatReader.decode(bitmap);
//                Log.d(TAG, "result:" + result);
//            } catch (Exception e) {
//                Log.e(TAG, "Error decoding barcode");
//                result = null;
//            }
            imageProxy.close();
        }


        /**
         * 录制视频
         */
        @SuppressLint("RestrictedApi")
        private void recordVideo() {
            Log.d(TAG, "recordVideo");
            final ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, RECORDED_FILE_NAME
                    + "_" + recCount++);
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, RECORDED_FILE_NAME_END);

            Log.d(TAG, "startRecording");
            try {
                VideoCapture.OutputFileOptions outputFileOptions = new VideoCapture.OutputFileOptions.Builder(mContext.getContentResolver(), MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues).build();
                mVideoCapture.startRecording(outputFileOptions, CameraXExecutors.mainThreadExecutor(), new VideoCapture.OnVideoSavedCallback() {
                            @Override
                            public void onVideoSaved(@NonNull VideoCapture.OutputFileResults outputFileResults) {
                                Log.d(TAG, "onVideoSaved outputFileResults:"
                                        + outputFileResults.getSavedUri().getPath());
                                Toast.makeText(mActivity, "Video got" + (outputFileResults.getSavedUri() != null ? " @ " + outputFileResults.getSavedUri().getPath(): ""), Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onError(int videoCaptureError, @NonNull String message,
                                                @Nullable Throwable cause) {
                                Log.d(TAG, "onError videoCaptureError:" + videoCaptureError + " message:" + message, cause);
                            }
                        }
                );
            } catch (Exception e) {
                Log.e(TAG, "Record video error:", e);
            }
        }
    }

    private static void setPreviewExtender(Preview.Builder builder, CameraSelector cameraSelector) {
        BeautyPreviewExtender beautyPreviewExtender = BeautyPreviewExtender.create(builder);
        if (beautyPreviewExtender.isExtensionAvailable(cameraSelector)) {
            // Enable the extension if available.
            Log.d(TAG, "beauty preview extension enable");
            beautyPreviewExtender.enableExtension(cameraSelector);
        } else {
            Log.d(TAG, "beauty preview extension not available");
        }
    }

    private static void setCaptureExtender(ImageCapture.Builder builder, CameraSelector cameraSelector) {
        NightImageCaptureExtender nightImageCaptureExtender = NightImageCaptureExtender.create(builder);
        if (nightImageCaptureExtender.isExtensionAvailable(cameraSelector)) {
            // Enable the extension if available.
            Log.d(TAG, "night capture extension enable");
            nightImageCaptureExtender.enableExtension(cameraSelector);
        } else {
            Log.d(TAG, "night capture extension not available");
        }
    }

}
