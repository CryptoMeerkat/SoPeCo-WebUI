package org.sopeco.frontend.client.layout.navigation;

import java.util.HashMap;

import org.sopeco.frontend.client.layout.center.CenterType;
import org.sopeco.frontend.shared.rsc.R;

import com.google.gwt.user.client.ui.FlowPanel;

/**
 * 
 * The Navigation Widget.
 * 
 * @author Marius Oehler
 * 
 */
public class NavigationView extends FlowPanel {

	/**
	 * Width of this panel.
	 */
	public static final String PANEL_WIDTH = "15";
	private static final int ITEM_MAP_SIZE = 4;
	private static final float ITEM_MAP_LOAD_FACTOR = 1F;

	private HashMap<CenterType, NavigationItem> naviItems;
	private FlowPanel experimentsPanel;
	private ChangeSpecificationPanel changeSpecificationPanel;

	public NavigationView() {
		initialize();
	}

	/**
	 * Initializes all necessary widgets.
	 */
	private void initialize() {
		setWidth(PANEL_WIDTH + "em");
		setHeight("100%");

		experimentsPanel = new FlowPanel();

		naviItems = new HashMap<CenterType, NavigationItem>(ITEM_MAP_SIZE, ITEM_MAP_LOAD_FACTOR);

		addNaviItem(CenterType.Environment, R.get("environment"));
		addNaviItem(CenterType.Specification, R.get("specification"));

		add(experimentsPanel);

		addNaviItem(CenterType.Execute, R.get("execute"));
		addNaviItem(CenterType.Result, R.get("result"));

		changeSpecificationPanel = naviItems.get(CenterType.Specification).addChangeSpecificationIcon();
	}

	/**
	 * Adding a new item to the navigation and add it to the itemMap, where all
	 * items of the navigation are stored.
	 * 
	 * @param type
	 *            which panel should be displayed, when this item is clicked
	 * @param text
	 *            the text on the item
	 */
	private void addNaviItem(CenterType type, String text) {
		naviItems.put(type, new NavigationItem(type, text));
		add(naviItems.get(type));
	}

	/**
	 * Returns panel, to change the specifications.
	 * 
	 * @return ChangeSpecificationPanel
	 */
	public ChangeSpecificationPanel getChangeSpecificationPanel() {
		return changeSpecificationPanel;
	}

	/**
	 * Returns the HashMap, which contains all items of the navigation.
	 * 
	 * @return hashmap
	 */
	public HashMap<CenterType, NavigationItem> getNaviItemsMap() {
		return naviItems;
	}

	/**
	 * Adds a new, indented item to the list of experiment-items in the
	 * navigation.
	 * 
	 * @param text
	 *            on the item
	 */
	public NavigationSubItem addExperimentItem(String text) {
		NavigationSubItem newItem = new NavigationSubItem(text);
		experimentsPanel.add(newItem);
		return newItem;
	}
}
