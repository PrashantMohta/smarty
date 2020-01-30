package com.mtk.btconnection;


public class LoadJniFunction {
    public static final int CMD_1 = 1;
    public static final int CMD_2 = 2;
    public static final int CMD_3 = 3;
    public static final int CMD_4 = 4;
    public static final int CMD_5 = 5;
    public static final int CMD_6 = 6;
    public static final int CMD_7 = 7;
    public static final int CMD_8 = 8;
    private static final String LIB_NAME = "Command";

    public native int getCmdTypeFromJni(byte[] bArr, int i);

    public native byte[] getDataCmdFromJni(int i, String str);

    public native int getDataLenthFromJni(byte[] bArr, int i);

    static {
        System.loadLibrary(LIB_NAME);
    }

    public byte[] getDataCmd(int len, String arg) {
        return getDataCmdFromJni(len, arg);
    }

    public int getCmdType(byte[] command, int commandlenth) {
        return getCmdTypeFromJni(command, commandlenth);
    }

    public int getDataLenth(byte[] command, int commandlenth) {
        return getDataLenthFromJni(command, commandlenth);
    }
}
