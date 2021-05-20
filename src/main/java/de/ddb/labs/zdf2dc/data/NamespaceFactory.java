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
package de.ddb.labs.zdf2dc.data;

import com.fasterxml.jackson.core.io.IOContext;
import com.fasterxml.jackson.dataformat.xml.XmlFactory;
import com.fasterxml.jackson.dataformat.xml.util.StaxUtil;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.Objects;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class NamespaceFactory extends XmlFactory {

    private final String defaultNamespace;
    private final Map<String, String> prefix2Namespace;

    public NamespaceFactory(String defaultNamespace, Map<String, String> prefix2Namespace) {
        this.defaultNamespace = Objects.requireNonNull(defaultNamespace);
        this.prefix2Namespace = Objects.requireNonNull(prefix2Namespace);
    }

    @Override
    public XMLStreamWriter _createXmlWriter(IOContext ctxt, Writer w) throws IOException {
        final XMLStreamWriter writer = super._createXmlWriter(ctxt, w);
        try {
            writer.setDefaultNamespace(defaultNamespace);
            for (Map.Entry<String, String> e : prefix2Namespace.entrySet()) {
                writer.setPrefix(e.getKey(), e.getValue());
            }
        } catch (XMLStreamException e) {
            StaxUtil.throwAsGenerationException(e, null);
        }
        return writer;
    }
}