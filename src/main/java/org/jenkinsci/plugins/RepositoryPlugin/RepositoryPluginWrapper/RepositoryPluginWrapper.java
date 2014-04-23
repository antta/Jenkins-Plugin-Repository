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
	public RepositoryPluginWrapper(String repoAlias, String url, int visibleItemCount) {
		this.repoAlias = repoAlias;
		this.url = url;
		this.visibleItemCount = (visibleItemCount == 0 ? 5 : visibleItemCount);
	}
	
	@Extension
 	public static final class DescriptorImpl extends BuildWrapperDescriptor {
		private AbstractProject<?, ?> project;
		
		@Override
		public String getDisplayName() {
			return "Add Repository URL chaine vide";
		}
		
		@Override
		public boolean isApplicable(AbstractProject<?, ?> project) {
			this.project = project;
			return true;
		}
		
		public FormValidation doCheckUrl(@QueryParameter String url) throws DocumentException, IOException {
			//*
			absolutePath = project.getRootDir().getCanonicalPath();
			Trap trap = new Trap("");
			/*
			if (!trap.isAValidRepository(url)) {
				return FormValidation.error("Repository URL is invalid !");
			}
			/*
			File folder = new File(absolutePath + "/zypp");
		    folder.mkdir();
		    //*/
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
		this.visibleItemCount = visibleItemCount;
	}

	public static String getAbsolutePath() {
		return absolutePath;
	}

	public static void setAbsolutePath(String absolutePath) {
		RepositoryPluginWrapper.absolutePath = absolutePath;
	}

}
