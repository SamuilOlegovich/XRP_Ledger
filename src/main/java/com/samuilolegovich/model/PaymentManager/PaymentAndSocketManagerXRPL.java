package com.samuilolegovich.model.PaymentManager;

import com.samuilolegovich.enums.BooleanEnum;
import com.samuilolegovich.enums.StringEnum;
import com.samuilolegovich.model.PaymentManager.interfaces.PaymentManager;
import com.samuilolegovich.model.PaymentManager.interfaces.Presets;
import com.samuilolegovich.model.PaymentManager.interfaces.SocketManager;
import com.samuilolegovich.model.sockets.SocketXRP;
import com.samuilolegovich.model.sockets.SocketXRPTest;
import com.samuilolegovich.model.sockets.enums.StreamSubscriptionEnum;
import com.samuilolegovich.model.sockets.exceptions.InvalidStateException;
import com.samuilolegovich.model.sockets.interfaces.CommandListener;
import com.samuilolegovich.subscribers.MyStreamSubscriber;
import com.samuilolegovich.subscribers.interfaces.StreamSubscriber;
import com.samuilolegovich.model.wallets.WalletXRP;
import com.samuilolegovich.model.wallets.WalletXRPTest;
import org.java_websocket.client.WebSocketClient;

import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.samuilolegovich.enums.StringEnum.ADDRESS_FOR_SUBSCRIBE_AND_MONITOR;


public class PaymentAndSocketManagerXRPL implements PaymentManager, SocketManager, Presets {
    private static PaymentAndSocketManagerXRPL paymentAndSocketManagerXRPL;

    private Integer NUMBER_OF_SOCKET_RESTARTS = 100;
    private Long TIME_OF_SOCKET_RESTARTS = 20000L;
    private Integer numberOfSocketRestarts;

    private WalletXRPTest walletTest;
    private SocketXRPTest socketTest;

    private SocketXRP socket;
    private WalletXRP wallet;


    public PaymentAndSocketManagerXRPL() {
        this.socketTest = (SocketXRPTest) createNewSocket(false);
        this.socket = (SocketXRP) createNewSocket(true);
        this.walletTest = new WalletXRPTest();
        this.wallet = new WalletXRP();
        paymentAndSocketManagerXRPL = this;
    }

    // подумать может более элегантно сделать проверку на нул - так чтобы не отдавать его дальше ***********************
    public PaymentAndSocketManagerXRPL(boolean isRealOrTest) {
        if (isRealOrTest) {
            this.socket = (SocketXRP) createNewSocket(isRealOrTest);
            this.wallet = new WalletXRP();
            paymentAndSocketManagerXRPL = this;
        } else {
            this.socketTest = (SocketXRPTest) createNewSocket(isRealOrTest);
            this.walletTest = new WalletXRPTest();
            paymentAndSocketManagerXRPL = this;
        }
    }

    private WebSocketClient createNewSocket(boolean b) {
        try {
            return b
                    ? new SocketXRP(StringEnum.WSS_REAL.getValue())
                    : new SocketXRPTest(StringEnum.WSS_TEST.getValue());
        } catch (URISyntaxException e) { e.printStackTrace(); }
        return null;
    }

    public static PaymentAndSocketManagerXRPL getInstances() {
        return paymentAndSocketManagerXRPL;
    }



    // Payment *********************************************************************************************************

    @Override
    public void sendPayment(String address, Integer tag, BigDecimal numberOfXRP, boolean isReal) {
        if (isReal && wallet != null) { wallet.sendPaymentToAddressXRP(address, tag, numberOfXRP);
        } else if (walletTest != null) { walletTest.sendPaymentToAddressXRP(address, tag, numberOfXRP); }
    }

    @Override
    public Map<String, String> connectAnExistingWallet(String seed, boolean isReal) {
        if (isReal && wallet != null) {
            StringEnum.setValue(StringEnum.SEED_REAL, seed);
            return wallet.restoreWallet();
        } else if (walletTest != null) {
            StringEnum.setValue(StringEnum.SEED_TEST, seed);
            return walletTest.restoreWallet();
        }
        return new HashMap<>();
    }

    @Override
    public Map<String, String> createNewWallet(boolean isReal) {
        if (isReal && wallet != null) {
            return wallet.createNewWallet();
        } else if (walletTest != null) {
            return walletTest.createNewWallet();
        }
        return new HashMap<>();
    }

    @Override
    public void updateWallet(boolean isReal) {
        if (isReal && wallet != null) {
            wallet.restoreWallet();
        } else if (walletTest != null) {
            walletTest.restoreWallet();
        }
    }

    @Override
    public void setterWallet(boolean isReal) {

    }

    @Override
    public String getClassicAddress(boolean isReal) {
        if (isReal && wallet != null) {
            return wallet.classicAddress().toString();
        } else if (walletTest != null) {
            return walletTest.classicAddress().toString();
        }
        return StringEnum.WALLET_NOT_ACTIVATED.getValue();
    }

    @Override
    public String getPrivateKey(boolean isReal) {
        if (isReal && wallet != null) {
            return wallet.privateKey().get();
        } else if (walletTest != null) {
            return walletTest.privateKey().get();
        }
        return StringEnum.WALLET_NOT_ACTIVATED.getValue();
    }

    @Override
    public String getXAddress(boolean isReal) {
        if (isReal && wallet != null) {
            return wallet.xAddress().toString();
        } else if (walletTest != null) {
            return walletTest.xAddress().toString();
        }
        return StringEnum.WALLET_NOT_ACTIVATED.getValue();
    }

