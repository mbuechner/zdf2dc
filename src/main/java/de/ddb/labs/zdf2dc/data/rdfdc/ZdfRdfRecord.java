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
import de.ddb.labs.zdf2dc.data.ZDF2DcList;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import lombok.Getter;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JacksonXmlRootElement(namespace = "http://www.w3.org/1999/02/22-rdf-syntax-ns#", localName = "RDF")
public class ZdfRdfRecord {

    @Getter
    @Setter
    @JacksonXmlProperty(localName = "Description", namespace = "http://www.w3.org/1999/02/22-rdf-syntax-ns#")
    private Description description = new Description();

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    protected static class Description {

        @Getter
        @Setter
        @JacksonXmlProperty(localName = "isReferencedBy", namespace = "http://purl.org/dc/terms/")
        private IsReferencedBy isReferencedBy = new IsReferencedBy();

        @Getter
        @Setter
        @JacksonXmlProperty(localName = "about", namespace = "http://www.w3.org/1999/02/22-rdf-syntax-ns#", isAttribute = true)
        private String about;

        @Getter
        @Setter
        @JacksonXmlProperty(localName = "alternative", namespace = "http://purl.org/dc/terms/")
        @JacksonXmlElementWrapper(useWrapping = false)
        private ZDF2DcList<ElementWithAttributes> alternative = new ZDF2DcList<>();

        @Getter
        @Setter
        @JacksonXmlProperty(localName = "description", namespace = "http://purl.org/dc/elements/1.1/")
        @JacksonXmlElementWrapper(useWrapping = false)
        private List<ElementWithAttributes> description = new ArrayList<>();

        @Setter
        @JacksonXmlProperty(localName = "extent", namespace = "http://purl.org/dc/terms/")
        private Integer extent;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        @JacksonXmlProperty(localName = "created", namespace = "http://purl.org/dc/terms/")
        private Date created;

        @Getter
        @Setter
        @JacksonXmlProperty(localName = "format", namespace = "http://purl.org/dc/elements/1.1/")
        private String format = "video/vnd.ddbkultur.zdf";

        @Getter
        @Setter
        @JacksonXmlProperty(localName = "isShownAt", namespace = "http://www.europeana.eu/schemas/edm/")
        private ElementWithAttributes isShownAt;

        @Getter
        @Setter
        @JacksonXmlProperty(localName = "identifier", namespace = "http://purl.org/dc/elements/1.1/")
        private DcIdentifier identifier = new DcIdentifier();

        @Getter
        @Setter
        @JacksonXmlProperty(localName = "object", namespace = "http://www.europeana.eu/schemas/edm/")
        private ElementWithAttributes object;

        @Getter
        @Setter
        @JacksonXmlProperty(localName = "language", namespace = "http://purl.org/dc/elements/1.1/")
        private String language = "de";

        @Getter
        @Setter
        @JacksonXmlProperty(localName = "licence", namespace = "http://purl.org/dc/terms/")
        private ElementWithAttributes licence = new ElementWithAttributes(null, null, "https://www.deutsche-digitale-bibliothek.de/content/lizenzen/rv-fz", null);

        @Getter
        @Setter
        @JacksonXmlProperty(localName = "subject", namespace = "http://purl.org/dc/elements/1.1/")
        private ElementWithAttributes subject;

        @Getter
        @Setter
        @JacksonXmlProperty(localName = "title", namespace = "http://purl.org/dc/elements/1.1/")
        @JacksonXmlElementWrapper(useWrapping = false)
        private List<ElementWithAttributes> title = new ArrayList<>();

        @Getter
        @Setter
        @JacksonXmlProperty(localName = "type", namespace = "http://purl.org/dc/elements/1.1/")
        private ElementWithAttributes dcType = null;

        @Getter
        @Setter
        @JacksonXmlProperty(localName = "_DELETE_type", namespace = "http://www.w3.org/1999/02/22-rdf-syntax-ns#")
        private ElementWithAttributes rdfType = new ElementWithAttributes(null, null, "http://purl.org/dc/dcmitype/MovingImage", null);

        public String getExtent() {
            if (extent == null) {
                return null;
            }

            final Duration dur = Duration.ofSeconds(extent);
            if (dur.toHours() > 0) {
                return LocalTime.MIDNIGHT.plus(dur).format(DateTimeFormatter.ofPattern("HH:mm:ss", Locale.GERMAN));
            } else {
                return LocalTime.MIDNIGHT.plus(dur).format(DateTimeFormatter.ofPattern("mm:ss", Locale.GERMAN));
            }
        }

        public Date getCreated() {
            return new Date(created.getTime());
        }

        public void setCreated(Date date) {
            this.created = new Date(date.getTime());
        }

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        protected static class IsReferencedBy {

            @Getter
            @Setter
            @JacksonXmlProperty(localName = "CatalogRecord", namespace = "http://www.w3.org/ns/dcat#")
            private CatalogRecord CatalogRecord = new CatalogRecord();

            @JsonInclude(JsonInclude.Include.NON_EMPTY)
            protected static class CatalogRecord {

                @Getter
                @Setter
                @JacksonXmlProperty(localName = "identifier", namespace = "http://purl.org/dc/elements/1.1/")
                private String identifier;

                @Getter
                @Setter
                @JacksonXmlProperty(localName = "creator", namespace = "http://purl.org/dc/elements/1.1/")
                private String creator = "oid1616747773566";

                @Getter
                @Setter
                @JacksonXmlProperty(localName = "licence", namespace = "http://purl.org/dc/terms/")
                private ElementWithAttributes licence = new ElementWithAttributes(null, null, "http://creativecommons.org/publicdomain/zero/1.0/", null);

            }
        }

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        protected static class DcIdentifier {

            @Getter
            @Setter
            @JacksonXmlProperty(localName = "Identifier", namespace = "http://id.loc.gov/ontologies/bibframe/#")
            private BfIdentifier identifier = new BfIdentifier();

            @JsonInclude(JsonInclude.Include.NON_EMPTY)
            protected static class BfIdentifier {

                @Getter
                @Setter
                @JacksonXmlProperty(localName = "value", namespace = "http://www.w3.org/1999/02/22-rdf-syntax-ns#")
                @JacksonXmlElementWrapper(useWrapping = false)
                private List<String> value = new ArrayList<>();
            }
        }
    }
}
