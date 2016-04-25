package com.github.zkxs.evcar;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.io.InputStream;

public final class Util
{	
	/**
	 * Do not allow instances of this class to be created
	 */
	private Util()
	{
	}
	
	/**
	 * Gets the InputStream of a file given its relative path.
	 * This expects the resources to be relative to the root of the src folder.
	 * @param url Path to file
	 * @return The InputStream of a file
	 */
	public static InputStream getFileInputStream(String url)
	{
		InputStream in = Util.class.getResourceAsStream("/" + url);
		return in;
	}
	
	public static Rectangle getStringBounds(Graphics2D g, String str)
	{
		return getStringBounds(g, str, 0, 0);
	}
	
	public static Rectangle getStringBounds(Graphics2D g, String str, float x, float y)
	{
		FontRenderContext frc = g.getFontRenderContext();
		GlyphVector gv = g.getFont().createGlyphVector(frc, str);
		return gv.getPixelBounds(null, x, y);
	}
}
