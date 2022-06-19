package com.google.intelligence.sense.leveldb;

public class LevelDbTable {

    private static native void nativeClose(long arg0);

    private static native byte[] nativeGet(long arg0, byte[] arg1);

    private static native long nativeLoad(String arg0);

}
