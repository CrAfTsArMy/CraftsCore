package de.craftsblock.craftscore.api.discord;

public class DiscordRPCCache {

    private final String state;
    private final String details;
    private final String smallKey;
    private final String smallText;

    public DiscordRPCCache(String state, String details, String smallKey, String smallText) {
        this.state = state;
        this.details = details;
        this.smallKey = smallKey;
        this.smallText = smallText;
    }

    public String getState() {
        return state;
    }
    public String getDetails() {
        return details;
    }
    public String getSmallKey() {
        return smallKey;
    }
    public String getSmallText() {
        return smallText;
    }

}
