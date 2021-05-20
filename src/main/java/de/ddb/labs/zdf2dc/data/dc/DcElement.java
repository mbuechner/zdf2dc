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

@EqualsAndHashCode(doNotUseGetters=true)
public class DcElement {

    @Getter
    @Setter
    // @JacksonXmlProperty(localName = "type", namespace = "http://purl.org/dc/elements/1.1/", isAttribute = true)
    @JacksonXmlProperty(localName = "type", isAttribute = true)
    private String type;

    @Getter
    @Setter
    @JacksonXmlProperty(localName = "lang", namespace = "http://www.w3.org/XML/1998/namespace", isAttribute = true)
    private String lang;

    @Setter
    @JacksonXmlText
    private String elementValue;

    public DcElement() {
        this.lang = null;
        this.type = null;
        this.elementValue = null;
    }

    public DcElement(String elementValue, String type, String lang) {
        this.lang = lang;
        this.type = type;
        this.elementValue = elementValue;
    }

    public String getElementValue() {
        if (elementValue.length() > 2 && elementValue.startsWith("\"") && elementValue.endsWith("\"")) {
            return elementValue.substring(1, elementValue.length() - 1);
        }
        return elementValue;
    }
}
