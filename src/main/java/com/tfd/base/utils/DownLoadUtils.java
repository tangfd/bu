package com.tfd.base.utils;


import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class DownLoadUtils {
    private static final Log LOG = LogFactory.getLog(DownLoadUtils.class);
    private static final Map<String, String> CONTENT_TYPE = new HashMap<String, String>(4);
    private static final String ATTACHMENT = "attachment;fileName=";
    private static final String INLINE = "inline;fileName=";
    private static final String CONTENTDISPOSITION = "Content-Disposition";

    static {
        CONTENT_TYPE.put(".xls", "application/vnd.ms-excel;charset=utf-8");
        CONTENT_TYPE.put(".xlsx", "application/vnd.ms-excel;charset=utf-8");
        CONTENT_TYPE.put(".doc", "application/msword;charset=utf-8");
        CONTENT_TYPE.put(".docx", "application/msword;charset=utf-8");
    }

    public static void downLoad(HttpServletResponse response, String webPath, String fileName) {
        InputStream inStream = null;
        try {
            inStream = new FileInputStream(webPath);
            response.reset();
            // 设置文件类型
            response.setContentType(CONTENT_TYPE.get(fileName.substring(fileName.lastIndexOf("."))));
            response.setHeader(CONTENTDISPOSITION, ATTACHMENT + URLEncoder.encode(fileName, "UTF-8"));
            byte[] b = new byte[2049];
            int len;
            OutputStream outputStream = response.getOutputStream();
            while ((len = inStream.read(b)) > 0) {
                outputStream.write(b, 0, len);
            }
            outputStream.flush();
        } catch (Exception e) {
            LOG.error(e);
        } finally {
            IOUtils.closeQuietly(inStream);
        }
    }
}
