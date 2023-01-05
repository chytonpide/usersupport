package com.nconnect.usersupport.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public final class Commenter {

  public final String id;
  public final String name;

  public static Commenter from(final String id, final String name) {
    return new Commenter(id, name);
  }

  private Commenter(final String id, final String name) {
    this.id = id;
    this.name = name;
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(31, 17)
              .append(id)
              .append(name)
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
    Commenter another = (Commenter) other;
    return new EqualsBuilder()
              .append(this.id, another.id)
              .append(this.name, another.name)
              .isEquals();
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.DEFAULT_STYLE)
              .append("id", id)
              .append("name", name)
              .toString();
  }
}
