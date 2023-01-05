package com.nconnect.usersupport.model.supportcase;

import com.nconnect.usersupport.model.User;
import io.vlingo.xoom.common.version.SemanticVersion;
import io.vlingo.xoom.lattice.model.IdentifiedDomainEvent;


/**
 * See
 * <a href="https://docs.vlingo.io/xoom-lattice/entity-cqrs#commands-domain-events-and-identified-domain-events">
 *   Commands, Domain Events, and Identified Domain Events
 * </a>
 */
public final class SupportCaseClosed extends IdentifiedDomainEvent {

  public final String id;
  public final SupportCaseStatus status;
  public final User closingUser;

  public SupportCaseClosed(final String id, final SupportCaseStatus status, final User closingUser) {
    super(SemanticVersion.from("1.0.0").toValue());
    this.id = id;
    this.status = status;
    this.closingUser = closingUser;
  }

  @Override
  public String identity() {
    return id;
  }
}
