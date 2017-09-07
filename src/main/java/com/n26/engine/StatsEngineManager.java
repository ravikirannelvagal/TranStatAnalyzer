package com.n26.engine;

import java.util.Queue;
import java.util.Timer;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.springframework.stereotype.Service;

import com.n26.model.Stat;
import com.n26.model.Transaction;

/**
 * Class:		StatsEngineManager
 * @version:	1.0
 * @author:		Ravikiran Nelvagal
 * Description:	StatsEngineManager manages all the 
 * 				transactions that are registered and 
 * 				keeps calculating the latest statistics 
 * 				based on the transactions that exists in
 * 				its queue which are not more than 60 seconds old.
 * Logic:		Keep pushing all transactions that are added
 * 				to a ConcurrentLinedQueue. Start a timer that runs
 * 				the StatsEngineTimer every 1 second. This timer task
 * 				keeps flushing transactions older than 60 seconds.
 * 				If a transaction is added, then the statistics is
 * 				recalculated automatically, so that when a GET call
 * 				is received to read the stats, it happens in O(1) time.
 * 				Similarly, when a transaction is removed from the queue,
 * 				the statistics is recalculated automatically.
 * Choices:		ConcurrentLinkedQueue - This is a Queue which is thread
 * 				safe. This ensures even multiple calls to add transactions
 * 				are handled graciously and none of the transactions are
 * 				missed out. Queue itself suggests FIFO, so that as and when
 * 				transactions are timed out, they will be phased out of
 * 				the queue.
 * 				lockForModification - This is an object passed around 
 * 				which holds the lock to modifying the Stats object.
 * 				Only one block of code which hold this lock has the access
 * 				to modify Stat. Other blocks of code need to wait and
 * 				modify once the lock is released, and by themselves gaining
 * 				the lock.
 */
@Service
public class StatsEngineManager {

	/* queue to hold the transactions that are added */
	private final Queue<Transaction> transQ = new ConcurrentLinkedQueue<>();
	/* the timestamp of the first transaction 
	 * indicates when the 60 second period begins */
	private long qStartAt=0L;
	/* the statistics object */
	private Stat opStat;
	/* lock object to ensure 
	 * synchronized modification of stat*/
	private Object lockForModification;
	/* the time frame within which 
	 * transactions need to be 
	 * present to be honoured 
	 * 60000ms=60s=1m */
	private final long TRANSACTION_WINDOW=60000;
	/* the periodic delay after which 
	 * the timer will be run again 
	 * 1000ms=1s */
	private final long ONE_SECOND=1000;
	
	public StatsEngineManager(){
		//create a new timer
		Timer t = new Timer();
		//schedulethe timer to run our task every 1 sec.
		t.schedule(new StatsEngineTimer(this), 0, ONE_SECOND);
		// initialize the lock object
		this.lockForModification=new Object();
	}
	
	/* Setters and Getters */
	public Stat getOpStat() {
		return opStat;
	}

	public void setOpStat(Stat opStat) {
		this.opStat = opStat;
	}

	public Queue<Transaction> getTransQ(){
		return transQ;
	}
	
	public long getQStartAt() {
		return qStartAt;
	}

	public void setQStartAt(long qStartAt) {
		this.qStartAt = qStartAt;
	}
	
	/*
	 * method:			addTransaction
	 * input params:	Transaction
	 * return:			void
	 * description:		Add the passed transaction
	 * endpoint:		This is the endpoint for /transactions
	 * */
	public void addTransaction(Transaction tran){
		if(transQ.size() == 0){
			/* if this is the first transaction that is being added 
			*  set its timestamp as the start for the queue */
			setQStartAt(tran.getTimestamp());
			addFirstToQueue(tran);
		}else{
			// if the queue has transactions already, then add this transaction and process the stats immediately
			addToQueue(tran);
		}
		transQ.add(tran);
	}
	
	/*
	 * method:			getStat
	 * input params:	None
	 * return:			String
	 *  
	public String getStat(){
		return opStat.toString();
	}*/

	
	
	public void keepChecking(){
		//current queue size at System.currentTimeMillis()
		if(transQ.size() == 0){
			//since size is 0, resetting all values
			Stat s = new Stat(0,0,0,0,0);
			//get lock to set the stat
			synchronized (lockForModification) {
				this.setOpStat(s);
			}
			return;
		}
		long currTime=System.currentTimeMillis();
		//this current queue start time is this.getQStartAt()
		//Current time is currTime
		if((currTime - this.getQStartAt()) >=TRANSACTION_WINDOW){
			//since queue has started more than 60 seconds ago, need to flush out values older than 60 secs
			long tempCurrTime=currTime-TRANSACTION_WINDOW;
			//flush out transactions before tempCurrTime
			for(Transaction t:transQ){
				if(t.getTimestamp()<=tempCurrTime){
					//time stamp of this transaction is t.getTimestamp() older than 60 secs hence flushing out
					removeTFromQueue(t);
				}
			}
		}
		//if there are more transactions in the queue, get the next start timestamp
		if(transQ.size()>0)
			qStartAt=transQ.peek().getTimestamp();
	}
	
	private void removeTFromQueue(Transaction t){
		//trying to remove tran
		transQ.remove(t);
		//transaction removed
		//get lock to set the stat
		synchronized (lockForModification) {
			Stat s = this.getOpStat();
			long newCount=s.getCount()-1;
			if(newCount==0){
				this.setOpStat(new Stat(0,0,0,0,0));
				return;
			}
			s.setCount(newCount);
			double newSum=s.getSum()-t.getAmount();
			s.setSum(newSum);
			s.setAvg(newSum/newCount);
			if(s.getMin() == t.getAmount()){
				double min=Double.MAX_VALUE;
				for(Transaction t1:transQ){
					if(t1.getAmount()<min){
						min=t1.getAmount();
					}
				}
				s.setMin(min);
			}
			if(s.getMax() == t.getAmount()){
				double max=Double.MIN_VALUE;
				for(Transaction t1:transQ){
					if(t1.getAmount()>max){
						max=t1.getAmount();
					}
				}
				s.setMax(max);
			}
			
			this.setOpStat(s);
		}
	}
	
	private void addToQueue(Transaction tran){
		Stat s= this.getOpStat();
		//get lock to set the stat
		synchronized (lockForModification) {
			long newCount = s.getCount()+1;
			s.setCount(newCount);
			double newSum = s.getSum()+tran.getAmount();
			s.setSum(newSum);
			s.setAvg(newSum/newCount);
			if(tran.getAmount()<s.getMin())
				s.setMin(tran.getAmount());
			
			if(tran.getAmount()>s.getMax())
				s.setMax(tran.getAmount());
			
			this.setOpStat(s);
		}
	}
	
	private void addFirstToQueue(Transaction tran){
		Stat s =  new Stat(0,0,0,0,0);
		//get lock to set the stat
		synchronized (lockForModification) {
			s.setCount(1);
			s.setMax(tran.getAmount());
			s.setMin(tran.getAmount());
			s.setAvg(tran.getAmount());
			s.setSum(tran.getAmount());
			this.setOpStat(s);
		}
	}
}
