import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Set;

public class FileServerNIO {
    private static final int PORT = 8080;
    private static final int BUFFER_SIZE = 4096;
    private static final String ipAddress = "192.168.1.103";
    public static void main(String[] args) throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(ipAddress,PORT));
        serverSocketChannel.configureBlocking(false);
        System.out.println("Server started at port " + PORT);

        Selector selector = Selector.open();
        // 监听serversocket上的acceptible
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        while (true) {
            // selector上有serversocket的accept,有sokcet的I/O
            int readyChannels = selector.select();
            if (readyChannels == 0) {
                continue;
            }

            // 准备遍历keys
            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

            while (keyIterator.hasNext()) {
                SelectionKey key = keyIterator.next();

                if (key.isAcceptable()) {
                    SocketChannel socketChannel = serverSocketChannel.accept();
                    System.out.println("Accepted connection from " + socketChannel);
                    socketChannel.configureBlocking(false);
                    socketChannel.register(selector, SelectionKey.OP_READ);
                }

                if (key.isReadable()) {
                    SocketChannel socketChannel = (SocketChannel) key.channel();

                    ByteBuffer fileNameSizeBuffer = ByteBuffer.allocate(8);
                    socketChannel.read(fileNameSizeBuffer);
                    fileNameSizeBuffer.flip();
                    long fileNameSize = fileNameSizeBuffer.getLong();

                    ByteBuffer fileNameBuffer = ByteBuffer.allocate((int) fileNameSize);
                    socketChannel.read(fileNameBuffer);
                    fileNameBuffer.flip();
                    String fileName = new String(fileNameBuffer.array());

                    ByteBuffer fileSizeBuffer = ByteBuffer.allocate(8);
                    socketChannel.read(fileSizeBuffer);
                    fileSizeBuffer.flip();
                    long fileSize = fileSizeBuffer.getLong();
                    System.out.println("Receiving file: " + fileName + ", size: " + fileSize + " bytes");

                    Path filePath = Paths.get("./" + fileName);
                    ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
                    long remainingBytes = fileSize;

                    while (remainingBytes > 0) {
                        int bytesRead = socketChannel.read(buffer);
                        if (bytesRead == -1) {
                            break;
                        }
                        buffer.flip();
                        byte[] byteToWrite = new byte[bytesRead];
                        buffer.get(byteToWrite);
                        Files.write(filePath, byteToWrite, java.nio.file.StandardOpenOption.CREATE,
                                java.nio.file.StandardOpenOption.APPEND);
                        remainingBytes -= bytesRead;
                        buffer.clear();
                    }

                    socketChannel.close();
                    System.out.println("File " + fileName + " received and saved to disk");
                }

                keyIterator.remove();
            }
        }
    }
}
