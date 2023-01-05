package com.nconnect.usersupport.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.ArrayList;
import java.util.List;

public final class User {

  public final String id;
  public final String name;

  public static User from(final String id, final String name) {
    return new User(id, name);
  }

  // TODO: Add validation logic
  private User(final String id, final String name) {
    this.id = id;
    this.name = name;
  }

  public List<String> validate() {
    List<String> errors = new ArrayList<>();
    if(id == null || id.isEmpty()) {
      errors.add("Id is required");
    }

    if (name == null || name.isEmpty()) {
      errors.add("Name is required");
    }
    return errors;
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
    User another = (User) other;
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
