package chat;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

// 最原始的socket连接客户端
public class MyChatClient {
    public static void main(String[] args) {

        try {
            InetAddress localHost = InetAddress.getLocalHost();
            String hostAddress = localHost.getHostAddress();
            System.out.println(hostAddress);
            Socket socket = new Socket(hostAddress, 9090);

            //发送线程
            new Thread(() -> {
                OutputStream outputStream = null;
                try {
                    outputStream = socket.getOutputStream();
                    while (true) {
                        Scanner sc = new Scanner(System.in);
                        String str = sc.nextLine();
                        outputStream.write(str.getBytes("UTF-8"));
                        outputStream.flush();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }, "发送线程").start();

            //接收线程
            new Thread(() -> {
                InputStream inputStream = null;
                try {
                    inputStream = socket.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                    while (true) {
                        String data = reader.readLine();
                        System.out.println(data);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }, "接收线程").start();
            for (; ; ) {

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
