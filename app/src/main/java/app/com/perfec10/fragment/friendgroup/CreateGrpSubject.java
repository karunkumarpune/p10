package app.com.perfec10.fragment.friendgroup;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.android.volley.toolbox.Volley;
import com.github.javiersantos.bottomdialogs.BottomDialog;
import com.makeramen.roundedimageview.RoundedImageView;
import com.theartofdev.edmodo.cropper.CropImage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import app.com.perfec10.R;
import app.com.perfec10.activity.MainActivity;
import app.com.perfec10.fragment.friendgroup.adapter.CreateGroupFriendsAdapter;
import app.com.perfec10.fragment.friendgroup.adapter.HorizontalSelectAdaptor;
import app.com.perfec10.fragment.profile.Profile;
import app.com.perfec10.helper.VolleyMultipartRequest;
import app.com.perfec10.model.FriendListGS;
import app.com.perfec10.network.NetworkConstants;
import app.com.perfec10.util.Model;
import app.com.perfec10.util.PreferenceManager;
import app.com.perfec10.util.Progress;

import static android.app.Activity.RESULT_OK;

/**
 * Created by fluper on 1/12/17.
 */

@SuppressLint("ValidFragment")
public class CreateGrpSubject extends Fragment {
    private MainActivity mainActivity;
    private ArrayList<FriendListGS> selectList;
    private int selctedNo;
    private ImageView iv_back_crtgrpsub, iv_check_crtgrpsub;
    private TextView tv_sw1_crtgrpsub, tv_sw2_crtgrpsub;
    private RoundedImageView riv_add_img_crtgrpsub;
    private EditText et_subject_crtgrpsub;
    public static TextView tv_particitants_crtgrpsub;
    private RecyclerView rv_crt_selected_crtgrpsub;
    private LinearLayoutManager linearLayoutManager;
    private Typeface regular, bold;
    private int size;
    private Uri outPutfileUri;
    private static final int SELECT_PICTURE = 100;
    static int TAKE_PIC = 1;
    private Bitmap bitImg;
    byte[] userImage;
    private PreferenceManager preferenceManager;
    private Progress progress;
    private ArrayList<String> userId;
    private String TAG = "CreateGrpSubject";


    public CreateGrpSubject(MainActivity mainActivity, ArrayList<FriendListGS> selectList, int selctedNo){
        this.mainActivity = mainActivity;
        this.selectList = selectList;
        this.selctedNo = selctedNo;
        bold = Typeface.createFromAsset(mainActivity.getAssets(), "fonts/Roboto-Bold.ttf");
        regular = Typeface.createFromAsset(mainActivity.getAssets(), "fonts/Roboto-Regular.ttf");
        Log.d(TAG+" from subject ", selectList.size()+" ");
        size = selectList.size();
        preferenceManager = new PreferenceManager(mainActivity);
        progress = new Progress(mainActivity);
        progress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progress.setCancelable(false);
        progress.setCanceledOnTouchOutside(false);
        CreateGroup.backFrom = "sub";
    }

