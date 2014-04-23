package org.jenkinsci.plugins.RepositoryPlugin.RepositoryPluginWrapper;

import fr.univsavoie.serveurbeta.trap.Trap;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.tasks.BuildWrapper;
import hudson.tasks.BuildWrapperDescriptor;
import hudson.util.FormValidation;

import java.io.IOException;

import org.dom4j.DocumentException;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

public class RepositoryPluginWrapper extends BuildWrapper {
	private String repoAlias;
	private String url;
	private int visibleItemCount;
	private static String absolutePath;
	
	@DataBoundConstructor
	public RepositoryPluginWrapper(String name, String description, String repoAlias,
			String url, int visibleItemCount) {
		this.repoAlias = repoAlias;
		this.url = url;
		this.visibleItemCount = (visibleItemCount == 0 ? 5 : visibleItemCount);
	}
	
	public String getRepoAlias() {
		return repoAlias;
	}

	public void setRepoAlias(String repoAlias) {
		this.repoAlias = repoAlias;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getVisibleItemCount() {
		return visibleItemCount;
	}

	public void setVisibleItemCount(int visibleItemCount) {
		this.visibleItemCount = visibleItemCount;
	}

	@Extension
 	public static final class DescriptorImpl extends BuildWrapperDescriptor {
		@Override
		public String getDisplayName() {
			return "Get job absolute path";
		}
		
		@Override
		public boolean isApplicable(AbstractProject<?, ?> project) {
			try {
				setAbsolutePath(project.getRootDir().getCanonicalPath() + "/zypp");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return true;
		}
		
		public FormValidation doCheckUrl(@QueryParameter String url) throws DocumentException {
			//*
			Trap trap = new Trap("/var/lib/jenkins/jobs/Job Creation Repository/root/");
			if (!trap.isAValidRepository(url)) {
				return FormValidation.error("Repository URL is invalid !");
			}
			//*/
			return FormValidation.ok("Valid URL");
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

	public static String getAbsolutePath() {
		return absolutePath;
	}

	public static void setAbsolutePath(String absolutePath) {
		RepositoryPluginWrapper.absolutePath = absolutePath;
	}
}
