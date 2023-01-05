package com.nconnect.usersupport.infrastructure;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@SuppressWarnings("all")
public class SupportCaseContentData {
    public final String categoryId;
    public final String subject;
    public final String description;

    public static SupportCaseContentData from(final String categoryId, final String subject, final String description) {
        return new SupportCaseContentData(categoryId, subject, description);
    }

    private SupportCaseContentData(final String categoryId, final String subject, final String description) {
        this.categoryId = categoryId;
        this.subject = subject;
        this.description = description;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        SupportCaseContentData another = (SupportCaseContentData) other;
        return new EqualsBuilder()
                .append(this.categoryId, another.categoryId)
                .append(this.subject, another.subject)
                .append(this.description, another.description)
                .isEquals();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.DEFAULT_STYLE)
                .append("categoryId", categoryId)
                .append("subject", subject)
                .append("description", description)
                .toString();
    }
}
