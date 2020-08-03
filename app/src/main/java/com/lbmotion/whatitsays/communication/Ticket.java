package com.lbmotion.whatitsays.communication;


import com.lbmotion.whatitsays.UCApp;
import com.lbmotion.whatitsays.common.BaseActivity;
import com.lbmotion.whatitsays.data.DB;
import com.lbmotion.whatitsays.data.List3;
import com.lbmotion.whatitsays.data.TicketInformation;
import com.lbmotion.whatitsays.util.Util;

import java.util.Date;
import java.util.Vector;

public class Ticket extends Base {
	private TicketInformation ti;
	public short 				portBackgroundUsed;
	private boolean				fromPrint;

	public Ticket(TicketInformation ti,boolean fromPrint) {
		this.ti = ti;
		this.fromPrint = fromPrint;
	}

	public static Ticket doSend(TicketInformation ti) {
		return doSend(ti,false);
	}

	public static Ticket doSend(TicketInformation ti,boolean fromPrint) {
		Ticket me = new Ticket(ti,fromPrint);

		me.start();
//		communicationThreads.addElement(me);
		try {
			while (!me.isDone.get()) {
				try {
                    Thread.sleep(250);} catch (InterruptedException ie) {}
			}
		}
		catch (Exception e) {}
		catch (Error e) {}
//		communicationThreads.removeElement(me);
		return me;
	}

	private String getField(String ss, int length) {
		int maxLength = (int) Math.pow(10,length)-1;
		if(ss.length() > maxLength)
			ss = ss.substring(0,maxLength);
		String s = removeIllegalCharacters(ss);
		String l = s.length()+"";
		while(l.length() < length)
			l = '0'+l;
		return (l+s);
	}

	private String getFeild(int i, int length) {
		String s = i+"";
		return getField(s,length);
	}

	private void setReference(Vector<List3> remarks, int remarkReference1, int remarkReference2, int remarkReference3, int remarkReference4, StringBuffer ticket) {
		String reference1, reference2, reference3, reference4;
		reference1 = reference2 = reference3 = reference4 = (""+ BaseActivity.NO_REFERENCE).length()+""+BaseActivity.NO_REFERENCE;//"79999999";
		try {// Maybe reference table was changed
			if(remarkReference1 != BaseActivity.NO_REFERENCE)
				reference1 = (""+remarks.get(remarkReference1).code).length()+""+remarks.get(remarkReference1).code;
			if(remarkReference2 != BaseActivity.NO_REFERENCE)
				reference2 = (""+remarks.get(remarkReference2).code).length()+""+remarks.get(remarkReference2).code;
			if(remarkReference3 != BaseActivity.NO_REFERENCE)
				reference3 = (""+remarks.get(remarkReference3).code).length()+""+remarks.get(remarkReference3).code;
			if(remarkReference4 != BaseActivity.NO_REFERENCE)
				reference4 = (""+remarks.get(remarkReference4).code).length()+""+remarks.get(remarkReference4).code;
		} 
		catch (Exception ie) {}
		ticket.append(reference1);
		ticket.append(reference2);
		ticket.append(reference3);
		ticket.append(reference4);
	}

