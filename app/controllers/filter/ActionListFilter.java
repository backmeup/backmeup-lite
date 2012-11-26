package controllers.filter;

import java.util.ArrayList;
import java.util.List;

import org.backmeup.api.actions.ActionDescription;

public class ActionListFilter {
	
	public List<ActionDescription> actions;
	
	public ActionListFilter(List<ActionDescription> actions) {
		this.actions = new ArrayList<ActionDescription>();
		for (ActionDescription a : actions) {
			this.actions.add(a);
		}
	}


}
