/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package afis;

import com.toedter.calendar.JDateChooser;
import com.toedter.calendar.JTextFieldDateEditor;
import java.awt.Image;
import java.awt.Toolkit;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Stefanos Tzortzoglou
 */
public class ViewAttendance extends javax.swing.JFrame {

    /**
     * Creates new form ViewAttendance
     */
    public ViewAttendance() {
        initComponents();
        this.setLocationRelativeTo(null);
        findAttendance();
    }
    
    public Image getIconImage(){
        Image retValue = Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource("Resources/icon.png"));
        return retValue;
    }
    
    public class Attendance {
    
    private int idEmployee;
    private Date dteDate;
    private String formattedDate;
    private String tmeTime;
    
    
    public Attendance(int idEmployee,Date dteDate,String tmeTime)
    {
        this.idEmployee = idEmployee;
        this.dteDate = dteDate;
        this.tmeTime = tmeTime;
    }
    
    public int getidEmployee()
    {
        return idEmployee;
    }
    
    public Date getdteDate()
    {
        return dteDate;
    }
    
    public String getFormattedDate()
    {
        DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
        formattedDate = dateFormat.format(dteDate);
        return formattedDate;
    }
    
    public String gettmeTime()
    {
        return tmeTime;
    }
    
}
    
    public ArrayList<Attendance> ListAttendance()
    {
        
        ArrayList<Attendance> AttendanceList = new ArrayList<Attendance>();
        
        Statement st;
        ResultSet rs;
        
        try{
            //Connection variables
            ConnectionMySQL mysql = new ConnectionMySQL();
            Connection con = mysql.Connect();
            
            st = con.createStatement();
            
            //Save dates
            String dFrom = new SimpleDateFormat("yyyy-MM-dd").format(dateFrom.getDate());
            String dTo = new SimpleDateFormat("yyyy-MM-dd").format(dateTo.getDate());
        
            rs = st.executeQuery("SELECT idEmployee, dteDate, tmeTime FROM `tblattendance` WHERE tblattendance.dteDate BETWEEN '" + dFrom + "' AND '" + dTo + "' ");
            
            Attendance attendance;
            
            while(rs.next())
            {
                attendance = new Attendance(
                                rs.getInt(1),
                                rs.getDate(2),
                                rs.getString(3)
                                );
                AttendanceList.add(attendance);
            }
            
        }catch(Exception ex){
            System.out.println(ex.getMessage());
        }
        
        return AttendanceList;
    }
    
    // Function to display data in jTable
    public void findAttendance()
    {
      
        //Save dates
        java.util.Date dFrom = dateFrom.getDate();
        java.util.Date dTo = dateTo.getDate();
        
        ArrayList<Attendance> attendance = ListAttendance();
                    
                    DefaultTableModel model = new DefaultTableModel();
                    model.setColumnIdentifiers(new Object[]{"Employee","Date","Time"});
                    Object[] row = new Object[3];
        
                    for(int i = 0; i < attendance.size(); i++)
                    {
                        row[0] = attendance.get(i).getidEmployee();
                        row[1] = attendance.get(i).getFormattedDate();
                        row[2] = attendance.get(i).gettmeTime();
                        model.addRow(row);
                    }
                    jTable_Attendance.setModel(model);
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
        labelTime = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        dateFrom = new com.toedter.calendar.JDateChooser();
        btnSearch = new javax.swing.JButton();
        dateTo = new com.toedter.calendar.JDateChooser();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable_Attendance = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("View Attendance");
        setIconImage(getIconImage());
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setPreferredSize(new java.awt.Dimension(789, 444));
        jPanel1.setLayout(null);

        jLabel1.setFont(new java.awt.Font("Segoe UI Light", 0, 36)); // NOI18N
        jLabel1.setText("Attendance Tracker");
        jPanel1.add(jLabel1);
        jLabel1.setBounds(20, 20, 310, 48);

        labelTime.setFont(new java.awt.Font("Segoe UI Semibold", 0, 14)); // NOI18N
        labelTime.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jPanel1.add(labelTime);
        labelTime.setBounds(20, 70, 150, 30);

        jLabel3.setFont(new java.awt.Font("Segoe UI Light", 0, 20)); // NOI18N
        jLabel3.setText("From:");
        jPanel1.add(jLabel3);
        jLabel3.setBounds(20, 150, 90, 27);

        jLabel4.setFont(new java.awt.Font("Segoe UI Light", 0, 20)); // NOI18N
        jLabel4.setText("To:");
        jPanel1.add(jLabel4);
        jLabel4.setBounds(20, 200, 90, 27);

        jLabel2.setFont(new java.awt.Font("Segoe UI Light", 0, 24)); // NOI18N
        jLabel2.setText("Pick the dates to view attendance");
        jPanel1.add(jLabel2);
        jLabel2.setBounds(20, 90, 370, 20);

        dateFrom.setDateFormatString("d MMM yyyy");
        Date date = new Date();
        dateFrom.setDate(date);
        jPanel1.add(dateFrom);
        dateFrom.setBounds(150, 150, 210, 30);

        btnSearch.setFont(new java.awt.Font("Segoe UI Light", 0, 14)); // NOI18N
        btnSearch.setForeground(new java.awt.Color(255, 255, 255));
        btnSearch.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/fontButton0.png"))); // NOI18N
        btnSearch.setText("Search");
        btnSearch.setBorder(null);
        btnSearch.setBorderPainted(false);
        btnSearch.setContentAreaFilled(false);
        btnSearch.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });
        jPanel1.add(btnSearch);
        btnSearch.setBounds(410, 170, 70, 29);

        dateTo.setDateFormatString("d MMM yyyy");
        dateTo.setDate(date);
        jPanel1.add(dateTo);
        dateTo.setBounds(150, 200, 210, 30);

        jScrollPane1.setBackground(new java.awt.Color(255, 255, 255));

        jTable_Attendance.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(jTable_Attendance);

        jPanel1.add(jScrollPane1);
        jScrollPane1.setBounds(10, 250, 510, 430);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 525, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 685, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        findAttendance();
    }//GEN-LAST:event_btnSearchActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        AFIS obj = new AFIS();
        obj.setVisible(true);
    }//GEN-LAST:event_formWindowClosing

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
            java.util.logging.Logger.getLogger(ViewAttendance.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ViewAttendance.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ViewAttendance.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ViewAttendance.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ViewAttendance().setVisible(true);
                JDateChooser chooser = new JDateChooser();
                JTextFieldDateEditor editor = (JTextFieldDateEditor) chooser.getDateEditor();
                editor.setEditable(false);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnSearch;
    private com.toedter.calendar.JDateChooser dateFrom;
    private com.toedter.calendar.JDateChooser dateTo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable_Attendance;
    private javax.swing.JLabel labelTime;
    // End of variables declaration//GEN-END:variables
}
