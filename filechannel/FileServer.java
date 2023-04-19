import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;

public class FileServer {
    public static void main(String[] args) throws IOException {
        int port = 8080; // 服务器端口号
        int bufferSize = 4096; // 缓冲区大小
        byte[] buffer = new byte[bufferSize]; // 缓冲区

        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("Server started at port " + port);
        
        while (true) {
            Socket socket = serverSocket.accept();
            System.out.println("Accepted connection from " + socket);

            InputStream inputStream = socket.getInputStream();

            // 读取文件名和文件大小
            byte[] fileNameSizeBuffer = new byte[8];
            inputStream.read(fileNameSizeBuffer);
            long fileNameSize = ByteBuffer.wrap(fileNameSizeBuffer).getLong();

            byte[] fileNameBuffer = new byte[(int) fileNameSize];
            inputStream.read(fileNameBuffer);
            String fileName = new String(fileNameBuffer);

            byte[] fileSizeBuffer = new byte[8];
            inputStream.read(fileSizeBuffer);
            long fileSize = ByteBuffer.wrap(fileSizeBuffer).getLong();
            System.out.println("Receiving file: " + fileName + ", size: " + fileSize + " bytes");

            // 创建输出流，将客户端发送的文件保存到磁盘
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream("./serverfile" + fileName));

            // 分块传输文件
            long remainingBytes = fileSize;
            while (remainingBytes > 0) {
                int bytesRead = inputStream.read(buffer, 0, (int) Math.min(bufferSize, remainingBytes));
                if (bytesRead < 0) {
                    break;
                }
                bos.write(buffer, 0, bytesRead);
                remainingBytes -= bytesRead;
            }

            bos.close();
            socket.close();

            System.out.println("File " + fileName + " received and saved to disk");
        }
    }
}
