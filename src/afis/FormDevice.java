/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package afis;

import com.digitalpersona.onetouch.DPFPDataPurpose;
import com.digitalpersona.onetouch.DPFPFeatureSet;
import com.digitalpersona.onetouch.DPFPGlobal;
import com.digitalpersona.onetouch.DPFPSample;
import com.digitalpersona.onetouch.DPFPTemplate;
import com.digitalpersona.onetouch.capture.DPFPCapture;
import com.digitalpersona.onetouch.capture.event.DPFPDataAdapter;
import com.digitalpersona.onetouch.capture.event.DPFPDataEvent;
import com.digitalpersona.onetouch.capture.event.DPFPErrorAdapter;
import com.digitalpersona.onetouch.capture.event.DPFPErrorEvent;
import com.digitalpersona.onetouch.capture.event.DPFPReaderStatusAdapter;
import com.digitalpersona.onetouch.capture.event.DPFPReaderStatusEvent;
import com.digitalpersona.onetouch.capture.event.DPFPSensorAdapter;
import com.digitalpersona.onetouch.capture.event.DPFPSensorEvent;
import com.digitalpersona.onetouch.processing.DPFPEnrollment;
import com.digitalpersona.onetouch.processing.DPFPFeatureExtraction;
import com.digitalpersona.onetouch.processing.DPFPImageQualityException;
import com.digitalpersona.onetouch.processing.DPFPTemplateStatus;
import com.digitalpersona.onetouch.verification.DPFPVerification;
import com.digitalpersona.onetouch.verification.DPFPVerificationResult;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 *
 * @author Stefanos Tzortzoglou
 */
public class FormDevice extends javax.swing.JFrame {
    
    /* Variable that handles the fingerprint reader.*/
    private DPFPCapture Capture = DPFPGlobal.getCaptureFactory().createCapture();
    
    /* Variable that allows the capture of the fingerprint traces, to determine 
    the characteristics and be able to estimate the creation of a template of the
    fingerprint and then to save it. */
    private DPFPEnrollment Enrollment = DPFPGlobal.getEnrollmentFactory().createEnrollment();
    
    /* This variable also captures the fingerprint and creats it's characteristics 
    to authenticate it or verify it with the ones that are stored in the Database. */
    private DPFPVerification Verification = DPFPGlobal.getVerificationFactory().createVerification();
    
    /*Variable that creates the template of the fingerprint after the characteristics
    are created.*/
    private DPFPTemplate template;
    public static String TEMPLATE_PROPERTY = "template";

    /**
     * Creates new form FormDevice
     */
    public FormDevice() {
        initComponents();
        this.setLocationRelativeTo(null);
        Clock obj = new Clock(labelTime);
        obj.start();
    }
    
