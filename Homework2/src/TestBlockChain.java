import org.junit.Test;
import org.junit.Before;
import org.junit.After;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.*;
import java.util.ArrayList;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.CoreMatchers.is;

import static org.junit.Assert.assertThat;


import static org.junit.Assert.assertEquals;


public class TestBlockChain {

    public int nPeople;
    public int nUTXOTx;
    public int maxUTXOTxOutput;
    public double maxValue;
    public int nTxPerTest;
    public int maxInput;
    public int maxOutput;
    List<KeyPair> people;


    @Before
    public void DropboxTestBlockChain() throws FileNotFoundException, IOException, NoSuchAlgorithmException {

        this.nPeople = 20;
        this.nUTXOTx = 20;
        this.maxUTXOTxOutput = 20;
        this.maxValue = 10;
        this.nTxPerTest = 50;
        this.maxInput = 4;
        this.maxOutput = 20;
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        people = new ArrayList<KeyPair>();
        for (int i = 0; i < nPeople; i++) {
            people.add(generator.generateKeyPair());
        }

    }


    @After
    public void tearDownAfterClass() throws Exception {
        System.out.println("release the source");
    }


    @Test
    public void test1() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        System.out.println("Existing of a genesis block");
        Block genesisBlock = new Block(null, people.get(0).getPublic());
        genesisBlock.finalize();
        BlockChain blockChain = new BlockChain(genesisBlock);
        assertEquals(true, blockChain.getMaxHeightUTXOPool().contains(new UTXO(genesisBlock.getCoinbase().getHash(), 0)));
    }

    @Test
    public void test2() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        System.out.println("Process a new genesis block--no double genesis block");
        Block genesisBlock = new Block(null, people.get(0).getPublic());
        genesisBlock.finalize();
        BlockChain blockChain = new BlockChain(genesisBlock);
        BlockHandler blockHandler = new BlockHandler(blockChain);
        Block genesisblock = new Block(null, people.get(1).getPublic());
        genesisblock.finalize();
        assertEquals(false, blockHandler.processBlock(genesisblock));
    }


    @Test
    public void test3() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        System.out.println("Process a block with an invalid prevBlockHash");
        Block genesisBlock = new Block(null, people.get(0).getPublic());
        genesisBlock.finalize();
        BlockChain blockChain = new BlockChain(genesisBlock);
        BlockHandler blockHandler = new BlockHandler(blockChain);
        byte[] hash = genesisBlock.getHash();
        byte[] wronghash = Arrays.copyOf(hash, hash.length);
        wronghash[0]++;
        Block block = new Block(wronghash, people.get(1).getPublic());
        block.finalize();
        assertEquals(false, blockHandler.processBlock(block));
    }


    @Test
    public void test4() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {

        System.out.println("Process a block with no transactions");
        PublicKey k1 = (PublicKey) people.get(0).getPublic();
        Block genesisBlock = new Block(null, k1);
        genesisBlock.finalize();
        BlockChain blockChain = new BlockChain(genesisBlock);
        BlockHandler blockHandler = new BlockHandler(blockChain);
        Block block = new Block(genesisBlock.getHash(), (PublicKey) people.get(1).getPublic());
        block.finalize();
        assertEquals(true, blockHandler.processBlock(block));
        assertEquals(block, blockHandler.getBlockChain().getMaxHeightBlock());
    }


    @Test
    public void test5() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        System.out.println("Process a block with a single valid transaction");
        Block genesisBlock = new Block(null, people.get(0).getPublic());
        genesisBlock.finalize();
        BlockChain blockChain = new BlockChain(genesisBlock);
        BlockHandler blockHandler = new BlockHandler(blockChain);
        Block block = new Block(genesisBlock.getHash(), people.get(1).getPublic());
        Transaction Tx = new Transaction();
        Tx.addInput(genesisBlock.getCoinbase().getHash(), 0);
        Tx.addOutput(5, people.get(1).getPublic());
        Signature sig = Signature.getInstance("SHA256withRSA");
        sig.initSign(people.get(0).getPrivate());
        sig.update(Tx.getRawDataToSign(0));
        byte[] a = sig.sign();
        Tx.addSignature(a, 0);
        Tx.finalize();
        block.addTransaction(Tx);
        block.finalize();
        assertEquals(true, blockHandler.processBlock(block));
        assertEquals(block, blockHandler.getBlockChain().getMaxHeightBlock());
    }

    @Test
    public void test6() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        System.out.println("Process a block with many valid transactions");
        Block genesisBlock = new Block(null, people.get(0).getPublic());
        genesisBlock.finalize();
        BlockChain blockChain = new BlockChain(genesisBlock);
        BlockHandler blockHandler = new BlockHandler(blockChain);
        Block block = new Block(genesisBlock.getHash(), people.get(1).getPublic());
        Transaction tx1 = new Transaction();
        tx1.addInput(genesisBlock.getCoinbase().getHash(), 0);
        tx1.addOutput(1, people.get(1).getPublic());
        Signature sig = Signature.getInstance("SHA256withRSA");
        sig.initSign(people.get(0).getPrivate());
        sig.update(tx1.getRawDataToSign(0));
        byte[] a = sig.sign();
        tx1.addSignature(a, 0);
        tx1.finalize();
        block.addTransaction(tx1);
        Transaction tx2 = new Transaction();
        tx2.addInput(tx1.getHash(), 0);
        tx2.addOutput(1, people.get(0).getPublic());
        Signature sig1 = Signature.getInstance("SHA256withRSA");
        sig1.initSign(people.get(1).getPrivate());
        sig1.update(tx2.getRawDataToSign(0));
        byte[] b = sig1.sign();
        tx2.addSignature(b, 0);
        tx2.finalize();
        block.addTransaction(tx2);
        block.finalize();
        assertEquals(true, blockHandler.processBlock(block));
        assertEquals(block, blockHandler.getBlockChain().getMaxHeightBlock());
    }

    @Test
    public void test7() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        System.out.println("Process a block with invalid transactions -double spending");
        PublicKey k1 = people.get(0).getPublic();
        Block genesisBlock = new Block(null, k1);
        genesisBlock.finalize();
        BlockChain blockChain = new BlockChain(genesisBlock);
        BlockHandler blockHandler = new BlockHandler(blockChain);
        List<Transaction> transactions = new ArrayList<Transaction>();
        Transaction tx1 = new Transaction();
        tx1.addInput(genesisBlock.getCoinbase().getHash(), 0);
        tx1.addOutput(25, people.get(1).getPublic());
        Signature sig = Signature.getInstance("SHA256withRSA");
        sig.initSign(people.get(0).getPrivate());
        sig.update(tx1.getRawDataToSign(0));
        byte[] a = sig.sign();
        tx1.addSignature(a, 0);
        tx1.finalize();
        transactions.add(tx1);
        blockHandler.processTx(tx1);
        Transaction tx2 = new Transaction();
        tx2.addInput(genesisBlock.getCoinbase().getHash(), 0);
        tx2.addOutput(25, people.get(2).getPublic());
        Signature sig1 = Signature.getInstance("SHA256withRSA");
        sig1.initSign(people.get(0).getPrivate());
        sig1.update(tx1.getRawDataToSign(0));
        byte[] b = sig1.sign();
        tx2.addSignature(b, 0);
        tx2.finalize();
        blockHandler.processTx(tx2);
        transactions.add(tx2);
       Block block = blockHandler.createBlock(people.get(1).getPublic());
        assertEquals(block, blockHandler.getBlockChain().getMaxHeightBlock());
   //     assertEquals(true, blockHandler.getBlockChain().getMaxHeightUTXOPool().contains(new UTXO(transactions.get(0).getHash(), 0)));
        //       assertEquals(false, blockHandler.getBlockChain().getMaxHeightUTXOPool().contains(new UTXO(transactions.get(1).getHash(), 0)));

    }


    @Test
    public void test8() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        System.out.println("Process several block with invalid transactions");
        PublicKey k1 = people.get(0).getPublic();
        Block genesisBlock = new Block(null, k1);
        genesisBlock.finalize();
        BlockChain blockChain = new BlockChain(genesisBlock);
        BlockHandler blockHandler = new BlockHandler(blockChain);
        List<Transaction> transactions = new ArrayList<Transaction>();
        for (int i = 1; i < nPeople; i++) {
            Transaction tx1 = new Transaction();
            tx1.addInput(genesisBlock.getCoinbase().getHash(), 0);
            tx1.addOutput(1, people.get(i).getPublic());
            Signature sig = Signature.getInstance("SHA256withRSA");
            sig.initSign(people.get(0).getPrivate());
            sig.update(tx1.getRawDataToSign(0));
            byte[] a = sig.sign();
            tx1.addSignature(a, 0);
            tx1.finalize();
            transactions.add(tx1);
            blockHandler.processTx(tx1);
            blockHandler.createBlock(people.get(i).getPublic());
            if(i==1) {
                assertEquals(true, blockHandler.getBlockChain().getMaxHeightUTXOPool().contains(new UTXO(transactions.get(i - 1).getHash(), 0)));
            }
            else
                assertEquals(false, blockHandler.getBlockChain().getMaxHeightUTXOPool().contains(new UTXO(transactions.get(i - 1).getHash(), 0)));
        }
    }

    //Then we will talk about the blockchain fork.

    @Test
    public void test9() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        System.out.println("Process blockchain fork after the gensisblock with valid transaction and will the oldest block");
        PublicKey k1 = people.get(0).getPublic();
        Block genesisBlock = new Block(null, k1);
        genesisBlock.finalize();
        BlockChain blockChain = new BlockChain(genesisBlock);
        BlockHandler blockHandler = new BlockHandler(blockChain);
        Block block = new Block(genesisBlock.getHash(), people.get(1).getPublic());
        Transaction Tx = new Transaction();
        Tx.addInput(genesisBlock.getCoinbase().getHash(), 0);
        Tx.addOutput(5, people.get(1).getPublic());
        Signature sig = Signature.getInstance("SHA256withRSA");
        sig.initSign(people.get(0).getPrivate());
        sig.update(Tx.getRawDataToSign(0));
        byte[] a = sig.sign();
        Tx.addSignature(a, 0);
        Tx.finalize();
        block.addTransaction(Tx);
        block.finalize();
        blockHandler.processBlock(block);
        Block block1 = new Block(genesisBlock.getHash(), people.get(1).getPublic());
        block1.finalize();
        blockHandler.processBlock(block1);
        assertEquals(2,blockHandler.getBlockChain().getMaxHeight());
        assertEquals(block, blockHandler.getBlockChain().getMaxHeightBlock());
        Block block2 = new Block(block1.getHash(), people.get(1).getPublic());
        block2.finalize();
        blockHandler.processBlock(block2);
        assertEquals(3,blockHandler.getBlockChain().getMaxHeight());
        assertEquals(block2, blockHandler.getBlockChain().getMaxHeightBlock());
    }

    @Test
    public void test10() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        System.out.println("Process valid and invalid blockchain fork when considering CUT_OFF_AGE");
        PublicKey k1 = people.get(0).getPublic();
        Block genesisBlock = new Block(null, k1);
        genesisBlock.finalize();
        BlockChain blockChain = new BlockChain(genesisBlock);
        BlockHandler blockHandler = new BlockHandler(blockChain);
        Block block = new Block(genesisBlock.getHash(), people.get(1).getPublic());
        block.finalize();
        blockHandler.processBlock(block);
        Block block1 = new Block(block.getHash(), people.get(2).getPublic());
        block1.finalize();
        blockHandler.processBlock(block1);
        Block block2 = new Block(block1.getHash(), people.get(3).getPublic());
        block2.finalize();
        blockHandler.processBlock(block2);
        Block block3 = new Block(block2.getHash(), people.get(4).getPublic());
        block3.finalize();
        blockHandler.processBlock(block3);
        Block block4 = new Block(block3.getHash(), people.get(5).getPublic());
        block4.finalize();
        blockHandler.processBlock(block4);
        Block block5 = new Block(block4.getHash(), people.get(6).getPublic());
        block5.finalize();
        blockHandler.processBlock(block5);
        Block block6 = new Block(block5.getHash(), people.get(7).getPublic());
        block6.finalize();
        blockHandler.processBlock(block6);
        Block block7 = new Block(block6.getHash(), people.get(8).getPublic());
        block7.finalize();
        blockHandler.processBlock(block7);
        Block block8 = new Block(block7.getHash(), people.get(9).getPublic());
        block8.finalize();
        blockHandler.processBlock(block8);
        Block block9 = new Block(block8.getHash(), people.get(10).getPublic());
        block9.finalize();
        blockHandler.processBlock(block9);
        assertEquals(11,blockHandler.getBlockChain().getMaxHeight());
        Block block10 = new Block(block9.getHash(), people.get(11).getPublic());
        block10.finalize();
        blockHandler.processBlock(block10);
        assertEquals(12,blockHandler.getBlockChain().getMaxHeight());
        Block blocka = new Block(block.getHash(), people.get(12).getPublic());
        blocka.finalize();
        assertEquals(12,blockHandler.getBlockChain().getMaxHeight());
        assertEquals(true, blockHandler.processBlock(blocka));
        Block blockb = new Block(genesisBlock.getHash(), people.get(12).getPublic());
        blockb.finalize();
        blockHandler.processBlock(blockb);
        assertEquals(false, blockHandler.processBlock(blockb));
    }

    @Test
    public void test11() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        System.out.println("Add Transaction and remove Transaction and then test Transaction Pool");
        PublicKey k1 = people.get(0).getPublic();
        Block genesisBlock = new Block(null, k1);
        genesisBlock.finalize();
        BlockChain blockChain = new BlockChain(genesisBlock);
        BlockHandler blockHandler = new BlockHandler(blockChain);
        Block block = new Block(genesisBlock.getHash(), people.get(1).getPublic());
        Transaction Tx = new Transaction();
        TransactionPool txpool = new TransactionPool();
        Tx.addInput(genesisBlock.getCoinbase().getHash(), 0);
        Tx.addOutput(5, people.get(1).getPublic());
        Signature sig = Signature.getInstance("SHA256withRSA");
        sig.initSign(people.get(0).getPrivate());
        sig.update(Tx.getRawDataToSign(0));
        byte[] a = sig.sign();
        Tx.addSignature(a, 0);
        Tx.finalize();
        txpool.addTransaction(Tx);
        block.addTransaction(Tx);
        assertEquals(1, txpool.getTransactions().size());
        txpool.removeTransaction(Tx.getHash());
        assertEquals(0, txpool.getTransactions().size());
    }



    @Test
    public void test12() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        System.out.println("when use a transaction in a block, it will be removed from the transaction pool");
        PublicKey k1 = people.get(0).getPublic();
        Block genesisBlock = new Block(null, k1);
        genesisBlock.finalize();
        BlockChain blockChain = new BlockChain(genesisBlock);
        BlockHandler blockHandler = new BlockHandler(blockChain);
        Block block = new Block(genesisBlock.getHash(), people.get(1).getPublic());
        Transaction Tx = new Transaction();
        TransactionPool txpool = new TransactionPool();
        Tx.addInput(genesisBlock.getCoinbase().getHash(), 0);
        Tx.addOutput(5, people.get(1).getPublic());
        Signature sig = Signature.getInstance("SHA256withRSA");
        sig.initSign(people.get(0).getPrivate());
        sig.update(Tx.getRawDataToSign(0));
        byte[] a = sig.sign();
        Tx.addSignature(a, 0);
        Tx.finalize();
        txpool.addTransaction(Tx);
        block.addTransaction(Tx);
        Transaction Tx1 = new Transaction();
        Tx1.addInput(Tx.getHash(), 0);
        Tx1.addOutput(5, people.get(2).getPublic());
        Signature sig1 = Signature.getInstance("SHA256withRSA");
        sig1.initSign(people.get(1).getPrivate());
        sig1.update(Tx1.getRawDataToSign(0));
        byte[] b = sig1.sign();
        Tx1.addSignature(b, 0);
        Tx1.finalize();
        block.addTransaction(Tx1);
        block.finalize();
        blockHandler.processBlock(block);
        assertEquals(1, txpool.getTransactions().size());

    }
}