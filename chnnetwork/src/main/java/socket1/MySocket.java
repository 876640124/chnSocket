package socket1;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
// 最原始的socket连接客户端
public class MySocket {
    public static void main(String[] args) {
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            String hostAddress = localHost.getHostAddress();
            System.out.println(hostAddress);
            Socket socket = new Socket(hostAddress, 9090);
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write("1".getBytes("UTF-8"));
            outputStream.flush();
            outputStream.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
