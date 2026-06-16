import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public final class RestDataClient {
    private static final String DEFAULT_STUDENT_CODE = "B22DCDT074";
    private static final String DEFAULT_Q_CODE = "eTF6h0kP";
    private static final String DEFAULT_EXAM_IP = "36.50.135.242";

    private RestDataClient() {
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        String examIp = args.length > 0 ? args[0] : DEFAULT_EXAM_IP;
        String studentCode = args.length > 1 ? args[1] : DEFAULT_STUDENT_CODE;
        String qCode = args.length > 2 ? args[2] : DEFAULT_Q_CODE;

        HttpClient client = HttpClient.newHttpClient();
        String baseUrl = "http://" + examIp + ":2230";
        URI getUri = URI.create(baseUrl + "/api/rest/data?studentCode="
                + urlEncode(studentCode) + "&qCode=" + urlEncode(qCode));

        HttpRequest getRequest = HttpRequest.newBuilder(getUri)
                .GET()
                .header("Accept", "application/json")
                .build();

        HttpResponse<String> getResponse =
                client.send(getRequest, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        ensureSuccess(getResponse.statusCode(), "GET", getResponse.body());

        DataResponse dataResponse = DataResponse.fromJson(getResponse.body());
        int answer = dataResponse.sum();
        String submitJson = buildSubmitJson(studentCode, qCode, dataResponse.requestId(), answer);

        HttpRequest postRequest = HttpRequest.newBuilder(URI.create(baseUrl + "/api/rest/data/submit"))
                .POST(HttpRequest.BodyPublishers.ofString(submitJson, StandardCharsets.UTF_8))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .build();

        HttpResponse<String> postResponse =
                client.send(postRequest, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        ensureSuccess(postResponse.statusCode(), "POST", postResponse.body());

        System.out.println("requestId: " + dataResponse.requestId());
        System.out.println("data: " + dataResponse.data());
        System.out.println("answer: " + answer);
        System.out.println("submit response: " + postResponse.body());
    }

    public static String buildSubmitJson(String studentCode, String qCode, String requestId, int answer) {
        return "{\"studentCode\":\"" + escapeJson(studentCode)
                + "\",\"qCode\":\"" + escapeJson(qCode)
                + "\",\"requestId\":\"" + escapeJson(requestId)
                + "\",\"answer\":" + answer + "}";
    }

    private static String urlEncode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private static void ensureSuccess(int statusCode, String method, String body) {
        if (statusCode < 200 || statusCode >= 300) {
            throw new IllegalStateException(method + " failed with HTTP " + statusCode + ": " + body);
        }
    }

    private static String escapeJson(String value) {
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"");
    }
}
