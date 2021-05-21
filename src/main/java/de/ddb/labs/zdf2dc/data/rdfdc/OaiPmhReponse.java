/*
 * Copyright 2019-2021 Michael Büchner <m.buechner@dnb.de>, Deutsche Digitale Bibliothek
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.ddb.labs.zdf2dc.data.rdfdc;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author Michael Büchner <m.buechner@dnb.de>
 */
@JacksonXmlRootElement(localName = "OAI-PMH", namespace = "http://www.openarchives.org/OAI/2.0/")
public class OaiPmhReponse {

    @Getter
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    @JacksonXmlProperty(localName = "responseDate", namespace = "http://www.openarchives.org/OAI/2.0/")
    private final Date responseDate;

    @Getter
    @Setter
    @JacksonXmlProperty(localName = "request", namespace = "http://www.openarchives.org/OAI/2.0/")
    private Request request = new Request();

    @Getter
    @Setter
    @JacksonXmlProperty(localName = "ListRecords", namespace = "http://www.openarchives.org/OAI/2.0/")
    private ListRecords listRecords = new ListRecords();

    public OaiPmhReponse() {
        this.responseDate = new Date();
    }

    public void clear() {
        this.listRecords.getList().clear();
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class Request {

        @Getter
        @Setter
        @JacksonXmlProperty(localName = "verb", isAttribute = true)
        private String verb = "ListRecords";

        @Getter
        @Setter
        @JacksonXmlProperty(localName = "metadataPrefix", isAttribute = true)
        private String metadataPrefix = "dc-ddb";

        @Setter
        @Getter
        @JacksonXmlText
        private String elementValue = "https://api.zdf.de";
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class ListRecords {

        @Getter
        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "record", namespace = "http://www.openarchives.org/OAI/2.0/")
        private final List<Record> list = new ArrayList<>();

        public void addRecord(ZdfRdfRecord rec) {
            final Record newRecord = new Record();
            newRecord.getHeader().setDatestamp(rec.getDescription().getCreated());
            newRecord.getHeader().setIdentifier(rec.getDescription().getIsReferencedBy().getCatalogRecord().getIdentifier());

            // if is empty
            if (newRecord.getHeader().getIdentifier() == null || newRecord.getHeader().getIdentifier().trim().isEmpty()) {
                newRecord.getHeader().setIdentifier(UUID.randomUUID().toString());
            }

            if (newRecord.getHeader().getDatestamp() == null) {
                newRecord.getHeader().setDatestamp(new Date());
            }

            newRecord.getMetadata().setRdf(rec);
            list.add(newRecord);
        }

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        public static class Record {

            @Getter
            @Setter
            @JacksonXmlProperty(localName = "header", namespace = "http://www.openarchives.org/OAI/2.0/")
            private Header header = new Header();

            @Getter
            @Setter
            @JacksonXmlProperty(localName = "metadata", namespace = "http://www.openarchives.org/OAI/2.0/")
            private Metadata metadata = new Metadata();

            public static class Header {

                @Setter
                @Getter
                @JacksonXmlProperty(localName = "identifier", namespace = "http://www.openarchives.org/OAI/2.0/")
                private String identifier;

                @Setter
                @Getter
                @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
                @JacksonXmlProperty(localName = "datestamp", namespace = "http://www.openarchives.org/OAI/2.0/")
                private Date datestamp;
            }

            public static class Metadata {

                @Setter
                @Getter
                @JacksonXmlProperty(localName = "RDF", namespace = "http://www.w3.org/1999/02/22-rdf-syntax-ns#")
                private ZdfRdfRecord rdf;
            }
        }
    }
}
