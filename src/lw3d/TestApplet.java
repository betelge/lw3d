package lw3d;

import java.applet.Applet;

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
		test.main(new String[0]);
	}

}
