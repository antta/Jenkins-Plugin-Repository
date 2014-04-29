package org.jenkinsci.plugins.RepositoryPlugin;

import fr.univsavoie.serveurbeta.trap.Trap;
import hudson.Extension;
import hudson.model.ParameterValue;
import hudson.model.ParameterDefinition;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.jenkinsci.plugins.RepositoryPlugin.RepositoryPluginWrapper;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

public class RepositoryPluginDefinition extends ParameterDefinition {
    private static int nbInstance = 0;
	private static final long serialVersionUID = 750881539978939359L;
	private String multiSelectDelimiter;
    private String packages;
    private int idInstance = nbInstance++;
	private int visibleItemCount;
	
	@DataBoundConstructor
	public RepositoryPluginDefinition(String name, String description) {
		super(name, description);
		this.multiSelectDelimiter = ",";
        SharedData.addRepoDefinition(this.idInstance, this);
	}

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
	
	public String getMultiSelectDelimiter() {
		return this.multiSelectDelimiter;
	}
	
	public void setMultiSelectDelimiter(final String multiSelectDelimiter) {
		this.multiSelectDelimiter = multiSelectDelimiter;
	}
	
	@Extension
	public static class DescriptorImpl extends ParameterDescriptor {
		@Override
		public String getDisplayName() {
			return "Repository Parameter";
		}
	}
	
	public String getPackages() {
        if (packages == null)
            getPackageList();
        return packages;
	}

    public void setPackages(String packages) {
        this.packages = packages;
    }
    public void getPackageList() {
        String repo = SharedData.getRepoWrapper(this.idInstance).getRepoAlias();
        Trap trap = RepositoryPluginWrapper.getTrap();
        trap.refreshRepo(repo);
        setVisibleItemCount(SharedData.getRepoDefinition(this.idInstance).visibleItemCount);
        setPackages(trap.getPackagesIn(repo));
    }

	public int getVisibleItemCount() {
        return visibleItemCount;
	}

	public void setVisibleItemCount(int visibleItemCount) {
		this.visibleItemCount = visibleItemCount;
	}

}