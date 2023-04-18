import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

//4字节的消息总长度
//4字节的消息类型标识号
//20字节的username
//30字节的password
public class Request {
  static int len = 58;
  int tag;
  String username;
  String password;

  public Request(int tag, String username, String password) {
    this.tag = tag;
    this.username = username;
    this.password = password;
    if (username.getBytes().length > 20 || password.getBytes().length > 30) {
      throw new IllegalArgumentException("长度错误");
    }
  }

  public byte[] Serialization() {
    ByteArrayOutputStream res = new ByteArrayOutputStream(len);
    try {
      res.write(new Uint32(len).getByteArray());
      res.write(new Uint32(tag).getByteArray());
      int usernameLen = username.length();
      int passwordLen = password.length();
      for (int i = 0; i < 20 - usernameLen; i++) {
        res.write(0x00);
      }
      res.write(username.getBytes());
      for (int i = 0; i < 30 - passwordLen; i++) {
        res.write(0x00);
      }
      res.write(password.getBytes());
    } catch (IOException e) {
      e.printStackTrace();
    }
    return res.toByteArray();
  }
  public static void main(String[] args) {
    Request req = new Request(1, "zhuangjinjun", "123456");
    byte[] ia = req.Serialization();
    DataInputStream iais = new DataInputStream(new ByteArrayInputStream(ia));
    // 反序列化读取
    try {
      int a = iais.readInt();
      System.out.println("a:" + a);
      int b = iais.readInt();
      System.out.println("b:" + b);
      StringBuffer username = new StringBuffer();
      StringBuffer password = new StringBuffer();
      for (int i = 0; i < 20; i++) {
        username.append((char) iais.readByte());
      }
      System.out.println("username:" + username);
      for (int i = 0; i < 30; i++) {
        password.append((char) iais.readByte());
      }
      System.out.println("password:" + password);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
