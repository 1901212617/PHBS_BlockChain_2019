# Homework 2    Blockchain

Name:陆雨田

Student ID:1901212617

## The solution to BlockChain.java

In this project, we are required to generate a genesis block, then the miner mines new blocks,  we will decide whether the this block is valid, if it is valid, we will add it to the blockchain.

First, we create a private BlockNode Class

```
private class BlockNode {    
public Block b;    
public BlockNode parent;    
public ArrayList<BlockNode> children;    
public int height;    // utxo pool for making a new block on top of this block    
private UTXOPool uPool;    
public BlockNode(Block b, BlockNode parent, UTXOPool uPool) {        
        this.b = b;        
        this.parent = parent;        
        children = new ArrayList<BlockNode>();        
        this.uPool = uPool;        
        if (parent != null) {            
            height = parent.height + 1;    //也就是离创世块的距离            
            parent.children.add(this);   //相当于是创世块        }` 
        else {            
        	height = 1;        
        	}`
}    
public UTXOPool getUTXOPoolCopy() {        return new UTXOPool(uPool);    }`
}`
```

In a BlockNode, it contains a block, the parent of this block, which is its previous block, and the children of this block, which is the next block of this block, and the UTXOPool for this block. We will this class later.

And then,we create the genesisblock, and its now the *maxHeightBlock*.

Next, we will decide whether the newly generated block is valid.

(1)Whether this block has the previous block and whether the preblockhash is valid.(whether the parent is valid)

(2)Whether the fork is valid. We come up with the CUT_OFF_AGE, which means that we can add block to the block chain if it is valid. F block should be at code height > (maxHeight - CUT_OFF_AGE).

(3)Whether the transactions in this block is valid.

If all these are satisfied, we will add this block to the block chain and then

(1)Update the UTXOPool of the block chain.

(2)Update the Transaction Pool of the Block Chain.

(3)Update the MaxHeightBlock and its related UTXOPool and Transaction Pool.

## Test Cases

In the cases, we mainly test the block chain fork, which involves CUT_OFF_AGE. And the block with valid and invalid transactions, the update of the Transaction Pool and UTXOPool.

**Test1:**

We will test the existing of a genesis block.

We also test whether the coinbase transaction is in the UTXOpool.

**Test2:**

We will test the process a new genesis block--no double genesis block, only one genesis block is ok.

**Test3:**

We will process a block with an invalid prevBlockHash.

**Test4:**

We will process a block with no transactions.

**Test5:**

We will process a block with a single valid transaction.

**Test6:**

We will process a block with many valid transactions. We also test whether the MaxHeightBlock is true.

**Test7:**

We will process a block with invalid transactions -double spending.

**Test8:**

We will process several block with invalid transactions. We give the coinbase of genesis block to 20 new blocks, these 25 belongs to only one UTXO, so it can only be used once. So only the first generated block except for the genesis block can be added to the block chain.

We also test  whether the these transaction is in the UTXOpool.(Transaction belongs the first generated block except for the genesis block can be added to the UTXOpool, transactions belong to other blocks can't be added to the UTXOpool).

**Test9:**

We will test the valid fork.

![img](https://github.com/YutianNancy/PHBS_BlockChain_2019/blob/master/Homework2/QQ图片20191020214858.png)

First, we add block to the block chain.

Then, we add block1 to the block chain.

At that time, we will test the MaxHeightBlock, it will return to the old one-----block, not the block1.

Finally, we add the block2 after the block1, and it becomes the longest chain, and MaxHeightBlock changed to block2.

**Test10:**

We will test valid and invalid blockchain fork when considering CUT_OFF_AGE.

![img](file:///C:\Users\10360\Documents\Tencent Files\1036023145\Image\C2C\~34L]%C0$8SRA0~$AC@O$P1.png)

When considering CUT_OFF_AGE, blocka is valid and blockb is not valid.

**Test11:**

We will add Transaction and remove Transaction and then test Transaction Pool.

**Test12:**

We will test when we use a transaction in a block, it will be removed from the transaction pool.

## Result

All tests have passed.

![img](file:///C:\Users\10360\Documents\Tencent Files\1036023145\Image\C2C\{[U~~9[9P%ZQ_{R0TY4)LP7.png)