    public Image getIconImage(){
        Image retValue = Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource("resources/icon.png"));
        return retValue;
    }
    
    protected void Initiation(){
        //Fingerprint Capture
        Capture.addDataListener(new DPFPDataAdapter(){
            @Override public void dataAcquired(final DPFPDataEvent e){
                SwingUtilities.invokeLater(new Runnable(){
                    public void run(){
                        SetText("The fingerprint has been captured");
                        ProcessCapture(e.getSample());
                    }
                });
            }
        });
        //Handling the status of the fingerprint reader
        Capture.addReaderStatusListener(new DPFPReaderStatusAdapter(){
            //Activate reader
            @Override public void readerConnected(final DPFPReaderStatusEvent e){
                SwingUtilities.invokeLater(new Runnable(){
                    public void run(){
                        SetText("The fingerprint reader is activated");
                    }
                });
            }
            //Fingerprint reader deactivated
            @Override public void readerDisconnected(final DPFPReaderStatusEvent e){
                SwingUtilities.invokeLater(new Runnable(){
                    public void run(){
                    SetText("The fingerprint reader is not available");
                    }
                });
            }
        });
        //Handling the sensor
        Capture.addSensorListener(new DPFPSensorAdapter(){
            //Positioning the finger on the sensor
            @Override public void fingerTouched(final DPFPSensorEvent e){
                SwingUtilities.invokeLater(new Runnable(){
                    public void run(){
                        SetText("The finger has been placed");
                    }
                });
            }
            //Remove finger from the sensor
            @Override public void fingerGone(final DPFPSensorEvent e){
                SwingUtilities.invokeLater(new Runnable(){
                    public void run(){
                        SetText("The finger has been removed");
                        try{
                            FingerprintIdentification();
                            Enrollment.clear();
                        }catch(IOException ex){
                            Logger.getLogger(FormDevice.class.getName()).log(Level.SEVERE, null, ex);
                        }finally{
                            lblFingerprintImage.setIcon(null);
                        }
                    }
                });
            }
        });
        //Error handling
        Capture.addErrorListener(new DPFPErrorAdapter(){
            public void errorReader(final DPFPErrorEvent e){
                SwingUtilities.invokeLater(new Runnable(){
                    public void run(){
                        SetText("Error: "+e.getError());
                    }
                });
            }
        });
    }
    
    //Variables to save and verify the traces in the DB
    public DPFPFeatureSet featuresinscription;
    public DPFPFeatureSet featuresverification;
    
    public  DPFPFeatureSet extractCharacteristics(DPFPSample sample, DPFPDataPurpose purpose){
        DPFPFeatureExtraction extractor = DPFPGlobal.getFeatureExtractionFactory().createFeatureExtraction();
        try {
            return extractor.createFeatureSet(sample, purpose);
        }catch (DPFPImageQualityException e){
            return null;
        }
    }
    
    public Image ClearFingerprintImage(DPFPSample sample){
        return DPFPGlobal.getSampleConversionFactory().createImage(sample);
    }
    
    public void CreateFingerprintImage(Image image){
        lblFingerprintImage.setIcon(new ImageIcon(image.getScaledInstance(lblFingerprintImage.getWidth(),lblFingerprintImage.getHeight(),Image.SCALE_DEFAULT)));
        repaint();
    }

    public void SetText(String string){
        lblStatus.setText(string);
    }

    public void start(){
        Capture.startCapture();
        SetText("Fingerprint reader is being used");
    }

    public void stop(){
        Capture.stopCapture();
        SetText("Fingerprint reader is not being used");
    } 

    public DPFPTemplate getTemplate() {
        return template;
    }

    public void setTemplate(DPFPTemplate template) {
        DPFPTemplate old = this.template;
        this.template = template;
        firePropertyChange(TEMPLATE_PROPERTY, old, template);
    }

    public  void ProcessCapture(DPFPSample sample){
        /* Process the sample of the fingerprint and create a set of characteristics
        for the purpose of registration.*/
        featuresinscription = extractCharacteristics(sample, DPFPDataPurpose.DATA_PURPOSE_ENROLLMENT);
        /* Process the sample of the fingerprint and create a set of characteristics
        for the purpose of verification.*/
        featuresverification = extractCharacteristics(sample, DPFPDataPurpose.DATA_PURPOSE_VERIFICATION);

        /* Check the quality of the fingerprint sample and add it to the enrollment
        if it is good. */
        if (featuresinscription != null)
            try{
                Enrollment.addFeatures(featuresinscription);//Add the characteristics of the fingerprint to the template.
                
                // Create the captured fingerprint.
                Image image=ClearFingerprintImage(sample);
                CreateFingerprintImage(image);

            }catch(DPFPImageQualityException ex){
                JOptionPane.showMessageDialog(null, ex);
            }finally{
                // Check if the template has been created.
                switch(Enrollment.getTemplateStatus()){
                    case TEMPLATE_STATUS_READY:	// If successful stop.
                        stop();
                        setTemplate(Enrollment.getTemplate());
                        SetText("The fingerprint has been created, now you can save it");
                    break;
                    case TEMPLATE_STATUS_FAILED: // If failed restart fingerprint capture
                        Enrollment.clear();
                        stop();
                        setTemplate(null);
                        JOptionPane.showMessageDialog(FormDevice.this, "The fingerprint cannot been created", "Attendance Tracker", JOptionPane.ERROR_MESSAGE);
                        start();
                    break;
                }
            }
    }

    public static int user;
    public void FingerprintIdentification() throws IOException{
        
        //Connection to the DB
        ConnectionMySQL mysql = new ConnectionMySQL();
        Connection con = mysql.Connect();
        
        try{
            //Get all traces from the DB
            PreparedStatement identificationStmt = con.prepareStatement("SELECT (idEmployee) AS ID, blobFinger FROM tblemployee");
            ResultSet rs = identificationStmt.executeQuery();

            //If the name is found in the DB
            while(rs.next()){
                //Read the template from the DB
                byte templateBuffer[] = rs.getBytes("blobFinger");
                user = rs.getInt("ID");
                //Create a new template from the one stored in the DB
                DPFPTemplate referenceTemplate = DPFPGlobal.getTemplateFactory().createTemplate(templateBuffer);
                //Send the created template to the template container of the fingerprint component.
                setTemplate(referenceTemplate);

                /* Compare the characteristics of the recently captured fingerprint
                with any template saved in the DB that matches that type.*/
                DPFPVerificationResult result = Verification.verify(featuresverification, getTemplate());
                //Compare the templates (actual vs DB)
                //If they correspond create the map
                //and indicate the name of the person who matches
                if (result.isVerified()){
                    //Create the image of the saved data from the fingerprint stored in the DB.
                    //Captured fingerprint found in the DB therefore the attendance is saved.
                    registerAttendance();
                     return;
                }
            }
            //If there are no fingerprint found corresponding to the name, indicate it with a message
            JOptionPane.showMessageDialog(null, "There is no record that matches the fingerprint", "Error", JOptionPane.ERROR_MESSAGE);
            setTemplate(null);
            }catch(SQLException e) {
                JOptionPane.showMessageDialog(null, "Failed to identify the fingerprint" + e);
            }
    }
    
    public void registerAttendance(){
        //Variables for the connection
        ConnectionMySQL mysql = new ConnectionMySQL();
        Connection con = mysql.Connect();
        
        //Variables
        String qSql;
        int nd, emp;
                        
        emp = user;
                                
        qSql = "INSERT INTO tblattendance(idEmployee, dteDate, tmeTime)"
                + " VALUES(?,current_date(),current_time())";
        
        String men = "Recorded Attendance";
                 
        try {
            PreparedStatement pst = con.prepareStatement(qSql);
            
            //Insert the value in the field of the DB.
            pst.setInt(1, emp);
            nd = pst.executeUpdate();
            
            if(nd > 0){
                JOptionPane.showMessageDialog(null, men, "Time Attendance Checker", 1);            
            }
            
        } catch (SQLException ex) {
        }
                
    }

    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        lblFingerprintImage = new javax.swing.JLabel();
        lblStatus = new javax.swing.JLabel();
        labelTime = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Biometric Reader");
        setIconImage(getIconImage());
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setFont(new java.awt.Font("Segoe UI Light", 0, 36)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Attendance Tracker");
        jLabel1.setToolTipText("");
        jLabel1.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        jLabel1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Resources/administrator2-128.png"))); // NOI18N

        lblFingerprintImage.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(204, 204, 204), 1, true));

        lblStatus.setFont(new java.awt.Font("Segoe UI Light", 0, 12)); // NOI18N

        labelTime.setFont(new java.awt.Font("Segoe UI Semibold", 0, 14)); // NOI18N
        labelTime.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 384, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(48, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelTime, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(lblStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblFingerprintImage, javax.swing.GroupLayout.DEFAULT_SIZE, 138, Short.MAX_VALUE))
                        .addGap(46, 46, 46)
                        .addComponent(jLabel2)))
                .addGap(48, 48, 48))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelTime, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(27, 27, 27)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblFingerprintImage, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        stop();
        AFIS obj = new AFIS();
        obj.setVisible(true);
    }//GEN-LAST:event_formWindowClosing

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        Initiation();
	start();                             
    }//GEN-LAST:event_formWindowOpened

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(FormDevice.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FormDevice.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FormDevice.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FormDevice.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FormDevice().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel labelTime;
    private javax.swing.JLabel lblFingerprintImage;
    private javax.swing.JLabel lblStatus;
    // End of variables declaration//GEN-END:variables
}
