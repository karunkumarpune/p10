package app.com.perfec10.fragment.friendgroup.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

import app.com.perfec10.R;
import app.com.perfec10.activity.MainActivity;
import app.com.perfec10.fragment.friendgroup.GroupDetail;
import app.com.perfec10.fragment.profile.Profile;
import app.com.perfec10.helper.CustomVolleyRequestQueue;
import app.com.perfec10.model.FriendListGS;
import app.com.perfec10.network.Network;
import app.com.perfec10.network.NetworkCallBack;
import app.com.perfec10.network.NetworkConstants;
import app.com.perfec10.util.SwipeRevealLayout;
import app.com.perfec10.util.ViewBinderHelper;

import static app.com.perfec10.helper.HelperClass.returnEmptyString;

/**
 * Created by fluper on 4/12/17.
 */

public class ParticipantAdaptor extends RecyclerView.Adapter<ParticipantAdaptor.Holder> implements NetworkCallBack{
private MainActivity mainActivity;
private ArrayList<FriendListGS> participants;
    private ImageLoader mImageLoader;
    private String groupId;
    private final ViewBinderHelper binderHelper = new ViewBinderHelper();
    private String TAG = "ParticipantAdaptor";

public ParticipantAdaptor(MainActivity mainActivity, ArrayList<FriendListGS> participants, String groupId){
    this.mainActivity = mainActivity;
    this.participants = participants;
    this.groupId = groupId;
}

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(mainActivity).inflate(R.layout.participant_row, parent, false);
    return new Holder(view);
    }

    @Override
    public void onBindViewHolder(final Holder holder, final int position) {
        binderHelper.bind(holder.swipe_layout, participants.get(position).getName());

        // Bind your data here
        holder.bind(position);
        String name = participants.get(position).getName();
        name = name.replace("\"", "");
        holder.tv_partiname_partrow.setText(name);
        String url = participants.get(position).getImage();
        url = url.replace("\"","");
        Log.d(TAG+" image profile ", NetworkConstants.imageBaseUrl+url);
        mImageLoader = CustomVolleyRequestQueue.getInstance(mainActivity).getImageLoader();
        mImageLoader.get(NetworkConstants.imageBaseUrl+url, ImageLoader.getImageListener(holder.riv_img_partrow,
                R.mipmap.user, R.mipmap.user));
        holder.riv_img_partrow.setImageUrl(NetworkConstants.imageBaseUrl+url, mImageLoader);
        /*holder.tv_delete_partrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.bind();
                delete(position);

            }
        });*/

    }

    public void delete(int position){
        JsonObject jsonObject = null;
        try {
            jsonObject = new JsonObject();
            jsonObject.addProperty("user_id", participants.get(position).getUserId());
            jsonObject.addProperty("group_id", groupId);
            Log.d(TAG+" params delete parti ", jsonObject+" ");
            GroupDetail.tv_participant_grp.setText("Participants : "+(participants.size()-1));
            Network.hitPostApiWithAuth(mainActivity,jsonObject,this, NetworkConstants.deleteGroupMember,1);
            if (participants.size() ==1){
                Profile.fragmentManager.popBackStack();
               // Network.hitPostApiWithAuth(mainActivity,jsonObject,this, NetworkConstants.deleteGroup,2);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return participants.size();
    }

    @Override
    public void onSuccess(Object data, int requestCode, int statusCode) {
        Log.d(TAG+" response delete ", data+" ");
        String message = "";
        JsonObject jsonObject = (JsonObject) data;
        switch (statusCode) {
            case 200:
                try {
                    if (requestCode==1) {
                        Log.d(TAG+" response delete", jsonObject + " ");
                        message = returnEmptyString(jsonObject.get("message"));
                        if (message.equals("User deleted successfully")) {
                          Toast.makeText(mainActivity,message, Toast.LENGTH_SHORT).show();

                        }
                    }

                } catch (Exception e) {
                    Log.d(TAG+" Outcome", e.toString());
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
            default:
                Toast.makeText(mainActivity, "Network Error", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onError(String msg) {

    }

    public class Holder extends RecyclerView.ViewHolder{
    private TextView tv_partiname_partrow, tv_delete_partrow;
    private app.com.perfec10.helper.RoundedImageView riv_img_partrow;
    private SwipeRevealLayout swipe_layout;
    private FrameLayout delete_layout;
        public Holder(View itemView) {
            super(itemView);
            riv_img_partrow = (app.com.perfec10.helper.RoundedImageView) itemView.findViewById(R.id.riv_img_partrow);
            tv_partiname_partrow = (TextView) itemView.findViewById(R.id.tv_partiname_partrow);
            tv_delete_partrow = (TextView) itemView.findViewById(R.id.tv_delete_partrow);
            swipe_layout = (SwipeRevealLayout) itemView.findViewById(R.id.swipe_layout);
            delete_layout = (FrameLayout) itemView.findViewById(R.id.delete_layout);
        }

        public void bind(final int position) {
            delete_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    delete(position);
                    participants.remove(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());

                }
            });

        }

    }
}
