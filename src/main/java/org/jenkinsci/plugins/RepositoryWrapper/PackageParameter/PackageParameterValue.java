package org.jenkinsci.plugins.RepositoryWrapper.PackageParameter;

import org.kohsuke.stapler.DataBoundConstructor;

import hudson.model.StringParameterValue;

public class PackageParameterValue extends StringParameterValue {
	private static final long serialVersionUID = -1991499409680487801L;

	@DataBoundConstructor
	public PackageParameterValue(String name, String value) {
		super(name, value);
	}

}