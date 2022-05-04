package com.samuilolegovich.enums;

public enum SettingsXRP {
    ACTIVATION_PAYMENT(10.000000);

    public double value;
    SettingsXRP(double value) {
        this.value = value;
    }
    private void setValue(double value) {
        this.value = value;
    }

    public static void setValue(SettingsXRP settingsXRP, double value) {
        for (SettingsXRP e : SettingsXRP.values()) {
            if (e.equals(settingsXRP)) {
                e.setValue(value);
                break;
            }
        }
    }
}
