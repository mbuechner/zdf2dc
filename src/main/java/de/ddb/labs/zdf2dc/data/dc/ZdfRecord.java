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
package de.ddb.labs.zdf2dc.data.dc;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import lombok.Getter;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JacksonXmlRootElement(namespace = "http://www.openarchives.org/OAI/2.0/", localName = "record")
public class ZdfRecord {

    @Setter
    @Getter
    @JacksonXmlProperty(localName = "alternative", namespace = "http://purl.org/dc/terms/")
    @JacksonXmlElementWrapper(useWrapping = false)
    private ZDF2DcList<DcTerms> alternative = new ZDF2DcList<>();
    @Getter
    @Setter
    private String aspectRatio;
    @Getter
    @Setter
    @JacksonXmlProperty(localName = "description", namespace = "http://purl.org/dc/elements/1.1/")
    @JacksonXmlElementWrapper(useWrapping = false)
    private ZDF2DcList<DcElement> description = new ZDF2DcList<>();
    @Setter
    @JacksonXmlProperty(localName = "extent", namespace = "http://purl.org/dc/terms/")
    private Integer duration;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "d. MMMM yyyy")
    @JacksonXmlProperty(localName = "created", namespace = "http://purl.org/dc/terms/")
    private Date editorialDate;
    @Getter
    @Setter
    @JacksonXmlProperty(localName = "format", namespace = "http://purl.org/dc/elements/1.1/")
    private String format = "video/vnd.ddbkultur.zdf";
    @Getter
    @Setter
    private String fsk;
    @Getter
    @Setter
    @JacksonXmlProperty(localName = "identifier", namespace = "http://purl.org/dc/elements/1.1/")
    @JacksonXmlElementWrapper(useWrapping = false)
    public ZDF2DcList<DcElement> identifier = new ZDF2DcList<>();
    @Getter
    @Setter
    @JacksonXmlProperty(localName = "language", namespace = "http://purl.org/dc/elements/1.1/")
    private String language = "Deutsch";
    @Getter
    @Setter
    @JacksonXmlProperty(localName = "isPartOf", namespace = "http://purl.org/dc/terms/")
    @JsonProperty("moduleName")
    private String module_title;
    @Getter
    @Setter
    @JacksonXmlProperty(localName = "rights", namespace = "http://purl.org/dc/elements/1.1/")
    private DcElement rights = new DcElement("https://www.deutsche-digitale-bibliothek.de/content/lizenzen/rv-fz", "binary", null);
    @Getter
    @Setter
    @JacksonXmlProperty(localName = "subject", namespace = "http://purl.org/dc/elements/1.1/")
    @JacksonXmlElementWrapper(useWrapping = false)
    public ZDF2DcList<DcElement> subject = new ZDF2DcList<>();
    @Getter
    @Setter
    @JacksonXmlProperty(localName = "title", namespace = "http://purl.org/dc/elements/1.1/")
    @JacksonXmlElementWrapper(useWrapping = false)
    private ZDF2DcList<DcElement> title = new ZDF2DcList<>();
    @Getter
    @Setter
    @JacksonXmlProperty(localName = "type", namespace = "http://purl.org/dc/elements/1.1/")
    @JacksonXmlElementWrapper(useWrapping = false)
    public ZDF2DcList<DcElement> type = new ZDF2DcList<>();
    @Getter
    @JacksonXmlProperty(localName = "dc", namespace = "http://www.w3.org/2000/xmlns/", isAttribute = true)
    private final static String XMLNSDC = "http://purl.org/dc/elements/1.1/";
    @Getter
    @JacksonXmlProperty(localName = "dcterms", namespace = "http://www.w3.org/2000/xmlns/", isAttribute = true)
    private final static String XMLNSDCTERMS = "http://purl.org/dc/terms/";
    @Getter
    @JacksonXmlProperty(localName = "doc", namespace = "http://www.w3.org/2000/xmlns/", isAttribute = true)
    private final static String XMLNSDOC = "http://www.lyncode.com/xoai";
    @Getter
    @JacksonXmlProperty(localName = "europeana", namespace = "http://www.w3.org/2000/xmlns/", isAttribute = true)
    private final static String XMLNSEUROPEANA = "http://www.europeana.eu/schemas/ese/";

    public ZdfRecord() {
        getIdentifier().add(new DcElement("oid1616747773566", "providerId", null));
    }

    public String getDuration() {
        if (duration == null) {
            return null;
        }

        final Duration dur = Duration.ofSeconds(duration);
        if (dur.toHours() > 0) {
            return LocalTime.MIDNIGHT.plus(dur).format(DateTimeFormatter.ofPattern("HH:mm:ss", Locale.GERMAN));
        } else {
            return LocalTime.MIDNIGHT.plus(dur).format(DateTimeFormatter.ofPattern("mm:ss", Locale.GERMAN));
        }
    }

    public Date getEditorialDate() {
        return new Date(editorialDate.getTime());
    }

    public void setEditorialDate(Date date) {
        this.editorialDate = new Date(date.getTime());
    }

}
