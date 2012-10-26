package org.sopeco.frontend.client.layout.center.experiment;

import org.sopeco.frontend.client.model.ScenarioManager;
import org.sopeco.frontend.client.widget.tree.TreeItem;
import org.sopeco.persistence.entities.definition.ParameterDefinition;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.CheckBox;

/**
 * 
 * @author Marius Oehler
 * 
 */
public class ParameterTreeItemLeaf extends TreeItem implements ValueChangeHandler<Boolean> {

	private static final String CSS_LEAF_CLASS = "selectionTreeItemLeaf";
	private Element textElement;
	private CheckBox preperationCheckBox, experimentCheckBox;
	private ParameterDefinition parameter;

	public ParameterTreeItemLeaf(ParameterDefinition parameterDefinition) {
		this(parameterDefinition, false, false);
	}

	public ParameterTreeItemLeaf(ParameterDefinition parameterDefinition, boolean preperation, boolean experiment) {
		super(parameterDefinition.getName());

		parameter = parameterDefinition;

		if (preperation) {
			preperationCheckBox.setValue(true);
		} else if (experiment) {
			experimentCheckBox.setValue(true);
		}
	}

	@Override
	protected void initialize() {
		addStyleName(CSS_LEAF_CLASS);

		textElement = DOM.createSpan();
		textElement.setInnerHTML(getText());

		preperationCheckBox = new CheckBox();
		experimentCheckBox = new CheckBox();

		preperationCheckBox.addValueChangeHandler(this);
		experimentCheckBox.addValueChangeHandler(this);

		getElement().appendChild(textElement);
		add(experimentCheckBox);
		add(preperationCheckBox);
	}

	@Override
	public void onValueChange(ValueChangeEvent<Boolean> event) {
		if (event.getSource() == preperationCheckBox) {
			if (event.getValue()) {
				experimentCheckBox.setValue(false, true);
				ScenarioManager.get().experiment().addPreperationAssignment(parameter);
			} else {
				ScenarioManager.get().experiment().removePreperationAssignment(parameter);
			}
		} else if (event.getSource() == experimentCheckBox) {
			if (event.getValue()) {
				preperationCheckBox.setValue(false, true);
				ScenarioManager.get().experiment().addExperimentAssignment(parameter);
			} else {
				ScenarioManager.get().experiment().removeExperimentAssignment(parameter);
			}
		}
	}
}