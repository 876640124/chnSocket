package testReactor;

public class MainThread {
    public static final int port = 9999;

    public static void main(String[] args) {
        //创建IO Thread
        SelectThreadGroup stg = new SelectThreadGroup(1);
        //监听的server注册到某一个selector
        stg.bind(port);

    }
}
