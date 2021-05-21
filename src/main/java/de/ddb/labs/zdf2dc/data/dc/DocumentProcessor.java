package de.ddb.labs.zdf2dc.data.dc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import de.ddb.labs.zdf2dc.helper.NamespaceFactory;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.prefs.Preferences;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okio.BufferedSink;
import okio.Okio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Michael Büchner <m.buechner@dnb.de>
 */
public class DocumentProcessor {

    private final static String ID = "SCMS_1aa2b672-b635-4df8-96e6-0ff488a634b0";
    private final static int CONNECTTIMEOUT = 10;
    private final static int WRITETIMEOUT = 10;
    private final static int READTIMEOUT = 30;
    private final static String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    private final static Logger LOG = LoggerFactory.getLogger(DocumentProcessor.class);
    private final static Map<String, String> otherNamespaces;
    private final static XmlMapper xmlMapper;
    private final static Preferences userPrefs = Preferences.userRoot().node("de/ddb/labs/zdf2dc");

    static {
        otherNamespaces = new HashMap<>();
        otherNamespaces.put("xml", "http://www.w3.org/XML/1998/namespace");
        otherNamespaces.put("xsi", "http://www.w3.org/2001/XMLSchema-instance");
        otherNamespaces.put("ex", "http://www.example.org/");
        otherNamespaces.put("bf", "http://id.loc.gov/ontologies/bibframe/#");
        otherNamespaces.put("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
        otherNamespaces.put("marcrel", "http://id.loc.gov/vocabulary/relators/");
        otherNamespaces.put("dcterms", "http://purl.org/dc/terms/");
        otherNamespaces.put("skos", "http://www.w3.org/2004/02/skos/core#");
        otherNamespaces.put("rdfs", "http://www.w3schools.com/RDF/rdf-schema.xml");
        otherNamespaces.put("dcat", "http://www.w3.org/ns/dcat#");
        otherNamespaces.put("foaf", "http://xmlns.com/foaf/0.1/");
        otherNamespaces.put("dc", "http://purl.org/dc/elements/1.1/");
        otherNamespaces.put("edm", "http://www.europeana.eu/schemas/edm/");

        xmlMapper = new XmlMapper(new NamespaceFactory("http://www.openarchives.org/OAI/2.0/", otherNamespaces));
        xmlMapper.enable(SerializationFeature.INDENT_OUTPUT);
        xmlMapper.configure(ToXmlGenerator.Feature.WRITE_XML_DECLARATION, true);
    }

    public static int getIntegerValueFromJsonPath(DocumentContext docCtx, String jsonPath) {
        try {
            return docCtx.read(JsonPath.compile(jsonPath));
        } catch (Exception e) {
            return 0;
        }
    }

    public static String getStringValueFromJsonPath(DocumentContext docCtx, String jsonPath) {
        try {
            final String val = docCtx.read(JsonPath.compile(jsonPath));
            if (!val.trim().isEmpty()) {
                return val;
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
        final OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(CONNECTTIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(WRITETIMEOUT, TimeUnit.SECONDS)
                .readTimeout(READTIMEOUT, TimeUnit.SECONDS)
                .build();

        final Request request = new Request.Builder()
                .url("https://api.zdf.de/content/documents/" + ID + ".json")
                .get()
                .addHeader("Api-Auth", "Bearer " + userPrefs.get("ZDFAPIKEY", ""))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LOG.error("\"{}\" ist keine gültige ID. {}", ID, e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    LOG.error("\"{}\" ist keine gültige ID. Response code: {}.", ID, response.code());
                    return;
                }

                final File saveToJsonFile = File.createTempFile("zdf2dc-", ".json");
                try (final BufferedSink sink = Okio.buffer(Okio.sink(saveToJsonFile))) {
                    sink.writeAll(response.body().source());
                } catch (Exception e) {
                    LOG.error("\"{}\" konnte nicht heruntergeladen werden. {}", ID, e.getMessage());
                }

                final ZdfRecord rec = process(saveToJsonFile);

                try (final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(System.out, Charset.defaultCharset()))) {
                    writer.append(xmlMapper.writeValueAsString(rec));
                    writer.close();
                } catch (Exception e) {
                    LOG.error("\"{}\" konnte nicht als XML serialisiert werden. {}", ID, e.getMessage());
                }

                if (!saveToJsonFile.delete()) {
                    LOG.warn("Konnte temporäre Datei {} nicht löschen.", saveToJsonFile.getAbsolutePath());
                }

            }
        });

        client.dispatcher().executorService().shutdown();
    }

