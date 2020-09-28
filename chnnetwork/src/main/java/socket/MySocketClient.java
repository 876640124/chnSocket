package socket;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

/**
 * @description:
 * @author: ChenHaoNan
 * @create: 2020-09-28
 **/
public class MySocketClient {
    public static void main(String[] args) {
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            String hostAddress = localHost.getHostAddress();
            System.out.println(hostAddress);
            Socket socket = new Socket(hostAddress, 9090);
            OutputStream outputStream = socket.getOutputStream();
            new Thread(() -> {
                try (OutputStream out = socket.getOutputStream()) {
                    while (true){
                        InputStream in = System.in;
                        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                        String message = reader.readLine();
                        outputStream.write(message.getBytes("UTF-8"));
                        outputStream.flush();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
            new Thread(()->{
                try (InputStream in = socket.getInputStream()){
                    byte[] bytes = new byte[1024];
                    int length;
                    StringBuilder str = new StringBuilder();
                    while ((length = in.read(bytes)) != -1) {
                        str.append(new String(bytes, "UTF-8"));
                        System.out.println(str);
                    }
                }catch (IOException e){
                    e.printStackTrace();
                }
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
