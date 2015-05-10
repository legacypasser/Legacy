package com.androider.legacy.net;

import android.util.Log;

import com.androider.legacy.data.Holder;
import com.androider.legacy.data.Record;
import com.androider.legacy.data.User;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

/**
 * Created by Think on 2015/5/9.
 */
public class Sender implements Runnable{
    private ByteBuffer sendBuf;
    private UdpClient common;

    @Override
    public void run() {
        while (true){
            try {
                sendToServer(""+User.id, NetConstants.heartHead);
                Thread.currentThread();
                Thread.sleep(NetConstants.heartSpace);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private Sender(){
        sendBuf = ByteBuffer.allocate(256);
        common = UdpClient.getInstance();
    }

    private static class SingletonHolder{
        private final static Sender instance = new Sender();
    }

    public static Sender getInstance(){
        return SingletonHolder.instance;
    }

    public void send(InetSocketAddress target, String information){
        sendBuf.clear();
        sendBuf.put(information.getBytes());
        sendBuf.flip();
        try {
            common.channel.send(sendBuf, target);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(String ip, int port, String information){
        InetSocketAddress target = new InetSocketAddress(ip, port);
        send(target, information);
    }

    public void sendToServer(String information, int type){
        String resultStr = NetConstants.prefix + type + NetConstants.regex + information;
        Log.v("panbo", resultStr);
        send(common.targetServer, resultStr);
    }

    public void sendToPeer(int id, String information){
        Record message = new Record(User.id, id, information);
        Holder.waitBack.put(message.edit, message);
        sendToServer(message.toString(), NetConstants.message);
    }

    public void feedBack(Record message){
        sendToServer(message.formFeedBack(), NetConstants.feedback);
    }
}
