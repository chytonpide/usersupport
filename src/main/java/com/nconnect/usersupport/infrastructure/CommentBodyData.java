package com.nconnect.usersupport.infrastructure;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@SuppressWarnings("all")
public class CommentBodyData {
    public final String body;

    public static CommentBodyData from(final String body) {
        return new CommentBodyData(body);
    }

    private CommentBodyData(final String body) {
        this.body = body;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        CommentBodyData another = (CommentBodyData) other;
        return new EqualsBuilder()
                .append(this.body, another.body)
                .isEquals();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.DEFAULT_STYLE)
                .append("body", body)
                .toString();
    }
}
