package com.github.zkxs.evcar;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;

public class Assets
{	
	private static Font font7seg;
	
	static
	{
		try
		{
			font7seg = Font.createFont(Font.TRUETYPE_FONT, Util.getFileInputStream("assets/fonts/DSEG/DSEG7Classic-Regular.ttf"));
		}
		catch (FontFormatException | IOException e)
		{
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public static Font getFont7seg()
	{
		return font7seg;
	}
	
	
	/** unconstructable */
	private Assets(){}
}
