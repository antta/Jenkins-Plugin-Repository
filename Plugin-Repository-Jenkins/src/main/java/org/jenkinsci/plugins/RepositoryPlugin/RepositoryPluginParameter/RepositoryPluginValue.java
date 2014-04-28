package org.jenkinsci.plugins.RepositoryPlugin.RepositoryPluginParameter;

import org.kohsuke.stapler.DataBoundConstructor;

import hudson.model.StringParameterValue;

public class RepositoryPluginValue extends StringParameterValue {
	private static final long serialVersionUID = -1991499409680487801L;

	@DataBoundConstructor
	public RepositoryPluginValue(String name, String value) {
		super(name, value);
	}

}