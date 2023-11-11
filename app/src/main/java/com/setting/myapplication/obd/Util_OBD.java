package com.setting.myapplication.obd;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class Util_OBD {


    /**
     * 시간을 패킷에서 사용하는 byte 형태로 변경
     *
     * @param date
     * @return
     */
    public static byte[] dateToPacketDate(Date date) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        byte[] byDate = new byte[6];
        Arrays.fill(byDate, (byte) 0);

        try {
            byDate[0] = (byte) (calendar.get(Calendar.YEAR) - 2000);
            byDate[1] = (byte) (calendar.get(Calendar.MONTH) + 1);
            byDate[2] = (byte) calendar.get(Calendar.DAY_OF_MONTH);
            byDate[3] = (byte) calendar.get(Calendar.HOUR_OF_DAY);
            byDate[4] = (byte) calendar.get(Calendar.MINUTE);
            byDate[5] = (byte) calendar.get(Calendar.SECOND);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return byDate;
    }

    /**
     * 현재 시간을 패킷에서 사용하는 포맷으로 리턴
     */
    public static byte[] nowDateToPacketDate() {
        return dateToPacketDate(new Date());
    }

    /**
     * 패킷에서 받은 시간을 Date로 변경
     */
    public static Date packetDateToDate(byte[] buf, int nIdx) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(buf[nIdx + 0] + 2000, buf[nIdx + 1] - 1, buf[nIdx + 2], buf[nIdx + 3], buf[nIdx + 4], buf[nIdx + 5]);
        return calendar.getTime();
    }

    /**
     */
    @SuppressWarnings("deprecation")
    public static int getUnsignedByte(byte b) {
        int nValue = 0;

        if (b < 0) {
            nValue = (int) b + 256;
        } else {
            nValue = (int) b;
        }

        return nValue;
    }

    /**
     */
    @SuppressWarnings("deprecation")
    public static byte[] getLoginByteDate() {
        Date date = new Date();
        //reserved +12
        byte[] byDate = new byte[19];
        Arrays.fill(byDate, (byte) 0);

        try {
            byDate[0] = (byte) (date.getYear() / 256);
            byDate[1] = (byte) (date.getYear() % 256);
            byDate[2] = (byte) (date.getMonth() + 1);
            byDate[3] = (byte) date.getDate();
            byDate[4] = (byte) date.getHours();
            byDate[5] = (byte) date.getMinutes();
            byDate[6] = (byte) date.getSeconds();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return byDate;
    }

    public static byte[] getLittleEndian(int Butter) {
        byte[] buf = new byte[4];

        buf[3] = (byte) ((Butter >>> 24) & 0xFF);
        buf[2] = (byte) ((Butter >>> 16) & 0xFF);
        buf[1] = (byte) ((Butter >>> 8) & 0xFF);
        buf[0] = (byte) ((Butter >>> 0) & 0xFF);

        return buf;
    }

    public static int getIntBigEndian(byte[] buffer, int nIdx, int nByCount) {
        int nRtnValue = 0;

        do {
            nByCount--;
            nRtnValue += ((buffer[nIdx] & 0x000000FF) << (8 * nByCount));
            nIdx++;
        } while (nByCount > 0);

        return nRtnValue;
    }

    public static int getIntLittleEndian(byte[] buffer, int nIdx, int nByCount) {
        int nRtnValue = 0;
        int nInc = 0;

        do {
            nRtnValue += ((buffer[nIdx] & 0x000000FF) << (8 * nInc));
            nIdx++;
            nInc++;
        } while (nInc < nByCount);

        return nRtnValue;
    }


    public static void LogToHexString(String strTag, byte[] buf, int nLen) {
        String strRcvData = "Hex [" + String.valueOf(nLen) + "] ";
        for (int i = 0; i < nLen; ++i) {
            strRcvData += String.format("%02x ", buf[i]);
            //strRcvData += Integer.toHexString(buf[i] & 0x000000FF) + ", ";
        }
        Log.d(strTag, strRcvData);
    }

    public static String ToHexString(byte[] buf, int nLen) {
        String strRcvData = "";
        for (int i = 0; i < nLen; ++i) {
            strRcvData += String.format("%02x ", buf[i]);
            //strRcvData += Integer.toHexString(buf[i] & 0x000000FF) + ", ";
        }
        return strRcvData;
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

    public static String byteToString(byte[] array, String charsetName) {
        if (array == null)
            return null;

        String rtn = null;

        try {
            rtn = new String(array, charsetName);
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
}
