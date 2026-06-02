package org.kohsuke.github;

import org.kohsuke.github.internal.graphql.response.GHGraphQLResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * GraphQL queries behind the GitHub Sponsors methods of {@link GHPerson}.
 */
class GHSponsors {

    private static final String SPONSORSHIP_PAGE = "{ pageInfo { hasNextPage endCursor } nodes {"
            + " sponsorEntity { ... on User { login } ... on Organization { login } }"
            + " sponsorable { ... on User { login } ... on Organization { login } }"
            + " tier { id name description monthlyPriceInDollars monthlyPriceInCents isOneTime isCustomAmount }"
            + " isActive isOneTimePayment privacyLevel createdAt } }";

    @SuppressWarnings("unchecked")
    private static Map<String, Object> asMap(Object value) {
        return (Map<String, Object>) value;
    }

    private static String escape(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private static List<GHSponsorship> listSponsorships(GHPerson person, String connection, String arguments)
            throws IOException {
        List<GHSponsorship> sponsorships = new ArrayList<>();
        String cursor = null;
        do {
            String after = cursor == null ? "" : ", after: \"" + escape(cursor) + "\"";
            Map<String, Object> page = asMap(
                    query(person, connection + "(" + arguments + after + ") " + SPONSORSHIP_PAGE).get(connection));
            GHSponsorship[] nodes = GitHubClient.getMappingObjectReader(person.root())
                    .forType(GHSponsorship[].class)
                    .readValue(GitHubClient.getMappingObjectWriter().writeValueAsString(page.get("nodes")));
            Collections.addAll(sponsorships, nodes);
            Map<String, Object> pageInfo = asMap(page.get("pageInfo"));
            cursor = (boolean) pageInfo.get("hasNextPage") ? (String) pageInfo.get("endCursor") : null;
        } while (cursor != null);
        return Collections.unmodifiableList(sponsorships);
    }

    private static Map<String, Object> query(GHPerson person, String fields) throws IOException {
        boolean organization = person instanceof GHOrganization || "Organization".equals(person.getType());
        String type = organization ? "organization" : "user";
        String query = "{ " + type + "(login: \"" + escape(person.login) + "\") { " + fields + " } }";
        Object data = person.root().createGraphQLRequest(query).fetchGraphQL(GHGraphQLResponse.ObjectResponse.class);
        return asMap(asMap(data).get(type));
    }

    static boolean hasSponsorsListing(GHPerson person) throws IOException {
        return (boolean) query(person, "hasSponsorsListing").get("hasSponsorsListing");
    }

    static boolean isSponsoredBy(GHPerson person, String sponsorLogin) throws IOException {
        return (boolean) query(person, "isSponsoredBy(accountLogin: \"" + escape(sponsorLogin) + "\")")
                .get("isSponsoredBy");
    }

    static boolean isSponsoring(GHPerson person, String sponsorableLogin) throws IOException {
        String query = "{ repositoryOwner(login: \"" + escape(sponsorableLogin)
                + "\") { ... on Sponsorable { isSponsoredBy(accountLogin: \"" + escape(person.login) + "\") } } }";
        Object data = person.root().createGraphQLRequest(query).fetchGraphQL(GHGraphQLResponse.ObjectResponse.class);
        Map<String, Object> owner = asMap(asMap(data).get("repositoryOwner"));
        if (owner == null) {
            throw new GHFileNotFoundException(sponsorableLogin);
        }
        return (boolean) owner.get("isSponsoredBy");
    }

    static List<GHSponsorship> listSponsorshipsAsMaintainer(GHPerson person, boolean activeOnly, boolean includePrivate)
            throws IOException {
        return listSponsorships(person,
                "sponsorshipsAsMaintainer",
                "first: 100, activeOnly: " + activeOnly + ", includePrivate: " + includePrivate);
    }

    static List<GHSponsorship> listSponsorshipsAsSponsor(GHPerson person, boolean activeOnly) throws IOException {
        return listSponsorships(person, "sponsorshipsAsSponsor", "first: 100, activeOnly: " + activeOnly);
    }

    private GHSponsors() {
    }
}
