package nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;

/**
 * nio服务端代码，配置非阻塞，可以在单线程内接收多个客户端，该种实现方式效率问题原因在于连接增多之后每次需要遍历问询连接是否有数据到
 * 达，会有多次的用户态与内核态切换影响效率。
 */
public class NIOServer {
    public static void main(String[] args) throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(9090));

        // 设置为非阻塞
        serverSocketChannel.configureBlocking(false);

        // 存放已经获取到的client
        LinkedList<SocketChannel> clients = new LinkedList<>();

        while (true) {
            SocketChannel client = serverSocketChannel.accept();
            if (client == null) {
                System.out.println("client is null.");
            } else {
                client.configureBlocking(false);
                int port = client.socket().getPort();
                System.out.println("connect client port is: " + port);
                clients.add(client);
            }

            ByteBuffer buffer = ByteBuffer.allocateDirect(4096);

            // 遍历每个clients看是否能获取到数据
            clients.stream().forEach((c) -> {
                try {
                    int num = c.read(buffer);
                    if (num > 0) {
                        buffer.flip();
                        byte[] bytes = new byte[buffer.limit()];
                        buffer.get(bytes);

                        String str = new String(bytes);
                        System.out.println(c.socket().getPort()+"端口发送数据："+str);
                        buffer.clear();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            });
        }

    }
}
