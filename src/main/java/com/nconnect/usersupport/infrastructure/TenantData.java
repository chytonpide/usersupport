package com.nconnect.usersupport.infrastructure;

import com.nconnect.usersupport.model.Tenant;
import com.nconnect.usersupport.model.Tenant;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TenantData {

  public final String id;

  public static TenantData from(final Tenant tenant) {
    if (tenant == null) {
      return TenantData.empty();
    } else {
      return from(tenant.id);
    }
  }

  public static TenantData from(final String id) {
    return new TenantData(id);
  }

  public static Set<TenantData> fromAll(final Set<Tenant> correspondingObjects) {
    return correspondingObjects == null ? Collections.emptySet() : correspondingObjects.stream().map(TenantData::from).collect(Collectors.toSet());
  }

  public static List<TenantData> fromAll(final List<Tenant> correspondingObjects) {
    return correspondingObjects == null ? Collections.emptyList() : correspondingObjects.stream().map(TenantData::from).collect(Collectors.toList());
  }

  private TenantData(final String id) {
    this.id = id;
  }

  public Tenant toTenant() {
    return Tenant.from(id);
  }

  public static TenantData empty() {
    return new TenantData(null);
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
    TenantData another = (TenantData) other;
    return new EqualsBuilder()
              .append(this.id, another.id)
              .isEquals();
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.DEFAULT_STYLE)
              .append("id", id)
              .toString();
  }
}
