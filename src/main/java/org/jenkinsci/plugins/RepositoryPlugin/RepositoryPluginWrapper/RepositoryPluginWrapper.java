package org.jenkinsci.plugins.RepositoryPlugin.RepositoryPluginWrapper;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.tasks.BuildWrapper;
import hudson.tasks.BuildWrapperDescriptor;

import java.io.IOException;

import org.kohsuke.stapler.DataBoundConstructor;

public class RepositoryPluginWrapper extends BuildWrapper {
	private static String absolutePath;
	
	@DataBoundConstructor
	public RepositoryPluginWrapper() {
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
