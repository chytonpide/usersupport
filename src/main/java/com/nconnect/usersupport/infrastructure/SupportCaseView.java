package com.nconnect.usersupport.infrastructure;

import com.nconnect.usersupport.model.Supporter;
import com.nconnect.usersupport.model.Customer;
import com.nconnect.usersupport.model.Tenant;
import com.nconnect.usersupport.model.comment.CommentState;
import com.nconnect.usersupport.model.supportcase.SupportCaseState;
import com.nconnect.usersupport.model.supportcase.SupportCaseStatus;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.Collections.emptyList;

@SuppressWarnings("all")
public class SupportCaseView {
  public final String id;
  public final TenantData tenant;
  public final CustomerData customer;
  public final String categoryId;
  public final String subject;
  public final String description;
  public final SupportCaseStatus status;
  public final SupporterData supporter;
  public final List<CommentListItem> comments;
  public final LocalDateTime openedAt;
  public final LocalDateTime assignedAt;
  public final LocalDateTime closedAt;
  public final LocalDateTime lastEditedAt;
  public final List<EventData> events;

  public static SupportCaseView from(final SupportCaseState supportCaseState, final List<CommentListItem> comments, final List<EventData> events, final LocalDateTime openedAt, final LocalDateTime assignedAt, final LocalDateTime lastEditedAt, final LocalDateTime cloasedAt) {
    final TenantData tenant = supportCaseState.tenant != null ? TenantData.from(supportCaseState.tenant) : null;
    final CustomerData customer = supportCaseState.customer != null ? CustomerData.from(supportCaseState.customer) : null;
    final SupporterData supporter = supportCaseState.supporter != null ? SupporterData.from(supportCaseState.supporter) : null;

    return from(supportCaseState.id, tenant, customer, supportCaseState.categoryId, supportCaseState.subject, supportCaseState.description, supportCaseState.status, supporter, comments, events, openedAt, assignedAt, lastEditedAt, cloasedAt);
  }

  public static SupportCaseView from(final String id,
                                     final TenantData tenant,
                                     final CustomerData customer,
                                     final String categoryId,
                                     final String subject,
                                     final String description,
                                     final SupportCaseStatus status,
                                     final SupporterData supporter,
                                     final List<CommentListItem> comments,
                                     final List<EventData> events,
                                     final LocalDateTime openedAt,
                                     final LocalDateTime assignedAt,
                                     final LocalDateTime lastEditedAt,
                                     final LocalDateTime cloasedAt) {
    return new SupportCaseView(id, tenant, customer, categoryId, subject, description, status, supporter, comments, events, openedAt, assignedAt, lastEditedAt, cloasedAt);
  }

  public static SupportCaseView empty() {
    return from(SupportCaseState.identifiedBy(""), emptyList(),  emptyList(), null, null, null, null);
  }

  private SupportCaseView(final String id, final TenantData tenant, final CustomerData customer, final String categoryId, final String subject, final String description, final SupportCaseStatus status, final SupporterData supporter, final List<CommentListItem> comments, final List<EventData> events, final LocalDateTime openedAt, final LocalDateTime assignedAt, final LocalDateTime lastEditedAt, final LocalDateTime cloasedAt) {
    this.id = id;
    this.tenant = tenant;
    this.customer = customer;
    this.categoryId = categoryId;
    this.subject = subject;
    this.description = description;
    this.status = status;
    this.supporter = supporter;
    this.comments = comments;
    this.events = events;
    this.openedAt = openedAt;
    this.assignedAt = assignedAt;
    this.lastEditedAt = lastEditedAt;
    this.closedAt = cloasedAt;
  }

  public SupportCaseState toSupportCaseState() {
    final Tenant tenant = Tenant.from(this.tenant.id);
    final Customer customer = Customer.from(this.customer.id, this.customer.name);
    final Supporter supporter = Supporter.from(this.supporter.id, this.supporter.name);
    return new SupportCaseState(id, tenant, customer, categoryId, subject, description, status, supporter);
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) {
      return true;
    }
    if (other == null || getClass() != other.getClass()) {
      return false;
    }
    SupportCaseView another = (SupportCaseView) other;
    return new EqualsBuilder()
              .append(this.id, another.id)
              .append(this.tenant, another.tenant)
              .append(this.customer, another.customer)
              .append(this.categoryId, another.categoryId)
              .append(this.subject, another.subject)
              .append(this.description, another.description)
              .append(this.status, another.status)
              .append(this.supporter, another.supporter)
              .append(this.comments, another.comments)
            .append(this.openedAt, another.openedAt)
            .append(this.assignedAt, another.assignedAt)
            .append(this.lastEditedAt, another.lastEditedAt)
            .append(this.closedAt, another.closedAt)
              .isEquals();
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.DEFAULT_STYLE)
              .append("id", id)
              .append("tenant", tenant)
              .append("customer", customer)
              .append("categoryId", categoryId)
              .append("subject", subject)
              .append("description", description)
              .append("status", status)
              .append("supporter", supporter)
              .append("comments", comments)
              .append("events", events)
              .append("openedAt", openedAt)
              .append("assignedAt", assignedAt)
              .append("lastEditedAt", lastEditedAt)
              .append("closedAt", closedAt)
              .toString();
  }

  public static class CommentListItem {
    public final String id;
    public final CommenterData commenter;
    public final String body;
    public final LocalDateTime commentedAt;
    public final LocalDateTime lastEditedAt;


    public static CommentListItem from(final CommentState commentState, final LocalDateTime commentedAt, final LocalDateTime lastEditedAt) {
      final CommenterData commenter = commentState.commenter != null ? CommenterData.from(commentState.commenter) : null;
      return from(commentState.id, commenter, commentState.body, commentedAt, lastEditedAt);
    }

    public static CommentListItem from(final String id, final CommenterData commenter, final String body, final LocalDateTime commentedAt, final LocalDateTime lastEditedAt) {
      return new CommentListItem(id, commenter, body, commentedAt, lastEditedAt);
    }


    public static CommentListItem empty() {
      return from(CommentState.identifiedBy(""), null, null);
    }

    private CommentListItem(final String id, final CommenterData commenter, final String body, final LocalDateTime commentedAt, final LocalDateTime lastEditedAt) {
      this.id = id;
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
      CommentListItem another = (CommentListItem) other;
      return new EqualsBuilder()
              .append(this.id, another.id)
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
              .append("commenter", commenter)
              .append("body", body)
              .append("commentedAt", commentedAt)
              .append("lastEditedAt", lastEditedAt)
              .toString();
    }

  }


}
