package org.jenkinsci.plugins.RepositoryPlugin;

import fr.univsavoie.serveurbeta.trap.Trap;
import hudson.Extension;
import hudson.model.ParameterDefinition;
import hudson.model.ParameterValue;
import hudson.util.FormValidation;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.dom4j.DocumentException;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Add components in Jenkins Job Configuration to add a repository for virtual machines generation.<br/>
 * In Job Configuration page, check GitBucket > This build is parameterized > Repository Parameter.
 */
public class RepositoryPluginDefinition extends ParameterDefinition {
    private static int nbInstance = 0;
    private static final long serialVersionUID = 750881539978939359L;
    private final String repoAlias;
    private final String url;
    private final String path;
    private String multiSelectDelimiter;
    private String packages;
    private int idInstance = nbInstance++;
	private int visibleItemCount;

    private static Trap trap;

    /**
     * This constructor is called when saving new configurations in a job.<br/>
     * Initializes the required repository for installing packages.
     * @param repoAlias The name of the repository.
     * @param url The URL of the repository.
     * @param visibleItemCount The number of packages you will see in the "Build with parameters" page
     *                         before you have to use the scrolling bar.
     */
	@DataBoundConstructor
	public RepositoryPluginDefinition(String repoAlias, String url, int visibleItemCount) {
		super(repoAlias, url);
		this.multiSelectDelimiter = ",";
        this.repoAlias = repoAlias;
        this.visibleItemCount = (visibleItemCount < 0 ? 5 : visibleItemCount);
        this.url = url;
        this.path = "zypproot";

        File folder = new File(path);
        trap = new Trap(path);
        folder.mkdirs();

        trap.refreshRepo(repoAlias);
    }

    /**
     * Return a String or convert an array of String to a single String with comma-separated values.
     * @param request Contains the package list values.
     * @return Don't know what this method is for. Need more information.
     */
	@Override
	public ParameterValue createValue(StaplerRequest request) {
		String[] requestValues = request.getParameterValues(getName());
		if(requestValues == null || requestValues.length == 0) {
			return getDefaultParameterValue();
		}

		String valueStr = getPackages();
		if(valueStr != null) {
			List<String> result = new ArrayList<String>();

			String[] values = valueStr.split(",");
			Set<String> valueSet = new HashSet<String>();
			for(String value: values) {
				valueSet.add(value);
			}

			for(String requestValue: requestValues) {
				if(valueSet.contains(requestValue)) {
					result.add(requestValue);
				}
			}

			return new RepositoryPluginValue(getName(), StringUtils.join(result, getMultiSelectDelimiter()));
		}

		return null;
	}

    /**
     * Return a String or convert an array of String to a single String with comma-separated values.
     * @param req Not used.
     * @param jo The object containing the array or the String.
     * @return Don't know what this method is for. Need more information.
     */
	@Override
	public ParameterValue createValue(StaplerRequest req, JSONObject jo) {
		Object value = jo.get("value");
		String strValue = "";
		if(value instanceof String) {
			strValue = (String)value;
		}
		else if(value instanceof JSONArray) {
			JSONArray jsonValues = (JSONArray)value;
			strValue = StringUtils.join(jsonValues.iterator(), getMultiSelectDelimiter());
		}
		return new RepositoryPluginValue(getName(), strValue);
	}

    /**
     * Gets the token to separate values. Default is "," (comma).
     * @return The current token to separate values.
     */
	public String getMultiSelectDelimiter() {
		return this.multiSelectDelimiter;
	}

    /**
     * Sets the token to separate values. Default is "," (comma).
     * @param multiSelectDelimiter The new token to separate values.
     */
	public void setMultiSelectDelimiter(final String multiSelectDelimiter) {
		this.multiSelectDelimiter = multiSelectDelimiter;
	}

    /**
     * Sets a name to identify what the plugin is for.
     * Also, checks if the currently typed repository already exists.
     */
	@Extension
	public static class DescriptorImpl extends ParameterDescriptor {

        private String currentUrl;
        private String currentAlias;

        /**
         * Gets the name which identifies the plugin feature.
         * @return The name of the plugin feature.
         */
		@Override
		public String getDisplayName() {
			return "Repository Parameter";
		}

        /**
         * Checks if the currently typed repository has already been saved.
         * If it's the case, an error is shown, indicating you should not proceed.
         * @param repoAlias The name of the repository.
         * @return A message indicating if a special case is detected. See error message for information.
         */
        public FormValidation doCheckRepoAlias(@QueryParameter String repoAlias) {
            if(!repoAlias.matches("[a-zA-Z][a-zA-Z0-9_]*"))
                return FormValidation.error("The repository Alias Should only use alphanumerical values and _ and must start with a letter !");
            Trap trap = RepositoryPluginDefinition.getTrap();
            if (currentUrl != null && !currentUrl.equals("")) {
                if (trap.localRepositoryExists(repoAlias) && !trap.localRepositoryExists(repoAlias, currentUrl)) {
                    return FormValidation.error("The Repository already exists with another URL !");
                }
            }
            currentAlias = repoAlias;
            return FormValidation.ok();
        }

        /**
         * Checks if the currently typed repository URL has already been saved.<br/>
         * If it's the case, an error is shown, indicating you should not proceed.
         * @param url The repository URL.
         * @return A message indicating if a special case is detected. See error message for information.
         */
        public FormValidation doCheckUrl(@QueryParameter String url) {
            Trap trap = RepositoryPluginDefinition.getTrap();
            if (!trap.isAValidRepository(url)) {
                return FormValidation.error("Repository URL is invalid !");
            }
            if (currentAlias != null && !currentAlias.equals("")) {
                if (trap.localRepositoryExists(currentAlias) && !trap.localRepositoryExists(currentAlias, url)) {
                    return FormValidation.error("The Repository already exists with another URL !");
                }
            }
            currentUrl = url;
            return FormValidation.ok();
        }
	}

    /**
     * Gets the package list of the repository.
     * @return The package list with comma-separated values.
     */
	public String getPackages() {
        if (packages == null)
            getPackageList();
        return packages;
	}

    /**
     * Sets the package list to show in the "Build with parameters" section.
     * @param packages The package list to show.
     */
    public void setPackages(String packages) {
        this.packages = packages;
    }

    /**
     * Ask TRAP to get the package list from the repository, specified earlier in the process.
     */
    public void getPackageList() {
        Trap trap = new Trap(path,false);
        trap.refreshRepo(repoAlias);
        setPackages(trap.getPackagesIn(repoAlias));
    }

    /**
     * Gets the number of visible packages at the same time in the "Build with parameters" section.
     * @return The number of visible packages.
     */
	public int getVisibleItemCount() {
        return visibleItemCount;
	}

    /**
     * Gets the name of the repository, specified earlier in the process.
     * @return The name of the repository.
     */
    public String getRepoAlias() {
        return repoAlias;
    }

    /**
     * Gets the repository URL, specified earlier in the process.
     * @return The repository URL.
     */
    public String getUrl() {
        return url;
    }

    /**
     * Uses a temporary TRAP to check if the current repository alias and URL are not already saved.
     * @return A Trap object to check current repository alias and URL.
     */
    public static Trap getTrap() {
        if (trap == null)
            trap = new Trap("tmpZyppRoot/");
        return trap;
    }

}