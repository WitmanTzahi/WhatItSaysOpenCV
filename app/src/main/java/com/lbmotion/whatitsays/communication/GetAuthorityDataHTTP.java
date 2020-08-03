package com.lbmotion.whatitsays.communication;

import android.util.Log;

import com.lbmotion.whatitsays.data.DB;

import static com.lbmotion.whatitsays.communication.GetInsideCodesHTTP.doGetInsideCodesHTTP;
import static com.lbmotion.whatitsays.communication.GetList2HTTP.doGetList2HTTP;
import static com.lbmotion.whatitsays.communication.GetList2sHTTP.doGetList2sHTTP;
import static com.lbmotion.whatitsays.communication.GetList3HTTP.doGetList3HTTP;
import static com.lbmotion.whatitsays.communication.GetNotebookTypesHTTP.doGetNotebookTypesHTTP;
import static com.lbmotion.whatitsays.communication.GetTrialDatesHTTP.doGetTrialDatesHTTP;
import static com.lbmotion.whatitsays.communication.GetViolationsHTTP.doGetViolationsHTTP;
import static com.lbmotion.whatitsays.communication.GetWarningsAndEventsHTTP.doGetWarningsAndEventsHTTP;
import static com.lbmotion.whatitsays.communication.LoadAuthorityCharacteristicHTTP.doLoadAuthorityCharacteristicHTTP;
import static com.lbmotion.whatitsays.communication.LoadStreetsHTTP.doLoadStreetsHTTP;

public class GetAuthorityDataHTTP extends BaseHTTP {
	public static GetAuthorityDataHTTP 		me;
	private GetViolationsHTTP 				getViolationsHTTP = null;
	private GetList3HTTP 					getList3HTTP = null;
	private GetList2HTTP 					getList2HTTP = null;
	private GetList2sHTTP 					getList2sHTTP = null;
	private LoadStreetsHTTP					loadStreetsHTTP = null;
	private LoadAuthorityCharacteristicHTTP	loadAuthorityCharacteristicHTTP = null;
	private GetInsideCodesHTTP				getInsideCodesHTTP = null;
	private GetTrialDatesHTTP	 			getTrialDatesHTTP = null;
	private GetWarningsAndEventsHTTP		getWarningsAndEventsHTTP = null;
	private GetNotebookTypesHTTP			getNotebookTypesHTTP = null;

	public GetAuthorityDataHTTP() {
	}

	public static void abort() {
		try {
			if(me != null) {
				me.baseAbort();
				if(me.getViolationsHTTP != null)
					me.getViolationsHTTP.abort();
				if(me.getList3HTTP != null)
					me.getList3HTTP.abort();
				if(me.getList2HTTP != null)
					me.getList2HTTP.abort();
				if(me.getList2sHTTP != null)
					me.getList2sHTTP.abort();
				if(me.loadStreetsHTTP != null)
					me.loadStreetsHTTP.abort();
				if(me.loadAuthorityCharacteristicHTTP != null)
					me.loadAuthorityCharacteristicHTTP.abort();
				if(me.getInsideCodesHTTP != null)
					me.getInsideCodesHTTP.abort();
				if(me.getTrialDatesHTTP != null)
					me.getTrialDatesHTTP.abort();
				if(me.getWarningsAndEventsHTTP != null)
					me.getWarningsAndEventsHTTP.abort();
				if(me.getNotebookTypesHTTP != null)
					me.getNotebookTypesHTTP.abort();
			}
		}
		catch (Exception e) {}
	}

