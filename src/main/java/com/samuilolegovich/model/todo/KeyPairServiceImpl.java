package com.samuilolegovich.model.todo;

import org.xrpl.xrpl4j.codec.addresses.UnsignedByteArray;
import org.xrpl.xrpl4j.keypairs.KeyPair;
import org.xrpl.xrpl4j.keypairs.KeyPairService;
import org.xrpl.xrpl4j.model.transactions.Address;

public class KeyPairServiceImpl implements KeyPairService {
    @Override
    public String generateSeed() {
        return null;
    }

    @Override
    public String generateSeed(UnsignedByteArray unsignedByteArray) {
        return null;
    }

    @Override
    public KeyPair deriveKeyPair(String s) {
        return null;
    }

    @Override
    public String sign(UnsignedByteArray unsignedByteArray, String s) {
        return null;
    }

    @Override
    public String sign(String s, String s1) {
        return null;
    }

    @Override
    public boolean verify(UnsignedByteArray unsignedByteArray, String s, String s1) {
        return false;
    }

    @Override
    public boolean verify(String s, String s1, String s2) {
        return false;
    }

    @Override
    public Address deriveAddress(String s) {
        return null;
    }

    @Override
    public Address deriveAddress(UnsignedByteArray unsignedByteArray) {
        return null;
    }
}
