package app.com.perfec10.fragment.profile;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArrayMap;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import app.com.perfec10.R;
import app.com.perfec10.activity.MainActivity;
import app.com.perfec10.fragment.home.Home;
import app.com.perfec10.fragment.measure.Stats;
import app.com.perfec10.helper.CustomVolleyRequestQueue;
import app.com.perfec10.helper.VolleyMultipartRequest;
import app.com.perfec10.network.Network;
import app.com.perfec10.network.NetworkConstants;
import app.com.perfec10.util.GPSTracker;
import app.com.perfec10.util.Model;
import app.com.perfec10.util.PreferenceManager;
import app.com.perfec10.util.Progress;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

/**
 * Created by fluper on 21/11/17.
 */

@SuppressLint("ValidFragment")
public class EditProfile extends Fragment implements GoogleApiClient.OnConnectionFailedListener {
    private MainActivity mainActivity;
    private TextView tv_sw1_edprofile, tv_sw2_edprofile, tv_sw3_edprofile, tv_sw4_edprofile, tv_sw5_edprofile,
            tv_sw6_edprofile, tv_sw7_edprofile, tv_sw8_edprofile, tv_sw9_edprofile, tv_sw10_edprofile,
            tv_sw11_edprofile, tv_namehead_etprofile, tv_postno_edprofile, tv_frndno_edprofile,
            tv_email_edprofile, tv_location_edprofile;
    private EditText et_name_edprofile, et_age_edprofile, et_race_edprofile;
    private Spinner sp_gender_edprofile;
    private String post, frnd, name, location, age, gende, race, imageUrl, genderStr;
    private LinearLayout ll_location_edprofile, ll_frnds_edprofile, ll_setting_edprofile;
    private PreferenceManager preferenceManager;
    private CircleImageView civ_edprofile;
    private RelativeLayout rv_edit_img;
    private Uri outPutfileUri;
    private ImageView iv_save_etprofile, iv_back_etprofile;
    private static final int SELECT_PICTURE = 100;
    static int TAKE_PIC = 1;
    String[] gender = {"Select Gender", "Male", "Female"};
    private Progress progress;
    private Bitmap bitImg;
    byte[] userImage;
    private Geocoder geocoder;
    List<Address> addresses;
    private GoogleApiClient mGoogleApiClient;
    private int PLACE_PICKER_REQUEST = 1;
    private Typeface regular, bold;
    private ImageLoader imageLoader;
    private String TAG = "EditProfile";

    public static ArrayList<Long> social_id=new ArrayList<>();

    public EditProfile(MainActivity mainActivity, String post, String frnd, String name,
                       String location, String age, String gender, String race, String imageUrl) {
        this.mainActivity = mainActivity;
        this.post = post;
        this.frnd = frnd;
        this.name = name;
        this.location = location;
        this.age = age;
        this.gende = gender;
        this.race = race;
        this.imageUrl = imageUrl;
        preferenceManager = new PreferenceManager(mainActivity);
        progress = new Progress(mainActivity);
        progress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progress.setCancelable(false);
        progress.setCanceledOnTouchOutside(false);
        bold = Typeface.createFromAsset(mainActivity.getAssets(), "fonts/Roboto-Bold.ttf");
        regular = Typeface.createFromAsset(mainActivity.getAssets(), "fonts/Roboto-Regular.ttf");
        Log.d(TAG+" location ", location);
        Log.d(TAG+" gender ", gende+" ");
        genderStr = gende;
        Home.editUserId = "786";
    }

