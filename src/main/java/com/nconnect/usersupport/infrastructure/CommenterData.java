package com.nconnect.usersupport.infrastructure;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import com.nconnect.usersupport.model.*;

public class CommenterData {

  public final String id;
  public final String name;

  public static CommenterData from(final Commenter commenter) {
    if (commenter == null) {
      return CommenterData.empty();
    } else {
      return from(commenter.id, commenter.name);
    }
  }

  public static CommenterData from(final String id, final String name) {
    return new CommenterData(id, name);
  }

  public static Set<CommenterData> fromAll(final Set<Commenter> correspondingObjects) {
    return correspondingObjects == null ? Collections.emptySet() : correspondingObjects.stream().map(CommenterData::from).collect(Collectors.toSet());
  }

  public static List<CommenterData> fromAll(final List<Commenter> correspondingObjects) {
    return correspondingObjects == null ? Collections.emptyList() : correspondingObjects.stream().map(CommenterData::from).collect(Collectors.toList());
  }

  private CommenterData(final String id, final String name) {
    this.id = id;
    this.name = name;
  }

  public Commenter toCommenter() {
    return Commenter.from(id, name);
  }

  public static CommenterData empty() {
    return new CommenterData(null, null);
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
    CommenterData another = (CommenterData) other;
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
