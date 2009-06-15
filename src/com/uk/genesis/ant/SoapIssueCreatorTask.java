package com.uk.genesis.ant;


import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import com.voca.jira.soap.JiraSoapService;
import com.voca.jira.soap.JiraSoapServiceServiceLocator;
import com.voca.jira.soap.RemoteAuthenticationException;
import com.voca.jira.soap.RemoteComment;
import com.voca.jira.soap.RemoteCustomFieldValue;
import com.voca.jira.soap.RemoteIssue;
import com.voca.jira.soap.RemoteIssueType;
import com.voca.jira.soap.RemoteProject;
import com.voca.jira.soap.RemoteComponent;
import com.voca.jira.soap.RemoteValidationException;
public class SoapIssueCreatorTask  extends Task{

	private static final String TEST_PHASE_FEILD_ID = "customfield_10015";
	private String username;
	private String password;
	private String key;
	private String summary;
	private String description;
	
	private int priority;
	
	private String issueTypeKey ;
	
	
	@Override
	public void execute(){
		JiraSoapServiceServiceLocator jiraSoapServiceServiceLocator = new JiraSoapServiceServiceLocator();
		JiraSoapService soapService;
		String token ;
		RemoteIssueType[] issueTypes = {}; 
			try {
		
			soapService = jiraSoapServiceServiceLocator.getJirasoapserviceV2();
			token = soapService.login(username, password);
			RemoteProject jiraProject = soapService.getProjectByKey(token, key);
			RemoteComponent component = new RemoteComponent();
			component.setId("10242");

			

		
			issueTypes = soapService.getIssueTypesForProject(token, jiraProject.getId());
			
			RemoteIssue remoteIssue = new RemoteIssue();
			remoteIssue.setSummary(summary);
			remoteIssue.setDescription(description);
			remoteIssue.setPriority("1");
			remoteIssue.setType(issueTypeKey);
			remoteIssue.setProject(jiraProject.getKey());
			remoteIssue.setComponents(new RemoteComponent[]{component});

			

			RemoteCustomFieldValue testPhase = new RemoteCustomFieldValue();
			testPhase.setCustomfieldId(TEST_PHASE_FEILD_ID);
			testPhase.setValues(new String[] {"Development"}); // dummy
			RemoteCustomFieldValue[] customFields = new RemoteCustomFieldValue[] { testPhase };
			remoteIssue.setCustomFieldValues(customFields);
			
			RemoteIssue createdIssue = soapService.createIssue(token, remoteIssue );
			log("Created Jira issue " + createdIssue.getKey());			
			
		}
		catch (RemoteAuthenticationException e){
			throw new BuildException("Authentication Error error: " + e.getMessage() );
		}
		catch (RemoteValidationException e){
			throw new BuildException("Validation error: " + e.getMessage() +  " Valid types: " + listIssueTypes(issueTypes, e));
		}
		catch (Exception e) {
			throw new BuildException("Issue creation failed: " + e.getMessage(),e);
		} 
		
	}


	private String listIssueTypes(RemoteIssueType[] issueTypes,
			RemoteValidationException e) {
		StringBuilder sb = new StringBuilder();
		for(RemoteIssueType type: issueTypes){
			sb.append(String.format("%s %s\n", type.getId(), type.getName()));
		}
		return sb.toString();
		
	}


	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public void setDescription(String description) {
		this.description = description;
	}
	
	
	
	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public void setIssueType(String issueTypeKey) {
		this.issueTypeKey = issueTypeKey;
	}

	public static void main(String[] args){
		SoapIssueCreatorTask it = new SoapIssueCreatorTask();
		it.setUsername("mike.mochan");
		it.setPassword("mike.mochan");
		it.setKey("BGCINF");
		it.setSummary("Automated Genesis  SOAP  API to Jira");
		it.setDescription("Genesis Build System ticket - PLEASE IGNORE");
		it.setPriority(4);
		it.setIssueType("7"); // Development Defect
		it.execute();	
		
		
	}

	public String getIssueType() {
		return issueTypeKey;
	}

}
