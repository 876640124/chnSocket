package socket2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 多线程方式创建server端为了可以监听多个连接每次拿到连接后开启一个新线程单独处理每个连接不阻塞其他的连接到来
 * 无线开辟线程，到达一定数量级存在问题
 */
public class SocketBIOThread {
    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(9090);
        System.out.println("Port 9090 has been listening");

        for (; ; ) {
            // 阻塞获等待客户端连接
            Socket client = server.accept();

            new Thread(() -> {
                try (InputStream in = client.getInputStream()) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    while (true) {
                        // 阻塞获取数据
                        String data = reader.readLine();
                        if (null != data) {
                            System.out.println(data);
                        } else {
                            client.close();
                            break;
                        }
                    }
                    System.out.println("连接断开");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }

    }
}
