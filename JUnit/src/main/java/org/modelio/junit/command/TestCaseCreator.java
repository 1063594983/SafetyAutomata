package org.modelio.junit.command;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.modelio.api.module.IModule;
import org.modelio.metamodel.mmextensions.infrastructure.ExtensionNotFoundException;
import org.modelio.metamodel.uml.infrastructure.Dependency;
import org.modelio.metamodel.uml.infrastructure.Stereotype;
import org.modelio.metamodel.uml.statik.Class;
import org.modelio.api.modelio.model.IModelingSession;
import org.modelio.api.modelio.model.ITransaction;
import org.modelio.api.modelio.model.IUmlModel;
import org.modelio.metamodel.uml.statik.Package;

public class TestCaseCreator {

	public boolean createTestCase(Class classToTest, IModule module) {
		for (Dependency dep : classToTest.getDependsOnDependency()) {
			if (dep.isStereotyped(module.getName(), "JUnitDependency")) {
				MessageDialog.openError(Display.getCurrent().getActiveShell(), 
						"Error", 
						"Command cannot be applied: class already has a test class");
				return false;
			}
		}
		
		IModelingSession session = module.getModuleContext().getModelingSession();
		try (ITransaction t = session.createTransaction("Create a test case")) {
			String testClassName = classToTest.getName() + "Test";
			Package testCaseParentPackage = getTestCaseParentPackage(classToTest, module);
			
			Class testClass = createTestClass(session, testClassName, testCaseParentPackage);
			
			stereotypeTestCase(testClass, module);
			
			linkTestCase(classToTest, testClass, module);
			
			t.commit();
			return true;
		} catch (Exception e) {
			module.getModuleContext().getLogService().error(e);
			return false;
		}
	}

	private boolean linkTestCase(Class classToTest, Class testClass, IModule module) {
		IModelingSession session = module.getModuleContext().getModelingSession();
		try (ITransaction t = session.createTransaction("Stereotype a test case")) {
			IUmlModel model = session.getModel();
			model.createDependency(testClass, classToTest, module.getName(), "JUnitDependency");
			t.commit();
			return true;
		} catch (ExtensionNotFoundException  e) {
			MessageDialog.openError(Display.getCurrent().getActiveShell(), 
					"Error", 
					"Stereotype JUnitDependency not found, check your installation");
			return false;
		} catch (Exception e) {
			module.getModuleContext().getLogService().error(e);
			return false;
		}
	}

	private boolean stereotypeTestCase(Class testClass, IModule module) {
		IModelingSession session = module.getModuleContext().getModelingSession();
		try (ITransaction t = session.createTransaction("Stereotype a test case")) {
			Stereotype s = session.getMetamodelExtensions().getStereotype(module.getName(), "JUnit", 
					module.getModuleContext().getModelioServices().getMetamodelService().getMetamodel().getMClass(Class.class));
			if(s != null) {
				testClass.getExtension().add(s);
				t.commit();
				return true;
			} else {
				MessageDialog.openError(Display.getCurrent().getActiveShell(),
						"Error",
						"Stereotype JUnit not found, check your installation");
				return false;
			}
		}
	}

	private Class createTestClass(IModelingSession session, String testClassName, Package testCaseParentPackage) {
		IUmlModel model = session.getModel();
		Class testClass = model.createClass(testClassName, testCaseParentPackage);
		return testClass;
	}

	private Package getTestCaseParentPackage(Class classToTest, IModule module) {
		IModelingSession session = module.getModuleContext().getModelingSession();
		try (ITransaction t = session.createTransaction("Create test hierarchy")) {
			Package parent = (Package) classToTest.getOwner();
			
			t.commit();
			return parent;
		} catch (Exception e) {
			module.getModuleContext().getLogService().error(e);
			return null;
		}
	}


}
