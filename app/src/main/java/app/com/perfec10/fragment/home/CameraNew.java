package app.com.perfec10.fragment.home;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimerTask;

import app.com.perfec10.R;
import app.com.perfec10.activity.MainActivity;
import app.com.perfec10.fragment.measure.ImageInputs;
import app.com.perfec10.fragment.measure.Stats;
import app.com.perfec10.util.Progress;
import app.com.perfec10.util.SquareCameraPreview;

/**
 * Created by fluper on 16/11/17.
 */

public class CameraNew extends Fragment implements SurfaceHolder.Callback {
    private MainActivity mainActivity;

    private boolean safeToTakePicture = false;
    File pictureFile;
    int rotation = 0;
    private static int RESULT_LOAD_IMG = 1;
    private String TAG = "CameraNew";

    //  private Activity activity;
    boolean background;
    private SquareCameraPreview surfaceView;
    private SurfaceHolder surfaceHolder;
    private Camera camera;
    private int cameraId;
    MediaRecorder mediaRecorder;
    Camera.Size mPreviewSize = null;
    private boolean flashmode = false;
    TimerTask timerTask;
    public String imagePath;
    boolean recording;
    private String thumbFilePath;
    private String filePath;
    private Progress progress;
    private ProgressBar progress_camera;
    private boolean mPreviewRunning;
    private LinearLayout ll_gallery_home, ll_click_home, ll_flash_home;
    private View view;
    private boolean clickOn = false;
    private ImageView iv_flash_home;
    Uri outPutfileUri;
    static int TAKE_PIC = 1;
    private static final int SELECT_PICTURE = 100;
    String path = "", pic_name;

    @SuppressLint("ValidFragment")
    public CameraNew(MainActivity mainActivity){
        this.mainActivity = mainActivity;
        progress = new Progress(mainActivity);
        progress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progress.setCancelable(false);
        progress.setCanceledOnTouchOutside(false);
    }

    public CameraNew(){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.home_camera, container, false);
        initView(view);
        clickListner();

