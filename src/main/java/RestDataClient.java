import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public static final class DataResponse {
        private static final Pattern REQUEST_ID_PATTERN =
                Pattern.compile("\"requestId\"\\s*:\\s*\"([^\"]*)\"");
        private static final Pattern DATA_PATTERN =
                Pattern.compile("\"data\"\\s*:\\s*\\[(.*?)\\]", Pattern.DOTALL);
        private static final Pattern INTEGER_PATTERN =
                Pattern.compile("-?\\d+");

        private final String requestId;
        private final List<Integer> data;

        private DataResponse(String requestId, List<Integer> data) {
            this.requestId = requestId;
            this.data = Collections.unmodifiableList(new ArrayList<>(data));
        }

        public static DataResponse fromJson(String json) {
            Matcher requestMatcher = REQUEST_ID_PATTERN.matcher(json);
            if (!requestMatcher.find()) {
                throw new IllegalArgumentException("Response JSON does not contain requestId");
            }

            Matcher dataMatcher = DATA_PATTERN.matcher(json);
            if (!dataMatcher.find()) {
                throw new IllegalArgumentException("Response JSON does not contain data array");
            }

            List<Integer> numbers = new ArrayList<>();
            Matcher integerMatcher = INTEGER_PATTERN.matcher(dataMatcher.group(1));
            while (integerMatcher.find()) {
                numbers.add(Integer.parseInt(integerMatcher.group()));
            }

            return new DataResponse(requestMatcher.group(1), numbers);
        }

        public String requestId() {
            return requestId;
        }

        public List<Integer> data() {
            return data;
        }

        public int sum() {
            int total = 0;
            for (int value : data) {
                total += value;
            }
            return total;
        }
    }
}
