package ChatApp;

import java.net.URI;
import java.net.URLEncoder;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;

public class Client {
    // URL of server must be added here
    String url = "";

    public static void main(String[] args) throws Exception {
        Scanner input = new Scanner(System.in);

        System.out.print("\033[H\033[2J");
        System.out.flush();

        System.out.print("Enter your name: ");
        String name = input.nextLine();

        HttpClient client = HttpClient.newHttpClient();

        String lastMsg = "";
        while (true)
        {
            System.out.print("\033[H\033[2J");
            System.out.flush();

            HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url + "/update?name=" + URLEncoder.encode(name, StandardCharsets.UTF_8) + "&message=" + URLEncoder.encode(lastMsg, StandardCharsets.UTF_8)))
            .build();

            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
            String body = response.body();

            String[] messages = body.split(Pattern.quote("~"));

            for (int i = 0; i < messages.length; i++)
            {
                if (messages[0].trim().length() == 0) continue;
                String[] parts = messages[i].split(Pattern.quote("_"));
                String sender = parts[0];
                String content = parts[1];
                System.out.println("[" + sender + "] " + content);
            }

            System.out.println();

            System.out.print("Enter message to send or leave blank to refresh: ");
            lastMsg = input.nextLine();
        }
    }
}
