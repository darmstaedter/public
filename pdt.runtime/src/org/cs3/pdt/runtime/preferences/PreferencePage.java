package org.cs3.pdt.runtime.preferences;

import org.cs3.pdt.runtime.PrologRuntime;
import org.cs3.pdt.runtime.PrologRuntimePlugin;
import org.cs3.pl.prolog.PrologInterface;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * This class represents a preference page that is contributed to the
 * Preferences dialog. By subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows us to create a page
 * that is small and knows how to save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They are stored in the
 * preference store that belongs to the main plug-in class. That way,
 * preferences can be accessed directly via the preference store.
 */

public class PreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
	
	
	public PreferencePage() {
		super(GRID);
		
		PrologRuntimePlugin plugin = PrologRuntimePlugin.getDefault();
		setPreferenceStore(plugin.getPreferenceStore());
		setDescription("Preferences for the Prolog Interface");

	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common
	 * GUI blocks needed to manipulate various types of preferences. Each field
	 * editor knows how to save and restore itself.
	 */
	public void createFieldEditors() {
		// Will be passed to SWI-Prolog using the -p command line option.
		// Make sure that the library(consult_server) can be resolved.
	//	addField(new StringFieldEditor(PrologInterface.PREF_FILE_SEARCH_PATH, "File Search Path", getFieldEditorParent()));
	
		
		// The factory to be used for creating PrologInterface instances
//		addField(new StringFieldEditor(PrologRuntime.PREF_PROLOGIF_IMPLEMENTATION, "PrologInterface implementation", getFieldEditorParent()));
//		addField(new ComboFieldEditor(PrologRuntime.PREF_PROLOGIF_IMPLEMENTATION,"PrologInterface implementation",
//				  new String[][] {
//                	{"Socket (stable)", AbstractPrologInterface.PL_INTERFACE_DEFAULT},
//                	{"PIFcom (very experimental - don't change, if you don't know what you do)", AbstractPrologInterface.PL_INTERFACE_PIFCOM}
//				}
//                ,getFieldEditorParent()));	
		
		// The PrologInterface needs to temporarily store some
		// prolog files during bootstrapping. Any directory for which 
		// you have write permissions will do.
		addField(new DirectoryFieldEditor(PrologRuntime.PREF_PIF_BOOTSTRAP_DIR, "PrologInterface Bootstrap Directory", getFieldEditorParent()));

		// eg. xpce or /usr/bin/xpce
		addField(new StringFieldEditor(PrologInterface.PREF_EXECUTABLE, "SWI-Prolog executable", getFieldEditorParent()));

		// A comma-separated list of VARIABLE=VALUE pairs.
		addField(new StringFieldEditor(PrologInterface.PREF_ENVIRONMENT, "Extra environment variables", getFieldEditorParent()));
	
		// If true, the PIF will not try to start and stop its own server
		// process.
		BooleanFieldEditor standalone = new BooleanFieldEditor(PrologInterface.PREF_STANDALONE, "stand-alone server", getFieldEditorParent());
		standalone.setEnabled(false, getFieldEditorParent());
		addField(standalone);

		// The host the PIF server is listening on
		StringFieldEditor host = new StringFieldEditor(PrologInterface.PREF_HOST, "Server host", getFieldEditorParent());
		host.setEnabled(false, getFieldEditorParent());
		addField(host);

	
	
		// Maximum time in milliseconds to wait for the prolog process to come up.
		addField(new IntegerFieldEditor(PrologInterface.PREF_TIMEOUT, "Connect Timeout", getFieldEditorParent()));
		
		


	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}

}