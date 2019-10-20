// Block Chain should maintain only limited block nodes to satisfy the functions
// You should not have all the blocks added to the block chain in memory 
// as it would cause a memory overflow.


import java.util.ArrayList;
import java.util.HashMap;


public class BlockChain {


    public static final int CUT_OFF_AGE = 10;


    private class BlockNode {
        public Block b;
        public BlockNode parent;
        public ArrayList<BlockNode> children;
        public int height;

        // utxo pool for making a new block on top of this block
        private UTXOPool uPool;
        public BlockNode(Block b, BlockNode parent, UTXOPool uPool) {
            this.b = b;
            this.parent = parent;
            children = new ArrayList<BlockNode>();
            this.uPool = uPool;
            if (parent != null) {
                height = parent.height + 1;    //也就是离创世块的距离
                parent.children.add(this);   //相当于是创世块
            } else {
                height = 1;
            }
        }

        public UTXOPool getUTXOPoolCopy() {
            return new UTXOPool(uPool);
        }
    }

    private ArrayList<BlockNode> heads;
    private HashMap<ByteArrayWrapper, BlockNode> H;
    private int height;
    private BlockNode maxHeightBlock;
    private TransactionPool txPool;

        /**
     * create an empty block chain with just a genesis block. Assume {@code genesisBlock} is a valid
     * block
     */


    public BlockChain(Block genesisBlock) {
        // IMPLEMENT THIS
        UTXOPool uPool = new UTXOPool();
        Transaction coinbase = genesisBlock.getCoinbase();
        UTXO utxoCoinbase = new UTXO(coinbase.getHash(), 0);
        uPool.addUTXO(utxoCoinbase, coinbase.getOutput(0));
        BlockNode genesis = new BlockNode(genesisBlock, null, uPool);
        heads = new ArrayList<BlockNode>();
        heads.add(genesis);
        H = new HashMap<ByteArrayWrapper, BlockNode>();
        H.put(new ByteArrayWrapper(genesisBlock.getHash()), genesis);
        height = 1;
        maxHeightBlock = genesis;
        txPool = new TransactionPool();
    }

    /** Get the maximum height block */
    public Block getMaxHeightBlock() {
        // IMPLEMENT THIS
        return maxHeightBlock.b;
    }

    /** Get the UTXOPool for mining a new block on top of max height block */
    //这个是mine一个新的block，utxo的变化值

    public UTXOPool getMaxHeightUTXOPool() {

        return maxHeightBlock.getUTXOPoolCopy();

    }

    /** Get the transaction pool to mine a new block */
    //这个是到目前为止的
    public TransactionPool getTransactionPool() {
        // IMPLEMENT THIS
        return txPool;
    }

    public int getMaxHeight() {
        return height;
    }



    /* Return the block node if its height >= (maxHeight - CUT_OFF_AGE)
     * so we can search for genesis block (height = 1) as long as height of
     * block chain <= (CUT_OFF_AGE + 1).
     * So if max height is (CUT_OFF_AGE + 2), search for genesis block
     * will return null
     */

    private BlockNode getBlock(byte[] blockHash) {
        ByteArrayWrapper hash = new ByteArrayWrapper(blockHash);
        return H.get(hash);
    }



    /* Check if the transactions of the block form a valid set of transactions
     * corresponding to the utxo pool (similar validity as in Assignment 1)
     * If its a valid set, return the updated utxo pool
     * and add the block's coinbase transaction to it.
     * If not a valid set, return null
     */

    private UTXOPool processBlockTxs(UTXOPool uPool, Block b) {
        Transaction[] txs = b.getTransactions().toArray(new Transaction[0]);
        TxHandler handler = new TxHandler(uPool);
        Transaction[] rTxs = handler.handleTxs(txs);
        if (rTxs.length != txs.length)
            return null;
        uPool = handler.getUTXOPool();
        Transaction coinbase = b.getCoinbase();          //每一个block都有coinbase transaction，相当于出块奖励
        UTXO utxoCoinbase = new UTXO(coinbase.getHash(), 0);
        uPool.addUTXO(utxoCoinbase, coinbase.getOutput(0));
        return uPool;

    }


    /* update the transaction pool, removing transactions used by block and return it
     */

    private void updateTransactionPool(Block b) {
        Transaction[] aTxs = b.getTransactions().toArray(new Transaction[0]);
        for (Transaction tx : aTxs) {
            txPool.removeTransaction(tx.getHash());
        }

    }


    /**
     * Add {@code block} to the block chain if it is valid. For validity, all transactions should be
     * valid and block should be at {@code height > (maxHeight - CUT_OFF_AGE)}.
     * 
     * <p>
     * For example, you can try creating a new block over the genesis block (block height 2) if the
     * block chain height is {@code <=
     * CUT_OFF_AGE + 1}. As soon as {@code height > CUT_OFF_AGE + 1}, you cannot create a new block
     * at height 2.
     * 
     * @return true if block is successfully added
     */
    public boolean addBlock(Block b) {
        // IMPLEMENT THIS
        byte[] prevBlock = b.getPrevBlockHash();
        if (prevBlock == null) {
            return false;
        }
        BlockNode parent = getBlock(prevBlock);
        if (parent == null)
            return false;
        UTXOPool uPool = parent.getUTXOPoolCopy();
        uPool = processBlockTxs(uPool, b);
        if (uPool == null)
            return false;
        updateTransactionPool(b);
        //note, add uPool to the b
        BlockNode current = new BlockNode(b, parent, uPool);
        H.put(new ByteArrayWrapper(b.getHash()), current);
        if (current.height > height) {
            maxHeightBlock = current;
            height = current.height;
        }

        if (height - heads.get(0).height > CUT_OFF_AGE) {
            ArrayList<BlockNode> newHeads = new ArrayList<BlockNode>();
            for (BlockNode bHeads : heads) {
                for (BlockNode bChild : bHeads.children) {
                    newHeads.add(bChild);
                }
                H.remove(new ByteArrayWrapper(bHeads.b.getHash()));
            }
            heads = newHeads;
        }
        return true;
    }




    /** Add a transaction to the transaction pool */
    public void addTransaction(Transaction tx) {
        // IMPLEMENT THIS
        txPool.addTransaction(tx);
    }



}