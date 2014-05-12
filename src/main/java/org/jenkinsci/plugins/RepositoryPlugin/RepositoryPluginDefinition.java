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
import org.jdom2.JDOMException;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
	
	@DataBoundConstructor
	public RepositoryPluginDefinition(String name, String description, String repoAlias, String url, int visibleItemCount) {
		super(repoAlias, url);
		this.multiSelectDelimiter = ",";
        this.repoAlias = repoAlias;
        this.visibleItemCount = (visibleItemCount < 0 ? 5 : visibleItemCount);
        this.url = url;
        this.path = "zypproot";


        File folder = new File(path);
        trap = new Trap(path,false);
        folder.mkdir();

        trap.addRepository(repoAlias, url);
        try {
            trap.refreshRepo(repoAlias);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JDOMException e) {
            e.printStackTrace();
        }
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

        public FormValidation doCheckUrl(@QueryParameter String url) throws DocumentException, IOException {
            RepositoryPluginDefinition.trap = RepositoryPluginDefinition.getTrap();
            if (!trap.isAValidRepository(url)) {
                return FormValidation.error("Repository URL is invalid !");
            }
            return FormValidation.ok();
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
        Trap trap = new Trap(path,false);
        try {
            trap.refreshRepo(repoAlias);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JDOMException e) {
            e.printStackTrace();
        }
        File file = new File(path+"log.txt");
        try {
            FileWriter writer = new FileWriter(file);
            writer.write(System.currentTimeMillis() + " getPackages list ! repoAlias : " + repoAlias + " url : " + url + " path : " + path + " packages : " + packages);
        } catch (IOException e) {
            e.printStackTrace();
        }
        setPackages(trap.getPackagesIn(repoAlias));
    }

	public int getVisibleItemCount() {
        return visibleItemCount;
	}

    public String getRepoAlias() {
        return repoAlias;
    }

    public String getUrl() {
        return url;
    }

    public static Trap getTrap() {
        if (trap == null)
            trap = new Trap("tmpZyppRoot/",false);
        return trap;
    }

}