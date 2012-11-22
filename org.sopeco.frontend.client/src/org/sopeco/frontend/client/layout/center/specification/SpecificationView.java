package org.sopeco.frontend.client.layout.center.specification;

import org.sopeco.frontend.client.R;
import org.sopeco.gwt.widgets.ClearDiv;

import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author Marius Oehler
 * 
 */
public class SpecificationView extends FlowPanel {

	private static final String IMAGE_DUPLICATE = "images/duplicate.png";
	private static final String IMAGE_RENAME = "images/rename.png";
	private static final String IMAGE_REMOVE = "images/trash.png";


	private Widget selectionView, assignmentListPanel;
	private int selectionPanelPosition;
	private boolean selectionPanelIsVisible;

	private HTML htmlName;
	private FlowPanel topWrapper;
	private Image imgDuplicate, imgRemove, imgRename;

	private static final String TOP_PANEL_HEIGHT = "39";
	private static final int SELECTION_PANEL_WIDTH = 400;

	public SpecificationView(Widget assignmentView, Widget sView) {
		assignmentListPanel = assignmentView;
		selectionView = sView;

		initialize();
	}

	/**
	 * Initializing the widgets.
	 */
	private void initialize() {
		setWidth("100%");
		setHeight("100%");

		htmlName = new HTML("1234567890");
		htmlName.addStyleName("name");

		imgRename = new Image(IMAGE_RENAME);
		imgDuplicate = new Image(IMAGE_DUPLICATE);
		imgRemove = new Image(IMAGE_REMOVE);

		imgRename.setTitle(R.get("Rename"));
		imgDuplicate.setTitle(R.get("Duplicate"));
		imgRemove.setTitle(R.get("Remove"));

		topWrapper = new FlowPanel();
		topWrapper.add(new HTML("Name:"));
		topWrapper.add(htmlName);
		topWrapper.add(imgRename);
		//topWrapper.add(imgDuplicate);
		topWrapper.add(imgRemove);
		topWrapper.add(new ClearDiv());
		topWrapper.addStyleName("expTopWrapper");


		Label nameLabel = new Label(R.get("name") + ":");
		nameLabel.addStyleName("spc-Label");


		selectionView.getElement().getStyle().setTop(Double.parseDouble(TOP_PANEL_HEIGHT), Unit.PX);
		selectionView.getElement().getStyle().setPosition(Position.ABSOLUTE);
		selectionView.getElement().getStyle().setWidth(SELECTION_PANEL_WIDTH, Unit.PX);

		assignmentListPanel.getElement().getStyle().setPosition(Position.ABSOLUTE);
		assignmentListPanel.getElement().getStyle().setTop(Double.parseDouble(TOP_PANEL_HEIGHT), Unit.PX);

		setSelectionPanelPosition(1);
		selectionPanelIsVisible = false;

		// ADDING
		add(topWrapper);
		add(assignmentListPanel);
		add(selectionView);
	}

	/**
	 * Set the position of the selection panel and of all related elements like
	 * the toggleSelectionElement and the assignmentListPanel. parameter is a
	 * percent value between 0 (0%) and 1 (100%).
	 * 
	 * @param x
	 */
	private void setSelectionPanelPosition(float x) {
		x = Math.max(0F, Math.min(1F, x));
		selectionPanelPosition = (int) (SELECTION_PANEL_WIDTH * x);

		selectionView.getElement().getStyle().setLeft(selectionPanelPosition - SELECTION_PANEL_WIDTH, Unit.PX);
		assignmentListPanel.getElement().getStyle().setLeft(selectionPanelPosition, Unit.PX);
	}

	/**
	 * Set whether the selectionPanel is visible.
	 * 
	 * @param visible
	 */
	public void setSelectionPanelVisible(boolean visible) {
		selectionPanelIsVisible = visible;
		if (visible) {
			setSelectionPanelPosition(1F);
		} else {
			setSelectionPanelPosition(0);
		}
	}

	/**
	 * @return the selectionPanelVisible
	 */
	public boolean isSelectionPanelVisible() {
		return selectionPanelIsVisible;
	}

	/**
	 * Setting the given string to the specificationName-Textbox.
	 * 
	 * @param name
	 */
	public void setSpecificationName(String name) {
		// textboxName.setText(name);
		htmlName.setText(name);
	}

	public Image getImgDuplicate() {
		return imgDuplicate;
	}

	public Image getImgRemove() {
		return imgRemove;
	}

	public Image getImgRename() {
		return imgRename;
	}

	/**
	 * Returns the textbox of the specification name.
	 * 
	 * @return
	 */
	// public TextBox getSpecificationNameTextbox() {
	// return textboxName;
	// }

}
