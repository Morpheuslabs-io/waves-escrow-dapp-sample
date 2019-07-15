package com.mpl.waves;

import java.io.IOException;
import java.net.URISyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.wavesplatform.wavesj.Account;
import com.wavesplatform.wavesj.Base58;
import com.wavesplatform.wavesj.Node;
import com.wavesplatform.wavesj.PrivateKeyAccount;
import com.wavesplatform.wavesj.PublicKeyAccount;
import com.wavesplatform.wavesj.Transaction;

@Component
public class CmdApplicationRunner implements CommandLineRunner{
	private static Logger LOG = LoggerFactory.getLogger(CmdApplicationRunner.class);
	
	@Value("${NODE_URL}")
	private String nodeURL;
	
	@Value("${NETWORK_BYTE}")
	private char NETWORK_BYTE;
	
	@Value ("${FEE}")
	private Long fee;
	
	@Value("${BUYER_PRIVATEKEY}")
	private String BUYER_PRIVATEKEY;
	
	@Value("${SELLER_SEED}")
	private String SELLER_SEED;
	
	@Value("${ESCROW_SEED}")
	private String ESCROW_SEED;
	
	@Value("${SMART_ACCOUNT_PUBLIC_KEY}")
	private String smartAccountPublicKey;
	
	@Override
    public void run(String... args) throws IOException, URISyntaxException {
        LOG.info("Starting Escrow smart contract demo");
  
        // Connect to Wave nodes
        Node node = new Node(nodeURL);
        PublicKeyAccount smartAccount = new PublicKeyAccount(smartAccountPublicKey, (byte)NETWORK_BYTE);

        //Get Private keys
        PrivateKeyAccount buyer = PrivateKeyAccount.fromPrivateKey(BUYER_PRIVATEKEY, (byte)NETWORK_BYTE);
        PrivateKeyAccount seller = PrivateKeyAccount.fromSeed(SELLER_SEED, 0, (byte)NETWORK_BYTE);
        PrivateKeyAccount escrow = PrivateKeyAccount.fromSeed(ESCROW_SEED,0, (byte)NETWORK_BYTE);

        String newAccountAddress = smartAccount.getAddress();
        LOG.info("Smart Contract Address: {}", newAccountAddress);

        //Get Addresses and print them
        String buyerAddress = buyer.getAddress();
        String sellerAddress = seller.getAddress();
        
        // Buyer make a transfer
        LOG.info("Buyer sell money and request for goods, example 10 waves");
        Transaction buyTx = Transaction.makeTransferTx(buyer, smartAccount.getAddress(), 1000000000,"WAVES", fee * 4 ,"WAVES", "Sending Money");
        String sendingTX = node.send(buyTx);
        LOG.info("Transaction Id: {}", sendingTX);
        
        
        LOG.info("Seller request withraw money");
        Transaction tx1 = Transaction.makeTransferTx(smartAccount, sellerAddress, 1000000000,"WAVES", fee * 4 ,"WAVES", "Sending Money");

        //Buyer and Seller sign the deal with proofs
        String buyerSig =  buyer.sign(tx1);
        LOG.info("Buyer accept transaction by signning it: {}", buyerSig);
        
        String sellerSig =  seller.sign(tx1);
        LOG.info("Seller sign with transaction: {}", sellerSig);
        tx1 = tx1.withProof(0, buyerSig);
        tx1 = tx1.withProof(1, sellerSig);
        
        LOG.info("Transaction after signs: {}", tx1.getJson());

        String txid = node.send(tx1);
        LOG.info("Broadcast transaction to network transaction Id : {}", txid);
	}
}
