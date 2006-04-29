/*****************************************************************************
 * This file is part of the Prolog Development Tool (PDT)
 * 
 * Author: Lukas Degener (among others) 
 * E-mail: degenerl@cs.uni-bonn.de
 * WWW: http://roots.iai.uni-bonn.de/research/pdt 
 * Copyright (C): 2004-2006, CS Dept. III, University of Bonn
 * 
 * All rights reserved. This program is  made available under the terms 
 * of the Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * In addition, you may at your option use, modify and redistribute any
 * part of this program under the terms of the GNU Lesser General Public
 * License (LGPL), version 2.1 or, at your option, any later version of the
 * same license, as long as
 * 
 * 1) The program part in question does not depend, either directly or
 *   indirectly, on parts of the Eclipse framework and
 *   
 * 2) the program part in question does not include files that contain or
 *   are derived from third-party work and are therefor covered by special
 *   license agreements.
 *   
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *   
 * ad 1: A program part is said to "depend, either directly or indirectly,
 *   on parts of the Eclipse framework", if it cannot be compiled or cannot
 *   be run without the help or presence of some part of the Eclipse
 *   framework. All java classes in packages containing the "pdt" package
 *   fragment in their name fall into this category.
 *   
 * ad 2: "Third-party code" means any code that was originaly written as
 *   part of a project other than the PDT. Files that contain or are based on
 *   such code contain a notice telling you so, and telling you the
 *   particular conditions under which they may be used, modified and/or
 *   distributed.
 ****************************************************************************/

package org.cs3.pl.common;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * contains static methods that do not quite fit anywhere else :-)=
 */
public class Util {

	public static String generateFingerPrint() {
		long l = System.currentTimeMillis();
		double m = Math.random();
		return "fp_" + l + "_" + m;
	}

	public static  boolean isJava5(){
		return System.getProperty("java.version").startsWith("1.5");
	}
	public static File getLockFile() {
		String tmpdir = System.getProperty("java.io.tmpdir");
		return new File(tmpdir, generateFingerPrint());
	}

	/**
	 * @deprecated this does not work correctly e.g. on systems that have
	 *             separate ip4 and ip6 stacks (MacOS X,...)
	 */
	public static boolean probePort(int port) {
		try {
			ServerSocket ss = new ServerSocket(port);
			// Socket cs = ss.accept();
			//            
			// new InputStreamPump(cs.getInputStream()){
			// protected void dataAvailable(char[] buffer, int length) {
			// System.out.print(String.copyValueOf(buffer,0,length));
			// }
			// };
			ss.close();
		} catch (IOException e1) {
			return true;
		}
		return false;
	}

	public static String prettyPrint(Map r) {
		if (r != null) {
			boolean first=true;
			StringBuffer sb = new StringBuffer();
			for (Iterator it = r.keySet().iterator(); it.hasNext();) {
				if(!first){
					sb.append(", ");
				}
				String key = (String) it.next();
				String val = (String) r.get(key);
				sb.append(key + "-->" + val);
				first=false;

			}
			return sb.toString();
		}
		return "";
	}

	public static String prettyPrint(Object[] a) {
		if (a == null) {
			return "";
		}
		StringBuffer sb = new StringBuffer();
		//sb.append("{");
		for (int i = 0; i < a.length; i++) {
			if (i > 0) {
				sb.append(", ");
			}
			sb.append(a[i].toString());
		}
		//sb.append("}");
		return sb.toString();
	}

	public static File getLogFile(String name) throws IOException {
		File logFile = new File(System.getProperty("java.io.tmpdir")
				+ File.separator + name);
		if (!logFile.exists()) {
			logFile.getParentFile().mkdirs();
			logFile.createNewFile();
		}
		return logFile.getCanonicalFile();
	}

