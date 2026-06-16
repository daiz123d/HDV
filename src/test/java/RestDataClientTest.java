public class RestDataClientTest {
    public static void main(String[] args) {
        testDataTask();
        testCharacterTask();
        System.out.println("All tests passed");
    }

    private static void testDataTask() {
        String json = "{\"requestId\":\"REQ-DATA\",\"data\":[1,2,3,4,-5]}";

        assertEquals("REQ-DATA", DataRestClient.requestId(json), "data requestId");
        assertEquals(5, DataRestClient.sumData(json), "data sum");
        assertEquals(
                "{\"studentCode\":\"B22DCDT074\",\"qCode\":\"eTF6h0kP\",\"requestId\":\"REQ-DATA\",\"answer\":5}",
                DataRestClient.buildSubmitJson("B22DCDT074", "eTF6h0kP", "REQ-DATA", 5),
                "data payload");
    }

    private static void testCharacterTask() {
        String json = "{\"requestId\":\"REQ-CHAR\",\"data\":\"banana Apple apple cherry\"}";

        assertEquals("REQ-CHAR", CharacterRestClient.requestId(json), "character requestId");
        assertEquals("Apple apple banana cherry", CharacterRestClient.sortedWords(json), "sorted words");
        assertEquals(
                "{\"studentCode\":\"B22DCDT074\",\"qCode\":\"vDWuPkz8\",\"requestId\":\"REQ-CHAR\",\"answer\":\"apple banana cherry\"}",
                CharacterRestClient.buildSubmitJson("B22DCDT074", "vDWuPkz8", "REQ-CHAR", "apple banana cherry"),
                "character payload");
    }

    private static void assertEquals(Object expected, Object actual, String name) {
        if (!expected.equals(actual)) {
            throw new AssertionError(name + " expected <" + expected + "> but was <" + actual + ">");
        }
    }
}
