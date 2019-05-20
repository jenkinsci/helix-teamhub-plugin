package com.helixteamhub.plugins.jenkins;

import com.helixteamhub.plugin.jenkins.HelixTeamHubEvent;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class HelixTeamHubEventTest  {

    HelixTeamHubEvent event;

    @Before
    public void setup() {
        String operation = "completed";
        String name = "test";
        String projectId = "platform";
        String repositoryId = "backend";
        String ref = "master";
        String revisionId = "d27327b131054d4a9922546aa5ecb14ba22f45ad";
        String buildUrl = "https://example.com/jenkins/job/test/42";

        event = new HelixTeamHubEvent(operation, name, projectId, repositoryId, ref, revisionId, buildUrl);
    }

    @Test
    public void testToJSON() {
        JSONObject jsonEvent = new JSONObject();
        // "commits": ["d27327b131054d4a9922546aa5ecb14ba22f45ad"]
        jsonEvent.put("commits", JSONArray.fromObject(event.getCommits()));
        // "name": "test"
        jsonEvent.put("name", event.getName());
        // "operation": "completed"
        jsonEvent.put("operation", event.getOperation());
        // "project": "platform"
        jsonEvent.put("project", event.getProject());
        // "ref": "master"
        jsonEvent.put("ref", event.getRef());
        // "repository": "backend"
        jsonEvent.put("repository", event.getRepository());
        // "resources": ["https://example.com/jenkins/job/test/42"]
        jsonEvent.put("resources", JSONArray.fromObject(event.getResources()));
        // "target": "build"
        jsonEvent.put("target", event.getTarget());

        JSONObject wrapper = new JSONObject();
        wrapper.put("event", jsonEvent);

        assertEquals(event.toJSON(), wrapper.toString());
    }

}
