package SimpleChat.client;

import SimpleChat.ConsoleHelper;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class BotClient extends Client {
    public class BotSocketThread extends SocketThread {
        @Override
        protected void clientMainLoop() throws IOException, ClassNotFoundException {
            sendTextMessage("Привет чатику. Я бот. Понимаю команды: дата, день, месяц, год, время, час, минуты, секунды.");
            super.clientMainLoop();
        }

        @Override
        protected void processIncomingMessage(String message) {
            ConsoleHelper.writeMessage(message);
            if (!message.contains(": ")) return;
            String sender = message.split(": ")[0];
            String text = message.split(": ")[1];
            String answer = "";
            Date date = Calendar.getInstance().getTime();
            switch (text) {
                case "дата":
                    answer = new SimpleDateFormat("d.MM.YYYY").format(date);
                    break;
                case "день":
                    answer = new SimpleDateFormat("d").format(date);
                    break;
                case "месяц":
                    answer = new SimpleDateFormat("MMMM").format(date);
                    break;
                case "год":
                    answer = new SimpleDateFormat("YYYY").format(date);
                    break;
                case "время":
                    answer = new SimpleDateFormat("H:mm:ss").format(date);
                    break;
                case "час":
                    answer = new SimpleDateFormat("H").format(date);
                    break;
                case "минуты":
                    answer = new SimpleDateFormat("m").format(date);
                    break;
                case "секунды":
                    answer = new SimpleDateFormat("s").format(date);
            }
            if (!answer.equals("")) sendTextMessage(String.format("Информация для %s: %s", sender, answer));
        }
    }

    public static void main(String[] args) {
        BotClient botClient = new BotClient();
        botClient.run();
    }

    @Override
    protected SocketThread getSocketThread() {
        return new BotSocketThread();
    }

    @Override
    protected boolean shouldSendTextFromConsole() {
        return false;
    }

    @Override
    protected String getUserName() {
        return String.format("date_bot_%d", (int) (Math.random() * 100));
    }
}
