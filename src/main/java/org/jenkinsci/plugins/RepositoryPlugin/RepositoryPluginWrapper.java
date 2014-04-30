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
    private static String lastPath;

	@DataBoundConstructor
	public RepositoryPluginWrapper() {
	}

	@Extension
 	public static final class DescriptorImpl extends BuildWrapperDescriptor {
		private AbstractProject<?, ?> project;

		@Override
		public String getDisplayName() {
			return "Set static project path";
		}

		@Override
		public boolean isApplicable(AbstractProject<?, ?> project) {
			this.project = project;
            try {
                lastPath = project.getRootDir().getCanonicalPath();
            } catch (IOException e) {
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

    public static String getLastPath() {
        return lastPath;
    }
}
