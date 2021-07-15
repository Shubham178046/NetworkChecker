package com.example.networkdemo.network;

import android.net.NetworkInfo;
import android.os.Parcel;
import android.os.Parcelable;

public class NetworkStateChanged implements Parcelable {
    public static final Creator<NetworkStateChanged> CREATOR = new Creator<NetworkStateChanged>() {
        public NetworkStateChanged createFromParcel(Parcel parcel) {
            return new NetworkStateChanged(parcel);
        }

        public NetworkStateChanged[] newArray(int i) {
            return new NetworkStateChanged[i];
        }
    };
    private NetworkInfo networkInfo;

    public NetworkStateChanged(NetworkInfo networkInfo2) {
        this.networkInfo = networkInfo2;
    }

    public int describeContents() {
        return 0;
    }

    public NetworkInfo getNetworkInfo() {
        return this.networkInfo;
    }

    public void setNetworkInfo(NetworkInfo networkInfo2) {
        this.networkInfo = networkInfo2;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeValue(this.networkInfo);
    }

    public NetworkStateChanged(Parcel parcel) {
        this.networkInfo = (NetworkInfo) parcel.readValue(NetworkInfo.class.getClassLoader());
    }
}
