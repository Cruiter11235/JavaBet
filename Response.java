import java.io.ByteArrayOutputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;

//4字节长度
//4字节tag
//1字节的status
//64字节的描述
public class Response {
  static int len = 73;
  int tag;
  int status;
  String msg;

  public Response(int tag, int status, String msg) {
    this.tag = tag;
    this.status = status;
    this.msg = msg;
  }

  public byte[] Serialization() {
    byte[] lenbuffer = ByteBuffer.allocate(4).putInt(len).array();
    byte[] tagbuffer = ByteBuffer.allocate(4).putInt(tag).array();
    byte[] statusbuffer = ByteBuffer.allocate(1).put((byte)status).array();
    byte[] desc = ByteBuffer.allocate(64).put(msg.getBytes()).array();
    ByteBuffer buffer = ByteBuffer.allocate(73);
    buffer.put(lenbuffer);
    buffer.put(tagbuffer);
    buffer.put(statusbuffer);
    buffer.put(desc);
    return buffer.array();
  }

  public static void main(String[] args) {
    Response r = new Response(2, 1, "wadawd");
    byte[] b = r.Serialization();
    for (int i = 0; i < b.length; i++) {
      System.out.print(b[i] + " ");
    }
    System.out.println();
    // 反序列化
  }
}
