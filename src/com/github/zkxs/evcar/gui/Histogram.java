package com.github.zkxs.evcar.gui;

import java.awt.BasicStroke;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.Toolkit;
import java.awt.image.BufferStrategy;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.github.zkxs.evcar.data.DataPoint;
import com.github.zkxs.evcar.data.DataReceiver;

public class Histogram extends Canvas implements DataReceiver
{
	private static final long serialVersionUID = 0L;
	
	private final static int DATA_POINT_WIDTH = 25;
	
	// I assume this value cannot change
	private final static int MAX_DATA_POINTS = Toolkit.getDefaultToolkit().getScreenSize().width;
	
	private LinkedList<DataPoint> dataPoints = new LinkedList<>();
	private Queue<DataPoint> newDataQueue = new ConcurrentLinkedQueue<>();
	private GaugeParameters gagueParameters;
	
	
	
	public Histogram(GaugeParameters gagueParameters)
	{
		this.gagueParameters = gagueParameters;
	}
	
	@Override
	public void paint(Graphics screen)
	{
		// prepare data for drawing
		double maxValue;
		{
			DataPoint dp;
			while ((dp = newDataQueue.poll()) != null)
			{
				/* We need to iterate over the list starting with the most recent element. It is much easier
				 * to iterate over lists in forward order, so lets order our data queue appropriately. New items
				 * will be added to the left, and old items will be removed from the right.
				 */
				dataPoints.addFirst(dp);
				
				/* Note that 'if' is used here instead of 'while'. This is because this check is performed every
				 * time something is inserted, guaranteeing we will never be more than one element over capacity.
				 */
				if (dataPoints.size() > MAX_DATA_POINTS)
				{
					dataPoints.removeLast();
				}
			}
			
			// find maximum value of data
			Optional<DataPoint> maybeMax = dataPoints.stream().max((a, b) -> Double.compare(gagueParameters.getValue(a), gagueParameters.getValue(b)));
			if (maybeMax.isPresent())
			{
				maxValue = gagueParameters.getValue(maybeMax.get());
			}
			else
			{
				maxValue = gagueParameters.getMaximim();
			}
		}
		
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
		int maxX = width - 1;
		int maxY = height - 1;
		
		/* More recent points are shown at the right. Each tick the graph is effectively shifted
		 * one unit to the left.
		 */
		
		/* here we set up a shifting lens of 2 datapoints. dp1 is on the left, dp2 is on the right.
		 */
		DataPoint dpNewer;
		int xOldest, yOldest, xOlder, yOlder, xNewer, yNewer;
		
		xOlder = width - 1;
		xNewer = xOlder - DATA_POINT_WIDTH;
		
		xOldest = -1;
		yOlder = -1;
		
		ListIterator<DataPoint> iter = dataPoints.listIterator();
		Polygon poly = new Polygon();
		boolean oldestValid = false;
		
		// clear screen
		g.clearRect(0, 0, width, height);
		
		if (iter.hasNext())
		{
			dpNewer = iter.next();
			yNewer = getYCoordinate(dpNewer, maxY, maxValue);
			while (iter.hasNext() && xOlder >= 0)
			{
				dpNewer = iter.next();
				yOldest = yOlder;
				yOlder = yNewer;
				yNewer = getYCoordinate(dpNewer, maxY, maxValue);
				
				poly.addPoint(xNewer, height);
				poly.addPoint(xOlder + 1, height);
				poly.addPoint(xOlder + 1, yOlder);
				poly.addPoint(xNewer, yNewer);
				
				// first we draw the area below the trapezoid
				g.setColor(Color.DARK_GRAY);
				g.fillPolygon(poly);
				poly.reset();
				
				// then we draw the top edge of the trapezoid
				if (oldestValid)
				{
					g.setColor(Color.BLACK);
					g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // enable anti-aliasing
					Stroke oldStroke = g.getStroke();
					g.setStroke(new BasicStroke(2));	
					g.drawLine(xOldest, yOldest, xOlder, yOlder);
					g.setStroke(oldStroke);
					g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF); // disable anti-aliasing
				}
				
				xOldest = xOlder;
				xOlder = xNewer;
				xNewer -= DATA_POINT_WIDTH;
				oldestValid = true;
			}
			
			if (oldestValid)
			{
				g.setColor(Color.BLACK);
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // enable anti-aliasing
				Stroke oldStroke = g.getStroke();
				g.setStroke(new BasicStroke(2));	
				g.drawLine(xNewer + DATA_POINT_WIDTH, yNewer, xOlder + DATA_POINT_WIDTH, yOlder);
				g.setStroke(oldStroke);
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF); // disable anti-aliasing
			}
		}
		
		
		
		
		
		
//		g.setColor(Color.RED);
//		g.fillRect(10, 10, width - 20, height - 20);
//		
//		g.setColor(Color.BLACK);
//		g.drawRect(0, 0, width - 1, height - 1);
		
		// show the back buffer
		g.dispose();
		bufferStrategy.show();
	}

	@Override
	public void acceptDataPoint(DataPoint dp)
	{
		/* In order to deal with synchronization issues, we don't actually put the data in the list at this point.
		 * We just add it to a queue that will be efficiently transfered into the real list during paint().
		 */
		newDataQueue.add(dp);
		repaint();
	}
	
	private int getYCoordinate(DataPoint dp, int maxY, double maxValue)
	{
		double value = gagueParameters.getValue(dp);
		double minValue = gagueParameters.getMinimum();
		double range = maxValue - minValue;
		double normalizedValue = (value - minValue) / range;
		int yCoord = maxY - ((int) (normalizedValue * maxY));
		return yCoord;
	}
}
