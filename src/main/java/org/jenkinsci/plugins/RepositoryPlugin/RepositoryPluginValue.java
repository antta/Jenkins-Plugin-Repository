package org.jenkinsci.plugins.RepositoryPlugin;

import org.kohsuke.stapler.DataBoundConstructor;

import hudson.model.StringParameterValue;

/**
 * This class has been kept for the plugin to work correctly.<br/>
 * Not sure about its true behavior, but it's used in some methods.
 */
public class RepositoryPluginValue extends StringParameterValue {
	private static final long serialVersionUID = -1991499409680487801L;

	@DataBoundConstructor
	public RepositoryPluginValue(String name, String value) {
		super(name, value);
	}

}