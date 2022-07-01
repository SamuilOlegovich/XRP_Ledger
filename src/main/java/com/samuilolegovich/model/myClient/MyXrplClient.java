package com.samuilolegovich.model.myClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xrpl.xrpl4j.client.JsonRpcClientErrorException;
import org.xrpl.xrpl4j.client.JsonRpcRequest;
import org.xrpl.xrpl4j.codec.binary.XrplBinaryCodec;
import org.xrpl.xrpl4j.keypairs.DefaultKeyPairService;
import org.xrpl.xrpl4j.keypairs.KeyPairService;
import org.xrpl.xrpl4j.model.client.XrplMethods;
import org.xrpl.xrpl4j.model.client.accounts.AccountChannelsRequestParams;
import org.xrpl.xrpl4j.model.client.accounts.AccountChannelsResult;
import org.xrpl.xrpl4j.model.client.accounts.AccountInfoRequestParams;
import org.xrpl.xrpl4j.model.client.accounts.AccountInfoResult;
import org.xrpl.xrpl4j.model.client.accounts.AccountLinesRequestParams;
import org.xrpl.xrpl4j.model.client.accounts.AccountLinesResult;
import org.xrpl.xrpl4j.model.client.accounts.AccountObjectsRequestParams;
import org.xrpl.xrpl4j.model.client.accounts.AccountObjectsResult;
import org.xrpl.xrpl4j.model.client.accounts.AccountTransactionsRequestParams;
import org.xrpl.xrpl4j.model.client.accounts.AccountTransactionsResult;
import org.xrpl.xrpl4j.model.client.channels.ChannelVerifyRequestParams;
import org.xrpl.xrpl4j.model.client.channels.ChannelVerifyResult;
import org.xrpl.xrpl4j.model.client.fees.FeeResult;
import org.xrpl.xrpl4j.model.client.ledger.LedgerRequestParams;
import org.xrpl.xrpl4j.model.client.ledger.LedgerResult;
import org.xrpl.xrpl4j.model.client.path.RipplePathFindRequestParams;
import org.xrpl.xrpl4j.model.client.path.RipplePathFindResult;
import org.xrpl.xrpl4j.model.client.server.ServerInfo;
import org.xrpl.xrpl4j.model.client.server.ServerInfoResult;
import org.xrpl.xrpl4j.model.client.transactions.SignedTransaction;
import org.xrpl.xrpl4j.model.client.transactions.SubmitMultiSignedRequestParams;
import org.xrpl.xrpl4j.model.client.transactions.SubmitMultiSignedResult;
import org.xrpl.xrpl4j.model.client.transactions.SubmitRequestParams;
import org.xrpl.xrpl4j.model.client.transactions.SubmitResult;
import org.xrpl.xrpl4j.model.client.transactions.TransactionRequestParams;
import org.xrpl.xrpl4j.model.client.transactions.TransactionResult;
import org.xrpl.xrpl4j.model.jackson.ObjectMapperFactory;
import org.xrpl.xrpl4j.model.transactions.AccountDelete;
import org.xrpl.xrpl4j.model.transactions.AccountSet;
import org.xrpl.xrpl4j.model.transactions.Address;
import org.xrpl.xrpl4j.model.transactions.CheckCancel;
import org.xrpl.xrpl4j.model.transactions.CheckCash;
import org.xrpl.xrpl4j.model.transactions.CheckCreate;
import org.xrpl.xrpl4j.model.transactions.DepositPreAuth;
import org.xrpl.xrpl4j.model.transactions.EscrowCancel;
import org.xrpl.xrpl4j.model.transactions.EscrowCreate;
import org.xrpl.xrpl4j.model.transactions.EscrowFinish;
import org.xrpl.xrpl4j.model.transactions.Hash256;
import org.xrpl.xrpl4j.model.transactions.OfferCancel;
import org.xrpl.xrpl4j.model.transactions.OfferCreate;
import org.xrpl.xrpl4j.model.transactions.Payment;
import org.xrpl.xrpl4j.model.transactions.PaymentChannelClaim;
import org.xrpl.xrpl4j.model.transactions.PaymentChannelCreate;
import org.xrpl.xrpl4j.model.transactions.PaymentChannelFund;
import org.xrpl.xrpl4j.model.transactions.SetRegularKey;
import org.xrpl.xrpl4j.model.transactions.SignerListSet;
import org.xrpl.xrpl4j.model.transactions.Transaction;
import org.xrpl.xrpl4j.model.transactions.TrustSet;
import org.xrpl.xrpl4j.model.transactions.XrpCurrencyAmount;
import org.xrpl.xrpl4j.wallet.Wallet;


