/*
 * Copyright 2017 MovingBlocks
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
package aurelienribon.bodyeditor.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

import aurelienribon.Res;
import aurelienribon.bodyeditor.Settings;
import aurelienribon.ui.components.PaintedPanel;
import aurelienribon.ui.css.Style;

public class AutoTraceParamsDialog extends javax.swing.JDialog {
        private boolean result = false;

        public AutoTraceParamsDialog(javax.swing.JFrame parent) {
                super(parent, true);

                setContentPane(new PaintedPanel());
                initComponents();

                Style.registerCssClasses(getContentPane(), ".popPanel", ".configPanel");
                Style.registerCssClasses(commentLabel, ".brightcomment");
                Style.apply(getContentPane(), new Style(Res.getUrl("/css/style.css")));

                hullToleranceSlider.setValue((int) (Settings.autoTraceHullTolerance * 100));
                alphaToleranceSlider.setValue(Settings.autoTraceAlphaTolerance);
                multiPartDetectionChk.setSelected(Settings.autoTraceMultiPartDetection);
                holeDetectionChk.setSelected(Settings.autoTraceHoleDetection);

                okBtn.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                                Settings.autoTraceHullTolerance = hullToleranceSlider.getValue() / 100f;
                                Settings.autoTraceAlphaTolerance = alphaToleranceSlider.getValue();
                                Settings.autoTraceMultiPartDetection = multiPartDetectionChk.isSelected();
                                Settings.autoTraceHoleDetection = holeDetectionChk.isSelected();
                                dispose();
                                result = true;
                        }
                });

                cancelBtn.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                                dispose();
                        }
                });
        }

        public boolean prompt() {
                setVisible(true);
                dispose();
                return result;
        }

        // -------------------------------------------------------------------------
        // Generated stuff
        // -------------------------------------------------------------------------

        @SuppressWarnings("unchecked")
        // <editor-fold defaultstate="collapsed" desc="Generated
        // Code">//GEN-BEGIN:initComponents
        private void initComponents() {

                paintedPanel1 = new aurelienribon.ui.components.PaintedPanel();
                hullToleranceSlider = new javax.swing.JSlider();
                alphaToleranceSlider = new javax.swing.JSlider();
                multiPartDetectionChk = new javax.swing.JCheckBox();
                holeDetectionChk = new javax.swing.JCheckBox();
                jLabel1 = new javax.swing.JLabel();
                jLabel2 = new javax.swing.JLabel();
                okBtn = new javax.swing.JButton();
                cancelBtn = new javax.swing.JButton();
                commentLabel = new javax.swing.JLabel();
                jLabel3 = new javax.swing.JLabel();

                setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
                setTitle("Auto-trace parameters");
                setResizable(false);

                hullToleranceSlider.setMajorTickSpacing(100);
                hullToleranceSlider.setMaximum(400);
                hullToleranceSlider.setMinimum(100);
                hullToleranceSlider.setMinorTickSpacing(10);
                hullToleranceSlider.setPaintTicks(true);
                hullToleranceSlider.setValue(400);

                alphaToleranceSlider.setMaximum(255);
                alphaToleranceSlider.setMinorTickSpacing(5);
                alphaToleranceSlider.setPaintTicks(true);
                alphaToleranceSlider.setValue(128);

                multiPartDetectionChk.setText("Multi-part detection");
                multiPartDetectionChk.setEnabled(false);

                holeDetectionChk.setText("Hole detection");
                holeDetectionChk.setEnabled(false);

                jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
                jLabel1.setText("Hull tolerance: ");

                jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
                jLabel2.setText("Alpha tolerance: ");

                okBtn.setLayout(new BorderLayout());
                javax.swing.JLabel oklabel = new javax.swing.JLabel("OK");
                oklabel.setPreferredSize(new Dimension(70, 30));
                oklabel.setAlignmentX(javax.swing.JLabel.CENTER_ALIGNMENT);
                oklabel.setVerticalAlignment(SwingConstants.CENTER);
                oklabel.setHorizontalAlignment(SwingConstants.CENTER);

                oklabel.setOpaque(true);

                okBtn.add(oklabel);
                okBtn.setOpaque(true);
                okBtn.setBorderPainted(true);
                okBtn.setFocusPainted(false);
                okBtn.setBorder(new LineBorder(new Color(87, 87, 87), 2, true));
                okBtn.setVerticalAlignment(SwingConstants.CENTER);
                okBtn.setHorizontalAlignment(SwingConstants.CENTER);

                cancelBtn.setLayout(new BorderLayout());
                javax.swing.JLabel label = new javax.swing.JLabel("CANCEL");
                label.setPreferredSize(new Dimension(70, 30));
                label.setAlignmentX(javax.swing.JLabel.CENTER_ALIGNMENT);
                label.setVerticalAlignment(SwingConstants.CENTER);
                label.setHorizontalAlignment(SwingConstants.CENTER);

                label.setOpaque(true);

                cancelBtn.add(label);
                cancelBtn.setOpaque(true);
                cancelBtn.setBorderPainted(true);
                cancelBtn.setFocusPainted(false);
                cancelBtn.setBorder(new LineBorder(new Color(87, 87, 87), 2, true));
                cancelBtn.setVerticalAlignment(SwingConstants.CENTER);
                cancelBtn.setHorizontalAlignment(SwingConstants.CENTER);

                javax.swing.GroupLayout paintedPanel1Layout = new javax.swing.GroupLayout(paintedPanel1);
                paintedPanel1.setLayout(paintedPanel1Layout);
                paintedPanel1Layout.setHorizontalGroup(paintedPanel1Layout
                                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(paintedPanel1Layout.createSequentialGroup().addContainerGap()
                                                .addGroup(paintedPanel1Layout.createParallelGroup(
                                                                javax.swing.GroupLayout.Alignment.LEADING)
                                                                .addGroup(paintedPanel1Layout.createSequentialGroup()
                                                                                .addComponent(jLabel1)
                                                                                .addPreferredGap(
                                                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                .addComponent(hullToleranceSlider,
                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                Short.MAX_VALUE))
                                                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
                                                                                paintedPanel1Layout
                                                                                                .createSequentialGroup()
                                                                                                .addComponent(jLabel2)
                                                                                                .addPreferredGap(
                                                                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                                .addComponent(alphaToleranceSlider,
                                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                                Short.MAX_VALUE))
                                                                .addGroup(paintedPanel1Layout.createSequentialGroup()
                                                                                .addComponent(multiPartDetectionChk)
                                                                                .addGap(0, 0, Short.MAX_VALUE))
                                                                .addGroup(paintedPanel1Layout.createSequentialGroup()
                                                                                .addComponent(holeDetectionChk)
                                                                                .addPreferredGap(
                                                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                Short.MAX_VALUE)
                                                                                .addComponent(okBtn)
                                                                                .addPreferredGap(
                                                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                .addComponent(cancelBtn,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)))
                                                .addContainerGap()));

                paintedPanel1Layout.linkSize(javax.swing.SwingConstants.CENTER,
                                new java.awt.Component[] { jLabel1, jLabel2 });

                paintedPanel1Layout.linkSize(javax.swing.SwingConstants.CENTER,
                                new java.awt.Component[] { cancelBtn, okBtn });

                paintedPanel1Layout.setVerticalGroup(paintedPanel1Layout
                                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(paintedPanel1Layout.createSequentialGroup().addContainerGap()
                                                .addGroup(paintedPanel1Layout.createParallelGroup(
                                                                javax.swing.GroupLayout.Alignment.LEADING)
                                                                .addComponent(hullToleranceSlider,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addComponent(jLabel1))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(paintedPanel1Layout.createParallelGroup(
                                                                javax.swing.GroupLayout.Alignment.LEADING)
                                                                .addComponent(alphaToleranceSlider,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addComponent(jLabel2))
                                                .addGap(18, 18, 18).addComponent(multiPartDetectionChk)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(paintedPanel1Layout.createParallelGroup(
                                                                javax.swing.GroupLayout.Alignment.BASELINE)
                                                                .addComponent(holeDetectionChk).addComponent(okBtn)
                                                                .addComponent(cancelBtn,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addContainerGap()));

                paintedPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {
                                alphaToleranceSlider, hullToleranceSlider, jLabel1, jLabel2 });

                // commentLabel.setText("Only check multi-part detection or hole detection if
                // your image needs it.");
                // commentLabel.setVerticalAlignment(javax.swing.SwingConstants.TOP);

                jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gfx/autoTrace.png"))); // NOI18N

                javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
                getContentPane().setLayout(layout);
                layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup().addComponent(jLabel3)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(layout.createParallelGroup(
                                                                javax.swing.GroupLayout.Alignment.LEADING)
                                                                .addComponent(paintedPanel1,
                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                Short.MAX_VALUE)
                                                                .addComponent(commentLabel,
                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                388, Short.MAX_VALUE))
                                                .addContainerGap()));
                layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup().addContainerGap()
                                                .addComponent(paintedPanel1, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(commentLabel).addContainerGap())
                                .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));

                pack();
        }// </editor-fold>//GEN-END:initComponents

        // Variables declaration - do not modify//GEN-BEGIN:variables
        private javax.swing.JSlider alphaToleranceSlider;
        private javax.swing.JButton cancelBtn;
        private javax.swing.JLabel commentLabel;
        private javax.swing.JCheckBox holeDetectionChk;
        private javax.swing.JSlider hullToleranceSlider;
        private javax.swing.JLabel jLabel1;
        private javax.swing.JLabel jLabel2;
        private javax.swing.JLabel jLabel3;
        private javax.swing.JCheckBox multiPartDetectionChk;
        private javax.swing.JButton okBtn;
        private aurelienribon.ui.components.PaintedPanel paintedPanel1;
        // End of variables declaration//GEN-END:variables
}
