package com.samuilolegovich;

import com.samuilolegovich.enums.EnumBoo;
import com.samuilolegovich.enums.EnumStr;
import com.samuilolegovich.model.PaymentManager.PaymentManager;
import com.samuilolegovich.model.PaymentManager.PaymentManagerXRP;

import java.util.Locale;


public class XRPLedgerApplication {

    public static void main(String[] args)  {
        // Обязательно стоит установить локаль иначе будет падать с ошибкой парсинга даты
        Locale.setDefault(Locale.ENGLISH);

//        new PaymentTest().run();
//        new PaymentReal().run();
        EnumBoo.setValue(EnumBoo.IS_REAL, true);
        EnumBoo.setValue(EnumBoo.IS_WALLET, true);
//        EnumStr.setValue(EnumStr.REAL_SEED, "sEdSyKacM9uMcHco7o8oEnu1hyYnSVP");

        PaymentManager paymentManager = new PaymentManagerXRP();

        System.out.println("X Address  -- >  " + paymentManager.getXAddress(EnumBoo.IS_REAL.b));
        System.out.println("Classic Address  -- >  " + paymentManager.getClassicAddress(EnumBoo.IS_REAL.b));
        System.out.println("Private Key  -- >  " + paymentManager.getPrivateKey(EnumBoo.IS_REAL.b));
        System.out.println("Public Key  -- >  " + paymentManager.getPublicKey(EnumBoo.IS_REAL.b));
        System.out.println("Seed  -- >  " + paymentManager.getSeed(EnumBoo.IS_REAL.b));
        System.out.println("Balance  -- >  " + paymentManager.getBalance(EnumBoo.IS_REAL.b));
//        System.out.println(paymentManager.getXAddress(EnumBoo.IS_REAL.b));

    }

//    X Address  -- >  X726GPFxsTDjgbZYF3THM63PUXaC1XRZjXdeiRhqn5e6b16
//    Classic Address  -- >  rsUBuNwsJZnLt7TbS5Yt9WTkSSbjeKRWbb
//    Private Key  -- >  ED9AE669B37B3951FC1C4A1C41E923FE8B06BEBAB21BE8ED778574162C34704A0C
//    Public Key  -- >  EDBFB7603C8C9F692C380E28E6B5733CB6158869F0FC0DE0A87D9D36DD20D98A00
//    Seed  -- >  sEdVTGv1AYcyWmCcQecmW2RHLEdDs3z


//    X Address  -- >  TVYQx3SRhixvhztNAc6AuMXno6wwfY3VApCQ8TAxGnSFyU4
//    Classic Address  -- >  rMCcNuTcajgw7YTgBy1sys3b89QqjUrMpH
//    Private Key  -- >  009A8559713F87414EEB019C2BDFF98EA9FB85039661E30D06415C2E4C9E086DED
//    Public Key  -- >  039543A0D3004CDA0904A09FB3710251C652D69EA338589279BC849D47A7B019A1
//    Seed  -- >  Это не новый кошелек, у вас уже есть востановительная фраза
}
