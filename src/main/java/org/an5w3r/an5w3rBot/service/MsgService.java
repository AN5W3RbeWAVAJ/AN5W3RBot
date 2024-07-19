package org.an5w3r.an5w3rBot.service;

import org.an5w3r.an5w3rBot.dao.MsgDao;
import org.an5w3r.an5w3rBot.entity.MsgItem;

import java.io.IOException;
import java.util.ArrayList;

public class MsgService {//这里写具体如何回复
    public static ArrayList<MsgItem> msgTipsText(String message) throws IOException {
        ArrayList<MsgItem> msgList = new ArrayList<>();
        MsgItem item = new MsgItem("text","text", message);
        msgList.add(item);
        return msgList;
    }

    public static ArrayList<MsgItem> msgOneText(String message) throws IOException {
        ArrayList<MsgItem> msgList = new ArrayList<>();
        MsgItem item = new MsgItem("text","text", MsgDao.getTextByMsg(message));
        msgList.add(item);
        return msgList;
    }


    public static ArrayList<MsgItem> msgTranslationText(String[] msgStr){//#翻译 文本 目标语言 源语言
        ArrayList<MsgItem> msgList = new ArrayList<>();

        MsgItem item = new MsgItem("text","text",MsgDao.getTranslation(msgStr));
        msgList.add(item);

        return msgList;
    }

    public static ArrayList<MsgItem> msgOneRandomImage(String message) throws IOException {
        ArrayList<MsgItem> msgList = new ArrayList<>();
        MsgItem item = new MsgItem("image","file", MsgDao.getImageByMsg(message));
        msgList.add(item);
        return msgList;
    }

}
