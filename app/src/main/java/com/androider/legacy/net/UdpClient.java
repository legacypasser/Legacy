package com.androider.legacy.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;
import java.nio.channels.Selector;
import java.util.PriorityQueue;

/**
 * Created by Think on 2015/5/9.
 */
public class UdpClient {
    DatagramChannel channel;
    InetSocketAddress targetServer = new InetSocketAddress(NetConstants.serverAddr, NetConstants.serverPort);;
    public boolean isRunning;
    private UdpClient(){
    }

    private static class SingletonHolder{
        private static final UdpClient instance = new UdpClient();
    }
    public static UdpClient getInstance(){
        return SingletonHolder.instance;
    }

    public void open(){
        try {
            channel = DatagramChannel.open();
            channel.socket().bind(new InetSocketAddress(NetConstants.listenPort));
            channel.configureBlocking(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close(){
        channel.socket().close();
        try {
            channel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
