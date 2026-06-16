import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HeaderRestClient {
    static final String STUDENT = "B22DCDT074";
    static final String Q_CODE = "1UinnZoS";
    static final String EXAM_IP = "36.50.135.242";

    public static void main(String[] args) throws Exception {
        String ip = args.length > 0 ? args[0] : EXAM_IP;
        String student = args.length > 1 ? args[1] : STUDENT;
        String qCode = args.length > 2 ? args[2] : Q_CODE;
        String base = "http://" + ip + ":2230";
        HttpClient client = HttpClient.newHttpClient();

        String json = send(client, HttpRequest.newBuilder(URI.create(base
                + "/api/rest/header?studentCode=" + enc(student)
                + "&qCode=" + enc(qCode))).GET().build());

        String requestId = match(json, "\"requestId\"\\s*:\\s*\"([^\"]+)\"");
        String nonce = match(json, "\"nonce\"\\s*:\\s*\"([^\"]+)\"");
        String key = match(json, "\"signingKey\"\\s*:\\s*\"([^\"]+)\"");
        String events = match(json, "\"events\"\\s*:\\s*\\[(.*?)]").replace("\"", "").replace(",", "|");
        String signature = hmac(nonce + ":" + events + ":" + student.toUpperCase(), key);
        String body = "{\"studentCode\":\"" + student + "\",\"qCode\":\"" + qCode
                + "\",\"requestId\":\"" + requestId + "\"}";

        String result = send(client, HttpRequest.newBuilder(URI.create(base + "/api/rest/header/submit"))
                .header("Content-Type", "application/json")
                .header("X-Signature", signature)
                .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                .build());

        System.out.println("requestId: " + requestId);
        System.out.println("signature: " + signature);
        System.out.println("submit response: " + result);
    }

    static String hmac(String data, String key) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        StringBuilder hex = new StringBuilder();
        for (byte b : mac.doFinal(data.getBytes(StandardCharsets.UTF_8))) {
            hex.append(String.format("%02x", b));
        }
        return hex.toString();
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
