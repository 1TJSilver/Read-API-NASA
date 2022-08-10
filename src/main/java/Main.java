import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.*;

//https://api.nasa.gov/planetary/apod?api_key=1YdJj6gtAfqv5BZJEioKW3PBOJXDRkXRpDSGN3cj
public class Main {
    public static void main(String[] args) {
        String uri = "https://api.nasa.gov/planetary/apod?api_key=1YdJj6gtAfqv5BZJEioKW3PBOJXDRkXRpDSGN3cj";
        installPage(uri);
    }
    public static void installPage (String uri) {
        String url = null;
        try {
            CloseableHttpClient httpClient = HttpClientBuilder.create()
                    .setDefaultRequestConfig(RequestConfig.custom()
                            .setConnectTimeout(5000)
                            .setSocketTimeout(30000)
                            .setRedirectsEnabled(false)
                            .build())
                    .build();

            HttpGet request = new HttpGet(uri);
            CloseableHttpResponse response = httpClient.execute(request);
            ObjectMapper mapper = new ObjectMapper();
            NasaObject nasaObject = mapper.readValue(
                    response.getEntity().getContent(),
                    new TypeReference<>() {
                    }
            );
            url = nasaObject.getUrl();
            String name = url.substring(38);
            request = new HttpGet(url);
            response = httpClient.execute(request);
            BufferedInputStream reader = new BufferedInputStream(response.getEntity().getContent());
            byte[] page = reader.readAllBytes();
            File picture = new File(name);
            if (picture.createNewFile()){
                System.out.println("Picture was created");
            }
            BufferedOutputStream writer = new BufferedOutputStream(
                    new FileOutputStream(picture));
            writer.write(page);
            reader.close();
            writer.close();
            httpClient.close();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
