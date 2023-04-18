import java.nio.ByteBuffer;

public class testbuffer {
    public static void main(String[] args){
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.putInt(10);
        byte[] bytes = buffer.array();
        for(int i=0;i<bytes.length;i++){
            System.out.print(bytes[i]+" ");
        }
    }
}
