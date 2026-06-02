package org.kohsuke.github;

import org.junit.Test;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThrows;

/**
 * Tests for the GitHub Sponsors methods of {@link GHPerson}.
 */
public class GHSponsorsTest extends AbstractGitHubWireMockTest {

    /**
     * Create default GHSponsorsTest instance
     */
    public GHSponsorsTest() {
    }

    /**
     * A user with a Sponsors listing reports one.
     *
     * @throws IOException
     *             the io exception
     */
    @Test
    public void hasSponsorsListing() throws IOException {
        GHUser user = gitHub.getUser("sponsor-test-user");
        assertThat(user.hasSponsorsListing(), is(true));
    }

    /**
     * A user reports an account it sponsors.
     *
     * @throws IOException
     *             the io exception
     */
    @Test
    public void isSponsoring() throws IOException {
        GHUser user = gitHub.getUser("sponsor-test-user");
        assertThat(user.isSponsoring("sponsor-test-org"), is(true));
    }

    /**
     * Checking sponsorship of an unknown account fails like a missing REST resource.
     *
     * @throws IOException
     *             the io exception
     */
    @Test
    public void isSponsoringUnknownAccount() throws IOException {
        GHUser user = gitHub.getUser("sponsor-test-user");
        assertThrows(GHFileNotFoundException.class, () -> user.isSponsoring("no-such-login"));
    }

    /**
     * Active public sponsorships received by an organization come back with their tiers.
     *
     * @throws IOException
     *             the io exception
     */
    @Test
    public void listSponsorshipsAsMaintainer() throws IOException {
        GHOrganization org = gitHub.getOrganization("sponsor-test-org");
        List<GHSponsorship> sponsorships = org.listSponsorshipsAsMaintainer();

        assertThat(sponsorships, hasSize(5));

        GHSponsorship first = sponsorships.get(0);
        assertThat(first.getSponsorLogin(), equalTo("tiny-supporter"));
        assertThat(first.getSponsorableLogin(), equalTo("sponsor-test-org"));
        assertThat(first.isActive(), is(true));
        assertThat(first.isOneTimePayment(), is(false));
        assertThat(first.getPrivacyLevel(), equalTo(GHSponsorship.PrivacyLevel.PUBLIC));
        assertThat(first.getCreatedAt(), equalTo(Instant.parse("2026-05-06T09:56:53Z")));

        GHSponsorsTier tier = first.getTier();
        assertThat(tier.getId(), equalTo("ST_kwDOAAAAAc4AAAAB"));
        assertThat(tier.getName(), equalTo("$1 a month"));
        assertThat(tier.getDescription(), equalTo("Say Thanks!"));
        assertThat(tier.getMonthlyPriceInDollars(), equalTo(1));
        assertThat(tier.getMonthlyPriceInCents(), equalTo(100));
        assertThat(tier.isOneTime(), is(false));
        assertThat(tier.isCustomAmount(), is(false));

        GHSponsorship gold = sponsorships.get(2);
        assertThat(gold.getSponsorLogin(), equalTo("gold-patron"));
        assertThat(gold.getTier().getName(), equalTo("$100 a month"));
        assertThat(gold.getTier().getMonthlyPriceInDollars(), equalTo(100));
        assertThat(gold.getTier().getMonthlyPriceInCents(), equalTo(10000));
    }

    /**
     * Sponsorships given by a user name the sponsored accounts.
     *
     * @throws IOException
     *             the io exception
     */
    @Test
    public void listSponsorshipsAsSponsor() throws IOException {
        GHUser user = gitHub.getUser("sponsor-test-user");
        List<GHSponsorship> sponsorships = user.listSponsorshipsAsSponsor();

        assertThat(sponsorships, hasSize(2));

        GHSponsorship monthly = sponsorships.get(0);
        assertThat(monthly.getSponsorLogin(), equalTo("sponsor-test-user"));
        assertThat(monthly.getSponsorableLogin(), equalTo("beloved-maintainer"));
        assertThat(monthly.isActive(), is(true));
        assertThat(monthly.isOneTimePayment(), is(false));
        assertThat(monthly.getTier().getName(), equalTo("$5 a month"));

        GHSponsorship oneTime = sponsorships.get(1);
        assertThat(oneTime.getSponsorLogin(), equalTo("sponsor-test-user"));
        assertThat(oneTime.getSponsorableLogin(), equalTo("sponsor-test-org"));
        assertThat(oneTime.isOneTimePayment(), is(true));
        assertThat(oneTime.getPrivacyLevel(), equalTo(GHSponsorship.PrivacyLevel.PRIVATE));
        assertThat(oneTime.getTier().isOneTime(), is(true));
        assertThat(oneTime.getTier().isCustomAmount(), is(true));
    }

