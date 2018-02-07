package app.com.perfec10.fragment.friendposts.adapter;

import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;

import java.util.ArrayList;

import app.com.perfec10.R;
import app.com.perfec10.activity.MainActivity;
import app.com.perfec10.helper.CustomVolleyRequestQueue;
import app.com.perfec10.helper.RoundedImageView;
import app.com.perfec10.model.LikeGS;
import app.com.perfec10.network.NetworkConstants;

/**
 * Created by fluper on 12/12/17.
 */

public class LikeAdapter extends RecyclerView.Adapter<LikeAdapter.Holder>{
    private MainActivity mainActivity;
    private Typeface  regular;
    private ImageLoader imageLoader;
    private ArrayList<LikeGS> likeList;
    private String TAG = "LikeAdapter";


    public LikeAdapter(MainActivity mainActivity, ArrayList<LikeGS> likeList){
        this.mainActivity = mainActivity;
        this.likeList = likeList;
        regular = Typeface.createFromAsset(mainActivity.getAssets(), "fonts/Roboto-Regular.ttf");
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mainActivity).inflate(R.layout.like_row, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
    holder.tv_name_likerow.setText(likeList.get(position).getName());
    String url = likeList.get(position).getImage();
    url = url.replace("\"", "");
        imageLoader = CustomVolleyRequestQueue.getInstance(mainActivity).getImageLoader();
        imageLoader.get(NetworkConstants.imageBaseUrl+url, ImageLoader.getImageListener(holder.riv_img_likerow,
                R.mipmap.user, R.mipmap.user));
        holder.riv_img_likerow.setImageUrl(NetworkConstants.imageBaseUrl+url, imageLoader);
        if (position==likeList.size()-1){
            holder.view_likerow.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return likeList.size();
    }

    public class Holder extends RecyclerView.ViewHolder{
        private RoundedImageView riv_img_likerow;
        private TextView tv_name_likerow;
        private View view_likerow;

        public Holder(View itemView) {
            super(itemView);

            riv_img_likerow = (RoundedImageView) itemView.findViewById(R.id.riv_img_likerow);
            tv_name_likerow = (TextView) itemView.findViewById(R.id.tv_name_likerow);
            view_likerow = (View) itemView.findViewById(R.id.view_likerow);
        }
    }
}
