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

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author Michael Büchner <m.buechner@dnb.de>
 */
@EqualsAndHashCode(doNotUseGetters=true)
public class DcTerms {

    @Getter
    @Setter
    @JacksonXmlProperty(localName = "type", namespace = "http://purl.org/dc/elements/1.1/", isAttribute = true)
    private String type;

    @Getter
    @Setter
    @JacksonXmlProperty(localName = "lang", namespace = "http://www.w3.org/XML/1998/namespace", isAttribute = true)
    private String lang;

    @Getter
    @Setter
    @JacksonXmlProperty(localName = "resource", namespace = "http://www.w3.org/1999/02/22-rdf-syntax-ns#", isAttribute = true)
    private String resource;

    @Setter
    @JacksonXmlText
    private String elementValue;

    public DcTerms(String elementValue, String type, String resource, String lang) {
        this.lang = lang;
        this.type = type;
        this.resource = resource;
        this.elementValue = elementValue;
    }

    public String getElementValue() {
        if (elementValue == null) {
            return null;
        }
        if (elementValue.length() > 2 && elementValue.startsWith("\"") && elementValue.endsWith("\"")) {
            return elementValue.substring(1, elementValue.length() - 1);
        }
        return elementValue;
    }
}
