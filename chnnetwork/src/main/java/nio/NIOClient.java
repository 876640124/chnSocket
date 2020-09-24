package nio;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class NIOClient {
    public static void main(String[] args) throws Exception {
        InetSocketAddress serverAddr = new InetSocketAddress(InetAddress.getLocalHost().getHostAddress(), 9090);
        SocketChannel client1 = SocketChannel.open();
        client1.bind(new InetSocketAddress(InetAddress.getLocalHost().getHostAddress(), 5555));
        //  192.168.150.1：10000   192.168.150.11：9090
        client1.connect(serverAddr);
        ByteBuffer buffer = ByteBuffer.allocateDirect(4096);
        buffer.put("hello".getBytes());
        client1.write(buffer);
        System.in.read();
    }
}
