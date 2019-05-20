package com.helixteamhub.plugins.jenkins;

import com.helixteamhub.plugin.jenkins.HelixTeamHubRepository;
import com.helixteamhub.plugin.jenkins.HelixTeamHubURLException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.net.MalformedURLException;

import static org.junit.Assert.assertEquals;

public class HelixTeamHubRepositoryTest {

    HelixTeamHubRepository repository;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void throwsHelixTeamHubURLExceptionForUnsupportedURL() throws HelixTeamHubURLException {
        thrown.expect(HelixTeamHubURLException.class);
        thrown.expectMessage("The URL doesn't appear to be a Helix TeamHub URL.");

        new HelixTeamHubRepository("hth@helixteamhub.com:foo");
    }

    @Test
    public void acceptsGitHTTPURL() throws HelixTeamHubURLException {
        repository = new HelixTeamHubRepository("https://hth@helixteamhub.com/foo/projects/bar/repositories/git/baz");

        assertEquals("bar", repository.getProjectId());
        assertEquals("baz", repository.getId());
    }

    @Test
    public void acceptsHelixGitHTTPSURL() throws HelixTeamHubURLException {
        repository = new HelixTeamHubRepository("https://hth@helixteamhub.com/foo/bar");

        assertEquals("foo", repository.getProjectId());
        assertEquals("bar", repository.getId());
    }

    @Test
    public void acceptsCustomHelixGitHTTPSURL() throws HelixTeamHubURLException {
        repository = new HelixTeamHubRepository("https://hth@helixteamhub.com/some/path/1/2/3/foo/bar.git");

        assertEquals("foo", repository.getProjectId());
        assertEquals("bar", repository.getId());
    }

    @Test
    public void acceptsSubversionHTTPURL() throws HelixTeamHubURLException {
        repository = new HelixTeamHubRepository("https://hth@helixteamhub.com/foo/projects/bar/repositories/subversion/baz");

        assertEquals("bar", repository.getProjectId());
        assertEquals("baz", repository.getId());
    }

    @Test
    public void acceptsMercurialHTTPURL() throws HelixTeamHubURLException {
        repository = new HelixTeamHubRepository("https://hth@helixteamhub.com/foo/projects/bar/repositories/mercurial/baz");

        assertEquals("bar", repository.getProjectId());
        assertEquals("baz", repository.getId());
    }

    @Test
    public void acceptsGitSSHURL() throws HelixTeamHubURLException {
        repository = new HelixTeamHubRepository("hth@helixteamhub.com:foo/projects/bar/repositories/git/baz");

        assertEquals("bar", repository.getProjectId());
        assertEquals("baz", repository.getId());
    }

    @Test
    public void acceptsHelixGitSSHURL() throws HelixTeamHubURLException {
        repository = new HelixTeamHubRepository("hth@helixteamhub.com:foo/bar");

        assertEquals("foo", repository.getProjectId());
        assertEquals("bar", repository.getId());
    }

    @Test
    public void acceptsCustomHelixGitSSHURL() throws HelixTeamHubURLException {
        repository = new HelixTeamHubRepository("hth@helixteamhub.com:some/path/1/2/3/foo/bar.git");

        assertEquals("foo", repository.getProjectId());
        assertEquals("bar", repository.getId());
    }

    @Test
    public void acceptsSubversionSSHURL() throws HelixTeamHubURLException {
        repository = new HelixTeamHubRepository("svn+ssh://hth@helixteamhub.com/foo/projects/bar/repositories/subversion/baz");

        assertEquals("bar", repository.getProjectId());
        assertEquals("baz", repository.getId());
    }

    @Test
    public void acceptsMercurialSSHURL() throws HelixTeamHubURLException {
        repository = new HelixTeamHubRepository("ssh://hth@helixteamhub.com/foo/projects/bar/repositories/mercurial/baz");

        assertEquals("bar", repository.getProjectId());
        assertEquals("baz", repository.getId());
    }

    @Test
    public void acceptsSlashesInRepository() throws HelixTeamHubURLException {
        repository = new HelixTeamHubRepository("hth@helixteamhub.com:foo/projects/bar/repositories/git/path/to/repo");

        assertEquals("bar", repository.getProjectId());
        assertEquals("path/to/repo", repository.getId());
    }

    @Test
    public void ignoresEndingSlash() throws HelixTeamHubURLException {
        repository = new HelixTeamHubRepository("hth@helixteamhub.com:foo/projects/bar/repositories/git/path/to/repo/");

        assertEquals("bar", repository.getProjectId());
        assertEquals("path/to/repo", repository.getId());
    }
}