public class MyXrplClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(MyXrplClient.class);

    private final XrplBinaryCodec binaryCodec;
    private final KeyPairService keyPairService;
    private final ObjectMapper objectMapper;
    private final ApiClient apiClient;



    public MyXrplClient(String stringURL) {
        this.objectMapper = ObjectMapperFactory.create();
        this.binaryCodec = new XrplBinaryCodec();
        this.keyPairService = DefaultKeyPairService.getInstance();
        this.apiClient = new ApiClient(stringURL);
    }



    public <T extends Transaction> SubmitResult<T> submit(
            Wallet wallet,
            T unsignedTransaction
    ) throws JsonRpcClientErrorException {
        Preconditions.checkArgument(
                unsignedTransaction.signingPublicKey().isPresent(),
                "Transaction.signingPublicKey() must be set."
        );

        SignedTransaction<T> signedTransaction = signTransaction(wallet, unsignedTransaction);
        return submit(signedTransaction);
    }



    public <T extends Transaction> SubmitResult<T> submit(SignedTransaction<T> signedTransaction) throws JsonRpcClientErrorException {
        JsonRpcRequest request = JsonRpcRequest.builder()
                .method(XrplMethods.SUBMIT)
                .addParams(SubmitRequestParams.of(signedTransaction.signedTransactionBlob()))
                .build();
        JavaType resultType = objectMapper.getTypeFactory()
                .constructParametricType(SubmitResult.class, signedTransaction.signedTransaction().getClass());
        return apiClient.send(request, resultType);
    }



    public <T extends Transaction> SubmitResult<T> submit(
            final org.xrpl.xrpl4j.crypto.signing.SignedTransaction signedTransaction
    ) throws JsonRpcClientErrorException, JsonProcessingException {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("About to submit signedTransaction: {}", signedTransaction);
        }

        String signedJson = objectMapper.writeValueAsString(signedTransaction.signedTransaction());
        String signedBlob = binaryCodec.encode(signedJson); // <-- txBlob must be binary-encoded.
        JsonRpcRequest request = JsonRpcRequest.builder()
                .method(XrplMethods.SUBMIT)
                .addParams(SubmitRequestParams.of(signedBlob))
                .build();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("About to submit JsonRpcRequest: {}", request);
        }

        JavaType resultType = objectMapper.getTypeFactory()
                .constructParametricType(SubmitResult.class, signedTransaction.unsignedTransaction().getClass());
        return apiClient.send(request, resultType);
    }



    public <T extends Transaction> SubmitMultiSignedResult<T> submitMultisigned(
            T transaction
    ) throws JsonRpcClientErrorException {
        JsonRpcRequest request = JsonRpcRequest.builder()
                .method(XrplMethods.SUBMIT_MULTISIGNED)
                .addParams(SubmitMultiSignedRequestParams.of(transaction))
                .build();
        JavaType resultType = objectMapper.getTypeFactory().constructParametricType(
                SubmitMultiSignedResult.class, transaction.getClass()
        );
        return apiClient.send(request, resultType);
    }



    public FeeResult fee() throws JsonRpcClientErrorException {
        JsonRpcRequest request = JsonRpcRequest.builder()
                .method(XrplMethods.FEE)
                .build();
        return apiClient.send(request, FeeResult.class);
    }



    public ServerInfo serverInfo() throws JsonRpcClientErrorException {
        JsonRpcRequest request = JsonRpcRequest.builder()
                .method(XrplMethods.SERVER_INFO)
                .build();
        return apiClient.send(request, ServerInfoResult.class).info();
    }



    public AccountChannelsResult accountChannels(AccountChannelsRequestParams params) throws JsonRpcClientErrorException {
        JsonRpcRequest request = JsonRpcRequest.builder()
                .method(XrplMethods.ACCOUNT_CHANNELS)
                .addParams(params)
                .build();
        return apiClient.send(request, AccountChannelsResult.class);
    }



    public AccountInfoResult accountInfo(AccountInfoRequestParams params) throws JsonRpcClientErrorException {
        JsonRpcRequest request = JsonRpcRequest.builder()
                .method(XrplMethods.ACCOUNT_INFO)
                .addParams(params)
                .build();
        return apiClient.send(request, AccountInfoResult.class);
    }



    public AccountObjectsResult accountObjects(AccountObjectsRequestParams params) throws JsonRpcClientErrorException {
        JsonRpcRequest request = JsonRpcRequest.builder()
                .method(XrplMethods.ACCOUNT_OBJECTS)
                .addParams(params)
                .build();
        return apiClient.send(request, AccountObjectsResult.class);
    }



    public AccountTransactionsResult accountTransactions(Address address) throws JsonRpcClientErrorException {
        return accountTransactions(AccountTransactionsRequestParams.builder()
                .account(address)
                .build());
    }



    public AccountTransactionsResult accountTransactions(AccountTransactionsRequestParams params)
            throws JsonRpcClientErrorException {
        JsonRpcRequest request = JsonRpcRequest.builder()
                .method(XrplMethods.ACCOUNT_TX)
                .addParams(params)
                .build();
        return apiClient.send(request, AccountTransactionsResult.class);
    }



    public <T extends Transaction> TransactionResult<T> transaction(
            TransactionRequestParams params,
            Class<T> transactionType
    ) throws JsonRpcClientErrorException {
        JsonRpcRequest request = JsonRpcRequest.builder()
                .method(XrplMethods.TX)
                .addParams(params)
                .build();

        JavaType resultType = objectMapper.getTypeFactory().constructParametricType(TransactionResult.class, transactionType);
        return apiClient.send(request, resultType);
    }



    public LedgerResult ledger(LedgerRequestParams params) throws JsonRpcClientErrorException {
        JsonRpcRequest request = JsonRpcRequest.builder()
                .method(XrplMethods.LEDGER)
                .addParams(params)
                .build();

        return apiClient.send(request, LedgerResult.class);
    }



    public RipplePathFindResult ripplePathFind(RipplePathFindRequestParams params) throws JsonRpcClientErrorException {
        JsonRpcRequest request = JsonRpcRequest.builder()
                .method(XrplMethods.RIPPLE_PATH_FIND)
                .addParams(params)
                .build();
        return apiClient.send(request, RipplePathFindResult.class);
    }



    public AccountLinesResult accountLines(AccountLinesRequestParams params) throws JsonRpcClientErrorException {
        JsonRpcRequest request = JsonRpcRequest.builder()
                .method(XrplMethods.ACCOUNT_LINES)
                .addParams(params)
                .build();
        return apiClient.send(request, AccountLinesResult.class);
    }



    public ChannelVerifyResult channelVerify(Hash256 channelId, XrpCurrencyAmount amount,
            String signature, String publicKey) throws JsonRpcClientErrorException {

        ChannelVerifyRequestParams params = ChannelVerifyRequestParams.builder()
                .channelId(channelId)
                .amount(amount)
                .signature(signature)
                .publicKey(publicKey)
                .build();

        JsonRpcRequest request = JsonRpcRequest.builder()
                .method(XrplMethods.CHANNEL_VERIFY)
                .addParams(params)
                .build();
        return apiClient.send(request, ChannelVerifyResult.class);
    }

    public <T extends Transaction> SignedTransaction<T> signTransaction(Wallet wallet, T unsignedTransaction) {
        try {
            String unsignedJson = objectMapper.writeValueAsString(unsignedTransaction);

            String unsignedBinaryHex = binaryCodec.encodeForSigning(unsignedJson);
            String signature = keyPairService.sign(unsignedBinaryHex, wallet.privateKey()
                    .orElseThrow(() -> new RuntimeException("Wallet must provide a private key to sign the transaction.")));

            T signedTransaction = (T) addSignature(unsignedTransaction, signature);

            String signedJson = objectMapper.writeValueAsString(signedTransaction);
            String signedBinary = binaryCodec.encode(signedJson);
            return SignedTransaction.<T>builder()
                    .signedTransaction(signedTransaction)
                    .signedTransactionBlob(signedBinary)
                    .build();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }



    private Transaction addSignature(Transaction unsignedTransaction, String signature) {
        if (Payment.class.isAssignableFrom(unsignedTransaction.getClass())) {
            return Payment.builder().from((Payment) unsignedTransaction)
                    .transactionSignature(signature)
                    .build();
        } else if (AccountSet.class.isAssignableFrom(unsignedTransaction.getClass())) {
            return AccountSet.builder().from((AccountSet) unsignedTransaction)
                    .transactionSignature(signature)
                    .build();
        } else if (AccountDelete.class.isAssignableFrom(unsignedTransaction.getClass())) {
            return AccountDelete.builder().from((AccountDelete) unsignedTransaction)
                    .transactionSignature(signature)
                    .build();
        } else if (CheckCancel.class.isAssignableFrom(unsignedTransaction.getClass())) {
            return CheckCancel.builder().from((CheckCancel) unsignedTransaction)
                    .transactionSignature(signature)
                    .build();
        } else if (CheckCash.class.isAssignableFrom(unsignedTransaction.getClass())) {
            return CheckCash.builder().from((CheckCash) unsignedTransaction)
                    .transactionSignature(signature)
                    .build();
        } else if (CheckCreate.class.isAssignableFrom(unsignedTransaction.getClass())) {
            return CheckCreate.builder().from((CheckCreate) unsignedTransaction)
                    .transactionSignature(signature)
                    .build();
        } else if (DepositPreAuth.class.isAssignableFrom(unsignedTransaction.getClass())) {
            return DepositPreAuth.builder().from((DepositPreAuth) unsignedTransaction)
                    .transactionSignature(signature)
                    .build();
        } else if (EscrowCreate.class.isAssignableFrom(unsignedTransaction.getClass())) {
            return EscrowCreate.builder().from((EscrowCreate) unsignedTransaction)
                    .transactionSignature(signature)
                    .build();
        } else if (EscrowCancel.class.isAssignableFrom(unsignedTransaction.getClass())) {
            return EscrowCancel.builder().from((EscrowCancel) unsignedTransaction)
                    .transactionSignature(signature)
                    .build();
        } else if (EscrowFinish.class.isAssignableFrom(unsignedTransaction.getClass())) {
            return EscrowFinish.builder().from((EscrowFinish) unsignedTransaction)
                    .transactionSignature(signature)
                    .build();
        } else if (TrustSet.class.isAssignableFrom(unsignedTransaction.getClass())) {
            return TrustSet.builder().from((TrustSet) unsignedTransaction)
                    .transactionSignature(signature)
                    .build();
        } else if (OfferCreate.class.isAssignableFrom(unsignedTransaction.getClass())) {
            return OfferCreate.builder().from((OfferCreate) unsignedTransaction)
                    .transactionSignature(signature)
                    .build();
        } else if (OfferCancel.class.isAssignableFrom(unsignedTransaction.getClass())) {
            return OfferCancel.builder().from((OfferCancel) unsignedTransaction)
                    .transactionSignature(signature)
                    .build();
        } else if (PaymentChannelCreate.class.isAssignableFrom(unsignedTransaction.getClass())) {
            return PaymentChannelCreate.builder().from((PaymentChannelCreate) unsignedTransaction)
                    .transactionSignature(signature)
                    .build();
        } else if (PaymentChannelClaim.class.isAssignableFrom(unsignedTransaction.getClass())) {
            return PaymentChannelClaim.builder().from((PaymentChannelClaim) unsignedTransaction)
                    .transactionSignature(signature)
                    .build();
        } else if (PaymentChannelFund.class.isAssignableFrom(unsignedTransaction.getClass())) {
            return PaymentChannelFund.builder().from((PaymentChannelFund) unsignedTransaction)
                    .transactionSignature(signature)
                    .build();
        } else if (SetRegularKey.class.isAssignableFrom(unsignedTransaction.getClass())) {
            return SetRegularKey.builder().from((SetRegularKey) unsignedTransaction)
                    .transactionSignature(signature)
                    .build();
        } else if (SignerListSet.class.isAssignableFrom(unsignedTransaction.getClass())) {
            return SignerListSet.builder().from((SignerListSet) unsignedTransaction)
                    .transactionSignature(signature)
                    .build();
        }

        throw new IllegalArgumentException("Signing fields could not be added to the unsignedTransaction.");
    }
}

