package com.webapp.support.httpClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Set;

/**
 * Created by SongCQ on 2017/8/29.
 */
public class HttpSendMessage {

    private static Logger logger = LoggerFactory.getLogger(HttpSendMessage.class);

    public static String postHttpRequest4Str(String url,Map<String,Object> paramMap){
        try (InputStream inputStream = postHttpRequest(url,paramMap) ){
            if(inputStream==null)
                return null;
            String line = null;
            StringBuilder result = new StringBuilder();
            try( BufferedReader in = new BufferedReader(
                    new InputStreamReader(inputStream,"UTF-8"))){
                while ((line = in.readLine()) != null) {
                    result.append(line);
                }
                return result.toString();
            }catch (IOException e){
                e.printStackTrace();
                throw e;
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    public static InputStream postHttpRequest(String url,Map<String,Object> paramMap){
        try {
            URLConnection conn = makeConnection(url);
            conn.setDoOutput(true);
            conn.setDoInput(true);

            StringBuilder builder = new StringBuilder();
            if(paramMap!=null){
                for(String paramKey : paramMap.keySet()){
                    Object paramVal = paramMap.get(paramKey);
                    if(builder.length()>0){
                        builder.append("&");
                    }
                    builder.append(paramKey);
                    builder.append("=");
                    if(paramVal instanceof String){
                        builder.append(URLEncoder.encode((String) paramVal, "UTF-8") );
                    }else{
                        builder.append(paramVal );
                    }
                }

                logger.info("\n request http msg to -->{}<--,\n params:-->{}<--, \n finally send to server param string is -->{}<--",url,paramMap,builder.toString());

                try(PrintWriter out = new PrintWriter(conn.getOutputStream())){
                    out.print(builder.toString());
                    out.flush();
                }catch (IOException e) {
                    e.printStackTrace();
                    throw e;
                }
            }

            return conn.getInputStream();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static URLConnection makeConnection(String url) throws IOException {
        URL urlObj = new URL(url);
        URLConnection conn = urlObj.openConnection();
        conn.setRequestProperty("accept", "*/*");
        conn.setRequestProperty("connection", "Keep-Alive");
        conn.setRequestProperty("user-agent",
                "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
        return conn;
    }
}
