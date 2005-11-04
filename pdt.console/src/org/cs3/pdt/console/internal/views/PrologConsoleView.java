package org.cs3.pdt.console.internal.views;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.cs3.pdt.console.PDTConsole;
import org.cs3.pdt.console.PrologConsolePlugin;
import org.cs3.pdt.console.internal.ImageRepository;
import org.cs3.pdt.console.internal.hooks.ConsoleServerHook;
import org.cs3.pdt.core.IPrologProject;
import org.cs3.pdt.core.PDTCore;
import org.cs3.pdt.runtime.PrologInterfaceRegistry;
import org.cs3.pdt.runtime.PrologInterfaceRegistryEvent;
import org.cs3.pdt.runtime.PrologInterfaceRegistryListener;
import org.cs3.pdt.runtime.PrologRuntimePlugin;
import org.cs3.pdt.ui.util.UIUtils;
import org.cs3.pl.common.Debug;
import org.cs3.pl.console.ConsoleModel;
import org.cs3.pl.console.NewConsoleHistory;
import org.cs3.pl.console.prolog.PrologConsole;
import org.cs3.pl.console.prolog.PrologConsoleEvent;
import org.cs3.pl.console.prolog.PrologConsoleListener;
import org.cs3.pl.metadata.DefaultMetaInfoProvider;
import org.cs3.pl.prolog.LifeCycleHook;
import org.cs3.pl.prolog.PrologInterface;
import org.cs3.pl.prolog.PrologSession;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ControlContribution;
import org.eclipse.jface.action.IContributionManager;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.progress.UIJob;

