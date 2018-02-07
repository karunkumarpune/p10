package app.com.perfec10.fragment.measure.adapater;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import app.com.perfec10.R;
import app.com.perfec10.activity.MainActivity;

/**
 * Created by admin on 11/13/2017.
 */

public class InputFieldsAdapter extends RecyclerView.Adapter<InputFieldsAdapter.ViewHolder>{
    private MainActivity mainActivity;
    private String[] values;
    private String calledFrom;
    private String TAG = "InputFieldsAdapter";

    public InputFieldsAdapter(MainActivity mainActivity, String[] values, String calledFrom){
        this.mainActivity = mainActivity;
        this.values = values;
        this.calledFrom = calledFrom;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mainActivity).inflate(R.layout.input_field_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tv_inputfld_row.setText(values[position]);
        Log.d(TAG+" inside adapter ", values[position]+" ");
    }

    @Override
    public int getItemCount() {
        return values.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView tv_inputfld_row;
        public ViewHolder(View itemView) {
            super(itemView);
            tv_inputfld_row = (TextView) itemView.findViewById(R.id.tv_inputfld_row);
        }
    }
}
