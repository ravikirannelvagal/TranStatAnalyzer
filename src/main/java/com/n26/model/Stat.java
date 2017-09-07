package com.n26.model;

public class Stat {

	private double sum;
	private double avg;
	private double max;
	private double min;
	private long count;
	
	
	
	public Stat(double sum, double avg, double max, double min, long count) {
		super();
		this.sum = sum;
		this.avg = avg;
		this.max = max;
		this.min = min;
		this.count = count;
	}
	
	public double getSum() {
		return sum;
	}
	public void setSum(double sum) {
		this.sum = sum;
	}
	public double getAvg() {
		return avg;
	}
	public void setAvg(double avg) {
		this.avg = avg;
	}
	public double getMax() {
		return max;
	}
	public void setMax(double max) {
		this.max = max;
	}
	public double getMin() {
		return min;
	}
	public void setMin(double min) {
		this.min = min;
	}
	public long getCount() {
		return count;
	}
	public void setCount(long count) {
		this.count = count;
	}
	
	public String toString(){
		return "Sum: "+sum+", "+
				"Avg: "+avg+", "+
				"Min: "+min+", "+
				"Max: "+max+", "+
				"Count: "+count;
	}
	
	@Override
	public boolean equals(Object o){
		boolean isEqual=false;
		if(o==null)
			return false;
		if(! (o instanceof Stat))
			return false;
		Stat temp=(Stat)o;
		if(this.sum == temp.sum && this.max == temp.max && this.min==temp.min && this.avg == temp.avg && this.count == temp.count)
			isEqual=true;
		return isEqual;
	}
	
	@Override
	public int hashCode(){
		int prime=19;
		double ret=0;
		ret=ret*(this.sum*prime);
		ret=ret*(this.avg*prime);
		ret=ret*(this.min*prime);
		ret=ret*(this.max*prime);
		ret=ret*(this.count*prime);
		
		return (int)ret;
	}
	
}
