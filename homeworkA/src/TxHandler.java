import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


public class TxHandler {
    /**
     * Creates a public ledger whose current UTXOPool (collection of unspent transaction outputs) is
     * {@code utxoPool}. This should make a copy of utxoPool by using the UTXOPool(UTXOPool uPool)
     * constructor.
     */
    // IMPLEMENT THIS
    private UTXOPool utxoPool;
    public TxHandler(UTXOPool utxoPool) {
       this.utxoPool = new UTXOPool(utxoPool);

    }

    /**
     * @return true if:
     * (1) all outputs claimed by {@code tx} are in the current UTXO pool, 
     * (2) the signatures on each input of {@code tx} are valid, 
     * (3) no UTXO is claimed multiple times by {@code tx},
     * (4) all of {@code tx}s output values are non-negative, and
     * (5) the sum of {@code tx}s input values is greater than or equal to the sum of its output
     *     values; and false otherwise.
     */
    public boolean isValidTx(Transaction tx) {
        // IMPLEMENT THIS
        Set<UTXO> txused = new HashSet<>();
        int n = tx.numInputs();
        int m = tx.numOutputs();
        double insum = 0;
        double outsum = 0;
        for (int i = 0; i < n; i++) {
            Transaction.Input in = tx.getInput(i);
            UTXO u = new UTXO(in.prevTxHash, in.outputIndex);
            System.out.println(utxoPool);
            if (!utxoPool.contains(u)) {
             //   System.out.println(u);
                return false;
            }
            Transaction.Output out = utxoPool.getTxOutput(u);
            insum = insum + out.value;
            if (!Crypto.verifySignature(out.address, tx.getRawDataToSign(i), in.signature)) {
                return false;
            }
            if (txused.contains(u)) {
                return false;
            } else {
                txused.add(u);
            }
        }
        for (int j = 0; m < j; j++) {
            Transaction.Output output = tx.getOutput(j);
            outsum = outsum + output.value;
            if (output.value < 0) {
                return false;
            }
        }
        if (insum < outsum) {
            return false;
        }
        return true;
    }



    /**
     * Handles each epoch by receiving an unordered array of proposed transactions, checking each
     * transaction for correctness, returning a mutually valid array of accepted transactions, and
     * updating the current UTXO pool as appropriate.
     * @return
     */
    public Transaction[] handleTxs(Transaction[] possibleTxs) {
        // IMPLEMENT THIS
        ArrayList<Transaction> trans = new ArrayList<Transaction>();
            for(Transaction tx: possibleTxs) {
                //decide whether tx is valid
                boolean flag = false;
                if(isValidTx(tx)){
                    trans.add(tx);
                    flag = true;
                //deal with UTXOpool
                for(int i = 0 ; i < tx.numOutputs() ; ++i){
                    UTXO u = new UTXO(tx.getHash(),i);
                    utxoPool.addUTXO(u, tx.getOutput(i));
                }
                for(int j = 0 ; j < tx.numInputs(); ++j){
                    Transaction.Input in = tx.getInput(j);
                    UTXO u = new UTXO(in.prevTxHash,in.outputIndex);
                    utxoPool.removeUTXO(u);
                }
            }
                if(flag == false) break;
            }

        Transaction[] transan = new Transaction[trans.size()];
        for(int i = 0;i < trans.size();i++){
            transan[i] = trans.get(i);
         }
        return transan;
    }

    public Object verify(UTXO utxo) {
        return utxoPool.contains(utxo);
    }
}

