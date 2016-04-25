package com.github.zkxs.evcar;

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
}
