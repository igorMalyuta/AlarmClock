package clock;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.*;
import java.io.IOException;
import java.time.LocalTime;
import java.util.concurrent.TimeUnit;

import javax.sound.sampled.*;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import com.jtattoo.plaf.noire.NoireLookAndFeel;

public class Main implements Runnable{

	private Font font = new Font("Verbana", Font.ITALIC, 20);
	private Font buttonFont = new Font("Arial", Font.BOLD, 15);
	
	private TrayFrame frame;

	private JPanel panel;
	private JPanel panel2;
	private JLabel showTime;
	private ImageIcon icon = new ImageIcon(getClass()
			.getResource("/resource/clock2.png"));
	
	private JTextField fieldHour;
	private JTextField fieldMinute;
	private JTextField fieldSecond;
	
	private JButton setButton;

	private AlarmClock alarmClock = new AlarmClock();


	private void addButton() {
		setButton = new JButton("Turn on");
		setButton.setFont(buttonFont);
		setButton.setHorizontalAlignment(JButton.CENTER);
		
		setButton.addActionListener(event -> {
			alarmClock.setAlarm(fieldHour, fieldMinute,fieldSecond);

			fieldHour.setFocusable(true);
			fieldMinute.setFocusable(true);
			fieldSecond.setFocusable(true);

			isIcon(alarmClock.isReady());
		});
	}
	
	private PlainDocument getPlain() {
		return new PlainDocument() {
			@Override
			public void insertString(int offset, String str,
					AttributeSet attr) throws BadLocationException {
				if (str == null)
					return;
				if ((getLength() + str.length()) <= 2) {
					super.insertString(offset, str, attr);
				}
			}
		};
	}

	private void addTextField() {
		fieldHour = new JTextField(2);
		fieldHour.setFont(font);
		fieldHour.setDocument(getPlain());
		fieldHour.addKeyListener(new TextFieldsListener(fieldHour));
		fieldHour.requestFocus(true);
		
		fieldMinute = new JTextField(2);
		fieldMinute.setFont(font);
		fieldMinute.setDocument(getPlain());
		fieldMinute.addKeyListener(new TextFieldsListener(fieldMinute));
		
		fieldSecond = new JTextField(2);
		fieldSecond.setFont(font);
		fieldSecond.setDocument(getPlain());
		fieldSecond.addKeyListener(new TextFieldsListener(fieldSecond));
	}
	
	private void cleanFields() {
		fieldHour.setText("");
		fieldHour.requestFocus(true);

		fieldMinute.setText("");
		fieldSecond.setText("");
	}
	
	private void addLabel() {	
		showTime = new JLabel();
		setLabelText();
		showTime.setFont(font);
		showTime.setHorizontalAlignment(JLabel.CENTER);
	}
	
	private void setLabelText() {
		LocalTime time = LocalTime.now();
		
		String h = time.getHour() >= 10 ?
				String.valueOf(time.getHour()) : 0 + String.valueOf(time.getHour());

		String m = time.getMinute() >= 10 ?
				String.valueOf(time.getMinute()) : 0 + String.valueOf(time.getMinute());

		String s = time.getSecond() >= 10 ?
				String.valueOf(time.getSecond()) : 0 + String.valueOf(time.getSecond());
		
		showTime.setText(h + ":" + m + ":" + s);
	}
	
	private void isIcon(boolean flag) {
		if(flag)
			showTime.setIcon(icon);
		else
			showTime.setIcon(null);
	}
	
	private void addPanel() {
		panel = new JPanel();
		panel.setSize(150, 100);
		panel.setLayout(new BorderLayout());
		panel.setBorder(BorderFactory.createEtchedBorder());
		panel.add(showTime);
		
		panel2 = new JPanel();
		panel2.setSize(150, 100);
		panel2.setBorder(BorderFactory.createEtchedBorder());
		panel2.add(fieldHour);
		panel2.add(fieldMinute);
		panel2.add(fieldSecond);
		panel2.add(setButton);
	}
	
	private void addFrame() {
		frame = new TrayFrame();
		frame.setTitle("Alarm clock");
		frame.setSize(200, 200);
		frame.setLayout(new GridLayout(2,0));
		frame.setLocationRelativeTo(null);
				
		frame.add(panel);
		frame.add(panel2);
	     
		frame.setIconImage(new ImageIcon(getClass()
				.getResource("/resource/clock.png")).getImage());
	}
	
	public Main() {
		addButton();
		addLabel();
		addTextField();
		addPanel();
		addFrame();
		
		new Thread(this).start();
	}
	
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(new NoireLookAndFeel());
		} catch (UnsupportedLookAndFeelException e) {}
		
		JFrame.setDefaultLookAndFeelDecorated(true);

		SwingUtilities.invokeLater(Main::new);
	}

	@Override
	public void run() {
		while(true) {
			setLabelText();
			alarmClock.compareTime();
			
			if(alarmClock.isMessage()) {
				frame.removeTrayIcon();

				try {
					Clip clip = AudioSystem.getClip();
					AudioInputStream auInStream =
							AudioSystem.getAudioInputStream(Main.class.getResourceAsStream("/resource/ring.wav"));

					clip.open(auInStream);
					clip.start();

					JOptionPane.showMessageDialog(frame,
							"Adventure Time!", "Alarm clock",
							JOptionPane.INFORMATION_MESSAGE);
					clip.stop();

				} catch (LineUnavailableException | UnsupportedAudioFileException | IOException e) {
					e.printStackTrace();
				}

				alarmClock.setMessage(false);
				alarmClock.setReady(false);
				
				isIcon(alarmClock.isReady());
				cleanFields();
			}
			
			try {
				Thread.sleep(500);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	class TextFieldsListener extends KeyAdapter {

		private JTextField jTextField;
		private int i = 0;

		TextFieldsListener(JTextField field) {
			jTextField = field;
		}

		@Override
		public void keyTyped(KeyEvent e) {
			i++;

			if(i == 2) {
				jTextField.setFocusable(false);
				i = 0;
			}
		}
	}
}
