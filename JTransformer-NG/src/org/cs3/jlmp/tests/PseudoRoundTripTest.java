package org.cs3.jlmp.tests;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.cs3.jlmp.JLMP;
import org.cs3.jlmp.JLMPPlugin;
import org.cs3.jlmp.natures.JLMPProjectNature;
import org.cs3.pl.common.ResourceFileLocator;
import org.cs3.pl.common.Util;
import org.cs3.pl.prolog.PrologException;
import org.cs3.pl.prolog.PrologInterface;
import org.cs3.pl.prolog.PrologSession;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.text.BadLocationException;

/**
 * Testing for bytecode invariance of fact generation / source re-generation
 * roundtripp.
 * 
 * 
 * <p>
 * This testcase will
 * <ul>
 * <li>setup the converter testproject</li>
 * <li>traverse all packeges starting with "test"</li>
 * <li>for each package
 * <ul>
 * <li>create prolog facts for all files in this package.</li>
 * <li>consult the generated facts</li>
 * <li>normalize all source files in that package</li>
 * <li>compile all files in the package</li>
 * <li>rename the resulting class files by attaching the prefix ".orig" This
 * set of files is until now adressed as "the original bytecode"</li>
 * <li>rename the normalized source files by attaching the prefix ".orig" Those
 * files will be adressed as "the original source code"</li>
 * <li>regenerate the source code of all toplevels present in the prolog system
 * </li>
 * <li>normalize the resulting source files in the package. These files will
 * from now on be called "the generated sourcecode"</li>
 * <li>Assert that for each original source file there is a generated source
 * file with corresponding name.</li>
 * <li>Assert that for each generated source file there is an original source
 * file with corresponding name.</li>
 * <li>compile all files in the package, from now on adressed as "the generated
 * bytecode"</li>
 * <li>assert that for each original bytecode file there is a generated
 * bytecode file with corresponding name.</li>
 * <li>assert that for each generated bytecode file there is an original
 * bytecode file with corresponding name.</li>
 * <li>assert that each corresponding pair of original and generated bytecode
 * files is binary identical.</li>
 * </ul>
 * </li>
 * </ul>
 *  
 */
public class PseudoRoundTripTest extends FactGenerationTest {

    private final class Comparator implements IResourceVisitor {
        public boolean visit(IResource resource) throws CoreException {
            switch (resource.getType()) {
            case IResource.FOLDER:
                return true;
            case IResource.FILE:
                IFile file = (IFile) resource;
                if (!file.getFileExtension().equals("class"))
                    return false;

                IFile orig = ResourcesPlugin.getWorkspace().getRoot().getFile(
                        file.getFullPath().addFileExtension("orig"));
                assertTrue(packageName
                        + ": original class file not accessible: "
                        + orig.getFullPath().toString(), orig.isAccessible());
                //both files should be of EXACTLY the same size:
                BufferedReader origReader = new BufferedReader(
                        new InputStreamReader(orig.getContents()));
                BufferedReader genReader = new BufferedReader(
                        new InputStreamReader(file.getContents()));
                int origR = 0;
                int genR = 0;
                int i = 0;
                for (i = 0; origR != -1 && genR != -1; i++) {
                    try {
                        origR = origReader.read();
                        genR = genReader.read();
                        assertTrue(
                                packageName
                                        + ": orig and generated file differ at position "
                                        + i+": "+orig.getName(), origR == genR);
                    } catch (IOException e) {
                        org.cs3.pl.common.Debug.report(e);
                    }
                }
                org.cs3.pl.common.Debug.info("compared " + i
                        + " chars succsessfully.");
                return false;

            }
            return false;
        }
    }

    private final class Renamer implements IResourceVisitor {
        String[] extensions = null;

        String suffix = null;

        public Renamer(String extensions[], String suffix) {
            this.extensions = extensions;
            this.suffix = suffix;
        }

        public boolean visit(IResource resource) throws CoreException {
            switch (resource.getType()) {
            case IResource.FOLDER:
                return true;
            case IResource.FILE:
                IFile file = (IFile) resource;
                if (extensions == null || extensions.length == 0) {
                    file.move(file.getFullPath().addFileExtension(suffix),
                            true, null);
                    break;
                }
                for (int i = 0; i < extensions.length; i++) {
                    if (extensions[i].equals(file.getFileExtension())) {
                        file.move(file.getFullPath().addFileExtension(suffix),
                                true, null);
                        break;
                    }
                }
                break;
            case IResource.PROJECT:
                return true;
            default:
                throw new IllegalStateException("Unexpected resource type.");
            }
            return false;
        }
    }

