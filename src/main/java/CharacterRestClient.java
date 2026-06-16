import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CharacterRestClient {
    static final String STUDENT = "B22DCDT074";
    static final String Q_CODE = "vDWuPkz8";
    static final String EXAM_IP = "36.50.135.242";

    public static void main(String[] args) throws Exception {
        String ip = args.length > 0 ? args[0] : EXAM_IP;
        String student = args.length > 1 ? args[1] : STUDENT;
        String qCode = args.length > 2 ? args[2] : Q_CODE;
        String base = "http://" + ip + ":2230";
        String json = get(base + "/api/rest/character?studentCode=" + enc(student) + "&qCode=" + enc(qCode));

        String requestId = requestId(json);
        String answer = sortedWords(json);
        String body = buildSubmitJson(student, qCode, requestId, answer);

        String result = post(base + "/api/rest/character/submit", body);

        System.out.println("requestId: " + requestId);
        System.out.println("answer: " + answer);
        System.out.println("submit response: " + result);
    }

    static String requestId(String json) {
        return match(json, "\"requestId\"\\s*:\\s*\"([^\"]+)\"");
    }

    static String sortedWords(String json) {
        String[] words = match(json, "\"data\"\\s*:\\s*\"([^\"]*)\"").trim().split("\\s+");
        Arrays.sort(words);
        return String.join(" ", words);
    }

    static String buildSubmitJson(String student, String qCode, String requestId, String answer) {
        return "{\"studentCode\":\"" + student + "\",\"qCode\":\"" + qCode
                + "\",\"requestId\":\"" + requestId + "\",\"answer\":\"" + escape(answer) + "\"}";
    }

    static String get(String url) throws Exception {
        return send(HttpRequest.newBuilder(URI.create(url)).GET().build());
    }

    static String post(String url, String body) throws Exception {
        return send(HttpRequest.newBuilder(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                .build());
    }

    static String send(HttpRequest request) throws Exception {
        HttpResponse<String> res = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
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

    static String escape(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
