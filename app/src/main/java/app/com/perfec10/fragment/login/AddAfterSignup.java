package app.com.perfec10.fragment.login;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.regex.Pattern;

import app.com.perfec10.R;
import app.com.perfec10.activity.MainActivity;
import app.com.perfec10.fragment.home.Home;
import app.com.perfec10.helper.Validation;
import app.com.perfec10.model.AddFrndGS;
import app.com.perfec10.model.FbFrndsGS;
import app.com.perfec10.network.Network;
import app.com.perfec10.network.NetworkCallBack;
import app.com.perfec10.network.NetworkConstants;
import app.com.perfec10.util.PreferenceManager;
import app.com.perfec10.util.Progress;

import static app.com.perfec10.helper.HelperClass.returnEmptyString;

/**
 * Created by fluper on 4/1/18.
 */

@SuppressLint("ValidFragment")
public class AddAfterSignup extends Fragment implements NetworkCallBack{
    private MainActivity mainActivity;
    private String TAG = "AddAfterSignup";
    private TextView tv_sw1_addfrnd;
    private ImageView iv_next_addfrnd, iv_mail_addfrnd, iv_fb_addfrnd, iv_cancle_addfrnd;
    private RecyclerView rv_serachlist_addfrnd;
    private LinearLayoutManager linearLayoutManager;
    private LinearLayout ll_mail_addfrnd, ll_fb_addfrnd;
    private EditText et_seach_addfrnd;
    private Button btn_search_addfrnd;
    private PreferenceManager preferenceManager;
    private ArrayList<FbFrndsGS> fbFrndsList = new ArrayList<>();
    private ArrayList<AddFrndGS> searchedList = new ArrayList<>();
    private Progress progress;
    private boolean fbClick = false;

    public AddAfterSignup(MainActivity mainActivity){
        this.mainActivity = mainActivity;
        preferenceManager = new PreferenceManager(mainActivity);
        progress = new Progress(mainActivity);
        progress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progress.setCancelable(false);
        progress.setCanceledOnTouchOutside(false);
    }

