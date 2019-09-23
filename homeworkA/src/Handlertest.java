import org.junit.Test;
import org.junit.Before;
import org.junit.After;

import java.security.*;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;


public class Handlertest {

    TxHandler handlertest;
    private KeyPair k1;
    private KeyPair k2;
    private KeyPair k3;
    double value1 = Math.random()*(10-0+1);
    double value2 = Math.random()*(10-0+1);
    double value3 = Math.random()*(10-0+1);
    byte[] hash1 = "generate hash1".getBytes();
    byte[] hash2 = "generate hash2".getBytes();
    byte[] hash3 = "generate hash3".getBytes();
    UTXOPool utxoPool;




    @Before
    public void setUpBeforeClass() throws Exception{
        KeyPairGenerator generator =  KeyPairGenerator.getInstance("RSA");
        k1 = generator.generateKeyPair();
        k2 = generator.generateKeyPair();
        k3 = generator.generateKeyPair();
        Transaction tx1 = new Transaction();
        Transaction tx2 = new Transaction();
        Transaction tx3 = new Transaction();
        Transaction.Output out1 = tx1.new Output(value1,k1.getPublic());
        Transaction.Output out2 = tx2.new Output(value2,k2.getPublic());
        Transaction.Output out3 = tx3.new Output(value3,k3.getPublic());
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
        txAC.addOutput(value1,k3.getPublic());
        Signature sig = Signature.getInstance("SHA256withRSA");
        sig.initSign(k1.getPrivate());
        sig.update(txAC.getRawDataToSign(0));
        byte[] a = sig.sign();
        txAC.addSignature(a,0);
        txAC.finalize();
        System.out.println(txAC);
        assertEquals(true,handlertest.isValidTx(txAC));
    }

    @Test
    public void test2() throws InvalidKeyException, NoSuchAlgorithmException, SignatureException {
        System.out.println("连续交易");
        Transaction txAB = new Transaction();
        txAB.addInput(hash1,0);
        txAB.addOutput(value1,k2.getPublic());
        Signature sig = Signature.getInstance("SHA256withRSA");
        sig.initSign(k1.getPrivate());
        sig.update(txAB.getRawDataToSign(0));
        byte[] b = sig.sign();
        txAB.addSignature(b,0);
        txAB.finalize();
        Transaction txBC = new Transaction();
        txBC.addInput(txAB.getHash(),0);
        txBC.addOutput(value2,k3.getPublic());
        sig.initSign(k2.getPrivate());
        sig.update(txBC.getRawDataToSign(0));
        byte[] c = sig.sign();
        txBC.addSignature(c,0);
        txBC.finalize();
        assertEquals(true,handlertest.isValidTx(txAB));
        assertEquals(false,handlertest.isValidTx(txBC));

    }


    @Test
    public void test3() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        System.out.println("多个输入多个输出");
        Transaction ABtxAC = new Transaction();
        ABtxAC.addInput(hash1,0);
        ABtxAC.addInput(hash2,0);
        ABtxAC.addOutput(value1,k1.getPublic());
        ABtxAC.addOutput(value3,k3.getPublic());
        Signature sig = Signature.getInstance("SHA256withRSA");
        sig.initSign(k1.getPrivate());
        sig.update(ABtxAC.getRawDataToSign(0));
        byte[] d = sig.sign();
        ABtxAC.addSignature(d,0);
        sig.initSign(k2.getPrivate());
        sig.update(ABtxAC.getRawDataToSign(1));
        byte[] e = sig.sign();
        ABtxAC.addSignature(e,1);
        ABtxAC.finalize();
        assertEquals(true,handlertest.isValidTx(ABtxAC));
    }


    @Test
    public void test4() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        System.out.println("negative output");
        Transaction txAC = new Transaction();
        txAC.addInput(hash1,0);
        txAC.addOutput(-value1,k3.getPublic());
        Signature sig = Signature.getInstance("SHA256withRSA");
        sig.initSign(k1.getPrivate());
        sig.update(txAC.getRawDataToSign(0));
        byte[] f = sig.sign();
        txAC.addSignature(f,0);
        txAC.finalize();
        assertEquals(true,handlertest.isValidTx(txAC));
    }


    @Test
    public void test5() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        System.out.println("double spending");
        Transaction txAB = new Transaction();
        txAB.addInput(hash1,0);
        txAB.addOutput(value1,k2.getPublic());
        Signature sig = Signature.getInstance("SHA256withRSA");
        sig.initSign(k1.getPrivate());
        sig.update(txAB.getRawDataToSign(0));
        byte[] g = sig.sign();
        txAB.addSignature(g,0);
        txAB.finalize();
        Transaction txAC = new Transaction();
        txAC.addInput(hash1,0);
        txAC.addOutput(value1,k3.getPublic());
        sig.initSign(k1.getPrivate());
        sig.update(txAC.getRawDataToSign(0));
        byte[] h = sig.sign();
        txAC.addSignature(h,0);
        txAC.finalize();
        assertEquals(true,handlertest.isValidTx(txAB));
        assertEquals(true,handlertest.isValidTx(txAC));
    }

    @Test
    public void test6() throws InvalidKeyException, NoSuchAlgorithmException, SignatureException {
        System.out.println("signature invalid");
        Transaction txAC = new Transaction();
        txAC.addInput(hash1,0);
        txAC.addOutput(value1, k3.getPublic());
        Signature sig = Signature.getInstance("SHA256withRSA");
        sig.initSign(k2.getPrivate());
        sig.update(txAC.getRawDataToSign(0));
        byte[] m = sig.sign();
        txAC.addSignature(m,0);
        txAC.finalize();
        Transaction[] trans = {txAC};
        assertEquals(false,handlertest.isValidTx(txAC));
    }


    @Test
    public void test7() throws InvalidKeyException, NoSuchAlgorithmException, SignatureException {
        System.out.println("input invalid");
        Transaction txAC = new Transaction();
        txAC.addInput(hash2,0);
        txAC.addOutput(value1,k3.getPublic());
        Signature sig = Signature.getInstance("SHA256withRSA");
        sig.initSign(k1.getPrivate());
        sig.update(txAC.getRawDataToSign(0));
        byte[] n = sig.sign();
        txAC.addSignature(n,0);
        txAC.finalize();
        Transaction[] trans = {txAC};
        assertEquals(false,handlertest.isValidTx(txAC));
    }


}

