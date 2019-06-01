package org.modelio.junit.impl;

import java.util.Map;

import org.modelio.api.modelio.model.IModelingSession;
import org.modelio.api.module.context.IModuleContext;
import org.modelio.api.module.lifecycle.DefaultModuleLifeCycleHandler;
import org.modelio.api.module.lifecycle.ModuleException;
import org.modelio.vbasic.version.Version;

/**
 * Implementation of the IModuleLifeCycleHandler interface.
 * <br>This default implementation may be inherited by the module developers in order to simplify the code writing of the module life cycle handler .
 */
public class JUnitLifeCycleHandler extends DefaultModuleLifeCycleHandler {
	
	private JUnitModelChangeHandler modelChangeHandler = null;

	/**
	 * Constructor.
	 * @param module the Module this life cycle handler is instanciated for.
	 */
	public JUnitLifeCycleHandler(JUnitModule module) {
		super(module);
	}

	/**
	 * @see org.modelio.api.module.DefaultModuleLifeCycleHandler#start()
	 */
	@Override
	public boolean start() throws ModuleException {
		/*
		// get the version of the module
		Version moduleVersion = this.module.getVersion();

		// get the Modelio log service
		ILogService logService = this.module.getModuleContext().getLogService();

		String message = "Start of " + this.module.getName() + " " + moduleVersion;
		logService.info(message);
		*/
		
		IModuleContext context = this.module.getModuleContext();
		IModelingSession session = context.getModelingSession();
		modelChangeHandler = new JUnitModelChangeHandler();
		session.addModelHandler(modelChangeHandler);
		
		return super.start();
	}

	/**
	 * @see org.modelio.api.module.DefaultModuleLifeCycleHandler#stop()
	 */
	@Override
	public void stop() throws ModuleException {
		IModuleContext context = this.module.getModuleContext();
		IModelingSession session = context.getModelingSession();
		session.removeModelHandler(modelChangeHandler);
		modelChangeHandler = null;
		super.stop();
	}

	public static boolean install(String modelioPath, String mdaPath) throws ModuleException {
		return DefaultModuleLifeCycleHandler.install(modelioPath, mdaPath);
	}

	/**
	 * @see org.modelio.api.module.DefaultModuleLifeCycleHandler#select()
	 */
	@Override
	public boolean select() throws ModuleException {
		return super.select();
	}

	/**
	 * @see org.modelio.api.module.DefaultModuleLifeCycleHandler#unselect()
	 */
	@Override
	public void unselect() throws ModuleException {
		super.unselect();
	}

	/**
	 * @see org.modelio.api.module.DefaultModuleLifeCycleHandler#upgrade(org.modelio.api.modelio.Version, java.util.Map)
	 */
	@Override
	public void upgrade(Version oldVersion, Map<String, String> oldParameters) throws ModuleException {
		super.upgrade(oldVersion, oldParameters);
	}
}
