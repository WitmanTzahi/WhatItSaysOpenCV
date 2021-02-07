package com.lbmotion.whatitsays.util;

/**
 * Created by witman on 28/01/2018.
 */


import com.lbmotion.whatitsays.common.BaseActivity;
import com.lbmotion.whatitsays.data.DB;
import com.lbmotion.whatitsays.data.List2s;
import com.lbmotion.whatitsays.data.List3;

import java.util.Vector;

public class ExistingWarningOrEvent {

/*
    public static void showErrorMessageCodeAveraNotFound() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.instance);
        TextView message = new TextView(MainActivity.instance);
        message.setText(R.string.code_avera_not_found);
        message.setPadding(10, 10, 10, 10);
        message.setTextColor(Color.RED);
        message.setTextSize(20);
        builder.setView(message);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.button_continue,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        MainActivity.instance.startNewTicket(true);
                    }
                }
        );
        AlertDialog alert = builder.create();
        alert.show();
    }
*/
    public static Vector<Integer> convertCitizenRemarkToIndex() {
        Vector<Integer> remarkToIndex = new Vector<Integer>();
        try {
            for(short i = 0; i < DB.citizenRemarks.size(); i++)
                remarkToIndex.add(Integer.valueOf(DB.citizenRemarks.get(i).code));
        }
        catch(Exception e){}
        return remarkToIndex;
    }

    public static Vector<Integer> convertPakachRemarkToIndex(int C) {
        Vector<Integer> remarkToIndex = new Vector<Integer>();
        try {
            for(int i = 0;i < DB.remarksToViolations.size();i++) {
                List2s remarkToViolation = DB.remarksToViolations.get(i);
                if(C == remarkToViolation.Violation) {
                    for(short j = 0;j < DB.remarks.size();j++) {
                        List3 remark = DB.remarks.elementAt(j);
                        if(remark.name.indexOf("*?=*") != -1)
                            continue;
                        if(remark.name.indexOf("*?D*") != -1)
                            continue;
                        if(remark.name.indexOf("*?U*") != -1)
                            continue;
                        if(remarkToViolation.code == remark.code) {
                            remarkToIndex.add(Integer.valueOf(remark.code));
                        }
                    }
                }
            }
        }
        catch (Exception e) {}
        return remarkToIndex;
    }

