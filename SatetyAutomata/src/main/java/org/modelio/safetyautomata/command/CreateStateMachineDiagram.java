package org.modelio.safetyautomata.command;

import java.util.List;

import org.modelio.api.module.IModule;
import org.modelio.api.module.command.DefaultModuleCommandHandler;
import org.modelio.vcore.smkernel.mapi.MObject;

public class CreateStateMachineDiagram extends DefaultModuleCommandHandler{
	
	@Override
	public boolean accept(List<MObject> selectedElements, IModule module) {
		// TODO Auto-generated method stub
		return selectedElements.size() == 1;
	}
	
	@Override
	public void actionPerformed(List<MObject> selectedElements, IModule module) {
		// TODO Auto-generated method stub
		MObject object = selectedElements.get(0);
		Package myPackage = (Package) object;
		myPackage.get
	}
	
}
