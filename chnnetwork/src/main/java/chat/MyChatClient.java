package chat;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

// 最原始的socket连接客户端
public class MyChatClient {
    public static void main(String[] args) {

        try {
            InetAddress localHost = InetAddress.getLocalHost();
            String hostAddress = localHost.getHostAddress();
            System.out.println(hostAddress);
            Socket client = new Socket(hostAddress, 9090);
 
            //发送线程
            new Thread(() -> {
                OutputStream out = null;
                try {

                  /* client.setTcpNoDelay(true);
                    client.setSendBufferSize(20);*/
                    out = client.getOutputStream();

                    InputStream in = System.in;
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
                    while (true) {
                        String line = reader.readLine();
                        if (line != null) {
                            /*byte[] bb = line.getBytes();
                            for (byte b : bb) {
                                out.write(b);
                            }
                            out.flush();*/
                           writer.write(line);
                            writer.write("\n");
                            writer.flush();

                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }, "发送线程").start();

            //接收线程
            new Thread(() -> {
                InputStream inputStream = null;
                try {
                    inputStream = client.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    // char[] data = new char[1024];
                    while (true) {

                       /* int num = reader.read(data);
                        if (num>0){
                            System.out.println(new String(data, 0, num));
                        }else{
                            client.close();
                            break;
                        }*/

                        String dataline = reader.readLine(); //阻塞2

                        if (null != dataline) {
                            System.out.println(dataline);
                        } else {
                            client.close();
                            break;
                        }
                    }
                    System.out.println("客户端断开");
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