    public CreateGrpSubject(){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.newgrp_subject, container, false);
        initView(view);
        clickListner();
        return view;
    }

    public void initView(View view){
        iv_back_crtgrpsub = (ImageView) view.findViewById(R.id.iv_back_crtgrpsub);
        iv_check_crtgrpsub = (ImageView) view.findViewById(R.id.iv_check_crtgrpsub);

        tv_sw1_crtgrpsub = (TextView) view.findViewById(R.id.tv_sw1_crtgrpsub);
        tv_sw2_crtgrpsub = (TextView) view.findViewById(R.id.tv_sw2_crtgrpsub);
        tv_particitants_crtgrpsub = (TextView) view.findViewById(R.id.tv_particitants_crtgrpsub);

        riv_add_img_crtgrpsub = (RoundedImageView) view.findViewById(R.id.riv_add_img_crtgrpsub);

        et_subject_crtgrpsub = (EditText) view.findViewById(R.id.et_subject_crtgrpsub);

        rv_crt_selected_crtgrpsub = (RecyclerView) view.findViewById(R.id.rv_crt_selected_crtgrpsub);
        linearLayoutManager = new LinearLayoutManager(mainActivity, LinearLayoutManager.HORIZONTAL, false);
        rv_crt_selected_crtgrpsub.setLayoutManager(linearLayoutManager);
        CreateGroupFriendsAdapter.selectedContactList = selectList;
        HorizontalSelectAdaptor horizontalRecyclerAdapter = new HorizontalSelectAdaptor(mainActivity, "subject");
        rv_crt_selected_crtgrpsub.setAdapter(horizontalRecyclerAdapter);
        horizontalRecyclerAdapter.notifyDataSetChanged();

        tv_sw1_crtgrpsub.setTypeface(bold);
        tv_sw2_crtgrpsub.setTypeface(regular);
        tv_particitants_crtgrpsub.setTypeface(bold);

        tv_particitants_crtgrpsub.setText("Participants: "+size+" / 150");
    }

    public void clickListner(){
        iv_back_crtgrpsub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Profile.fragmentManager.popBackStack();
            }
        });
        iv_check_crtgrpsub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> str;
                str = new ArrayList<>();
                userId = new ArrayList<>();
                for (int i = 0; i < CreateGroupFriendsAdapter.selectedContactList.size(); i++){
                    String a = CreateGroupFriendsAdapter.selectedContactList.get(i).getUserId();
                    Log.d(TAG+" user ids ", CreateGroupFriendsAdapter.selectedContactList.get(i).getUserId());
                    userId.add(a);

                }
                Log.d(TAG+" selected ids ", userId+" ");
                if (userId.size() > 0){
                if (et_subject_crtgrpsub.getText().toString().length() > 0){
                 //   if (bitImg != null){
                        progress.show();
                        createGrp();
                    /*}else {
                        Toast.makeText(mainActivitySignUP, "Please select the Group Image ", Toast.LENGTH_SHORT).show();
                    }*/
                }else {
                    Toast.makeText(mainActivity, "Please enter Group Subject ", Toast.LENGTH_SHORT).show();
                }
                }else {
                    Toast.makeText(mainActivity, "Please select Participants ", Toast.LENGTH_SHORT).show();
                }

            }
        });
        riv_add_img_crtgrpsub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callDialog();
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
                    riv_add_img_crtgrpsub.setImageURI(resultUri);
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

    public void createGrp() {
        // loading or check internet connection or something...
        // ... then
        RequestQueue requestQueue = Volley.newRequestQueue(mainActivity);
        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, NetworkConstants.createGroup,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        String resultResponse = new String(response.data);
                        Log.d(TAG+" crt grp response", "" + resultResponse);
                        JSONObject jsonObject = Model.getObject(resultResponse);
                        try {
                            String message = jsonObject.getString("message");
                            if (message.contains("successfully")){
                                //JSONArray result = jsonObject.getJSONArray("result");
                                Toast.makeText(mainActivity, message, Toast.LENGTH_SHORT).show();
                                for (int i = 0; i < 2; i++){
                                    Profile.fragmentManager.popBackStack();
                                }

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
                VolleyLog.d(TAG+" edit", "Error: " + error.getMessage());
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Log.d(TAG+ " error ocurred", "TimeoutError");
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

                params.put("name", et_subject_crtgrpsub.getText().toString());
                params.put("user_id", userId+"");

                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                // file name could found file base or direct access from real path
                // for now just get bitmap data from ImageView
                    if (bitImg != null){
                        ByteArrayOutputStream baosInvoice = new ByteArrayOutputStream();
                        bitImg.compress(Bitmap.CompressFormat.JPEG, 80, baosInvoice);
                        userImage = baosInvoice.toByteArray();

                        params.put("image", new DataPart("image.jpg", userImage, "image/jpeg"));

                    }else {
                        Bitmap icon = BitmapFactory.decodeResource(getResources(),R.mipmap.add_image);
                        ByteArrayOutputStream baosInvoice = new ByteArrayOutputStream();
                        icon.compress(Bitmap.CompressFormat.JPEG, 80, baosInvoice);
                        userImage = baosInvoice.toByteArray();
                        params.put("image", new DataPart("image.jpg", userImage, "image/jpeg"));
                    }

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
