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
import java.awt.EventQueue;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Michael Büchner <m.buechner@dnb.de>
 */
public class Main {

    private final static Logger LOG = LoggerFactory.getLogger(Main.class);

    /**
     * 
     * @param args 
     */
    public static void main(String args[]) {

        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            LOG.error("{}", ex.getMessage(), ex);
        }

        EventQueue.invokeLater(new Runnable() {
            @Override
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
