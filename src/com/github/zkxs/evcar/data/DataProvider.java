package com.github.zkxs.evcar.data;

import java.util.LinkedList;
import java.util.List;

abstract public class DataProvider
{	
	private List<DataReceiver> dataReceivers = new LinkedList<>();
	
	public void start()
	{
	}
	
	public void restart()
	{
	}
	
	public void stop()
	{
	}
	
	/**
	 * Constructs a new DataPoint object
	 * @return Most recent data from provider
	 */
	abstract public DataPoint getDataPoint();
	
	public void addListener(DataReceiver dataReceiver)
	{
		assert(!dataReceivers.contains(dataReceiver)) : "Somebody tried to register the same DataReceiver twice";
		dataReceivers.add(dataReceiver);
	}
	
	public void dispatchData()
	{
		DataPoint dp = getDataPoint();
		dataReceivers.forEach(dr -> dr.acceptDataPoint(dp));
	}
}
