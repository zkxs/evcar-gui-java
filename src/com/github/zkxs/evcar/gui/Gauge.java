package com.github.zkxs.evcar.gui;

import java.awt.BasicStroke;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.image.BufferStrategy;

import com.github.zkxs.evcar.data.DataPoint;
import com.github.zkxs.evcar.data.DataReceiver;

public class Gauge extends Canvas implements DataReceiver
{
	private final static int EDGE_PADDING = 2;
	private double destinationValue;
	private double currentValue;
	private GaugeParameters gaugeParameters;

	public Gauge(GaugeParameters gaugeParameters)
	{
		this.gaugeParameters = gaugeParameters;
		setMinimumSize(new Dimension(200, 100));
		setPreferredSize(new Dimension(200, 100));
	}

	@Override
	public void update(Graphics g)
	{
		/* This magically fixes flickering. AWT is trying to be smart, but it is too smart for its
		 * own good. If you don't override this method AWT will clear the screen in a non-double buffered way.
		 */
		paint(g);
	}
	
	@Override
	public void paint(Graphics screen)
	{
		// get size of component
		int width = getWidth();
		int height = getHeight();
		int maxX = width - 1;
		int maxY = height - 1;
		
		int arcDiameter, arcX, arcY, arcRadius;
		
		// height is the smaller dimension
		if (width > height * 2)
		{
			arcDiameter = (height - 2 * EDGE_PADDING) * 2;
			arcRadius = arcDiameter / 2;
			arcX = (width - arcDiameter) / 2;
			arcY = EDGE_PADDING;
		}
		else // width is the smaller dimension
		{
			arcDiameter = width - 2 * EDGE_PADDING;
			arcRadius = arcDiameter / 2;
			arcX = EDGE_PADDING;
			arcY = (height - arcRadius) / 2;
		}
		int centerX = arcX + arcRadius;
		int centerY = arcY + arcRadius;
		
		// set up double buffering
		BufferStrategy bufferStrategy = getBufferStrategy();
		if (bufferStrategy == null)
		{
			createBufferStrategy(2);
			bufferStrategy = getBufferStrategy();
		}
		
		// get the graphic object for the back buffer
		Graphics2D g = (Graphics2D) bufferStrategy.getDrawGraphics();
		
		// clear the whole canvas
		g.clearRect(0, 0, width, height);
		
		// actually perform drawing
		g.setColor(Color.WHITE);
		g.fillArc(arcX, arcY, arcDiameter, arcDiameter, 0, 180);
		
		
		g.setColor(Color.BLACK);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // enable anti-aliasing
		Stroke oldStroke = g.getStroke();
		g.setStroke(new BasicStroke(2));
		g.drawArc(arcX, arcY, arcDiameter, arcDiameter, 0, 180);
		g.drawLine(arcX, arcY + arcRadius, arcX + arcDiameter, arcY + arcRadius);
		
		for (int i = 0; i <= 8; i++)
		{
			drawLineRadial(g, centerX, centerY, arcRadius - 5, arcRadius, i * Math.PI / 8);
		}
		
		g.setColor(Color.RED);
		drawLineRadial(g, centerX, centerY, 0, arcRadius, getNeedleTheta(currentValue));
		
		g.setStroke(oldStroke);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF); // disable anti-aliasing
		
		
		// show the back buffer
		g.dispose();
		bufferStrategy.show();
	}
	
	
	@Override
	public void acceptDataPoint(DataPoint dp)
	{
		destinationValue = gaugeParameters.getValue(dp);
		currentValue = gaugeParameters.getValue(dp);
		if (currentValue > gaugeParameters.getMaximum()) currentValue = gaugeParameters.getMaximum();
		if (currentValue < gaugeParameters.getMinimum()) currentValue = gaugeParameters.getMinimum();
		repaint();
	}
	
	private void drawLineRadial(Graphics2D g, int centerX, int centerY, int radius1, int radius2, double theta)
	{
		int x1 = centerX + (int) (radius1 * Math.cos(theta));
		int y1 = centerY - (int) (radius1 * Math.sin(theta));
		int x2 = centerX + (int) (radius2 * Math.cos(theta));
		int y2 = centerY - (int) (radius2 * Math.sin(theta));
		g.drawLine(x1, y1, x2, y2);
	}
	
	private double getNeedleTheta(double value)
	{
		double range = gaugeParameters.getMaximum() - gaugeParameters.getMinimum();
		double normalizedValue = (value -  gaugeParameters.getMinimum()) / range;
		double theta = Math.PI - (normalizedValue * Math.PI);
		return theta;
	}
	
}