        return view;
    }

    public void initView(View view){
        thumbFilePath = Environment.getExternalStorageDirectory() + "/data/";

        cameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
        surfaceView = (SquareCameraPreview) view.findViewById(R.id.surface_home);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        mainActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //   imagePath = Environment.getExternalStorageDirectory().getPath() + "/data" + getPackageName() + "/" + "temp" + "/" + timeStamp + "chapChat.png";
        imagePath = Environment.getExternalStorageDirectory() + "/cleekchat/";
        //sharedPreference.putString(Constants.DIRECTORY_PATH, imagePath);

        ll_gallery_home = (LinearLayout)view.findViewById(R.id.ll_gallery_home);
        ll_click_home = (LinearLayout)view.findViewById(R.id.ll_click_home);
        ll_flash_home = (LinearLayout)view.findViewById(R.id.ll_flash_home);

        iv_flash_home = (ImageView) view.findViewById(R.id.iv_flash_home);
        progress_camera = (ProgressBar) view.findViewById(R.id.progress_camera);

        Stats.tagPerson = "";
        Stats.location = "";
        Stats.age = "";
        Stats.height = "";
        Stats.wieght = "";
        Stats.race = "";
    }

    public void clickListner(){
        ll_click_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // progress.show();
               // progress_camera.setVisibility(View.VISIBLE);
               /* Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                       // cropImage();
                        progress.dismiss();
                    }
                }, 3000); // 3000 milliseconds delay*/

                Camera.Parameters p = camera.getParameters();
                if (clickOn){
                    Log.d(TAG+" third","if");
                    p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                }/*else {
                    Log.d("third","else");
                    p.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                }*/

                camera.setParameters(p);
                camera.takePicture(null, null, mPicture);
                safeToTakePicture = false;
            }
        });
        ll_gallery_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosefromgallery();
            }
        });
        ll_flash_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickOn){
                    iv_flash_home.setBackgroundResource(R.mipmap.flash_off);
                    clickOn = false;

                    // flashLightOff();
                }else {
                    iv_flash_home.setBackgroundResource(R.mipmap.flash_on);
                    clickOn = true;
                    // flashLightOn();
                }
                Log.d(TAG+" first flash", clickOn+" ");

            }
        });
    }
    public void choosefromgallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
      //  intent.setType("image/*");
        //intent.setAction(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
       // startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
        startActivityForResult(intent, SELECT_PICTURE);
    }

    @Override
    public void onResume() {
        super.onResume();
        background = false;
        clickOn = false;
    }

    @Override
    public void onStop() {
        super.onStop();
        background = true;
    }

    @SuppressLint("WrongConstant")
    private void setUpCamera(Camera c) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        rotation = mainActivity.getWindowManager().getDefaultDisplay().getRotation();
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
       // ll_reclick.setVisibility(View.VISIBLE);
       // button_take_picture.setVisibility(View.GONE);
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

        int targetHeight = h;

        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

    void setUpSize() {
        List<Camera.Size> mSupportedPreviewSizes = camera.getParameters().getSupportedPreviewSizes();


        if (mSupportedPreviewSizes != null) {
            mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes, surfaceView.width, surfaceView.height);
        }

        Camera.Size mPictureSize = getOptimalPictureSize();
        Camera.Parameters parameters = camera.getParameters();
        parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
        parameters.setPictureSize(mPictureSize.width, mPictureSize.height);
        parameters.setJpegQuality(100);
        parameters.setRotation(90);

        if (parameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
        }

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
            Log.e(TAG+" error", e.toString());
            camera = null;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        if (!openCamera(cameraId)) {
            Toast.makeText(mainActivity, "Failer to open camera", Toast.LENGTH_SHORT).show();
        } else {

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

    private static File getOutputMediaFile() {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "CameraDemo");

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return new File(mediaStorageDir.getPath() + File.separator +
                "IMG_" + timeStamp + ".jpg");
    }

    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            final Bitmap bit = BitmapFactory.decodeByteArray(data, 0, data.length);

            try {

                String filename = "bitmap.png";
                FileOutputStream stream = mainActivity.openFileOutput(filename, Context.MODE_PRIVATE);
                bit.compress(Bitmap.CompressFormat.PNG, 100, stream);
               Uri file = Uri.fromFile(getOutputMediaFile());
                /*Uri uri = getImageUri(mainActivitySignUP, bit);
                ExifInterface ei = new ExifInterface(getRealPathFromURI(mainActivitySignUP, uri));

                int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_UNDEFINED);

                Bitmap rotatedBitmap = null;
                switch(orientation) {

                    case ExifInterface.ORIENTATION_ROTATE_90:
                        rotatedBitmap = rotateImage(bit, 90);
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_180:
                        rotatedBitmap = rotateImage(bit, 180);
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_270:
                        rotatedBitmap = rotateImage(bit, 270);
                        break;

                    case ExifInterface.ORIENTATION_NORMAL:
                    default:
                        rotatedBitmap = bit;
                }*/
                new File(file.getPath()).delete();
                //new File(uri.getPath()).delete();
                //Cleanup
                stream.close();
                camera.stopPreview();
              //  progress.dismiss();
              //  MainActivity.changeFragment(new CropZoomScreen(mainActivity, bit), "zoom");
                MainActivity.changeFragment(new CropZoomScreen(mainActivity, bit), "zoom");

              //  MainActivity.changeFragment(new ImageInputs(mainActivity, bit), "Image input");
            } catch (Exception e) {
                e.printStackTrace();
            } catch (Throwable e) {
                e.printStackTrace();
            }
            if (pictureFile == null) {
                return;
            }

            safeToTakePicture = true;
          //  sharedPreference.putString(Constants.FILE_PATH, pictureFile.getAbsolutePath());
            reclick();
        }

    };
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG+" Inside ", "on Activity");
        Log.d(TAG+" request code ", requestCode+" ");
        if (requestCode==100){
            Log.d(TAG+" from gallery ", "called");
            try {
                Uri uri = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(mainActivity.getContentResolver(), uri);
                    MainActivity.changeFragment(new CropZoomScreen(mainActivity, bitmap), "zoom");
                  //  MainActivity.changeFragment(new ImageInputs(mainActivity, bitmap), "Image input");
                   // mainActivitySignUP.fragmentManager = mainActivitySignUP.getSupportFragmentManager();
                   // mainActivitySignUP.fragmentManager.beginTransaction().replace(R.id.main_frame, new ImageInputs(mainActivitySignUP, bitmap)).commit();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}


