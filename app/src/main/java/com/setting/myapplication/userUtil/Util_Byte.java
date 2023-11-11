package com.setting.myapplication.userUtil;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Util_Byte {

    // 바이트 배열을 Int32로 변환
    public static int byteToInt32(byte[] buf, int index, boolean isBigEndian) {
        ByteBuffer bb = ByteBuffer.wrap(buf);

        if (isBigEndian)
            bb.order(ByteOrder.BIG_ENDIAN);
        else
            bb.order(ByteOrder.LITTLE_ENDIAN);

        return bb.getInt(index);
    }

    // 안드로이드는 unsigned int 형이 없음.
    public static long byteToUInt32(byte[] buf, int index, boolean isBigEndian) {
        ByteBuffer bb = ByteBuffer.wrap(buf);

        if (isBigEndian)
            bb.order(ByteOrder.BIG_ENDIAN);
        else
            bb.order(ByteOrder.LITTLE_ENDIAN);

        return bb.getInt(index) & 0xFFFFFFFFL;
    }

    public long byteToLong(byte[] buf, int index, boolean isBigEndian) {
        ByteBuffer bb = ByteBuffer.wrap(buf);

        if (isBigEndian)
            bb.order(ByteOrder.BIG_ENDIAN);
        else
            bb.order(ByteOrder.LITTLE_ENDIAN);

        return bb.getLong(index);
    }

    // 바이트 배열을 문자열로 변환
    public static String byteToString(byte[] array, int index, int count, String charsetName) {
        if (array == null || array.length < (index + count))
            return null;

        String rtn = null;

        try {
            if (index == 0 && count == array.length) {
                rtn = new String(array, charsetName);
            } else {
                byte[] buf = new byte[count];
                System.arraycopy(array, index, buf, 0, buf.length);
                rtn = new String(buf, charsetName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return rtn;
    }

    public static byte[] stirngToByte(String str, String charsetName) {
        byte[] rtn = null;
        try {
            rtn = str.getBytes(charsetName);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return rtn;
    }

    public static String hexToString(String strTag, byte[] buf) {
        return hexToString(strTag, buf, buf == null ? 0 : buf.length);
    }

    public static String hexToString(String strTag, byte[] buf, int nLen) {
        StringBuilder bb = new StringBuilder();
        try {
            bb.append(strTag);
            bb.append(" [").append(nLen).append("] ");

            for (int i = 0; i < nLen; ++i) {
                bb.append(String.format("%02x ", buf[i]));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bb.toString();
    }

    public static void LogToHexString(String strTag, byte[] buf, int nLen) {
        Log.d(strTag, hexToString(strTag, buf, nLen));
    }

}
