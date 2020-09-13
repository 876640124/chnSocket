import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;

/**
 * 发送多条数据测试服务端接收连接需要的时间,用于测试NIO，BIO的性能
 */
public class C10Kclient {

    public static void main(String[] args) throws UnknownHostException {
        LinkedList<SocketChannel> clients = new LinkedList<>();
        InetSocketAddress serverAddr = new InetSocketAddress(InetAddress.getLocalHost().getHostAddress(), 9090);

        //端口号的问题：65535
        //  建立多个连接
        for (int i = 10000; i < 65000; i++) {
            try {
                SocketChannel client1 = SocketChannel.open();

                //SocketChannel client2 = SocketChannel.open();

                /*
                linux中你看到的连接就是：
                client...port: 10508
                client...port: 10508
                 */

                client1.bind(new InetSocketAddress(InetAddress.getLocalHost().getHostAddress(), i));
                //  192.168.150.1：10000   192.168.150.11：9090
                client1.connect(serverAddr);
                ByteBuffer buffer = ByteBuffer.allocateDirect(4096);
                buffer.put("hello".getBytes());
                client1.write(buffer);
                clients.add(client1);


                /*client2.bind(new InetSocketAddress("192.168.110.100", i));
                //  192.168.110.100：10000  192.168.150.11：9090
                client2.connect(serverAddr);
                clients.add(client2);*/

            } catch (IOException e) {
                e.printStackTrace();
            }


        }
        System.out.println("clients "+ clients.size());

        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