public class PrologConsoleView extends ViewPart implements LifeCycleHook,
		PrologConsole {
	public final class PifSelector extends ControlContribution implements SelectionListener, PrologInterfaceRegistryListener {
		private Combo combo;
		private Vector entries=new Vector();

		public PifSelector(String id) {
			super(id);
		}

		protected Control createControl(Composite parent) {
			
			combo = new Combo (parent, SWT.READ_ONLY);
			fillCombo();
			combo.addSelectionListener(this);
			PrologInterfaceRegistry reg = PrologRuntimePlugin.getDefault().getPrologInterfaceRegistry();
			reg.addPrologInterfaceRegistryListener(this);
			return combo;
		}

		private void fillCombo() {
			PrologInterfaceRegistry reg = PrologRuntimePlugin.getDefault().getPrologInterfaceRegistry();
			Set keys =  reg.getRegisteredKeys();
			for (Iterator it = keys.iterator(); it.hasNext();) {
				String key = (String) it.next();
				String label = reg.getName(key);
				if(label==null){
					label=key;
				}
				entries.add(key);
				combo.add(label);
			}
			
			combo.pack ();
		}

		public void widgetSelected(SelectionEvent e) {
			String key = (String) entries.get(combo.getSelectionIndex());
			PrologInterfaceRegistry reg = PrologRuntimePlugin.getDefault().getPrologInterfaceRegistry();
			String currentKey=reg.getKey(getPrologInterface());
			if(!key.equals(currentKey)){
				PrologConsoleView.this.setPrologInterface(reg.getPrologInterface(key));
			}
		}

		public void widgetDefaultSelected(SelectionEvent e) {
			String key = (String) entries.get(combo.getSelectionIndex());
			PrologInterfaceRegistry reg = PrologRuntimePlugin.getDefault().getPrologInterfaceRegistry();
			String currentKey=reg.getKey(getPrologInterface());
			if(!key.equals(currentKey)){
				PrologConsoleView.this.setPrologInterface(reg.getPrologInterface(key));
			}				
			
		}

		public void prologInterfaceAdded(final PrologInterfaceRegistryEvent e) {
			if(combo.isDisposed()){
				return;
			}
			if(combo.getDisplay()!=Display.getCurrent()){
				combo.getDisplay().asyncExec(new Runnable() {
				
					public void run() {
						prologInterfaceAdded(e);				
					}
				
				});
				return;
			}
			PrologInterfaceRegistry reg = PrologRuntimePlugin.getDefault().getPrologInterfaceRegistry();
			String key = e.key;
			String label = reg.getName(key);
			if(label==null){
				label=key;
			}
			if(entries.contains(key)){
				return;
			}
			entries.add(key);
			combo.add(label);			
			combo.pack ();			
		}

		public void prologInterfaceRemoved(final PrologInterfaceRegistryEvent e) {
			if(combo.isDisposed()){
				return;
			}
			if(combo.getDisplay()!=Display.getCurrent()){
				combo.getDisplay().asyncExec(new Runnable() {
				
					public void run() {
						prologInterfaceRemoved(e);				
					}
				
				});
				return;
			}	
			combo.remove(entries.indexOf(e.key));
		}

		public void subscriptionAdded(PrologInterfaceRegistryEvent e) {
			;
		}

		public void subscriptionRemoved(PrologInterfaceRegistryEvent e) {
			;
		}
		
		public void selectPrologInterface(final PrologInterface pif){
			if(combo.isDisposed()){
				return;
			}
			if(combo.getDisplay()!=Display.getCurrent()){
				combo.getDisplay().asyncExec(new Runnable() {
				
					public void run() {
						selectPrologInterface(pif);				
					}
				
				});
				return;
			}
			if(pif==null){
				combo.deselectAll();
				return;
			}
			PrologInterfaceRegistry reg = PrologRuntimePlugin.getDefault().getPrologInterfaceRegistry();
			String key = reg.getKey(pif);
			String label = reg.getName(key);
			if(label==null){
				label=key;
			}
			int i = entries.indexOf(key);
			if(i<0){
				combo.add(label);
				entries.add(key);
			}
			combo.select(entries.size()-1);
			
		}
	}

	private final class ClearAction extends Action {
		private ClearAction(String text, String tooltip,ImageDescriptor image) {
			super(text, image);
			setToolTipText(tooltip);
		}

		public void run() {
			getViewer().clearOutput();
		}
	}

	private final class ConsoleAction extends Action {
		private String query;

		public ConsoleAction(String query, String text, String tooltip,
				ImageDescriptor icon) {
			super(text, icon);
			this.query = query.trim().endsWith(".")?query:query+".";
			setToolTipText(tooltip);
		}

		public void run() {
			try {

				Job j = new Job(getToolTipText()) {

					protected IStatus run(IProgressMonitor monitor) {
						try {
							PrologConsole c = getConsole();
							ConsoleModel model = c.getModel();
							model.setLineBuffer(" ");
							model.commitLineBuffer();
							model.setLineBuffer(query);
							model.commitLineBuffer();
						} catch (Throwable e) {
							Debug.report(e);
							return Status.CANCEL_STATUS;
						} finally {
							monitor.done();
						}
						return Status.OK_STATUS;
					}

					private PrologConsole getConsole() {
						return PrologConsoleView.this;
					}

				};
				j.schedule();
			} catch (Throwable t) {
				Debug.report(t);
			}
		}
	}

	private final class RestartAction extends Action {
		public void run() {
			try {

				Job j = new UIJob("Restarting the PrologInterface") {

					public IStatus runInUIThread(IProgressMonitor monitor) {
						try {
							monitor.beginTask("initializing...",
									IProgressMonitor.UNKNOWN);

							try {
								if(pif!=null){
									pif.stop();	
								}
								//setPrologInterface(getEditorPrologInterface());
							} finally {								
								if(pif!=null){
									pif.start();
								}
							}
						} catch (Throwable e) {
							Debug.report(e);
							return Status.CANCEL_STATUS;
						} finally {
							monitor.done();
						}
						return Status.OK_STATUS;
					}

					
				};
				j.schedule();
			} catch (Throwable t) {
				Debug.report(t);
			}

		}

		public ImageDescriptor getImageDescriptor() {
			return ImageRepository.getImageDescriptor(ImageRepository.RESTART);
		}

		public String getToolTipText() {
			return "restart";
		}

		public String getText() {
			return "restart";
		}
	}

	public static final String HOOK_ID = "org.cs3.pdt.console.internal.views.PrologConsoleView";

	private ConsoleViewer viewer;

	private PrologSocketConsoleModel model;

	private PrologCompletionProvider completionProvider;

	private Composite partControl;

	private Vector listeners = new Vector();

	private PrologInterface pif;

	private Menu contextMenu;

	private Action cutAction;

	private Action copyAction;

	private Action pasteAction;

	private Action selectAllAction;

	private ConsoleAction activateGuiTracerAction;

	private ClearAction clearAction;

	private ConsoleAction deactivateGuiTracerAction;

	private RestartAction restartAction;

	private NewConsoleHistory history;

	private PifSelector pifSelector;

	public void createPartControl(Composite parent) {

		try {
			createPartControl_impl(parent);
		} catch (Throwable t) {
			Debug.report(t);
			throw new RuntimeException(t.getLocalizedMessage(), t);
		}
	}

	
	
	
	private void createPartControl_impl(Composite parent) {

		this.partControl = parent;

		Listener handler = new Listener() {

			public void handleEvent(Event event) {
				switch (event.type) {
				case SWT.Show:
				case SWT.Hide:
					fireConsoleVisibilityChanged();
					break;
				case SWT.FocusOut:
					fireConsoleLostFocus();
				}

			}

		};
		parent.addListener(SWT.Show, handler);
		parent.addListener(SWT.Hide, handler);
		parent.addListener(SWT.FocusOut, handler);
		PrologConsolePlugin.getDefault().getPrologConsoleService()
				.registerPrologConsole(this);
		viewer = new ConsoleViewer(parent, SWT.BORDER | SWT.MULTI | SWT.WRAP
				| SWT.V_SCROLL);
		viewer.getControl().setEnabled(false);
		PrologInterface thepif = getEditorPrologInterface();
	
		
		setPrologInterface(thepif);
		
		
		
		
		
		createActions();
		initMenus(parent);
		initToolBars();
		getSite().setSelectionProvider(viewer);
		//configureAndConnect();
		//This is a bit "shoot-first-talk-later"-ish, but it should do.
		getViewSite().getPage().addPartListener(new IPartListener2() {
		
			public void partInputChanged(IWorkbenchPartReference partRef) {
				if ( partRef instanceof IEditorReference ){
					editorInputChanged();
				}
		
			}
		
			public void partVisible(IWorkbenchPartReference partRef) {
				if ( partRef instanceof IEditorReference ){
					editorInputChanged();
				}
		
			}
		
			public void partHidden(IWorkbenchPartReference partRef) {
//				if ( partRef instanceof IEditorReference ){
//					editorInputChanged();
//				}
		
			}
		
			public void partOpened(IWorkbenchPartReference partRef) {
				if ( partRef instanceof IEditorReference ){
					editorInputChanged();
				}
		
			}
		
			public void partDeactivated(IWorkbenchPartReference partRef) {
//				if ( partRef instanceof IEditorReference ){
//					editorInputChanged();
//				}
		
			}
		
			public void partClosed(IWorkbenchPartReference partRef) {
//				if ( partRef instanceof IEditorReference ){
//					editorInputChanged();
//				}
		
			}
		
			public void partBroughtToTop(IWorkbenchPartReference partRef) {
				if ( partRef instanceof IEditorReference ){
					editorInputChanged();
				}
		
			}
		
			public void partActivated(IWorkbenchPartReference partRef) {
				if ( partRef instanceof IEditorReference ){
					editorInputChanged();
				}
		
			}
		
		});
		
	}



// 
	private void createCombo(IToolBarManager toolBarManager) {
		toolBarManager.add(new PifSelector(PDTConsole.CONTRIB_PIF_SELECTOR_ID));
	}




	protected void editorInputChanged() {
		PrologInterface thePif = getEditorPrologInterface();
		if(thePif!=null&&thePif!=pif){
			Debug.debug("new pif from editor");
			setPrologInterface(thePif);
		}
		
	}




	private PrologInterface getEditorPrologInterface() {
		IEditorPart activeEditor = UIUtils.getActiveEditor();
		if(activeEditor==null){
			return null;
		}
		IFileEditorInput editorInput;
		try{			
			editorInput = (IFileEditorInput) activeEditor.getEditorInput();
		}catch(ClassCastException mirEgal){
			return null;
		}
		if(editorInput==null){
			return null;
		}
		IPrologProject nature;
		
		try {
			
			IProject project = editorInput.getFile().getProject();
			if(!project.hasNature(PDTCore.NATURE_ID)){
				return null;
			}
			nature = (IPrologProject) project.getNature(PDTCore.NATURE_ID);
		} catch (CoreException e) {
			Debug.report(e);
			throw new RuntimeException(e);
		}
		PrologInterface thepif = nature.getRuntimePrologInterface();
		return thepif;
	}

	

	private void loadHistory() {
		
		try {
			FileInputStream in = new FileInputStream(getHistoryFile());
			history.loadHistory(in);
			in.close();
		} catch (IOException e) {
			Debug.report(e);
		}
		
	}

	private void createActions() {
		cutAction = new Action() {
			public void run() {
				viewer.cut();
			}
		};
		
		copyAction = new Action() {
			public void run() {
				viewer.copy();
			}
		};
		pasteAction = new Action() {
			public void run() {
				viewer.paste();
			}
		};
		selectAllAction = new Action() {
			public void run() {
				viewer.selectAll();
			}
		};
		clearAction = new ClearAction("Clear","clear console output", ImageRepository
						.getImageDescriptor(ImageRepository.CLEAR));
		activateGuiTracerAction = new ConsoleAction("guitracer", "activate guitracer",
				"activate GUI tracer", ImageRepository
						.getImageDescriptor(ImageRepository.GUITRACER));
		deactivateGuiTracerAction = new ConsoleAction("noguitracer", "deactivate guitracer",
				"deactivate GUI tracer", ImageRepository
						.getImageDescriptor(ImageRepository.NOGUITRACER));

		restartAction = new RestartAction();
	}

	private void initMenus(Control parent) {
		
		MenuManager manager = new MenuManager();
		manager.setRemoveAllWhenShown(true);
		manager.addMenuListener(new IMenuListener() {

			public void menuAboutToShow(IMenuManager manager) {
				addContributions(manager);

			}

		});
		getSite().registerContextMenu(manager, viewer);
		contextMenu = manager.createContextMenu(parent);
		viewer.getControl().setMenu(contextMenu);
		// ContextMenuProvider menuProvider = new ContextMenuProvider();
		// menuProvider.addMenu(parent);
		
		
		
		
	}




	private void initToolBars() {
		IActionBars bars = this.getViewSite().getActionBars();

		bars.setGlobalActionHandler(ActionFactory.SELECT_ALL.getId(),
				selectAllAction);
		bars.setGlobalActionHandler(ActionFactory.CUT.getId(), cutAction);
		bars.setGlobalActionHandler(ActionFactory.COPY.getId(), copyAction);
		bars.setGlobalActionHandler(ActionFactory.PASTE.getId(), pasteAction);
		IToolBarManager toolBarManager = bars.getToolBarManager();

		addContributions(toolBarManager);
		//this.pifSelector = new PifSelector(PDTConsole.CONTRIB_PIF_SELECTOR_ID);
		//toolBarManager.add(pifSelector);
	}

	private void addContributions(IContributionManager manager) {
		IWorkbenchWindow window = getSite().getWorkbenchWindow();
		manager.add(new Separator("#ConsoleInternal"));
		manager.add(clearAction);
		manager.add(activateGuiTracerAction);
		manager.add(deactivateGuiTracerAction);
		manager.add(restartAction);
		manager.add(new Separator("#ConsoleInternal-end"));
		manager.add(new Separator("#Clipboard"));
		IWorkbenchAction sall = ActionFactory.SELECT_ALL.create(window);
		sall.setImageDescriptor(ImageRepository.getImageDescriptor(ImageRepository.SELECT_ALL));
		manager.add(sall);
		manager.add(ActionFactory.COPY.create(window));
		manager.add(ActionFactory.CUT.create(window));
		manager.add(ActionFactory.PASTE.create(window));
		manager.add(new Separator("#Clipboard-end"));
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS
				+ "-end"));
	}

	private File getHistoryFile(){
		String value = PrologConsolePlugin.getDefault().getPreferenceValue(
				PDTConsole.PREF_CONSOLE_HISTORY_FILE, null);
		if (value == null) {
			throw new NullPointerException("Required property \""
					+ PDTConsole.PREF_CONSOLE_HISTORY_FILE + "\" was not specified.");
		}
		return new File(value);
	}

	

	public void setFocus() {
		if (viewer == null) {
			Debug
					.warning("PrologConsoleView.setFocus(): View not instantiated yet.");
			return;
		}
		viewer.getControl().setFocus();
		fireConsoleRecievedFocus();
	}

	private void fireConsoleRecievedFocus() {
		Vector clone = null;
		synchronized (listeners) {
			clone = (Vector) listeners.clone();
		}
		PrologConsoleEvent e = new PrologConsoleEvent(this);
		for (Iterator iter = clone.iterator(); iter.hasNext();) {
			PrologConsoleListener l = (PrologConsoleListener) iter.next();
			l.consoleRecievedFocus(e);
		}
	}

	private void fireConsoleLostFocus() {
		Vector clone = null;
		synchronized (listeners) {
			clone = (Vector) listeners.clone();
		}
		PrologConsoleEvent e = new PrologConsoleEvent(this);
		for (Iterator iter = clone.iterator(); iter.hasNext();) {
			PrologConsoleListener l = (PrologConsoleListener) iter.next();
			l.consoleLostFocus(e);
		}
	}

	private void fireConsoleVisibilityChanged() {
		Vector clone = null;
		synchronized (listeners) {
			clone = (Vector) listeners.clone();
		}
		PrologConsoleEvent e = new PrologConsoleEvent(this);
		for (Iterator iter = clone.iterator(); iter.hasNext();) {
			PrologConsoleListener l = (PrologConsoleListener) iter.next();
			l.consoleVisibilityChanged(e);
		}
	}

	public void dispose() {
		PrologConsolePlugin.getDefault().getPrologConsoleService()
				.unregisterPrologConsole(this);
		disconnect();
		contextMenu.dispose();
		// viewer.getControl().dispose();
		super.dispose();
	}

	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cs3.pl.prolog.LifeCycleHook#onInit(org.cs3.pl.prolog.PrologSession)
	 */
	public void onInit(PrologInterface pif, PrologSession initSession) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cs3.pl.prolog.LifeCycleHook#afterInit()
	 */
	public void afterInit(PrologInterface pif) {
		// viewer.setController(controller);
		configureAndConnect();
		
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cs3.pl.prolog.LifeCycleHook#beforeShutdown(org.cs3.pl.prolog.PrologSession)
	 */
	public void beforeShutdown(PrologInterface pif, PrologSession session) {
		// viewer.setController(null);
		model.disconnect();
		saveHistory();
	}

	private void saveHistory() {
		try {
			FileOutputStream out = new FileOutputStream(getHistoryFile());
			history.saveHistory(out);
			out.close();
		} catch (IOException e) {
			Debug.report(e);
		}		
	}

	public ConsoleModel getModel() {
		return model;
	}

	public PrologInterface getPrologInterface() {
		return pif;
	}

	public void setPrologInterface(PrologInterface newPif){
		if(pif!=null){
			disconnect();	
		}
		
		this.pif=newPif;
		if(pif!=null){
			addHooks();
			configureAndConnect();	
			
		}
		else{
			Debug.debug("no pif (yet).");
		}
		if(pifSelector!=null){
			pifSelector.selectPrologInterface(pif);
		}
	}




	private void disconnect() {
		model.disconnect();
		removeHooks();
		if(viewer.getControl().isDisposed()){
			return;
		}
		if(Display.getCurrent()!=viewer.getControl().getDisplay()){
			viewer.getControl().getDisplay().asyncExec(new Runnable(){
				public void run(){
					if(!viewer.getControl().isDisposed()){
						viewer.getControl().setEnabled(false);
					}
				}
			});
			return;
		}
		if(!viewer.getControl().isDisposed()){
			viewer.getControl().setEnabled(false);
		}
		
		
	}
	
	private void addHooks() {
		pif.addLifeCycleHook(new ConsoleServerHook(),ConsoleServerHook.HOOK_ID,new String[0]);
		pif.addLifeCycleHook(this, HOOK_ID,
				new String[] { ConsoleServerHook.HOOK_ID });
		
	}




	private void configureAndConnect() {
		
		if(!pif.isUp()){
			//no problem, hook will call us back once pif is up.
			return;
		}
		
		if(Display.getCurrent()!=viewer.getControl().getDisplay()){
			viewer.getControl().getDisplay().asyncExec(new Runnable(){
				public void run(){
					configureAndConnect();
				}
			});
			return;
		}
		
		PrologSession session = pif.getSession();
		
		Map m = null;
		try{
			 m = session.queryOnce("current_predicate(pdt_console_server/2),pdt_console_server(Port,LockFile)");
				//maybe the pif is up, but the server is not yet installed.
			 	//this does happen, if setPrologInterface is called with a pif
			 	//that is up but was not yet configured for the console.
			 	//we will manualy execute onInit on the ConsoleServerHook
			    //TODO: merge both console hooks. no need to have two anymore.
			 if(m==null){
				 new ConsoleServerHook().onInit(pif,session);
				 m = session.queryOnce("pdt_console_server(Port,LockFile)");
			 }
			 if(m==null){
				 //now we really have a problem
				 throw new RuntimeException("could not install console server");
			 }
		}finally{
			if(session!=null){
				session.dispose();
			}
		}

		
		int port = Integer.parseInt(m.get("Port").toString());
		File lockFile = new File((String) m.get("LockFile"));
		if(model==null){
			model = new PrologSocketConsoleModel(false);
		}
		model.setPort(port);
		model.setLockFile(lockFile);
		
		viewer.setModel(model);
		if (lockFile.exists()) {
			model.connect();
		}
		boolean useEnter = Boolean.valueOf(PrologConsolePlugin.getDefault().getPreferenceValue(PDTConsole.PREF_ENTER_FOR_BACKTRACKING,"false")).booleanValue();
		viewer.setEnterSendsSemicolon(useEnter);
		
		
		completionProvider = new PrologCompletionProvider();
		completionProvider.setMetaInfoProvider(new DefaultMetaInfoProvider(pif));
		viewer.setCompletionProvider(completionProvider);
		
		history = new NewConsoleHistory();
		
		viewer.setHistory(history);
		loadHistory();
		viewer.getControl().setEnabled(true);
	}




	private void removeHooks() {
		
		pif.removeLifeCycleHook(HOOK_ID);
		pif.removeLifeCycleHook(ConsoleServerHook.HOOK_ID);	
	}

	


	public void addPrologConsoleListener(PrologConsoleListener l) {
		synchronized (listeners) {
			if (!listeners.contains(l)) {
				listeners.add(l);
			}
		}

	}

	public void removePrologConsoleListener(PrologConsoleListener l) {
		synchronized (listeners) {
			if (listeners.contains(l)) {
				listeners.remove(l);
			}
		}
	}

	public boolean isVisible() {
		return partControl.isVisible();
	}

	public ConsoleViewer getViewer() {
		return viewer;
	}

	public String getText() {
		return getViewer().getText();
	}

	public int getLineAtOffset(int offset) {
		return getViewer().getLineAtOffset(offset);
	}

	public int getOffsetAtLine(int line) {
		return getViewer().getOffsetAtLine(line);
	}

	public int getLineCount() {
		return getViewer().getLineCount();
	}

	public void clearOutput() {
		getViewer().clearOutput();

	}

	public String getTextRange(int offset, int length) {
		return getViewer().getTextRange(offset, length);
	}

	public int getCaretOffset() {
		return getViewer().getCaretOffset();
	}
}