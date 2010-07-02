package lw3d;

import java.applet.Applet;
import java.io.InputStream;

import lw3d.utils.GeometryLoader;
import lw3d.utils.StringLoader;
import lw3d.utils.TextureLoader;

public class TestApplet extends Applet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	Test test;
	
	public void init()
	{
		System.out.println("Applet init");
		test = new Test();
	}
	
	public void start()
	{
		System.out.println("Applet start");
		
		GeometryLoader.setObject(this);
		StringLoader.setObject(this);
		TextureLoader.setObject(this);
		
		test.main(new String[0]);
	}

}
