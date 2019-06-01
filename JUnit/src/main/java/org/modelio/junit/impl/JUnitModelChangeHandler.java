package org.modelio.junit.impl;

import java.util.ArrayList;
import java.util.List;

import org.modelio.api.modelio.model.IModelingSession;
import org.modelio.api.modelio.model.event.IElementDeletedEvent;
import org.modelio.api.modelio.model.event.IModelChangeEvent;
import org.modelio.api.modelio.model.event.IModelChangeHandler;
import org.modelio.vcore.smkernel.mapi.MObject;
import org.modelio.api.modelio.model.ITransaction;

public class JUnitModelChangeHandler implements IModelChangeHandler {

	@Override
	public void handleModelChange(IModelingSession session, IModelChangeEvent event) {
		TestModelUpdater updater = new TestModelUpdater();
		
		List<MObject> parentsToUpdate = new ArrayList<MObject>();
		
		for (IElementDeletedEvent deletedElement : event.getDeleteEvents()) {
			if (!parentsToUpdate.contains(deletedElement.getOldParent())) {
				parentsToUpdate.add(deletedElement.getOldParent());
			}
		}
		
		try (ITransaction t = session.createTransaction("Update the test hierarchy")) {
			boolean modelUpdated = false;
			for (MObject parent : parentsToUpdate) {
				if(((Boolean) parent.accept(updater)) == true) {
					modelUpdated = true;
				}
			}
			
			if(modelUpdated) {
				t.commit();
			} else {
				t.rollback();
			}
		}
	}

}
