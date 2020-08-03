	/** If the defendant ID was entered, sends a query to get his/her information.
	 *  If no ID was entered, sends a query to get the information about the people living in the building.
	 *  If the witnesse's ID was entered, sends query to get his/her information.
	 *  If the car licence number is present, sends a query to get its information.
	 * @param Lk    authority code
	 * @param AveraC    violation code
	 * @param StreetC   street name
	 * @param HouseNo   house number
	 * @param TeudatZeut   defendant's id
	 * @param TzHed witness's id
	 * @param CarNo car licence number
	 * @param MikumC    car position parameter  
	 * @return true if all the needed queries were successful, false otherwise      
	 */
package com.lbmotion.whatitsays.communication;

    import android.util.Log;

    import com.lbmotion.whatitsays.UCApp;
    import com.lbmotion.whatitsays.data.DB;
    import com.lbmotion.whatitsays.data.Defendant;
    import com.lbmotion.whatitsays.data.Vehicle;
    import com.lbmotion.whatitsays.data.Witness;

    public class LoadDetails extends Base {
        private final String TAG = "LoadDetails";
        public static LoadDetails 		me = null;

        private String Lk;
        private String username;
        private int 			AveraC;
        private int 			StreetC;
        private String HouseNo;
        private String TeudatZeut;
    //	private String 			TzHed;
        private String CarNo;
        private byte 			MikumC;
        private int 			type;
        private String firstName;
        private String lastName;
        private String companyName;

        //	public static boolean doLoadDetails(String Lk, String username, int AveraC, int StreetC,String HouseNo, String TeudatZeut,String TzHed, String CarNo, byte MikumC,int type) {
        public static boolean doLoadDetails(String Lk, String username, int AveraC, int StreetC, String HouseNo, String TeudatZeut, String CarNo, byte MikumC, int type, String firstName, String lastName, String companyName) {
            UCApp.showPopupText = UCApp.showLoadDetails = true;
    //		me = new LoadDetails(Lk, username, AveraC, StreetC, HouseNo, TeudatZeut, TzHed, CarNo, MikumC,type);
            me = new LoadDetails(Lk, username, AveraC, StreetC, HouseNo, TeudatZeut, CarNo, MikumC,type,firstName, lastName, companyName);
            me.start();
            communicationThreads.addElement(me);
            while(!me.isDone.get()) {
                try {
                    Thread.sleep(250);} catch (InterruptedException ie) {}
            }
            try {communicationThreads.removeElement(me);} catch (Exception e) {}
            return !me.errorOccurred;
        }

        public static void abort() {
            try {
                if(me != null)
                    me.doAbort();
            }
            catch (Exception e) {}
        }

    //	public LoadDetails(String Lk, String username, int AveraC, int StreetC,String HouseNo, String TeudatZeut,String TzHed, String CarNo, byte MikumC,int type) {
        public LoadDetails(String Lk, String username, int AveraC, int StreetC, String HouseNo, String TeudatZeut, String CarNo, byte MikumC, int type, String firstName, String lastName, String companyName) {
            this.Lk 		= Lk;
            this.username	= username;
            this.AveraC 	= AveraC;
            this.StreetC 	= StreetC;
            this.HouseNo 	= HouseNo;
            this.TeudatZeut = TeudatZeut;
    //		this.TzHed 		= TzHed;
            this.CarNo 		= CarNo;
            this.MikumC 	= MikumC;
            this.type 		= type;
            this.firstName  = firstName;
            this.lastName   = lastName;
            this.companyName= companyName;
        }

        public void run() {
            Log.i(TAG, "run()");
            try {
                DB.witness = new Witness();
                DB.defendant = new Defendant();
                DB.car = new Vehicle();
                errorOccurred = false;
                open();
    //			sender("_DN"+"version"+"\f"+version+"\f"+"username"+"\f"+username+"\f"+"authority"+"\f"+Lk+"\f"+
    //					"AveraC"+"\f"+AveraC+"\f"+"StreetC"+"\f"+StreetC+"\f"+"HouseNo"+"\f"+HouseNo+"\f"+"MikumC"+"\f"+MikumC+"\f"+
    //					"TeudatZeut"+"\f"+TeudatZeut+"\f"+"TzHed"+"\f"+TzHed+"\f"+"CarNo"+"\f"+CarNo+"\f"+"Type"+"\f"+type+"\f");
                if(type == 3) {
                    firstName = companyName;
                    lastName = "";
                }
                sender("_DS"+"version"+"\f"+version+"\f"+"username"+"\f"+username+"\f"+"authority"+"\f"+Lk+"\f"+
                        "AveraC"+"\f"+AveraC+"\f"+"StreetC"+"\f"+StreetC+"\f"+"HouseNo"+"\f"+HouseNo+"\f"+"MikumC"+"\f"+MikumC+"\f"+
                        "TeudatZeut"+"\f"+TeudatZeut+"\f"+"TzHed"+"\f\f"+"CarNo"+"\f"+CarNo+"\f"+"Type"+"\f"+type+"\f"+
                        "firstName"+"\f"+Base.codedRequest(firstName)+"\f"+"lastName"+"\f"+Base.codedRequest(lastName)+"\f"+"User"+"\f"+UCApp.loginData.UsrCounter+"\f");
                helperTimer.schedule(new CancelCommunicationTask(this),60000);
                readServer();
                helperTimer.cancel();
                close(true);
                if(bufferChar != null) {
                    errorOccurred = false;
                    if(TeudatZeut.length() > 0)
                        getDefendantInfo();
                    else if(firstName.length()+lastName.length() > 0)
                        getBuildingInfoName();
                    else if(CarNo.length() > 0)
                        getCarInfo();
                    else
                        getBuildingInfoAddress();
    //				if(TzHed.length() > 0)
    //					getWitnessInfo();
                }
            }
            catch (Exception e) {
                errorOccurred = true;
                Log.i(TAG, "run()"+e.getMessage());
                close(false);
            }
            catch (Error e) {
                errorOccurred = true;
                Log.i(TAG, "run()"+e.getMessage());
                close(false);
            }
            isDone.set(true);
        }

        private void getDefendantInfo () {
            String tz = "",last = "", name = "", streetCode = "", streetName = "", number = "", flat = "", cityCode = "", cityName = "", zipcode = "", packachName = "",validUpto = "",chip = "",box = "",TxtPopUp = "",KeshelKod = "", KeshelMsg = "",
                   RightSide = "", LeftSide = "",swExistHistory = "";
            if (bufferChar[i] == '<' && bufferChar[i+1] == 'E' && bufferChar[i+2] == 'r' &&	bufferChar[i+3] == 'r' && bufferChar[i+4] == '>')     {
                i += 5;
            }
            else {
                last = getString(readBytes(2));
                name = getString(readBytes(2));
                streetCode = getString(readBytes(2));
                streetName = getString(readBytes(2));
                number = getString(readBytes(2));
                flat = getString(readBytes(2));
                cityCode = getString(readBytes(2));
                cityName = getString(readBytes(2));
                zipcode = getString(readBytes(2));
                if(zipcode.equals("0"))
                    zipcode = "";
                packachName = getString(readBytes(2));
                validUpto = getString(readBytes(2));
                chip = getString(readBytes(2));
                box = getString(readBytes(2));
                tz = getString(readBytes(2));
                TxtPopUp = getString(readBytes(2));
                KeshelKod = getString(readBytes(2));
                KeshelMsg = getString(readBytes(2));
                RightSide = getString(readBytes(2));
                LeftSide = getString(readBytes(2));
                if(readBytes(1) == 1)
                    swExistHistory = "1";
            }
            if(tz.length() == 0 && type == 1)
                tz = TeudatZeut;
            if(tz.length() == 0 && type == 2)
                chip = TeudatZeut;
            DB.defendant = new Defendant(tz, last, name, streetCode, streetName, number, flat, cityCode, cityName, zipcode,packachName,validUpto,chip,box,TxtPopUp,KeshelKod, KeshelMsg, RightSide, LeftSide,swExistHistory);
        }

        private void getBuildingInfoName() {
            String id, last, name, streetKod, street, streetNo,dira, cityKod, cityNm, zipcode, packachName = "",validUpto = "",chip = "",box = "",TxtPopUp = "",KeshelKod = "", KeshelMsg = "", RightSide = "", LeftSide = "";
            DB.defendantList.removeAllElements();
            while (i< bufferChar.length && bufferChar[i] != '$')   { // '$' is the identifier which shows the end of the list
                if (bufferChar[i] == '<' && bufferChar[i+1] == 'E' && bufferChar[i+2] == 'r' && bufferChar[i+3] == 'r' && bufferChar[i+4] == '>') {
                    i += 5;
                    break;
                }
                else {
                    last = getString(readBytes(2));
                    name = getString(readBytes(2));
                    streetKod = getString(readBytes(2));
                    street = getString(readBytes(2));
                    streetNo = getString(readBytes(2));
                    dira = getString(readBytes(2));
                    cityKod = getString(readBytes(2));
                    cityNm = getString(readBytes(2));
                    zipcode = getString(readBytes(2));
                    if(zipcode.equals("0"))
                        zipcode = "";
                    packachName = getString(readBytes(2));
                    validUpto = getString(readBytes(2));
                    chip = getString(readBytes(2));
                    box = getString(readBytes(2));
                    id = getString(readBytes(2));
                    TxtPopUp = getString(readBytes(2));
                    KeshelKod = getString(readBytes(2));
                    KeshelMsg = getString(readBytes(2));
                    RightSide = getString(readBytes(2));
                    LeftSide = getString(readBytes(2));
                    DB.defendant = new Defendant (id, last,name, streetKod, street, streetNo, dira, cityKod, cityNm, zipcode, packachName, validUpto,chip,box,TxtPopUp,KeshelKod, KeshelMsg, RightSide, LeftSide,"");
                    DB.defendantList.addElement (DB.defendant);
                }
            }
            i++;    // skip the '$' character
        }

        private void getBuildingInfoAddress() {
            String id, last, name, streetKod, street, streetNo,dira, cityKod, cityNm, zipcode, packachName = "",validUpto = "",chip = "",box = "",TxtPopUp = "",KeshelKod = "", KeshelMsg = "", RightSide = "", LeftSide = "";
            DB.defendantList.removeAllElements();
            while (i< bufferChar.length && bufferChar[i] != '$')   { // '$' is the identifier which shows the end of the list
                if (bufferChar[i] == '<' && bufferChar[i+1] == 'E' && bufferChar[i+2] == 'r' && bufferChar[i+3] == 'r' && bufferChar[i+4] == '>') {
                    i += 5;
                    break;
                }
                else {
                    last = getString(readBytes(2));
                    name = getString(readBytes(2));
                    id = getString(readBytes(2));
                    street = getString(readBytes(2));
                    streetNo = getString(readBytes(2));
                    dira = getString(readBytes(2));
                    cityNm = getString(readBytes(2));
                    zipcode = getString(readBytes(2));
                    streetKod = getString(readBytes(2));
                    cityKod = getString(readBytes(2));
                    packachName = getString(readBytes(2));
                    validUpto = getString(readBytes(2));
                    chip = getString(readBytes(2));
                    box = getString(readBytes(2));
                    TxtPopUp = getString(readBytes(2));
                    KeshelKod = getString(readBytes(2));
                    KeshelMsg = getString(readBytes(2));
                    RightSide = getString(readBytes(2));
                    LeftSide = getString(readBytes(2));
                    DB.defendant = new Defendant (id, last,name, streetKod, street, streetNo, dira, cityKod, cityNm, zipcode, packachName, validUpto,chip,box,TxtPopUp,KeshelKod, KeshelMsg, RightSide, LeftSide,"");
                    DB.defendantList.addElement (DB.defendant);
                }
            }
            i++;    // skip the '$' character
        }

        private void getWitnessInfo() {
            String last = "", name = "", street = "", number = "", flat = "", city = "", zipcode = "";
            if (bufferChar[i] == '<' && bufferChar[i+1] == 'E' && bufferChar[i+2] == 'r' && bufferChar[i+3] == 'r' && bufferChar[i+4] == '>') {
                i += 5;
            }
            else {
                last = getString(readBytes(2));
                name = getString(readBytes(2));
                street = getString(readBytes(2));
                number = getString(readBytes(2));
                flat = getString(readBytes(2));
                city = getString(readBytes(2));
                zipcode = getString(readBytes(2));
                if(zipcode.equals("0"))
                    zipcode = "";
            }
    //		DB.witness = new Witness(TzHed, last, name,street, number, flat, city, zipcode,"");
        }

        private void getCarInfo() {
            String type = "", color = "", manufacturer = "",TxtPopUp = "",KeshelKod = "", KeshelMsg = "", RightSide = "", LeftSide = "";
            if (bufferChar[i] == '<' && bufferChar[i+1] == 'E' && bufferChar[i+2] == 'r' && bufferChar[i+3] == 'r' && bufferChar[i+4] == '>') {
                i += 5;
            }
            else {
                type = getString(readBytes(2));
                color = getString(readBytes(2));
                manufacturer = getString(readBytes(2));
                TxtPopUp = getString(readBytes(2));
                KeshelKod = getString(readBytes(2));
                KeshelMsg = getString(readBytes(2));
                RightSide = getString(readBytes(2));
                LeftSide = getString(readBytes(2));
            }
            DB.car = new Vehicle(CarNo, type, color, manufacturer,TxtPopUp,KeshelKod, KeshelMsg, RightSide, LeftSide);
        }
    }