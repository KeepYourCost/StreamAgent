package infrastructure.http;

import config.AppConfig;
import infrastructure.singleton.Singleton;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Singleton
public class Fetcher {
    public static final String BASE_URL = AppConfig.getApplicationProperties().getProperty("MASTER_SERVER_URL");
    private final static Logger LOGGER = LoggerFactory.getLogger(Fetcher.class);

    private HttpMethod method;
    private URL url = null;
    private MimeType mimeType;
    private String data;
    private final Map<String, String> headers = new HashMap<>();

    private Fetcher() {}

    public static Fetcher method(String method) {
        Fetcher fetcher = new Fetcher();
        fetcher.method = HttpMethod.valueOf(method);
        return fetcher;
    }

    public Fetcher url(String url) {
        try {
            URL validUrl = new URL(url);
            this.url = validUrl;
        } catch (MalformedURLException ex) {
            throw new IllegalArgumentException("Invalid URL: " + url, ex);
        }
        return this;
    }

    public Fetcher path(String path) {
        try {
            if (this.url != null) {
                this.url = new URL(this.url + path);
            } else {
                this.url = new URL(BASE_URL + path);
            }
        } catch (MalformedURLException ex) {
            throw new IllegalArgumentException("Invalid URL: " + path, ex);
        }
        return this;
    }

    public Fetcher mime(String mime) {
        try {
            this.mimeType = MimeType.valueOf(mime);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid MIME type: " + mime, ex);
        }
        return this;
    }

    public Fetcher header(Map<String, String> headers) {
        if (headers != null) {
            this.headers.putAll(headers);
        }
        return this;
    }

    public Fetcher body(Object obj) {
        this.data = obj.toString();
        // JSON 형식으로 바꿀 수 있도록 올바르게 구성해야함.
        // 우선은 구현하지 않음
        return this;
    }

    public Fetcher query(Map<String, String> params) {
        try {
            String queryString = params.entrySet().stream()
                    .map(entry -> URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8) + "=" + URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8))
                    .collect(Collectors.joining("&"));
            String newUrl = this.url.toString() + "?" + queryString;
            this.url = new URL(newUrl);
        } catch (Exception ex) {
            throw new IllegalArgumentException("Failed to encode query parameters", ex);
        }
        return this;
    }

    public String send() throws Exception {
        if (this.url == null) {
            throw new IllegalStateException("Not setup URL");
        }
        HttpURLConnection connection = (HttpURLConnection) this.url.openConnection();
        connection.setRequestMethod(this.method.name());

        for (Map.Entry<String, String> entry : this.headers.entrySet()) {
            connection.setRequestProperty(entry.getKey(), entry.getValue());
        }

        if (this.mimeType != null) {
            connection.setRequestProperty("Content-Type", this.mimeType.getContentType());
        }

        if (this.method == HttpMethod.POST || this.method == HttpMethod.PATCH || this.method == HttpMethod.PUT) {
            connection.setDoOutput(true);
            if (this.data != null) {
                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = this.data.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }
            }
        }

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            String contentType = connection.getHeaderField("Content-Type");
            if (contentType != null && contentType.contains("application/json")) {
                return parseJsonResponse(connection);
            } else {
                return new String(connection.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            }
        } else {
            return "Error: " + responseCode;
        }
    }

    private Map<String, Object> parseJsonResponse(HttpURLConnection connection) throws Exception {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            JSONObject json = new JSONObject(response.toString());
            return jsonToMap(json);
        }
    }

    private Map<String, Object> jsonToMap(JSONObject json) {
        Map<String, Object> map = new HashMap<>();

        for (String key : json.keySet()) {
            Object value = json.get(key);
            if (value instanceof JSONObject) {
                map.put(key, jsonToMap((JSONObject) value));
            } else if (value instanceof JSONArray) {
                map.put(key, jsonArrayToList((JSONArray) value));
            } else {
                map.put(key, value);
            }
        }

        return map;
    }

    private Object jsonArrayToList(JSONArray array) {
        if (array.length() == 0) {
            return new HashMap<>();
        }
        if (array.get(0) instanceof JSONObject) {
            Map<Integer, Map<String, Object>> list = new HashMap<>();
            for (int i = 0; i < array.length(); i++) {
                list.put(i, jsonToMap(array.getJSONObject(i)));
            }
            return list;
        } else {
            return array.toList(); // Primitive array
        }
    }
}

enum HttpMethod {
    POST,
    GET,
    PUT,
    PATCH,
    DELETE,
}

enum MimeType {
    TEXT("text/plain"),
    JSON("application/json");

    private final String contentType;

    MimeType(String contentType) {
        this.contentType = contentType;
    }

    public String getContentType() {
        return contentType;
    }
}
