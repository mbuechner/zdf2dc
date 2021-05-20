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
package de.ddb.labs.zdf2dc;

import de.ddb.labs.zdf2dc.gui.Gui;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private final static Logger LOG = LoggerFactory.getLogger(Main.class);

    public static void main(String args[]) {

        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            LOG.error("{}", ex.getMessage(), ex);
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    final Gui gui = new Gui();
                    gui.setLocationRelativeTo(null);
                    gui.setVisible(true);
                } catch (Exception e) {
                    LOG.error("{}", e.getMessage(), e);
                }
            }
            
        });
    }
}
