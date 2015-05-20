package com.androider.legacy.net;

import android.content.Intent;
import android.util.Log;

import com.androider.legacy.activity.ChatActivity;
import com.androider.legacy.activity.MainActivity;
import com.androider.legacy.data.Constants;
import com.androider.legacy.service.ChatService;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

/**
 * Created by Think on 2015/5/9.
 */
public class Receiver implements Runnable{
    private ByteBuffer recvBuf;
    private UdpClient common;
    private CharsetDecoder decoder;

    @Override
    public void run() {
        while (common.isRunning){
            try {
                recvBuf.clear();
                common.channel.receive(recvBuf);
                recvBuf.flip();
                int type = -1;
                try {
                    type = recvBuf.get(4) - '0';
                }catch (IndexOutOfBoundsException e){
                    continue;
                }
                if(type < 0)
                    continue;
                Intent intent = new Intent(MainActivity.instance, ChatService.class);
                intent.putExtra(Constants.intentType, type);
                intent.putExtra(NetConstants.content, decoder.decode(recvBuf).toString());
                MainActivity.instance.startService(intent);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Receiver(){
        common = UdpClient.getInstance();
        recvBuf = ByteBuffer.allocate(256);
        decoder = Charset.forName("UTF-8").newDecoder();
    }

    private static class SingletonHolder{
        private static final Receiver instance = new Receiver();
    }
    public  static Receiver getInstance(){
        return SingletonHolder.instance;
    }
}
