package client;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Client {
    private static final int THREAD_TIMEOUT = 1000;

    public static void main(String[] args) throws IOException {
        InetSocketAddress socketAddress = new InetSocketAddress("127.0.0.1",25757);
        final SocketChannel socketChannel = SocketChannel.open();
        //Подключаемся к серверу
        socketChannel.connect(socketAddress);

        try (Scanner scanner = new Scanner(System.in)) {
            //Определяем буфер для получения данных
            final ByteBuffer inputBuffer = ByteBuffer.allocate(2 << 10);

            String message;
            while(true) {
                System.out.println("Enter message for server...");
                message = scanner.nextLine();
                if ("end".equals(message)) break;

                socketChannel.write(ByteBuffer.wrap(message.getBytes(StandardCharsets.UTF_8)));
                //Вместо sleep можно выполнять другую логику
                Thread.sleep(THREAD_TIMEOUT);

                int byteCounts = socketChannel.read(inputBuffer);
                System.out.println(new String(inputBuffer.array(), 0, byteCounts, StandardCharsets.UTF_8).trim());
                inputBuffer.clear();
            }
        } catch (IOException | InterruptedException ex) {
            System.out.println(ex.getMessage());
        }finally {
            socketChannel.close();
        }
    }
}
