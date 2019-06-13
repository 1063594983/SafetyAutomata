package ecnu.safetyautomata.propertypage;

import java.util.List;

import org.modelio.api.module.IModule;
import org.modelio.api.module.propertiesPage.AbstractModulePropertyPage;
import org.modelio.api.module.propertiesPage.IModulePropertyTable;
import org.modelio.metamodel.uml.behavior.stateMachineModel.StateVertex;
import org.modelio.vcore.smkernel.mapi.MObject;

public class StatePage extends AbstractModulePropertyPage{

	public StatePage(IModule module, String name, String label, String bitmap) {
		super(module, name, label, bitmap);
	}

	@Override
	public void changeProperty(List<MObject> elements, int row, String value) {
		if (elements.size() == 1 && elements.get(0) instanceof StateVertex) {
			StateVertex modelElement = (StateVertex) elements.get(0);
			switch (row) {
			case 1:
				modelElement.setLocalProperty("StateName", value);
				break;
			case 2:
				modelElement.setLocalProperty("StateValues", value);
				break;
			}
			String name = modelElement.getLocalProperty("StateName");
			String[] strs = modelElement.getLocalProperty("StateValues").split(" ");
			name += "\n\n";
			for (String str : strs) {
				name += str + "\n";
			}
			modelElement.setName(name);
		}
	}

	@Override
	public void update(List<MObject> elements, IModulePropertyTable table) {
		if (elements.size() == 1 && elements.get(0) instanceof StateVertex) {
			StateVertex modelElement = (StateVertex) elements.get(0);
			table.addProperty("StateName", modelElement.getLocalProperty("StateName"));
			table.addProperty("StateValues", modelElement.getLocalProperty("StateValues"));
		}
	}

}
