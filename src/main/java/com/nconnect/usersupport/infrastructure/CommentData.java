package com.nconnect.usersupport.infrastructure;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.stream.Collectors;

import com.nconnect.usersupport.model.comment.CommentState;

import java.util.*;

import com.nconnect.usersupport.model.*;

@SuppressWarnings("all")
public class CommentData {
    public final String supportCaseId;
    public final String body;

    public static CommentData from(final String supportCaseId, final String body) {
        return new CommentData(supportCaseId, body);
    }

    private CommentData(final String supportCaseId, final String body) {
        this.supportCaseId = supportCaseId;
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
        CommentData another = (CommentData) other;
        return new EqualsBuilder()
                .append(this.supportCaseId, another.supportCaseId)
                .append(this.body, another.body)
                .isEquals();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.DEFAULT_STYLE)
                .append("supportCaseId", supportCaseId)
                .append("body", body)
                .toString();
    }

}
