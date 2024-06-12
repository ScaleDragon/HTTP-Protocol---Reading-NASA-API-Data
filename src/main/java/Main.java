import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {
    public static final String REMOTE_SERVICE_URI =
            "https://api.nasa.gov/planetary/apod?api_key=VJPq7NAR1msIwkMhOAq2B5PW55rbIdh9yHiusc3B";
    public static ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) throws IOException {

        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(5000)
                        .setSocketTimeout(3000)
                        .setRedirectsEnabled(false)
                        .build())
                .build();

        HttpGet request = new HttpGet(REMOTE_SERVICE_URI);
        CloseableHttpResponse response = httpClient.execute(request);

        NasaImage nasaImage = mapper.readValue(response.getEntity()
                .getContent(), new TypeReference<>() {
        });

        HttpGet urlRequest2 = new HttpGet(nasaImage.getHdurl());
        CloseableHttpResponse response2 = httpClient.execute(urlRequest2);

        String nameFile = Stream.of(nasaImage.getHdurl().split("/"))
                .filter(value -> value.contains("jpg")).collect(Collectors.joining());

        try (InputStream in = response2.getEntity().getContent();
             FileOutputStream fileOutputStream = new FileOutputStream(nameFile)) {
            byte[] dataBuffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }
        } catch (IOException e) {

        }
    }
}
