package app.com.perfec10.fragment.login.signUp.login;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import app.com.perfec10.R;
import app.com.perfec10.activity.MainActivity;

/**
 * Created by fluper on 23/1/18.
 */

@SuppressLint("ValidFragment")
public class AddFriendAllowDialogesLogin extends DialogFragment {
    private TermCoditionsActivityLogin mainActivity;
    private String TAG = "AddFriendAllowDialoge";
    private TextView tv_later_popup, tv_yes_popup, tv_subtext_dialoge;
    private String from;

    public AddFriendAllowDialogesLogin(TermCoditionsActivityLogin mainActivity, String from){
        this.mainActivity = mainActivity;
        this.from = from;
    }

    public AddFriendAllowDialogesLogin(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.MY_DIALOG);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Dialog dialog = new Dialog(getActivity());
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.MY_DIALOG);
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

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void clickListner(){

        tv_later_popup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                startActivity(new Intent(mainActivity,MainActivity.class)
                        .putExtra("key_termCondition",2)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                        mainActivity.finish();


            }
        });

        tv_yes_popup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();

                startActivity(new Intent(mainActivity,MainActivity.class)
                        .putExtra("key_termCondition",1)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                        mainActivity.finish();
                //  new MainActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, new AddAfterSignup(), "AddAfterSignup").commit();
            }
        });


    }
}
