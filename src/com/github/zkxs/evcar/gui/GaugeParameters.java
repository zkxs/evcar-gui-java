package com.github.zkxs.evcar.gui;

import java.awt.Color;

import com.github.zkxs.evcar.data.DataPoint;

public class GaugeParameters
{	
	// declare different types of gauges
	public static final GaugeParameters
		CURRENT_GAGUE_PARAMETERS = new GaugeParameters(0, 80, dp -> dp.getCurrent(),  new Color(0xFF6666)),
		VOLTAGE_GAGUE_PARAMETERS = new GaugeParameters(0, 72, dp -> dp.getVoltage(),  new Color(0x66FF66)),
		RPM_GAGUE_PARAMETERS     = new GaugeParameters(-500, 4500, dp -> dp.getRpm(), new Color(0x6666FF));
	
	/** Method by which value is gotten */
	@FunctionalInterface
	private interface ValueGetter
	{
		public double getValue(DataPoint dp);
	}
	
	private double minimum;
	private double maximum;
	private ValueGetter valueGetter;
	private Color color;
	
	private GaugeParameters(double minimum, double maximum, ValueGetter valueGetter, Color color)
	{
		this.minimum = minimum;
		this.maximum = maximum;
		this.valueGetter = valueGetter;
		this.color = color;
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
	public double getMaximum()
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
	
	/**
	 * @return A color you could use for the histogram
	 */
	public Color getColor()
	{
		return color;
	}
}
