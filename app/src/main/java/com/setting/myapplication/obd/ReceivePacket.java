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

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ReceivePacket {
    private final String TAG = "Receive";

    private Lock m_lock = new ReentrantLock();

    public static final int MAX_SIZE = 2048;

    private byte[] m_buffer = null;
    private int m_nMaxSize = 0;
    private int m_nSize = 0;

    private PacketCheck m_packetCheck = new PacketCheck();


    public ReceivePacket(int nMaxSize) {
        m_nMaxSize = nMaxSize;
        m_buffer = new byte[m_nMaxSize];
        m_nSize = 0;
    }

    public void clear() {
        m_lock.lock();
        m_nSize = 0;
        m_lock.unlock();
    }

    public void push(byte[] data, int nSize) {
        m_lock.lock();
        int nNewSize = m_nSize + nSize;
        if (nNewSize > m_nMaxSize) {
            // overflow
            clear();
        } else {
            System.arraycopy(data, 0, m_buffer, m_nSize, nSize);
            m_nSize = nNewSize;
        }

        m_lock.unlock();
    }

    public void pop(int nSize) {
        m_lock.lock();
        ppop(nSize);
        m_lock.unlock();
    }

    private void ppop(int nSize) {
        if (nSize >= m_nSize) {
            m_nSize = 0;
        } else {
            m_nSize -= nSize;
            System.arraycopy(m_buffer, nSize, m_buffer, 0, m_nSize);
        }
        Util_Byte.LogToHexString("ppop",m_buffer,m_nSize);
    }

    public void checkSTX(int nStartIdx) {
        // STX 체크
        int nFindSTX = m_packetCheck.findSTX(m_buffer, m_nSize, nStartIdx);
        if (nFindSTX == 0) {

        } else if (nFindSTX > 0) {
            // STX 이전 데이터는 버림
            pop(nFindSTX);
        } else {
            // 전체 데이터 삭제
            clear();
        }
    }

    public byte[] popPacket() {
        m_lock.lock();
        byte[] packet = null;

        while (m_nSize >= PacketCheck.PACKET_MIN_SIZE) {

            int nSize = m_packetCheck.check(m_buffer, m_nSize);
            if (nSize < 0) {
             //   Log.d(TAG, "rcv check fail = " + nSize);

                // err 02
                if (nSize == PacketCheck.CHK_ERR_STX) {
                    checkSTX(0);
                }  else if (nSize == PacketCheck.CHK_ERR_SHORT) {

                    break;
                }
            } else {
                // 패킷이 존재
                packet = new byte[nSize];
                System.arraycopy(m_buffer, 0, packet, 0, nSize);

                // 찾은 패킷은 버퍼에서 제거
                ppop(nSize);
                break;
            }
        }
        m_lock.unlock();

        return packet;
    }

}
