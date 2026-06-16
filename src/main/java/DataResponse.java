import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class DataResponse {
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
