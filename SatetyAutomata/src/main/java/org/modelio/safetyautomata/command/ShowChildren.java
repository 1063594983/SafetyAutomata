package org.modelio.safetyautomata.command;

import java.util.List;

import org.modelio.api.modelio.diagram.IDiagramService;
import org.modelio.api.modelio.model.IModelingSession;
import org.modelio.api.module.IModule;
import org.modelio.api.module.command.DefaultModuleCommandHandler;
import org.modelio.api.module.context.IModuleContext;
import org.modelio.api.module.context.log.ILogService;
import org.modelio.metamodel.diagrams.StaticDiagram;
import org.modelio.metamodel.uml.infrastructure.Element;
import org.modelio.vcore.smkernel.mapi.MObject;

public class ShowChildren extends DefaultModuleCommandHandler {
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
		for (MObject o : object.getCompositionChildren()) {
			System.out.println(o.getName());
		}
		
		IModuleContext context = module.getModuleContext();
		ILogService logService = context.getLogService();
		IDiagramService diagramService = context.getModelioServices().getDiagramService();
		IModelingSession session = context.getModelingSession();
		
		for (StaticDiagram diagram : session.findByClass(StaticDiagram.class)) {
			logService.info(diagram.getName());
			for(Element e : diagram.getRepresented()) {
				logService.info(e.getName());
			}
		}
	}
}
