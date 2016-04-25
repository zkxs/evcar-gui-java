package com.github.zkxs.evcar;

import java.awt.EventQueue;

import com.github.zkxs.evcar.data.DataProvider;
import com.github.zkxs.evcar.data.FakeDataProvider;
import com.github.zkxs.evcar.gui.GraphFrame;

public class Driver
{	
	public static void main(String[] args)
	{
		// fixes a flickering problem
		System.setProperty("sun.awt.noerasebackground", "true");
		
		DataProvider dataProvider = new FakeDataProvider();
		
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				try
				{
					GraphFrame frame = new GraphFrame(dataProvider);
					frame.setVisible(true);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}
}
