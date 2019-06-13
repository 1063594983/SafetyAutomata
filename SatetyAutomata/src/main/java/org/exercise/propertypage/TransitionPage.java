package org.exercise.propertypage;

import java.util.List;

import org.modelio.api.module.IModule;
import org.modelio.api.module.propertiesPage.AbstractModulePropertyPage;
import org.modelio.api.module.propertiesPage.IModulePropertyTable;
import org.modelio.metamodel.uml.behavior.stateMachineModel.Transition;
import org.modelio.vcore.smkernel.mapi.MObject;

public class TransitionPage extends AbstractModulePropertyPage{

	public TransitionPage(IModule module, String name, String label, String bitmap) {
		super(module, name, label, bitmap);
	
	}

	@Override
	public void changeProperty(List<MObject> elements, int row, String value) {
		if (elements.size() == 1 && elements.get(0) instanceof Transition) {
			Transition modelElement = (Transition) elements.get(0);
			switch (row) {
			case 1:
				modelElement.setLocalProperty("Priority", value);
				break;
			case 2:
				modelElement.setLocalProperty("Condition", value);
				break;
			}
			modelElement.setGuard(modelElement.getLocalProperty("Priority") + ": " + modelElement.getLocalProperty("Condition"));
		}
	}

	@Override
	public void update(List<MObject> elements, IModulePropertyTable table) {
		if (elements.size() == 1 && elements.get(0) instanceof Transition) {
			Transition modelElement = (Transition) elements.get(0);
			table.addProperty("Priority", modelElement.getLocalProperty("Priority"));
			table.addProperty("Condition", modelElement.getLocalProperty("Condition"));
		}
	}
}
