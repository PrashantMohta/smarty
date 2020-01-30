package com.mtk.map;

import com.mtk.data.MessageObj;
import java.util.ArrayList;

public class MapConstants {
    public static final String ADDRESS = "address";
    public static final String BODY = "body";
    public static final String BT_MAP_BROADCAST_ACTION = "com.mtk.map.BT_MAP_COMMAND_ARRIVE";
    public static final String BT_MAP_REQUEST_ACTION = "com.mtk.map.BT_MAP_REQUEST";
    public static final String CONVERSATION = "content://mms-sms/conversataions";
    public static final String DATE = "date";
    public static final String DEFAULT_SORT_ORDER = "date DESC";
    public static final int DELETE_STATUS = 2;
    public static final String DISCONNECT = "DISCONNECT";
    public static final String DRAFT = "content://sms/draft";
    public static final String ERROR_CODE = "error_code";
    public static final int EVENT_DELETE = 2;
    public static final String EVENT_DELETE_S = "MessageDeleted";
    public static final int EVENT_NEW = 1;
    public static final String EVENT_NEW_S = "NewMessage";
    public static final int EVENT_SHIFT = 3;
    public static final String EVENT_SHIFT_S = "MessageShift";
    public static final String EXTRA_DATA = "EXTRA_DATA";
    public static final String FAILED = "content://sms/failed";
    public static final String INBOX = "content://sms/inbox";
    public static final String MAPD_WITH_VCF = " 2 1 ";
    public static final String MAPD_WITH_XML = " 2 0 ";
    public static final int MAX_SUBJECT_LEN = 254;
    public static final long MESSAGE_HANDLE_MASK = 1152921504606846975L;
    public static final String MESSAGE_SIZE = "m_size";
    public static final int MESSAGE_TYPE_ALL = 0;
    public static final int MESSAGE_TYPE_DRAFT = 3;
    public static final int MESSAGE_TYPE_FAILED = 5;
    public static final int MESSAGE_TYPE_INBOX = 1;
    public static final int MESSAGE_TYPE_OUTBOX = 4;
    public static final int MESSAGE_TYPE_QUEUED = 6;
    public static final int MESSAGE_TYPE_SENT = 2;
    public static final String MESSAGE_TYPE_SMS_GSM = "SMS_GSM";
    public static final int MSG_TYPE_SMS_GSM = 1;
    public static final String OUTBOX = "content://sms/outbox";
    public static final String PERSON = "person";
    public static final int POSITION_OF_COMMAND = 0;
    public static final int POSITION_OF_FOLDER = 3;
    public static final int POSITION_OF_LIST_SIZE = 2;
    public static final int POSITION_OF_MSG = 2;
    public static final int POSITION_OF_MSG_ID = 2;
    public static final int POSITION_OF_STATUS = 1;
    public static final int POSITION_OF_SUBJECT_SIZE = 4;
    public static final String PROTOCOL = "protocol";
    public static final String READ = "read";
    public static final int READ_STATUS = 1;
    public static final int RECEPIENT_STATUS_COMPLETE = 0;
    public static final int RECEPIENT_STATUS_FRACTIONED = 1;
    public static final int RECEPIENT_STATUS_NOTIFICATION = 2;
    public static final String REPLY_PATH_PRESENT = "reply_path_present";
    public static final int RESULT_ERROR = -1;
    public static final int RESULT_OK = 0;
    public static final String SEEN = "seen";
    public static final String SENT = "content://sms/sent";
    public static final String SERVICE_CENTER = "service_center";
    public static final String SMS_CONTENT_URI = "content://sms/";
    public static final long SMS_GSM_HANDLE_BASE = 1152921504606846976L;
    public static final int SRV_MAPC_ADP_CMD_GET_LISTING = 3;
    public static final int SRV_MAPC_ADP_CMD_GET_LIST_SIZE = 2;
    public static final int SRV_MAPC_ADP_CMD_GET_MSG = 4;
    public static final int SRV_MAPC_ADP_CMD_PUSH_MSG = 6;
    public static final int SRV_MAPC_ADP_CMD_SET_FOLDER = 1;
    public static final int SRV_MAPC_ADP_CMD_SET_STATUS = 5;
    public static final int SRV_MAPC_ADP_CONNECT_REQUEST = 8;
    public static final int SRV_MAPC_ADP_EVENT_REPORT = 7;
    public static final String STATUS = "status";
    public static final int STATUS_PENDING = 64;
    public static final String SUBJECT = "subject";
    public static final String THREAD_ID = "thread_id";
    public static final String TYPE = "type";
    public static final int UNREAD_STATUS = 0;
    public static final String _ID = "_id";
    public static final ArrayList<String> messageItemField = new C05621();

    static class C05621 extends ArrayList<String> {
        C05621() {
            add("handle");
            add(MapConstants.SUBJECT);
            add("datetime");
            add("sender_name");
            add("sender_addressing");
            add("recipient_name");
            add("recipient_addressing");
            add(MapConstants.TYPE);
            add("size");
            add(MessageObj.SUBTYPE_NOTI);
            add("reception_status");
            add("attachment_size");
            add("priority");
            add(MapConstants.READ);
            add(Mailbox.SENT);
            add("protected");
        }
    }

    class Mailbox {
        public static final String DELETED = "deleted";
        public static final String DRAFT = "draft";
        public static final String FAILED = "failed";
        public static final String INBOX = "inbox";
        public static final String MSG = "msg";
        public static final String OUTBOX = "outbox";
        public static final String SENT = "sent";
        public static final String TELECOM = "telecom";

        Mailbox() {
        }
    }

    class MessageItemField {
        public static final int AttachSize = 11;
        public static final int DateTime = 2;
        public static final int MsgHandle = 0;
        public static final int MsgType = 7;
        public static final int OrignalMsgSize = 8;
        public static final int RecipientAddr = 6;
        public static final int RecipientName = 5;
        public static final int RecipientStatus = 10;
        public static final int SenderAddr = 4;
        public static final int SenderName = 3;
        public static final int Subject = 1;
        public static final int bPriority = 12;
        public static final int bProtected = 15;
        public static final int bSent = 14;
        public static final int bText = 9;
        public static final int read = 13;

        MessageItemField() {
        }
    }
}
