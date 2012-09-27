package org.cs3.pdt.common.queries;

import static org.cs3.prolog.common.QueryUtils.bT;

import java.io.IOException;
import java.util.Map;

import org.cs3.pdt.common.PDTCommonPredicates;
import org.cs3.pdt.common.metadata.Goal;
import org.cs3.pdt.common.structureElements.ModuleMatch;
import org.cs3.pdt.common.structureElements.SearchModuleElement;
import org.cs3.prolog.common.FileUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.search.ui.text.Match;

public class ModuleDefinitionsSearchQuery extends PDTSearchQuery {

	public ModuleDefinitionsSearchQuery(Goal goal) {
		super(goal);
		if (goal.isExactMatch()) {
			setSearchType("Definitions of module");
		} else {
			setSearchType("Definitions of modules containing");			
		}
	}

	@Override
	protected String buildSearchQuery(Goal goal, String module) {
		String query = bT(PDTCommonPredicates.FIND_MODULE_DEFINITION,
				module,
				Boolean.toString(goal.isExactMatch()),
				"File",
				"Line",
				"Module");
		return query;
	}

	@Override
	protected Match constructPrologMatchForAResult(Map<String, Object> m) throws IOException {
		String module = m.get("Module").toString();
		IFile file = FileUtils.findFileForLocation(m.get("File").toString());
		int line = Integer.parseInt(m.get("Line").toString());
		
		SearchModuleElement moduleElement = new SearchModuleElement(null, module, null);
		ModuleMatch match = new ModuleMatch(moduleElement, module, file, line);
		moduleElement.setMatch(match);

		return match;
	}

}