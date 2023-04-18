import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

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
    byte[] lenbuffer = ByteBuffer.allocate(4).putInt(len).array();
    byte[] tagbuffer = ByteBuffer.allocate(4).putInt(tag).array();
    byte[] username = ByteBuffer.allocate(20).put(this.username.getBytes()).array();
    byte[] password = ByteBuffer.allocate(30).put(this.password.getBytes()).array();
    ByteBuffer buffer = ByteBuffer.allocate(58);
    buffer.put(lenbuffer);
    buffer.put(tagbuffer);
    buffer.put(username);
    buffer.put(password);
    return buffer.array();
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
