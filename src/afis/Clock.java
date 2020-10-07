/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package afis;

import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

/**
 *
 * @author Stefanos Tzortzoglou
 */
public class Clock extends Thread
{
    private JLabel label;
    
    public Clock(JLabel label)
    {
        //Updates the value with the time.
        this.label = label;
    }
    
    //Method that loads the thread.
    public void run()
    {
        while(true)
        {
            Date current_time = new Date();
            SimpleDateFormat theformat = new SimpleDateFormat("dd-MMM-yyyy   HH:mm:ss");
            label.setText(theformat.format(current_time));
            try{
                sleep(1000);
            }catch (Exception ex) {
                JOptionPane.showMessageDialog(null,"Error! Time update isn't available" + ex);
            }
        }
    }
}
