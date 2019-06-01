package org.modelio.junit.propertypage;

import java.util.List;

import org.modelio.api.module.IModule;
import org.modelio.api.module.propertiesPage.AbstractModulePropertyPage;
import org.modelio.api.module.propertiesPage.IModulePropertyTable;
import org.modelio.metamodel.uml.infrastructure.ModelElement;
import org.modelio.vcore.smkernel.mapi.MObject;

public class JUnitPropertyPage extends AbstractModulePropertyPage {

	public JUnitPropertyPage(IModule module, String name, String label, String bitmap) {
		super(module, name, label, bitmap);
	}

	@Override
	public void changeProperty(List<MObject> elements, int row, String value) {
		if (elements.size() == 1 && elements.get(0) instanceof ModelElement) {
			ModelElement modelElement = (ModelElement) elements.get(0);
			switch (row) {
			case 1:
				modelElement.setName(value);
				break;
			}
		}
	}

	@Override
	public void update(List<MObject> elements, IModulePropertyTable table) {
		if (elements.size() == 1 && elements.get(0) instanceof ModelElement) {
			ModelElement modelElement = (ModelElement) elements.get(0);
			table.addProperty("Name", modelElement.getName());
		}
	}

}
