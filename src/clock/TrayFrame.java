package clock;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.TrayIcon;

import javax.swing.ImageIcon;
import javax.swing.JFrame;


public class TrayFrame extends JFrame{

	private final String NAME = "Alarm clock";

	private SystemTray systemTray = SystemTray.getSystemTray();
	private TrayIcon trayIcon; 

	
	public TrayFrame() {
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setResizable(false);
		setVisible(true);

		Image tray = new ImageIcon(getClass()
				.getResource("/resource/tray.png")).getImage();

		trayIcon = new TrayIcon(tray,NAME);
		trayIcon.addActionListener(event -> removeTrayIcon());
		
		addWindowStateListener(e -> {
			if(e.getNewState() == JFrame.ICONIFIED)
			{
				setVisible(false);
				addTrayIcon();
			}
		});
	}
	
	protected void removeTrayIcon() {
		this.setVisible(true);
		this.setState(JFrame.NORMAL);
		systemTray.remove(trayIcon);
	}
	
	private void addTrayIcon() {
		try	{
			systemTray.add(trayIcon);
			trayIcon.displayMessage(NAME, "Double click to show",
					TrayIcon.MessageType.INFO);
		}
		catch (AWTException ex)
		{ex.printStackTrace();}
	}
}
