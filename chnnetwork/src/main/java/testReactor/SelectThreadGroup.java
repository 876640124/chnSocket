package testReactor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.Channel;
import java.nio.channels.ServerSocketChannel;
import java.util.concurrent.atomic.AtomicInteger;

// 管理各个selector
public class SelectThreadGroup {

    SelectThread[] sts;

    ServerSocketChannel server;

    AtomicInteger xid = new AtomicInteger(0);

    // num为线程数
    public SelectThreadGroup(int num) {
        sts = new SelectThread[num];
        for (int i = 0; i < sts.length; i++) {
            sts[i] = new SelectThread(this);
            new Thread(sts[i]).start();
        }
    }

    public void bind(int port) {
        try {
            server = ServerSocketChannel.open();
            server.configureBlocking(false);
            server.bind(new InetSocketAddress(port));
            // 选择多路复用器并且绑定
            nextSelector(server);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void nextSelector(Channel c) {
        SelectThread st = next();
        ServerSocketChannel s = (ServerSocketChannel) c;
        // 注册到多路复用器并将阻塞的多路复用器wakeup(),因为牵扯到不同的线程需要在若wakeup与register在两个线程中执行可能会产生执行顺序造成的问题所以这里采用阻塞队列将需要操作的事件给其他线程
        st.lbq.add(c);
        st.selector.wakeup();
    }

    private SelectThread next() {
        int index = xid.incrementAndGet() % sts.length;
        return sts[index];
    }
}
