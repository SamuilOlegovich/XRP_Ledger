package com.samuilolegovich.model.sockets.runnable;

import com.samuilolegovich.model.PaymentManager.PaymentAndSocketManagerXRPL;



public class RestartSubscriberRun implements Runnable {
    public static volatile boolean FLAG = true;

    private PaymentAndSocketManagerXRPL paymentManager;
    private int time;



    public RestartSubscriberRun() {
        this.time = 1000;
    }

    public RestartSubscriberRun(Integer time) {
        this.time = time;
    }

    public RestartSubscriberRun(String nameModule) {
        this.time = 1000;
    }



    @Override
    public void run() {
        FLAG = false;
        restartSocket();
        startSocket();
        restartSubscribeTo();
        FLAG = true;
    }



    private void restartSocket() {
        paymentManager = PaymentAndSocketManagerXRPL.getInstances();
        try {
            paymentManager.restartSocket();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startSocket() {
        boolean b = false;

        while (!b) {
            try {
                b = paymentManager.startSocket();
            } catch (Exception e) {
                b = false;
                restartSocket();
                e.printStackTrace();
            }

            try {
                Thread.sleep(time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void restartSubscribeTo() {
        try {
            paymentManager.restartSubscribeTo();
        } catch (Exception e) {
            FLAG = true;
            RestartSubscriberRun restartSubscriberRun = new RestartSubscriberRun(10000);
            new Thread(restartSubscriberRun).start();
            e.printStackTrace();
        }
    }
}