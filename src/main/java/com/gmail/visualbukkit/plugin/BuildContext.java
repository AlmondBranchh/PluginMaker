package com.gmail.visualbukkit.plugin;

import org.jboss.forge.roaster.model.source.JavaClassSource;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class BuildContext {

    private Set<String> mavenRepositories = new HashSet<>();
    private Set<String> mavenDependencies = new HashSet<>();
    private Set<String> utilClasses = new HashSet<>();
    private Set<String> utilMethods = new HashSet<>();
    private JavaClassSource mainClass;

    public BuildContext(JavaClassSource mainClass) {
        this.mainClass = mainClass;
    }

    public void addPluginModules(PluginModule... modules) {
        for (PluginModule module : modules) {
            module.prepareBuild(this);
        }
    }

    public void addMavenRepositories(String... repositories) {
        mavenRepositories.addAll(Arrays.asList(repositories));
    }

    public void addMavenDependencies(String... dependencies) {
        mavenDependencies.addAll(Arrays.asList(dependencies));
    }

    public void addUtilClasses(String... classes) {
        utilClasses.addAll(Arrays.asList(classes));
    }

    public void addUtilMethods(String... methods) {
        utilMethods.addAll(Arrays.asList(methods));
    }

    public Set<String> getMavenRepositories() {
        return mavenRepositories;
    }

    public Set<String> getMavenDependencies() {
        return mavenDependencies;
    }

    public Set<String> getUtilClasses() {
        return utilClasses;
    }

    public Set<String> getUtilMethods() {
        return utilMethods;
    }

    public JavaClassSource getMainClass() {
        return mainClass;
    }
}