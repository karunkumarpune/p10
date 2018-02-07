package app.com.perfec10.fragment.login;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import app.com.perfec10.R;
import app.com.perfec10.activity.MainActivity;
import app.com.perfec10.fragment.home.Home;

/**
 * Created by fluper on 23/1/18.
 */

@SuppressLint("ValidFragment")
public class AddFriendAllowDialoge extends DialogFragment {
    private MainActivity mainActivity;
    private String TAG = "AddFriendAllowDialoge";
    private TextView tv_later_popup, tv_yes_popup, tv_subtext_dialoge;
    private String from;

    public AddFriendAllowDialoge(MainActivity mainActivity, String from){
        this.mainActivity = mainActivity;
        this.from = from;
    }

    public AddFriendAllowDialoge(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(android.support.v4.app.DialogFragment.STYLE_NORMAL, R.style.MY_DIALOG);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Dialog dialog = new Dialog(getActivity());
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        setStyle(android.support.v4.app.DialogFragment.STYLE_NORMAL, R.style.MY_DIALOG);
        dialog.setContentView(R.layout.addfriend_permisiondialoge);
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        initView(dialog);
        clickListner();

        return dialog;
    }

    public void initView(Dialog dialog){
        tv_later_popup = (TextView) dialog.findViewById(R.id.tv_later_popup);
        tv_yes_popup = (TextView) dialog.findViewById(R.id.tv_yes_popup);
        tv_subtext_dialoge = (TextView) dialog.findViewById(R.id.tv_subtext_dialoge);
        if (from.equals("email")){
            tv_subtext_dialoge.setText("Have friends who are using Perfec10? Connect with them via their Email Id ?");
        }
    }

    public void clickListner(){

        tv_later_popup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                mainActivity.getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, new Home(mainActivity), "home").commit();
            }
        });
        tv_yes_popup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                mainActivity.getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, new AddAfterSignup(mainActivity), "AddAfterSignup").commit();
            }
        });


    }
}
