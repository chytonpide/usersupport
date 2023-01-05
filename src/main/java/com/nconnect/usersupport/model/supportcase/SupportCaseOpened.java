package com.nconnect.usersupport.model.supportcase;

import com.nconnect.usersupport.model.Customer;
import com.nconnect.usersupport.model.Tenant;
import io.vlingo.xoom.common.version.SemanticVersion;
import io.vlingo.xoom.lattice.model.IdentifiedDomainEvent;


/**
 * See
 * <a href="https://docs.vlingo.io/xoom-lattice/entity-cqrs#commands-domain-events-and-identified-domain-events">
 *   Commands, Domain Events, and Identified Domain Events
 * </a>
 */
public final class SupportCaseOpened extends IdentifiedDomainEvent {

  public final String id;
  public final Tenant tenant;
  public final Customer customer;
  public final String categoryId;
  public final String subject;
  public final String description;
  public final SupportCaseStatus status;

  public SupportCaseOpened(final String id, final Tenant tenant, final Customer customer, final String categoryId, String subject, String description, SupportCaseStatus status) {
    super(SemanticVersion.from("1.0.0").toValue());
    this.id = id;
    this.tenant = tenant;
    this.customer = customer;
    this.categoryId = categoryId;
    this.subject = subject;
    this.description = description;
    this.status = status;
  }

  @Override
  public String identity() {
    return id;
  }
}
