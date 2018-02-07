package app.com.perfec10.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by fluper on 5/1/18.
 */

public class FbFrndsGS implements Parcelable {
    private String name;
    private String id;
    private String status;
    private String image;
    private ArrayList<FbFrndsGS> fbList;

    public FbFrndsGS(ArrayList<FbFrndsGS> fbList){
        this.fbList = fbList;
    }

    public FbFrndsGS(){

    }

    protected FbFrndsGS(Parcel in) {
        name = in.readString();
        id = in.readString();
        status = in.readString();
        image = in.readString();
        fbList = in.createTypedArrayList(FbFrndsGS.CREATOR);
    }

    public static final Creator<FbFrndsGS> CREATOR = new Creator<FbFrndsGS>() {
        @Override
        public FbFrndsGS createFromParcel(Parcel in) {
            return new FbFrndsGS(in);
        }

        @Override
        public FbFrndsGS[] newArray(int size) {
            return new FbFrndsGS[size];
        }
    };

    public void setFbList(ArrayList<FbFrndsGS> fbList){ this.fbList = fbList;}

    public ArrayList<FbFrndsGS> getFbList() { return fbList; }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(id);
        parcel.writeString(status);
        parcel.writeString(image);
        parcel.writeTypedList(fbList);

    }
}
