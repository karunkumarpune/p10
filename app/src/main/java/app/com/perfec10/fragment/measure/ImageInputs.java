package app.com.perfec10.fragment.measure;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.Volley;
import com.theartofdev.edmodo.cropper.CropImage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

import app.com.perfec10.R;
import app.com.perfec10.activity.MainActivity;
import app.com.perfec10.fragment.home.CropZoomScreen;
import app.com.perfec10.helper.VolleyMultipartRequest;
import app.com.perfec10.network.Network;
import app.com.perfec10.network.NetworkCallBack;
import app.com.perfec10.network.NetworkConstants;
import app.com.perfec10.util.Model;
import app.com.perfec10.util.PreferenceManager;
import app.com.perfec10.util.Progress;

/**
 * Created by fluper on 6/11/17.
 */

@SuppressLint("ValidFragment")
public class ImageInputs extends Fragment implements NetworkCallBack, Animation.AnimationListener{
    private MainActivity mainActivity;
    private TextView tv_sw1_input, tv_sw2_input, tv_sw3_input, tv_sw4_input, tv_mine_input, tv_notmine_input, tv_male_input,
                    tv_female_input, tv_front_input, tv_back_input, tv_measure_input;
    private ImageView iv_zoomin_input, iv_zoomout_input, iv_crop_input, iv_show_input, iv_back_input;
    private Typeface bold, regular;
    private String pic="", angle="", gender="", filePath;
    private Bitmap bitImg;
    byte[] userImage;
    private Progress progress;
    Animation animation,animation2;
    int zoom = 0;
    String bmp = "";
    private String TAG = "ImageInputs";

    private PreferenceManager preferenceManager;

    public ImageInputs(MainActivity mainActivity, String bmp){
        this.mainActivity = mainActivity;
        this.bmp = bmp;
        preferenceManager = new PreferenceManager(mainActivity);
        bold = Typeface.createFromAsset(mainActivity.getAssets(), "fonts/Roboto-Bold.ttf");
        regular = Typeface.createFromAsset(mainActivity.getAssets(), "fonts/Roboto-Regular.ttf");
        progress = new Progress(mainActivity);
        progress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progress.setCancelable(false);
        progress.setCanceledOnTouchOutside(false);
        Log.d(TAG+"lenght of back btack ", mainActivity.getFragmentManager().getBackStackEntryCount()+" ");
    }

