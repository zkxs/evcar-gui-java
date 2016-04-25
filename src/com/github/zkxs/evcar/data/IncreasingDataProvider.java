package com.github.zkxs.evcar.data;

public class IncreasingDataProvider extends DataProvider
{
	private double value = 0;
	
	@Override
	public DataPoint getDataPoint()
	{
		value += 5;
		return new DataPoint(System.nanoTime(), value, value, value);
	}	
	
}