    @Override
    public String getPublicKey(boolean isReal) {
        if (isReal && wallet != null) {
            return wallet.publicKey();
        } else  if (walletTest != null) {
            return walletTest.publicKey();
        }
        return StringEnum.WALLET_NOT_ACTIVATED.getValue();
    }

    @Override
    public String getSeed(boolean isReal) {
        if (isReal && wallet != null) {
            return wallet.getSeed();
        } else  if (walletTest != null) {
            return walletTest.getSeed();
        }
        return StringEnum.WALLET_NOT_ACTIVATED.getValue();
    }

    @Override
    public boolean isTest(boolean isReal) {
        if (isReal && wallet != null) {
            return wallet.isTest();
        } else  if (walletTest != null) {
            return walletTest.isTest();
        }
        return false;
    }

    @Override
    public BigDecimal getBalance(boolean isReal) {
        BigDecimal allBalance = getAllBalance(isReal);
        BigDecimal activationPayment = new BigDecimal(StringEnum.ACTIVATION_PAYMENT.getValue());
        int compareTo = allBalance.compareTo(activationPayment);
        if (compareTo <= 0) { return new BigDecimal("0.000000"); }
        return allBalance.subtract(activationPayment);
    }

    @Override
    public BigDecimal getAllBalance(boolean isReal) {
        if (isReal && wallet != null) {
            return wallet.getBalance();
        } else if (walletTest != null) {
            return walletTest.getBalance();
        }
        return BigDecimal.ZERO;
    }



    // Socket **********************************************************************************************************

    @Override
    public String sendCommand(String command, CommandListener listener, boolean isReal) throws InvalidStateException {
        if (isReal && socket != null) {
            return socket.sendCommand(command, null, listener);
        } else if (socketTest != null) {
            return socketTest.sendCommand(command, null, listener);
        }
        return StringEnum.WALLET_NOT_ACTIVATED.getValue();
    }

    @Override
    public String sendCommand(String command, Map<String, Object> parameters, CommandListener listener, boolean isReal)
            throws InvalidStateException {
        if (isReal && socket != null) {
            return socket.sendCommand(command, parameters, listener);
        } else if (socketTest != null) {
            socketTest.sendCommand(command, parameters, listener);
        }
        return StringEnum.WALLET_NOT_ACTIVATED.getValue();
    }

    @Override
    public void subscribe(EnumSet<StreamSubscriptionEnum> streams, StreamSubscriber subscriber, boolean isReal)
            throws InvalidStateException {
        if (isReal && socket != null) {
            socket.subscribe(streams, subscriber);
        } else if (socketTest != null) {
            socketTest.subscribe(streams, subscriber);
        }
    }

    @Override
    public void subscribe(EnumSet<StreamSubscriptionEnum> streams, Map<String, Object> parameters,
                          StreamSubscriber subscriber, boolean isReal) throws InvalidStateException {
        if (isReal && socket != null) {
            socket.subscribe(streams, parameters, subscriber);
        } else if (socketTest != null) {
            socketTest.subscribe(streams, parameters, subscriber);
        }
    }

    @Override
    public void unsubscribe(EnumSet<StreamSubscriptionEnum> streams, boolean isReal) throws InvalidStateException {
        if (isReal) {
            socket.unsubscribe(streams);
        } else if (socketTest != null) {
            socketTest.unsubscribe(streams);
        }
    }

    @Override
    public EnumSet<StreamSubscriptionEnum> getActiveSubscriptions(boolean isReal) {
        if (isReal && socket != null) {
            return socket.getActiveSubscriptions();
        } else if (socketTest != null) {
            return socketTest.getActiveSubscriptions();
        }
        return null;
    }

    @Override
    public void closeWhenComplete(boolean isReal) {
        if (isReal) {
            socket.closeWhenComplete();
        } else if (socketTest != null) {
            socketTest.closeWhenComplete();
        }
    }


    public void restartSocket() {
        this.socket = null;
        this.socket = (SocketXRP) createNewSocket(true);
        numberOfSocketRestarts++;

        if (this.socket == null && numberOfSocketRestarts < NUMBER_OF_SOCKET_RESTARTS) {
            try {
                Thread.sleep(TIME_OF_SOCKET_RESTARTS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            restartSocket();
        } else if (numberOfSocketRestarts >= NUMBER_OF_SOCKET_RESTARTS){
            // ОТПРАВИТЬ В СЕНТРИ УВЕДОМЛЕНИЕ ЧТО СОКЕТ НЕ СТАРТАНУЛ НАДО ЧТО_ТО ДЕЛТЬ
        } else {
            numberOfSocketRestarts = 0;
        }
    }



    public boolean startSocket() {
        return connectBlocking();
    }


    private boolean connectBlocking() {
        if (socket != null) {
            try {
                return socket.connectBlocking(3000, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    public void restartSubscribeTo() {
        this.subscribeTo(true);
    }

    private void subscribeTo(boolean b) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("accounts", List.of(ADDRESS_FOR_SUBSCRIBE_AND_MONITOR));
        try {
            if (b) {
                socket.subscribe(EnumSet.of(StreamSubscriptionEnum.ACCOUNT_CHANNELS), parameters, new MyStreamSubscriber());
            }
        } catch (InvalidStateException e) {
            e.printStackTrace();
        }
    }



    // Presets *********************************************************************************************************


    public void setPresets(BooleanEnum enums, boolean b) {
        BooleanEnum.setValue(enums, b);
    }

    public void setPresets(StringEnum enums, String s) {
        StringEnum.setValue(enums, s);
    }
}
