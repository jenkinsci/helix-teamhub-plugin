package com.helixteamhub.plugin.jenkins;

import net.sf.json.JSONObject;

public class HelixTeamHubEvent {

    private String target = "build";
    private String operation;
    private String name;
    private String project;
    private String repository;
    private String ref;
    private String[] commits;
    private String[] resources;

    public HelixTeamHubEvent(String operation, String name, String projectId, String repositoryId, String ref, String revisionId, String buildUrl) {
        this.name = name;
        this.project = projectId;
        this.repository = repositoryId;
        this.ref = ref;
        this.operation = operation;
        this.commits = new String[]{revisionId};
        this.resources = new String[]{buildUrl};
    }

    public String toJSON() {
        JSONObject json = new JSONObject();
        json.put("event", JSONObject.fromObject(this));
        return json.toString();
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getRepository() {
        return repository;
    }

    public void setRepository(String repository) {
        this.repository = repository;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String[] getCommits() {
        return commits.clone();
    }

    public void setCommits(String[] commit) {
        this.commits = commit.clone();
    }

    public String[] getResources() {
        return resources.clone();
    }

    public void setResources(String[] resources) {
        this.resources = resources.clone();
    }

}
