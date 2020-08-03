package com.lbmotion.whatitsays;

public interface LicenseQueryCompleted {
    public void notifyQueryCompletion(final String licensePlate, final String directoryLicensePlate,int packachDecisionCode);
}
