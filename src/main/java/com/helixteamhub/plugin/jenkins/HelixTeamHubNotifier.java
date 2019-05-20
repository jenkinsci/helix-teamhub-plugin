package com.helixteamhub.plugin.jenkins;

import hudson.EnvVars;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.scm.SCM;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Notifier;
import hudson.tasks.Publisher;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import java.io.IOException;
import java.io.PrintStream;

public class HelixTeamHubNotifier extends Notifier {

    private static final String SCM_GIT = "hudson.plugins.git.GitSCM";
    private static final String SCM_SUBVERSION = "hudson.scm.SubversionSCM";
    private static final String SCM_MERCURIAL = "hudson.plugins.mercurial.MercurialSCM";

    private final String accountKey;
    private final String projectId;
    private final String repositoryId;

    @DataBoundConstructor
    public HelixTeamHubNotifier(String accountKey, String projectId, String repositoryId) {
        this.accountKey = accountKey;
        this.projectId = projectId;
        this.repositoryId = repositoryId;
    }

    public String getAccountKey() {
        return accountKey;
    }

    public String getRepositoryId() {
        return repositoryId;
    }

    public String getProjectId() {
        return projectId;
    }

    public boolean projectIdAndRepositoryIdProvided() {
        return projectId != null && !projectId.isEmpty() && repositoryId != null && !repositoryId.isEmpty();
    }

    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }

    @Override
    public boolean needsToRunAfterFinalized() {
        return true;
    }

    @Override
    public boolean perform(AbstractBuild build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
        notifyHelixTeamHub(build, listener);

        return true;
    }

    private EnvVars getEnvironment(AbstractBuild build, BuildListener listener) {
        EnvVars environment = null;

        try {
            environment = build.getEnvironment(listener);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return environment;
    }

    private String getRevisionId(SCM scm, EnvVars environment) {
        String id;

        switch (scm.getType()) {
            case SCM_GIT:
                id = environment.get("GIT_COMMIT");
                break;
            case SCM_SUBVERSION:
                id = environment.get("SVN_REVISION");
                break;
            case SCM_MERCURIAL:
                id = environment.get("MERCURIAL_REVISION");
                break;
            default:
                id = "";
        }

        return id;
    }

    private String getBuildUrl(EnvVars environment) {
        return environment.get("BUILD_URL");
    }

    private String getOperation(AbstractBuild build) {
        return build.getResult() == Result.SUCCESS ? "completed" : "failed";
    }

    private String getJobName(AbstractBuild build) {
        return build.getProject().getDisplayName();
    }

    private String getRef(SCM scm, EnvVars environment) {
        return scm.getType().equals(SCM_GIT) ? environment.get("GIT_BRANCH").replace("origin/", "") : "";
    }

    private String getRepositoryURL(SCM scm, EnvVars environment) {
        String repositoryURL;

        switch (scm.getType()) {
            case SCM_GIT:
                repositoryURL = environment.get("GIT_URL");
                break;
            case SCM_SUBVERSION:
                repositoryURL = environment.get("SVN_URL");
                break;
            case SCM_MERCURIAL:
                repositoryURL = environment.get("MERCURIAL_REPOSITORY_URL");
                break;
            default:
                repositoryURL = "";
        }

        return repositoryURL;
    }

    private HelixTeamHubAPIKeys getApiKeys(HelixTeamHubBuildStepDescriptor descriptor) {
        return new HelixTeamHubAPIKeys(descriptor.getCompanyKey(), getAccountKey());
    }

    public void notifyHelixTeamHub(AbstractBuild build, BuildListener listener) {
        EnvVars environment = getEnvironment(build, listener);
        SCM scm = build.getProject().getScm();

        String operation = getOperation(build);
        String jobName = getJobName(build);
        String ref = getRef(scm, environment);
        String revisionId = getRevisionId(scm, environment);
        String buildUrl = getBuildUrl(environment);
        String projectId = getProjectId();
        String repositoryId = getRepositoryId();

        if (!projectIdAndRepositoryIdProvided()) {
            String repositoryURL = getRepositoryURL(scm, environment);
            HelixTeamHubRepository repository;

            try {
                repository = new HelixTeamHubRepository(repositoryURL);
                projectId = repository.getProjectId();
                repositoryId = repository.getId();
            } catch (HelixTeamHubURLException ex) {
                logError(listener, "The configured repository URL is not a Helix TeamHub URL.", ex);
                return;
            }
        }

        HelixTeamHubAPI api = new HelixTeamHubAPI(getDescriptor().getHostname(), getApiKeys(getDescriptor()));
        HelixTeamHubEvent event = new HelixTeamHubEvent(operation, jobName, projectId, repositoryId, ref, revisionId, buildUrl);

        try {
            api.create("events", event.toJSON());
        } catch (HelixTeamHubException ex) {
            logError(listener, String.format("Failed to create event.%nEvent: %s", event.toJSON()), ex);
        }
    }

    private void logError(BuildListener listener, String message, Exception ex) {
        PrintStream logger = listener.getLogger();
        logger.println(String.format("Helix TeamHub: %s", message));
        logger.println(String.format("Helix TeamHub: %s", ex.getMessage()));
    }

    @Override
    public HelixTeamHubBuildStepDescriptor getDescriptor() {
        return (HelixTeamHubBuildStepDescriptor) super.getDescriptor();
    }

    @Extension
    public static final class HelixTeamHubBuildStepDescriptor extends BuildStepDescriptor<Publisher> {

        private String hostname = "https://helixteamhub.cloud";
        private String companyKey = "";

        public HelixTeamHubBuildStepDescriptor() {
            load();
        }

        public String getHostname() {
            return hostname;
        }

        public String getCompanyKey() {
            return companyKey;
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return "Helix TeamHub Notification";
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject json) throws FormException {
            this.hostname = json.getString("hostname");
            this.companyKey = json.getString("companyKey");

            save();

            return super.configure(req, json);
        }

    }

}
