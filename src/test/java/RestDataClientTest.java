public class RestDataClientTest {
    public static void main(String[] args) {
        testParsesResponseAndSumsData();
        testBuildsSubmitPayload();
        System.out.println("All tests passed");
    }

    private static void testParsesResponseAndSumsData() {
        String json = "{\"requestId\":\"REQ-123\",\"data\":[1,2,3,4,-5]}";

        assertEquals("REQ-123", RestDataClient.requestId(json), "requestId");
        assertEquals(5, RestDataClient.sumData(json), "sum");
    }

    private static void testBuildsSubmitPayload() {
        String payload = RestDataClient.buildSubmitJson("B22DCDT074", "eTF6h0kP", "REQ-123", 10);

        assertEquals(
                "{\"studentCode\":\"B22DCDT074\",\"qCode\":\"eTF6h0kP\",\"requestId\":\"REQ-123\",\"answer\":10}",
                payload,
                "submit payload");
    }

    private static void assertEquals(Object expected, Object actual, String name) {
        if (!expected.equals(actual)) {
            throw new AssertionError(name + " expected <" + expected + "> but was <" + actual + ">");
        }
    }
}
