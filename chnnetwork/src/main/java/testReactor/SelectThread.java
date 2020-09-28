package testReactor;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 每个线程对应一个selector，多线程情况下该主机在并发客户端被分配到多个selector，每个客户端只绑定到其中一个selector
 */
public class SelectThread implements Runnable {
    Selector selector = null;

    LinkedBlockingQueue<Channel> lbq = new LinkedBlockingQueue<Channel>();

    SelectThreadGroup stg;

    public SelectThread(SelectThreadGroup stg) {
        try {
            this.stg = stg;
            this.selector = Selector.open();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {
        // LOOP
        while (true) {
            try {
                int nums = selector.select(); //没有设置超时，为阻塞

                if (nums > 0) {
                    Set<SelectionKey> selectionKeys = selector.selectedKeys();
                    Iterator<SelectionKey> iterator = selectionKeys.iterator();
                    // 线性处理
                    while (iterator.hasNext()) {
                        SelectionKey key = iterator.next();
                        iterator.remove();
                        // 判断是刚收到的线程还是有读写事件
                        if (key.isAcceptable()) {
                            // 多线程情况下接受并且要分发给其他的selector
                            acceptHandler(key);

                        } else if (key.isReadable()) {
                            readHandler(key);
                        } else if (key.isWritable()) {
                            writeHandler(key);
                        }

                    }
                }

                if (!lbq.isEmpty()){
                    Channel c = lbq.take();
                    if (c instanceof ServerSocketChannel){
                        // 绑定监听事件注册到多路复用器上
                        ServerSocketChannel server = (ServerSocketChannel) c;
                        server.register(selector,SelectionKey.OP_ACCEPT);
                    }else if (c instanceof  SocketChannel){
                        // 有客户端发数据将读事件绑定
                        SocketChannel client = (SocketChannel)c;
                        ByteBuffer buffer = ByteBuffer.allocateDirect(4096);
                        client.register(selector,SelectionKey.OP_READ);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void writeHandler(SelectionKey key) {
    }

    private void readHandler(SelectionKey key) {
        ByteBuffer buff = (ByteBuffer) key.attachment();
        SocketChannel client = (SocketChannel) key.channel();
        buff.clear();
        while (true) {
            try {
                int num = client.read(buff);
                if (num > 0) {
                    buff.flip();//将读取的内容翻转，直接写出
                    while (buff.hasRemaining()) {
                        client.write(buff);
                    }
                    buff.clear();
                } else if (num == 0) {
                    break;
                } else if (num < 0) {
                    // 客户端断开造成的
                    System.out.println("client: " + client.getRemoteAddress() + "is closed.");
                    key.cancel();
                    break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void acceptHandler(SelectionKey key) {
        ServerSocketChannel server = (ServerSocketChannel) key.channel();
        try {
            SocketChannel client = server.accept();
            client.configureBlocking(false);
            //选择一个多路复用器注册
            stg.nextSelector(client);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
