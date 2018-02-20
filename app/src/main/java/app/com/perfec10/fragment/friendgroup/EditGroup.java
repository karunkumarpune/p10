package app.com.perfec10.fragment.friendgroup;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.github.javiersantos.bottomdialogs.BottomDialog;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import app.com.perfec10.R;
import app.com.perfec10.activity.MainActivity;
import app.com.perfec10.fragment.measure.Stats;
import app.com.perfec10.fragment.profile.Profile;
import app.com.perfec10.helper.CustomVolleyRequestQueue;
import app.com.perfec10.helper.RoundedImageView;
import app.com.perfec10.helper.VolleyMultipartRequest;
import app.com.perfec10.network.Network;
import app.com.perfec10.network.NetworkConstants;
import app.com.perfec10.util.Model;
import app.com.perfec10.util.PreferenceManager;
import app.com.perfec10.util.Progress;

import static android.app.Activity.RESULT_OK;

/**
 * Created by fluper on 30/11/17.
 */

@SuppressLint("ValidFragment")
public class EditGroup extends Fragment {
    private MainActivity mainActivity;
    private String groupId;
    private ImageView iv_check_newsub;
    private TextView tv_sw1_newsub;
    private RoundedImageView riv_add_img_newsub;
    private EditText et_subject_newsub;
    private Uri outPutfileUri;
    private static final int SELECT_PICTURE = 100;
    static int TAKE_PIC = 1;
    private Progress progress;
    private PreferenceManager preferenceManager;
    private Bitmap bitImg;
    byte[] userImage;
    private String grpname, imgUrl;
    private ImageLoader imageLoader;
    private String TAG = "EditGroup";

    public EditGroup(MainActivity mainActivity, String groupId, String groupname, String imgUrl){
        this.mainActivity = mainActivity;
        this.groupId = groupId;
        this.grpname = groupname;
        this.imgUrl = imgUrl;
        preferenceManager = new PreferenceManager(mainActivity);
        progress = new Progress(mainActivity);
        progress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progress.setCancelable(false);
        progress.setCanceledOnTouchOutside(false);
    }

    public EditGroup(){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.edit_group, container, false);
        initView(view);
        clickListner();
        return view;
    }

    public void initView(View view){
        riv_add_img_newsub = (RoundedImageView) view.findViewById(R.id.riv_add_img_newsub);
        iv_check_newsub = (ImageView) view.findViewById(R.id.iv_check_newsub);

        tv_sw1_newsub = (TextView) view.findViewById(R.id.tv_sw1_newsub);

        et_subject_newsub = (EditText) view.findViewById(R.id.et_subject_newsub);
        et_subject_newsub.setText(grpname);
       // Picasso.with(mainActivitySignUP).load(imgUrl).into(riv_add_img_newsub);
        imageLoader = CustomVolleyRequestQueue.getInstance(mainActivity).getImageLoader();
        imageLoader.get(NetworkConstants.imageBaseUrl+imgUrl, ImageLoader.getImageListener(riv_add_img_newsub,
                R.mipmap.add_image, R.mipmap.add_image));
        riv_add_img_newsub.setImageUrl(NetworkConstants.imageBaseUrl+imgUrl, imageLoader);
        bitImg = getBitmapFromURL(NetworkConstants.imageBaseUrl+imgUrl);
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            return image;
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void clickListner(){
        riv_add_img_newsub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            callDialog();
            }
        });
        iv_check_newsub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (et_subject_newsub.getText().toString().length() > 0){

                    if (Network.isConnected(mainActivity)){
                        progress.show();
                        editGroup();
                    }else {
                        Toast.makeText(mainActivity, "No Internet Connection", Toast.LENGTH_SHORT).show();
                    }

                }else {
                    Toast.makeText(mainActivity, "Please enter subject", Toast.LENGTH_SHORT).show();
                }
            }
        });
        tv_sw1_newsub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Profile.fragmentManager.popBackStack();
            }
        });
    }

    public void callDialog() {
        new BottomDialog.Builder(mainActivity)
                .setTitle("Image Chooser !")
                .setContent("Choose Image from Camera or Gallery")
                .setIcon(R.mipmap.logo)
                .setPositiveText("Camera")
                .setNegativeText("Gallery")
                .onPositive(new BottomDialog.ButtonCallback() {
                    @Override
                    public void onClick(BottomDialog dialog) {
                        takeImageFromCamera();
                    }
                })
                .onNegative(new BottomDialog.ButtonCallback() {
                    @Override
                    public void onClick(BottomDialog dialog) {
                        choosefromgallery();
                    }
                })
                .show();
    }

    public void takeImageFromCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = new File(Environment.getExternalStorageDirectory(), String.valueOf(System.currentTimeMillis()) + ".jpg");
        outPutfileUri = Uri.fromFile(file);
        String uriString = outPutfileUri.toString();
        File myFile = new File(uriString);
        String path = myFile.getAbsolutePath();
        // this method is used to get pic name
        // getPicName(path, outPutfileUri, myFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outPutfileUri);
        startActivityForResult(intent, TAKE_PIC);
    }

    public void choosefromgallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, SELECT_PICTURE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == TAKE_PIC && resultCode == RESULT_OK) {
            if (null != outPutfileUri) {
                crop_Method(outPutfileUri);
            }
        }

        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    crop_Method(selectedImageUri);
                }
            }
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                File file = null;
                String path = resultUri.toString();
                Bitmap bitmap = null;
                try {
                    file = new File(new URI(path));
                    riv_add_img_newsub.setImageURI(resultUri);
                    //  this.path = file.getPath();
                    // this method is used to get pic name
                    //  getPicName(path, resultUri, file);
                    Log.d(TAG+" profilePicPath", "file " + file.getPath());
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), resultUri);
                    if (bitmap != null) {
                        Drawable d = new BitmapDrawable(getResources(), bitmap);
                        bitImg = bitmap;
//                        user_pic_round.setImageDrawable(d);
                        //    invoiceBitmap = bitmap;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    public void crop_Method(Uri imageUri) {
        CropImage.activity(imageUri).start(getContext(), this);
    }

    public void editGroup() {
        // loading or check internet connection or something...
        // ... then
        RequestQueue requestQueue = Volley.newRequestQueue(mainActivity);
        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, NetworkConstants.editGroup,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        String resultResponse = new String(response.data);
                        Log.d(TAG+" edit grp response", "" + resultResponse);
                        JSONObject jsonObject = Model.getObject(resultResponse);
                        try {
                            String message = jsonObject.getString("message");
                            Toast.makeText(mainActivity, message, Toast.LENGTH_SHORT).show();
                            Profile.fragmentManager.popBackStack();
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
                VolleyLog.d(TAG+" edit", "Error: " + error.getMessage());
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

                params.put("name", et_subject_newsub.getText().toString());
                params.put("group_id", groupId);

                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                // file name could found file base or direct access from real path
                // for now just get bitmap data from ImageView
                ByteArrayOutputStream baosInvoice = new ByteArrayOutputStream();
                if (bitImg != null) {
                    Log.e(TAG+" image is not null", " ");
                    bitImg.compress(Bitmap.CompressFormat.JPEG, 80, baosInvoice);
                    userImage = baosInvoice.toByteArray();
                    params.put("image", new DataPart("image.jpg", userImage, "image/jpeg"));
                } else {
                    Log.e(TAG+" image is null", " ");
                    userImage = baosInvoice.toByteArray();

                }
                /*bitImg.compress(Bitmap.CompressFormat.JPEG, 80, baosInvoice);
                userImage = baosInvoice.toByteArray();*/
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
}