	/**
	 * 
	 * @param cmd
	 * @return an array of two strings: th 0th one contains process output, the
	 *         1th one error.
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static String[] exec(String cmd) throws IOException,
			InterruptedException {
		Process process = null;
		try {

			process = Runtime.getRuntime().exec(cmd);
		} catch (Throwable t) {
			Debug.report(t);
			return new String[] { "ERROR", "" };
		}

		class _InputStreamPump extends InputStreamPump {
			StringBuffer sb = new StringBuffer();

			public _InputStreamPump(InputStream s) {
				super(s);
			}

			protected void dataAvailable(char[] buffer, int length) {
				String string = new String(buffer, 0, length);
				sb.append(string);

			}

		}
		_InputStreamPump errPump = new _InputStreamPump(process
				.getErrorStream());
		_InputStreamPump outPump = new _InputStreamPump(process
				.getInputStream());
		errPump.start();
		outPump.start();
		process.waitFor();
		outPump.join();
		errPump.join();
		return new String[] { outPump.sb.toString(), errPump.sb.toString() };
	}

	public static void copy(InputStream in, OutputStream out)
			throws IOException {
		BufferedInputStream bIn = null;
		BufferedOutputStream bOut = null;
		try {
			bIn = new BufferedInputStream(in);
			bOut = new BufferedOutputStream(out);
			byte[] buf = new byte[255];
			int read = -1;
			while ((read = bIn.read(buf)) > -1) {
				out.write(buf, 0, read);
			}
		} finally {
			bOut.flush();
		}
	}

	public static String normalizeOnWindoze(String s) {
		boolean windowsPlattform = isWindoze();
		if (windowsPlattform) {
			s = s.replace('\\', '/').toLowerCase();
		}
		return s;
	}

	/**
	 * @return
	 */
	public static boolean isWindoze() {
		boolean windowsPlattform = System.getProperty("os.name").indexOf(
				"Windows") > -1;
		return windowsPlattform;
	}

	/**
	 * @return
	 */
	public static boolean isMacOS() {
		boolean mac = System.getProperty("os.name").indexOf(
				"Mac") > -1;
		return mac;
	}
	public static String prologFileName(File f) {
		try {
			return normalizeOnWindoze(f.getCanonicalPath());
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	public static String toString(InputStream in) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] buf = new byte[1024];
		int read = in.read(buf);
		while (read > 0) {
			out.write(buf, 0, read);
			read = in.read(buf);
		}
		return out.toString();
	}

	// specify buffer size for extraction
	static final int BUFFER = 2048;

