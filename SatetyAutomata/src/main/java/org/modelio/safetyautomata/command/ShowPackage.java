package org.modelio.safetyautomata.command;

import java.util.List;

import org.modelio.api.modelio.model.IModelingSession;
import org.modelio.api.module.IModule;
import org.modelio.api.module.command.DefaultModuleCommandHandler;
import org.modelio.api.module.context.IModuleContext;
import org.modelio.api.module.context.log.ILogService;
import org.modelio.vcore.smkernel.mapi.MObject;
import org.modelio.metamodel.uml.behavior.commonBehaviors.Behavior;
import org.modelio.metamodel.uml.infrastructure.UmlModelElement;
import org.modelio.metamodel.uml.statik.Package;

public class ShowPackage extends DefaultModuleCommandHandler {

	
	@Override
	public boolean accept(List<MObject> selectedElements, IModule module) {
		return selectedElements.size() == 1;
	}
	
	@Override
	public void actionPerformed(List<MObject> selectedElements, IModule module) {
		Package myPackage = (Package) selectedElements.get(0);
		IModuleContext context = module.getModuleContext();
		IModelingSession session = context.getModelingSession();
		ILogService logService = context.getLogService();
		
		logService.info("analyse Package");
		
		for (UmlModelElement element : myPackage.getMerge()) {
			logService.info("Merge : " + element.getName());
		}
		
		for (UmlModelElement element : myPackage.getReceivingMerge()) {
			logService.info("ReceivingMerge : " + element.getName());
		}
		
		for (UmlModelElement element : myPackage.getPackageImporting()) {
			logService.info("PackageImporting : " + element.getName());
		}
		
		for (UmlModelElement element : myPackage.getRepresenting()) {
			logService.info("Representing : " + element.getName());
		}
		
		for (Behavior behavior : myPackage.getOwnedBehavior()) {
			logService.info("Behavior : " + behavior.getName());
		}
		
		for (UmlModelElement element : myPackage.getOwnedImport()) {
			logService.info("OwnedImport : " + element.getName());
		}
		
		for (UmlModelElement element : myPackage.getOwnedElement()) {
			logService.info("OwnedElement : " + element.getName());
		}
		
		
	}

}
