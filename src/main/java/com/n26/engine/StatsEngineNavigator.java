package com.n26.engine;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.n26.exception.OldTransactionException;
import com.n26.model.Stat;
import com.n26.model.Transaction;


/**
 * Class:		StatsEngineNavigator
 * @version:	1.0
 * @author:		Ravikiran Nelvagal
 * Description:	StatsEngineNavigator manages and navigates
 * 				all the requests based on the URL.
 * 
 */
@RestController
public class StatsEngineNavigator {
	@Autowired
	private StatsEngineManager sem;
	private final long TRANSACTION_WINDOW=60000;

	@RequestMapping(method=RequestMethod.GET,path="/statistics")
	public Stat getStat(){
		return sem.getOpStat();
	}
	
	@RequestMapping(method=RequestMethod.POST,path="/transactions")
	@ResponseStatus(HttpStatus.CREATED)
	public void addExisitingTransaction(@RequestBody Transaction tran) {
		long currTime=System.currentTimeMillis();
		if(currTime > tran.getTimestamp() && (currTime-tran.getTimestamp() >TRANSACTION_WINDOW)){
			
			throw new OldTransactionException();
		}
		sem.addTransaction(tran);
    }
	
	/*
	 * The below requestMappings are for Web based calls, 
	 * which can be called from a browser
	 * */
	@RequestMapping(value={"/","/init"})
	public ModelAndView init(){
		ModelAndView m = new ModelAndView();
		m.setViewName("home");
		return m;
	}
	
	@RequestMapping(method=RequestMethod.POST,path="/addWebTransaction")
	@ResponseStatus(HttpStatus.CREATED)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public ModelAndView addTransaction(@FormParam("amount")String amount) {
		ModelAndView m = new ModelAndView();
		m.setViewName("home");
		double dobAmt;
		try{
			dobAmt= Double.parseDouble(amount);
		}catch(NumberFormatException e){
			m.setViewName("error");
			m.addObject("reason", "Incorrect amount!");
			return m;
		}
		long currTime=System.currentTimeMillis();
		Transaction trans = new Transaction(dobAmt,currTime);
		sem.addTransaction(trans);
		
		return m;
    }
	
	@RequestMapping(method=RequestMethod.POST,path="/addExistWebTransaction")
	@ResponseStatus(HttpStatus.CREATED)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public ModelAndView addExisitingTransaction(@FormParam("amount")String amount, @FormParam("timeStamp") String timeStamp) {
		ModelAndView m = new ModelAndView();
		m.setViewName("home");
		double dobAmt;
		try{
			System.out.println(amount);
			dobAmt = Double.parseDouble(amount);
		}catch(NumberFormatException e){
			m.setViewName("error");
			m.addObject("reason", "Incorrect amount!");
			return m;
		}
		long lTimeStamp;
		try{
			System.out.println(timeStamp);
			lTimeStamp=Long.parseLong(timeStamp);
		}catch(NumberFormatException e){
			m.setViewName("error");
			m.addObject("reason", "Incorrect timestamp!");
			return m;
		}
		long currTime=System.currentTimeMillis();
		if(currTime > lTimeStamp && (currTime-lTimeStamp >TRANSACTION_WINDOW)){
			m.setViewName("error");
			m.addObject("reason", "Transaction is not added since it is older than 60 seconds from current time");
			return m;
		}
		Transaction trans = new Transaction(dobAmt,lTimeStamp);
		sem.addTransaction(trans);
		
		return m;
    }
	
	@RequestMapping(path="/transInit")
    public ModelAndView initTransaction() {
		ModelAndView m = new ModelAndView();
		m.setViewName("transAdd");
		return m;
    }
	
	@RequestMapping(path="/transExist")
    public ModelAndView initExistingTransaction() {
		ModelAndView m = new ModelAndView();
		m.setViewName("transAddExisting");
		return m;
    }
	
}
