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

package org.cs3.pl.prolog;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

public class PrologEventDispatcher extends DefaultAsyncPrologSessionListener {
	
	private HashMap listenerLists = new HashMap();
	/*
	 * XXX i don't like the idea of keeping a reference to this
	 * session on the heap. 
	 * This has proven a bad practice in the past.
	 * Is there any other way to solve this?
	 */
	private AsyncPrologSession session;
	Object observerTicket = new Object();
	Object eventTicket = new Object();
	private PrologInterface2 pif;
	public PrologEventDispatcher(PrologInterface2 pif) {
		this.pif=pif;
	}
	protected void finalize() throws Throwable {
		if(session!=null){
			stop();
			
		}
	}
	
	public void addPrologInterfaceListener(String subject,
            PrologInterfaceListener l) {
        synchronized (listenerLists) {
            Vector list = (Vector) listenerLists.get(subject);
            if (list == null) {
                list = new Vector();
                listenerLists.put(subject, list);
                enableSubject(subject);
            }
            if (!list.contains(l)) {
                list.add(l);
            }
        }

    }

   
	/*
     * (non-Javadoc)
     * 
     * @see org.cs3.pl.prolog.IPrologInterface#removePrologInterfaceListener(java.lang.String,
     *          org.cs3.pl.prolog.PrologInterfaceListener)
     */
    public void removePrologInterfaceListener(String subject,
            PrologInterfaceListener l) {
        synchronized (listenerLists) {
            Vector list = (Vector) listenerLists.get(subject);
            if (list == null) {
                return;
            }
            if (list.contains(l)) {
                list.remove(l);
            }
            if(list.isEmpty()){
            	disableSubject(subject);
            	listenerLists.remove(subject);
            }
        }

    }
    
    private void enableSubject(String subject) {
    	if(session==null){
    		session=pif.getAsyncSession();
    		session.addBatchListener(this);
    	}else{
    		abort();
    	}
    	PrologSession s = pif.getSession();
    	try{
    		s.queryOnce("observe('"+session.getProcessorThreadAlias()+"',"+subject+",'"+subject+"')");
    	}
    	finally{
    		s.dispose();
    	}
		//session.queryOnce(observerTicket,"thread_self(_Me),observe(_Me,"+subject+",'"+subject+"')");
		dispatch();
	}
	
	private void disableSubject(String subject) {
    	if(session==null){
    		return;
    	}
    	
    	abort(); 		
		session.queryOnce(observerTicket,"thread_self(_Me),unobserve(_Me,"+subject+")");
		if(!listenerLists.isEmpty()){
			dispatch();	
		}				
	}
	private void dispatch() {
		session.queryAll(eventTicket,"dispatch(Subject,Key,Event)");
	}
    private void abort() {
    	PrologSession s =pif.getSession();
    	try{
    		s.queryOnce("thread_send_message('"+session.getProcessorThreadAlias()+"',notify('$abort',_))");
    	}
    	finally{
    		if(s!=null){
    			s.dispose();
    		}
    	}
		
	}
    public void stop(){
    	if(session==null){
    		return;
    	}
    	abort();
    	session.dispose();
    	session=null;
    }
	
	/**
     * @param subject2
     * @param string
     */
    private void fireUpdate(String subject, String key, String event) {
        Vector listeners = (Vector) listenerLists.get(key);
        if (listeners == null) {
            return;
        }
        PrologInterfaceEvent e = new PrologInterfaceEvent(this, subject,key,
                event);

        Vector cloned = null;
        synchronized (listeners) {
            cloned = (Vector) listeners.clone();
        }
        for (Iterator it = cloned.iterator(); it.hasNext();) {
            PrologInterfaceListener l = (PrologInterfaceListener) it.next();
            l.update(e);
        }
    }
    
    public void goalHasSolution(AsyncPrologSessionEvent e) {
    	String subject = (String) e.bindings.get("Subject");
    	if("$abort".equals(subject)){
    		return;
    	}
    	String key = (String) e.bindings.get("Key");
    	String event = (String) e.bindings.get("Event");
    	fireUpdate(subject,key,event);
    }
	public void abortComplete(AsyncPrologSessionEvent e) {
		// TODO Auto-generated method stub
		super.abortComplete(e);
	}
	public void batchComplete(AsyncPrologSessionEvent e) {
		// TODO Auto-generated method stub
		super.batchComplete(e);
	}
	public void goalCut(AsyncPrologSessionEvent e) {
		// TODO Auto-generated method stub
		super.goalCut(e);
	}
	public void goalFailed(AsyncPrologSessionEvent e) {
		// TODO Auto-generated method stub
		super.goalFailed(e);
	}
	public void goalRaisedException(AsyncPrologSessionEvent e) {
		// TODO Auto-generated method stub
		super.goalRaisedException(e);
	}
	public void goalSkipped(AsyncPrologSessionEvent e) {
		// TODO Auto-generated method stub
		super.goalSkipped(e);
	}
	public void goalSucceeded(AsyncPrologSessionEvent e) {
		// TODO Auto-generated method stub
		super.goalSucceeded(e);
	}
	public void joinComplete(AsyncPrologSessionEvent e) {
		// TODO Auto-generated method stub
		super.joinComplete(e);
	}
}
