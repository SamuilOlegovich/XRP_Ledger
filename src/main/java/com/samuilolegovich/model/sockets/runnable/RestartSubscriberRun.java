package com.samuilolegovich.model.sockets.runnable;

import com.samuilolegovich.model.PaymentManager.PaymentAndSocketManagerXRPL;



public class RestartSubscriberRun implements Runnable {
    public static volatile boolean FLAG = true;

    private PaymentAndSocketManagerXRPL paymentManager;
    private int time;



    public RestartSubscriberRun() {
        this.time = 3000;
    }

    public RestartSubscriberRun(Integer time) {
        this.time = time;
    }



    @Override
    public void run() {
        FLAG = false;
        startSocket();
        restartSubscribeTo();
        FLAG = true;
    }



    private void startSocket() {
        boolean b = false;

        while (!b) {
            restartSocket();
            try {
                b = paymentManager.startSocket();
            } catch (Exception e) {
                b = false;
                e.printStackTrace();
            }

            try {
                Thread.sleep(time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }



    private void restartSocket() {
        paymentManager = PaymentAndSocketManagerXRPL.getInstances();
        try {
            paymentManager.restartSocket();
        } catch (Exception e) {
            e.printStackTrace();
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