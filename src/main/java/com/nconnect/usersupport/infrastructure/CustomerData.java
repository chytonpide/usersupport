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

public class CustomerData {

  public final String id;
  public final String name;

  public static CustomerData from(final Customer customer) {
    if (customer == null) {
      return CustomerData.empty();
    } else {
      return from(customer.id, customer.name);
    }
  }

  public static CustomerData from(final String identity, final String name) {
    return new CustomerData(identity, name);
  }

  public static Set<CustomerData> fromAll(final Set<Customer> correspondingObjects) {
    return correspondingObjects == null ? Collections.emptySet() : correspondingObjects.stream().map(CustomerData::from).collect(Collectors.toSet());
  }

  public static List<CustomerData> fromAll(final List<Customer> correspondingObjects) {
    return correspondingObjects == null ? Collections.emptyList() : correspondingObjects.stream().map(CustomerData::from).collect(Collectors.toList());
  }

  private CustomerData(final String id, final String name) {
    this.id = id;
    this.name = name;
  }


  public static CustomerData empty() {
    return new CustomerData(null, null);
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
    CustomerData another = (CustomerData) other;
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
