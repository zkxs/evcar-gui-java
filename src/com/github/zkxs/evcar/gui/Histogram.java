package com.github.zkxs.evcar.gui;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.image.BufferStrategy;
import java.util.LinkedList;
import java.util.Queue;

import com.github.zkxs.evcar.data.DataPoint;
import com.github.zkxs.evcar.data.DataReceiver;

public class Histogram extends Canvas implements DataReceiver
{
	private static final long serialVersionUID = 0L;
	private LinkedList<DataPoint> dataPoints = new LinkedList<>();
	private Queue<DataPoint> newDataQueue = new LinkedList<>();
	
	// I assume this cannot change
	private final static int MAX_DATA_POINTS = Toolkit.getDefaultToolkit().getScreenSize().width;
	
	public Histogram()
	{
		
	}
	
	@Override
	public void paint(Graphics screen)
	{
		// set up double buffering
		BufferStrategy bufferStrategy = getBufferStrategy();
		if (bufferStrategy == null)
		{
			createBufferStrategy(2);
			bufferStrategy = getBufferStrategy();
		}
		
		// get the graphic object for the back buffer
		Graphics2D g = (Graphics2D) bufferStrategy.getDrawGraphics();
		
		// actually perform drawing
		int width = getWidth();
		int height = getHeight();
		
		g.setColor(Color.RED);
		g.fillRect(10, 10, width - 20, height - 20);
		
		// show the back buffer
		g.dispose();
		bufferStrategy.show();
	}

	@Override
	public void acceptDataPoint(DataPoint dp)
	{
		// TODO Auto-generated method stub
		
	}
}
