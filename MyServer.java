import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

public class MyServer {
    class Worker implements Runnable {
        Socket socket;
        DataCache dataCache;
        Worker(Socket socket) {
            this.socket = socket;
        }

        // 返回一个信号量指示操作是否完成
        // 涉及到读写文件操作，需要保证线程安全
        boolean handledataset(int tag, String username, String passwd) {
            System.out.println(username.length());
            return true;
        }

        void handleREQ() {
            // 反序列化-读取req
            try {
                DataInputStream iais = new DataInputStream(socket.getInputStream());
                int reqlen = iais.readInt();
                // System.out.println("len:" + reqlen);
                int reqtag = iais.readInt();
                // System.out.println("tag:" + reqtag);
                StringBuffer username = new StringBuffer();
                StringBuffer password = new StringBuffer();
                for (int i = 0; i < 20; i++) {
                    username.append((char) iais.readByte());
                }
                // System.out.println("username:" + username);
                for (int i = 0; i < 30; i++) {
                    password.append((char) iais.readByte());
                }
                // System.out.println("password:" + password);
                // 得到了len,tag,username,password
                boolean ok = handledataset(reqtag, username.toString().trim(),
                        password.toString().trim());
                // return res
                OutputStream out = socket.getOutputStream();
                if (ok) {
                    if (reqtag == 1) {
                        out.write(new Response(reqtag + 1, 1, "register succeed").Serialization());
                    } else if (reqtag == 3) {
                        out.write(new Response(reqtag + 1, 1, "login succeed").Serialization());
                    }
                } else {
                    if (reqtag == 1) {
                        out.write(new Response(reqtag + 1, 1, "register failed").Serialization());
                    } else if (reqtag == 3) {
                        out.write(new Response(reqtag + 1, 1, "login failed").Serialization());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            System.out.println("客户端已连接：" + socket.getRemoteSocketAddress());
            // 接收并且解码req，处理请求后返回res
            handleREQ();
        }
    }

    public static void main(String[] args) throws IOException {
        // 监听套接口
        MyServer server = new MyServer();
        // 取出套接口处理IO
        server.launch();
    }

    void launch() throws IOException {
        int portNumber = 12001;
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(portNumber);
            while (true) {
                // 从q1队列中取出完成三路握手的套接口
                Socket clientSocket = serverSocket.accept();
                // 每个socket的逻辑写到worker线程里面
                (new Thread(new Worker(clientSocket))).start();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            serverSocket.close();
        }
    }
}
