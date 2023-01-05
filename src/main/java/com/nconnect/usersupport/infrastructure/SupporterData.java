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

public class SupporterData {

  public final String id;
  public final String name;

  public static SupporterData from(final Supporter supporter) {
    if (supporter == null) {
      return SupporterData.empty();
    } else {
      return from(supporter.id, supporter.name);
    }
  }

  public static SupporterData from(final String id, final String name) {
    return new SupporterData(id, name);
  }

  public static Set<SupporterData> fromAll(final Set<Supporter> correspondingObjects) {
    return correspondingObjects == null ? Collections.emptySet() : correspondingObjects.stream().map(SupporterData::from).collect(Collectors.toSet());
  }

  public static List<SupporterData> fromAll(final List<Supporter> correspondingObjects) {
    return correspondingObjects == null ? Collections.emptyList() : correspondingObjects.stream().map(SupporterData::from).collect(Collectors.toList());
  }

  private SupporterData(final String id, final String name) {
    this.id = id;
    this.name = name;
  }

  public Supporter toSupporter() {
    return Supporter.from(id, name);
  }

  public static SupporterData empty() {
    return new SupporterData(null, null);
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
    SupporterData another = (SupporterData) other;
    return new EqualsBuilder()
              .append(this.id, another.id)
              .append(this.name, another.name)
              .isEquals();
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.DEFAULT_STYLE)
              .append("identity", id)
              .append("name", name)
              .toString();
  }
}
