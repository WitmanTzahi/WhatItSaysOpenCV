package com.lbmotion.whatitsays.communication;

public class SendError extends Base {
	private String error;
	private String user;
	private String authority;

	public SendError(String authority, String user, String error) {
		this.error = error;
		this.user = user;
		this.authority = authority;
	}

	public static SendError send(String authority, String user, String error) {
		SendError me = new SendError( authority,user,error);
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
			sender("SEM"+"version"+"\f"+version+"\f"+"authority"+"\f"+authority+"\f"+"user"+"\f"+user+"\f"+"error"+"\f"+error);
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
