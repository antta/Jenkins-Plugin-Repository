package org.jenkinsci.plugins.RepositoryPlugin;

import java.util.HashMap;

/**
 * Created by cyril on 29/04/14.
 */
public class SharedData {

    private static HashMap<Integer, RepositoryPluginDefinition> repoDefinitions = new HashMap<Integer, RepositoryPluginDefinition>();
    private static HashMap<Integer, RepositoryPluginWrapper> repoWrappers = new HashMap<Integer, RepositoryPluginWrapper>();

    public static RepositoryPluginDefinition getRepoDefinition(int id){
        return SharedData.repoDefinitions.get(id);
    }

    public static RepositoryPluginWrapper getRepoWrapper(int id){
        return SharedData.repoWrappers.get(id);
    }

    public static void addRepoDefinition(int id, RepositoryPluginDefinition repositoryPluginDefinition){
        SharedData.repoDefinitions.put(id,repositoryPluginDefinition);
    }

    public static void addRepoWrapper(int id, RepositoryPluginWrapper repositoryPluginWrapper){
        SharedData.repoWrappers.put(id,repositoryPluginWrapper);
    }

}
