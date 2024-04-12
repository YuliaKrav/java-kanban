package kvclient;

public class KVTaskClientTest {

    public static void main(String[] args) {
        try {
            KVTaskClient client = new KVTaskClient("http://localhost:8078");
            client.put("testKey", "{\"testName\":\"testValue\"}");
            String value = client.load("testKey");
            System.out.println(value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
