package com.nconnect.usersupport.infrastructure;

import com.nconnect.usersupport.model.Tenant;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class EventData {
    public final String description;
    public final LocalDateTime occurredAt;

    public static EventData from(final String description, LocalDateTime occurredAt) {
        return new EventData(description, occurredAt);
    }

    private EventData(final String description, final LocalDateTime occurredAt) {
        this.description = description; this.occurredAt = occurredAt;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        EventData another = (EventData) other;
        return new EqualsBuilder()
                .append(this.description, another.description)
                .append(this.occurredAt, another.occurredAt)
                .isEquals();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.DEFAULT_STYLE)
                .append("description", description)
                .append("occurredAt", occurredAt)
                .toString();
    }

}