	public static GetAuthorityDataHTTP doGetAuthorityDataHTTP(String username, String authority) {
		try {
			me = new GetAuthorityDataHTTP();
			Log.i(me.TAG, "run()");
			try {
				me.errorOccurred = true;
				me.getViolationsHTTP = doGetViolationsHTTP(username,authority);
				if(me.getViolationsHTTP != null && me.getViolationsHTTP.result && !me.getViolationsHTTP.errorOccurred && !me.didAbort) {
					me.getViolationsHTTP = null;
					me.getList3HTTP = doGetList3HTTP("VolleyGetVehicleTypeHTTP", "N_GetCarTypeXMLTbl_Mirs.asp?", username, authority, DB.vehicleTypes);
					if (me.getList3HTTP != null && me.getList3HTTP.result && !me.getList3HTTP.errorOccurred && !me.didAbort) {
						me.getList3HTTP = null;
						me.getList2HTTP = doGetList2HTTP("VolleyGetVehicleManufacturersHTTPM", "N_GetCarManufacturerXMLTbl_Mirs.asp?", username, authority, DB.vehicleManufacturers);
						if (me.getList2HTTP != null && me.getList2HTTP.result && !me.getList2HTTP.errorOccurred && !me.didAbort) {
							me.getList2HTTP = null;
							me.getList2HTTP = doGetList2HTTP("VolleyGetVehicleColorsHTTPC", "N_GetCarColorXMLTbl_Mirs.asp?", username, authority, DB.vehicleColors);
							if (me.getList2HTTP != null && me.getList2HTTP.result && !me.getList2HTTP.errorOccurred && !me.didAbort) {
								me.getList2HTTP = null;
								me.getList3HTTP = doGetList3HTTP("VolleyGetAreasHTTP", "N_GetEzorXMLTbl_Mirs.asp?", username, authority, DB.areas);
								if (me.getList3HTTP != null  && me.getList3HTTP.result && !me.getList3HTTP.errorOccurred && !me.didAbort) {
									me.getList3HTTP = null;
									me.getList2HTTP = doGetList2HTTP("VolleyGetCancelRemarksHTTPM", "N_GetCancelRemarksXMLTbl_Mirs.asp?", username, authority, DB.cancelRemarks);
									if (me.getList2HTTP != null && me.getList2HTTP.result && !me.getList2HTTP.errorOccurred && !me.didAbort) {
										me.getList2HTTP = null;
										me.getList3HTTP = doGetList3HTTP("VolleyGetRemarksHTTP", "N_GetWardensRemarksXMLTbl_Mirs.asp?", username, authority, DB.remarks);
										if (me.getList3HTTP != null && me.getList3HTTP.result && !me.getList3HTTP.errorOccurred && !me.didAbort) {
											me.getList3HTTP = null;
											me.getList2sHTTP = doGetList2sHTTP("VolleyGetrRemarksToViolationsHTTP", "N_GetWardensRemarksCXMLTbl_Mirs.asp?", username, authority, DB.remarksToViolations);
											if (me.getList2sHTTP != null && me.getList2sHTTP.result && !me.getList2sHTTP.errorOccurred && !me.didAbort) {
												me.getList2sHTTP = null;
												me.getList3HTTP = doGetList3HTTP("VolleyGetCitizenRemarksHTTP", "N_GetCitizenRemarkXMLTbl_Mirs.asp?", username, authority, DB.citizenRemarks);
												if (me.getList3HTTP != null && me.getList3HTTP.result && !me.getList3HTTP.errorOccurred && !me.didAbort) {
													me.getList3HTTP = null;
													me.loadAuthorityCharacteristicHTTP = doLoadAuthorityCharacteristicHTTP(username, authority);
													if (me.loadAuthorityCharacteristicHTTP != null && me.loadAuthorityCharacteristicHTTP.result && !me.loadAuthorityCharacteristicHTTP.errorOccurred && !me.didAbort) {
														me.loadAuthorityCharacteristicHTTP = null;
														me.getInsideCodesHTTP = doGetInsideCodesHTTP(username, authority);
														if (me.getInsideCodesHTTP != null && me.getInsideCodesHTTP.result && !me.getInsideCodesHTTP.errorOccurred && !me.didAbort) {
															me.getInsideCodesHTTP = null;
															me.getTrialDatesHTTP = doGetTrialDatesHTTP(username, authority);
															if (me.getTrialDatesHTTP != null && me.getTrialDatesHTTP.result && !me.getTrialDatesHTTP.errorOccurred && !me.didAbort) {
																me.getTrialDatesHTTP = null;
																me.getWarningsAndEventsHTTP = doGetWarningsAndEventsHTTP(authority , username );
																if (me.getWarningsAndEventsHTTP != null && me.getWarningsAndEventsHTTP.result && !me.getWarningsAndEventsHTTP.errorOccurred && !me.didAbort) {
																	me.getWarningsAndEventsHTTP = null;
																	me.getNotebookTypesHTTP = doGetNotebookTypesHTTP("VolleyGetNotebookTypesHTTP","N_GetSendReport_Mirs.asp?",username, authority,DB.nimhans);
																	if (me.getNotebookTypesHTTP != null && me.getNotebookTypesHTTP.result && !me.getNotebookTypesHTTP.errorOccurred && !me.didAbort) {
																		me.getNotebookTypesHTTP = null;
																		me.loadStreetsHTTP = doLoadStreetsHTTP(username, authority,true);
																		if (me.loadStreetsHTTP != null && me.loadStreetsHTTP.result && !me.loadStreetsHTTP.errorOccurred && !me.didAbort) {
																			me.loadStreetsHTTP = null;
																			me.result = true;
																			me.errorOccurred = false;
																			Log.i(me.TAG, "run() OK");
																		}
																	}
																}
															}
														}
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
			catch (Exception e) {
				me.errorOccurred = true;
				Log.i(me.TAG, "run()"+e.getMessage());
			}
			me.getViolationsHTTP = null;
			me.getList3HTTP = null;
			me.getList2HTTP = null;
			me.getList2sHTTP = null;
			me.loadStreetsHTTP = null;
			me.loadAuthorityCharacteristicHTTP = null;
			me.getInsideCodesHTTP = null;
			me.getTrialDatesHTTP = null;
			me.getWarningsAndEventsHTTP = null;
			me.getNotebookTypesHTTP = null;
			Log.i(me.TAG, "run() DOWN");
		}
		catch(Exception e) {
			try {
				Log.i(me.TAG, "run() ERROR:"+e.getMessage());
				me.abort();
				me.result = false;
				me.errorOccurred = true;
			}
			catch(Exception ee) {}
		}
		return me;
	}

	protected void parseResponse(String response) {

	}
}
