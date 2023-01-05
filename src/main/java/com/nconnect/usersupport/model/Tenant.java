package com.nconnect.usersupport.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public final class Tenant {

  public final String id;

  public static Tenant from(final String id) {
    return new Tenant(id);
  }

  private Tenant(final String identity) {
    this.id = identity;
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(31, 17)
              .append(id)
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
    Tenant another = (Tenant) other;
    return new EqualsBuilder()
              .append(this.id, another.id)
              .isEquals();
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.DEFAULT_STYLE)
              .append("identity", id)
              .toString();
  }
}
