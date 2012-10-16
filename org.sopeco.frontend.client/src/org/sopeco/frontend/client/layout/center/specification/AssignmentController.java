package org.sopeco.frontend.client.layout.center.specification;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author Marius Oehler
 * 
 */
class AssignmentController {
	private AssignmentView view;
	private List<AssignmentItem> assignmentList;

	public AssignmentController() {
		reset();
	}

	/**
	 * Creates a new assignmentView.
	 */
	public void reset() {
		assignmentList = new ArrayList<AssignmentItem>();

		view = new AssignmentView();
	}

	/**
	 * Returns the current view.
	 * 
	 * @return AssignmentView
	 */
	public Widget getAssignmentView() {
		return view.getInScrollPanel();
	}

	/**
	 * Adding a new assignmentitem to the assignmentList and to the
	 * assignmentListPanel.
	 * 
	 * @param assignment
	 */
	public void addAssignment(AssignmentItem assignment) {
		assignmentList.add(assignment);
		view.add(assignment);
	}
}
