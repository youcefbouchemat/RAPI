package inducesmile.com.opencvexample.tangible;

import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;

import java.io.IOException;

//----------------------------------------------------------------------------------------
//	Copyright © 2006 - 2019 Tangible Software Solutions, Inc.
//	This class can be used by anyone provided that the copyright notice remains intact.
//
//	This class provides the ability to initialize and delete array elements.
//----------------------------------------------------------------------------------------
public final class Arrays
{
	public static Point[] initializeWithDefaultPoint2fInstances(int length)
	{
		Point[] array = new Point[length];
		for (int i = 0; i < length; i++)
		{
			array[i] = new Point();
		}
		return array;
	}

	public static <T extends java.io.Closeable> void deleteArray(T[] array) throws IOException {
		for (T element : array)
		{
			if (element != null)
				element.close();
		}
	}
}