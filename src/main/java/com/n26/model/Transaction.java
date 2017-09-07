package com.n26.model;

public class Transaction implements Comparable<Transaction>{

	private double amount;
	private long timestamp;
	
	public Transaction(){
		
	}
	
	public Transaction(double amount, long timestamp) {
		super();
		this.amount = amount;
		this.timestamp = timestamp;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	
	
	@Override
	public boolean equals(Object o){
		if(o == null)
			return false;
		if(!(o instanceof Transaction))
			return false;
		Transaction t=(Transaction)o;
		if(t.getAmount()==this.getAmount() && t.getTimestamp()==this.getTimestamp())
			return true;
		return false;
	}
	
	@Override
	public int hashCode(){
		int prime=17;
		int ret=0;
		ret=(int)(ret+(prime*amount));
		ret=(int)(ret+(prime*timestamp));
		return ret;
	}

	@Override
	public int compareTo(Transaction o) {
		// TODO Auto-generated method stub
		return (int)(this.getTimestamp()-o.getTimestamp());
	}
	
}
