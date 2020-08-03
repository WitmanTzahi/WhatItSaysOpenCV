package com.lbmotion.whatitsays.communication;

import static com.lbmotion.whatitsays.data.DB.authorityCharacteristic;


public class LoadAuthorityCharacteristicHTTP extends BaseHTTP {
	public static LoadAuthorityCharacteristicHTTP 	me;

	public LoadAuthorityCharacteristicHTTP() {
		REQUEST_TAG = "VolleyLoadAuthorityCharacteristicHTTP";
		TAG = "LoadAuthorityCharHTTP";
	}

	public static void abort() {
		try {
			if(me != null) {
				me.baseAbort();
			}
		}
		catch (Exception e) {}
	}

	public static LoadAuthorityCharacteristicHTTP doLoadAuthorityCharacteristicHTTP(String username, String authority) {
		me = new LoadAuthorityCharacteristicHTTP();
		me.doRun("N_GetPrmXMLTbl_Mirs.asp?Odbc=" + authority + "&Pakach=" + username);
		me.doWait();
		return me;
	}

	protected void parseResponse(String response) {
		try {
			result = true;
			authorityCharacteristic.SwQR = 0;
			while (response.length() > 0) {
				if (response.startsWith("<row>")) {
					response = response.substring("<row>".length());
					while (response.length() > 0) {
						/**/if (response.startsWith("</row>")) {
							response = response.substring("</row>".length());
							break;
						}
						else if(response.startsWith("<SwchooseNechePic>")) {
							response = response.substring("<SwchooseNechePic>".length());
							short repeatRegisterNumberInBikoret;
							try {repeatRegisterNumberInBikoret = (short) Integer.parseInt(GetField(response).trim());}catch(Exception e) {repeatRegisterNumberInBikoret = 0;}
							authorityCharacteristic.SwchooseNechePic = repeatRegisterNumberInBikoret == 1;
							response = response.substring(response.indexOf("</SwchooseNechePic>")+"</SwchooseNechePic>".length());
						}
						else if(response.startsWith("<RepeatRegisterNumber>")) {
							response = response.substring("<RepeatRegisterNumber>".length());
							short repeatRegisterNumberInBikoret;
							try {repeatRegisterNumberInBikoret = (short) Integer.parseInt(GetField(response).trim());}catch(Exception e) {repeatRegisterNumberInBikoret = 0;}
							authorityCharacteristic.RepeatLicsence = repeatRegisterNumberInBikoret == 1;
							response = response.substring(response.indexOf("</RepeatRegisterNumber>")+"</RepeatRegisterNumber>".length());
						}
						else if(response.startsWith("<PinkasWarningReport>")) {
							response = response.substring("<PinkasWarningReport>".length());
							short pinkasWarningReport;
							try {pinkasWarningReport = (short) Integer.parseInt(GetField(response).trim());}catch(Exception e) {pinkasWarningReport = 0;}
							authorityCharacteristic.PinkasWarningReport = pinkasWarningReport == 1;
							response = response.substring(response.indexOf("</PinkasWarningReport>")+"</PinkasWarningReport>".length());
						}
						else if(response.startsWith("<SwPrintTotal>")) {
							response = response.substring("<SwPrintTotal>".length());
							try {authorityCharacteristic.SwPrintTotal = (short) Integer.parseInt(GetField(response).trim());}catch(Exception e) {authorityCharacteristic.SwPrintTotal = 0;}
							response = response.substring(response.indexOf("</SwPrintTotal>")+"</SwPrintTotal>".length());
						}
						else if(response.startsWith("<SwPicHanaya>")) {
							response = response.substring("<SwPicHanaya>".length());
							try {authorityCharacteristic.SwPicHanaya = (short) Integer.parseInt(GetField(response).trim());}catch(Exception e) {authorityCharacteristic.SwPicHanaya = 0;}
							response = response.substring(response.indexOf("</SwPicHanaya>")+"</SwPicHanaya>".length());
						}
						else if (response.startsWith("<Violation>")) {
							response = response.substring("<Violation>".length());
							try {authorityCharacteristic.Violation = (short) Integer.parseInt(GetField(response).trim());} catch (Exception e) {}
							response = response.substring(response.indexOf("</Violation>")+ "</Violation>".length());
						} else if (response.startsWith("<CarType>")) {
							response = response.substring("<CarType>".length());
							try {authorityCharacteristic.CarType = (short) Integer.parseInt(GetField(response).trim());} catch (Exception e) {}
							response = response.substring(response.indexOf("</CarType>")+"</CarType>".length());
						} else if (response.startsWith("<CarColor>")) {
							response = response.substring("<CarColor>".length());
							try {authorityCharacteristic.CarColor = (short) Integer.parseInt(GetField(response).trim());} catch (Exception e) {}
							response = response.substring(response.indexOf("</CarColor>")+ "</CarColor>".length());
						} else if (response.startsWith("<CarIzran>")) {
							response = response.substring("<CarIzran>".length());
							try {authorityCharacteristic.CarIzran = (short) Integer.parseInt(GetField(response).trim());} catch (Exception e) {}
							response = response.substring(response.indexOf("</CarIzran>")+ "</CarIzran>".length());
						} else if (response.startsWith("<SwN>")) {
							response = response.substring("<SwN>".length());
							try {authorityCharacteristic.SwN = (short) Integer.parseInt(GetField(response).trim());} catch (Exception e) {}
							response = response.substring(response.indexOf("</SwN>")+ "</SwN>".length());
						} else if (response.startsWith("<SwS>")) {
							response = response.substring("<SwS>".length());
							try {authorityCharacteristic.SwS = (short) Integer.parseInt(GetField(response).trim());} catch (Exception e) {}
							response = response.substring(response.indexOf("</SwS>")+ "</SwS>".length());
						} else if (response.startsWith("<SwT>")) {
							response = response.substring("<SwT>".length());
							try {authorityCharacteristic.SwT = (short) Integer.parseInt(GetField(response).trim());} catch (Exception e) {}
							response = response.substring(response.indexOf("</SwT>")+ "</SwT>".length());
						}
						else if(response.startsWith("<SwPic240_320>")) {
							response = response.substring("<SwPic240_320>".length());
							try {authorityCharacteristic.SwPic240_320 = (short) Integer.parseInt(GetField(response).trim());}catch(Exception e) {}
							response = response.substring(response.indexOf("</SwPic240_320>")+"</SwPic240_320>".length());
						}
						else if (response.startsWith("<SwM>")) {
							response = response.substring("<SwM>".length());
							try {authorityCharacteristic.SwM = (short) Integer.parseInt(GetField(response).trim());} catch (Exception e) {}
							response = response.substring(response.indexOf("</SwM>")+ "</SwM>".length());
						} else if (response.startsWith("<ServTo>")) {
							response = response.substring("<ServTo>".length());
							try {authorityCharacteristic.ServTo = (short) Integer.parseInt(GetField(response).trim());} catch (Exception e) {}
							response = response.substring(response.indexOf("</ServTo>")+ "</ServTo>".length());
						} else if (response.startsWith("<TimeOut_CarNumber>")) {
							response = response.substring("<TimeOut_CarNumber>".length());
							try {authorityCharacteristic.TimeOut_CarNumber = (short) Integer.parseInt(GetField(response).trim());} catch (Exception e) {}
							response = response.substring(response.indexOf("</TimeOut_CarNumber>")+"</TimeOut_CarNumber>".length());
						} else if (response.startsWith("<TimeOut_ChkHeter>")) {
							response = response.substring("<TimeOut_ChkHeter>".length());
							try {authorityCharacteristic.TimeOut_ChkHeter = (short) Integer.parseInt(GetField(response).trim());} catch (Exception e) {}
							response = response.substring(response.indexOf("</TimeOut_ChkHeter>")+"</TimeOut_ChkHeter>".length());
						} else if (response.startsWith("<TimeOut_DoubleReport>")) {
							response = response.substring("<TimeOut_DoubleReport>".length());
							try {authorityCharacteristic.TimeOut_DoubleReport = (short) Integer.parseInt(GetField(response).trim());} catch (Exception e) {}
							response = response.substring(response.indexOf("</TimeOut_DoubleReport>")+ "</TimeOut_DoubleReport>".length());
						} else if (response.startsWith("<TimeOut_SendReport>")) {
							response = response.substring("<TimeOut_SendReport>".length());
							try {authorityCharacteristic.TimeOut_SendReport = (short) Integer.parseInt(GetField(response).trim());} catch (Exception e) {}
							response = response.substring(response.indexOf("</TimeOut_SendReport>")+ "</TimeOut_SendReport>".length());
						} else if (response.startsWith("<TimeOut_SendPic>")) {
							response = response.substring("<TimeOut_SendPic>".length());
							try {authorityCharacteristic.TimeOut_SendPic = (short) Integer.parseInt(GetField(response).trim());} catch (Exception e) {}
							response = response.substring(response.indexOf("</TimeOut_SendPic>")+ "</TimeOut_SendPic>".length());
						} else if (response.startsWith("<TimeOut_GetReport>")) {
							response = response.substring("<TimeOut_GetReport>".length());
							try {authorityCharacteristic.TimeOut_GetReport = (short) Integer.parseInt(GetField(response).trim());} catch (Exception e) {}
							response = response.substring(response.indexOf("</TimeOut_GetReport>")+ "</TimeOut_GetReport>".length());
						} else if (response.startsWith("<TimeOut_SendCancel>")) {
							response = response.substring("<TimeOut_SendCancel>".length());
							try {authorityCharacteristic.TimeOut_SendCancel = (short) Integer.parseInt(GetField(response).trim());} catch (Exception e) {}
							response = response.substring(response.indexOf("</TimeOut_SendCancel>")+ "</TimeOut_SendCancel>".length());
						} else if (response.startsWith("<TimeOut_SendCancelPic>")) {
							response = response.substring("<TimeOut_SendCancelPic>".length());
							try {authorityCharacteristic.TimeOut_SendCancelPic = (short) Integer.parseInt(GetField(response).trim());} catch (Exception e) {}
							response = response.substring(response.indexOf("</TimeOut_SendCancelPic>") + "</TimeOut_SendCancelPic>".length());
						} else if (response.startsWith("<PicNo>")) {
							response = response.substring("<PicNo>".length());
							try {authorityCharacteristic.PicNo = (short) Integer.parseInt(GetField(response).trim());} catch (Exception e) {}
							response = response.substring(response.indexOf("</PicNo>") + "</PicNo>".length());
						} else if (response.startsWith("<SwNoDeleteDochMeshofon>")) {
							response = response.substring("<SwNoDeleteDochMeshofon>".length());
							try {authorityCharacteristic.SwNoDeleteDochMeshofon = (short) Integer.parseInt(GetField(response).trim());} catch (Exception e) {}
							response = response.substring(response.indexOf("</SwNoDeleteDochMeshofon>")+ "</SwNoDeleteDochMeshofon>".length());
						} else if (response.startsWith("<SwAddPinkasMesofon>")) {
							response = response.substring("<SwAddPinkasMesofon>".length());
							try {authorityCharacteristic.SwAddPinkasMesofon = (short) Integer.parseInt(GetField(response).trim());} catch (Exception e) {}
							response = response.substring(response.indexOf("</SwAddPinkasMesofon>") + "</SwAddPinkasMesofon>".length());
						} else if (response.startsWith("<SwPrintDochBreratMispat>")) {
							response = response.substring("<SwPrintDochBreratMispat>".length());
							try {authorityCharacteristic.SwPrintDochBreratMispat = (short) Integer.parseInt(GetField(response).trim());} catch (Exception e) {}
							response = response.substring(response.indexOf("</SwPrintDochBreratMispat>") + "</SwPrintDochBreratMispat>".length());
						} else if (response.startsWith("<SwEnterCar>")) {
							response = response.substring("<SwEnterCar>".length());
							try {authorityCharacteristic.SwEnterCar = (short) Integer.parseInt(GetField(response).trim());} catch (Exception e) {}
							response = response.substring(response.indexOf("</SwEnterCar>")+ "</SwEnterCar>".length());
						}
						else if(response.startsWith("<FlashOnFrom>")) {
							response = response.substring("<FlashOnFrom>".length());
							String tmp = GetField(response).trim();
							try {authorityCharacteristic.FlashOnFrom = Integer.parseInt(tmp.substring(0, 2)) * 100 + Integer.parseInt(tmp.substring(3, 5));}catch(Exception e) {}
							response = response.substring(response.indexOf("</FlashOnFrom>")+"</FlashOnFrom>".length());
						}
						else if(response.startsWith("<FlashOnTo>")) {
							response = response.substring("<FlashOnTo>".length());
							String tmp = GetField(response).trim();
							try {authorityCharacteristic.FlashOnTo = Integer.parseInt(tmp.substring(0, 2)) * 100 + Integer.parseInt(tmp.substring(3, 5));}catch(Exception e) {}
							response = response.substring(response.indexOf("</FlashOnTo>")+"</FlashOnTo>".length());
						}
						else if(response.startsWith("<BetMishpat>")) {
							response = response.substring("<BetMishpat>".length());
							authorityCharacteristic.BetMishpat = ToHebrew(GetField(response).trim());
							response = response.substring(response.indexOf("</BetMishpat>")+"</BetMishpat>".length());
						}
						else if(response.startsWith("<CntMaxPicMesofon>")) {
							response = response.substring("<CntMaxPicMesofon>".length());
							String tmp = GetField(response).trim();
							if (tmp.length() == 0)// if the parameter field is empty
								tmp = "10000";
							try {authorityCharacteristic.CntMaxPicMesofon = Integer.parseInt(tmp.trim());}catch(Exception e) {}
							response = response.substring(response.indexOf("</CntMaxPicMesofon>")+"</CntMaxPicMesofon>".length());
						}
						else if(response.startsWith("<SwPrintDate>")) {
							response = response.substring("<SwPrintDate>".length());
							String tmp = GetField(response).trim();
							if (tmp.length() == 0)// if the parameter field is empty
								tmp = "0";
							try {authorityCharacteristic.SwPrintDate = Integer.parseInt(tmp.trim());}catch(Exception e) {}
							response = response.substring(response.indexOf("</SwPrintDate>")+"</SwPrintDate>".length());
						}
						else if(response.startsWith("<Minute_CloseMesofon>")) {
							response = response.substring("<Minute_CloseMesofon>".length());
							String tmp = GetField(response).trim();
							if (tmp.length() == 0 || tmp.equals ("0"))// if the parameter field is empty
								tmp = "1440";
							try {authorityCharacteristic.Minute_CloseMesofon = Integer.parseInt(tmp.trim());}catch(Exception e) {}
							response = response.substring(response.indexOf("</Minute_CloseMesofon>")+"</Minute_CloseMesofon>".length());
						}
						else if(response.startsWith("<DurationRecording>")) {
							response = response.substring("<DurationRecording>".length());
							String tmp = GetField(response).trim();
							if (tmp.length() == 0)// if the parameter field is empty
								tmp = "0";
							try {authorityCharacteristic.DurationRecording = Integer.parseInt(tmp.trim());}catch(Exception e) {}
							response = response.substring(response.indexOf("</DurationRecording>")+"</DurationRecording>".length());
						}
						else if(response.startsWith("<SwQR>")) {
							response = response.substring("<SwQR>".length());
							String tmp = GetField(response).trim();
							if (tmp.length() == 0)// if the parameter field is empty
								tmp = "0";
							try {authorityCharacteristic.SwQR += Integer.parseInt(tmp.trim());}catch(Exception e) {}
							response = response.substring(response.indexOf("</SwQR>")+"</SwQR>".length());
						}
						else if(response.startsWith("<SwQRKlali>")) {
							response = response.substring("<SwQRKlali>".length());
							String tmp = GetField(response).trim();
							if (tmp.length() == 0)// if the parameter field is empty
								tmp = "0";
							try {
								if(Integer.parseInt(tmp.trim()) > 0)
									authorityCharacteristic.SwQR += 2;
							}
							catch(Exception e) {}
							response = response.substring(response.indexOf("</SwQRKlali>")+"</SwQRKlali>".length());
						}
						else if(response.startsWith("<DurationRecordingDoch>")) {
							response = response.substring("<DurationRecordingDoch>".length());
							String tmp = GetField(response).trim();
							if (tmp.length() == 0) // if the parameter field is empty
								tmp = "0";
							try {authorityCharacteristic.DurationRecordingDoch = Integer.parseInt(tmp.trim());}catch(Exception e) {}
							response = response.substring(response.indexOf("</DurationRecordingDoch>")+"</DurationRecordingDoch>".length());
						}
						else if(response.startsWith("<SwSend_SMS>")) {
							response = response.substring("<SwSend_SMS>".length());
							String tmp = GetField(response).trim();
							if (tmp.length() == 0) // if the parameter field is empty
								tmp = "0";
							try {authorityCharacteristic.SwSend_SMS = Integer.parseInt(tmp.trim());}catch(Exception e) {}
							response = response.substring(response.indexOf("</SwSend_SMS>")+"</SwSend_SMS>".length());
						}
						else if(response.startsWith("<SwAzara>")) {
							response = response.substring("<SwAzara>".length());
							String tmp = GetField(response).trim();
							if (tmp.length() == 0)// if the parameter field is empty
								tmp = "0";
							try {authorityCharacteristic.SwAzara = Integer.parseInt(tmp.trim());}catch(Exception e) {}
							response = response.substring(response.indexOf("</SwAzara>")+"</SwAzara>".length());
						}
						else if(response.startsWith("<AuthorityName>")) {
							response = response.substring("<AuthorityName>".length());
							authorityCharacteristic.AuthorityName = ToHebrew(GetField(response).trim());
							response = response.substring(response.indexOf("</AuthorityName>")+"</AuthorityName>".length());
						}
						else if(response.startsWith("<SwQRManager>")) {
							response = response.substring("<SwQRManager>".length());
							String tmp = GetField(response).trim();
							if (tmp.length() == 0) // if the parameter field is empty
								tmp = "0";
							try {
								if(Integer.parseInt(tmp) != 0)
									authorityCharacteristic.SwQRManager = true;
								else
									authorityCharacteristic.SwQRManager = false;
							}
							catch(Exception e) {}
							response = response.substring(response.indexOf("</SwQRManager>")+"</SwQRManager>".length());
						}
						else if(response.startsWith("<SwNotMustTz>")) {
							response = response.substring("<SwNotMustTz>".length());
							String tmp = GetField(response).trim();
							if (tmp.length() == 0) // if the parameter field is empty
								tmp = "1";
							try {
								if(Integer.parseInt(tmp) != 0)
									authorityCharacteristic.SwNotMustTz = true;
								else
									authorityCharacteristic.SwNotMustTz = false;
							}
							catch(Exception e) {}
							response = response.substring(response.indexOf("</SwNotMustTz>")+"</SwNotMustTz>".length());
						}
						else if(response.startsWith("<SwMustServTo>")) {
							response = response.substring("<SwMustServTo>".length());
							String tmp = GetField(response).trim();
							if (tmp.length() == 0) // if the parameter field is empty
								tmp = "1";
							try {
								if(Integer.parseInt(tmp) != 0)
									authorityCharacteristic.SwMustServTo = true;
								else
									authorityCharacteristic.SwMustServTo = false;
							}
							catch(Exception e) {}
							response = response.substring(response.indexOf("</SwMustServTo>")+"</SwMustServTo>".length());
						}
						else {
							response = response.substring(1);
						}
					}
				} else {
					response = response.substring(1);
				}
			}
		}
		catch (Exception e) {
		}
	}
}
