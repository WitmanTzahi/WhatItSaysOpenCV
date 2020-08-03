package com.lbmotion.whatitsays.communication;

public class SendCrash extends Base {
	private String mail;
	private String authority;
	
	public SendCrash(String mail, String authority) {
		this.mail = mail;
		this.authority = authority;
	}

	public static SendCrash send(String mail, String authority) {
		SendCrash me = new SendCrash(mail,authority);
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
			openBackground();
			sender("MAI"+"version"+"\f"+version+"\f"+"authority"+"\f"+authority+"\f"+"mail"+"\f"+mail);
			helperTimer.schedule(new CancelCommunicationTask(this),12000);
			readServer();
			helperTimer.cancel();
			close(true);
			if(bufferChar != null && bufferChar.length == 2 && bufferChar[0] == 'E' && bufferChar[1] == 'R')
				errorFromServer = true;
		}
		catch (Exception e) {
			close(false);
		}
		isDone.set(true);
	}	
}
