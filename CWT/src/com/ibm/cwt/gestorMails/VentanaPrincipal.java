package com.ibm.cwt.gestorMails;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.logging.Handler;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.ibm.cwt.exceptions.CWTDAOException;
import com.ibm.cwt.exceptions.CWTEmailException;
import com.ibm.cwt.exceptions.CWTInvalidCredentialsException;
import com.ibm.cwt.exceptions.CWTReportSaveException;
import com.ibm.cwt.services.ReportService;

public class VentanaPrincipal extends JFrame implements ActionListener, MouseListener {
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(VentanaPrincipal.class.getName());
	private int anchoV;
	private int altoV;
	
	public static void main(String[] args) {
		VentanaPrincipal panelSup = new VentanaPrincipal();
		JTextField txuser = new JTextField(15);
		JPasswordField pass = new JPasswordField(15);
		
		JLabel runningLabel = new JLabel("Running report...");
		runningLabel.setBounds(220, 100, 200, 30);
		runningLabel.setVisible(false);
		panelSup.add(runningLabel);
		
		JButton runButton = new JButton("Run report");
		runButton.setBounds(50, 100, 160, 30);
		runButton.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent event) {
		        
		        try {
		        	runningLabel.setVisible(true);
		        	panelSup.repaint();
		        	Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
		        	panelSup.setCursor(hourglassCursor);
		        	LOGGER.info("Initiating report");
					new ReportService().runReport(txuser.getText(), pass.getPassword());
					JOptionPane.showMessageDialog(panelSup, "Report finished", "Success", JOptionPane.INFORMATION_MESSAGE);
					LOGGER.info("Report finished");
					LOGGER.finest("Closing app");
	                for(Handler h : LOGGER.getHandlers()) {
	                    h.close();
	                }
	                System.exit(0);
					
				} catch (CWTDAOException e) {
					JOptionPane.showMessageDialog(panelSup, "Error connecting to DB. Report canceled. Try again later", "Error", JOptionPane.ERROR_MESSAGE);
				} catch (CWTReportSaveException e) {
					JOptionPane.showMessageDialog(panelSup, "Error saving report to file. Notifications were sent anyway", "Error", JOptionPane.ERROR_MESSAGE);
				} catch (CWTEmailException e) {
					JOptionPane.showMessageDialog(panelSup, "Error sending notifications. Try again later", "Error", JOptionPane.ERROR_MESSAGE);
				} catch (CWTInvalidCredentialsException e) {
					JOptionPane.showMessageDialog(panelSup, "Invalid credentials. Verify user and password", "Error", JOptionPane.ERROR_MESSAGE);
				} finally {
					Cursor normalCursor = new Cursor(Cursor.DEFAULT_CURSOR);
					panelSup.setCursor(normalCursor);
					runningLabel.setVisible(false);
				}
		   }
		 });
		
		panelSup.add(runButton);
		
		JLabel idLabel = new JLabel("ID:");
		idLabel.setBounds(10, 10, 100, 30);
		panelSup.add(idLabel);
	    JLabel pwdLabel = new JLabel("Password:");
	    pwdLabel.setBounds(10, 40, 100, 30);
	    panelSup.add(pwdLabel);
		
		
		txuser.setBounds(100, 10, 250, 30);
        
        pass.setBounds(100, 40, 250, 30);
	    panelSup.add(txuser);
	    panelSup.add(pass);
	    
		panelSup.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                LOGGER.finest("Closing app");
                for(Handler h : LOGGER.getHandlers()) {
                    h.close();
                }
                System.exit(0);
            }
        });
		
		panelSup.setTitle("Warning claim Tool");
		BorderLayout layoutPrin = new BorderLayout();
		panelSup.setLayout(layoutPrin);		
		//panelSup.add(panelSup,BorderLayout.CENTER);
		panelSup.setVisible(true);
	}

	public VentanaPrincipal() {
		dimensionarYPosicionar();
		//this.getContentPane().setBackground(Color.RED);
	}

	
	private void dimensionarYPosicionar(){
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		anchoV = 440;
		altoV = 200;
		this.setLocation((int)screenSize.getWidth()/2-anchoV/2, (int)screenSize.getHeight()/2-altoV/2);
		this.setSize(anchoV, altoV);
		
		
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void actionPerformed(ActionEvent e) {}

}
