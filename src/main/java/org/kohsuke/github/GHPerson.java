package org.kohsuke.github;

import com.infradna.tool.bridge_method_injector.WithBridgeMethods;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

// TODO: Auto-generated Javadoc
/**
 * Common part of {@link GHUser} and {@link GHOrganization}.
 *
 * @author Kohsuke Kawaguchi
 */
public abstract class GHPerson extends GHObject {

    /** The public gists. */
    protected int followers, following, publicRepos, publicGists;

    /** The html url. */
    protected String htmlUrl;

    /** The twitter username. */
    // other fields (that only show up in full data)
    protected String location, blog, email, bio, name, company, type, twitterUsername;

    /** The avatar url. */
    // core data fields that exist even for "small" user data (such as the user info in pull request)
    protected String login, avatarUrl;

    /** The hireable. */
    protected boolean siteAdmin, hireable;

    /** The total private repos. */
    // other fields (that only show up in full data) that require privileged scope
    protected Integer totalPrivateRepos;

    /**
     * Create default GHPerson instance
     */
    public GHPerson() {
    }

    /**
     * Returns a string of the avatar image URL.
     *
     * @return the avatar url
     */
    public String getAvatarUrl() {
        return avatarUrl;
    }

    /**
     * Gets the blog URL of this user.
     *
     * @return the blog
     * @throws IOException
     *             the io exception
     */
    public String getBlog() throws IOException {
        populate();
        return blog;
    }

    /**
     * Gets the company name of this user, like "Sun Microsystems, Inc."
     *
     * @return the company
     * @throws IOException
     *             the io exception
     */
    public String getCompany() throws IOException {
        populate();
        return company;
    }

    /**
     * Gets the created at.
     *
     * @return the created at
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @WithBridgeMethods(value = Date.class, adapterMethod = "instantToDate")
    public Instant getCreatedAt() throws IOException {
        populate();
        return super.getCreatedAt();
    }

    /**
     * Gets the e-mail address of the user.
     *
     * @return the email
     * @throws IOException
     *             the io exception
     */
    public String getEmail() throws IOException {
        populate();
        return email;
    }

    /**
     * Gets followers count.
     *
     * @return the followers count
     * @throws IOException
     *             the io exception
     */
    public int getFollowersCount() throws IOException {
        populate();
        return followers;
    }

    /**
     * Gets following count.
     *
     * @return the following count
     * @throws IOException
     *             the io exception
     */
    public int getFollowingCount() throws IOException {
        populate();
        return following;
    }

    /**
     * Gets the html url.
     *
     * @return the html url
     */
    public URL getHtmlUrl() {
        return GitHubClient.parseURL(htmlUrl);
    }

    /**
     * Gets the location of this user, like "Santa Clara, California".
     *
     * @return the location
     * @throws IOException
     *             the io exception
     */
    public String getLocation() throws IOException {
        populate();
        return location;
    }

    /**
     * Gets the login ID of this user, like 'kohsuke'.
     *
     * @return the login
     */
    public String getLogin() {
        return login;
    }

    /**
     * Gets the human-readable name of the user, like "Kohsuke Kawaguchi".
     *
     * @return the name
     * @throws IOException
     *             the io exception
     */
    public String getName() throws IOException {
        populate();
        return name;
    }

    /**
     * Gets public gist count.
     *
     * @return the public gist count
     * @throws IOException
     *             the io exception
     */
    public int getPublicGistCount() throws IOException {
        populate();
        return publicGists;
    }

    /**
     * Gets public repo count.
     *
     * @return the public repo count
     * @throws IOException
     *             the io exception
     */
    public int getPublicRepoCount() throws IOException {
        populate();
        return publicRepos;
    }

    /**
     * Gets the public repositories this user owns.
     *
     * <p>
     * To list your own repositories, including private repositories, use {@link GHMyself#listRepositories()}
     *
     * @return the repositories
     */
    public synchronized Map<String, GHRepository> getRepositories() {
        Map<String, GHRepository> repositories = new TreeMap<String, GHRepository>();
        for (GHRepository r : listRepositories().withPageSize(100)) {
            repositories.put(r.getName(), r);
        }
        return Collections.unmodifiableMap(repositories);
    }

