package com.setting.myapplication.obd;

public interface ReceiveListener {

    /**
     * 정상적인 패킷 수신 시 호출
     * @param data
     * @param nLen
     */
    void onReceivedPacket(byte[] data, int nLen);

}
