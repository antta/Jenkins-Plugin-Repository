package org.jenkinsci.plugins.RepositoryWrapper;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Hudson;
import hudson.tasks.BuildWrapper;
import hudson.tasks.BuildWrapperDescriptor;
import hudson.util.FormValidation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import jenkins.model.Jenkins;
import jenkins.model.JenkinsLocationConfiguration;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

public class RepositoryWrapperScript extends BuildWrapper {
	private final String name;
	private final String url;
	private final String description;

	@DataBoundConstructor
	public RepositoryWrapperScript(String name, String url, String description)
			throws FileNotFoundException, UnsupportedEncodingException {
		this.name = name;
		this.url = url;
		this.description = description;
	}

	@Extension
 	public static final class DescriptorImpl extends BuildWrapperDescriptor {
		private AbstractProject<?, ?> project;
		public String getDisplayName() {
			return "Add repository URL";
		}

		@Override
		public boolean isApplicable(AbstractProject<?, ?> project) {
			this.project = project;
			return true;
		}
		
		public FormValidation doCheckUrl(@QueryParameter String url) throws DocumentException {
			try {
				URL u = new URL(url);
			    HttpURLConnection huc = (HttpURLConnection) u.openConnection(); 
			    huc.setRequestMethod("GET"); 
			    huc.connect();
			    if (huc.getResponseCode() == 404) {
			    	return FormValidation.error("Wrong repository URL !");
			    }
			    File folder = new File(project.getRootDir().getCanonicalPath() + "/zypp");
			    folder.mkdir();
			    return FormValidation.ok();
			}
			catch (IOException e) {
				return FormValidation.error("Repository URL is malformed");
			}
		}
	}
	
	@SuppressWarnings("rawtypes")
	public Environment setUp(AbstractBuild build, Launcher launcher, BuildListener listener)
			throws IOException, InterruptedException {
		return new Environment() {
			@Override
			public boolean tearDown(AbstractBuild build, BuildListener listener)
					throws IOException, InterruptedException {
				return true;
			}
		};
	}
            
	public String getUrl() {
		return url;
	}

	public String getDescription() {
		return description;
	}
	
	public String getName() {
		return name;
	}
}
