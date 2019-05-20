package com.helixteamhub.plugin.jenkins;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HelixTeamHubRepository {

    private static final String PATH_PATTERN = ".+/projects/([^/]+)/repositories/(?:mercurial|git|subversion)/(.+?)/?$";
    private static final String GCONN_PATH_PATTERN = ".+(?::|/)(.+?)/(.+?)(?:/|\\.git)?$";

    private String projectId;
    private String id;

    public HelixTeamHubRepository(String projectId, String id) {
        this.projectId = projectId;
        this.id = id;
    }

    public HelixTeamHubRepository(String repositoryURL) throws HelixTeamHubURLException {
        Pattern pattern = Pattern.compile(PATH_PATTERN);
        Pattern gconn_pattern = Pattern.compile(GCONN_PATH_PATTERN);
        Matcher matcher = pattern.matcher(repositoryURL);
        Matcher gconn_matcher = gconn_pattern.matcher(repositoryURL);

        if (matcher.matches()) {
            this.projectId = matcher.group(1);
            this.id = matcher.group(2);
        } else if (gconn_matcher.matches()) {
            this.projectId = gconn_matcher.group(1);
            this.id = gconn_matcher.group(2);
        } else {
            throw new HelixTeamHubURLException("The URL doesn't appear to be a Helix TeamHub URL.");
        }
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
