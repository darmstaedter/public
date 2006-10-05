/*
 */
package org.cs3.jtransformer;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Vector;

import org.cs3.pdt.ui.util.DefaultErrorMessageProvider;
import org.cs3.pdt.ui.util.ErrorMessageProvider;
import org.cs3.pdt.ui.util.UIUtils;
import org.cs3.pl.common.Debug;
import org.cs3.pl.common.DefaultResourceFileLocator;
import org.cs3.pl.common.Option;
import org.cs3.pl.common.ResourceFileLocator;
import org.cs3.pl.common.SimpleOption;
import org.cs3.pl.common.Util;
import org.cs3.pl.prolog.PrologException;
import org.cs3.pl.prolog.PrologInterfaceException;
import org.cs3.pl.prolog.PrologSession;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The Java Logical Meta-Programming.(aka JTransformer) Plugin. <br>
 * Not much here. You may want to see JTransformerProject.
 * 
 * @see JTransformerProject
 */
public class JTransformerPlugin extends AbstractUIPlugin {

    private static JTransformerPlugin plugin;

    private ResourceBundle resourceBundle;

    private ResourceFileLocator rootLocator;

    private Vector projectlisteners = new Vector();

    private Option[] options;

    public JTransformerPlugin() {
        super();
        plugin = this;
        try {
            resourceBundle = ResourceBundle
                    .getBundle("prg.cs3.pdt.PDTPluginResources"); //$NON-NLS-1$
        } catch (MissingResourceException x) {
            resourceBundle = null;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     */
    public void start(BundleContext context) throws Exception {
        super.start(context);        
        initOptions();
        getPluginPreferences();
        collectListeners();
    }

    /**
     * @throws CoreException
     *  
     */
    private void initOptions() throws CoreException {
//        IJavaProject dummyOutput = getDummyOutput();
//        IClasspathEntry firstSourceFolder = getFirstSourceFolder(dummyOutput);
//        IResource r= ResourcesPlugin.getWorkspace().getRoot().findMember(firstSourceFolder.getPath());
//        String src= r.getLocation().toOSString();
        String storeFile = getResourceLocator("").resolve("global_pef_store.pl").toString(); //$NON-NLS-1$ //$NON-NLS-2$
        options = new Option[] { 
//                new SimpleOption(
//                JTransformer.PREF_DEFAULT_OUTPUT_PROJECT,
//                "Default output source folder", //$NON-NLS-1$
//                "Used as default value for JTransformer Projects that do not specify their own output folder.", //$NON-NLS-1$
//                Option.STRING, src),
//                new SimpleOption(
//                JTransformer.PREF_DEFAULT_OUTPUT_FOLDER,
//                "Default output source folder", //$NON-NLS-1$
//                "Used as default value for JTransformer Projects that do not specify their own output folder.", //$NON-NLS-1$
//                Option.STRING, src),
                new SimpleOption(
                        JTransformer.PREF_USE_PEF_STORE,
                        "Use PEF store (EXPERIMENTAL)", //$NON-NLS-1$
                        "If enabled, JTransformer will save PEFs before shutdown and reload them at startup. ", //$NON-NLS-1$
                        Option.FLAG, "false"), //$NON-NLS-1$
                new SimpleOption(
                        JTransformer.PREF_DEFAULT_PEF_STORE_FILE,
                        "Default PEF store file", //$NON-NLS-1$
                        "Used as default value for JTransformer Projects " //$NON-NLS-1$
                                + "that do not specify their own store file.", //$NON-NLS-1$
                        Option.FILE, storeFile)};

    }

    
    public IClasspathEntry getFirstSourceFolder(IJavaProject javaProject) throws JavaModelException {
        IClasspathEntry[] cp = javaProject.getResolvedClasspath(true);
        for(int i=0;i<cp.length;i++){
            if(cp[i].getEntryKind()==IClasspathEntry.CPE_SOURCE){
               return cp[i];
            }
        }
        return null;
    }

//    /**
//     * @return
//     * @throws CoreException
//     */
//    private IJavaProject getDummyOutput() throws CoreException {
//        String projectName = "test-output"; //$NON-NLS-1$
//        IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
//        if(!project.exists()){
//            project=createProject(projectName);
//        }
//        if(!project.isOpen()){
//            project.open(null);
//        }
//        IJavaProject javaProject = (IJavaProject) project.getNature(JavaCore.NATURE_ID);
//        if(!project.hasNature(JavaCore.NATURE_ID)){            
//            addNature(project, JavaCore.NATURE_ID);            
//            IClasspathEntry[] cp = new IClasspathEntry[] {
//                    JavaCore.newSourceEntry(project.getFullPath()),
//                    JavaRuntime.getDefaultJREContainerEntry(),
//
//            };
//            javaProject = (IJavaProject) project.getNature(JavaCore.NATURE_ID);
//            javaProject.setRawClasspath(cp, project.getFullPath(),
//                    null);
//        }
//        return javaProject;
//    }
    /*
     * Create simple project.
     */
    private IProject createProject(final String projectName)
            throws CoreException {
        final IProject project = ResourcesPlugin.getWorkspace().getRoot()
                .getProject(projectName);
        IWorkspaceRunnable create = new IWorkspaceRunnable() {
            public void run(IProgressMonitor monitor) throws CoreException {
                if (project.exists()) {
                    try {
                        project.open(null);
                    } catch (Throwable t) {

                    }
                    project.delete(true, null);
                }
                project.create(null);
                project.open(null);
            }
        };
        ResourcesPlugin.getWorkspace().run(create,null);
        return project;
    }
    
    private void addNature(IProject project, String id) throws CoreException {
        if (!project.hasNature(id)) {
            IProjectDescription ipd = project.getDescription();
            String[] oldNIDs = ipd.getNatureIds();
            String[] newNIDs = new String[oldNIDs.length + 1];
            newNIDs[0] = id;
            System.arraycopy(oldNIDs, 0, newNIDs, 1, oldNIDs.length);
            ipd.setNatureIds(newNIDs);
            if (!project.isSynchronized(IResource.DEPTH_ONE)) {
                project.refreshLocal(IResource.DEPTH_ONE, null);
            }
            project.setDescription(ipd, null);
        }
    }
    /**
     * Returns the shared instance.
     */
    public static JTransformerPlugin getDefault() {
        return plugin;
    }

    /**
     * Returns a resource file locator for a given key.
     * <p>
     * The current implementation returns the value of
     * <code>rootLocator.subLocator(key)</code> where root locator "points" to
     * the installation directory of the plugin.
     * 
     * @param key
     *                should be a valid filesystem path element.
     * @return a resource file locator
     */
    public ResourceFileLocator getResourceLocator(String key) {
        if (rootLocator == null) {
            URL url = getBundle().getEntry("/"); //$NON-NLS-1$
            File location = null;
            try {
                location = new File(Platform.asLocalURL(url).getFile());
            } catch (IOException t) {
                Debug.report(t);
                throw new RuntimeException(t);
            }

            rootLocator = new DefaultResourceFileLocator(location);
        }
        return rootLocator.subLocator(key);
    }

    private void collectListeners() {
        IExtensionRegistry registry = Platform.getExtensionRegistry();
        IExtensionPoint point = registry.getExtensionPoint("org.cs3.jtransformer", //$NON-NLS-1$
                JTransformer.EP_PROJECT_LISTENER);
        if (point == null) {
            Debug.error("could not find the extension point " //$NON-NLS-1$
                    + JTransformer.EP_PROJECT_LISTENER);
            return;
        }
        IExtension[] extensions = point.getExtensions();
        try {
            for (int i = 0; i < extensions.length; i++) {
                IConfigurationElement[] celem = extensions[i]
                        .getConfigurationElements();
                for (int j = 0; j < celem.length; j++) {

                    if (!celem[j].getName().equals("listener")) { //$NON-NLS-1$
                        Debug.warning("hmmm... asumed a listener, but got a " //$NON-NLS-1$
                                + celem[j].getName());
                    } else {
                        JTransformerProjectListener listener = (JTransformerProjectListener) celem[j]
                                .createExecutableExtension("class"); //$NON-NLS-1$
                        projectlisteners.add(listener);
                    }
                }
            }
        } catch (CoreException e) {
            Debug.report(e);
        }
    }

    /**
     * notify lregistered listeners. <br>
     * This method is typicaly called by a JTransformerProject when it recieves
     * notification from the builder. This method multiplexes the call to
     * listeners registered via the JTransformer Project Listener extension point <br>
     * Clients should not call this method directly.
     * 
     * @param e
     *                the update event.
     */
    public void fireFactBaseUpdated(JTransformerProjcetEvent e) {
        Vector cloned = null;
        synchronized (projectlisteners) {
            cloned = (Vector) projectlisteners.clone();
        }
        for (Iterator it = cloned.iterator(); it.hasNext();) {
            JTransformerProjectListener l = (JTransformerProjectListener) it.next();
            l.factBaseUpdated(e);
        }
    }

    public Option[] getOptions() {
        return this.options;
    }

    /**
     * look up a preference value.
     * <p>
     * will return user settings if available or default settings if not. If a
     * system property with the given key is defined it will overrule any
     * existing setting in the preference store. if the key is not defined, this
     * method returns the given default..
     * 
     * @param key
     * @return the value or specified default if no such key exists..
     */
    public String getPreferenceValue(String key, String defaultValue) {        
        IPreferencesService service = Platform.getPreferencesService();
        String qualifier = getBundle().getSymbolicName();

        // XXX: schmatz: is the try-catch ok? returning default value ok?
        
        // schmatz: surrounded with try-catch
        String value = null;
        try
		{
            // XXX: schmatz: Note: the next line has thrown a NPE everytime when closing the runtime Eclipse IDE!
        	value = service.getString(qualifier, key, defaultValue, null);	
		}
        catch (Exception e)
		{
			return defaultValue;
		}
        
        return System.getProperty(key, value);
    }
    
    /**
     * conveniance method. should propably be inlined. --lu
     */
    public void setPreferenceValue(String key, String value){
        Preferences prefStore = getPluginPreferences();
        prefStore.setValue(key, value);
        savePluginPreferences();
    }

    /**
     * reload preferences and configure components.
     * <br><b>NOTE:</b> clients should not call this method directly.
     */
    public void reconfigure() {
        try {
			IProject[] projects = ResourcesPlugin.getWorkspace().getRoot()
					.getProjects();
			for (int i = 0; i < projects.length; i++) {
				IProject project = projects[i];

				if (project.isAccessible() && project.hasNature(JTransformer.NATURE_ID)) {
					JTransformerProject jtransformerProject = (JTransformerProject) project
							.getNature(JTransformer.NATURE_ID);
					
					jtransformerProject.reconfigure();
					
				}
			}
		} catch (Throwable e) {
			Debug.report(e);
			throw new RuntimeException(e);
		}
    }
    /**
     * @throws PrologInterfaceException 
     * @throws PrologException 
     * @deprecated 
     */
    public  void reload(PrologSession initSession) throws PrologException, PrologInterfaceException {       
        String storeName = JTransformerPlugin.getDefault().getPreferenceValue(JTransformer.PREF_DEFAULT_PEF_STORE_FILE,null);
        File storeFile = new File(storeName);
         if(storeFile.canRead()){
            initSession.queryOnce("['"+Util.prologFileName(storeFile)+"']"); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }
    /**
     * @throws PrologInterfaceException 
     * @throws PrologException 
     * @deprecated 
     */    
    public  void save(PrologSession shutdownSession) throws PrologException, PrologInterfaceException{
        String storeName = JTransformerPlugin.getDefault().getPreferenceValue(JTransformer.PREF_DEFAULT_PEF_STORE_FILE,null);
        File storeFile = new File(storeName);
        
            shutdownSession.queryOnce("writeTreeFacts('"+Util.prologFileName(storeFile)+"')"); //$NON-NLS-1$ //$NON-NLS-2$
        
    }

	public ErrorMessageProvider getErrorMessageProvider()
	{
		return new JTransformerErrorMessageProvider(plugin);
	}
	
	public void createPrologInterfaceExceptionCoreExceptionWrapper(PrologInterfaceException e1) throws CoreException
	{
		throw new CoreException(UIUtils.createErrorStatus(
				new DefaultErrorMessageProvider(getDefault()), e1,
				JTransformer.ERR_PROLOG_INTERFACE_EXCEPTION));
	}

	class JTransformerErrorMessageProvider implements ErrorMessageProvider {
		
		private Plugin plugin;

		public JTransformerErrorMessageProvider(Plugin plugin) {
			this.plugin = plugin;
		}

		public String getErrorMessage(int errCode)
		{
			return Messages.getString("JTransformerPlugin." + errCode); //$NON-NLS-1$
		}

		public String getContextMessage(int cxCode)
		{
			return Messages.getString("JTransformerPlugin." + cxCode); //$NON-NLS-1$
		}

		public String getId()
		{
			return getPlugin().getBundle().getSymbolicName();
		}

		public Plugin getPlugin()
		{
			return plugin;
		}
		
	};

}
