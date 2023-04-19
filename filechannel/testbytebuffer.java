import java.nio.ByteBuffer;

public class testbytebuffer {
    public static void main(String[] args){
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.putLong((long)1<<32);
        byte[] bytes = buffer.array();
        for(int i=0;i<bytes.length;i++){
            System.out.print(bytes[i]+" ");
        }
    }
}
