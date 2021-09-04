package server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class Server {
    public static void main(String[] args) throws IOException {
        final ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.bind(new InetSocketAddress("localhost", 25757));

        while (true) {
            //Ждем подключения клиента
            try (SocketChannel socketChannel = serverChannel.accept()) {
                //Определяем буффер для получения данных
                final ByteBuffer inputBuffer = ByteBuffer.allocate(2 << 10);
                while (socketChannel.isConnected()) {
                    System.out.println("New Connection accepted");
                    //Читаем данные их канала в буфер
                    int bytesCount = socketChannel.read(inputBuffer);
                    //Прерываем работу с клиентом если из потока читать нельзя
                    if (bytesCount == -1 ) break;
                    //Получаем от клиента строку в нужной кодировке и очищаем буфер
                    final String message = new String(inputBuffer.array(), 0, bytesCount, StandardCharsets.UTF_8);
                    inputBuffer.clear();
                    System.out.println("Получено сообщение от клиента " + message);
                    //Удаляем из сообщения все пробелы
                    String clearAllGapsMsg = message.replaceAll(" ", "");

                    //Отправляем эхо клиенту
                    socketChannel.write(ByteBuffer.wrap(("Эхо: " + clearAllGapsMsg).getBytes(StandardCharsets.UTF_8)));
                }
            }catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
}
