package org.modelio.safetyautomata.command;

import org.modelio.api.module.IModule;
import org.modelio.api.module.context.log.ILogService;
import org.modelio.vcore.smkernel.mapi.MObject;

public class StateCreator {

	public void createState(MObject object, IModule module) {
		ILogService logService = module.getModuleContext().getLogService();
		for(MObject o : object.getCompositionChildren()) {
			logService.info("createState - " + o.getName());
		}
	}

}