	/*
	 * the body of this method was taken from here.
	 * 
	 * http://www.devshed.com/c/a/Java/Zip-Meets-Java/2/
	 * 
	 * Many thanks to the author, Kulvir Singh Bhogal.
	 * 
	 * I could not find any license or copyright notice If there are any legal
	 * problems please let me: know degenerl_AT_cs_DOT_uni-bonn_DOT_de
	 * 
	 * --lu
	 */
	public static void unzip(File sourceZipFile, File unzipDestinationDirectory) {
		try {

			// Open Zip file for reading
			ZipFile zipFile = new ZipFile(sourceZipFile, ZipFile.OPEN_READ);

			// Create an enumeration of the entries in the zip file
			Enumeration zipFileEntries = zipFile.entries();

			// Process each entry
			while (zipFileEntries.hasMoreElements()) {
				// grab a zip file entry
				ZipEntry entry = (ZipEntry) zipFileEntries.nextElement();

				String currentEntry = entry.getName();
				System.out.println("Extracting: " + entry);

				File destFile = new File(unzipDestinationDirectory,
						currentEntry);

				// grab file's parent directory structure
				File destinationParent = destFile.getParentFile();

				// create the parent directory structure if needed
				destinationParent.mkdirs();

				// extract file if not a directory
				if (!entry.isDirectory()) {
					BufferedInputStream is = new BufferedInputStream(zipFile
							.getInputStream(entry));
					int currentByte;
					// establish buffer for writing file
					byte data[] = new byte[BUFFER];

					// write the current file to disk
					FileOutputStream fos = new FileOutputStream(destFile);
					BufferedOutputStream dest = new BufferedOutputStream(fos,
							BUFFER);

					// read and write until last byte is encountered
					while ((currentByte = is.read(data, 0, BUFFER)) != -1) {
						dest.write(data, 0, currentByte);
					}
					dest.flush();
					dest.close();
					is.close();
				}
			}
			zipFile.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	/**
	 * @param file
	 */
	public static void unzip(File file) {
		unzip(file, file.getParentFile());
	}

	/**
	 * @param file
	 */
	public static void deleteRecursive(File file) {
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (int i = 0; i < files.length; i++) {
				deleteRecursive(files[i]);
			}
		}
		file.delete();
	}

	public static String escape(String s) {
		StringBuffer result = new StringBuffer(s.length() + 10);
		for (int i = 0; i < s.length(); ++i) {
			char c = s.charAt(i);
			switch (c) {
			case '<':
				result.append("&lt;"); //$NON-NLS-1$
				break;
			case '>':
				result.append("&gt;"); //$NON-NLS-1$
				break;
			case '"':
				result.append("&quot;"); //$NON-NLS-1$
				break;
			case '\'':
				result.append("&apos;"); //$NON-NLS-1$
				break;
			case '&':
				result.append("&amp;"); //$NON-NLS-1$
				break;
			case '{':
				result.append("&cbo;"); //$NON-NLS-1$
				break;
			case '}':
				result.append("&cbc;"); //$NON-NLS-1$
				break;
			default:
				result.append(c);
				break;
			}
		}
		return result.toString();
	}

	/**
	 * @param line
	 * @param start
	 * @param end
	 * @return
	 */
	public static String unescape(String line, int start, int end) {
		StringBuffer sb = new StringBuffer();
		boolean escape = false;
		StringBuffer escBuf = new StringBuffer();
		for (int i = start; i < end; i++) {
			char c = line.charAt(i);
			switch (c) {
			case '&':
				escape = true;
				break;
			case ';':
				if (escape) {
					escape = false;
					String escSeq = escBuf.toString();
					escBuf.setLength(0);
					if ("lt".equals(escSeq)) {
						sb.append('<');
					} else if ("gt".equals(escSeq)) {
						sb.append('>');
					} else if ("cbo".equals(escSeq)) {
						sb.append('{');
					} else if ("cbc".equals(escSeq)) {
						sb.append('}');
					} else if ("amp".equals(escSeq)) {
						sb.append('&');
					} else if ("apos".equals(escSeq)) {
						sb.append('\'');
					} else if ("quot".equals(escSeq)) {
						sb.append('\"');
					}
				} else {
					sb.append(c);
				}
				break;
			default:
				if (escape) {
					escBuf.append(c);
				} else {
					sb.append(c);
				}
				break;
			}
		}
		return sb.toString();
	}

	private static HashMap startTimes = new HashMap();

	public static void startTime(String key) {
		String prefix = Thread.currentThread().toString();
		startTimes.put(prefix + key, new Long(System.currentTimeMillis()));
	}

	public static long time(String key) {
		String prefix = Thread.currentThread().toString();
		Long startTime = (Long) startTimes.get(prefix + key);
		return startTime == null ? -1 : System.currentTimeMillis()
				- startTime.longValue();
	}

	static PrintStream logStream = null;

	public static void printTime(String key) {
		if (logStream == null) {
			try {
				File f = getLogFile("times.txt");
				logStream = new PrintStream(new BufferedOutputStream(
						new FileOutputStream(f)));
				Runtime.getRuntime().addShutdownHook(new Thread() {
					public void run() {
						logStream.close();
					}
				});

			} catch (IOException e) {
				throw new RuntimeException(e.getMessage());
			}
		}
		logStream.println(key + " took " + time(key) + " millis.");
	}

	/**
	 * parse an association list.
	 * 
	 * @param l
	 *            A list containing strings of the form <code>key->value</code>.
	 * @return A map that represents the association. If the list contains
	 *         multiple mappings for a single key, the map will contain a List
	 *         of this values. Otherwise, the value type will bs String.
	 */
	public static Map parseAssociation(List l) {
		HashMap map = new HashMap();
		for (Iterator it = l.iterator(); it.hasNext();) {
			String elm = (String) it.next();
            String[] s = splitKeyValue(elm);
			String key = s[0];
			String val = s[1];
			Object o = map.get(key);
			if (o == null) {
				map.put(key, val);
			} else if (o instanceof List) {
				List ll = (List) o;
				ll.add(val);
			} else {
				List ll = new Vector();
				ll.add(o);
				ll.add(val);
				map.put(key, ll);
			}
		}
		return map;
	}

	private static String[] splitKeyValue(String elm) {
		final int LEN = elm.length();
		int l = 0;
		StringBuffer key = new StringBuffer();
		String value;
		while(l < LEN-1){
			if(elm.charAt(l) == '-' &&
			   elm.charAt(l+1) == '>') {
				value = elm.substring(l+2,LEN);
				return new String[] { key.toString(), value };
			} else {
				key.append(elm.charAt(l));
			}
			l++;
		}
		
		throw new IllegalArgumentException(elm);
	}
	
	public static long parsePrologTimeStamp(String input) {
		long l = 1000 * (long) Double.parseDouble(input);
		return l;
	}

	public static int findFreePort() throws IOException {
		ServerSocket ss = new ServerSocket(0);
		int port = ss.getLocalPort();
		ss.close();
		return port;
	}

	public static String prettyPrint(Collection c) {
		if (c != null && !c.isEmpty()) {
			StringBuffer sb = new StringBuffer();
			boolean first=true;
			for (Iterator it = c.iterator(); it.hasNext();) {
				if(!first){
					sb.append(", ");
				}
				Object next = it.next();
				String elm = next == null ? "<null>" : next.toString();
				sb.append(elm);
				first=false;
			}
			return sb.toString();
		}
		return "";

	}


	public static String quoteAtom(String term){
		
		return "'"+replaceAll(term,"'","\\'")+"'";
	}
	
	public static String replaceAll(String string,String search,String replace){
		int i=-1;
		StringBuffer sb=new StringBuffer();
		while((i =string.indexOf(search,0))>=0){
			sb.append(string.substring(0,i));
			sb.append(replace);
			string=string.substring(i+search.length());			
		}
		sb.append(string);
		return sb.toString();
	}
	
	public static String splice(Collection c, String delim) {
		if (c != null && !c.isEmpty()) {
			StringBuffer sb = new StringBuffer();
			for (Iterator it = c.iterator(); it.hasNext();) {
				Object next = it.next();
				sb.append(next);
				if (it.hasNext()) {
					sb.append(delim);
				}
			}
			return sb.toString();
		}
		return "";

	}

	public static String unquoteAtom(String image) {
		
		image=image.trim();
		if(image.length()==0 ||image.charAt(0)!='\''){
			return image;
		}
		image = image.substring(1,image.length()-1);
		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < image.length(); i++) {
			char c = image.charAt(i);
			if (c == '\\') {
				int len = appendUnescapedChar(image, i, sb);
				i += len - 1;
			} else {
				sb.append(c);
			}
		}
		return sb.toString();
	}
	
public static String unquoteString(String image) {
		
		image=image.trim();
		if(image.length()==0 ||image.charAt(0)!='\"'){
			return image;
		}
		image = image.substring(1,image.length()-1);
		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < image.length(); i++) {
			char c = image.charAt(i);
			if (c == '\\') {
				int len = appendUnescapedChar(image, i, sb);
				i += len - 1;
			} else {
				sb.append(c);
			}
		}
		return sb.toString();
	}

	private static int appendUnescapedChar(String image, int i, StringBuffer sb) {
		char c = image.charAt(i + 1);
		if (Character.isDigit(c)) {
			return appendUnescapedOctalCharSpec(image, i, sb);
		}
		switch (c) {
		case 'a':
			// sb.append('\a'); there is no bell char in java
			return 2;
		case 'b':
			sb.append('\b');
			return 2;
		case 'c':
			// sb.append('\c'); not supported
			return 2;
		case '\n':
			// ignoring
			return 2;
		case 'f':
			sb.append('\f');
			return 2;
		case 'n':
			sb.append('\n');
			return 2;
		case 'r':
			sb.append('\r');
			return 2;
		case 't':
			sb.append('\t');
			return 2;
		case 'v':
			// sb.append('\v'); vertical tabs are not supported in java
			return 2;
		case 'x':
			return appendUnescapedHexCharSpec(image, i, sb);
		default:
			sb.append(c);
			return 2;
		}		
	}
	private static int appendUnescapedOctalCharSpec(String image, int i,
			StringBuffer sb) {
		String val = "";
		int j=i+2;
		while(j<image.length()&&j<i+4&&isOctDigit(image.charAt(j))){
			val+=image.charAt(j);
			j++;
		}
		sb.append((char) Integer.parseInt(val,8));
		if(j<image.length()&&image.charAt(j)=='\\'){
			return 1+j-i;
		}
		return j-i;
	}
	private static int appendUnescapedHexCharSpec(String image, int i,
			StringBuffer sb) {
		
		String val = "";
		int j=i+2;
		while(j<image.length()&&j<i+4&&isHexDigit(image.charAt(j))){
			val+=image.charAt(j);
			j++;
		}
		sb.append((char)Byte.parseByte(val));
		if(j<image.length()&&image.charAt(j)=='\\'){
			return 1+j-i;
		}
		return j-i;
	}

	private static boolean isHexDigit(char c) {

		return Character.isDigit(c)
		||'a'<=Character.toLowerCase(c)
		  &&Character.toLowerCase(c)<='f';
	}

	private static boolean isOctDigit(char c) {

		return Character.isDigit(c)
		||'0'<=c
		  &&c<='7';
	}

	public static void split(String string, String search, Collection results)
	{
		if (string == null) {
			return;
		}
		int i=-1;
		while((i =string.indexOf(search,0))>=0){
			results.add(string.substring(0,i).trim());			
			string=string.substring(i+search.length());			
		}
		String rest = string.trim();
		if(rest.length()>0){
			results.add(rest);
		}

		
	}

	
	public static String[] split(String string, String search) {
		Vector v = new Vector();
		split(string,search,v);
		return (String[])v.toArray(new String[v.size()]);
		
	}

	/**
	 * fascilate testing by replacing things like $stream(1760696) with
	 * <replace>
	 * @param message
	 * @return
	 */
	public static String hideStreamHandles(String string,String replace) {
		int i=-1;
		String search="$stream(";
		StringBuffer sb=new StringBuffer();
		while((i =string.indexOf(search,0))>=0){
			sb.append(string.substring(0,i));
			sb.append(replace);
			int j=string.indexOf(')',i+search.length());
			string=string.substring(j+1);			
		}
		sb.append(string);
		return sb.toString();

	}

	

	
}