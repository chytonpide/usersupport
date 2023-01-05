package com.nconnect.usersupport.model.supportcase;

import io.vlingo.xoom.common.version.SemanticVersion;
import io.vlingo.xoom.lattice.model.IdentifiedDomainEvent;


/**
 * See
 * <a href="https://docs.vlingo.io/xoom-lattice/entity-cqrs#commands-domain-events-and-identified-domain-events">
 *   Commands, Domain Events, and Identified Domain Events
 * </a>
 */
public final class SupportCaseEdited extends IdentifiedDomainEvent {

  public final String id;
  public final String categoryId;
  public final String subject;
  public final String description;

  public SupportCaseEdited(final String id, final String categoryId, final String subject, final String description) {
    super(SemanticVersion.from("1.0.0").toValue());
    this.id = id;
    this.categoryId = categoryId;
    this.subject = subject;
    this.description = description;
  }

  @Override
  public String identity() {
    return id;
  }
}
