package org.an5w3r.an5w3rBot.util;

import org.an5w3r.an5w3rBot.entity.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.*;
import java.net.HttpURLConnection;

import java.net.URL;
import java.util.*;

public class ImageUtil {
    private static final Logger logger = LoggerFactory.getLogger(ImageUtil.class);


    public static Image getRandomImageLocal(String src) throws IOException {
        Image retImg = new Image();
        //无法获取图片名称
        File folder = new File(src);
        List<String> imagePaths = new ArrayList<>();

        if (folder.isDirectory()) {
            for (File file : folder.listFiles()) {
                if (isImageFile(file)) {
                    imagePaths.add(file.getAbsolutePath());
                }
            }
        }

        if (!imagePaths.isEmpty()) {
            Random random = new Random(System.currentTimeMillis());
            String randomImagePath = imagePaths.get(random.nextInt(imagePaths.size()));
            try {
                File file = new File(randomImagePath);
                retImg.setFileName(file.getName());
                String base64Image = encodeFileToBase64Binary(randomImagePath);
                retImg.setFile("base64://"+base64Image);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            retImg.setFileName("FromURL");
            retImg.setFile(getRandomImageUrl());
            retImg.setText("图片来源网络");
            retImg.setType("网络图片");
        }

        return retImg;
    }

    public static String getRandomImageUrl(){
//        https://api.sevin.cn/api/ecy.php
        StringBuilder content = new StringBuilder();
        try {
            // 创建URL对象
            URL url = new URL("https://api.sevin.cn/api/ecy.php");
            // 打开连接
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            // 设置请求方法为GET
            connection.setRequestMethod("GET");

            // 读取响应内容
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }

            // 关闭流
            in.close();

            // 打印响应内容
            logger.info("Response Content: " + content.toString());
        } catch (Exception e) {
            return "请求时出了点小问题";
        }
        return content.toString();
    }

    private static boolean isImageFile(File file) {
        String[] imageExtensions = { "jpg", "jpeg", "png", "gif", "bmp" };
        String fileName = file.getName().toLowerCase();
        for (String extension : imageExtensions) {
            if (fileName.endsWith(extension)) {
                return true;
            }
        }
        return false;
    }

    private static String encodeFileToBase64Binary(String filePath) throws IOException {
        File file = new File(filePath);
        FileInputStream fileInputStream = new FileInputStream(file);
        byte[] bytes = new byte[(int) file.length()];
        fileInputStream.read(bytes);
        fileInputStream.close();
        return Base64.getEncoder().encodeToString(bytes);
    }

    public static String cropImage(String base64Image) throws IOException {
        BufferedImage image = base64ToBufferedImage(base64Image);

        int width = image.getWidth();
        int height = image.getHeight();
        int guessRatio = Integer.parseInt(JSONUtil.getSettingMap().get("guessRatio"));
        int newWidth = width / guessRatio;
        int newHeight = height / guessRatio;

        Random rand = new Random();
        int x = rand.nextInt(width - newWidth);
        int y = rand.nextInt(height - newHeight);

        BufferedImage croppedImage = image.getSubimage(x, y, newWidth, newHeight);
        return bufferedImageToBase64(croppedImage);
    }

    // 将Base64字符串转换为BufferedImage
    public static BufferedImage base64ToBufferedImage(String base64Image) throws IOException {
        base64Image =  base64Image.replaceFirst("base64://","");
        byte[] imageBytes = Base64.getDecoder().decode(base64Image);
        ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);
        return ImageIO.read(bis);
    }

    // 将BufferedImage转换为Base64字符串
    public static String bufferedImageToBase64(BufferedImage image) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", bos);
        byte[] imageBytes = bos.toByteArray();
        return "base64://"+Base64.getEncoder().encodeToString(imageBytes);
    }



}

