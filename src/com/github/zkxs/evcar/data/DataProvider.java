package com.github.zkxs.evcar.data;

public interface DataProvider
{	
	public void start();
	public void restart();
	public void stop();
	
	/**
	 * Constructs a new DataPoint object
	 * @return Most recent data from provider
	 */
	public DataPoint getDataPoint();
}
