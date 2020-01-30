package com.mtk.map;

import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.os.Handler;
import android.util.Log;
import com.mtk.BTNotificationApplication;
import java.util.HashMap;
import java.util.Map.Entry;
import com.mtk.map.MapConstants.Mailbox;

public class SmsContentObserver extends ContentObserver {
    private static final String TAG = "MessageObserver";
    private static final Context sContext = BTNotificationApplication.getInstance().getApplicationContext();
    private final String HEADER = "telecom/msg/";
    private SmsController mSmsController = null;
    private HashMap<Long, Integer> previousMessage;

    public class DatabaseMonitor extends Thread {
        public static final int MONITER_TYPE_ONLY_QUERY = 0;
        public static final int MONITER_TYPE_QUERY_AND_NOTIFY = 1;
        private int mQueryType = 0;

        private void queryMessage(java.util.HashMap<java.lang.Long, java.lang.Integer> r9) {
            /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find block by offset: 0x004c in list [B:13:0x0049]
	at jadx.core.utils.BlockUtils.getBlockByOffset(BlockUtils.java:42)
	at jadx.core.dex.instructions.IfNode.initBlocks(IfNode.java:60)
	at jadx.core.dex.visitors.blocksmaker.BlockFinish.initBlocksInIfNodes(BlockFinish.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockFinish.visit(BlockFinish.java:33)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:306)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:199)
*/
            /*
            r8 = this;
            r7 = 0;
            r0 = com.mtk.map.SmsContentObserver.sContext;	 Catch:{ Exception -> 0x0041, all -> 0x0053 }
            r0 = r0.getContentResolver();	 Catch:{ Exception -> 0x0041, all -> 0x0053 }
            r1 = "content://sms/";	 Catch:{ Exception -> 0x0041, all -> 0x0053 }
            r1 = android.net.Uri.parse(r1);	 Catch:{ Exception -> 0x0041, all -> 0x0053 }
            r2 = 2;	 Catch:{ Exception -> 0x0041, all -> 0x0053 }
            r2 = new java.lang.String[r2];	 Catch:{ Exception -> 0x0041, all -> 0x0053 }
            r3 = 0;	 Catch:{ Exception -> 0x0041, all -> 0x0053 }
            r4 = "_id";	 Catch:{ Exception -> 0x0041, all -> 0x0053 }
            r2[r3] = r4;	 Catch:{ Exception -> 0x0041, all -> 0x0053 }
            r3 = 1;	 Catch:{ Exception -> 0x0041, all -> 0x0053 }
            r4 = "type";	 Catch:{ Exception -> 0x0041, all -> 0x0053 }
            r2[r3] = r4;	 Catch:{ Exception -> 0x0041, all -> 0x0053 }
            r3 = 0;	 Catch:{ Exception -> 0x0041, all -> 0x0053 }
            r4 = 0;	 Catch:{ Exception -> 0x0041, all -> 0x0053 }
            r5 = 0;	 Catch:{ Exception -> 0x0041, all -> 0x0053 }
            r7 = r0.query(r1, r2, r3, r4, r5);	 Catch:{ Exception -> 0x0041, all -> 0x0053 }
            if (r7 == 0) goto L_0x004d;	 Catch:{ Exception -> 0x0041, all -> 0x0053 }
        L_0x0025:
            r0 = r7.moveToNext();	 Catch:{ Exception -> 0x0041, all -> 0x0053 }
            if (r0 == 0) goto L_0x004d;	 Catch:{ Exception -> 0x0041, all -> 0x0053 }
        L_0x002b:
            r0 = 0;	 Catch:{ Exception -> 0x0041, all -> 0x0053 }
            r0 = r7.getLong(r0);	 Catch:{ Exception -> 0x0041, all -> 0x0053 }
            r0 = java.lang.Long.valueOf(r0);	 Catch:{ Exception -> 0x0041, all -> 0x0053 }
            r1 = 1;	 Catch:{ Exception -> 0x0041, all -> 0x0053 }
            r1 = r7.getInt(r1);	 Catch:{ Exception -> 0x0041, all -> 0x0053 }
            r1 = java.lang.Integer.valueOf(r1);	 Catch:{ Exception -> 0x0041, all -> 0x0053 }
            r9.put(r0, r1);	 Catch:{ Exception -> 0x0041, all -> 0x0053 }
            goto L_0x0025;
        L_0x0041:
            r6 = move-exception;
            if (r7 == 0) goto L_0x0047;
        L_0x0044:
            r7.close();	 Catch:{ Exception -> 0x0041, all -> 0x0053 }
        L_0x0047:
            if (r7 == 0) goto L_0x004c;
        L_0x0049:
            r7.close();
        L_0x004c:
            return;
        L_0x004d:
            if (r7 == 0) goto L_0x004c;
        L_0x004f:
            r7.close();
            goto L_0x004c;
        L_0x0053:
            r0 = move-exception;
            if (r7 == 0) goto L_0x0059;
        L_0x0056:
            r7.close();
        L_0x0059:
            throw r0;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.mtk.map.SmsContentObserver.DatabaseMonitor.queryMessage(java.util.HashMap):void");
        }

        public DatabaseMonitor(int type) {
            this.mQueryType = type;
        }

        public void run() {
            if (this.mQueryType == 0) {
                try {
                    query();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (1 == this.mQueryType) {
                try {
                    queryAndNotify();
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            } else {
                Log.i(SmsContentObserver.TAG, "invalid monitor type:" + this.mQueryType);
            }
        }

        private synchronized void query() {
            queryMessage(SmsContentObserver.this.previousMessage);
            Log.i(SmsContentObserver.TAG, "query: size->" + SmsContentObserver.this.previousMessage.size());
        }

        private synchronized void queryAndNotify() {
            HashMap<Long, Integer> currentMessage = new HashMap();
            queryMessage(currentMessage);
            Log.i(SmsContentObserver.TAG, "database has been changed, mType is  previous size is " + SmsContentObserver.this.previousMessage.size() + "current size is " + currentMessage.size());
            Long key;
            if (SmsContentObserver.this.previousMessage.size() < currentMessage.size()) {
                for (Entry entry : currentMessage.entrySet()) {
                    key = (Long) entry.getKey();
                    String folder = revertMailboxType(((Integer) currentMessage.get(key)).intValue());
                    if (!(SmsContentObserver.this.previousMessage.containsKey(key) || folder == null || !folder.equals(Mailbox.INBOX))) {

                        SmsContentObserver.this.mSmsController.onMessageEvent(key, "telecom/msg/" + folder, 1);
                        Intent newSMSIntent = new Intent();
                        newSMSIntent.setAction("com.mtk.btnotification.SMS_RECEIVED");
                        SmsContentObserver.sContext.sendBroadcast(newSMSIntent);
                    }
                }
            } else {
                for (Entry entry2 : SmsContentObserver.this.previousMessage.entrySet()) {
                    key = (Long) entry2.getKey();
                    if (currentMessage.containsKey(key)) {
                        String oldFolder = revertMailboxType(((Integer) entry2.getValue()).intValue());
                        String newFolder = revertMailboxType(((Integer) currentMessage.get(key)).intValue());
                        if (!(newFolder == null || oldFolder == null || oldFolder.equals(newFolder) || newFolder.equals(Mailbox.DELETED))) {
                            SmsContentObserver.this.mSmsController.onMessageEvent(key, "telecom/msg/" + oldFolder, 3);
                        }
                    } else {
                        SmsContentObserver.this.mSmsController.onMessageEvent(key, "telecom/msg/" + revertMailboxType(((Integer) SmsContentObserver.this.previousMessage.get(key)).intValue()), 2);
                    }
                }
            }
            SmsContentObserver.this.previousMessage = currentMessage;
        }

        private String revertMailboxType(int smsMailboxType) {
            switch (smsMailboxType) {
                case 1:
                    return Mailbox.INBOX;
                case 2:
                    return Mailbox.SENT;
                case 3:
                    return Mailbox.DRAFT;
                case 4:
                    return Mailbox.OUTBOX;
                default:
                    return Mailbox.DELETED;
            }
        }
    }

    public SmsContentObserver(SmsController smsController) {
        super(new Handler());
        this.mSmsController = smsController;
        this.previousMessage = new HashMap();
        new DatabaseMonitor(0).start();
    }

    public void onChange(boolean onSelf) {
        super.onChange(onSelf);
        Log.i(TAG, "DataBase State Changed");
        new DatabaseMonitor(1).start();
    }
}
