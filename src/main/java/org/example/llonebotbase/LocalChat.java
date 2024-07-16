package org.example.llonebotbase;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Random;

public class LocalChat {
    private static final Logger logger = LoggerFactory.getLogger(LocalChat.class);
    private static final Map<String, String[]> TextJsonMap;

    static {//TODO 改为调用时加载
        try {
            TextJsonMap = getJsonMap();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //迁移到dao层
    public static String ChatByMsg(String in) throws UnsupportedEncodingException {//选择回复的文本
        for (String s : TextJsonMap.keySet()) {
            if (in.contains(s)) {
                String[] value = TextJsonMap.get(s);
                Random r = new Random();//util.Random
                int index =r.nextInt(value.length);
                return value[index];
            }
        }

        String encodedString = URLEncoder.encode(in, StandardCharsets.UTF_8);
        return AiOne(encodedString);
    }

    //在json文件中获取所有回复
    //TODO 添加图片.json util层
    public static Map<String, String[]> getJsonMap() throws IOException {
        String jsonStr = null;
        // 创建一个 Path 对象，表示要读取的文件路径
        Path path = Paths.get("AtMessage.json");

        // 使用 Charset 类的 forName 方法，指定字符编码为 UTF-8，并将 byte 数组转换为字符串
        byte[] bytes = Files.readAllBytes(path);
        jsonStr = new String(bytes, Charset.forName("UTF-8"));

        Map<String, String[]> resultMap = JSON.parseObject(jsonStr, new TypeReference<Map<String, String[]>>() {}.getType());

        return resultMap;
    }

    //    该函数用于调用qingyunke生成聊天的回复
    //TODO 更换API utils层
    public static String AiOne(String sendMsg) {
        try {
            HttpGet httpGet = new HttpGet("http://api.qingyunke.com/api.php?key=free&appid=0&msg=" + sendMsg);
            String user_agent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36 Edg/108.0.1462.42";
            httpGet.addHeader("user-agent", user_agent);
            CloseableHttpClient httpClient = HttpClients.createDefault();
            CloseableHttpResponse response = httpClient.execute(httpGet);
            String body = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            body = body.substring(body.indexOf("content") + 10, body.length() - 2);
            logger.info("AiOne={}", body);
            return body;
        } catch (Exception e) {
            logger.error(e.toString());
            return null;
        }
    }

}
