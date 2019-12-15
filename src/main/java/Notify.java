import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;


import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class Notify {
    private String apiToken;
    private String userId;
    private String message;
    private String title;
    private String url;

    private Notify() {}

    public static AnonymousBuilder builderWithApiToken(String token) {
        return new AnonymousBuilder().setApiToken(token);
    }

    public static class AnonymousBuilder {

        private static Notify msg = new Notify();
        private static String PUSH_MESSAGE_URL = "https://api.pushover.net/1/messages.json";
        private static HttpClient httpClient = HttpClients.custom().useSystemProperties().build();

        public AnonymousBuilder setApiToken(String apiToken) {
            msg.apiToken = apiToken;
            return this;
        }

        public AnonymousBuilder setMessage(String message) {
            msg.message = message;
            return this;
        }

        public AnonymousBuilder setUserId(String userId) {
            msg.userId = userId;
            return this;
        }

        public void push() {
            final HttpPost post = new HttpPost(PUSH_MESSAGE_URL);
            final List<NameValuePair> nvps = new ArrayList();

            nvps.add(new BasicNameValuePair("token", msg.getApiToken()));
            nvps.add(new BasicNameValuePair("user", msg.getUserId()));
            nvps.add(new BasicNameValuePair("message", msg.getMessage()));

            if(msg.getTitle() != null) {
                nvps.add(new BasicNameValuePair("title", msg.getTitle()));
            }

            post.setEntity(new UrlEncodedFormEntity(nvps, Charset.defaultCharset()));

            try {
                HttpResponse response = httpClient.execute(post);
                System.out.println(EntityUtils.toString(response.getEntity()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public String getApiToken() {return apiToken; }

    public String getMessage() {return message;}

    public String getUserId() {return userId;}

    public String getTitle() {return title;}
}
