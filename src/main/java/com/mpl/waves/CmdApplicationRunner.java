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
	
	@Value("${BUYER_PK}")
	private String BUYER_PK;
	
	@Value("${SELLER_SEED}")
	private String SELLER_SEED;
	
	@Value("${ESCROW_SEED}")
	private String ESCROW_SEED;
	
	@Override
    public void run(String... args) throws IOException, URISyntaxException {
        LOG.info("Starting Escrow smart contract demo");
  
        // Connect to Wave nodes
        Node node = new Node(nodeURL);

        //Get Private keys
        PrivateKeyAccount buyer = PrivateKeyAccount.fromPrivateKey(BUYER_PK, (byte)NETWORK_BYTE);
        PrivateKeyAccount seller = PrivateKeyAccount.fromSeed(SELLER_SEED, 0, (byte)NETWORK_BYTE);
        PrivateKeyAccount escrow= PrivateKeyAccount.fromSeed(ESCROW_SEED,0, (byte)NETWORK_BYTE);

        //Generating random account
        String newAccountSeed = PrivateKeyAccount.generateSeed();
        PrivateKeyAccount newAccount = PrivateKeyAccount.fromSeed(newAccountSeed, 0, (byte)NETWORK_BYTE);
        String newAccountAddress = newAccount.getAddress();
        LOG.info("Create New address to deploy script contract: {}", newAccountAddress);

        LOG.info("Send some waves to new address");
        Transaction tx2 = Transaction.makeTransferTx(buyer, newAccountAddress, 100000000,"WAVES", fee * 4 ,"WAVES", "Sending Waves");
        String sendingTX = node.send(tx2);
        
        LOG.info("Transaction Id: {}", sendingTX);

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Get Addresses and print them
        String buyerAddress = buyer.getAddress();
        String sellerAddress = seller.getAddress();
        //String escrowAddress = escrow.getAddress();
        LOG.info("buyerPK: {}", Base58.encode(buyer.getPublicKey()));
        LOG.info("sellerPK: {}", Base58.encode(seller.getPublicKey()));

        LOG.info("Compile smart contract script");
        // Set Script and compile it
        String script = "let buyerPubKey  = base58'" + Base58.encode(buyer.getPublicKey()) + "';" +
                "let sellerPubKey = base58'" + Base58.encode(seller.getPublicKey()) + "';" +
                "let escrowPubKey = base58'" + Base58.encode(escrow.getPublicKey()) + "';" +
                "let buyerSigned = if(sigVerify(tx.bodyBytes, tx.proofs[0], buyerPubKey)) then 1 else 0;" +
                "let sellerSigned = if(sigVerify(tx.bodyBytes, tx.proofs[1], sellerPubKey)) then 1 else 0;" +
                "let escrowSigned = if(sigVerify(tx.bodyBytes, tx.proofs[2], escrowPubKey)) then 1 else 0;" +
                "buyerSigned + sellerSigned + escrowSigned >= 2";
        String bytecode = node.compileScript(script);
        LOG.info("contract bytecode: {}", bytecode);
        
        // new Account Make Script transaction and send it to the network
        LOG.info("Deploy smart contract to network {}", nodeURL);
        Transaction stx = Transaction.makeScriptTx(newAccount, bytecode, (byte)NETWORK_BYTE, fee);
        String tx = node.send(stx);
        LOG.info("Smart contract is deployed, transaction Id: {}", tx);

        // time required for the transaction to be included into the blockchain
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Buyer make a transfer
        Transaction tx1 = Transaction.makeTransferTx(newAccount, sellerAddress, 1,"WAVES", fee * 4 ,"WAVES", "Sending currency");
        LOG.info("Create buy transaction detail: {}", tx1.getJson());

        //Buyer and Escrow sign the deal with proofs
        String buyerSig =  buyer.sign(tx1);
        LOG.info("Buyer sign buy transaction {}", buyerSig);
        String escrowSig = escrow.sign(tx1);
        LOG.info("Esco sign buy transaction {}", buyerSig);
        tx1 = tx1.withProof(0, buyerSig);
        tx1 = tx1.withProof(2, escrowSig);

        // Send the transfer transaction to the network and print the Tx Id
        String txid = node.send(tx1);
        LOG.info("Send buy transaction Id : {}", txid);
	}
}