    private String packageName;

    private PrologSession session;

    private boolean passed;

    /**
     * @param name
     */
    public PseudoRoundTripTest(String name) {
        super(name);
        this.packageName = name;
    }

    /**
     * @param string
     * @param string2
     */
    public PseudoRoundTripTest(String name, String packageName) {
        super(name);

        this.packageName = packageName;
    }

    protected Object getKey() {

        return PseudoRoundTripTest.class;
    }

    public void setUpOnce() {
        super.setUpOnce();
        PrologInterface pif = getTestJLMPProject().getPrologInterface();
        
        synchronized (pif) {

            //install test workspace
            ResourceFileLocator l = JLMPPlugin.getDefault().getResourceLocator(
                    "");
            File r = l.resolve("testdata-roundtrip.zip");
            Util.unzip(r);
            org.cs3.pl.common.Debug
                    .info("setUpOnce caled for key  " + getKey());
            setAutoBuilding(false);

            try {
                pif.start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

    public void testIt() throws CoreException, IOException,
            BadLocationException {
        PrologInterface pif = getTestJLMPProject().getPrologInterface();
        synchronized (pif) {
            testIt_impl();
            passed = true;
        }

    }

    public synchronized void testIt_impl() throws CoreException, IOException,
            BadLocationException {

        Util.startTime("untilBuild");
        IProject project = getTestProject();
        IJavaProject javaProject = getTestJavaProject();
        JLMPProjectNature jlmpProject = getTestJLMPProject();
        PrologInterface pif = jlmpProject.getPrologInterface();

        org.cs3.pl.common.Debug.info("Running (Pseudo)roundtrip in "
                + packageName);
        //retrieve all cus in package
        ICompilationUnit[] cus = getCompilationUnitsInFolder(packageName);
        //normalize source files
        Util.startTime("norm1");
        for (int i = 0; i < cus.length; i++) {
            ICompilationUnit cu = cus[i];

            try {
                normalizeCompilationUnit(cu);
            } catch (Exception e) {
                throw new RuntimeException(packageName
                        + ": could not normalize cu " + cu.getElementName(), e);
            }
        }
        Util.printTime("norm1");
        Util.printTime("untilBuild");
        Util.startTime("build1");
        build(JavaCore.BUILDER_ID);
        build(JLMP.BUILDER_ID);
        Util.printTime("build1");
        Util.startTime("untilQueryToplevels");
        //now we should have SOME toplevelT
        assertNotNull(packageName + ": no toplevelT????", session
                .queryOnce("toplevelT(_,_,_,_)"));

        //and checkTreeLinks should say "yes"
        //assertNotNull("checkTreeLinks reports errors",
        // session.queryOnce("checkTreeLinks"));

        IResourceVisitor renamer = new Renamer(new String[]{"java","class"},"orig");
        Util.startTime("rename");
        IFolder folder = project.getFolder(packageName);
        folder.accept(renamer);
        Util.printTime("rename");

        //next, we use gen_tree on each toplevelT node known to the system.
        //as a result we should be able to regenerate each and every source
        // file we consulted
        //in the first step
        String query = "toplevelT(ID,_,FILENAME,_),gen_tree(ID,CONTENT)";
        Util.printTime("untilQueryToplevels");
        Util.startTime("queryToplevels");

        List results = null;
        try {
            results = session.queryAll(query);
        } catch (PrologException e) {
            throw new RuntimeException(packageName + ": " + e.getMessage(), e);
        }
        Util.printTime("queryToplevels");
        Util.startTime("writeToplevels");
        for (Iterator iter = results.iterator(); iter.hasNext();) {
            Map result = (Map) iter.next();
            String filename = result.get("FILENAME").toString();
            String content = result.get("CONTENT").toString();
            //clean the facts right away. another testcase might be running
            //concurrently and this is the easiest way to keep things from
            // interfering
            session
                    .queryOnce("remove_contained_global_ids('" + filename
                            + "')");
            session.queryOnce("delete_toplevel('" + filename + "')");
            assertTrue(packageName + ": problems writing generated file.",
                    createFile(filename, content).isAccessible());
            IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(
                    new Path(filename));
            String newContent = Util.toString(file.getContents());
            assertEquals(packageName, content, newContent);
        }
        Util.printTime("writeToplevels");
        //refetch cus
        Util.startTime("norm2");
        cus = getCompilationUnitsInFolder(packageName);
        //normalize again (now the generated source)
        for (int i = 0; i < cus.length; i++) {
            ICompilationUnit cu = cus[i];
            normalizeCompilationUnit(cu);

        }
        Util.printTime("norm2");
        //build again.(the generated source)
        Util.startTime("build2");

        build(JavaCore.BUILDER_ID);
        Util.printTime("build2");
        //now, visit each file in the binFolder, that has the .class extension.
        //and compare it to the respective original class file (which should
        // have the same name + .orig)
        IResourceVisitor comparator = new Comparator();
        Util.startTime("compare");
        folder.accept(comparator);
        Util.printTime("compare");
    }

    protected synchronized void setUp() throws Exception {
        super.setUp();
        PrologInterface pif = getTestJLMPProject().getPrologInterface();
        synchronized (pif) {
            waitForPif();
            assertTrue(pif.isUp());
            session = pif.getSession();
            if (session == null) {
                fail("failed to obtain session");
            }
            setTestDataLocator(JLMPPlugin.getDefault().getResourceLocator(
                    "testdata-roundtrip"));

            install(packageName);
            passed = false;
        }
    }

    protected synchronized void tearDown() throws Exception {
        super.tearDown();
        PrologInterface pif = getTestJLMPProject().getPrologInterface();
        synchronized (pif) {
            
            session.dispose();
            if (passed) {
                uninstall(packageName);
            } else {
                //if anything breaks,
                //we must make sure not interfere with consecutive tests.
                //1) move any left java or class files out of the way of the next build
                IFolder folder = getTestProject().getFolder(packageName);
                folder.accept(new Renamer(new String[] { "java", "class" },
                        "bak"));
                //2) restart the pif
                pif.stop();
                assertTrue(pif.isDown());
                pif.start();
            }
        }

    }

    public void tearDownOnce() {
        super.tearDownOnce();
        PrologInterface pif = getTestJLMPProject().getPrologInterface();
        synchronized (pif) {
            session.dispose();
            try {
                pif.stop();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

    public static Test suite() {
        TestSuite s = new TestSuite();
        BitSet blacklist = new BitSet();
        
        /*XXX: for these the generated code is aequivalent, but the formating is not!
         *These "differences" 
         * should be normalized away. So this IS an error on my part,
         *  an error in the test bed, to be precise..
         * These errors are not very critical though.  
         */
        //blacklist.set(130);
        //blacklist.set(160);
        
        //      XXX:ld:These seems to break consecutive tests. excluded until fixed.
        //blacklist.set(178);
        //blacklist.set(200);
        //blacklist.set(242);
        //blacklist.set(433);

        //these two are missing for some reason
        blacklist.set(157);
        blacklist.set(158);
        
        
        //ld: the following few do not compile. ergo, not our prob.
        blacklist.set(44);
        blacklist.set(78);
        blacklist.set(79);
        blacklist.set(80);
        blacklist.set(81);
        blacklist.set(86);
        blacklist.set(87);
        blacklist.set(118); //funny though, the builder eats it despite the compile errors??!
       
        blacklist.set(150);
        blacklist.set(152);
        blacklist.set(153);//the builder eats it anyway.
        for (int i = 165; i <=200; i++)//1-539 
            if (!blacklist.get(i))
                s.addTest(new PseudoRoundTripTest("testIt",
                        generatePackageName(i)));
        return s;
    }

    /**
     * @param i
     * @return
     */
    private static String generatePackageName(int n) {
        int desiredLength = 4;
        String number = String.valueOf(n);
        int padLength = desiredLength - number.length();
        StringBuffer sb = new StringBuffer("test");
        for (int i = 0; i < padLength; i++)
            sb.append('0');
        sb.append(number);
        return sb.toString();
    }

}