    public static void save(ZdfRecord record, File dst) throws FileNotFoundException, IOException {
        try (final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dst), Charset.defaultCharset()))) {
            writer.append(xmlMapper.writeValueAsString(record));
        }
    }

    public static void save(ZdfRecordList list, File dst) throws FileNotFoundException, IOException {
        try (final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dst), Charset.defaultCharset()))) {
            writer.append(xmlMapper.writeValueAsString(list));
        }
    }

    public static void process(File src, File dst) throws JsonProcessingException, IOException {

        save(process(src), dst);
    }

    public static ZdfRecord process(File src) throws JsonProcessingException, IOException {

        final ZdfRecord film = new ZdfRecord();

        final DocumentContext docCtx = JsonPath.parse(src);

        String val = getStringValueFromJsonPath(docCtx, "$.externalId");
        if (val != null) {
            film.getIdentifier().add(new DcElement(val, "providerItemId", null));
        }

        val = getStringValueFromJsonPath(docCtx, "$.[\"http://zdf.de/rels/uri\"]");
        if (val != null) {
            film.getIdentifier().add(new DcElement(val, "uri", null));
        }

        val = getStringValueFromJsonPath(docCtx, "$.editorialDate");
        if (val != null) {
            try {

                film.setEditorialDate(new SimpleDateFormat(DATE_FORMAT).parse(val));
            } catch (ParseException ex) {
                LOG.warn("Could not parse editorialDate. {}", ex.getMessage());
            }
        }

        val = getStringValueFromJsonPath(docCtx, "$.[\"http://zdf.de/rels/sharing-url\"]");
        if (val != null) {

            film.getIdentifier().add(new DcElement(val, "url", null));
        }

        val = getStringValueFromJsonPath(docCtx, "$.id");
        if (val != null) {

            film.getIdentifier().add(new DcElement(val, "id", null));
        }

        val = getStringValueFromJsonPath(docCtx, "$.title");
        if (val != null) {
            film.getTitle().add(new DcElement(val, null, "deu"));
        }

        val = getStringValueFromJsonPath(docCtx, "$.subtitle");
        if (val != null) {
            film.getTitle().add(new DcElement(val, null, "deu"));
        }

        val = getStringValueFromJsonPath(docCtx, "$.leadParagraph");
        if (val != null) {

            film.getDescription().add(new DcElement(val, "object", "deu"));
        }

        val = getStringValueFromJsonPath(docCtx, "$.teaserImageRef.layouts.original");
        if (val != null && !val.equalsIgnoreCase("zdf")) {

            film.getIdentifier().add(new DcElement(val, "binary", null));
        }

        val = getStringValueFromJsonPath(docCtx, "$.teaserImageRef.title");
        if (val != null && !val.equalsIgnoreCase("zdf")) {

            film.getAlternative().add(new DcTerms(val, null, null, "deu"));
        }

        val = getStringValueFromJsonPath(docCtx, "$.teaserImageRef.copyrightNotice");
        if (val != null && !val.equalsIgnoreCase("zdf")) {

            film.getAlternative().add(new DcTerms(val, null, null, "deu"));
        }

        val = getStringValueFromJsonPath(docCtx, "$.teaserImageRef.source");
        if (val != null && !val.equalsIgnoreCase("zdf")) {

            film.getAlternative().add(new DcTerms(val, null, null, "deu"));
        }

        val = getStringValueFromJsonPath(docCtx, "$.teaserImageRef.caption");
        if (val != null && !val.equalsIgnoreCase("zdf")) {

            film.getAlternative().add(new DcTerms(val, null, null, "deu"));
        }

        val = getStringValueFromJsonPath(docCtx, "$.['http://zdf.de/rels/category']['http://zdf.de/rels/target'].title");
        if (val != null) {

            film.getType().add(new DcElement(val, "object", "deu"));
        }

        val = getStringValueFromJsonPath(docCtx, "$.['http://zdf.de/rels/brand']['http://zdf.de/rels/target'].title");
        if (val != null) {

            film.getSubject().add(new DcElement(val, null, "deu"));
        }

        int iVal = getIntegerValueFromJsonPath(docCtx, "$.['mainVideoContent']['http://zdf.de/rels/target'].duration");
        if (iVal > 0) {
            film.setDuration(iVal);
        }

        val = getStringValueFromJsonPath(docCtx, "$.['mainVideoContent']['http://zdf.de/rels/target'].fsk");
        if (val != null && !val.equals("none")) {
            film.getDescription().add(new DcElement("FSK ab " + val + " freigegeben", "object", "deu"));
        } else {
            film.getDescription().add(new DcElement("FSK ab 0 freigegeben", "object", "deu"));
        }

        val = getStringValueFromJsonPath(docCtx, "$.['mainVideoContent']['http://zdf.de/rels/target'].aspectRatio");
        if (val != null) {
            film.getDescription().add(new DcElement("Seitenverhältnis " + val, "object", "deu"));
        }

        return film;
    }
}
