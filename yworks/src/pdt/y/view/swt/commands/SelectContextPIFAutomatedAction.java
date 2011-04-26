package pdt.y.view.swt.commands;
//a/**
// * 
// */
//package pdt.y.swt.commands;
//
//import java.util.HashSet;
//import java.util.Iterator;
//import java.util.Set;
//
//import org.cs3.pdt.runtime.PrologInterfaceRegistry;
//import org.cs3.pdt.runtime.PrologRuntimePlugin;
//import org.cs3.pdt.runtime.Subscription;
//import org.cs3.pdt.runtime.ui.PrologContextTracker;
//import org.cs3.pdt.runtime.ui.PrologContextTrackerListener;
//import org.cs3.pdt.runtime.ui.PrologContextTrackerService;
//import org.cs3.pdt.runtime.ui.PrologRuntimeUIPlugin;
//import org.cs3.pl.common.Util;
//import org.cs3.pl.prolog.PrologInterface;
//import org.eclipse.jface.action.Action;
//import org.eclipse.jface.action.ActionContributionItem;
//import org.eclipse.jface.action.IAction;
//import org.eclipse.jface.action.IMenuCreator;
//import org.eclipse.jface.resource.ImageDescriptor;
//import org.eclipse.jface.viewers.ISelection;
//import org.eclipse.swt.SWT;
//import org.eclipse.swt.events.MenuAdapter;
//import org.eclipse.swt.events.MenuEvent;
//import org.eclipse.swt.events.SelectionEvent;
//import org.eclipse.swt.events.SelectionListener;
//import org.eclipse.swt.widgets.Control;
//import org.eclipse.swt.widgets.Display;
//import org.eclipse.swt.widgets.Menu;
//import org.eclipse.swt.widgets.MenuItem;
//import org.eclipse.ui.ISharedImages;
//import org.eclipse.ui.IWorkbenchWindow;
//import org.eclipse.ui.IWorkbenchWindowPulldownDelegate2;
//import org.eclipse.ui.PlatformUI;
//
///**
// * @author morales
// * 
// */
//public abstract class SelectContextPIFAutomatedAction extends Action implements
//		IMenuCreator, IWorkbenchWindowPulldownDelegate2,
//		PrologContextTrackerListener {
//
//	
//	private static ImageDescriptor image;
//
//	static {
//		image = ImageDescriptor.createFromImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_PROJECT));
//	}
//	private Menu createdMenu;
//	private Set<String> activeTrackers;
//	private IAction fAction;
//	private IWorkbenchWindow window;
//	private boolean unifiedTrackerEnabled;
//
//	protected abstract void setPrologInterface(PrologInterface prologInterface);
//	protected abstract PrologInterface getPrologInterface();
//	protected abstract void trackerActivated();
//	protected abstract void trackerDeactivated();
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see org.eclipse.jface.action.IMenuCreator#dispose()
//	 */
//	public void dispose() {
//		if (createdMenu != null) {
//			createdMenu.dispose();
//		}
//	}
//
//	public SelectContextPIFAutomatedAction() {
//		super();
//
//		setText(null);
//		setMenuCreator(this);
//	}
//
//	
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see
//	 * org.eclipse.jface.action.IMenuCreator#getMenu(org.eclipse.swt.widgets
//	 * .Control)
//	 */
//	public Menu getMenu(Control parent) {
//		if (getCreatedMenu() != null) {
//			getCreatedMenu().dispose();
//		}
//		setCreatedMenu(new Menu(parent));
//		if (getCreatedMenu() != null) {
//			getCreatedMenu().dispose();
//		}
//		setCreatedMenu(new Menu(parent));
//		PrologContextTracker[] trackers = PrologRuntimeUIPlugin.getDefault()
//				.getContextTrackerService().getContextTrackers();
//
//		createContextUnifiedAction(getCreatedMenu(), trackers);
//
//		new MenuItem(createdMenu, SWT.SEPARATOR);
//		fillMenu();
//		initMenu();
//		return getCreatedMenu();
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see
//	 * org.eclipse.jface.action.IMenuCreator#getMenu(org.eclipse.swt.widgets
//	 * .Menu)
//	 */
//	public Menu getMenu(Menu parent) {
//		if (getCreatedMenu() != null) {
//			getCreatedMenu().dispose();
//		}
//		setCreatedMenu(new Menu(parent));
//		PrologContextTracker[] trackers = PrologRuntimeUIPlugin.getDefault()
//				.getContextTrackerService().getContextTrackers();
//
//		createContextUnifiedAction(getCreatedMenu(), trackers);
//
//		new MenuItem(createdMenu, SWT.SEPARATOR);
//
//		fillMenu();
//		initMenu();
//		return getCreatedMenu();
//	}
//
//	private void initMenu() {
//		// Add listener to repopulate the menu each time
//		// it is shown to reflect changes in selection or active perspective
//
//		createdMenu.addMenuListener(new MenuAdapter() {
//			public void menuShown(MenuEvent e) {
//				Menu m = (Menu) e.widget;
//				MenuItem[] items = m.getItems();
////				for loop starts with index 2 because first two elements of menu are not to be processed here
//				for (int i = 2; i < items.length; i++) {
//					items[i].dispose();
//				}
//				fillMenu();
//			}
//		});
//
//	}
//
//	// PIF Selection part
//	private void fillMenu() {
//		PrologInterfaceRegistry reg = PrologRuntimePlugin.getDefault().getPrologInterfaceRegistry();
//		Set<String> keys = reg.getAllKeys();
//		for (Iterator<String> it = keys.iterator(); it.hasNext();) {
//			String key = it.next();
//
//			createPIFAction(getCreatedMenu(), reg, key);
//		}
//	}
//
//	private void createContextUnifiedAction(Menu parent,
//			final PrologContextTracker[] trackers) {
//		final MenuItem item = new MenuItem(parent, SWT.CHECK);
//
//		item.setText("Follow Mode");
//		unifiedTrackerEnabled = !getActiveTrackers().isEmpty();
//		item.setSelection(unifiedTrackerEnabled);
//		
//		item.addSelectionListener(new SelectionListener() {
//
//			public void widgetDefaultSelected(SelectionEvent e) {
//
//			}
//
//			public void widgetSelected(SelectionEvent e) {
//				MenuItem item = (MenuItem) e.getSource();
//				if (item.getSelection()) {
//					for (int i = 0; i < trackers.length; i++) {
//						final String trackerId = trackers[i].getId();
//
//						getActiveTrackers().add(trackerId);
//						trackers[i]
//								.addPrologContextTrackerListener(SelectContextPIFAutomatedAction.this);
//
//						trackerActivated(trackers[i]);
//
//						PrologConsolePlugin.getDefault().setPreferenceValue(
//								PDTConsole.PREF_CONTEXT_TRACKERS,
//								Util.splice(getActiveTrackers(), ","));
//
//					}
//					setImageDescriptor(ImageRepository
//							.getImageDescriptor(ImageRepository.FOLLOW_MODE));
//				} else {
//					for (int i = 0; i < trackers.length; i++) {
//						final String trackerId = trackers[i].getId();
//
//						if (getActiveTrackers().contains(trackerId)) {
//							getActiveTrackers().remove(trackerId);
//							trackers[i]
//									.removePrologContextTrackerListener(SelectContextPIFAutomatedAction.this);
//
//							trackerDeactivated(trackers[i]);
//						}
//
//						PrologConsolePlugin.getDefault().setPreferenceValue(
//								PDTConsole.PREF_CONTEXT_TRACKERS,
//								Util.splice(getActiveTrackers(), ","));
//					}
//					setImageDescriptor(ImageRepository
//							.getImageDescriptor(ImageRepository.MANUAL_MODE_FREE));
//				}
//
//			}
//		});
//
//	}
//
//	private void createPIFAction(Menu menu, PrologInterfaceRegistry reg,
//			final String key) {
//		Set<Subscription> subs = reg.getSubscriptionsForPif(key);
//		if (subs.size() == 0) {
//			return;
//		}
//		// remove hidden subscriptions from the set
//		boolean showHidden = "true".equals(PrologConsolePlugin.getDefault()
//				.getPreferenceValue(PDTConsole.PREF_SHOW_HIDDEN_SUBSCRIPTIONS,
//						"false"));
//		if (!showHidden) {
//			for (Iterator<Subscription> it = subs.iterator(); it.hasNext();) {
//				Subscription sub = it.next();
//				if (!sub.isVisible()) {
//					it.remove();
//				}
//			}
//			if (subs.isEmpty()) {
//				return;
//			}
//		}
//		IAction action = new Action(key, IAction.AS_RADIO_BUTTON) {
//			public void run() {
//				if (this.isChecked())
//					setPrologInterface(PrologRuntimeUIPlugin.getDefault()
//							.getPrologInterface(key));
//				if(!unifiedTrackerEnabled) SelectContextPIFAutomatedAction.this.setImageDescriptor(ImageRepository
//						.getImageDescriptor(ImageRepository.MANUAL_MODE));
//			}
//
//		};
//		action.setChecked(key.equals(reg.getKey(getPrologInterface())));
//		StringBuffer buf = new StringBuffer();
//
//		buf.append(key);
//		buf.append(": ");
//		for (Iterator<Subscription> it = subs.iterator(); it.hasNext();) {
//			Subscription sub = it.next();
//			if (!sub.isVisible() && !showHidden) {
//				continue;
//			}
//			buf.append(sub.getName());
//			if (it.hasNext()) {
//				buf.append(", ");
//			}
//		}
//		action.setText(buf.toString());
//		action.setEnabled(!unifiedTrackerEnabled);
//		ActionContributionItem item = new ActionContributionItem(action);
//
//		item.fill(menu, -1);
//
//	}
//
//	private void setCreatedMenu(Menu menu) {
//		createdMenu = menu;
//
//	}
//
//	private Menu getCreatedMenu() {
//		return createdMenu;
//
//	}
//	
//	Set<String> getActiveTrackers() {
//		if (activeTrackers == null) {
//			activeTrackers = new HashSet<String>();
//			Util.split(PrologConsolePlugin.getDefault().getPreferenceValue(
//					PDTConsole.PREF_CONTEXT_TRACKERS, ""), ",", activeTrackers);
//			PrologContextTrackerService trackerService = PrologRuntimeUIPlugin
//					.getDefault().getContextTrackerService();
//			for (Iterator<String> iter = activeTrackers.iterator(); iter.hasNext();) {
//				String id = iter.next();
//				PrologContextTracker contextTracker = trackerService
//						.getContextTracker(id);
//				if (contextTracker != null) {
//					contextTracker.addPrologContextTrackerListener(this);
//				}
//			}
//		}
//		return activeTrackers;
//	}
//
//	/**
//	 * @see IAction#run()
//	 */
//	public void run() {
//		// do nothing, this action just creates a cascading menu.
//	}
//
//	/**
//	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
//	 */
//	public void run(IAction action) {
//		// do nothing - this is just a menu
//	}
//
//	public void init(IWorkbenchWindow window) {
//		this.window = window;
//
//	}
//
//	/**
//	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction,
//	 *      org.eclipse.jface.viewers.ISelection)
//	 */
//	public void selectionChanged(IAction action, ISelection selection) {
//		if (fAction == null) {
//			initialize(action);
//		}
//	}
//
//	/**
//	 * Set the enabled state of the underlying action based on whether there are
//	 * any registered launch shortcuts for this launch mode.
//	 */
//	private void initialize(IAction action) {
//		fAction = action;
//	}
//
//	public PrologInterface getCurrentPrologInterface() {
//
//		for (Iterator<String> it = getActiveTrackers().iterator(); it.hasNext();) {
//			String trackerId = it.next();
//			PrologContextTracker tracker = PrologRuntimeUIPlugin.getDefault()
//					.getContextTrackerService().getContextTracker(trackerId);
//			if (tracker == null) {
//				return null;
//			}
//			PrologInterface pif = null;
//			pif = tracker.getCurrentPrologInterface();
//			if (pif != null) {
//				return pif;
//			}
//		}
//		return null;
//	}
//
//	public void update() {
//		if (window == null) {
//			return;
//		}
//		Display display = window.getShell().getDisplay();
//		if (display != Display.getCurrent()) {
//			display.asyncExec(new Runnable() {
//
//				public void run() {
//					update();
//
//				}
//
//			});
//			return;
//		}
//		PrologInterfaceRegistry reg = PrologRuntimePlugin.getDefault().getPrologInterfaceRegistry();
//		PrologInterface pif = getPrologInterface();
//		if (pif == null) {
//			setToolTipText("no pif selected");
//			return;
//		}
//		String key = reg.getKey(pif);
//		if (key == null) {
//			setToolTipText("unregisterd Prologinterface???");
//			return;
//		}
//		setToolTipText(key);
//
//	}
//}