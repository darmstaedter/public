package org.cs3.timetracker;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import junit.framework.TestCase;

public class LoggerTest extends TestCase {
	
	private Logger Logger;
	
	private String GlobalTestMinutes = "02";
	private String GlobalTestSeconds = "12";
	private String GlobalTestComment = "Kommentar Kommentar Kommentar.";
	
	private String GlobalTestFilename = "test.txt";

	private static final String LOGFILE = System.getProperty("java.io.tmpdir") + File.separator + "test.txt";

	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		
		Logger = new Logger(new TimeTicker(),LOGFILE);
		Logger.log( GlobalTestComment);
	}

	public void testPausedTime() {
		TimeTicker ticker = new TimeTicker();
		Logger logger = new Logger(ticker,LOGFILE);
		ticker.start(false);
		try 
		{
			Thread.sleep(1500);
		} catch(Exception e) { e.printStackTrace(); }		
		
		String logString  = logger.log( GlobalTestComment);
		assertEquals("Recorded [03:00 02:59 00:01] "+GlobalTestComment+"\n", logString);
	}
	
	public void testReadLog() throws Exception
	{
		TimeTicker ticker = new TimeTicker();
		Logger logger = new Logger(ticker,LOGFILE);
		
		ticker.start(false);
		try
		{
			Thread.sleep(1800);
		} catch (Exception e) { e.printStackTrace(); }
	
		System.out.println("Trying to display contents of the logfile here:\n");
		System.out.println(logger.readLog());
		
		assertEquals(1,1);
	}

	
	/*
	 * This test proves, if all content written to the File is formerly
	 * readable. 
	 *
	 */
//	public void testWrittenToFile() throws Exception
//	{
//		byte[] InputLogByteArray = null; 
//	
//		String LogString = "Recorded [03:00 "+GlobalTestMinutes+":"+
//			GlobalTestSeconds+" 00:48] "+GlobalTestComment;
//
//		File FileObject = new File(GlobalTestFilename);
//		try {
//			FileInputStream InputStreamObject = new FileInputStream(FileObject);
//			
//			InputLogByteArray = new byte[InputStreamObject.available()];
//			InputStreamObject.read(InputLogByteArray);
//		}
//		catch(IOException e) {
//			System.out.println("IO Exception occured, while reading from Logfile.");
//		}		
//		
//		String InputLogString = new String(InputLogByteArray);
//		
//		assertEquals(LogString, InputLogString);
//	}
	
//	/*
//	 * This test makes sure, that all silly arguments will throw an 
//	 * IllegalArgumentException.
//	 * 
//	 * e.g. Minutes < 0 or empty Comments.
//	 * In either way no log entry will be generated.
//	 */
//	public void testIllegalArguments() throws Exception
//	{
//		byte[] InputLogByteArray = null; 
//		
//		String LogString = "Recorded ["+GlobalTestMinutes+":"+
//			GlobalTestSeconds+"] "+GlobalTestComment;
//		
//		try 
//		{
//			Logger.log(GlobalTestMinutes, GlobalTestSeconds, "");
//			fail();
//		}
//		catch(IllegalArgumentException e) {	}
//
//		try 
//		{
//			Logger.log("-1", GlobalTestSeconds, GlobalTestComment);
//			fail();
//		}
//		catch(IllegalArgumentException e) { }
//
//		try 
//		{
//			Logger.log(GlobalTestMinutes, "-1", GlobalTestComment);
//			fail();
//		}
//		catch(IllegalArgumentException e) {	}
//}

}
