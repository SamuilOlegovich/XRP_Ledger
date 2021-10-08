package com.samuilolegovich.model;

import lombok.Builder;
import org.xrpl.xrpl4j.codec.addresses.UnsignedByteArray;
import org.xrpl.xrpl4j.crypto.KeyMetadata;
import org.xrpl.xrpl4j.crypto.KeyStoreType;
import org.xrpl.xrpl4j.crypto.PrivateKey;
import org.xrpl.xrpl4j.crypto.PublicKey;
import org.xrpl.xrpl4j.crypto.signing.*;
import org.xrpl.xrpl4j.keypairs.KeyPairService;

public class SingleKeySignatureService extends AbstractSignatureService implements SignatureService
{
    private PrivateKey privateKey;

    public SingleKeySignatureService(PrivateKey privateKey) {
        super(null, null, null);
        this.privateKey = privateKey;

    }

    public SingleKeySignatureService(KeyStoreType keyStoreType,
                                     SignatureUtils signatureUtils,
                                     KeyPairService keyPairService) {
        super(keyStoreType, signatureUtils, keyPairService);
    }

    @Override
    protected Signature edDsaSign(KeyMetadata keyMetadata, UnsignedByteArray unsignedByteArray) {
        return null;
    }

    @Override
    protected Signature ecDsaSign(KeyMetadata keyMetadata, UnsignedByteArray unsignedByteArray) {
        return null;
    }

    @Override
    protected boolean edDsaVerify(KeyMetadata keyMetadata, SignedTransaction signedTransaction, UnsignedByteArray unsignedByteArray) {
        return false;
    }

    @Override
    protected boolean ecDsaVerify(KeyMetadata keyMetadata, SignedTransaction signedTransaction, UnsignedByteArray unsignedByteArray) {
        return false;
    }




    @Override       // implements SignatureService
    public PublicKey getPublicKey(KeyMetadata keyMetadata) {
        return null;
    }
}
