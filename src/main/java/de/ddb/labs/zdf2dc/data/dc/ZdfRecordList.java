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

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

/**
 * 
 * @author Michael Büchner <m.buechner@dnb.de>
 */
@JacksonXmlRootElement(localName = "ListRecords", namespace = "http://www.openarchives.org/OAI/2.0/")
public class ZdfRecordList {
    
    @Getter
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "record", namespace = "http://www.openarchives.org/OAI/2.0/")
    private List<ZdfRecord> list = new ArrayList<>();
    
    public void clear() {
        list.clear();
    }
    
}
