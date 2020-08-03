package com.lbmotion.whatitsays.communication;

public class VerifyDelivery extends Base {
	private String file;
	private String authority;
	public boolean 				arrived = false;
	public short 				port;
	
	public VerifyDelivery(String file, String authority, short port) {
		this.file = file;
		this.authority = authority;
		this.port = port;
	}

	public static VerifyDelivery doCheck(String file, String authority, short port) {
		VerifyDelivery me = new VerifyDelivery(file,authority,port);
		me.start();
//		communicationThreads.addElement(me);
		try {
			while (!me.isDone.get()) {
				try {
                    Thread.sleep(250);} catch (InterruptedException ie) {}
			}
		}
		catch (Exception ie) {}
		catch (Error ie) {}
//		communicationThreads.removeElement(me);
		return me;
	}

	public void run() {
		try {
			openBackgroundVerify(port);
			sender("VER"+"version"+"\f"+version+"\f"+"authority"+"\f"+authority+"\f"+"file"+"\f"+file);
			helperTimer.schedule(new CancelCommunicationTask(this),12000);
			readServer();
			helperTimer.cancel();
			close(true);
			if(bufferChar != null && bufferChar.length == 2 && bufferChar[0] == 'O' && bufferChar[1] == 'K') {
				arrived = true;
				result = true;
			}
			if(bufferChar != null && bufferChar.length == 2 && bufferChar[0] == 'N' && bufferChar[1] == 'Y') { 
				arrived = false;
				result = true;
			}
			if(bufferChar != null && bufferChar.length == 2 && bufferChar[0] == 'E' && bufferChar[1] == 'R')
				errorFromServer = true;
		}
		catch (Exception e) {close(false);}
		isDone.set(true);
	}	
}
