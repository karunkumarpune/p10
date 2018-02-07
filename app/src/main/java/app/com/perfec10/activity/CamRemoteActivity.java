/*
package app.com.perfec10.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.media.Image;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fluper.cleekchat.R;
import com.example.fluper.cleekchat.callbacks.NetworkCallback;
import com.example.fluper.cleekchat.utils.Constant;
import com.example.fluper.cleekchat.utils.SharedPreference;
import com.example.fluper.cleekchat.utils.SquareCameraPreview;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimerTask;

import static com.example.fluper.cleekchat.R.id.cast_button_type_closed_caption;
import static com.example.fluper.cleekchat.R.id.ll_reClick_done;

@RequiresApi(api = Build.VERSION_CODES.N)

public class CamRemoteActivity extends AppCompatActivity
        implements View.OnClickListener,
        SurfaceHolder.Callback ,NetworkCallback{

    private String imagePath;
    private File pictureFile;
    private int rotation = 0;
    private boolean background;
    MediaRecorder mediaRecorder;
    private Camera.Size mPreviewSize = null;
    TimerTask timerTask;
    boolean recording;
    private LinearLayout ll_reclick;
    private boolean safeToTakePicture = false;
    private SharedPreference sharedPreference;
    private Activity activity;
    private SquareCameraPreview surfaceView;
    private SurfaceHolder surfaceHolder;
    private Camera camera;
    private int cameraId;
    private boolean flashmode = false;
    private String thumbFilePath;
    private String filePath;
    private File cacheDir;
    private ImageView iv_camera,iv_dummy_camera;
    private TextView tv_reClick, tv_done;
    private Uri uri=null;
    private String pictuerPath="";

    private final Camera.PictureCallback mPicture = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            String filepath = Environment.getExternalStorageDirectory() + "/";
            try {
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
               pictuerPath = filepath + timeStamp + "_cleekChat" + ".jpg";
                pictureFile = new File(pictuerPath);
                uri= Uri.fromFile(pictureFile);
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                Bitmap bitmap = BitmapFactory.decodeFile(pictuerPath,bmOptions);
                FileOutputStream fileOuputStream = new FileOutputStream(pictureFile);
                fileOuputStream.write(data);
                fileOuputStream.close();
                camera.stopPreview();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (pictureFile == null) {
                return;
            }

            safeToTakePicture = true;
            sharedPreference.putString(Constant.FILE_PATH, pictureFile.getAbsolutePath());
            reclick();
        }

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cam_remote);

        thumbFilePath = Environment.getExternalStorageDirectory() + "/data/";
       init();
        listener();
        if(getIntent().getStringExtra("start")!=null)
        {
            dummyCamera();
        }




    }

    private void listener() {
        findViewById(R.id.iv_back).setOnClickListener(this);
        activity = CamRemoteActivity.this;
        sharedPreference = SharedPreference.getInstance(activity);

        tv_reClick.setOnClickListener(this);
        tv_done.setOnClickListener(this);
        iv_camera.setOnClickListener(this);
        iv_dummy_camera.setOnClickListener(this);


    }

    private void init() {
        surfaceView = (SquareCameraPreview) findViewById(R.id.suraface_view);
        tv_reClick = (TextView) findViewById(R.id.tv_reClick);
        tv_done = (TextView) findViewById(R.id.tv_done);
        iv_camera = (ImageView) findViewById(R.id.iv_camera);
        iv_dummy_camera = (ImageView) findViewById(R.id.iv_dummy_camera);
        ll_reclick = (LinearLayout) findViewById(ll_reClick_done);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_camera:
                ll_reclick.setVisibility(View.VISIBLE);
                iv_camera.setVisibility(View.GONE);
                if (safeToTakePicture) {
                    camera.takePicture(null, null, mPicture);
                    safeToTakePicture = false;
                }
                break;
            case R.id.iv_dummy_camera:
                dummyCamera();
                break;
            case R.id.tv_done:
                Intent intent=new Intent();
                intent.putExtra("uri_image",pictuerPath);
                intent.putExtra("from_Cam","from_Cam");
                setResult(200,intent);
                finish();
                break;
            case R.id.tv_reClick:
                ll_reclick.setVisibility(View.GONE);
                iv_camera.setVisibility(View.VISIBLE);
                Intent intent1=new Intent(this, CamRemoteActivity.class);
                intent1.putExtra("start","start");
                startActivity(intent1);
                finish();

                break;
            case R.id.iv_back:
                finish();
                break;
        }
    }

    private void dummyCamera() {
        iv_dummy_camera.setVisibility(View.GONE);
        iv_camera.setVisibility(View.VISIBLE);
        surfaceView.setVisibility(View.VISIBLE);
        cameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;

        surfaceHolder = surfaceView.getHolder();

        surfaceHolder.addCallback(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //   imagePath = Environment.getExternalStorageDirectory().getPath() + "/data" + getPackageName() + "/" + "temp" + "/" + timeStamp + "chapChat.png";
        imagePath = Environment.getExternalStorageDirectory() + "/cleekchat/";
        sharedPreference.putString(Constant.DIRECTORY_PATH, imagePath);
    }

    @Override
    protected void onResume() {
        super.onResume();
        background = false;

    }

    @Override
    protected void onStop() {
        super.onStop();
        background = true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @SuppressLint("WrongConstant")
    private void setUpCamera(Camera c) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        rotation = getWindowManager().getDefaultDisplay().getRotation();
        int degree = 270;
        switch (rotation) {
            case Surface.ROTATION_0:
                degree = 0;
                break;
            case Surface.ROTATION_90:
                degree = 90;
                break;
            case Surface.ROTATION_180:
                degree = 180;
                break;
            case Surface.ROTATION_270:
                degree = 270;
                break;
            default:
                break;
        }

        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            // frontFacing
            rotation = (info.orientation + degree) % 330;
            rotation = (360 - rotation) % 360;
        } else {
            // Back-facing
            rotation = (info.orientation - degree + 360) % 360;
        }
        c.setDisplayOrientation(rotation);
        Camera.Parameters params = c.getParameters();

        if (params.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);

//        showFlashButton(params);

            List<String> focusModes = params.getSupportedFocusModes();
            if (focusModes != null) {
                if (focusModes
                        .contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                    params.setFlashMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);

                }
            }
        }

        params.setRotation(rotation);
    }

    private void reclick() {
        ll_reclick.setVisibility(View.VISIBLE);
        iv_camera.setVisibility(View.GONE);
    }

    private Camera.Size getOptimalPictureSize() {
        if (camera == null)
            return null;

        List<Camera.Size> cameraSizes = camera.getParameters()
                .getSupportedPictureSizes();
        Camera.Size optimalSize = camera.new Size(0, 0);
        double previewRatio = (double) mPreviewSize.width / mPreviewSize.height;

        for (Camera.Size size : cameraSizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - previewRatio) > 0.01f)
                continue;
            if (size.height > optimalSize.height) {
                optimalSize = size;
            }
        }

        if (optimalSize.height == 0) {
            for (Camera.Size size : cameraSizes) {
                if (size.height > optimalSize.height) {
                    optimalSize = size;
                }
            }
        }
        return optimalSize;
    }

    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) h / w;

        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - h) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - h);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - h) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - h);
                }
            }
        }
        return optimalSize;
    }

    private void setUpSize() {
        List<Camera.Size> mSupportedPreviewSizes = camera.getParameters().getSupportedPreviewSizes();


        if (mSupportedPreviewSizes != null) {
            mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes, surfaceView.width, surfaceView.height);
        }

        Camera.Size mPictureSize = getOptimalPictureSize();
        Camera.Parameters parameters = camera.getParameters();
        parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
        parameters.setPictureSize(mPictureSize.width, mPictureSize.height);
        parameters.setJpegQuality(100);

        if (parameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
        }

//        if (parameters.getSupportedSceneModes().contains(Camera.Parameters.SCENE_MODE_HDR) && (cameraId==1 || !flashmode)) {
//            parameters.setSceneMode(Camera.Parameters.SCENE_MODE_HDR);
//        }

        camera.setParameters(parameters);
    }

    private boolean openCamera(int id) {
        boolean result = false;
        cameraId = id;
        releaseCamera();
        try {
            camera = Camera.open(cameraId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (camera != null) {
            try {
                setUpCamera(camera);
                camera.setErrorCallback(new Camera.ErrorCallback() {
                    @Override
                    public void onError(int error, Camera camera) {

                    }
                });
                camera.setPreviewDisplay(surfaceHolder);
                setUpSize();
                camera.startPreview();
                result = true;
            } catch (IOException e) {
                e.printStackTrace();
                result = false;
                releaseCamera();
            }
        }
        return result;
    }

    private void releaseCamera() {
        try {
            if (camera != null) {
                camera.setPreviewCallback(null);
                camera.setErrorCallback(null);
                camera.stopPreview();
                camera.release();
                camera = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("error", e.toString());
            camera = null;
        }
    }

    private boolean hasFlash() {
        if (cameraId == Camera.CameraInfo.CAMERA_FACING_FRONT && !sharedPreference.getBoolean(Constant.FRONT_FLASH, true)) {
            return false;
        }
        Camera.Parameters params = camera.getParameters();
        List<String> flashModes = params.getSupportedFlashModes();
        if (flashModes == null) {
            return false;
        }

        for (String flashMode : flashModes) {
            if (Camera.Parameters.FLASH_MODE_TORCH.equals(flashMode)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        if (!openCamera(cameraId)) {
            Toast.makeText(CamRemoteActivity.this, "Failer to open camera", Toast.LENGTH_SHORT).show();
        } else {
            if (hasFlash()) {
//                flashLayout.setVisibility(View.VISIBLE);
            } else {
//                flashLayout.setVisibility(View.INVISIBLE);
            }
        }


    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        camera.startPreview();
        safeToTakePicture = true;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        if (camera != null) {
            camera.setPreviewCallback(null);
            camera.release();
        }
    }

    @Override
    public void onNetworkSuccess(String result, String fromUrl, int status) {

    }

    @Override
    public void onNetworkTimeOut(String message, String fromUrl) {

    }
}
*/
