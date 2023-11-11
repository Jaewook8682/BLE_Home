package com.setting.myapplication.obd;

public class CMD {

    public static final byte CMD_DEVICE_INFO = (byte)0x02;//단말기 정보요청(스마트폰 => IOTGPS) //단말기 정보요청응답(IOTGPS => 스마트폰)

    public static final byte CMD_FILEINFO = (byte)0x03;//파일정보 요청(스마트폰 => IOTGPS) //파일정보 요청응답(IOTGPS => 스마트폰)
    public static final byte CMD_FILEINFO2 = (byte)0x33;//파일정보 요청(스마트폰 => IOTGPS) //파일정보 요청응답(IOTGPS => 스마트폰)
    public static final byte CMD_FILEDATA_REQ = (byte)0x04;//파일데이터 요청(IOTGPS => 스마트폰)
    public static final byte CMD_FILEDATA_TRANS = (byte)0x04;//파일데이터 전송(스마트폰 => IOTGPS) //파일데이터 전송응답(IOTGPS => 스마트폰)
    public static final byte CMD_FILEDATA_TRANS2 = (byte)0xa8;//가상데이터 전송(스마트폰 => IOTGPS) //파일데이터 전송응답(IOTGPS => 스마트폰) fa가 없는 가상데이터 전송
    public static final byte CMD_FILEDATA_NEXT = (byte)0x0a;//다음패킷요청(IOTGPS => 스마트폰)
    public static final byte CMD_FILEDATA_COMPLETE = (byte)0x05;//파일데이터 전송완료(스마트폰 => IOTGPS) //파일데이터 전송완료응답(IOTGPS => 스마트폰)

    public static final byte CMD_SETTING_READ = (byte)0x0b;//DB업데이트 취소요청(스마트폰 => IOTGPS) //DB업데이트 취소요청응답(IOTGPS => 스마트폰)
    public static final byte CMD_SETTING_WRITE = (byte)0x0c;//DB업데이트 취소요청(스마트폰 => IOTGPS) //DB업데이트 취소요청응답(IOTGPS => 스마트폰)
    public static final byte CMD_SETTING_READ_V2 = (byte)0x15;//환경설정 값받기 v2(스마트폰 => IOTGPS)
    public static final byte CMD_SETTING_WRITE_V2 = (byte)0x16;//환경설정 적용하기 v2(스마트폰 => IOTGPS)


    public static final byte CMD_DB_UPDATE_CANCEL = (byte)0x15;//DB업데이트 취소요청(스마트폰 => IOTGPS) //DB업데이트 취소요청응답(IOTGPS => 스마트폰)
    public static final byte CMD_DB_UPDATE_CANCEL_COMPLETE = (byte)0x15;//DB업데이트 취소요청완료(스마트폰 => IOTGPS) //DB업데이트 취소요청완료응답(IOTGPS => 스마트폰)
    public static final byte CMD_CANCEL_COMPLETE = (byte)0x18;//DB업데이트 취소요청(IOTGPS => 스마트폰) //DB업데이트 취소요청응답(IOTGPS => 스마트폰)
    public static final byte CMD_CANCEL_COMPLETE_RESPONSE = (byte)0x18;//DB업데이트 취소요청완료(스마트폰 => IOTGPS) //DB업데이트 취소요청완료응답(IOTGPS => 스마트폰)

    public static final byte CMD_DB_VERSION = (byte)0x20; //안전운전 DB버전 요청(스마트폰 => IOTGPS) //안전운전 DB버전정보(IOTGPS => 스마트폰)


    public static final byte Ack = (byte) 0x21;
    public static final byte Nak = (byte) 0x22;

}
