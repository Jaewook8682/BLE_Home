/*
 * STX      Length	    cmd     PAYLOAD     CS      ETX
 * 2	    1		    1		n			1       1
 *
 * 최소 사이즈 : 6
 * 최대 사이즈 : 6 + 64
 *
 * 항목		설명									nByte		value
 * STX		프레임의 시작을 표시					2			0xFF + 0xAF
 * Length	Payload의 길이(바이트 수)를 표시함	    4			Ex) 0x07
 * cmd		Command								1			Ex) 0x30
 * Payload	각종 정보								가변		    최대 64 바이트
 * CS		에러체크 바이트						１			(Length ~ Payload 끝까지 각 바이트를 XOR 한 값)^(0xFF)
 * ETX		프레임의 끝을 표시						1			0xFA
 *
 */

package com.setting.myapplication.obd;

import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

/**
 * frame
 */

public class ComFrame {
    private final String TAG = "ComFrame";

    byte[] _buf;

    public byte _cmd = 0;
    public int _payloadSize = 0;
    public byte[] _payload = null;

    public ComFrame() {
    }



    public byte[] getBuf() {
        return _buf;
    }



    public String GetPayloadToString(int index, int count) {
        if (_payload == null || _payloadSize < (index + count))
            return null;

        String rtn = Util_OBD.byteToString(_payload, index, count, PacketCheck.charsetName);

        return rtn;
    }

    public byte[] makeFrame(byte cmd, byte[] payload) {
        int payloadSize = (payload == null) ? 0 : payload.length;

        int bufSize = PacketCheck.PACKET_MIN_SIZE + payloadSize;
        if (_buf == null || _buf.length != bufSize)
            _buf = new byte[bufSize];
        Arrays.fill(_buf, (byte) 0);

        ByteBuffer bb = ByteBuffer.wrap(_buf);
        bb.order(ByteOrder.BIG_ENDIAN);

        // STX
        bb.put(PacketCheck.STX);


        // payload
        if (payload != null && payloadSize > 0)
            bb.put(payload);



        bb.put(PacketCheck.ETX);
        //Util_Byte.LogToHexString("send",_buf,_buf.length);
        return _buf;
    }






    public void writeLog() {
        String strRcvData = "Hex [" + String.valueOf(_payloadSize) + "] ";
        for (int i = 0; i < _payloadSize; ++i) {
            strRcvData += String.format("%00X ", _payload[i]);
        }
        Log.d(TAG, strRcvData);
    }

}
