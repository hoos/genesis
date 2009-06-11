package com.uk.genesis.ant;

import org.apache.tools.ant.Project;

import junit.framework.TestCase;

public class DefineTargetsForGenesisActionsTest extends TestCase {
	public void testDefinesCorrectActions() {
		CreateFilterSetUsingObjects task = new CreateFilterSetUsingObjects();
		Project project = new Project();
		task.init();
		task.setProject(project);
		task.setGenesisxml("src/test/etc/genesis.xml");
	}
}
