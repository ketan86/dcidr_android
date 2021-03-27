package com.example.turbo.dcidr.main.message_handler;

/**
 * Created by Turbo on 7/24/2016.
 */
public class NotificationCodes {
    // group specific ntf codes
    public static final int GROUP_NTF_CODE_START = 1000;
        public static final int GROUP_CREATE_NTF_CODE = 1000;
    public static final int GROUP_NTF_CODE_END = 3999;

    // event specific ntf codes
    public static final int EVENT_NTF_CODE_START = 4000;
        public static final int EVENT_CREATE_NTF_CODE = 4000;
    public static final int EVENT_NTF_CODE_END = 4999;

    // friend specific ntf codes
    public static final int FRIEND_NTF_CODE_START = 6000;
        public static final int FRIEND_INVITE_NTF_CODE = 6000;
        public static final int FRIEND_REMIND_NTF_CODE = 6001;
        public static final int FRIEND_ACCEPT_NTF_CODE = 6002;
        public static final int FRIEND_DECLINE_NTF_CODE = 6003;
    public static final int FRIEND_NTF_CODE_END = 6999;

    // chweet specific nft codes
    public static final int CHWEET_NTF_CODE_START = 7000;
        public static final int CHWEET_UPDATE_NTF_CODE = 7000;
    public static final int CHWEET_NTF_CODE_END = 7999;
}
