public class RestDataClientTest {
    public static void main(String[] args) {
        testSortsWordsFromData();
        testBuildsSubmitPayload();
        System.out.println("All tests passed");
    }

    private static void testSortsWordsFromData() {
        String json = "{\"requestId\":\"REQ-123\",\"data\":\"banana Apple apple cherry\"}";

        assertEquals("REQ-123", RestDataClient.requestId(json), "requestId");
        assertEquals("Apple apple banana cherry", RestDataClient.sortedWords(json), "sorted words");
    }

    private static void testBuildsSubmitPayload() {
        String payload = RestDataClient.buildSubmitJson("B22DCDT074", "vDWuPkz8", "REQ-123", "apple banana cherry");

        assertEquals(
                "{\"studentCode\":\"B22DCDT074\",\"qCode\":\"vDWuPkz8\",\"requestId\":\"REQ-123\",\"answer\":\"apple banana cherry\"}",
                payload,
                "submit payload");
    }

    private static void assertEquals(Object expected, Object actual, String name) {
        if (!expected.equals(actual)) {
            throw new AssertionError(name + " expected <" + expected + "> but was <" + actual + ">");
        }
    }
}
