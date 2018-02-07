package app.com.perfec10.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import app.com.perfec10.R;

/**
 * Created by fluper on 20/7/17.
 */

public class Progress extends ProgressDialog {
    public Progress(Context context) {
        super(context);
    }

    public Progress(Context context, int theme) {
        super(context, theme);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);
    }

    @Override
    public boolean isShowing() {
        return super.isShowing();
    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    public void hide() {
        super.hide();
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }
}
