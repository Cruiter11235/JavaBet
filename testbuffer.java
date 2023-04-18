import java.nio.ByteBuffer;

public class testbuffer {
    public static void main(String[] args){
        ByteBuffer buffer = ByteBuffer.allocate(8);
        String s = "test";
        buffer.put(s.getBytes());
        byte[] bytes = buffer.array();
        for(int i=0;i<bytes.length;i++){
            System.out.print(bytes[i]+" ");
        }
    }
}
