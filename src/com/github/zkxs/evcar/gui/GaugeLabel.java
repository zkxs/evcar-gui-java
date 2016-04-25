package com.github.zkxs.evcar.gui;

import javax.swing.JLabel;

import com.github.zkxs.evcar.data.DataPoint;
import com.github.zkxs.evcar.data.DataReceiver;

public class GaugeLabel extends JLabel implements DataReceiver
{
	private static final long serialVersionUID = 1L;
	private GaugeParameters gaugeParameters;
	private String numberFormat;
	
	public GaugeLabel(GaugeParameters gaugeParameters)
	{
		super();
		this.gaugeParameters = gaugeParameters;
		this.numberFormat = gaugeParameters.getNumberFormat();
	}

	@Override
	public void acceptDataPoint(DataPoint dp)
	{
		this.setText(String.format(numberFormat, gaugeParameters.getValue(dp)));
	}
}
