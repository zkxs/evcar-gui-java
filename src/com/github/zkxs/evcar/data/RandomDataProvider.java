package com.github.zkxs.evcar.data;

import java.util.Random;

public class RandomDataProvider extends DataProvider
{
	Random rand = new Random();

	@Override
	public DataPoint getDataPoint()
	{
		long time = System.nanoTime();
		double current = Math.sin(time / 700_000_000d) * 75 + 45;  // -30  to 120
		double voltage = rand.nextDouble() * 72;        //  0 to 72
		double rpm     = rand.nextDouble() * 5000- 500; // -500 to 4500
		
		return new DataPoint(time, current, voltage, rpm);
	}	
	
}
