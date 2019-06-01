package org.modelio.junit.impl;

import java.util.ArrayList;
import java.util.List;
import org.modelio.metamodel.uml.statik.Class;
import org.modelio.metamodel.uml.infrastructure.Dependency;
import org.modelio.metamodel.uml.infrastructure.Element;
import org.modelio.metamodel.uml.statik.Package;
import org.modelio.metamodel.visitors.DefaultInfrastructureVisitor;
import org.modelio.metamodel.visitors.DefaultModelVisitor;

public class TestModelUpdater extends DefaultModelVisitor {
	public TestModelUpdater() {
		super(new DefaultInfrastructureVisitor() {
			@Override
			public Object visitElement(Element obj) {
				return Boolean.FALSE;
			}
		});
	}
	
	@Override
	public Object visitPackage(Package obj) {
		List <Class> toDelete = new ArrayList<>();
		
		for (Class c : obj.getOwnedElement(Class.class)) {
			if (c.isStereotyped("JUnit", "JUnit")) {
				boolean hasTestedClass = false;
				for (Dependency dep : c.getDependsOnDependency()) {
					if (dep.isStereotyped("JUnit", "JUnitDependency")) {
						hasTestedClass = true;
					}
				}
				
				if (hasTestedClass == false) {
					toDelete.add(c);
				}
			}
		}
		
		if (toDelete.isEmpty()) {
			return Boolean.FALSE;
		} else {
			for (Class c : toDelete) {
				c.delete();
			}
			return Boolean.TRUE;
		}
	}
}
