package com.nconnect.usersupport.infrastructure;

import com.nconnect.usersupport.model.supportcase.SupportCaseStatus;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.time.LocalDateTime;
import java.util.List;

@SuppressWarnings("all")
public class SupportCasesView {
  public final List<ListItem> items;
  public final int offset;
  public final int limit;
  public final long total;

  public static SupportCasesView from(final List<ListItem> items, int offset, int limit, long total) {
    return new SupportCasesView(items, offset, limit, total);
  }

  private SupportCasesView(final List<ListItem> items, final int offset, final int limit, final long total) {
    this.items = items;
    this.offset = offset;
    this.limit = limit;
    this.total = total;
  }

  public void addListItem(ListItem item) {
    this.items.add(item);
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) {
      return true;
    }
    if (other == null || getClass() != other.getClass()) {
      return false;
    }
    SupportCasesView another = (SupportCasesView) other;
    return new EqualsBuilder()
              .append(this.offset, another.offset)
              .append(this.limit, another.limit)
              .append(this.items, another.items)
              .isEquals();
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.DEFAULT_STYLE)
              .append("offset", offset)
              .append("limit", limit)
              .append("items", items)
            .append("total", total)
              .toString();
  }

  public static class ListItem {
    public final String id;
    public final CustomerData customer;
    public final String categoryId;
    public final String subject;
    public final SupportCaseStatus status;
    public final SupporterData supporter;
    public final LocalDateTime openedAt;

    public static ListItem from(final String id, final CustomerData customer, final String categoryId, final String subject, final SupportCaseStatus status, final SupporterData supporter, final LocalDateTime openedAt) {
      return new ListItem(id, customer, categoryId, subject, status, supporter, openedAt);
    }

    public static ListItem from(SupportCaseView supprtCaseView) {
      return new ListItem(supprtCaseView.id, supprtCaseView.customer, supprtCaseView.categoryId, supprtCaseView.subject, supprtCaseView.status, supprtCaseView.supporter, supprtCaseView.openedAt);
    }

    private ListItem(final String id, final CustomerData customer, final String categoryId, final String subject, final SupportCaseStatus status, final SupporterData supporter, final LocalDateTime openedAt) {
      this.id = id;
      this.customer = customer;
      this.categoryId = categoryId;
      this.subject = subject;
      this.status = status;
      this.supporter = supporter;
      this.openedAt = openedAt;
    }

    @Override
    public int hashCode() {
      return new HashCodeBuilder(31, 17)
              .append(id)
              .append(customer)
              .append(categoryId)
              .append(subject)
              .append(status)
              .append(supporter)
              .append(openedAt)
              .toHashCode();
    }

    @Override
    public boolean equals(Object other) {
      if (other == this) {
        return true;
      }
      if (other == null || getClass() != other.getClass()) {
        return false;
      }
      ListItem another = (ListItem) other;
      return new EqualsBuilder()
              .append(this.id, another.id)
              .append(this.customer, another.customer)
              .append(this.categoryId, another.categoryId)
              .append(this.subject, another.subject)
              .append(this.status, another.status)
              .append(this.supporter, another.supporter)
              .append(this.openedAt, another.openedAt)
              .isEquals();
    }

    @Override
    public String toString() {
      return new ToStringBuilder(this, ToStringStyle.DEFAULT_STYLE)
              .append("id", id)
              .append("customer", customer)
              .append("categoryId", categoryId)
              .append("subject", subject)
              .append("status", status)
              .append("supporter", supporter)
              .append("openedAt", openedAt)
              .toString();
    }

  }


}
