package com.lbmotion.whatitsays.communication;

//import com.lbmotion.util.LocationDetection;

public class SendAlert extends Base {
	private String pakachCode;
	private String authority;
	private String address;
	private byte command;
	public char response = '0';
	private double x;
	private double y;
	
	public SendAlert(String pakachCode, String authority, String address) {
		this.pakachCode = pakachCode;
		this.authority = authority;
		this.address = address;
		command = 0;
	}

	public static SendAlert doSend(String pakachName, String authority, String address) {
		SendAlert me = new SendAlert(pakachName,authority,address);
		me.start();
//		communicationThreads.addElement(me);
		while(!me.isDone.get()) {
			try {
                Thread.sleep(250);}catch (InterruptedException ie) {}
		}
//		communicationThreads.removeElement(me);
		return me;
	}

	public void run() {
		try {
			try {
				y = 0;//LocationDetection.getLocation().getLatitude();
				x = 0;//LocationDetection.getLocation().getLongitude();
				command = 1;
			}
			catch (Exception e) {}
			open();
			if(pakachCode == null || pakachCode.length() == 0)
				pakachCode = " ";
			pakachCode = codedRequest(pakachCode);
			if(command == 0)
				sender("_LM"+"version"+"\f"+version+"\f"+"authority"+"\f"+authority+"\f"+"pakachCode"+"\f"+pakachCode+"\f"+'@'+Base.codedRequest(address)+"\n");
			else
				sender("_LN"+"version"+"\f"+version+"\f"+"authority"+"\f"+authority+"\f"+"pakachCode"+"\f"+pakachCode+"\f"+"x"+"\f"+x+"\f"+"y"+"\f"+y+"\f"+'@'+Base.codedRequest(address)+"\n");
			outputStream.flush();
			helperTimer.schedule(new CancelCommunicationTask(this),120000);
			readServer();
			helperTimer.cancel();
			if(bufferChar != null) {
				response = bufferChar[0];
				result = true;
			}
			close(true);
		}
		catch (Exception e) {close(false);}
		isDone.set(true);
	}	

}
