package com.n26.engine;

import java.util.TimerTask;

/**
 * Class:		StatsEngineTimer
 * @version:	1.0
 * @author:		Ravikiran Nelvagal
 * Description:	StatsEngineTimer is a scheduler,
 * 				which constantly keeps checking
 * 				for transactions that are timed out
 * 				and keeps phasing them out.
 * 
 */
public class StatsEngineTimer extends TimerTask{

	StatsEngineManager sem;
	public StatsEngineTimer(StatsEngineManager sem){
		this.sem=sem;
	}
	@Override
	public void run() {
		sem.keepChecking();
	}

}