    public ImageInputs(){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.image_inputs, container, false);
        CropZoomScreen.backFrom = "imputImage";
        initView(view);
        clickListner();
        return view;
    }

    public void initView(View view){
        tv_sw1_input = (TextView) view.findViewById(R.id.tv_sw1_input);
        tv_sw2_input = (TextView) view.findViewById(R.id.tv_sw2_input);
        tv_sw3_input = (TextView) view.findViewById(R.id.tv_sw3_input);
        tv_sw4_input = (TextView) view.findViewById(R.id.tv_sw4_input);
        tv_mine_input = (TextView) view.findViewById(R.id.tv_mine_input);
        tv_notmine_input = (TextView) view.findViewById(R.id.tv_notmine_input);
        tv_male_input = (TextView) view.findViewById(R.id.tv_male_input);
        tv_female_input = (TextView) view.findViewById(R.id.tv_female_input);
        tv_front_input = (TextView) view.findViewById(R.id.tv_front_input);
        tv_back_input = (TextView) view.findViewById(R.id.tv_back_input);
        tv_measure_input = (TextView) view.findViewById(R.id.tv_measure_input);

        tv_sw1_input.setTypeface(regular);
        tv_sw2_input.setTypeface(regular);
        tv_sw3_input.setTypeface(regular);
        tv_sw4_input.setTypeface(regular);
        tv_mine_input.setTypeface(regular);
        tv_notmine_input.setTypeface(regular);
        tv_male_input.setTypeface(regular);
        tv_female_input.setTypeface(regular);
        tv_front_input.setTypeface(regular);
        tv_back_input.setTypeface(regular);
        tv_measure_input.setTypeface(bold);

        iv_zoomin_input = (ImageView) view.findViewById(R.id.iv_zoomin_input);
        iv_zoomout_input = (ImageView) view.findViewById(R.id.iv_zoomout_input);
        iv_crop_input = (ImageView) view.findViewById(R.id.iv_crop_input);
        iv_show_input = (ImageView) view.findViewById(R.id.iv_show_input);
        iv_back_input = (ImageView) view.findViewById(R.id.iv_back_input);
        try {
            FileInputStream is = mainActivity.openFileInput(bmp);
            bitImg = BitmapFactory.decodeStream(is);
            is.close();

            if (bmp!=null) {
                iv_show_input.setImageBitmap(bitImg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
      //  iv_show_input.setImageBitmap(bitImg);
      //  iv_show_input.setScaleType(ImageView.ScaleType.FIT_XY);
        animation= AnimationUtils.loadAnimation(mainActivity, R.anim.zoom_in);
        animation2= AnimationUtils.loadAnimation(mainActivity, R.anim.zoom_out);
        animation.setAnimationListener((Animation.AnimationListener) this);
    }

    public void clickListner(){
        iv_back_input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.getSupportFragmentManager().popBackStack();
            }
        });
        tv_mine_input.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                tv_mine_input.setBackgroundResource(R.color.colorPrimary);
                tv_mine_input.setTypeface(null, Typeface.BOLD);
                tv_notmine_input.setBackgroundResource(R.color.white);
                tv_notmine_input.setTypeface(null, Typeface.NORMAL);
                pic = "0";
            }
        });
        tv_notmine_input.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                tv_notmine_input.setBackgroundResource(R.color.colorPrimary);
                tv_notmine_input.setTypeface(null, Typeface.BOLD);
                tv_mine_input.setBackgroundResource(R.color.white);
                tv_mine_input.setTypeface(null, Typeface.NORMAL);
                pic = "1";

            }
        });
        tv_male_input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_male_input.setBackgroundResource(R.color.colorPrimary);
                tv_male_input.setTypeface(null, Typeface.BOLD);
                tv_female_input.setBackgroundResource(R.color.white);
                tv_female_input.setTypeface(null, Typeface.NORMAL);
                gender = "0";
            }
        });
        tv_female_input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_female_input.setBackgroundResource(R.color.colorPrimary);
                tv_female_input.setTypeface(null, Typeface.BOLD);
                tv_male_input.setBackgroundResource(R.color.white);
                tv_male_input.setTypeface(null, Typeface.NORMAL);
                gender = "1";
            }
        });
        tv_front_input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_front_input.setBackgroundResource(R.color.colorPrimary);
                tv_front_input.setTypeface(null, Typeface.BOLD);
                tv_back_input.setBackgroundResource(R.color.white);
                tv_back_input.setTypeface(null, Typeface.NORMAL);
                angle = "0";
            }
        });
        tv_back_input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_front_input.setBackgroundResource(R.color.white);
                tv_front_input.setTypeface(null, Typeface.NORMAL);
                tv_back_input.setBackgroundResource(R.color.colorPrimary);
                tv_back_input.setTypeface(null, Typeface.BOLD);
                angle = "1";
            }
        });
        iv_crop_input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              //  crop_Method(getImageUri(getActivity(), bitImg));
            }
        });
        tv_measure_input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pic.length() > 0){
                    if (gender.length() > 0){
                        if (angle.length() > 0){
                            if (Network.isConnected(mainActivity)){
                              //  sendUserInput();
                                progress.show();
                                measure();
                            }else {
                                Toast.makeText(mainActivity, "No Internet Connection", Toast.LENGTH_SHORT).show();
                            }

                        }else {
                            Toast.makeText(mainActivity, "Please select Angle", Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(mainActivity, "Please select Gender", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(mainActivity, "Please select picture Type", Toast.LENGTH_SHORT).show();
                }

                //MainActivity.changeFragment(new Stats(mainActivitySignUP), "stats");
            }
        });
        iv_zoomin_input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (zoom==0){
                    iv_show_input.setScaleX((float) 1.1);
                    iv_show_input.setScaleY((float) 1.1);
                    zoom = 1;
                }else if (zoom==1){
                    iv_show_input.setScaleX((float) 1.2);
                    iv_show_input.setScaleY((float) 1.2);
                    zoom = 2;
                }else if (zoom==2){
                    iv_show_input.setScaleX((float) 1.3);
                    iv_show_input.setScaleY((float) 1.3);
                    zoom = 3;
                }else if (zoom==3){
                    iv_show_input.setScaleX((float) 1.4);
                    iv_show_input.setScaleY((float) 1.4);
                    zoom = 4;
                }else if (zoom==4){
                    iv_show_input.setScaleX((float) 1.5);
                    iv_show_input.setScaleY((float) 1.5);
                    zoom = 5;
                }else if (zoom==5){
                    iv_show_input.setScaleX((float) 1.6);
                    iv_show_input.setScaleY((float) 1.6);
                    zoom = 6;
                }else if (zoom==6){
                    iv_show_input.setScaleX((float) 1.7);
                    iv_show_input.setScaleY((float) 1.7);
                    zoom = 7;
                }else if (zoom==7){
                    iv_show_input.setScaleX((float) 1.8);
                    iv_show_input.setScaleY((float) 1.8);
                    zoom = 8;
                }else if (zoom==8){
                    iv_show_input.setScaleX((float) 1.9);
                    iv_show_input.setScaleY((float) 1.9);
                    zoom = 9;
                }else if (zoom==9){
                    iv_show_input.setScaleX((float) 2);
                    iv_show_input.setScaleY((float) 2);

                }

               // iv_show_input.startAnimation(animation);
            }
        });
        iv_zoomout_input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (zoom==9){
                    iv_show_input.setScaleX((float) 1.9);
                    iv_show_input.setScaleY((float) 1.9);
                    zoom = 8;
                }else if (zoom==8){
                    iv_show_input.setScaleX((float) 1.8);
                    iv_show_input.setScaleY((float) 1.8);
                    zoom = 7;
                }else if (zoom==7){
                    iv_show_input.setScaleX((float) 1.7);
                    iv_show_input.setScaleY((float) 1.7);
                    zoom = 6;
                }else if (zoom==6){
                    iv_show_input.setScaleX((float) 1.6);
                    iv_show_input.setScaleY((float) 1.6);
                    zoom = 5;
                }else if (zoom==5){
                    iv_show_input.setScaleX((float) 1.5);
                    iv_show_input.setScaleY((float) 1.5);
                    zoom = 4;
                }else if (zoom==4){
                    iv_show_input.setScaleX((float) 1.4);
                    iv_show_input.setScaleY((float) 1.4);
                    zoom = 3;
                }else if (zoom==3){
                    iv_show_input.setScaleX((float) 1.3);
                    iv_show_input.setScaleY((float) 1.3);
                    zoom = 2;
                }else if (zoom==2){
                    iv_show_input.setScaleX((float) 1.2);
                    iv_show_input.setScaleY((float) 1.2);
                    zoom = 1;
                }else if (zoom==1){
                    iv_show_input.setScaleX((float) 1.1);
                    iv_show_input.setScaleY((float) 1.1);
                    zoom = 0;
                }else if (zoom==0){
                    iv_show_input.setScaleX((float) 1);
                    iv_show_input.setScaleY((float) 1);

                }
            }
        });
    }

    public void sendUserInput(){

        if (Network.isConnected(mainActivity)){
            Bundle bundlereq = new Bundle();
            bundlereq.putString("picture", pic);
            bundlereq.putString("gender", gender);
            bundlereq.putString("angle", angle);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitImg.compress(Bitmap.CompressFormat.JPEG, 80, baos);
            userImage = baos.toByteArray();
            bundlereq.putString("image", String.valueOf(userImage));
            progress.show();
            Log.d(TAG+" params user input ", bundlereq+" ");
           /* Network.measureImage(mainActivitySignUP, bundlereq, 20 * 1000,
                    NetworkConstants.userInputUrl, this, 2);*/

          //  mainActivitySignUP.getFragmentManager().popBackStack();
           // MainActivity.changeFragment(new Stats(mainActivitySignUP), "stats");

            mainActivity.fragmentManager = mainActivity.getSupportFragmentManager();
          //  mainActivitySignUP.fragmentManager.beginTransaction().replace(R.id.main_frame, new Stats(mainActivitySignUP)).commit();
        }else {
            Toast.makeText(mainActivity, "No Internt Connection ", Toast.LENGTH_SHORT).show();
        }
    }

    public void measure() {
        // loading or check internet connection or something...
        // ... then
        RequestQueue requestQueue = Volley.newRequestQueue(mainActivity);
        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, NetworkConstants.userInputUrl,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        String resultResponse = new String(response.data);
                        Log.d(TAG+" measure response", "" + resultResponse);
                        JSONObject jsonObject = Model.getObject(resultResponse);
                        try {
                            String message = jsonObject.getString("message");
                            if (message.equals("Successful")){
                                //JSONArray result = jsonObject.getJSONArray("result");

                                JSONObject json = jsonObject.getJSONObject("result");
                                String input_id = json.getString("input_id");
                                String bust_waist = json.getString("bust_waist");
                                String waist_hips = json.getString("waist_hips");
                                String legs_body = json.getString("legs_body");
                                String body_waist = json.getString("body_waist");
                                String shoulder_hips = json.getString("shoulder_hips");
                                String score = json.getString("score");
                                String picture = json.getString("picture");
                                String angle = json.getString("angle");
                                String postId = json.getString("post_id");

                                if (picture.equals("1")){
                                    score = "0";
                                }

                                mainActivity.fragmentManager = mainActivity.getSupportFragmentManager();
                                mainActivity.fragmentManager.beginTransaction().replace(R.id.main_frame,
                                        new Stats(mainActivity, bust_waist, waist_hips, legs_body, body_waist
                                        , shoulder_hips, score, picture, angle, input_id, postId, gender)).commit();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        progress.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                /*NetworkResponse networkResponse = error.networkResponse;
                String result = new String(networkResponse.data);
                String errorMessage = "Unknown error";*/
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Log.d(TAG+" error ocurred", "TimeoutError");
                    Toast.makeText(mainActivity, "Please try later", Toast.LENGTH_SHORT).show();
                    //    Toast.makeText(mainActivitySignUP, "Internet connection is slow", Toast.LENGTH_LONG).show();
                } else if (error instanceof AuthFailureError) {
                    Log.d(TAG+" error ocurred", "AuthFailureError");
                    Toast.makeText(mainActivity, "Your session has been expired.", Toast.LENGTH_SHORT).show();
                    //    Toast.makeText(mainActivitySignUP, "Internet connection is slow", Toast.LENGTH_LONG).show();
                } else if (error instanceof ServerError) {
                    Log.d(TAG+" error ocurred", "ServerError");
                    //    Toast.makeText(getApplicationContext(), "Server Error", Toast.LENGTH_LONG).show();
                } else if (error instanceof NetworkError) {
                    Log.d(TAG+" error ocurred", "NetworkError");
                    Toast.makeText(mainActivity, "Network Error", Toast.LENGTH_LONG).show();
                } else if (error instanceof ParseError) {
                    Log.d(TAG+" error ocurred", "ParseError");
                    //    Toast.makeText(mainActivitySignUP, "Internet connection is slow", Toast.LENGTH_LONG).show();
                }

                progress.dismiss();
            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> mHeader = new ArrayMap<>();
                mHeader.put("accessToken", preferenceManager.getUserAuthkey());
                return mHeader;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                    params.put("picture", pic);
                    params.put("gender", gender);
                    params.put("angle", angle);

                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                // file name could found file base or direct access from real path
                // for now just get bitmap data from ImageView
                ByteArrayOutputStream baosInvoice = new ByteArrayOutputStream();
                bitImg.compress(Bitmap.CompressFormat.JPEG, 80, baosInvoice);
                userImage = baosInvoice.toByteArray();

                params.put("image", new DataPart("image.jpg", userImage, "image/jpeg"));

                return params;
            }
        };

        multipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                800000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(multipartRequest);
    }

    @Override
    public void onSuccess(Object data, int requestCode, int statusCode) {
        progress.dismiss();
        Log.e(TAG+" image ", "result: " + data);
    }

    @Override
    public void onError(String msg) {

    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public void crop_Method(Uri imageUri) {
        CropImage.activity(imageUri).start(getContext(), this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                Log.d(TAG+" called ", "activity "+data);
                CropImage.ActivityResult result1 = CropImage.getActivityResult(data);
                Uri uri = result1.getUri();
                iv_show_input.setImageURI(uri);
                //File fileLocation = new File(String.valueOf(uri)); //file path, which can be String, or Uri

                //Picasso.with(mainActivitySignUP).load(fileLocation).into(iv_show_input);
                if (resultCode == 0) {
                    Uri resultUri = result.getUri();
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            iv_show_input.setImageBitmap(bitImg);
        }
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {

    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}
