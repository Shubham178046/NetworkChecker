package com.example.networkdemo.network;

import android.os.Parcel;
import android.os.Parcelable;

public class NetworkDataEvent implements Parcelable {
    public static final Creator<NetworkDataEvent> CREATOR = new Creator<NetworkDataEvent>() {
        public NetworkDataEvent createFromParcel(Parcel parcel) {
            return new NetworkDataEvent(parcel);
        }

        public NetworkDataEvent[] newArray(int i) {
            return new NetworkDataEvent[i];
        }
    };
    private long download;
    private boolean snoozeNotification;
    private long total;
    private long upload;

    public NetworkDataEvent(long j, long j2, long j3, boolean z) {
        this.download = 0;
        this.upload = 0;
        this.total = 0;
        this.snoozeNotification = false;
        this.download = j;
        this.upload = j2;
        this.total = j3;
        this.snoozeNotification = z;
    }

    public int describeContents() {
        return 0;
    }

    public long getDownloadSpeed() {
        return this.download;
    }

    public long getTotalSpeed() {
        return this.total;
    }

    public long getUploadSpeed() {
        return this.upload;
    }

    public boolean isSnoozeNotification() {
        return this.snoozeNotification;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(this.download);
        parcel.writeLong(this.upload);
        parcel.writeLong(this.total);
        parcel.writeByte(this.snoozeNotification ? (byte) 1 : 0);
    }

    public NetworkDataEvent(Parcel parcel) {
        this.download = 0;
        this.upload = 0;
        this.total = 0;
        boolean z = false;
        this.snoozeNotification = false;
        this.download = parcel.readLong();
        this.upload = parcel.readLong();
        this.total = parcel.readLong();
        this.snoozeNotification = parcel.readByte() != 0 ? true : z;
    }
}
