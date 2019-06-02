package org.modelio.safetyautomata.command;

import java.util.List;

import org.modelio.api.module.IModule;
import org.modelio.api.module.command.DefaultModuleCommandHandler;
import org.modelio.vcore.smkernel.mapi.MObject;

public class CreateState extends DefaultModuleCommandHandler {

	@Override
		public boolean accept(List<MObject> selectedElements, IModule module) {
			if (super.accept(selectedElements, module) == false) {
				return false;
			}
			return selectedElements.size() == 1;
		}
	
	@Override
	public void actionPerformed(List<MObject> selectedElements, IModule module) {
		MObject object = selectedElements.get(0);
		StateCreator stateCreator = new StateCreator();
		stateCreator.createState(object, module);
	}
	
}
