package app.com.perfec10.fragment.login;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import app.com.perfec10.R;
import app.com.perfec10.activity.MainActivity;
import app.com.perfec10.fragment.home.Home;
import app.com.perfec10.util.Progress;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by fluper on 26/10/17.
 */

@SuppressLint("ValidFragment")
public class PreLogin extends Fragment {
    private MainActivity mainActivity;
    private Typeface regular, bold, medium;
    @BindView(R.id.ll_login_prelogin) LinearLayout ll_login_prelogin;
    private Progress progress;
    private String TAG = "PreLogin";

    public PreLogin (MainActivity mainActivity){
        this.mainActivity = mainActivity;
        medium = Typeface.createFromAsset(mainActivity.getAssets(), "fonts/roboto-medium.ttf");
        bold = Typeface.createFromAsset(mainActivity.getAssets(), "fonts/Roboto-Bold.ttf");
        regular = Typeface.createFromAsset(mainActivity.getAssets(), "fonts/Roboto-Regular.ttf");
        progress = new Progress(mainActivity);
        progress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progress.setCancelable(false);
        progress.setCanceledOnTouchOutside(false);
    }

    public PreLogin(){

    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.prelogin_fragment, container, false);
        ButterKnife.bind(this, view);
        iniView();
        clickListner();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void iniView(){
      //  progress.show();
      //  progress.dismiss();
    }

    public void clickListner(){
        ll_login_prelogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.changeFragment(new LoginHome(mainActivity), "login_home");
            }
        });
        /*ll_bottom_prelogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.clearBackStack();
                mainActivitySignUP.getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, new Home(mainActivitySignUP), "home").commit();

            }
        });*/
    }
}