    public AddAfterSignup(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.add_friend, container, false);
        initView(v);
        clickListner();
        return v;
    }

    public void initView(View v){
        tv_sw1_addfrnd = (TextView) v.findViewById(R.id.tv_sw1_addfrnd);
        tv_sw1_addfrnd.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

        iv_next_addfrnd = (ImageView) v.findViewById(R.id.iv_next_addfrnd);
        iv_mail_addfrnd = (ImageView) v.findViewById(R.id.iv_mail_addfrnd);
        iv_fb_addfrnd = (ImageView) v.findViewById(R.id.iv_fb_addfrnd);
        iv_cancle_addfrnd = (ImageView) v.findViewById(R.id.iv_cancle_addfrnd);

        rv_serachlist_addfrnd = (RecyclerView) v.findViewById(R.id.rv_serachlist_addfrnd);
        linearLayoutManager = new LinearLayoutManager(mainActivity);
        rv_serachlist_addfrnd.setLayoutManager(linearLayoutManager);

        ll_mail_addfrnd = (LinearLayout) v.findViewById(R.id.ll_mail_addfrnd);
        ll_fb_addfrnd = (LinearLayout) v.findViewById(R.id.ll_fb_addfrnd);

        et_seach_addfrnd = (EditText) v.findViewById(R.id.et_seach_addfrnd);
        btn_search_addfrnd = (Button) v.findViewById(R.id.btn_search_addfrnd);

        fbFrndsList = preferenceManager.getFbFrnds(mainActivity);

        edit_Text_Focus_Listner();
    }

    public void edit_Text_Focus_Listner() {
        et_seach_addfrnd.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (et_seach_addfrnd.getText().toString().length() > 0){
                    ArrayList<FbFrndsGS> searchedlist = new ArrayList<>();

                    try {


                        if (fbFrndsList.size() > 0) {

                            for (int i = 0; i < fbFrndsList.size(); i++) {
                                String s = et_seach_addfrnd.getText().toString();
                                boolean a = Pattern.compile(Pattern.quote(s), Pattern.CASE_INSENSITIVE).matcher(fbFrndsList.get(i).getName()).find();

                                if (a) {
                                    FbFrndsGS fbFrndsGS = new FbFrndsGS();
                                    fbFrndsGS.setId(fbFrndsList.get(i).getId());
                                    fbFrndsGS.setName(fbFrndsList.get(i).getName());
                                    fbFrndsGS.setStatus(fbFrndsList.get(i).getStatus());
                                    fbFrndsGS.setImage(fbFrndsList.get(i).getImage());
                                    searchedlist.add(fbFrndsGS);
                                }

                            }
                        }

                    }
                    catch (Exception e){}
                    if (fbClick){
                        AddFrndAdapter addFrndAdapter = new AddFrndAdapter(mainActivity, searchedlist);
                        rv_serachlist_addfrnd.setAdapter(addFrndAdapter);
                    }

                }else {
                    if (fbClick){
                        AddFrndAdapter addFrndAdapter = new AddFrndAdapter(mainActivity, fbFrndsList);
                        rv_serachlist_addfrnd.setAdapter(addFrndAdapter);
                    }

                }

                //  search_result(jsonObject.toString());
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
        });
    }

    public void clickListner(){
        iv_next_addfrnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<Integer> eid = new ArrayList<>();
                ArrayList<Long> fid = new ArrayList<>();
                for (int i = 0; i < searchedList.size(); i++){
                    if (searchedList.get(i).getSelected().equals("1")){
                        eid.add(Integer.parseInt(searchedList.get(i).getUserId()));
                    }
                }
                if (fbFrndsList != null){
                    for (int i = 0; i < fbFrndsList.size(); i++){
                        if (fbFrndsList.get(i).getStatus().equals("1")){
                            fid.add(Long.parseLong(fbFrndsList.get(i).getId()));
                        }
                    }
                }

                if (fid.size() > 0 || eid.size() > 0){
                    addFriend(fid, eid);

                }else {
                    MainActivity.clearBackStack();
                    mainActivity.getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, new Home(mainActivity), "home").commit();
                }

            }
        });
        iv_cancle_addfrnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                et_seach_addfrnd.setText("");
            }
        });
        ll_fb_addfrnd.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                fbClick = true;
                rv_serachlist_addfrnd.setAdapter(null);
                iv_fb_addfrnd.setImageResource(R.mipmap.facebook_select);
                ll_fb_addfrnd.setBackgroundColor(Color.parseColor("#cedc00"));
                ll_mail_addfrnd.setBackgroundColor(Color.parseColor("#ffffff"));
              //  iv_mail_addfrnd.setImageResource(R.mipmap.email_unselect);
                btn_search_addfrnd.setVisibility(View.GONE);
                if (fbFrndsList != null){
                    if (fbFrndsList.size() > 0){
                        rv_serachlist_addfrnd.setVisibility(View.VISIBLE);
                        AddFrndAdapter addFrndAdapter = new AddFrndAdapter(mainActivity, fbFrndsList);
                        rv_serachlist_addfrnd.setAdapter(addFrndAdapter);
                        addFrndAdapter.notifyDataSetChanged();
                    }else {
                        rv_serachlist_addfrnd.setVisibility(View.GONE);
                    }
                }else {
                    rv_serachlist_addfrnd.setVisibility(View.GONE);
                }


                et_seach_addfrnd.setText("");
            }
        });
        ll_mail_addfrnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fbClick = false;
                //rv_serachlist_addfrnd.setAdapter(null);
             //   iv_fb_addfrnd.setImageResource(R.mipmap.facebook_unselect);
                ll_mail_addfrnd.setBackgroundColor(Color.parseColor("#cedc00"));
                ll_fb_addfrnd.setBackgroundColor(Color.parseColor("#ffffff"));
                iv_mail_addfrnd.setImageResource(R.mipmap.email_select);
                btn_search_addfrnd.setVisibility(View.VISIBLE);
                if (searchedList.size() > 0){
                    rv_serachlist_addfrnd.setVisibility(View.VISIBLE);
                    AddFrndAdapter addFrndAdapter = new AddFrndAdapter(mainActivity, searchedList,
                            "email");
                    rv_serachlist_addfrnd.setAdapter(addFrndAdapter);
                    addFrndAdapter.notifyDataSetChanged();
                }else {
                    rv_serachlist_addfrnd.setVisibility(View.GONE);
                }

                et_seach_addfrnd.setText("");
            }
        });
        btn_search_addfrnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Validation.validEmail(et_seach_addfrnd.getText().toString())) {
                    progress.show();
                    searchUser();
                    Log.d(TAG + " edit text ", et_seach_addfrnd.getText().toString() + " ");
                } else {
                    if (fbClick){
                        Toast.makeText(mainActivity, "Please Enter name", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(mainActivity, "Please Enter valid Email", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    public void addFriend(ArrayList<Long> fId, ArrayList<Integer> eid) {
        if (Network.isConnected(mainActivity)) {
            /*{
        "normal":[2,3],
        "  social":["dfwewed"]
            }*/
            JsonObject jsonObject = new JsonObject();
            //     jsonObject.addProperty("user_id",friendId); 1723447934397164
            Gson gson = new Gson();
            if (eid.size() > 0){
                jsonObject.add("normal", gson.toJsonTree(eid));
            }
            if (fId.size() > 0){
                jsonObject.add("social", gson.toJsonTree(fId));
            }

            Log.d(TAG + " params add friend", jsonObject + " ");
            progress.show();
            Network.hitPostApiWithAuth(mainActivity, jsonObject, this, NetworkConstants.addFriend, 2);
        } else {
            Toast.makeText(mainActivity, "No Internt Connection ", Toast.LENGTH_SHORT).show();
        }
    }

    public void searchUser() {
        if (Network.isConnected(mainActivity)) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("email", et_seach_addfrnd.getText().toString());
            Log.d(TAG + " params search frnd", jsonObject + " ");
            Network.hitPostApiWithAuth(mainActivity, jsonObject, this, NetworkConstants.searchUser, 1);
        } else {
            Toast.makeText(mainActivity, "No Internt Connection ", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSuccess(Object data, int requestCode, int statusCode) {
        Log.d(TAG + " response seach user ", data + " ");
        progress.dismiss();
        String message = "";
        JsonObject jsonObject = (JsonObject) data;
        switch (statusCode) {
            case 200:
                try {
                    if (requestCode == 1) {
                        Log.d(TAG + " reponse profile", jsonObject + " ");
                        message = returnEmptyString(jsonObject.get("message"));
                        if (message.equals("Successfull")) {
                            JsonArray result = jsonObject.getAsJsonArray("result");
                            if (result.size() > 0) {

                                JsonObject json = result.get(0).getAsJsonObject();
                                String user_id = json.get("user_id") + "";

                                String name = json.get("name") + "";
                                name = name.replace("\"", "");
                                String email = json.get("email") + "";
                                email = email.replace("\"", "");
                                String url = json.get("image") + "";
                                url = url.replace("\"", "");
                                AddFrndGS addFrndGS = new AddFrndGS();
                                addFrndGS.setName(name);
                                addFrndGS.setUserId(user_id);
                                addFrndGS.setEmail(email);
                                addFrndGS.setImage(url);
                                addFrndGS.setSelected("0");
                                boolean b = false;// for checking already added id
                                if (searchedList.size() > 0){
                                    for (int i = 0; i < searchedList.size(); i++){
                                        String id = searchedList.get(i).getUserId();
                                        if (id.equals(user_id)){
                                            b = true;
                                            break;
                                        }else {
                                            b = false;
                                        }
                                    }
                                }else {
                                    b = false;
                                }
                                if (!b){
                                    searchedList.add(addFrndGS);
                                }
                                View view = mainActivity.getCurrentFocus();
                                if (view != null) {
                                    InputMethodManager imm = (InputMethodManager)mainActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                                }
                                rv_serachlist_addfrnd.setVisibility(View.VISIBLE);
                                AddFrndAdapter addFrndAdapter = new AddFrndAdapter(mainActivity, searchedList, "email");
                                rv_serachlist_addfrnd.setAdapter(addFrndAdapter);
                            }

                        }
                    } else if (requestCode == 2) {
                        Log.d(TAG + " response add friend ", jsonObject + " ");
                        message = returnEmptyString(jsonObject.get("message"));
                        if (message.contains("Successfull")) {
                            Toast.makeText(mainActivity, "Friend Added Successfully ", Toast.LENGTH_SHORT).show();
                            preferenceManager.setKey_frndcount("1");
                            MainActivity.clearBackStack();
                            mainActivity.getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, new Home(mainActivity), "home").commit();

                        } else {
                            Toast.makeText(mainActivity, message, Toast.LENGTH_SHORT).show();
                        }
                    }

                } catch (Exception e) {
                    Log.d(TAG + " Outcome", e.toString());
                }
                break;
            case 201:
                message = returnEmptyString(jsonObject.get("message"));
                Toast.makeText(mainActivity, message, Toast.LENGTH_SHORT).show();
                break;
            case 204:
                Toast.makeText(mainActivity, "No Data Found", Toast.LENGTH_SHORT).show();
                break;
            case 400:
                message = returnEmptyString(jsonObject.get("message"));
                Toast.makeText(mainActivity, message, Toast.LENGTH_SHORT).show();
                break;
            case 401:
                message = returnEmptyString(jsonObject.get("message"));
                Toast.makeText(mainActivity, message, Toast.LENGTH_SHORT).show();
                break;
            case 403:
                message = returnEmptyString(jsonObject.get("message"));
                Toast.makeText(mainActivity, message, Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(mainActivity, "Network Error", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onError(String msg) {

    }
}
