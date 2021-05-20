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

import java.util.ArrayList;
import java.util.ListIterator;

public class ZDF2DcList<DcElement> extends ArrayList<DcElement> {

    @Override
    public boolean add(DcElement e) {
        ListIterator<DcElement> litr = super.listIterator();
        while (litr.hasNext()) {
            if (e.equals(litr.next())) {
                return false;
            }
        }
        return super.add(e);
    }
}
