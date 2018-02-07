package app.com.perfec10.fragment.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.naver.android.helloyako.imagecrop.view.ImageCropView;

import java.io.FileOutputStream;

import app.com.perfec10.R;
import app.com.perfec10.activity.MainActivity;
import app.com.perfec10.fragment.measure.ImageInputs;
import app.com.perfec10.util.Progress;

/**
 * Created by fluper on 21/11/17.
 */

@SuppressLint("ValidFragment")
public class CropZoomScreen extends Fragment {
    private MainActivity mainActivity;
    private Bitmap bitmap;
    private ImageCropView iv_crop_zoom;
    private TextView tv_retake_zoomcrop, tv_next_zoomcrop;
    private Progress progress;
    public static String backFrom = "";
    private String TAG = " CropZoomScreen";

    public CropZoomScreen(MainActivity mainActivity, Bitmap bitmap){
        this.mainActivity = mainActivity;
        this.bitmap = bitmap;
        progress = new Progress(mainActivity);
        progress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progress.setCancelable(false);
        progress.setCanceledOnTouchOutside(false);

    }

    public CropZoomScreen(){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.zoom_crop, container, false);
        Log.d(TAG+" back from zoom ", backFrom+" ");
        if (backFrom.equals("imputImage")){
            Log.d(TAG+" inside ", "if");
        }else if (backFrom.equals("stats")){
            Log.d(TAG+" inside ", "else");
           // backFrom = "imputImage";
            mainActivity.getSupportFragmentManager().popBackStack();

        }
        initView(view);
        clickListner();
        return view;
    }


    public void initView(View view){
        iv_crop_zoom = (ImageCropView) view.findViewById(R.id.iv_crop_zoom);
        iv_crop_zoom.setGridInnerMode(ImageCropView.GRID_ON);
        iv_crop_zoom.setGridOuterMode(ImageCropView.GRID_ON);
        iv_crop_zoom.setImageBitmap(bitmap);

        tv_retake_zoomcrop = (TextView) view.findViewById(R.id.tv_retake_zoomcrop);
        tv_next_zoomcrop = (TextView) view.findViewById(R.id.tv_next_zoomcrop);
    }

    public void clickListner(){
        tv_retake_zoomcrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.getSupportFragmentManager().popBackStack();
            }
        });
        tv_next_zoomcrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progress.show();

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        cropImage();
                        progress.dismiss();
                    }
                }, 3000); // 3000 milliseconds delay
                tv_next_zoomcrop.setTextColor(Color.GRAY);

            }
        });
    }

    public void cropImage() {
        if (!iv_crop_zoom.isChangingScale()) {

            Bitmap b = iv_crop_zoom.getCroppedImage();

            if (b != null) {
                try {
                    //Write file
                    String filename = "bitmap.png";
                    FileOutputStream stream = mainActivity.openFileOutput(filename, Context.MODE_PRIVATE);
                    b.compress(Bitmap.CompressFormat.PNG, 100, stream);

                    //Cleanup
                    stream.close();
                    b.recycle();

                    //Pop intent
                    MainActivity.changeFragment(new ImageInputs(mainActivity, filename), "imageinput");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(mainActivity, "failed to crop", Toast.LENGTH_SHORT).show();
            }
        }
        progress.dismiss();
    }

}
