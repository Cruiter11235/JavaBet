import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashingExample {

    public static byte[] getSHA256Hash(String input) {
        try {
            // 创建SHA-256信息摘要
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            // 更新信息摘要
            messageDigest.update(input.getBytes());
            // 计算信息摘要的哈希值
            byte[] hash = messageDigest.digest();
            return hash;
        } catch (NoSuchAlgorithmException e) {
            // 处理算法不存在异常
            System.out.println("Algorithm not supported: " + e.getMessage());
            return null;
        }
    }

    public static void main(String[] args) {
        String input = "hello world";
        byte[] hash = getSHA256Hash(input);
        System.out.println("Input string: " + input);
        System.out.println("SHA-256 hash: " + bytesToHex(hash));
    }

    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    // 将字节数组转换为十六进制字符串
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int i = 0; i < bytes.length; i++) {
            int v = bytes[i] & 0xFF;
            hexChars[i * 2] = HEX_ARRAY[v >>> 4];
            hexChars[i * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }
}