    public EditProfile() {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mGoogleApiClient = mainActivity.getGoogleApiClient();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        /*mGoogleApiClient = new GoogleApiClient
                .Builder(mainActivitySignUP)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(mainActivitySignUP, this)
                .build();*/
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.edit_profile, container, false);
        initView(view);
        clickListner();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        mGoogleApiClient.stopAutoManage(getActivity());
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mGoogleApiClient = null;
    }

    public void initView(View view) {
        civ_edprofile = (CircleImageView) view.findViewById(R.id.civ_edprofile);

        tv_sw1_edprofile = (TextView) view.findViewById(R.id.tv_sw1_edprofile);
        tv_sw2_edprofile = (TextView) view.findViewById(R.id.tv_sw2_edprofile);
        tv_sw3_edprofile = (TextView) view.findViewById(R.id.tv_sw3_edprofile);
        tv_sw4_edprofile = (TextView) view.findViewById(R.id.tv_sw4_edprofile);
        tv_sw5_edprofile = (TextView) view.findViewById(R.id.tv_sw5_edprofile);
        tv_sw6_edprofile = (TextView) view.findViewById(R.id.tv_sw6_edprofile);
        tv_sw7_edprofile = (TextView) view.findViewById(R.id.tv_sw7_edprofile);
        tv_sw8_edprofile = (TextView) view.findViewById(R.id.tv_sw8_edprofile);
        tv_sw9_edprofile = (TextView) view.findViewById(R.id.tv_sw9_edprofile);
        tv_sw10_edprofile = (TextView) view.findViewById(R.id.tv_sw10_edprofile);
        tv_sw11_edprofile = (TextView) view.findViewById(R.id.tv_sw11_edprofile);
        tv_namehead_etprofile = (TextView) view.findViewById(R.id.tv_namehead_etprofile);
        tv_postno_edprofile = (TextView) view.findViewById(R.id.tv_postno_edprofile);
        tv_frndno_edprofile = (TextView) view.findViewById(R.id.tv_frndno_edprofile);
        tv_email_edprofile = (TextView) view.findViewById(R.id.tv_email_edprofile);
        tv_location_edprofile = (TextView) view.findViewById(R.id.tv_location_edprofile);

        et_name_edprofile = (EditText) view.findViewById(R.id.et_name_edprofile);
        et_age_edprofile = (EditText) view.findViewById(R.id.et_age_edprofile);
        et_race_edprofile = (EditText) view.findViewById(R.id.et_race_edprofile);

        rv_edit_img = (RelativeLayout) view.findViewById(R.id.rv_edit_img);

        ll_location_edprofile = (LinearLayout) view.findViewById(R.id.ll_location_edprofile);
        ll_setting_edprofile = (LinearLayout) view.findViewById(R.id.ll_setting_edprofile);
        ll_frnds_edprofile = (LinearLayout) view.findViewById(R.id.ll_frnds_edprofile);

        sp_gender_edprofile = (Spinner) view.findViewById(R.id.sp_gender_edprofile);

        iv_save_etprofile = (ImageView) view.findViewById(R.id.iv_save_etprofile);
        iv_back_etprofile = (ImageView) view.findViewById(R.id.iv_back_etprofile);

        ArrayAdapter aa = new ArrayAdapter(mainActivity, android.R.layout.simple_spinner_item, gender);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        sp_gender_edprofile.setAdapter(aa);
        Log.d(TAG+" gender ", gende+" ");
        if(gende.equals("Male")){
            sp_gender_edprofile.setSelection(1);
        }else if (gende.equals("Female")){
            sp_gender_edprofile.setSelection(2);
        }
        tv_sw1_edprofile.setTypeface(regular);
        tv_sw2_edprofile.setTypeface(regular);
        tv_sw3_edprofile.setTypeface(regular);
        tv_sw4_edprofile.setTypeface(regular);
        tv_sw5_edprofile.setTypeface(regular);
        tv_sw6_edprofile.setTypeface(regular);
        tv_sw7_edprofile.setTypeface(regular);
        tv_sw8_edprofile.setTypeface(regular);
        tv_sw9_edprofile.setTypeface(regular);
        tv_sw10_edprofile.setTypeface(regular);
        tv_sw11_edprofile.setTypeface(regular);
        tv_namehead_etprofile.setTypeface(regular);
        tv_postno_edprofile.setTypeface(bold);
        tv_frndno_edprofile.setTypeface(bold);
        tv_email_edprofile.setTypeface(regular);
        tv_location_edprofile.setTypeface(regular);
        et_name_edprofile.setTypeface(regular);
        et_age_edprofile.setTypeface(regular);
        et_race_edprofile.setTypeface(regular);

        tv_email_edprofile.setText(preferenceManager.getKeyUserEmailId());
        tv_postno_edprofile.setText(post);
        tv_frndno_edprofile.setText(frnd);
        tv_namehead_etprofile.setText(name);
        et_name_edprofile.setText(name);
        tv_location_edprofile.setText(location);
        et_age_edprofile.setText(age);
        et_race_edprofile.setText(race);

        if (Stats.location != null && Stats.location.length() > 0) {
            //  tv_location_edprofile.setText(Stats.location);
        } else {
            geocoder = new Geocoder(mainActivity, Locale.getDefault());
            GPSTracker gpsTracker = new GPSTracker(mainActivity);
            double latitude = gpsTracker.getLatitude();
            double longitude = gpsTracker.getLongitude();

            try {
                addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();
                String knownName = addresses.get(0).getFeatureName();
                // tv_location_edprofile.setText(address);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            imageLoader = CustomVolleyRequestQueue.getInstance(mainActivity).getImageLoader();
            imageLoader.get(imageUrl, ImageLoader.getImageListener(civ_edprofile,
                            R.mipmap.user, R.mipmap.user));
          //  civ_edprofile.setImageUrl(imageUrl, imageLoader);

            Picasso.with(mainActivity).load(imageUrl).into(civ_edprofile);
            Picasso.with(mainActivity).load(imageUrl).into(iv_back_etprofile);

        } catch (Exception e) {

            e.printStackTrace();
        }
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
        try {
            URL url = new URL(imageUrl);
            bitImg = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            Log.d(TAG+" from profile ", bitImg + " ");
        } catch (Exception e) {
            Log.e(TAG+" error ", e + "");
        }
        edit_Text_Focus_Listner();
        edit_Text_Focus_Listner1();
        edit_Text_Focus_Listner2();
    }

    public void edit_Text_Focus_Listner() {
        et_name_edprofile.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (et_name_edprofile.getText().toString().length() > 0){
                    checkChanges();
                }
                //  search_result(jsonObject.toString());
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
        });
    }

    public void edit_Text_Focus_Listner1() {
        et_age_edprofile.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (et_age_edprofile.getText().toString().length() > 0){
                    checkChanges();
                }
                //  search_result(jsonObject.toString());
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
        });
    }

    public void edit_Text_Focus_Listner2() {
        et_race_edprofile.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (et_race_edprofile.getText().toString().length() > 0){
                    checkChanges();
                }
                //  search_result(jsonObject.toString());
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
        });
    }


    public void clickListner() {
        rv_edit_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callDialog();
            }
        });
        iv_save_etprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Network.isConnected(mainActivity)) {
                    if (et_name_edprofile.getText().toString().length() > 0) {
                        MainActivity.save = false;
                        progress.show();
                        updateProfile();
                    } else {
                        et_name_edprofile.setError("Please enter Name");
                    }
                } else {
                    Toast.makeText(mainActivity, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ll_location_edprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(mainActivity), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });
        ll_frnds_edprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (checkChanges() || MainActivity.save) {
                    confirmSave(new FriendsList(mainActivity));
                    // Toast.makeText(mainActivitySignUP, "Please save the changes first", Toast.LENGTH_SHORT).show();
                } else {
                    Profile.fragmentManager = mainActivity.getSupportFragmentManager();
                    Profile.fragmentManager.popBackStack();
                    Profile.fragmentManager.beginTransaction().replace(R.id.profile_fram, new FriendsList(mainActivity)).addToBackStack("friends").commit();
                }//checkChanges();
            }
        });
        ll_setting_edprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (checkChanges()|| MainActivity.save) {
                    confirmSave(new Settings(mainActivity));
                    // Toast.makeText(mainActivitySignUP, "Please save the changes first", Toast.LENGTH_SHORT).show();
                } else {
                    Profile.fragmentManager = mainActivity.getSupportFragmentManager();
                    Profile.fragmentManager.popBackStack();
                    Profile.fragmentManager.beginTransaction().replace(R.id.profile_fram, new Settings(mainActivity)).addToBackStack("friends").commit();
                }
            }
        });
        sp_gender_edprofile.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (i == 1) {
                    gende = "0";

                } else if (i == 2) {
                    gende = "1";

                    Log.d(TAG+" female", "gen");
                }
                checkChanges();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public boolean checkChanges() {
        boolean b = false;
        if (name.equals(et_name_edprofile.getText().toString())) {
            Log.d(TAG+" same ", "name");
            if (location.equals(tv_location_edprofile.getText().toString())) {
                Log.d(TAG+" same ", "location");
                if (age.equals(et_age_edprofile.getText().toString())) {
                    Log.d(TAG+" same ", "age");
                    if (race.equals(et_race_edprofile.getText().toString())) {
                        Log.d(TAG+" same ", "race");
                        if (gende.length()==0){
                            if (sp_gender_edprofile.getSelectedItemPosition()==0){
                                Log.d(TAG+" same ", "gender");
                            }else {
                                Log.d(TAG+" chaaneg", "gender");
                                b = true;
                                MainActivity.save = true;                            }
                        }else {
                            if (genderStr.equalsIgnoreCase(sp_gender_edprofile.getSelectedItem().toString())){
                                Log.d(TAG+" same ", "gender");
                            }else {
                                Log.d(TAG+" chaaneg", "gender");
                                b = true;
                                MainActivity.save = true;
                            }
                        }

                    } else {
                        Log.d(TAG+" chaaneg", "race");
                        b = true;
                        MainActivity.save = true;
                    }
                } else {
                    Log.d(TAG+" chaaneg", "age");
                    b = true;
                    MainActivity.save = true;
                }
            } else {
                Log.d(TAG+" chaaneg", "location");
                b = true;
                MainActivity.save = true;
            }
        } else {
            b = true;
            Log.d(TAG+" chaaneg", "name");
            MainActivity.save = true;
        }
        return b;
    }

    public void confirmSave(final Fragment frag) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mainActivity);
       // alertDialog.setTitle("Confirm Save...");
        alertDialog.setMessage("You have unsaved changes,if you navigate away from this screen all unsaved changes will lost.");

        alertDialog.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //  Toast.makeText(getApplicationContext(), "You clicked on YES", Toast.LENGTH_SHORT).show();

                dialog.cancel();
            }
        });
        // Setting Negative "NO" Button
        alertDialog.setNegativeButton("Proceed Anyway", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                MainActivity.save = false;
                Profile.fragmentManager = mainActivity.getSupportFragmentManager();
                Profile.fragmentManager.beginTransaction().replace(R.id.profile_fram, frag).addToBackStack("friends").commit();
            }
        });
        // Showing Alert Message
        alertDialog.show();
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


    /*public void takeImageFromCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "CameraDemo");
        File fil = new File("");
        outPutfileUri = Uri.fromFile(fil);
        String uriString = outPutfileUri.toString();
        File myFile = new File(uriString);
        String path = myFile.getAbsolutePath();
        // this method is used to get pic name
       // getPicName(path, outPutfileUri, myFile);
        //Uri photoURI = FileProvider.getUriForFile(mainActivitySignUP,
               // mainActivitySignUP.getPackageName() + ".my.package.name.provider", createImageFile());
        File photo = new File(Environment.getExternalStorageDirectory(),  "Pic.jpg");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
        startActivityForResult(intent, TAKE_PIC);
    }*/

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
        if (requestCode == PLACE_PICKER_REQUEST) {
            //if (resultCode == RESULT_OK) {
            try {
                Place place = PlacePicker.getPlace(data, mainActivity);
                StringBuilder stBuilder = new StringBuilder();
                String placename = String.format("%s", place.getName());
                String latitude = String.valueOf(place.getLatLng().latitude);
                String longitude = String.valueOf(place.getLatLng().longitude);
                String address = String.format("%s", place.getAddress());
                stBuilder.append("Name: ");
                stBuilder.append(placename);
                stBuilder.append("\n");
                stBuilder.append("Latitude: ");
                stBuilder.append(latitude);
                stBuilder.append("\n");
                stBuilder.append("Logitude: ");
                stBuilder.append(longitude);
                stBuilder.append("\n");
                stBuilder.append("Address: ");
                stBuilder.append(address);
                Log.d(TAG+" Address ", address);
                //   tvPlaceDetails.setText(stBuilder.toString());
                // }
                tv_location_edprofile.setText(address);
                Stats.location = address;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
                    civ_edprofile.setImageURI(resultUri);
                    iv_back_etprofile.setImageURI(resultUri);
                    //  this.path = file.getPath();
                    // this method is used to get pic name
                    //  getPicName(path, resultUri, file);
                    Log.d(TAG+" profilePicPath", "file " + file.getPath());
                    MainActivity.save = true;

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

    public void updateProfile() {
        // loading or check internet connection or something...
        // ... then
        /*Ion.with(mainActivitySignUP).load(NetworkConstants.updateProfile).addHeader("accessToken", preferenceManager.getUserAuthkey())
                .setMultipartParameter("name", et_name_edprofile.getText().toString())
                .setMultipartParameter("location", tv_location_edprofile.getText().toString())
                .setMultipartParameter("gender", "1")
                .setMultipartParameter("race", et_race_edprofile.getText().toString())
                .setMultipartParameter("age", et_age_edprofile.getText().toString())
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        Log.e("ERR", "" + result);
                    }
                });*/
        RequestQueue requestQueue = Volley.newRequestQueue(mainActivity);
        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest
                (Request.Method.POST, NetworkConstants.updateProfile,
                        new Response.Listener<NetworkResponse>() {
                            @Override
                            public void onResponse(NetworkResponse response) {
                                String resultResponse = new String(response.data);
                                Log.d(TAG+" update profile response", "" + resultResponse);
                                JSONObject jsonObject = Model.getObject(resultResponse);
                                try {
                                    String message = jsonObject.getString("message");
                                    if (message.equals("Successfull")) {
                                        JSONArray result = jsonObject.getJSONArray("result");
                                        JSONObject json = Model.getObject(result, 0);
                                        String image = json.getString("image");
                                        preferenceManager.setKey_userImg(image);
                                        Toast.makeText(mainActivity, "Profile Updated Successfully ", Toast.LENGTH_SHORT).show();
                                        Profile.fragmentManager.popBackStack();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                progress.dismiss();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                String result = new String(networkResponse.data);
                String errorMessage = "Unknown error";
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
                            //Toast.makeText(mainActivitySignUP, )
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

                params.put("name", et_name_edprofile.getText().toString());
                params.put("location", tv_location_edprofile.getText().toString());
                params.put("gender", gende);
                params.put("race", et_race_edprofile.getText().toString());
                params.put("age", et_age_edprofile.getText().toString());

                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                // file name could found file base or direct access from real path
                // for now just get bitmap data from ImageView
                ByteArrayOutputStream baosInvoice = new ByteArrayOutputStream();
                if (bitImg != null) {
                    bitImg.compress(Bitmap.CompressFormat.JPEG, 80, baosInvoice);
                    userImage = baosInvoice.toByteArray();
                } else {
                    userImage = baosInvoice.toByteArray();
                }

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
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
