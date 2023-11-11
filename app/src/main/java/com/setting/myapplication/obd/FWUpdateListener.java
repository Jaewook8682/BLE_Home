package com.setting.myapplication.obd;

/**
 * OBD FW Update 상태 수신
 */
public interface FWUpdateListener {

    enum EnumFWUPDATEState {
        none,
        start,
        fw_sending,
        end,
        fail,
    }

    /**
     * 업데이트 상태
     * @param state
     */
    void onFWUpdateState(EnumFWUPDATEState state);


    /**
     * OBD로 파일 전송 상태 수신
     * @param pos       전송 한 인덱스
     * @param total     전송해야 할 전체 개수
     */
    void onFWFileSend(int pos, int total);

}
