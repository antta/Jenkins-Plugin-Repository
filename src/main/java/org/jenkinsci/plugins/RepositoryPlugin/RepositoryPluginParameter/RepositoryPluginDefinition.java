package org.jenkinsci.plugins.RepositoryPlugin.RepositoryPluginParameter;

import fr.univsavoie.serveurbeta.trap.Trap;
import hudson.Extension;
import hudson.model.ParameterValue;
import hudson.model.ParameterDefinition;
import hudson.util.FormValidation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.dom4j.DocumentException;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

public class RepositoryPluginDefinition extends ParameterDefinition {
	private static final long serialVersionUID = 750881539978939359L;
	private String repoAlias;
	private String url;
	private String multiSelectDelimiter;
	private int visibleItemCount;
	private String packages;
	
	@DataBoundConstructor
	public RepositoryPluginDefinition(String name, String description, String repoAlias,
			String url, int visibleItemCount) {
		super(name, description);
		this.repoAlias = repoAlias;
		this.url = url;
		this.multiSelectDelimiter = ",";
		this.visibleItemCount = (visibleItemCount == 0 ? 5 : visibleItemCount);
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

	@Extension
	public static class DescriptorImpl extends ParameterDescriptor {
		
		@Override
		public String getDisplayName() {
			return "Repository Parameter";
		}

		public FormValidation doCheckUrl(@QueryParameter String url) throws DocumentException {
			Trap trap = new Trap("/var/lib/jenkins/jobs/Job Creation Repository/root/");
			if (!trap.isAValidRepository(url)) {
				return FormValidation.error("Repository URL is invalid !");
			}
			return FormValidation.ok("Valid URL");
		}

	}
	
	public String getMultiSelectDelimiter() {
		return this.multiSelectDelimiter;
	}
	
	public void setMultiSelectDelimiter(final String multiSelectDelimiter) {
		this.multiSelectDelimiter = multiSelectDelimiter;
	}
	
	public String getPackages() {
		return packages;
	}

	public void setValue(String packages) {
		this.packages = packages;
	}
	
	public int getVisibleItemCount() {
		return visibleItemCount;
	}

	public void setVisibleItemCount(int visibleItemCount) {
		this.visibleItemCount = visibleItemCount;
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

	public void setPackages(String packages) {
		this.packages = packages;
	}

	public void getString() {
		setValue("myjenkins, mysonar, mynexus");
	}
}