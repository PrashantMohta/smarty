package com.mtk.data;

import android.content.Context;
import android.os.Environment;
import android.os.Process;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class LogUtil {
    private static final String LOG_TAG = "LogUtil";
    private static LogUtil sINSTANCE = null;
    private LogDumper mLogDumper = null;
    private String mLogPath;
    private final int mPId;

    private class LogDumper extends Thread {
        final String filename = "BTNotification_Log.txt";
        String mCmds = null;
        private FileWriter mLogWriter = null;
        private java.lang.Process mLogcatProc;
        private final String mPID;
        private BufferedReader mReader = null;
        private boolean mRunning = true;

        public LogDumper(String pid, String dir) {
            this.mPID = pid;
            File logFile = new File(dir, "BTNotification_Log.txt");
            if (!logFile.exists()) {
                try {
                    logFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                this.mLogWriter = new FileWriter(logFile, true);
            } catch (IOException e2) {
                e2.printStackTrace();
            }
            this.mCmds = "logcat *:e *:w *:i | grep \"(" + this.mPID + ")\"";
        }

        public void stopLogs() {
            this.mRunning = false;
        }

        public void run() {
            try {
                this.mLogcatProc = Runtime.getRuntime().exec(this.mCmds);
                this.mReader = new BufferedReader(new InputStreamReader(this.mLogcatProc.getInputStream()), 1024);
                while (this.mRunning) {
                    String line = this.mReader.readLine();
                    if (line == null) {
                        break;
                    } else if (!(line.length() == 0 || this.mLogWriter == null || !line.contains(this.mPID))) {
                        this.mLogWriter.write(Util.getFormatedDate() + "  " + line + "\n");
                    }
                }
                if (this.mLogcatProc != null) {
                    this.mLogcatProc.destroy();
                    this.mLogcatProc = null;
                }
                if (this.mReader != null) {
                    try {
                        this.mReader.close();
                        this.mReader = null;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (this.mLogWriter != null) {
                    try {
                        this.mLogWriter.close();
                    } catch (IOException e2) {
                        e2.printStackTrace();
                    }
                    this.mLogWriter = null;
                }
            } catch (IOException e22) {
                e22.printStackTrace();
                if (this.mLogcatProc != null) {
                    this.mLogcatProc.destroy();
                    this.mLogcatProc = null;
                }
                if (this.mReader != null) {
                    try {
                        this.mReader.close();
                        this.mReader = null;
                    } catch (IOException e222) {
                        e222.printStackTrace();
                    }
                }
                if (this.mLogWriter != null) {
                    try {
                        this.mLogWriter.close();
                    } catch (IOException e2222) {
                        e2222.printStackTrace();
                    }
                    this.mLogWriter = null;
                }
            } catch (Throwable th) {
                if (this.mLogcatProc != null) {
                    this.mLogcatProc.destroy();
                    this.mLogcatProc = null;
                }
                if (this.mReader != null) {
                    try {
                        this.mReader.close();
                        this.mReader = null;
                    } catch (IOException e22222) {
                        e22222.printStackTrace();
                    }
                }
                if (this.mLogWriter != null) {
                    try {
                        this.mLogWriter.close();
                    } catch (IOException e222222) {
                        e222222.printStackTrace();
                    }
                    this.mLogWriter = null;
                }
            }
        }
    }

    public static LogUtil getInstance(Context context) {
        if (sINSTANCE == null) {
            sINSTANCE = new LogUtil(context);
        }
        return sINSTANCE;
    }

    private LogUtil(Context context) {
        init(context);
        this.mPId = Process.myPid();
    }

    void init(Context context) {
        if (Environment.getExternalStorageState().equals("mounted")) {
            this.mLogPath = Environment.getExternalStorageDirectory().getAbsolutePath();
            this.mLogPath += File.separator + context.getPackageName();
        } else {
            this.mLogPath = context.getFilesDir().getAbsolutePath();
        }
        File file = new File(this.mLogPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        Log.m2355i(LOG_TAG, "init(), Log file path=" + this.mLogPath, new Object[0]);
    }

    public void start() {
        Log.m2355i(LOG_TAG, "Log is running", new Object[0]);
        if (this.mLogDumper == null) {
            this.mLogDumper = new LogDumper(String.valueOf(this.mPId), this.mLogPath);
        }
        this.mLogDumper.start();
    }

    public void stop() {
        Log.m2355i(LOG_TAG, "Log is stopped", new Object[0]);
        if (this.mLogDumper != null) {
            this.mLogDumper.stopLogs();
            this.mLogDumper = null;
        }
    }

    public boolean isStarted() {
        return this.mLogDumper != null;
    }
}
