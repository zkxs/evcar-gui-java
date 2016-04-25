package com.github.zkxs.evcar;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;

import com.github.zkxs.evcar.data.*;
import com.github.zkxs.evcar.gui.*;

public class Driver
{	
	public static final String VERSION_NUMBER = "0.0.0";
	public static final String APPLICATION_NAME = "Electric Vehicle Demonstrator";
	private static final String WINDOW_TITLE = APPLICATION_NAME + " v" + VERSION_NUMBER;
	
	private JFrame frame;
	private JPanel contentPane;
	private GraphicsDevice monitor;
	private boolean isFullscreen = false;
	private Rectangle windowBounds = new Rectangle(100, 100, 450, 300);
	private Point windowLocation;
	
	public static void main(String[] args)
	{
		new Driver();
	}
	
	public Driver()
	{
		// fixes a flickering problem caused by AWT doing things it really doesn't need to
		System.setProperty("sun.awt.noerasebackground", "true");
		
		DataProvider dataProvider = new RandomDataProvider();
		
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				try
				{
					contentPane = new JPanel();
					contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
					contentPane.setLayout(new BorderLayout(0, 0));
					
					Histogram currentHistogram = new Histogram(GaugeParameters.CURRENT_GAGUE_PARAMETERS);
					contentPane.add(currentHistogram, BorderLayout.CENTER);
					
					contentPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F11, 0), "toggleFullscreen");
					contentPane.getActionMap().put("toggleFullscreen", new AbstractAction() {
						private static final long serialVersionUID = 1L;

						@Override
						public void actionPerformed(ActionEvent e)
						{
							toggleFullscreen();
						}
					});
					
					dataProvider.addListener(currentHistogram);
					
					// set up timer to send data points to the various GUI components
					Timer updateTimer = new Timer("Update Timer");
					updateTimer.scheduleAtFixedRate(new TimerTask() {
						@Override
						public void run()
						{
							dataProvider.dispatchData();
						}
					}, 0, 1000);
					
					frame = getNewFrame(isFullscreen);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}
	
	private JFrame createWindowedFrame()
	{
		JFrame frame = new JFrame(WINDOW_TITLE);
		frame.setUndecorated(false);
		frame.setBounds(windowBounds);
		if (windowLocation != null)
		{
			frame.setLocation(windowLocation);
		}
		//frame.setMinimumSize(new Dimension(100, 100));
		frame.setResizable(true);
		return frame;
	}
	
	
	private JFrame createFullscreenFrame()
	{
		assert(monitor == null);
		monitor = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		JFrame frame = new JFrame(WINDOW_TITLE);
		frame.setUndecorated(true);
		frame.setSize(new Dimension(monitor.getDisplayMode().getWidth(), monitor.getDisplayMode().getHeight()));
		frame.setLocation(0, 0);
		monitor.setFullScreenWindow(frame);
		frame.setResizable(false);
		return frame;
	}
	
	private void toggleFullscreen()
	{
		destroyFrame();
		
		isFullscreen = !isFullscreen;
		frame = getNewFrame(isFullscreen);
	}
	
	private JFrame getNewFrame(boolean isFullscreen)
	{
		JFrame frame;
		
		if (isFullscreen)
		{
			frame = createFullscreenFrame();
		}
		else
		{
			frame = createWindowedFrame();
		}
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(contentPane);
		frame.setVisible(true);
		
		return frame;
	}
	
	private void destroyFrame()
	{
		if (monitor != null)
		{
			monitor.setFullScreenWindow(null);
			monitor = null;
		}
		
		if (!isFullscreen)
		{
			// back up parameters of window
			frame.getBounds(windowBounds);
			frame.getLocation(windowLocation);
		}
		
		frame.removeAll();
		frame.setVisible(false);
		frame.dispose();
		frame = null;
	}
}
