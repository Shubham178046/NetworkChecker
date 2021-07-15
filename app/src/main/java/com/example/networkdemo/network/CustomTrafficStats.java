package com.example.networkdemo.network;

import android.net.TrafficStats;
import android.os.Build;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class CustomTrafficStats {
    public static final long GB_IN_BYTES = 1073741824;
    public static final long KB_IN_BYTES = 1024;
    public static final long MB_IN_BYTES = 1048576;
    public static final long PB_IN_BYTES = 1125899906842624L;
    public static final long TB_IN_BYTES = 1099511627776L;
    private static final int TYPE_RX_BYTES = 0;
    private static final int TYPE_RX_PACKETS = 1;
    private static final int TYPE_TCP_RX_PACKETS = 4;
    private static final int TYPE_TCP_TX_PACKETS = 5;
    private static final int TYPE_TX_BYTES = 2;
    private static final int TYPE_TX_PACKETS = 3;
    public static final int UID_REMOVED = -4;
    public static final int UID_TETHERING = -5;
    public static final int UNSUPPORTED = -1;

    static {
        init();
    }

    public static long getIfaceRxBytes() {
        long j = 0;
        for (String nativeGetIfaceStat : getMobileIfacesRelect()) {
            j += nativeGetIfaceStat(nativeGetIfaceStat, 0);
        }
        return j;
    }

    public static long getIfaceTxBytes() {
        long j = 0;
        for (String nativeGetIfaceStat : getMobileIfacesRelect()) {
            j += nativeGetIfaceStat(nativeGetIfaceStat, 2);
        }
        return j;
    }

    public static String[] getMobileIfacesRelect() {
        try {
            Method declaredMethod = TrafficStats.class.getDeclaredMethod("getStatsService", new Class[0]);
            declaredMethod.setAccessible(true);
            return (String[]) Class.forName("android.net.INetworkStatsService").getMethod("getMobileIfaces", new Class[0]).invoke(declaredMethod.invoke((Object) null, new Object[0]), new Object[0]);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return new String[0];
        } catch (IllegalAccessException e2) {
            e2.printStackTrace();
            return new String[0];
        } catch (InvocationTargetException e3) {
            e3.printStackTrace();
            return new String[0];
        } catch (ClassNotFoundException e4) {
            e4.printStackTrace();
            return new String[0];
        }
    }

    public static long getMobileRxBytes() {
        return TrafficStats.getMobileRxBytes();
    }

    public static long getMobileTxBytes() {
        return TrafficStats.getMobileTxBytes();
    }

    public static long getTotalRxBytes() {
        return nativeGetTotalStat(0);
    }

    public static long getTotalTxBytes() {
        return nativeGetTotalStat(2);
    }

    public static long getUidRxBytes(int i) {
        if (Build.VERSION.SDK_INT >= 21) {
            return nativeGetUidRxLollipop(i);
        }
        return nativeGetUidStat(i, 0);
    }

    public static long getUidTxBytes(int i) {
        if (Build.VERSION.SDK_INT >= 21) {
            return nativeGetUidTxLollipop(i);
        }
        return nativeGetUidStat(i, 2);
    }

    public static native int init();

    private static native long nativeGetIfaceStat(String str, int i);

    private static native long nativeGetTotalStat(int i);

    private static native long nativeGetUidRxLollipop(int i);

    private static native long nativeGetUidStat(int i, int i2);

    private static native long nativeGetUidTxLollipop(int i);
}
