import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MethodRestClient {
    static final String STUDENT = "B22DCDT074";
    static final String Q_CODE = "W1DcSOoj";
    static final String EXAM_IP = "36.50.135.242";

    public static void main(String[] args) throws Exception {
        String ip = args.length > 0 ? args[0] : EXAM_IP;
        String student = args.length > 1 ? args[1] : STUDENT;
        String qCode = args.length > 2 ? args[2] : Q_CODE;
        String base = "http://" + ip + ":2230";
        HttpClient client = HttpClient.newHttpClient();

        String json = send(client, HttpRequest.newBuilder(URI.create(base
                + "/api/rest/method?studentCode=" + enc(student)
                + "&qCode=" + enc(qCode))).GET().build());

        String requestId = match(json, "\"requestId\"\\s*:\\s*\"([^\"]+)\"");
        String body = "{\"studentCode\":\"" + student + "\",\"qCode\":\"" + qCode
                + "\",\"answer\":{\"status\":\"done\"}}";

        String result = send(client, HttpRequest.newBuilder(URI.create(base + "/api/rest/method/" + enc(requestId)))
                .header("Content-Type", "application/json")
                .method("PUT", HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                .build());

        System.out.println("requestId: " + requestId);
        System.out.println("submit response: " + result);
    }

    static String send(HttpClient client, HttpRequest request) throws Exception {
        HttpResponse<String> res = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        if (res.statusCode() / 100 != 2) throw new RuntimeException(res.statusCode() + ": " + res.body());
        return res.body();
    }

    static String match(String text, String regex) {
        Matcher m = Pattern.compile(regex, Pattern.DOTALL).matcher(text);
        if (!m.find()) throw new IllegalArgumentException("Invalid response: " + text);
        return m.group(1);
    }

    static String enc(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }
}
