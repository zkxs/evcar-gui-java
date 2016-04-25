package com.github.zkxs.evcar.gui;

import com.github.zkxs.evcar.data.DataPoint;

public class GaugeParameters
{	
	// declare different types of gauges
	public static final GaugeParameters
		CURRENT_GAGUE_PARAMETERS = new GaugeParameters(-30, 120, dp -> dp.getCurrent()),
		VOLTAGE_GAGUE_PARAMETERS = new GaugeParameters(0, 72, dp -> dp.getVoltage()),
		RPM_GAGUE_PARAMETERS     = new GaugeParameters(-500, 4500, dp -> dp.getRpm());
	
	/** Method by which value is gotten */
	@FunctionalInterface
	private interface ValueGetter
	{
		public double getValue(DataPoint dp);
	}
	
	private double minimum;
	private double maximum;
	private ValueGetter valueGetter;
	
	private GaugeParameters(double minimum, double maximum, ValueGetter valueGetter)
	{
		this.minimum = minimum;
		this.maximum = maximum;
		this.valueGetter = valueGetter;
	}
	
	/**
	 * @return Expected minimum value =
	 */
	public double getMinimum()
	{
		return minimum;
	}
	
	/**
	 * @return Expected maximum value
	 */
	public double getMaximim()
	{
		return maximum;
	}
	
	/**
	 * Get a value from a DataPoint
	 * @param dp The DataPoint to extract the value from
	 * @return The appropriate value for these parameters
	 */
	public double getValue(DataPoint dp)
	{
		return valueGetter.getValue(dp);
	}
}
