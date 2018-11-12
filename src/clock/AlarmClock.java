package clock;

import javax.swing.JTextField;
import java.time.LocalTime;

public class AlarmClock {

	private LocalTime alarmTime;
	
	private boolean message = false;
	private boolean ready = false;
	
	public AlarmClock() {}
	
	protected void setAlarm(JTextField hours, JTextField minutes, JTextField seconds) {
		int hour = Integer.valueOf(hours.getText());
		int minute = Integer.valueOf(minutes.getText());
		int second = Integer.valueOf(seconds.getText());
		alarmTime = LocalTime.of(hour, minute, second);

		ready = true;
	}
	
	public boolean isMessage() {
		return message;
	}

	public void setMessage(boolean message) {
		this.message = message;
	}

	public boolean isReady() {
		return ready;
	}

	public void setReady(boolean ready) {
		this.ready = ready;
	}

	protected void compareTime() {
		if(ready) {
			LocalTime now = LocalTime.now();
			int nowSum = now.getHour() + now.getMinute() + now.getSecond();
			int alarmSum =
					alarmTime.getHour() + alarmTime.getMinute() + alarmTime.getSecond();
			message = (nowSum == alarmSum);
		}
	}
}
