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
    Selector selector;
    DatagramChannel channel;
    InetSocketAddress targetServer;
    private UdpClient(){
        try {
            channel = DatagramChannel.open();
            channel.socket().bind(new InetSocketAddress(NetConstants.listenPort));
            selector = Selector.open();
            channel.configureBlocking(true);
            targetServer = new InetSocketAddress(NetConstants.serverAddr, NetConstants.serverPort);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class SingletonHolder{
        private static final UdpClient instance = new UdpClient();
    }
    public static UdpClient getInstance(){
        return SingletonHolder.instance;
    }
}
