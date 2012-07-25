/*****************************************************************************
 * This file is part of the Prolog Development Tool (PDT)
 * 
 * WWW: http://sewiki.iai.uni-bonn.de/research/pdt/start
 * Mail: pdt@lists.iai.uni-bonn.de
 * Copyright (C): 2004-2012, CS Dept. III, University of Bonn
 * 
 * All rights reserved. This program is  made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 ****************************************************************************/

package org.cs3.pdt.internal.queries;

import static org.cs3.prolog.common.QueryUtils.bT;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.cs3.pdt.common.PDTCommonPredicates;
import org.cs3.pdt.internal.structureElements.PrologMatch;
import org.cs3.pdt.metadata.Goal;
import org.cs3.prolog.common.FileUtils;
import org.eclipse.core.resources.IFile;

/**
 * @author gk
 *
 */
public class ReferencesSearchQueryDirect extends PDTSearchQuery {

	
	public ReferencesSearchQueryDirect(Goal goal) {
		super(goal);
		setSearchType("References to");
	}


	@Override
	protected String buildSearchQuery(Goal goal, String module) {
		String arity = Integer.toString(goal.getArity());
		if (goal.getArity() < 0) 
			arity = "Arity";
		
		String file = "'"+goal.getFilePath()+"'";
		if (goal.getFilePath().equals(""))
			file = "File";
		
		String name = "'"+goal.getFunctor()+"'";
		if (goal.getFunctor().equals(""))
			name = "Predicate";
		
		String module2 = module;
		if (module.equals("''"))
			module2 = "Module";
		
		String query = bT(PDTCommonPredicates.FIND_REFERENCE_TO,
				name,
				arity,
				file,
				module2,
				"RefModule",
				"RefName",
				"RefArity",
				"RefFile",
				"RefLine",
				"Nth",
				"Kind",
				"PropertyList");
		return query;
	}

	
	@SuppressWarnings("unchecked")
	@Override
	protected PrologMatch constructPrologMatchForAResult(Map<String, Object> m)
	throws IOException {

		String module = (String)m.get("RefModule");
		String name = (String)m.get("RefName");
		int arity = Integer.parseInt((String)m.get("RefArity"));
		
		List<String> properties = null;
		Object prop = m.get("PropertyList");
		if (prop instanceof Vector<?>) {
			properties = (Vector<String>)prop;
		}
		IFile file = FileUtils.findFileForLocation(m.get("RefFile").toString());
		int line = Integer.parseInt(m.get("RefLine").toString());

		PrologMatch match = createUniqueMatch(module, name, arity, file, line, properties, null, "definition");
		return match;
	}
	
	public boolean isCategorized(){
		return false;
	}
	
}


