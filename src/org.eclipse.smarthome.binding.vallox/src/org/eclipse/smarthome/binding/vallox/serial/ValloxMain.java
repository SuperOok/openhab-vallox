package org.eclipse.smarthome.binding.vallox.serial;

public class ValloxMain {

	public static void main(String[] args) {
		ValloxSerialInterface vallox = new ValloxSerialInterface();
		try {
			vallox.connect("lwip", 26);
			vallox.startListening();
			Thread.sleep(10000);
			vallox.send(Variable.FAN_SPEED.key, Telegram.convertBackFanSpeed((byte)4));
			Thread.sleep(5000);
			System.out.println(vallox.getValloxStore().toString());
			vallox.stopListening();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			vallox.close();
			System.out.println("Done.");
		}
	}

}
