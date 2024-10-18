package com.example.openfilelibrary.video;


import android.os.Parcel;
import android.os.Parcelable;

public class DataBean implements Parcelable {
    public String url;
    public int localX;
    public int localY;
    public int width;
    public int height;

    public DataBean() {
    }

    protected DataBean(Parcel in) {
        url = in.readString();
        localX = in.readInt();
        localY = in.readInt();
        width = in.readInt();
        height = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(url);
        dest.writeInt(localX);
        dest.writeInt(localY);
        dest.writeInt(width);
        dest.writeInt(height);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DataBean> CREATOR = new Creator<DataBean>() {
        @Override
        public DataBean createFromParcel(Parcel in) {
            return new DataBean(in);
        }

        @Override
        public DataBean[] newArray(int size) {
            return new DataBean[size];
        }
    };
}
