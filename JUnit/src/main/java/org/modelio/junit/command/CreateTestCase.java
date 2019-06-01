package org.modelio.junit.command;

import java.util.List;

import org.modelio.api.module.IModule;
import org.modelio.metamodel.uml.statik.Class;
import org.modelio.api.module.command.DefaultModuleCommandHandler;
import org.modelio.vcore.smkernel.mapi.MObject;

public class CreateTestCase extends DefaultModuleCommandHandler {
	
	@Override
	public boolean accept(List<MObject> selectedElements, IModule module) {
		if(super.accept(selectedElements, module) == false) {
			return false;
		}
		return selectedElements.size() == 1;
	}

	@Override
	public void actionPerformed(List<MObject> selectedElements, IModule module) {
		Class classToTest = (Class) selectedElements.get(0);
		TestCaseCreator testCaseCreator = new TestCaseCreator();
		testCaseCreator.createTestCase(classToTest, module);
	}
	
}
