package com.mtk.data;

public class NoDataException extends Exception {
    private static final long serialVersionUID = 1;
    private static final String msg="";

    public NoDataException() {
        super(msg);
    }

    public NoDataException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public NoDataException(Throwable cause) {
        super(cause);
    }
}
