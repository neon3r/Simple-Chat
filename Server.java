package SimpleChat;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Server {

    private static class Handler extends Thread {
        private Socket socket;
        public Handler(Socket socket) {
            this.socket = socket;
        }

        private String serverHandshake (Connection connection) throws IOException, ClassNotFoundException {
            connection.send(new Message(MessageType.NAME_REQUEST, "Введите имя пользователя"));
            Message answer = connection.receive();
            while (answer.getType() != MessageType.USER_NAME || answer.getData().isEmpty() || answer.getData().equals("") || connectionMap.containsKey(answer.getData())) {
                connection.send(new Message(MessageType.NAME_REQUEST, "Введите имя пользователя"));
                answer = connection.receive();
            }
            connectionMap.put(answer.getData(), connection);
            connection.send(new Message(MessageType.NAME_ACCEPTED));
            return answer.getData();
        }

        private void notifyUsers (Connection connection, String userName) throws IOException {
            for (String key : connectionMap.keySet()) {
                if (!key.equals(userName)) {
                    connection.send(new Message(MessageType.USER_ADDED, key));
                }
            }
        }

        private void serverMainLoop (Connection connection, String userName) throws IOException, ClassNotFoundException {
            while (true) {
                Message input = connection.receive();
                if (input.getType() == MessageType.TEXT) {
                    Message res = new Message(MessageType.TEXT, userName + ": " + input.getData());
                    sendBroadcastMessage(res);
                } else {
                    ConsoleHelper.writeMessage("Ошибка");
                }
            }
        }

        public void run() {
            ConsoleHelper.writeMessage(String.format("Установлено соединение с %s", socket.getRemoteSocketAddress()));
            try (Connection connection= new Connection(socket)){
                String userName = serverHandshake(connection);
                sendBroadcastMessage(new Message(MessageType.USER_ADDED, userName));
                notifyUsers(connection, userName);
                serverMainLoop(connection, userName);
                connectionMap.remove(userName);
                sendBroadcastMessage(new Message(MessageType.USER_REMOVED, userName));
            } catch (IOException | ClassNotFoundException e) {
                ConsoleHelper.writeMessage("Произошла ошибка при обмене данными с удаленным адресом");
            }
            ConsoleHelper.writeMessage("Соединение с удаленным адресом закрыто");
        }
    }

    private static Map<String, Connection> connectionMap = new ConcurrentHashMap<>();

    public static void sendBroadcastMessage (Message message) {
        for (Connection connection : connectionMap.values()) {
            try {
                connection.send(message);
            } catch (IOException ex) {
                System.out.println("Сообщение не было отправлено");
            }
        }
    }

    public static void main(String[] args) throws IOException {
        System.out.println("Введите порт");
        int port = ConsoleHelper.readInt();
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Сервер запущен");
            while (true) {
                Socket socket = serverSocket.accept();
                Handler handler = new Handler(socket);
                handler.start();
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
}
