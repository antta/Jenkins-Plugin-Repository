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
    private static int nbInstance = 0;
	private String repoAlias;
	private String url;
    private int idInstance = nbInstance++;
	public int visibleItemCount;
	public String packageList;
    private static Trap trap;

	@DataBoundConstructor
	public RepositoryPluginWrapper(String repoAlias, String url, int visibleItemCount) {
		this.repoAlias = repoAlias;
		this.url = url;
		this.visibleItemCount = (visibleItemCount == 0 ? 5 : visibleItemCount);
        SharedData.addRepoWrapper(this.idInstance, this);

		trap.addRepository(repoAlias, url);
        trap.refreshRepo(repoAlias);
		//RepositoryPluginWrapper.packageList = trap.getPackagesIn(repoAlias);

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
			//*
            RepositoryPluginWrapper.trap = RepositoryPluginWrapper.getTrap();

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
		this.visibleItemCount = visibleItemCount;
	}

    public static Trap getTrap() {
        if (trap == null) {
            trap = new Trap("root/", false);
        }
        return trap;
    }
}
