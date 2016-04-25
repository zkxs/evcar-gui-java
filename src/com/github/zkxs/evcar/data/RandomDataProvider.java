package com.github.zkxs.evcar.data;

import java.util.Random;

public class RandomDataProvider extends DataProvider
{
	Random rand = new Random();
	private double voltage = 0;
	private double rpm = 0;
	private double drpm = 0;
	private long count = Long.MIN_VALUE;

	@Override
	public DataPoint getDataPoint()
	{
		long time = System.nanoTime();
		double current = Math.sin(time / 700_000_000d) * 75 + 45;  // -30  to 120
		voltage += rand.nextDouble() * 10 - 5;        //  0 to 72
		if (voltage > 80) voltage = 72;
		else if (voltage < -5) voltage = 0;
		
		if (count++ % 50 == 0){
			drpm = rand.nextDouble() * 40 - 20;
			if (rpm > 5400) drpm -= 9;
			if (rpm > 3000) drpm -= 3;
			if (rpm > 2000) drpm -= 1;
			if (rpm < 2000) drpm += 1;
			if (rpm < 1000) drpm += 3;
			if (rpm < 100 ) drpm += 9;
			
			System.out.println(drpm);
		}
		
		
		
		rpm  += drpm; // -500 to 4500
		if (rpm > 5500) rpm = 5500;
		else if (rpm < 0) rpm = 0;
		
		return new DataPoint(time, current, voltage, rpm);
	}	
	
}
