package com.ijustyce.fastandroiddev.net;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class MultipartRequest extends Request<String> {
    private ProcessListener mListener;
    private Map<String, String> headerMap;
    private Map<String, String> mParams;
    private FormFile[] files;
    private String BOUNDARY = "---------7dc05dba8f3e19";

    static Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError volleyError) {

            volleyError.printStackTrace();
        }
    };

    public MultipartRequest(String url, ProcessListener listener, Map<String, String> params, FormFile[] files) {
        this(Method.POST, url, listener, params, files);
    }

    public MultipartRequest(int method, String url, ProcessListener listener, Map<String, String> params, FormFile[] files) {
        super(method, url, errorListener);
        mListener = listener;
        mParams = params;
        this.files = files;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        headerMap = new HashMap<>();
        headerMap.put("Charset", "UTF-8");
        //Keep-Alive
        headerMap.put("Connection", "Keep-Alive");
        headerMap.put("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
        return headerMap;
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        //传参数
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : mParams.entrySet()) {
            // 构建表单字段内容
            sb.append("--");
            sb.append(BOUNDARY);
            sb.append("\r\n");
            sb.append("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"\r\n\r\n");
            sb.append(entry.getValue());
            sb.append("\r\n");
        }
        return sb.toString().getBytes();
    }

    public void handRequest(OutputStream out) {
        DataOutputStream dos = (DataOutputStream) out;
        try {
            //发送文件数据
            if (files != null) {
                for (FormFile file : files) {
                    // 发送文件数据
                    StringBuilder split = new StringBuilder();
                    split.append("--");
                    split.append(BOUNDARY);
                    split.append("\r\n");
                    split.append("Content-Disposition: form-data;name=\"")
                            .append(file.getParameterName())
                            .append("\";filename=\"")
                            .append(file.getFileName())
                            .append("\"\r\n");
                    split.append("Content-Type: ")
                            .append(file.getContentType())
                            .append("\r\n\r\n");
                    dos.write(split.toString().getBytes());
                    if (file.getInStream() != null) {
                        byte[] buffer = new byte[1024];
                        int len = -1;
                        int count = 0;
                        while ((len = file.getInStream().read(buffer)) != -1) {
                            dos.write(buffer, 0, len);
                            count += len;
                            if (mListener != null) {
                                mListener.onProcess(file.getFileSize(), count);
                            }
                        }
                        file.getInStream().close();
                    } else {
                        dos.write(file.getData(), 0, file.getData().length);
                    }
                    dos.write("\r\n".getBytes());
                }
            }
            dos.writeBytes("--" + BOUNDARY + "--\r\n");
            dos.flush();
        } catch (IOException e) {
            e.printStackTrace();
            if (mListener != null) {
                mListener.onError();
            }
            try {
                dos.close();
            } catch (IOException e1) {
                e1.printStackTrace();
                if (mListener != null) {
                    mListener.onError();
                }
            }
        }
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        String parsed;
        try {
            parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
        } catch (UnsupportedEncodingException e) {
            parsed = new String(response.data);
        }
        return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    protected void deliverResponse(String response) {
        if (mListener != null) {
            mListener.onSuccess(response);
        }
    }

    @Override
    public void deliverError(VolleyError error) {
        error.printStackTrace();
        if (mListener != null) {
            mListener.onError();
        }
    }
}
