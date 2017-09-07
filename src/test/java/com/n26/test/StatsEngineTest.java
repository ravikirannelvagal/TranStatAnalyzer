package com.n26.test;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

import java.sql.Timestamp;
import java.util.Random;

import com.n26.engine.StatsEngineManager;
import com.n26.model.Stat;
import com.n26.model.Transaction;

public class StatsEngineTest {

	private StatsEngineManager sem = new StatsEngineManager();
	
	@Test
	public void addSimpleTransaction(){
		Transaction tran = new Transaction(26, System.currentTimeMillis());
		sem.addTransaction(tran);
		assertEquals("Transaction added!",1,sem.getTransQ().size());
	}
	
	@Test
	public void add10Transactions() throws InterruptedException{
		double d;
		double[] addedDs= new double[10];
		double min=Double.MAX_VALUE;
		double max=Double.MIN_VALUE;
		double sum=0;
		double avg=0;
		Random r = new Random();
		for(int i=0;i<10;i++){
			d=r.nextDouble();
			long tempAdd=i*3000;
			sem.addTransaction(new Transaction(d, System.currentTimeMillis()+tempAdd));
			if(d<min)
				min=d;
			if(d>max)
				max=d;
			addedDs[i]=d;
			sum=sum+d;
		}
		avg=sum/10;
		assertEquals("Adding 10 transactions failed", new Stat(sum, avg, max, min, 10),sem.getOpStat());
		Thread.sleep(61000);
		for(int i=10;i>0;i--){
			assertEquals("transaction removal failed", i-1,sem.getTransQ().size());
			Thread.sleep(3000);
		}
	}
}