    /**
     * Cancelled and private sponsorships are included on request.
     *
     * @throws IOException
     *             the io exception
     */
    @Test
    public void listSponsorshipsIncludingInactive() throws IOException {
        GHUser user = gitHub.getUser("sponsor-test-user");
        List<GHSponsorship> sponsorships = user.listSponsorshipsAsMaintainer(false, true);

        assertThat(sponsorships, hasSize(4));

        long activeCount = sponsorships.stream().filter(GHSponsorship::isActive).count();
        long inactiveCount = sponsorships.stream().filter(s -> !s.isActive()).count();
        assertThat(activeCount, equalTo(1L));
        assertThat(inactiveCount, equalTo(3L));

        GHSponsorship pastSponsor = sponsorships.get(1);
        assertThat(pastSponsor.getSponsorLogin(), equalTo("past-sponsor"));
        assertThat(pastSponsor.getSponsorableLogin(), equalTo("sponsor-test-user"));
        assertThat(pastSponsor.isActive(), is(false));
        assertThat(pastSponsor.getCreatedAt(), equalTo(Instant.parse("2023-03-10T08:00:00Z")));

        GHSponsorship hidden = sponsorships.get(3);
        assertThat(hidden.getSponsorLogin(), nullValue());
        assertThat(hidden.getSponsorableLogin(), equalTo("sponsor-test-user"));
        assertThat(hidden.getPrivacyLevel(), equalTo(GHSponsorship.PrivacyLevel.PRIVATE));
        assertThat(hidden.getTier(), nullValue());
    }

    /**
     * Sponsorships spanning several pages are fetched completely.
     *
     * @throws IOException
     *             the io exception
     */
    @Test
    public void listSponsorshipsPaginated() throws IOException {
        GHOrganization org = gitHub.getOrganization("sponsor-test-org");
        List<GHSponsorship> sponsorships = org.listSponsorshipsAsMaintainer();

        assertThat(sponsorships, hasSize(3));
        assertThat(sponsorships.get(0).getSponsorLogin(), equalTo("first-page-one"));
        assertThat(sponsorships.get(1).getSponsorLogin(), equalTo("first-page-two"));
        assertThat(sponsorships.get(2).getSponsorLogin(), equalTo("second-page-one"));
    }

    /**
     * A GraphQL error for private sponsorships surfaces as {@link GHGraphQLException}.
     *
     * @throws IOException
     *             the io exception
     */
    @Test
    public void listSponsorshipsPrivateForbidden() throws IOException {
        GHOrganization org = gitHub.getOrganization("sponsor-test-org");
        GHGraphQLException e = assertThrows(GHGraphQLException.class,
                () -> org.listSponsorshipsAsMaintainer(false, true));
        assertThat(e.getErrorMessages(), contains("Resource not accessible by integration"));
    }

    /**
     * An organization reports an account that does not sponsor it.
     *
     * @throws IOException
     *             the io exception
     */
    @Test
    public void organizationIsNotSponsoredBy() throws IOException {
        GHOrganization org = gitHub.getOrganization("sponsor-test-org");
        assertThat(org.isSponsoredBy("some-random-user"), is(false));
    }

    /**
     * An organization reports an account that sponsors it.
     *
     * @throws IOException
     *             the io exception
     */
    @Test
    public void organizationIsSponsoredBy() throws IOException {
        GHOrganization org = gitHub.getOrganization("sponsor-test-org");
        assertThat(org.isSponsoredBy("active-sponsor"), is(true));
    }

    /**
     * A user reports an account that does not sponsor it.
     *
     * @throws IOException
     *             the io exception
     */
    @Test
    public void userIsNotSponsoredBy() throws IOException {
        GHUser user = gitHub.getUser("sponsor-test-user");
        assertThat(user.isSponsoredBy("some-random-user"), is(false));
    }
}