    public static int codeToIndex(int code, Vector<Integer> remarkToIndex) {
        for(int i = 0;i < remarkToIndex.size();i++) {
            if(remarkToIndex.get(i).intValue() == code)
                return i;
        }
        return BaseActivity.NO_REFERENCE;
    }
/*
    public static boolean continueWithExistingWarningOrWarningToHandle(boolean setAvera, ExistingWarning existingWarning) {
        if(UCApp.ticketInformation.carOrGM && UCApp.loginData.authority.equals("920039"))
            UCApp.ticketInformation.printTextBox = false;
        else
            UCApp.ticketInformation.printTextBox = true;
        pageLocationSavedStreetBetweenActivations = UCApp.ticketInformation.streetName;
        if (pageLocationSavedStreetBetweenActivations == null)
            pageLocationSavedStreetBetweenActivations = "";
        pageLocationSaveStreetNumber = UCApp.ticketInformation.streetNumber;
        pageLocationSaveSelectedItemLocation = UCApp.ticketInformation.streetByAcrossIn;
        pageLocationSaveStreetObject = null;
        for (int i = 0; i < DB.streets.size(); i++) {
            if (DB.streets.get(i).code == UCApp.ticketInformation.streetCode) {
                pageLocationSaveStreetObject = DB.streets.get(i);
                break;
            }
        }
        if (UCApp.ticketInformation.defendants.size() > 0)
            pageGIids_DefendantIdData = UCApp.ticketInformation.defendants.get(0).id;
//      pageGIids_WitnessIdData = UCApp.ticketInformation.witness.id;
        pageGIids_CarRegistrationNumberData = UCApp.ticketInformation.vehicle.licence;
        pageGIidType = (byte) UCApp.ticketInformation.idType;
        pageAveraViolationTypeWasChosen = true;
        pageAveraViolationObject = null;
        for (int i = 0; i < DB.allViolations.size(); i++) {
            if (DB.allViolations.get(i).C == UCApp.ticketInformation.violationListCode) {
                pageAveraViolationObject = DB.allViolations.get(i);
                UCApp.ticketInformation.isManagement = pageAveraViolationObject.Sivug == 2;
                break;
            }
        }
        pageAveraLastViolationObject = pageAveraViolationObject;
        UCApp.ticketInformation.remarkReference1 = BaseActivity.NO_REFERENCE;
        UCApp.ticketInformation.remarkReference2 = BaseActivity.NO_REFERENCE;
        UCApp.ticketInformation.remarkReference3 = BaseActivity.NO_REFERENCE;
        UCApp.ticketInformation.remarkReference4 = BaseActivity.NO_REFERENCE;
        UCApp.ticketInformation.citizenRemarkReference1 = BaseActivity.NO_REFERENCE;
        UCApp.ticketInformation.citizenRemarkReference2 = BaseActivity.NO_REFERENCE;
        UCApp.ticketInformation.citizenRemarkReference3 = BaseActivity.NO_REFERENCE;
        UCApp.ticketInformation.citizenRemarkReference4 = BaseActivity.NO_REFERENCE;
        if(pageAveraViolationObject != null) {
            Vector<Integer> remarkToIndex = convertPakachRemarkToIndex(pageAveraViolationObject.C);
            try{UCApp.ticketInformation.remarkReference1 = codeToIndex(Integer.parseInt(existingWarning.Pakach_Remark1),remarkToIndex);}catch (Exception e) {}
            try{UCApp.ticketInformation.remarkReference2 = codeToIndex(Integer.parseInt(existingWarning.Pakach_Remark2),remarkToIndex);}catch (Exception e) {}
            try{UCApp.ticketInformation.remarkReference3 = codeToIndex(Integer.parseInt(existingWarning.Pakach_Remark3),remarkToIndex);}catch (Exception e) {}
            try{UCApp.ticketInformation.remarkReference4 = codeToIndex(Integer.parseInt(existingWarning.Pakach_Remark4),remarkToIndex);}catch (Exception e) {}
        }
        if(UCApp.ticketInformation.remarkReference1 != BaseActivity.NO_REFERENCE) {
            UCApp.pakachSelectedItemRemarks.add(Integer.valueOf(UCApp.ticketInformation.remarkReference1));
            UCApp.pakachSelectedItemRemarksPrint.add(Boolean.valueOf(true));
        }
        if(UCApp.ticketInformation.remarkReference2 != BaseActivity.NO_REFERENCE) {
            UCApp.pakachSelectedItemRemarks.add(Integer.valueOf(UCApp.ticketInformation.remarkReference2));
            UCApp.pakachSelectedItemRemarksPrint.add(Boolean.valueOf(true));
        }
        if(UCApp.ticketInformation.remarkReference3 != BaseActivity.NO_REFERENCE) {
            UCApp.pakachSelectedItemRemarks.add(Integer.valueOf(UCApp.ticketInformation.remarkReference3));
            UCApp.pakachSelectedItemRemarksPrint.add(Boolean.valueOf(true));
        }
        if(UCApp.ticketInformation.remarkReference4 != BaseActivity.NO_REFERENCE) {
            UCApp.pakachSelectedItemRemarks.add(Integer.valueOf(UCApp.ticketInformation.remarkReference4));
            UCApp.pakachSelectedItemRemarksPrint.add(Boolean.valueOf(true));
        }
        Vector<Integer> remarkToIndex = convertCitizenRemarkToIndex();
        try{UCApp.ticketInformation.citizenRemarkReference1 = codeToIndex(Integer.parseInt(existingWarning.Citizen_Remark1),remarkToIndex);}catch (Exception e) {}
        try{UCApp.ticketInformation.citizenRemarkReference2 = codeToIndex(Integer.parseInt(existingWarning.Citizen_Remark2),remarkToIndex);}catch (Exception e) {}
        try{UCApp.ticketInformation.citizenRemarkReference3 = codeToIndex(Integer.parseInt(existingWarning.Citizen_Remark3),remarkToIndex);}catch (Exception e) {}
        try{UCApp.ticketInformation.citizenRemarkReference4 = codeToIndex(Integer.parseInt(existingWarning.Citizen_Remark4),remarkToIndex);}catch (Exception e) {}
        if(UCApp.ticketInformation.citizenRemarkReference1 != BaseActivity.NO_REFERENCE)
            UCApp.citizenSelectedItemRemarks.add(Integer.valueOf(UCApp.ticketInformation.citizenRemarkReference1));
        if(UCApp.ticketInformation.citizenRemarkReference2 != BaseActivity.NO_REFERENCE)
            UCApp.citizenSelectedItemRemarks.add(Integer.valueOf(UCApp.ticketInformation.citizenRemarkReference2));
        if(UCApp.ticketInformation.citizenRemarkReference3 != BaseActivity.NO_REFERENCE)
            UCApp.citizenSelectedItemRemarks.add(Integer.valueOf(UCApp.ticketInformation.citizenRemarkReference3));
        if(UCApp.ticketInformation.citizenRemarkReference4 != BaseActivity.NO_REFERENCE)
            UCApp.citizenSelectedItemRemarks.add(Integer.valueOf(UCApp.ticketInformation.citizenRemarkReference4));
        pageAveraSaveAutoCompleteTextViewAverotAsString = makeAveraTitle(pageAveraViolationObject, true, false);
        if (pageAveraViolationObject == null) {
            showErrorMessageCodeAveraNotFound();
            return false;
        }
        Vector<Violation> violationListTypeElements = new Vector<Violation>();
        String[] averaTypes = PageAvera.getAveraTypesListAsStringArray(violationListTypeElements, MainActivity.instance,true);
        for (short i = 0; i < averaTypes.length; i++) {
            if (averaTypes[i].equals(pageAveraViolationObject.VT_Nm)) {
                pageAveraSelectedItemAveraType = i;
                break;
            }
        }
        for (int i = 0; i < DB.allViolations.size(); i++) {
            Violation v = DB.allViolations.get(i);
            if (pageAveraViolationObject.VTC == v.VTC) {
                DB.violations.add(v);
            }
        }
        if (setAvera) {
            UCApp.ticketInformation.vSwHatraa = MainActivity.pageAveraViolationObject.SwHatraa;
            UCApp.ticketInformation.P = MainActivity.pageAveraViolationObject.P;
            UCApp.ticketInformation.D = MainActivity.pageAveraViolationObject.D;
            UCApp.ticketInformation.NumCopies = MainActivity.pageAveraViolationObject.NumCopies;
            if (MainActivity.pageAveraViolationObject.VT == 7)
                UCApp.ticketInformation.smoking = true;
            else
                UCApp.ticketInformation.smoking = false;
        }
        pageGIbeforeQuery = false;
        doSkipErrorCheckPage = MainActivity.PAGE_AVERA;
        BaseActivity.pakachRemarksList();
        BaseActivity.citizenRemarksList();
        return true;
    }

    public static void continueWithExistingWarning() {
        ExistingWarning existingWarning = DB.existingWarnings.get(UCApp.pakachSelectedItemWarning);
        UCApp.hasToReQueryAgain = true;
        UCApp.ticketInformation.streetByAcrossIn = (byte)0;
        UCApp.ticketInformation.inProccess = true;
        UCApp.isDoingPreviousWarning = true;
        UCApp.ticketInformation.isPrevious = true;
        UCApp.ticketInformation.carOrGM = true;
        if (existingWarning.AveraC.length() == 0 || existingWarning.AveraC.equals("999")) {
            UCApp.isDoingAssiment = true;
        }
        else {
            UCApp.ticketInformation.violationListCode = Integer.parseInt(existingWarning.AveraC);
            getPrintQR();
        }
        UCApp.ticketInformation.streetNumber = existingWarning.StreetNoAvera;
        UCApp.ticketInformation.streetName = existingWarning.StreetNm;
        UCApp.ticketInformation.p_printedDochNumber = existingWarning.DochKod + "-" + existingWarning.Sidra + "-" + existingWarning.Bikoret;
        UCApp.ticketInformation.p_existingWarningPrintedDochNumber = UCApp.ticketInformation.p_printedDochNumber;
        UCApp.ticketInformation.defendants.removeAllElements();
        MainActivity.instance.clearPagePageGIDefendant();
        UCApp.ticketInformation.currentDefendant = 0;
        UCApp.ticketInformation.defendants.add(existingWarning.defendant);
        try {
            if (existingWarning.numAzhara.length() > 0)
                UCApp.ticketInformation.numAzhara = Short.parseShort(existingWarning.numAzhara);
            else
                UCApp.ticketInformation.numAzhara = 0;
        } catch (Exception ex) {
        }
        try {
            UCApp.ticketInformation.streetCode = Integer.parseInt(existingWarning.StreetC);
        } catch (Exception ex) {
        }
        UCApp.ticketInformation.saveNumAzhara = UCApp.ticketInformation.numAzhara;
        UCApp.ticketInformation.DochKod = existingWarning.DochC;
        UCApp.ticketInformation.p_streetName = existingWarning.StreetNm;
        UCApp.ticketInformation.witness = existingWarning.witness;
        UCApp.ticketInformation.vehicle = existingWarning.vehicle;
        if (UCApp.ticketInformation.streetNumber.length() == 0)
            UCApp.ticketInformation.streetNumber = existingWarning.defendant.number;
        UCApp.ticketInformation.validUpto = existingWarning.validUpto;
        if (existingWarning.defendant.id.length() >= 9 && existingWarning.defendant.id.charAt(0) - '0' > 4)
            UCApp.ticketInformation.idType = 0;
        else
            UCApp.ticketInformation.idType = 1;
        UCApp.ticketInformation.animalId = existingWarning.animalId;
        MainActivity.instance.currentState = MainActivity.StatesTypes.STEP_TO_COMPLETE_GENERAL_INSPECTION_DOCH;
        UCApp.ticketInformation.pakachFreeText = existingWarning.FreeRemark;
        UCApp.ticketInformation.citizenFreeText = existingWarning.CitizenFreeText;
        UCApp.ticketInformation.picturesURL = existingWarning.pictures;
        if (continueWithExistingWarningOrWarningToHandle(true,existingWarning)) {
            if (MainActivity.instance.pageLocation != null)
                MainActivity.instance.pageLocation.initUI();
            if (MainActivity.instance.pageAvera != null)
                MainActivity.instance.pageAvera.initUI();
            loadAllOfTheRestGeneralInspectionPages();
        }
    }

    public static void getPrintQR() {
        for(int i = 0;i < DB.allViolations.size();i++) {
            if (DB.allViolations.get(i).C == UCApp.ticketInformation.violationListCode) {
                UCApp.ticketInformation.swPrintQR = DB.allViolations.get(i).swPrintQR;
            }
        }
    }

    public static void continueWithPastToHandleEvents() {// אירועים פתוחים
        ExistingWarning existingWarning = DB.pastToHandleEvents.get(UCApp.pakachSelectedItemWarning);
        UCApp.hasToReQueryAgain = true;
        UCApp.isDoingPreviousOpenEvent = true;
        UCApp.ticketInformation.streetByAcrossIn = (byte)0;
        UCApp.ticketInformation.inProccess = true;
        UCApp.ticketInformation.carOrGM = true;
        UCApp.ticketInformation.violationListCode = Integer.parseInt(existingWarning.AveraC);
        getPrintQR();
        UCApp.ticketInformation.streetCode = Integer.parseInt(existingWarning.StreetC);
        UCApp.ticketInformation.streetNumber = existingWarning.StreetNoAvera;
        UCApp.ticketInformation.streetName = existingWarning.StreetNm;
        UCApp.ticketInformation.p_streetName = existingWarning.StreetNm;
        UCApp.ticketInformation.p_printedDochNumber = UCApp.ticketInformation.p_existingWarningPrintedDochNumber = "";
        UCApp.ticketInformation.currentDefendant = 0;
        UCApp.ticketInformation.defendants.removeAllElements();
        MainActivity.instance.clearPagePageGIDefendant();
        UCApp.ticketInformation.defendants.add(existingWarning.defendant);
        UCApp.ticketInformation.saveNumAzhara = UCApp.ticketInformation.numAzhara = 0;
        UCApp.ticketInformation.witness = existingWarning.witness;
        UCApp.ticketInformation.vehicle = existingWarning.vehicle;
        if (UCApp.ticketInformation.streetNumber.length() == 0)
            UCApp.ticketInformation.streetNumber = existingWarning.defendant.number;
        UCApp.ticketInformation.date = existingWarning.validUpto + ' ' + existingWarning.validUptoTime;
        UCApp.ticketInformation.subItem = existingWarning.remark;
        UCApp.ticketInformation.dochBedihavad = true;
        UCApp.ticketInformation.DochKod = existingWarning.DochC;
        if (existingWarning.defendant.id.length() >= 9 && existingWarning.defendant.id.charAt(0) - '0' > 4)
            UCApp.ticketInformation.idType = 0;
        else
            UCApp.ticketInformation.idType = 1;
        UCApp.ticketInformation.animalId = existingWarning.animalId;
//      if (continueWithExistingWarningOrWarningToHandle(false))
        UCApp.ticketInformation.pakachFreeText = existingWarning.FreeRemark;
        UCApp.ticketInformation.citizenFreeText = existingWarning.CitizenFreeText;
        UCApp.ticketInformation.picturesURL = existingWarning.pictures;
        if (continueWithExistingWarningOrWarningToHandle(true,existingWarning)) {
            if (MainActivity.instance.pageLocation != null)
                MainActivity.instance.pageLocation.initUI();
            if (MainActivity.instance.pageAvera != null)
                MainActivity.instance.pageAvera.initUI();
            loadAllOfTheRestGeneralInspectionPages();
        }
    }

    public static void loadAllOfTheRestGeneralInspectionPages() {
        MainActivity.instance.theScrollViewLayout.setVisibility(View.INVISIBLE);
        MainActivity.instance.currentState = MainActivity.StatesTypes.STEP_TO_COMPLETE_GENERAL_INSPECTION_DOCH;
        MainActivity.instance.setPagesTitlesAndMenus();//true V3.62
        if(MainActivity.instance.pageLocation != null) {
            MainActivity.instance.pageLocation.initUI();
        }
        if(MainActivity.instance.pageAvera != null) {
            MainActivity.instance.pageAvera.buildingAllPages = true;
            MainActivity.instance.pageAvera.initUI();
            MainActivity.instance.pageAvera.buildingAllPages = false;
        }
        MainActivity.instance.progressBar.setVisibility(View.VISIBLE);
        try {
            MainActivity.instance.suspendPageMovementFlag = true;
            if(MainActivity.instance.pageGeneralInspectionDefendant != null)
                MainActivity.instance.pageGeneralInspectionDefendant.initUI();
            if(MainActivity.instance.pageCarDetails != null)
                MainActivity.instance.pageCarDetails.initUI();
            if(MainActivity.instance.pageGeneralInspectionWitness != null)
                MainActivity.instance.pageGeneralInspectionWitness.initUI();
            if(MainActivity.instance.pagePakachRemarks != null)
                MainActivity.instance.pagePakachRemarks.initUI();
            if(MainActivity.instance.pagePakachDecision != null)
                MainActivity.instance.pagePakachDecision.initUI();
            if(MainActivity.instance.pageCitizenRemarks != null)
                MainActivity.instance.pageCitizenRemarks.initUI();
            new Thread() { public void run() {
                try {
                    Thread.sleep(750);} catch (InterruptedException ie) {}
                MainActivity.instance.suspendPageMovementFlag = false;
                    MainActivity.instance.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (MainActivity.pageAveraViolationObject != null && MainActivity.pageAveraViolationObject.VT_Nm.equals(MainActivity.NATUS) && UCApp.isDoingPreviousWarning)
                                    MainActivity.instance.jumpToPage(MainActivity.PAGE_AVERA,true);
                                else if (MainActivity.pageAveraViolationObject != null && MainActivity.pageAveraViolationObject.VT_Nm.equals(MainActivity.NATUS))
                                    MainActivity.instance.jumpToPage(MainActivity.PAGE_CAR,true);
                                else if (UCApp.ticketInformation.vehicle.KeshelKod.length() > 0)
                                    MainActivity.instance.jumpToPage(MainActivity.PAGE_GENERAL_INFORMATION_CAR_DETAILS,true);
                                else
                                    MainActivity.instance.jumpToPage(MainActivity.PAGE_GENERAL_INFORMATION_DEFENDANT_DETAIL,true);
//
                              if(!(MainActivity.pageAveraViolationObject != null && MainActivity.pageAveraViolationObject.VT_Nm.equals(MainActivity.NATUS))) {
                                    if (MainActivity.instance.pageGeneralInspectionDefendant.getID().length() == 0 &&
                                            UCApp.ticketInformation.defendants.size() > 0 && UCApp.ticketInformation.defendants.get(0).id.length() > 0) {
                                        MainActivity.instance.pageGeneralInspectionDefendant.initUI();
                                        MainActivity.instance.pageGeneralInspectionDefendant.getRootView().invalidate();
                                    }
                                }
                            } catch (Exception e) {
                            } catch (Error e) {
                            }
                        }
                    });
            }}.start();
        }
        catch (Exception e) {}
        catch (Error e) {}
        try {
            MainActivity.instance.progressBar.setVisibility(View.GONE);
        }
        catch (Exception e) {}
        catch (Error e) {}
        try {
            MainActivity.instance.theScrollViewLayout.setVisibility(View.VISIBLE);
            if(UCApp.isDoingPreviousWarning) {
                UCApp.ticketInformation.saveCitizenFreeText = UCApp.ticketInformation.citizenFreeText;
                UCApp.ticketInformation.savePakachFreeText = UCApp.ticketInformation.pakachFreeText;
                Intent intent = new Intent(MainActivity.instance, DochActivity.class);
                intent.putExtra(BaseActivity.HANDLE_REPORT,BaseActivity.HANDLE_REPORT);
                MainActivity.instance.startActivityForResult(intent, MainActivity.DOC_ACIVITY);
            }
        }
        catch (Exception e) {}
        catch (Error e) {}
    }
*/
}
