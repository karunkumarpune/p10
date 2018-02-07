package app.com.perfec10.fragment.profile;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import app.com.perfec10.R;
import app.com.perfec10.activity.MainActivity;
import app.com.perfec10.fragment.self_snaps.adapter.SelfSnapAdapter;
import app.com.perfec10.helper.GIFView;
import app.com.perfec10.network.Network;
import app.com.perfec10.network.NetworkCallBack;
import app.com.perfec10.network.NetworkConstants;
import app.com.perfec10.util.PreferenceManager;
import app.com.perfec10.util.Progress;

import static app.com.perfec10.helper.HelperClass.returnEmptyString;

/**
 * Created by fluper on 13/12/17.
 */

@SuppressLint("ValidFragment")
public class ReportProblemDialoge extends DialogFragment implements NetworkCallBack {
    private MainActivity mainActivity;
    private TextView tv_sw1_report, tv_cancel_report, tv_submit_report;
    private EditText et_msg_report;
    private PreferenceManager preferenceManager;
    private Progress progress;
    private Typeface regular, bold;
    private String TAG = "ReportProblemDialoge";
    private GIFView loader_report;
    private LinearLayout ll_opensoftkey_report;

    public ReportProblemDialoge(MainActivity mainActivity){
        this.mainActivity = mainActivity;
        preferenceManager = new PreferenceManager(mainActivity);
        progress = new Progress(mainActivity);
        progress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progress.setCancelable(false);
        progress.setCanceledOnTouchOutside(false);
        bold = Typeface.createFromAsset(mainActivity.getAssets(), "fonts/Roboto-Bold.ttf");
        regular = Typeface.createFromAsset(mainActivity.getAssets(), "fonts/Roboto-Regular.ttf");

    }

    public ReportProblemDialoge(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(android.support.v4.app.DialogFragment.STYLE_NORMAL, R.style.MY_DIALOG);
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Dialog dialog = new Dialog(getActivity());
        dialog.setCanceledOnTouchOutside(false);
        setStyle(android.support.v4.app.DialogFragment.STYLE_NORMAL, R.style.MY_DIALOG);
        dialog.setContentView(R.layout.report_problum_dialoge);
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        initView(dialog);
        clickListner();

        return dialog;
    }

    public void initView(Dialog v){
        tv_sw1_report = (TextView) v.findViewById(R.id.tv_sw1_report);
        tv_cancel_report = (TextView) v.findViewById(R.id.tv_cancel_report);
        tv_submit_report = (TextView) v.findViewById(R.id.tv_submit_report);

        et_msg_report = (EditText) v.findViewById(R.id.et_msg_report);

        loader_report = (GIFView)v.findViewById(R.id.loader_report);
        loader_report.setImageResource(R.drawable.loader);

        ll_opensoftkey_report = (LinearLayout) v.findViewById(R.id.ll_opensoftkey_report);

        tv_sw1_report.setTypeface(bold);
        tv_cancel_report.setTypeface(regular);
        tv_submit_report.setTypeface(regular);
        et_msg_report.setTypeface(regular);
        et_msg_report.requestFocus();

    }

    public void clickListner(){
        tv_cancel_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    InputMethodManager imm = (InputMethodManager) mainActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                dismiss();
            }
        });

        tv_submit_report.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {

                report();

                InputMethodManager imm = (InputMethodManager)mainActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });
        ll_opensoftkey_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager)
                       mainActivity. getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(et_msg_report,
                        InputMethodManager.SHOW_IMPLICIT);
            }
        });


    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }

    @SuppressLint("ResourceAsColor")
    public void report(){
        if (et_msg_report.getText().toString().length() > 0){
            loader_report.setVisibility(View.VISIBLE);
            tv_submit_report.setTextColor(R.color.view_color);
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("message", et_msg_report.getText().toString());
            Log.d(TAG+" params report ", jsonObject+" ");
           // progress.show();
            Network.hitPostApiWithAuth(mainActivity, jsonObject, this, NetworkConstants.report,1);
        }else {
            et_msg_report.setError("Enter Message");
        }
    }

    @Override
    public void onSuccess(Object data, int requestCode, int statusCode) {
        Log.d(TAG+" reponse report ", data+" ");// {"message":"Successfuly."}
     //   progress.dismiss();
        String message = "";
        JsonObject jsonObject = (JsonObject) data;
        switch (statusCode) {
            case 200:
                try {
                    if (requestCode==1){
                        message = returnEmptyString(jsonObject.get("message"));

                        if (message.equals("Successfuly.")) {
                            Toast.makeText(mainActivity, "Send Successfully", Toast.LENGTH_SHORT).show();
                            dismiss();

                        }

                    }

                } catch (Exception e) {
                    Log.e(TAG+" Outcome", e.toString());
                }
                break;
            case 201:
                message = returnEmptyString(jsonObject.get("message"));
                Toast.makeText(mainActivity, message, Toast.LENGTH_SHORT).show();
                break;
            case 400:
                message = returnEmptyString(jsonObject.get("message"));
                Toast.makeText(mainActivity, message, Toast.LENGTH_SHORT).show();
                break;
            case 401:
                message = returnEmptyString(jsonObject.get("message"));
                //   showSessionDialog(message);
                Toast.makeText(mainActivity, message, Toast.LENGTH_SHORT).show();
                break;
            case 403:
                message = returnEmptyString(jsonObject.get("message"));
                Toast.makeText(mainActivity, message, Toast.LENGTH_SHORT).show();
                break;
            case 500:
                Toast.makeText(mainActivity, "in 500", Toast.LENGTH_SHORT).show();
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
