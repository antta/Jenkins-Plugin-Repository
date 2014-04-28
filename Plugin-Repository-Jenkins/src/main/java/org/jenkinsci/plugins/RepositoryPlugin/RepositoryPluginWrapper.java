package org.jenkinsci.plugins.RepositoryPlugin;

import fr.univsavoie.serveurbeta.trap.Trap;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.tasks.BuildWrapper;
import hudson.tasks.BuildWrapperDescriptor;
import hudson.util.FormValidation;

import java.io.File;
import java.io.IOException;

import org.dom4j.DocumentException;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

public class RepositoryPluginWrapper extends BuildWrapper {
	private String repoAlias;
	private String url;
	public static int visibleItemCount;
	public static String packageList;
	//private static Trap trap;

	@DataBoundConstructor
	public RepositoryPluginWrapper(String repoAlias, String url, int visibleItemCount) {
		this.repoAlias = repoAlias;
		this.url = url;
		RepositoryPluginWrapper.visibleItemCount = (visibleItemCount == 0 ? 5 : visibleItemCount);
		RepositoryPluginWrapper.packageList = "myjenkins, mysonar, mynexus"; 
		/*
		trap.addRepository(repoAlias, url);
		RepositoryPluginWrapper.packageList = trap.getPackagesIn(repoAlias);
		//*/
	}
	
	@Extension
 	public static final class DescriptorImpl extends BuildWrapperDescriptor {
		private AbstractProject<?, ?> project;
		
		@Override
		public String getDisplayName() {
			return "Add repository URL";
		}
		
		@Override
		public boolean isApplicable(AbstractProject<?, ?> project) {
			this.project = project;
			return true;
		}
		
		public FormValidation doCheckUrl(@QueryParameter String url) throws DocumentException, IOException {
			String absolutePath = project.getRootDir().getCanonicalPath();
			/*
			if (trap == null) {
				trap = new Trap("root/");
			}
			if (!trap.isAValidRepository(url)) {
				return FormValidation.error("Repository URL is invalid !");
			}
			//*/
			File folder = new File(absolutePath + "/zypproot");
		    folder.mkdir();
		    
			return FormValidation.ok();
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
		RepositoryPluginWrapper.visibleItemCount = visibleItemCount;
	}
}
