# Proposal of Improvement in Comment System of Taobao

## Member: 邓珂雅1901212571        陆雨田1901212617

### Part 1: Motivation — the problem to be solved

- **Buyers modify or delete comments driven by the interests of merchants, which makes it impossible for other consumers to get real comments**

  We will take Taobao as an example. Some merchants will produce goods for poor quality or will offer unsatisfactory service. Then, the customers may give this product or the merchants negative comment. But the merchants may allow buyers to delete reviews by giving them cash back. In this way, buyers may find one with excellent reviews, but the real object is in poor condition. This is the reason why we want to build a decentralized platform where the merchants  cannot change the reviews of the buyers. So, our buyers will not be misled.

- **In order to get good comments, the seller registered multiple accounts to create the illusion of purchase, malicious click farming orders to mislead consumers**

  In order to  compete for customers, network owners use a variety of methods to improve the store praise rate and credibility. For example, machine brush bill, manual brush bill, even gave birth to "brush guest" this kind of occupation, a lot of praise is paid to brush people up. So comment, to some extent, can not fully reflect the real quality of goods. The final result is that consumers are still unable to judge the quality of goods from a large number of true and false comments.

### Part 2: Solution — how to solve

- **Stop the behavior of malicious click farming**

  1. Users participating in the comment need to apply for a block chain based "e-wallet" first, and then recharge it to the "e-wallet" in advance to purchase the "token" (virtual token) needed for the comment. This token is equivalent to the "start-up fund" for consumers to participate in the comment, which increases the cost of malicious bill swiping by stores, because simulating a large number of accounts is not only electronic information, but also Need to recharge these virtual accounts.
  2. Each comment needs to consume a certain amount of token in the wallet, half of which is used to pay for "miner" to maintain the autonomy and stable operation of the block chain system; the other half is paid to the users evaluated before, and the meaning of this part will be explained later.
  3. Currency day is a measure used to describe the time that a token stays in an account for
     transactions. For example, a token stays in an account for 10 days, and the transaction is 10 currency days. After the transaction, the token's currency day is cleared, and the calculation starts again in a new account. Therefore, when the shopkeeper frequently swipes the bill, the actual transaction will generate very few currency days. If the currency days are used as the comment weight, it is easy to see the transaction abnormality, and then identify the behavior of swiping the bill.

  It can be seen that in this comment system, the cost of shop brush praise increases linearly
  with the number of praise, because each comment will consume token, and simulate a large number of users need a large number of "start-up funds", which will greatly eliminate the brush behavior.

- **Encourage real comments**

  We regard the token consumed in user comment as an equity investment, and the subsequent user's token will be divided into one part to the previous user, for example, the second user will share half of the token to the first user; the third user will share half of the token equally to the first and second users; and so on. Finally, the user's comment, like equity, will bring continuous revenue to itself. Benefit, so as to encourage consumers to evaluate the goods.

  It can be found that the earlier the users are evaluated, the higher the income. This is
  because the risk of the early users is the largest. The risk here comes from the uncertainty of the comment trend, which may lead to the subsequent lack of praise, or a large number of risk of poor comment (comparable to equity investment, the risk at the beginning of the project is the largest).

  Of course, the final revenue of users depends on the future sales volume and the number of favorable comments. If the user publishes a false praise, and later users find that the user may be a single user after experiencing the product, they will not continue to make additional investment.

-  **Preliminary idea**

  As the problem we have mentioned above, we decided to develop a website based on Ethereum. The design of the website may use HTML+CSS+Javascript. In our design, each buyer is an account and can make comments of the goods he or she has purchased. The main interface is like this:

  ![](https://github.com/YutianNancy/PHBS_BlockChain_2019/blob/master/Project/Figure%201.jpg)

  When a buyer makes an comment, it is a transaction. It will be added to the block after it will stimulate the running of the smart contract. And the smart contract judges whether this comment is valid or not and will package all these information. An comment might be like this:

  ![](F:\课程资料\module1\block chain and digital currency\project\图2.png)

  The reason why we use Goods ID is that we decide to develop a search function. When a buyer wants to buy a specific goods, he or she might search the Goods ID, like the key in database.

  We will also encourage the users to make comment, the stimulation mechanism might be like this:

  - The users will first apply for an account and charge tokens for it before making
    comments.
  - Every comment will use some tokens, which will be partially devoted to miners and the
    previous commenters.

  The establishment of account might be supported by the third party and be used to interact with our website, maybe the EOS Studio or the MetaMask, we will think it later.

  Add the additional functions might be the scoring system and the searching system, the latter one will be much more important, for the reason that the buyer would like to search the specific good when shopping.

### **Part 3: Rationale— Why such solution?**

-  **Platform's efforts for the current comments mechanism**

  For example, Taobao.com on the one hand uses artificial intelligence + big data to analyze
  and verify the massive transaction information of stores and report the suspicious stores; on the other hand, it has set up a team of more than 2000 people to check the stores suspected of high praise. However, it still has little effect. The reasons are as follows:

  1. The cost of the registered account is too low. It only needs a lot of computer operations, which is easy to simulate the illusion of a large number of users.
  2. The cost of comment is too low, only a little time of computer operation is needed, and it is easy to make a lot of false praise through automation.
  3. The similarity of true and false comment is very high, which can not be effectively distinguished whether through machine intelligent identification or artificial audit. Moreover, with the increasing labor cost, this audit mode does not have sustainable development.

- **What do consumers, stores and platforms get in our system**

  In this comment system, consumers, stores and platforms, as system participants, will actively contribute to the system from their own interests, and then promote the healthy
  and sustainable development of the comment ecosystem.

  1. **For consumers who have already purchased and experienced commodities**, they will participate in commodity comment more actively, because this investment may bring benefits to users in the future. At the same time, whether it's to praise the store or to short the bad reviews, it will tend to the real will of users, because only the real reviews are most likely to win the best interests for themselves.
  2. **For the consumers who are ready to buy goods**, they can safely choose the goods with more favorable comments and avoid the goods with more bad comments. Of course, the latter users can also not participate in investment activities, just be a general consumer, after all, with the increase of the number of favorable comments, the income of continuing investment will continue to decline.
  3. **For the store owners**, they don't need to worry about other unscrupulous competitors seizing the user's market by brushing the bill, but focus on improving the quality of the goods, because only high-quality goods can attract more users' praise and win market sales.
  4. **For the e-commerce platform**, it saves a lot of human R & D costs and checks the shop's bill brushing behavior.

  In essence, praise is actually a combination of user's personal information and time cost. It has been used by platforms and stores for a long time to create value for itself, while  consumers have not received substantial benefits. Through block chain technology, users can realize integrity and wisdom, and guide other consumers correctly.
