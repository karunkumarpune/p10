package app.com.perfec10.fragment.friendposts.adapter;

import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import app.com.perfec10.R;
import app.com.perfec10.activity.MainActivity;
import app.com.perfec10.model.CommentGS;
import app.com.perfec10.network.NetworkConstants;
import app.com.perfec10.util.CircleImageView;

/**
 * Created by fluper on 9/12/17.
 */

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.Holder>{
    private MainActivity mainActivity;
    private ArrayList<CommentGS> commentList;
    private Typeface bold, regular;
    private String TAG = "FriendPostAdapter";

    public CommentsAdapter(MainActivity mainActivity, ArrayList<CommentGS> commentList){
        this.mainActivity = mainActivity;
        this.commentList = commentList;
        bold = Typeface.createFromAsset(mainActivity.getAssets(), "fonts/Roboto-Bold.ttf");
        regular = Typeface.createFromAsset(mainActivity.getAssets(), "fonts/Roboto-Regular.ttf");
    }
    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mainActivity).inflate(R.layout.comment_row, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        String image = commentList.get(position).getImage();
        image = image.replace("\"", "");
        Picasso.with(mainActivity).load(NetworkConstants.imageBaseUrl+image).error(R.mipmap.user).into(holder.civ_commrow);
        holder.tv_name_commrow.setTypeface(bold);
        String name =  commentList.get(position).getName();
        name = name.replace("\"", "");
        holder.tv_name_commrow.setText(name);
        holder.tv_comment_commrow.setTypeface(regular);
        String comm = commentList.get(position).getComment();
        comm = comm.replace("\"", "");
        holder.tv_comment_commrow.setText(comm);
        if (position==commentList.size()-1){
            holder.view_commrow.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public class Holder extends RecyclerView.ViewHolder{
    private CircleImageView civ_commrow;
    private TextView tv_name_commrow, tv_comment_commrow;
        private View view_commrow;
        public Holder(View itemView) {
            super(itemView);
            civ_commrow = (CircleImageView) itemView.findViewById(R.id.civ_commrow);

            tv_name_commrow = (TextView) itemView.findViewById(R.id.tv_name_commrow);
            tv_comment_commrow = (TextView) itemView.findViewById(R.id.tv_comment_commrow);

            view_commrow = (View) itemView.findViewById(R.id.view_commrow);
        }
    }
}
