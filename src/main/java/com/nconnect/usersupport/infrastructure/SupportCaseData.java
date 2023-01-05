package com.nconnect.usersupport.infrastructure;

import com.nconnect.usersupport.model.supportcase.SupportCaseStatus;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import java.util.stream.Collectors;
import com.nconnect.usersupport.model.supportcase.SupportCaseState;
import java.util.*;
import com.nconnect.usersupport.model.*;

@SuppressWarnings("all")
public class SupportCaseData {
  public final String categoryId;
  public final String subject;
  public final String description;

  public static SupportCaseData from(final String categoryId, final String subject, final String description) {
    return new SupportCaseData(categoryId, subject, description);
  }

  private SupportCaseData (final String categoryId, final String subject, final String description) {
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
    SupportCaseData another = (SupportCaseData) other;
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
