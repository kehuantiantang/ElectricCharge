package com.kehuantiantang.logcollection.upload;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

/**
 * Created by kehuantiantang on 2015/11/30.
 */
public class HttpManager {
    //连接超时
    private static final int SET_CONNECTION_TIMEOUT = 5 * 1000;
    //上传超时
    private static final int SET_SOCKET_TIMEOUT = 20 * 1000;

    private static final String BOUNDARY = UUID.randomUUID().toString();

    private static final String MP_BOUNDARY = "--" + BOUNDARY;

    private static final String END_MP_BOUNDARY = "--" + BOUNDARY + "--";

    private static final String LINE_END = "\r\n";

    private static final String CHARSET = "UTF-8";


    public static String uploadFileStream(String path, File logFile, HttpParameters params) {
        URL url;
        try {
            url = new URL(path);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
        HttpURLConnection conn = null;
        InputStream inStream = null;
        try {
            conn = getHttpURLConnection(url);
            OutputStream outputStream = conn.getOutputStream();
            writeTitleStream(outputStream, logFile, params);
            writeFileStream(outputStream, logFile);
            return getResult(conn);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            // 关闭连接
            if (inStream != null) {
                try {
                    inStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    /**
     * 设置连接的一系列参数
     * @param url
     * @return
     * @throws IOException
     */
    private static HttpURLConnection getHttpURLConnection(URL url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");// 提交模式
        conn.setConnectTimeout(SET_CONNECTION_TIMEOUT);//连接超时 单位毫秒
        conn.setReadTimeout(SET_SOCKET_TIMEOUT);//读取超时 单位毫秒
        conn.setDoOutput(true);// 是否输入参数
        //设置属性，文件上传
        conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
        return conn;
    }

    /**
     * 写数据的标题头，内容如下
     * ----UUID
     * Content-disposition: form-data; name="logfile"; filename="key"
     * Content-Type: application/octet-stream; charset=utf-8
     * <p/>
     * Content
     * ----UUID----
     *
     * @param outputStream
     * @param logFile
     * @throws IOException
     */
    private static void writeTitleStream(OutputStream outputStream, File logFile, HttpParameters params) throws IOException {
        //多个文件
        if(params != null){
            String key;
            for (int i = 0; i < params.size(); i++) {
                key = params.getKey(i);
                StringBuilder temp = new StringBuilder(10);
                temp.setLength(0);
                temp.append(MP_BOUNDARY).append(LINE_END);
                temp.append("content-disposition: form-data; name=\"").append(key)
                        .append("\"").append(LINE_END + LINE_END);
                temp.append(params.getValue(key)).append(LINE_END);
                outputStream.write(temp.toString().getBytes());
            }
        }

        //只有一个文件上传
        StringBuilder temp = new StringBuilder();
        temp.append(MP_BOUNDARY).append(LINE_END);
        temp.append(
                "content-disposition: form-data; name=\"logfile\"; filename=\"")
                .append(logFile.getName()).append("\"").append(LINE_END);
        temp.append("Content-Type: application/octet-stream; charset=utf-8").append(LINE_END + LINE_END);

        Log.e("output", temp.toString());
        outputStream.write(temp.toString().getBytes());
    }

    /**
     * 写入log日志中的file的content
     *
     * @param outputStream
     * @param logFile
     * @throws IOException
     */
    private static void writeFileStream(OutputStream outputStream, File logFile) throws IOException {
        //读取log文件，写入缓冲区
        FileInputStream logFileInputStream = new FileInputStream(logFile);
        //8k
        byte[] buffer = new byte[1024 * 8];
        while (true) {
            int count = logFileInputStream.read(buffer);
            if (count == -1) {
                break;
            }
            //写入文件缓冲区
            outputStream.write(buffer, 0, count);
        }
        outputStream.write((LINE_END + LINE_END).getBytes());
        outputStream.write((END_MP_BOUNDARY + LINE_END).getBytes());
    }

    /**
     * 返回Service端传递回来的结果
     *
     * @param conn
     * @return
     * @throws IOException
     */
    private static String getResult(HttpURLConnection conn) throws IOException {
        //获得返回的结果
        InputStream inStream = conn.getInputStream();
        ByteArrayOutputStream content = new ByteArrayOutputStream();
        int readBytes;
        byte[] sBuffer = new byte[512];
        while ((readBytes = inStream.read(sBuffer)) != -1) {
            content.write(sBuffer, 0, readBytes);
        }
        String result = new String(content.toByteArray(), CHARSET);
        return result;
    }
}