	public void run() {
		try {
			StringBuffer ticket = new StringBuffer();
//			
			if(ti.LK <= 0)			
				ti.LK = UCApp.loginData.Lk;
			if(ti.UserCounter < 0)
				ti.UserCounter = UCApp.loginData.UsrCounter;
//
			if(ti.carOrGM) {
				ticket.append("1");//General inspection
				ticket.append(getFeild(ti.LK,1));//Authority number
				ticket.append(getFeild(ti.reasonCode,1));//Cancel code
				if(ti.sSwHatraa) ticket.append("1");else ticket.append("0");
				if(ti.vSwHatraa) ticket.append("1");else ticket.append("0");
				ticket.append(getFeild(ti.UserCounter,1));
				ticket.append(getFeild(ti.dochCode,1));
				ticket.append(getFeild(ti.idType,1));
//				
				if(ti.printDefendantSoFar >= 0 && ti.defendants.size() > ti.printDefendantSoFar) {
					ticket.append(getField(ti.defendants.get(ti.printDefendantSoFar).id,2));
					ticket.append(getField(ti.defendants.get(ti.printDefendantSoFar).last,2));
					ticket.append(getField(ti.defendants.get(ti.printDefendantSoFar).name,2));
					ticket.append(getField(ti.defendants.get(ti.printDefendantSoFar).street,2));
					ticket.append(getField(ti.defendants.get(ti.printDefendantSoFar).streetCode,2));
					ticket.append(getField(ti.defendants.get(ti.printDefendantSoFar).number,2));
					ticket.append(getField(ti.defendants.get(ti.printDefendantSoFar).flat,2));
					ticket.append(getField(ti.defendants.get(ti.printDefendantSoFar).cityCode,2));
					ticket.append(getField(ti.defendants.get(ti.printDefendantSoFar).cityName,2));
					ticket.append(getField(ti.defendants.get(ti.printDefendantSoFar).zipcode,2));
					ticket.append(getField(ti.defendants.get(ti.printDefendantSoFar).box,2));
				}
				else {
					ticket.append(getField("",2));
					ticket.append(getField("",2));
					ticket.append(getField("",2));
					ticket.append(getField("",2));
					ticket.append(getField("",2));
					ticket.append(getField("",2));
					ticket.append(getField("",2));
					ticket.append(getField("",2));
					ticket.append(getField("",2));
					ticket.append(getField("",2));
					ticket.append(getField("",2));
				}
//
				ticket.append(getField(ti.witness.id,2));
				ticket.append(getField(ti.witness.name,2));
				ticket.append(getField(ti.witness.last,2));
				ticket.append(getField(ti.witness.street,2));
				ticket.append(getField(ti.witness.number,2));
				ticket.append(getField(ti.witness.city,2));
				ticket.append(getField(ti.witness.zipcode,2));
				ticket.append(getField(ti.witness.flat,2));
//
				ticket.append(getField(ti.vehicle.licence,2));
				ticket.append(getFeild(ti.vehicle.type,2));
				ticket.append(getFeild(ti.vehicle.color,2));
				ticket.append(getFeild(ti.vehicle.manufacturer,2));
//				
				if(ti.warningDate.length() > 0) {//If warning
					ticket.append("0");                
					ticket.append(getField(ti.warningDate,2));
					ticket.append(getFeild(ti.warningHours,1));
				}   
				else {
					ticket.append("1");//If trial
					ticket.append(getField(ti.trialDate,2));
					ticket.append(getField(ti.trialHour,2));
					ticket.append(getFeild(ti.trialC,2));
				}
//
				setReference(DB.citizenRemarks,ti.citizenRemarkReference1,ti.citizenRemarkReference2,ti.citizenRemarkReference3,ti.citizenRemarkReference4,ticket);
				setReference(DB.remarks,ti.remarkReference1,ti.remarkReference2,ti.remarkReference3,ti.remarkReference4,ticket);
//				
				ticket.append(getFeild(ti.violationListCode,1));
				ticket.append(getFeild(ti.D,1));
				ticket.append(getFeild(ti.P,1));
				ticket.append(getField(ti.DochKod,1));
				ticket.append(getFeild(ti.numAzhara,1));
				ticket.append(getField(ti.streetNumber,1));
				if (ti.streetByAcrossIn < DB.insideCodes.size())
					ticket.append(getFeild(DB.insideCodes.get(ti.streetByAcrossIn).C,1));
				else
					ticket.append(getFeild(0,1));
//
				ticket.append(getFeild(ti.pictures.size(),2));
				for(short i = 0;i < ti.pictures.size();i++)
					ticket.append(getField(ti.pictures.elementAt(i),2));
//				
				ticket.append(getFeild(ti.sounds.size(),2));
				for(short i = 0; i < ti.sounds.size();i++)
					ticket.append(getField(ti.sounds.elementAt(i),2));
//				
				ticket.append(getField(ti.date,2));
				ticket.append(getFeild(ti.streetCode,1));
				while(ti.pakachFreeText.indexOf('\n') != -1)
					ti.pakachFreeText = ti.pakachFreeText.replace('\n',' ');
				if(UCApp.loginData.authority.equals("9700")) {
					if (ti.dochBedihavad && ti.pakachFreeText.indexOf("השלמת פרטים") == -1)
						ti.pakachFreeText = "\n" + "השלמת פרטים " + ti.pakachFreeText;
				}
				else {
					if (ti.dochBedihavad && ti.pakachFreeText.indexOf("דוח בדיעבד") == -1)
						ti.pakachFreeText = "דוח בדיעבד " + ti.pakachFreeText;
				}
				if(ti.docPehula != null && ti.docPehula.length() > 0)
					ti.pakachFreeText += " " + "דוח פעולה:" + ti.docPehula;
				if(ti.quontity.length() > 0)
					ticket.append(getField((ti.pakachFreeText+" מיכפלה לקנס "+ti.quontity),4));
				else
					ticket.append(getField(ti.pakachFreeText,4));
				while(ti.citizenFreeText.indexOf('\n') != -1)
					ti.citizenFreeText = ti.citizenFreeText.replace('\n',' ');
				ticket.append(getField(ti.citizenFreeText,4));
				ticket.append(getField(ti.animalId,2));
				ticket.append(getField(ti.latitude+"",2));
				ticket.append(getField(ti.longitude+"",2));
				if(ti.sendReportC < 0 || ti.sendReportC >= DB.nimhans.size())
					ticket.append(getField("0",1));
				else
					ticket.append(getField(DB.nimhans.get(ti.sendReportC).C+"",1));
				ticket.append(getField(ti.subItem,3));
				if(ti.openEvent) 
					ticket.append("1");
				else 
					ticket.append("0");
				if(ti.selectedHandicapPicture != -1)
					ticket.append(""+(ti.selectedHandicapPicture+1));
				else 
					ticket.append("0");
				if(ti.packachDecisionCode != -1)
					ticket.append(""+(ti.packachDecisionCode));
				else 
					ticket.append("0");
				ticket.append(getField(ti.filename,2));
				if(ti.printDefendantSoFar == 0 && ti.defendants.size() == 1)
					ticket.append(getField("3",1));
				else if(ti.printDefendantSoFar == 0 && ti.defendants.size() > 1)
					ticket.append(getField("1",1));
				else if(ti.printDefendantSoFar > 0 && ti.defendants.size() > 1)
					ticket.append(getField("2",1));
				else
					ticket.append(getField(" ",1));
				ticket.append(getField(ti.recipientId,2));
				ticket.append(getField(ti.recipientFirstName,2));
				ticket.append(getField(ti.recipientLastName,2));
				ticket.append(getField(ti.TasksC,2));
				ticket.append(ti.sendNewRemark?"1":"0");
				ticket.append(getField(ti.gosh,1));
				ticket.append(getField(ti.gosh,1));
			}
			else {
				long timestamp = ti.checkLincenseTime;
				if(timestamp == 0)
					timestamp = ti.timestamp;
/*				
				DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
				String printedTime = df.format(new Date(timestamp));
*/				
				String minute = Util.getMinute(new Date(timestamp))+"";
				if(minute.length() < 2) 
					minute = '0'+minute;
				String hour = Util.getHour(new Date(timestamp))+"";
				if(hour.length() < 2)
					hour = '0'+hour;
				String day = Util.getDay(new Date(timestamp))+"";
				if(day.length() < 2)
					day = '0'+day;
				String month = (Util.getMonth(new Date(timestamp))+1)+"";
				if(month.length() < 2)
					month = '0'+month;
				String year = (2000+Util.getYear(new Date(timestamp)))+"";
				if(year.length() < 2)
					year = '0'+year;
				String printedTime = day+"/"+month+"/"+year+" "+hour+":"+minute+":00";
//				
				ticket.append("0");//Parking inspection
				ticket.append(getFeild(ti.LK,1));
				ticket.append(getFeild(ti.reasonCode,1));//Cancel code
				if(ti.sSwHatraa) ticket.append("1");else ticket.append("0");
				if(ti.vSwHatraa) ticket.append("1");else ticket.append("0");
				ticket.append(getFeild(ti.UserCounter,1));
				ticket.append(getFeild(ti.dochCode,1));
				ticket.append(getField(ti.CarInformationId,1));
				ticket.append(getFeild(ti.CarInformationColor,1));
				ticket.append(getFeild(ti.CarInformationType,1));
				ticket.append(getFeild(ti.CarInformationManufacturer,1));
				ticket.append(getFeild(ti.violationListCode,1));
				ticket.append(getFeild(ti.D,1));
				ticket.append(getFeild(ti.P,1));
				ticket.append(getField(ti.streetNumber,1));
//
				setReference(DB.remarks,ti.remarkReference1,ti.remarkReference2,ti.remarkReference3,ti.remarkReference4,ticket);
//
				if (ti.streetByAcrossIn < DB.insideCodes.size())
					ticket.append(getFeild(DB.insideCodes.get(ti.streetByAcrossIn).C,1));
				else
					ticket.append(getFeild(0,1));
//
				ticket.append(getFeild(ti.pictures.size(),2));
				for(short i = 0;i < ti.pictures.size();i++)
					ticket.append(getField(ti.pictures.elementAt(i),2));
//				
				ticket.append(getFeild(ti.sounds.size(),2));
				for(short i = 0; i < ti.sounds.size();i++)
					ticket.append(getField(ti.sounds.elementAt(i),2));
//
				ticket.append(getField(ti.date,2));
				ticket.append(getFeild(ti.streetCode,1));
				ticket.append(getField(ti.parkingNumber1,1));
				ticket.append(getField(ti.parkingNumber2,1));
				while(ti.pakachFreeText.indexOf('\n') != -1)
					ti.pakachFreeText = ti.pakachFreeText.replace('\n',' ');
				ticket.append(getField(ti.pakachFreeText,4));
				ticket.append(getField(ti.uuid,2));
				ticket.append(getField(ti.checkLincenseTime+"",2));
				ticket.append(getField(ti.latitude+"",2));
				ticket.append(getField(ti.longitude+"",2));
				if(ti.sendReportC < 0 || ti.sendReportC >= DB.nimhans.size())
					ticket.append(getField("0",1));
				else
					ticket.append(getField(DB.nimhans.get(ti.sendReportC).C+"",1));
				ticket.append(getField(ti.subItem,3));
				if(ti.packachDecisionCode != -1)
					ticket.append(""+(ti.packachDecisionCode));
				else 
					ticket.append("0");
				if(ti.selectedHandicapPicture != -1)
					ticket.append(""+(ti.selectedHandicapPicture+1));
				else 
					ticket.append("0");
				ticket.append(getField(ti.filename,2));
				ticket.append(getField(printedTime,2));
				ticket.append(getField(ti.SwCellParkingPaid,2));
				ticket.append(getField(asciiToHex(ti.picUrl),4));
				ticket.append(ti.sendNewRemark?"1":"0");
				ticket.append(getField(ti.gosh,1));
				ticket.append(getField(ti.gosh,1));
			}
			openBackground();
			portBackgroundUsed = lastPortBackground;
			if(fromPrint)
				sender("BZ6"+"version"+"\f"+version+"\f"+'@'+Base.codedRequest(ticket.toString()));
			else
				sender("BZ5"+"version"+"\f"+version+"\f"+'@'+Base.codedRequest(ticket.toString()));
			helperTimer.schedule(new CancelCommunicationTask(this),12000);
			readServer();
			helperTimer.cancel();
			close(true);
			if(bufferChar != null && bufferChar.length == 2 && bufferChar[0] == 'O' && bufferChar[1] == 'K')
				result = true;
			if(bufferChar != null && bufferChar.length == 2 && bufferChar[0] == 'E' && bufferChar[1] == 'R')
				errorFromServer = true;
		}
		catch (Exception e) {
			errorOccurred = true;
			close(false);
		}
		isDone.set(true);
	}

	private String asciiToHex(String asciiStr) {
		char[] chars = asciiStr.toCharArray();
		StringBuilder hex = new StringBuilder();
		for (char ch : chars) {
			hex.append(Integer.toHexString((int) ch));
		}
		return hex.toString();
	}
}