    /**
     * Gets repository.
     *
     * @param name
     *            the name
     * @return null if the repository was not found
     * @throws IOException
     *             the io exception
     */
    public GHRepository getRepository(String name) throws IOException {
        try {
            return GHRepository.read(root(), login, name);
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    /**
     * Gets total private repo count.
     *
     * @return the total private repo count
     * @throws IOException
     *             the io exception
     */
    public Optional<Integer> getTotalPrivateRepoCount() throws IOException {
        populate();
        return Optional.ofNullable(totalPrivateRepos);
    }

    /**
     * Gets the Twitter Username of this user, like "GitHub".
     *
     * @return the Twitter username
     * @throws IOException
     *             the io exception
     */
    public String getTwitterUsername() throws IOException {
        populate();
        return twitterUsername;
    }

    /**
     * Gets the type. This is either "User" or "Organization".
     *
     * @return the type
     * @throws IOException
     *             the io exception
     */
    public String getType() throws IOException {
        if (type == null) {
            populate();
        }
        return type;
    }

    /**
     * Gets the updated at.
     *
     * @return the updated at
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @WithBridgeMethods(value = Date.class, adapterMethod = "instantToDate")
    public Instant getUpdatedAt() throws IOException {
        populate();
        return super.getUpdatedAt();
    }

    /**
     * Checks if this user or organization has a GitHub Sponsors listing.
     *
     * @return true if there is a sponsors listing
     * @throws IOException
     *             the io exception
     */
    @BetaApi
    public boolean hasSponsorsListing() throws IOException {
        return GHSponsors.hasSponsorsListing(this);
    }

    /**
     * Gets the siteAdmin field.
     *
     * @return the siteAdmin field
     * @throws IOException
     *             the io exception
     */
    public boolean isSiteAdmin() throws IOException {
        populate();
        return siteAdmin;
    }

    /**
     * Checks if the given account is currently sponsoring this user or organization.
     *
     * @param sponsorLogin
     *            the login of the potential sponsor
     * @return true if the account has an active sponsorship of this user or organization
     * @throws IOException
     *             the io exception
     */
    @BetaApi
    public boolean isSponsoredBy(String sponsorLogin) throws IOException {
        return GHSponsors.isSponsoredBy(this, sponsorLogin);
    }

    /**
     * Checks if this user or organization currently sponsors the given account.
     *
     * @param sponsorableLogin
     *            the login of the user or organization that may be sponsored
     * @return true if this user or organization has an active sponsorship of the account
     * @throws IOException
     *             the io exception
     */
    @BetaApi
    public boolean isSponsoring(String sponsorableLogin) throws IOException {
        return GHSponsors.isSponsoring(this, sponsorableLogin);
    }

    /**
     * Lists events for an organization or an user.
     *
     * @return the paged iterable
     * @throws IOException
     *             the io exception
     */
    public abstract PagedIterable<GHEventInfo> listEvents() throws IOException;

    /**
     * List all the repositories using a default of 30 items page size.
     * <p>
     * Unlike {@link #getRepositories()}, this does not wait until all the repositories are returned.
     *
     * @return the paged iterable
     */
    public PagedIterable<GHRepository> listRepositories() {
        return root().createRequest()
                .withUrlPath("/users/" + login + "/repos")
                .toIterable(GHRepository[].class, null)
                .withPageSize(30);
    }

    /**
     * Lists up all the repositories using the specified page size.
     *
     * @param pageSize
     *            size for each page of items returned by GitHub. Maximum page size is 100. Unlike
     *            {@link #getRepositories()}, this does not wait until all the repositories are returned.
     * @return the paged iterable
     * @deprecated Use #listRepositories().withPageSize() instead.
     */
    @Deprecated
    public PagedIterable<GHRepository> listRepositories(final int pageSize) {
        return listRepositories().withPageSize(pageSize);
    }

    /**
     * Lists the active, public sponsorships this user or organization receives.
     *
     * @return an unmodifiable list of active public sponsorships received
     * @throws IOException
     *             the io exception
     */
    @BetaApi
    public List<GHSponsorship> listSponsorshipsAsMaintainer() throws IOException {
        return listSponsorshipsAsMaintainer(true, false);
    }

    /**
     * Lists the sponsorships this user or organization receives.
     *
     * @param activeOnly
     *            if true, only active sponsorships; if false, cancelled sponsorships are included
     * @param includePrivate
     *            if true, private sponsorships are included. Their sponsor is only revealed to the maintainer or an
     *            admin of the organization; GitHub App installation tokens cannot read them at all and the request
     *            fails with "Resource not accessible by integration"
     * @return an unmodifiable list of sponsorships received
     * @throws IOException
     *             the io exception
     */
    @BetaApi
    public List<GHSponsorship> listSponsorshipsAsMaintainer(boolean activeOnly, boolean includePrivate)
            throws IOException {
        return GHSponsors.listSponsorshipsAsMaintainer(this, activeOnly, includePrivate);
    }

    /**
     * Lists the active sponsorships this user or organization gives.
     *
     * @return an unmodifiable list of active sponsorships given
     * @throws IOException
     *             the io exception
     */
    @BetaApi
    public List<GHSponsorship> listSponsorshipsAsSponsor() throws IOException {
        return listSponsorshipsAsSponsor(true);
    }

    /**
     * Lists the sponsorships this user or organization gives.
     *
     * @param activeOnly
     *            if true, only active sponsorships; if false, cancelled sponsorships are included
     * @return an unmodifiable list of sponsorships given
     * @throws IOException
     *             the io exception
     */
    @BetaApi
    public List<GHSponsorship> listSponsorshipsAsSponsor(boolean activeOnly) throws IOException {
        return GHSponsors.listSponsorshipsAsSponsor(this, activeOnly);
    }

    /**
     * Fully populate the data by retrieving missing data.
     * <p>
     * Depending on the original API call where this object is created, it may not contain everything.
     *
     * @throws IOException
     *             the io exception
     */
    protected synchronized void populate() throws IOException {
        if (super.getCreatedAt() != null) {
            return; // already populated
        }
        if (isOffline()) {
            return; // cannot populate, will have to live with what we have
        }
        URL url = getUrl();
        if (url != null) {
            root().createRequest().setRawUrlPath(url.toString()).fetchInto(this);
        }
    }
}
