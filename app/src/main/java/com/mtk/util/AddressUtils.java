package com.mtk.util;

import com.mtk.map.BMessage;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

public class AddressUtils {
    public static String decodeUnicode(String theString) {
        int len = theString.length();
        StringBuffer outBuffer = new StringBuffer(len);
        int x = 0;
        while (x < len) {
            int x2 = x + 1;
            char aChar = theString.charAt(x);
            if (aChar == '\\') {
                x = x2 + 1;
                aChar = theString.charAt(x2);
                if (aChar == 'u') {
                    int value = 0;
                    int i = 0;
                    while (i < 4) {
                        x2 = x + 1;
                        aChar = theString.charAt(x);
                        switch (aChar) {
                            case '0':
                            case '1':
                            case '2':
                            case '3':
                            case '4':
                            case '5':
                            case '6':
                            case '7':
                            case '8':
                            case '9':
                                value = ((value << 4) + aChar) - 48;
                                break;
                            case 'A':
                            case 'B':
                            case 'C':
                            case 'D':
                            case 'E':
                            case 'F':
                                value = (((value << 4) + 10) + aChar) - 65;
                                break;
                            case 'a':
                            case 'b':
                            case 'c':
                            case 'd':
                            case 'e':
                            case 'f':
                                value = (((value << 4) + 10) + aChar) - 97;
                                break;
                            default:
                                throw new IllegalArgumentException("Malformed      encoding.");
                        }
                        i++;
                        x = x2;
                    }
                    outBuffer.append((char) value);
                } else {
                    if (aChar == 't') {
                        aChar = '\t';
                    } else if (aChar == 'r') {
                        aChar = '\r';
                    } else if (aChar == 'n') {
                        aChar = '\n';
                    } else if (aChar == 'f') {
                        aChar = '\f';
                    }
                    outBuffer.append(aChar);
                }
            } else {
                outBuffer.append(aChar);
                x = x2;
            }
        }
        return outBuffer.toString();
    }

    public String getAddresses(String content, String encodingString) throws Throwable {
        String returnStr = getResult("http://ip.taobao.com/service/getIpInfo.php", content, encodingString);
        if (returnStr == null) {
            return null;
        }
        System.out.println(returnStr);
        String[] temp = returnStr.split(",");
        if (temp.length < 3) {
            return "0";
        }
        return decodeUnicode(temp[1].split(BMessage.SEPRATOR)[2].replaceAll("\"", ""));
    }

    private String getResult(String urlStr, String content, String encoding) throws Throwable {
        IOException e;
        Throwable th;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(urlStr);
            URL url2;
            try {
                connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(2000);
                connection.setReadTimeout(2000);
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setRequestMethod("POST");
                connection.setUseCaches(false);
                connection.connect();
                DataOutputStream out = new DataOutputStream(connection.getOutputStream());
                out.writeBytes(content);
                out.flush();
                out.close();
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), encoding));
                StringBuffer buffer = new StringBuffer();
                String str = "";
                while (true) {
                    str = reader.readLine();
                    if (str == null) {
                        break;
                    }
                    buffer.append(str);
                }
                reader.close();
                String stringBuffer = buffer.toString();
                if (connection != null) {
                    connection.disconnect();
                }
                url2 = url;
                return stringBuffer;
            } catch (IOException e2) {
                e = e2;
                url2 = url;
                try {
                    e.printStackTrace();
                    if (connection != null) {
                        connection.disconnect();
                    }
                    return null;
                } catch (Throwable th2) {
                    th = th2;
                    if (connection != null) {
                        connection.disconnect();
                    }
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                url2 = url;
                if (connection != null) {
                    connection.disconnect();
                }
                throw th;
            }
        } catch (IOException e3) {
            e = e3;
            e.printStackTrace();
            if (connection != null) {
                connection.disconnect();
            }
            return null;
        }
    }
}
