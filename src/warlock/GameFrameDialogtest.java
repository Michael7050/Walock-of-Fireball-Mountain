///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package warlock;
//
//import javax.swing.JOptionPane;
//
///**
// *
// * @author Michael Jones
// */
//public class GameFrame extends javax.swing.JFrame {
//
//
//    /**
//     * Creates new form GameFrame
//     */
//    public GameFrame() {
//        initComponents();
//    }
//
//    /**
//     * This method is called from within the constructor to initialize the form.
//     * WARNING: Do NOT modify this code. The content of this method is always
//     * regenerated by the Form Editor.
//     */
//    @SuppressWarnings("unchecked")
//    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
//    private void initComponents() {
//
//        jLabel1 = new javax.swing.JLabel();
//        jLabel2 = new javax.swing.JLabel();
//        jLabel3 = new javax.swing.JLabel();
//        playerInput = new javax.swing.JTextField();
//        jButton1 = new javax.swing.JButton();
//        jScrollPane1 = new javax.swing.JScrollPane();
//        gameOutput = new javax.swing.JTextArea();
//
//        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
//        setTitle("Warlock of Firetop Mountain");
//
//        jLabel1.setFont(new java.awt.Font("Viner Hand ITC", 1, 24)); // NOI18N
//        jLabel1.setText("The Warlock of Firetop Mountain");
//
//        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
//        jLabel2.setText("Game text:");
//
//        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
//        jLabel3.setText("Player input:");
//
//        playerInput.setToolTipText("");
//
//        jButton1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
//        jButton1.setText("Submit");
//        jButton1.addActionListener(new java.awt.event.ActionListener() {
//            public void actionPerformed(java.awt.event.ActionEvent evt) {
//                jButton1ActionPerformed(evt);
//            }
//        });
//
//        jScrollPane1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
//
//        gameOutput.setEditable(false);
//        gameOutput.setColumns(20);
//        gameOutput.setRows(5);
//        jScrollPane1.setViewportView(gameOutput);
//
//        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
//        getContentPane().setLayout(layout);
//        layout.setHorizontalGroup(
//            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
//            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
//                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
//                .addComponent(jLabel1)
//                .addGap(261, 261, 261))
//            .addGroup(layout.createSequentialGroup()
//                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
//                    .addGroup(layout.createSequentialGroup()
//                        .addGap(416, 416, 416)
//                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
//                            .addComponent(jLabel3)
//                            .addComponent(jLabel2)))
//                    .addGroup(layout.createSequentialGroup()
//                        .addGap(329, 329, 329)
//                        .addComponent(playerInput, javax.swing.GroupLayout.PREFERRED_SIZE, 256, javax.swing.GroupLayout.PREFERRED_SIZE)
//                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
//                        .addComponent(jButton1))
//                    .addGroup(layout.createSequentialGroup()
//                        .addGap(57, 57, 57)
//                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 837, javax.swing.GroupLayout.PREFERRED_SIZE)))
//                .addContainerGap(35, Short.MAX_VALUE))
//        );
//        layout.setVerticalGroup(
//            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
//            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
//                .addComponent(jLabel1)
//                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
//                .addComponent(jLabel2)
//                .addGap(7, 7, 7)
//                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 251, Short.MAX_VALUE)
//                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
//                .addComponent(jLabel3)
//                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
//                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
//                    .addComponent(playerInput, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
//                    .addComponent(jButton1))
//                .addGap(32, 32, 32))
//        );
//
//        pack();
//    }// </editor-fold>//GEN-END:initComponents
//
//    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
//        // TODO add your handling code here:
//
//        if (playerInput.getText().isEmpty())
//        {
//            JOptionPane.showMessageDialog(null, "Please enter an input.");
//        }
//        else
//        {
//        String input = playerInput.getText();
//        playerInput.setText(null);
//        getInput(input);
//        }
//    }//GEN-LAST:event_jButton1ActionPerformed
//
//    /**
//     * @param args the command line arguments
//     */
//    public static void main(String args[]) {
//        /* Set the Nimbus look and feel */
//        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
//        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
//         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
//         */
//        try {
//            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
//                if ("Nimbus".equals(info.getName())) {
//                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
//                    break;
//                }
//            }
//        } catch (ClassNotFoundException ex) {
//            java.util.logging.Logger.getLogger(GameFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (InstantiationException ex) {
//            java.util.logging.Logger.getLogger(GameFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (IllegalAccessException ex) {
//            java.util.logging.Logger.getLogger(GameFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
//            java.util.logging.Logger.getLogger(GameFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        }
//        //</editor-fold>
//
//        /* Create and display the form */
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                new GameFrame().setVisible(true);
//            }
//        });
//    }
//
//    // Variables declaration - do not modify//GEN-BEGIN:variables
//    public static javax.swing.JTextArea gameOutput;
//    private javax.swing.JButton jButton1;
//    private javax.swing.JLabel jLabel1;
//    private javax.swing.JLabel jLabel2;
//    private javax.swing.JLabel jLabel3;
//    private javax.swing.JScrollPane jScrollPane1;
//    private javax.swing.JTextField playerInput;
//    // End of variables declaration//GEN-END:variables
//
//    public static void writeToScreen(String... strings) {
//        for (String s : strings) {
//            gameOutput.append(s + "\n");
//            gameOutput.setCaretPosition(gameOutput.getDocument().getLength());
//        }
//    }
//
//    public static String getInput(String input)
//    {
//        return input;
//    }
//}