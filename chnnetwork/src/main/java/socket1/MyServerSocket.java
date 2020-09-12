package socket1;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class MyServerSocket {

    public static void main(String[] args) {
        try {
            ServerSocket server = new ServerSocket(1111);
            System.out.println("等待连接");
            Socket socket = server.accept();
            System.out.println("连接创建");
            InputStream inputStream = socket.getInputStream();
            byte[] bytes = new byte[1024];
            int len;
            StringBuilder sb = new StringBuilder();
            while ((len = inputStream.read(bytes)) != -1) {
                sb.append(new String(bytes, 0, len,"UTF-8"));
            }
            System.out.println(sb);
            inputStream.close();
            socket.close();
            server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
