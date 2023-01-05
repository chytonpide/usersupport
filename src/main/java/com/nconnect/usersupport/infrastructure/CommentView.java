package com.nconnect.usersupport.infrastructure;

import com.nconnect.usersupport.model.comment.CommentState;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.time.LocalDateTime;

public class CommentView {
    public final String id;
    public final TenantData tenant;
    public final CommenterData commenter;
    public final String body;
    public final LocalDateTime commentedAt;
    public final LocalDateTime lastEditedAt;

    public static CommentView from(final CommentState commentState, final LocalDateTime commentedAt, final LocalDateTime lastEditedAt) {
        final TenantData tenant = commentState.tenant != null ? TenantData.from(commentState.tenant) : null;
        final CommenterData commenter = commentState.commenter != null ? CommenterData.from(commentState.commenter) : null;
        return from(commentState.id, tenant, commenter, commentState.body, commentedAt, lastEditedAt);
    }

    public static CommentView from(final String id, final TenantData tenant, final CommenterData commenter, final String body, final LocalDateTime commentedAt, final LocalDateTime lastEditedAt) {
        return new CommentView(id, tenant, commenter, body, commentedAt, lastEditedAt);
    }

    public static CommentView empty() {
        return from(CommentState.identifiedBy(""), null, null);
    }

    private CommentView(final String id, final TenantData tenant, final CommenterData commenter, final String body, final LocalDateTime commentedAt, final LocalDateTime lastEditedAt) {
        this.id = id;
        this.tenant = tenant;
        this.commenter = commenter;
        this.body = body;
        this.commentedAt = commentedAt;
        this.lastEditedAt = lastEditedAt;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        CommentView another = (CommentView) other;
        return new EqualsBuilder()
                .append(this.id, another.id)
                .append(this.tenant, another.tenant)
                .append(this.commenter, another.commenter)
                .append(this.body, another.body)
                .append(this.commentedAt, another.commentedAt)
                .append(this.lastEditedAt, another.lastEditedAt)
                .isEquals();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.DEFAULT_STYLE)
                .append("id", id)
                .append("tenant", tenant)
                .append("commenter", commenter)
                .append("body", body)
                .append("commentedAt", commentedAt)
                .append("lastEditedAt", lastEditedAt)
                .toString();
    }

}
