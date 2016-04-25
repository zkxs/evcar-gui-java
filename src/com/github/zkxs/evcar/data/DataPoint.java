package com.github.zkxs.evcar.data;

public class DataPoint
{	
	private long time;
	private double current;
	private double voltage;
	private double rpm;
	
	public DataPoint(long time, double current, double voltage, double rpm)
	{
		this.time = time;
		this.current = current;
		this.voltage = voltage;
		this.rpm = rpm;
	}
	
	public long getTime()
	{
		return time;
	}
	
	void setTime(long time)
	{
		this.time = time;
	}
	
	public double getCurrent()
	{
		return current;
	}
	
	void setCurrent(double current)
	{
		this.current = current;
	}
	
	public double getVoltage()
	{
		return voltage;
	}
	
	void setVoltage(double voltage)
	{
		this.voltage = voltage;
	}
	
	public double getRpm()
	{
		return rpm;
	}
	
	void setRpm(double rpm)
	{
		this.rpm = rpm;
	}
}
