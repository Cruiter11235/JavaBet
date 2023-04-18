import java.io.ByteArrayOutputStream;

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
    ByteArrayOutputStream res = new ByteArrayOutputStream();
    byte[] msgbytes = msg.getBytes();
    try {
      res.write(new Uint32(len).getByteArray());
      res.write(new Uint32(tag).getByteArray());
      res.write(status);
      for (int i = 0; i < 64 - msgbytes.length; i++) {
        res.write(0x00);
      }
      res.write(msgbytes);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return res.toByteArray();
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
