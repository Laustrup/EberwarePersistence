package eberware.api.core.systems.persistence;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public enum DatabaseTable {
    USER("users"),
    ADDRESS("addresses"),
    CONTACT_INFO("contact_info"),
    STORY("stories"),
    STORY_DETAIL("story_details"),;

    private final String _title;
}
