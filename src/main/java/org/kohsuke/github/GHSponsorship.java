package org.kohsuke.github;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.time.Instant;
import java.util.Map;

/**
 * A GitHub Sponsors sponsorship.
 *
 * @see <a href="https://docs.github.com/en/graphql/reference/objects#sponsorship">Sponsorship</a>
 */
@BetaApi
@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
@SuppressFBWarnings(value = { "UWF_UNWRITTEN_FIELD", "NP_UNWRITTEN_FIELD" }, justification = "JSON API")
public class GHSponsorship {

    /**
     * Who can see a sponsorship.
     */
    public enum PrivacyLevel {
        /** Visible only to the sponsor and the maintainer. */
        PRIVATE,
        /** Visible to everyone. */
        PUBLIC
    }
    private String createdAt;
    private boolean isActive;
    private boolean isOneTimePayment;
    private PrivacyLevel privacyLevel;
    private Map<String, String> sponsorEntity;
    private Map<String, String> sponsorable;

    private GHSponsorsTier tier;

    GHSponsorship() {
    }

    /**
     * Gets the creation date of this sponsorship.
     *
     * @return the creation date
     */
    public Instant getCreatedAt() {
        return GitHubClient.parseInstant(createdAt);
    }

    /**
     * Gets the privacy level of this sponsorship.
     *
     * @return the privacy level
     */
    public PrivacyLevel getPrivacyLevel() {
        return privacyLevel;
    }

    /**
     * Gets the login of the sponsor.
     *
     * @return the sponsor login, or null if the sponsorship is private
     */
    public String getSponsorLogin() {
        return sponsorEntity == null ? null : sponsorEntity.get("login");
    }

    /**
     * Gets the login of the sponsored user or organization.
     *
     * @return the sponsorable login
     */
    public String getSponsorableLogin() {
        return sponsorable == null ? null : sponsorable.get("login");
    }

    /**
     * Gets the tier of this sponsorship.
     *
     * @return the tier, or null unless the authenticated user is the sponsor or the maintainer
     */
    public GHSponsorsTier getTier() {
        return tier;
    }

    /**
     * Whether this sponsorship is currently active.
     *
     * @return true if active
     */
    public boolean isActive() {
        return isActive;
    }

    /**
     * Whether this sponsorship is a one-time payment.
     *
     * @return true if one-time
     */
    public boolean isOneTimePayment() {
        return isOneTimePayment;
    }
}
