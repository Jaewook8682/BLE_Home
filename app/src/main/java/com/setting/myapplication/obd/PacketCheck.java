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


import com.setting.myapplication.userUtil.Util_Byte;

import java.nio.ByteOrder;

public class PacketCheck {

    public final static String charsetName = "UTF-8"; // 통신에 사용하는 스트링 인코딩 방식
    public final static ByteOrder byteOrder = ByteOrder.BIG_ENDIAN;

    /**
     * payload가 0일 경우 사이즈
     */
    public static final int PACKET_MIN_SIZE = 5;
    /**
     * payload 사이즈를 나타내는 크기
     */
    public static final int SIZE_LENGTH = 1;


    /**
     * 한패킷당 최대 파일 사이즈 (1024)
     */
    public static final int SIZE_MAX_FILESIZE = 512;
    /**
     * payload 최대 사이즈(길이포함)
     */
    public static final int SIZE_MAX_PAYLOAD = SIZE_MAX_FILESIZE+2;
    /**
     * 패킷 최대 사이즈
     */
    public static final int PACKET_MAX_SIZE = PACKET_MIN_SIZE + SIZE_MAX_PAYLOAD;
    /**
     * OBD SN 길이
     */
    public static final int SIZE_OBD_SN = 10;

    //public static final byte[]	STX							= { (byte) 0xFF, (byte) 0xAF };
    public static final byte STX = (byte) 0x24;
    public static final byte ETX = (byte) 0x40;


    public static final byte CAN_DATA_SIZE = 12;

    // packet index
    public static final int IDX_STX = 0;
    public static final int IDX_PAYLOAD = 3;


    // err
    public static final int CHK_ERR_MINSIZE = -1;   // 데이터를 제외한 최소 사이즈에 미달
    public static final int CHK_ERR_STX = -2;       // STX가 틀림
    public static final int CHK_ERR_ID = -3;        // ID가 틀림
    public static final int CHK_ERR_SHORT = -4;     // 데이터가 덜 받아진 상태
    public static final int CHK_ERR_ETX = -5;       // ETX가 틀림
    public static final int CHK_ERR_MAXSIZE = -6;   // 데이터 크기가 비 정상 적임


    public PacketCheck() {
    }


    /**
     * 입력된 버퍼가 하나의 패킷이 될 수 있는지 검사
     *
     * @param buf
     * @param nSize
     * @return 0 보다 작으면 패킷 아님.
     */
    public int check(byte[] buf, int nSize) {
        Util_Byte.LogToHexString("check",buf,nSize);
        int nPacketSize = CHK_ERR_MAXSIZE;

        try {
            //	Util_OBD.LogToHexString("buf", buf, nSize);

            // 최소 사이즈 검사
            if (buf[IDX_STX] != STX) {
                return CHK_ERR_STX;
            }

            //if ( buf[IDX_TGT_ID] != ID_PHONE || buf[IDX_SRC_ID] != ID_OBD )
            //	return CHK_ERR_ID;
            //쓰기일경우 길이
            int EXTPOS = 0;
            for(int i=0;i<nSize;i++){
                if(buf[i]==ETX){
                    EXTPOS = i;
                    break;
                }
            }


            if(EXTPOS == 0 ){
                return  PacketCheck.CHK_ERR_SHORT;
            }
            if ( buf[EXTPOS] != ETX && EXTPOS >0) {
                //    writeSDcard("comm.txt","CHK_ERR_ETX\n");

                return CHK_ERR_ETX;
            }else{

                nPacketSize = EXTPOS+1;
                //Log.e("ETXstr",new String(buf));
                Util_Byte.LogToHexString("ETX",buf,nPacketSize);
            }


        } catch (Exception e) {
            e.printStackTrace();
            //   writeSDcard("comm.txt","CHK_ERR_MAXSIZE\n");
            nPacketSize = CHK_ERR_MAXSIZE;
        }
        // writeSDcard("comm.txt","nPacketSize"+nPacketSize+"\n");
        return nPacketSize;
    }


    /**
     * 스타트 패킷 위치를 검색
     *
     * @param buf
     * @param nSize
     * @return
     */
    public int findSTX(byte[] buf, int nSize, int nStartIdx) {
        int nFind = -1;

        nSize -= 1;
        for (int i = nStartIdx; i < nSize; ++i) {
            if (buf[i] == STX) {
                nFind = i;
                break;
            }
        }

        return nFind;
    }

    public byte makeCheckSum(byte[] buf, int nStart, int nEnd) {
        byte cs = 0x00;

        try {
            cs = buf[nStart];
            nStart++;
            while (nStart <= nEnd) {
                cs = (byte) (cs ^ buf[nStart]);
                nStart++;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return cs;
    }

}
