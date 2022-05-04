package com.samuilolegovich.enums;

public enum EnumBoo {
    IS_WALLET(true),
    IS_WALLET_TEST(true),

    IS_REAL(false),
    ;

    public boolean b;
    EnumBoo(boolean b) {
        this.b = b;
    }

    private void setValue(boolean b) {
        this.b = b;
    }

    public static void setValue(EnumBoo enums, boolean b) {
        for (EnumBoo e : EnumBoo.values()) {
            if (e.equals(enums)) {
                e.setValue(b);
                break;
            }
        }
    }
}
