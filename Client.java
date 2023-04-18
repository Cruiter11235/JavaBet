import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
  public static void main(String[] args) throws IOException {
    String hostName = "127.0.0.1";
    int portNumber = 12001;
    Socket clientSocket = null;
    int tag=0;
    String username="";
    String password="";
    try {
      clientSocket = new Socket(hostName, portNumber);
      // 打开sock输入输出流
      Scanner sysin = new Scanner(System.in);
      System.out.println("input method:login or register");
      String method = sysin.nextLine();
      if(method.equals("login")){
        tag=3;
      }else{
        tag=1;
      }
      System.out.println("input username");
      username=sysin.nextLine();
      System.out.println("input password");
      password=sysin.nextLine();
      Request req = new Request(tag, username, password);
      OutputStream writer = clientSocket.getOutputStream();
      // 发送字节流
      writer.write(req.Serialization());
      DataInputStream in = new DataInputStream(clientSocket.getInputStream());
      int len = in.readInt();
      int restag = in.readInt();
      int status = (int) in.readByte();
      StringBuffer msg = new StringBuffer();
      for (int i = 0; i < 64; i++) {
        msg.append((char) in.readByte());
      }
      System.out.println("len:" + len + " tag:" + restag + " status:" + status + " msg:" + msg);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
