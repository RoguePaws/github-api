package org.kohsuke.github;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * A GitHub Sponsors tier.
 *
 * @see <a href="https://docs.github.com/en/graphql/reference/objects#sponsorstier">SponsorsTier</a>
 */
@BetaApi
@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
@SuppressFBWarnings(value = { "UWF_UNWRITTEN_FIELD", "NP_UNWRITTEN_FIELD" }, justification = "JSON API")
public class GHSponsorsTier {

    private String description;
    private String id;
    private boolean isCustomAmount;
    private boolean isOneTime;
    private int monthlyPriceInCents;
    private int monthlyPriceInDollars;
    private String name;

    GHSponsorsTier() {
    }

    /**
     * Gets the description of this tier.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the node id of this tier.
     *
     * @return the node id
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the monthly price in cents.
     *
     * @return the monthly price in cents
     */
    public int getMonthlyPriceInCents() {
        return monthlyPriceInCents;
    }

    /**
     * Gets the monthly price in dollars.
     *
     * @return the monthly price in dollars
     */
    public int getMonthlyPriceInDollars() {
        return monthlyPriceInDollars;
    }

    /**
     * Gets the name of this tier.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Whether this tier lets the sponsor choose the amount.
     *
     * @return true if this is a custom amount tier
     */
    public boolean isCustomAmount() {
        return isCustomAmount;
    }

    /**
     * Whether this tier is a one-time payment.
     *
     * @return true if this is a one-time tier
     */
    public boolean isOneTime() {
        return isOneTime;
    }
}
