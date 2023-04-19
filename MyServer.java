import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MyServer {
    class DataCache {
        private String FilePath;
        private ReadWriteLock lock = new ReentrantReadWriteLock();
        private final Lock readLock = lock.readLock();
        private final Lock writeLock = lock.writeLock();

        public DataCache(String filepath) {
            this.FilePath = filepath;
        }

        // 用于登录
        public boolean read(String name, String password) {
            boolean ok = false;
            try {
                readLock.lock();
                mockTimeConsumingOpt();
                // read
                try {
                    BufferedReader reader = new BufferedReader(new FileReader(FilePath));

                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        System.out.println(line); // 这里可以对每行数据进行处理，例如将其存入一个 List 中
                        String[] parts = line.split("\\,");
                        if (parts[0].equals(name)) {
                            if (parts[1].equals(password)) {
                                ok = true;
                            }
                        }
                    }
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } finally {
                readLock.unlock();
            }
            return ok;
        }

        public boolean write(String name, String password) {
            boolean ok = true;
            try {
                writeLock.lock();
                readLock.lock();
                mockTimeConsumingOpt();
                // read，检查有没有这个账号
                try {
                    BufferedReader reader = new BufferedReader(new FileReader(FilePath));

                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        // System.out.println(line); 
                        String[] parts = line.split("\\,");
                        if (parts[0].equals(name)) {
                            ok = false;
                        }
                    }
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // write
                if (ok) {
                    try {
                        PrintWriter writer = new PrintWriter(new FileWriter(FilePath, true));
                        writer.println(name + ',' + password);
                        writer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } finally {
                writeLock.unlock();
                readLock.unlock();
            }
            return ok;
        }

        // sleep
        private void mockTimeConsumingOpt() {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    class Worker implements Runnable {
        Socket socket;
        DataCache dataCache;

        Worker(Socket socket, DataCache db) {
            this.dataCache = db;
            this.socket = socket;
        }

        // 返回一个信号量指示操作是否完成
        // 涉及到读写文件操作，需要保证线程安全
        boolean handledataset(int tag, String username, String passwd) {
        
            byte[] hashbytes = HashingExample.getSHA256Hash(passwd);
            passwd = HashingExample.bytesToHex(hashbytes);
            boolean ok = false;
            System.out.println("username: " + username + " " + "passwd: " + passwd);
            if (tag == 1) {
                // 注册
                // 账号是否存在
                // dataCache.read(username, passwd);
                // 不存在那么写入
                ok = dataCache.write(username, passwd);
            } else if (tag == 3) {
                // 登录
                ok = dataCache.read(username, passwd);
            }
            return ok;
        }

        void handleREQ() {
            // 反序列化-读取req
            try {
                DataInputStream iais = new DataInputStream(socket.getInputStream());
                int reqlen = iais.readInt();
                int reqtag = iais.readInt();
                StringBuffer username = new StringBuffer();
                StringBuffer password = new StringBuffer();
                for (int i = 0; i < 20; i++) {
                    username.append((char) iais.readByte());
                }
                for (int i = 0; i < 30; i++) {
                    password.append((char) iais.readByte());
                }
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
                        out.write(new Response(reqtag + 1, 0, "register failed").Serialization());
                    } else if (reqtag == 3) {
                        out.write(new Response(reqtag + 1, 0, "login failed").Serialization());
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
        DataCache db = new DataCache("./db.txt");
        try {
            serverSocket = new ServerSocket(portNumber);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                (new Thread(new Worker(clientSocket, db))).start();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            serverSocket.close();
        }
    }
}
