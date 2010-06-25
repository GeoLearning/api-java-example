package api.awe;

import api.Csv;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.multipart.MultiPart;
import com.sun.jersey.multipart.file.FileDataBodyPart;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.Map;

public class PostExample {
    private static final Logger LOG = Logger.getLogger(PostExample.class);
    private static final String NL = System.getProperty("line.separator");
    private static final String BASE_URL = "https://services.geolearning.com/api";
    private static final String DOMAIN = "your domain";
    private static final String USERNAME = "your username";
    private static final String PASSWORD = "your password";
    
    private static final String AWE_FILE = "" +
            "request_id, attribute_name, attribute_value" + NL +
            "5001      , eye color     , brown" + NL +
            "5001      , hair color    , blonde" + NL +
            "5001      , unknown name  , hazel" + NL;

    public static void main(String[] args) throws Exception {
        WebResource resource = getWebResource();

        // A file can be posted
        postFileTo(resource);

        // or the contents of the file can be posted
        postContentsTo(resource);
    }

    private static void postFileTo(WebResource resource) throws Exception {
        LOG.info("posting file...");
        File file = writeContentsToFile();

        MultiPart multiPart = new MultiPart();
        multiPart.bodyPart(new FileDataBodyPart("filedata", file));

        String response = resource.type("multipart/form-data").post(String.class, multiPart);
        displayResults(response);
    }

    private static void postContentsTo(WebResource resource) throws Exception {
        LOG.info("posting file contents...");
        String response = resource.type("text/csv").post(String.class, AWE_FILE);
        displayResults(response);
    }

    private static WebResource getWebResource() {
        Client client = Client.create(new DefaultClientConfig());
        client.addFilter(new HTTPBasicAuthFilter(USERNAME, PASSWORD));
        WebResource resource = client.resource(BASE_URL).path(DOMAIN).path("awe");
        return resource;
    }

    private static void displayResults(String response) {
        Csv csv = new Csv(new ByteArrayInputStream(response.getBytes()));

        for(int i = 0; i < csv.size(); i++) {
            Map<String, String> row = csv.row(i);
            String message = String.format("Row %s results: status code = %s, message = %s", i + 1, row.get("status"), row.get("message"));
            LOG.info(message);
        }

        LOG.info("---");
    }

    private static File writeContentsToFile() throws IOException {
        File file = File.createTempFile("awe", ".csv");
        file.deleteOnExit();

        PrintWriter writer = new PrintWriter(new FileOutputStream(file));
        writer.write(AWE_FILE);
        writer.close();
        return file;
    }
}
