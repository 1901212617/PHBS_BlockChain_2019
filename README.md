# **Homework1** 

陆雨田 Lu Yutian   1901212617\

##  Summary

1. First, I read the java files provided by the professor and know the main classes and functions that can be used in creating a bitcoin.
2. And then, I finish the ***class TxHandler***, which creates a ***UTXOPool***, judge whether a transaction is valid, and then update the ***UTXOPool*** and return an array that consist with the valid transactions.
3. Finally, I wrote a ***junit*** test, which contains 7 subtests to verify our logic.

 

## TxHandler

1. We first create a ***UTXOPool*** and make a copy for it to prevent the change from others.

   

2. ***public boolean isValidTx(Transaction tx){}*** 

   We should decide whether a transaction is valid or not. Based on the information provided:

 ![流程图](C:\Users\10360\Desktop\流程图.png)

​       a transaction meets these five criteria can it be verified valid. 

3. ***public Transaction[] handleTxs(Transaction[] possibleTxs) {}*** 

   This function is used to help us handle with a set of transactions, to find the valid transactions and then update the UTXOpool. 

   

##  Test

1.First, I imported 3external jars:![import](C:\Users\10360\Desktop\import.jpg)

(**notice:**First I just import the first and the third one offered by TA, but I find that hamcrest can be used, so I add the library to it.)

2.And then, I learnt how to use junit, it has ***@before @test @after,*** and each one has its own function.

3.邓珂雅 has taught me how to use the junit to create a test. So my logic might be similar to hers. First, we will create three key pair for each (private key, public key)., and we give each one a random hash and a random value from 0 to 10.

 

### Test1: 

Create a transaction from A to C. A gives its all value to B, and uses its private key to sign it. And then we use ***assertEquals(true,handlertest.isValidTx(txAC))*** to convince it. Obviously, this transaction is valid. So ***handlertest.isValidTx(txAC)*** will return true.

### Test2:

It will be the transaction from A to B and then the B to C. A uses the its private key to sign the first transaction and B uses its private key to sign the second transaction. So, these two transactions will be valid.

### Test 3: 

This transaction has multiple inputs A and B, multiple outputs A and C, A and B signs jointly. And this transaction is valid as well.

### Test 4:

This test is invalid, because we make a negative input by making the value1 be the opposite. In this way, the function isValidTx() will return false.

### Test 5:

This test gives a situation double spending. First transaction is A to B, and the second transaction is A to C. So A is double spending, and one of the transaction will be invalid.

### Test 6:

 The signature is invalid, because we make a transaction from A to C, A’s private key is k1, but we sign the transaction by key2, so the signature is invalid.

### Test 7:

The input is invalid, we give the input a wrong hash, so it can not find the previous transaction and verify it. And finally, all tests are passed.



## Result

![result](C:\Users\10360\Desktop\result.png)

All passed the result.