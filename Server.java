package ChatApp;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class Server {
    // user message data type
    public static class Message {
        public String name;
        public String content;

        public Message(String username, String messageContent)
        {
            name = username;
            content = messageContent;
        }
    }

    // list of all user messages in the chatroom
    public static List<Message> messages = new ArrayList<Message>();

    // borrowed from https://stackoverflow.com/questions/11640025/how-to-obtain-the-query-string-in-a-get-with-java-httpserver-httpexchange
    public static Map<String, String> queryToMap(String query) {
        if(query == null) {
            return null;
        }
        Map<String, String> result = new HashMap<>();
        for (String param : query.split("&")) {
            String[] entry = param.split("=");
            if (entry.length > 1) {
                result.put(entry[0], entry[1]);
            } else {
                result.put(entry[0], "");
            }
        }
        return result;
    }

    public static void main(String[] args) throws Exception {
        // set up http server on port 8000
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/update", new updateHandler());
        server.setExecutor(null);
        server.start();
    }

    static class updateHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            Map<String, String> params = queryToMap(t.getRequestURI().getQuery());
            String name = URLDecoder.decode(params.get("name"), StandardCharsets.UTF_8);
            String message = URLDecoder.decode(params.get("message"), StandardCharsets.UTF_8);

            if (message.trim() != "")
            {
                name = name.replace('_', '*');
                name = name.replace('~', '*');
                Message msg = new Message(name, message);
                messages.add(msg);

                System.out.println("[" + name + "] " + message);
            }

            String response = "";
            int numMessages = messages.size();
            int count = numMessages < 10 ? numMessages : 10;
            for (int i = count; i > 0; i--)
            {
                int messageIndex = numMessages - i;
                Message msg = messages.get(messageIndex);
                response += msg.name + "_" + msg.content + "~";
            }

            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
}
