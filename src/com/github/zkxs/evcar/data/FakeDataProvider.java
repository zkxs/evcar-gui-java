package com.github.zkxs.evcar.data;

import java.util.Random;

public class FakeDataProvider extends DataProvider
{
	Random rand = new Random();

	@Override
	public DataPoint getDataPoint()
	{
		double current = rand.nextDouble() * 150 - 30;  // -30  to 120
		double voltage = rand.nextDouble() * 72;        //  0 to 72
		double rpm     = rand.nextDouble() * 5000- 500; // -500 to 4500
		
		return new DataPoint(System.nanoTime(), current, voltage, rpm);
	}	
	
}
