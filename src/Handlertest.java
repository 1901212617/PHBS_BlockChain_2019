import org.junit.Test;
import org.junit.Before;
import org.junit.After;

import java.security.*;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;


public class Handlertest {

    TxHandler handlertest;
    private KeyPair AliceKey;
    private KeyPair BobKey;
    private KeyPair CindyKey;
    double value1 = Math.random()*(10-0+1);
    double value2 = Math.random()*(10-0+1);
    double value3 = Math.random()*(10-0+1);
    byte[] hash1 = "generate hash1".getBytes();
    byte[] hash2 = "generate hash2".getBytes();
    byte[] hash3 = "generate hash3".getBytes();
    UTXOPool utxoPool;




    @Before
    public void generatekey() throws Exception{
        KeyPairGenerator generator =  KeyPairGenerator.getInstance("RSA");
        AliceKey = generator.generateKeyPair();
        BobKey = generator.generateKeyPair();
        CindyKey = generator.generateKeyPair();


    }



    @Before
    public void updateUTXOPool() throws Exception{
        Transaction tx1 = new Transaction();
        Transaction tx2 = new Transaction();
        Transaction tx3 = new Transaction();
        Transaction.Output out1 = tx1.new Output(value1,AliceKey.getPublic());
        Transaction.Output out2 = tx2.new Output(value2,BobKey.getPublic());
        Transaction.Output out3 = tx3.new Output(value3,CindyKey.getPublic());
        utxoPool = new UTXOPool();
        UTXO u1 = new UTXO(hash1,0);
        utxoPool.addUTXO(u1, out1);
        UTXO u2 = new UTXO(hash2,0);
        utxoPool.addUTXO(u2, out2);
        UTXO u3 = new UTXO(hash3,0);
        utxoPool.addUTXO(u3, out3);
        handlertest = new TxHandler(utxoPool);
    }


    @After
    public void tearDownAfterClass() throws Exception {

        System.out.println("释放一些资源");

    }

    @Test
    public void test1() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {

        System.out.println("单个输入单个输出");
        Transaction txAC = new Transaction();
        txAC.addInput(hash1,0);
        txAC.addOutput(value1,CindyKey.getPublic());
        txAC.makesign(AliceKey,txAC,0);
        assertEquals(true,handlertest.isValidTx(txAC));

    }


    @Test
    public void test2() throws InvalidKeyException, NoSuchAlgorithmException, SignatureException {
        System.out.println("连续交易");
        Transaction txAB = new Transaction();
        txAB.addInput(hash1,0);
        txAB.addOutput(value1,BobKey.getPublic());
        txAB.makesign(AliceKey,txAB,0);
        System.out.println(utxoPool);
        UTXO u4 = new UTXO(txAB.getHash(),0);
        utxoPool.addUTXO(u4, txAB.getOutput(0));
        Transaction.Input in = txAB.getInput(0);
        UTXO u5 = new UTXO(in.prevTxHash,in.outputIndex);
        utxoPool.removeUTXO(u5);
        Transaction txBC = new Transaction();
        txBC.addInput(txAB.getHash(),0);
        txBC.addOutput(value2,CindyKey.getPublic());
        txBC.makesign(BobKey,txBC,0);
        assertEquals(true,handlertest.isValidTx(txAB));
        assertEquals(false,handlertest.isValidTx(txBC));

    }


    @Test
    public void test3() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        System.out.println("多个输入多个输出");
        Transaction ABtxAC = new Transaction();
        ABtxAC.addInput(hash1,0);
        ABtxAC.addInput(hash2,0);
        ABtxAC.addOutput(value1,AliceKey.getPublic());
        ABtxAC.addOutput(value3,CindyKey.getPublic());
        ABtxAC.makesign(AliceKey,ABtxAC,0);
        ABtxAC.makesign(BobKey,ABtxAC,1);
        assertEquals(true,handlertest.isValidTx(ABtxAC));
    }


    @Test
    public void test4() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        System.out.println("negative output");
        Transaction txAC = new Transaction();
        txAC.addInput(hash1,0);
        txAC.addOutput(-value1,CindyKey.getPublic());
        txAC.makesign(AliceKey,txAC,0);
        assertEquals(false,handlertest.isValidTx(txAC));
    }


    @Test
    public void test5() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        System.out.println("double spending");
        Transaction txAB = new Transaction();
        txAB.addInput(hash1,0);
        txAB.addOutput(value1,BobKey.getPublic());
        txAB.makesign(AliceKey,txAB,0);
        UTXO u4 = new UTXO(txAB.getHash(),0);
        utxoPool.addUTXO(u4, txAB.getOutput(0));
        Transaction.Input in = txAB.getInput(0);
        UTXO u5 = new UTXO(in.prevTxHash,in.outputIndex);
        utxoPool.removeUTXO(u5);
        System.out.println(in.prevTxHash);
      //  System.out.println(in.outputIndex);
        Transaction txAC = new Transaction();
        txAC.addInput(hash1,0);
        txAC.addOutput(value1,CindyKey.getPublic());
        txAC.makesign(AliceKey,txAC,0);
        assertEquals(true,handlertest.isValidTx(txAB));
        assertEquals(true,handlertest.isValidTx(txAC));
    }

    @Test
    public void test6() throws InvalidKeyException, NoSuchAlgorithmException, SignatureException {
        System.out.println("signature invalid");
        Transaction txAC = new Transaction();
        txAC.addInput(hash1,0);
        txAC.addOutput(value1, CindyKey.getPublic());
        txAC.makesign(BobKey,txAC,0);
        assertEquals(false,handlertest.isValidTx(txAC));
    }


    @Test
    public void test7() throws InvalidKeyException, NoSuchAlgorithmException, SignatureException {
        System.out.println("input invalid");
        Transaction txAC = new Transaction();
        txAC.addInput(hash2,0);
        txAC.addOutput(value1,CindyKey.getPublic());
        txAC.makesign(AliceKey,txAC,0);
        assertEquals(false,handlertest.isValidTx(txAC));
    }


  }

