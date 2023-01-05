package com.nconnect.usersupport.model.supportcase;

import io.vlingo.xoom.common.version.SemanticVersion;
import io.vlingo.xoom.lattice.model.IdentifiedDomainEvent;

import com.nconnect.usersupport.model.*;

/**
 * See
 * <a href="https://docs.vlingo.io/xoom-lattice/entity-cqrs#commands-domain-events-and-identified-domain-events">
 *   Commands, Domain Events, and Identified Domain Events
 * </a>
 */
public final class SupportCaseAssigned extends IdentifiedDomainEvent {

  public final String id;
  public final Supporter supporter;
  public final SupportCaseStatus status;

  public SupportCaseAssigned(final String id, final Supporter supporter, final SupportCaseStatus status) {
    super(SemanticVersion.from("1.0.0").toValue());
    this.id = id;
    this.supporter = supporter;
    this.status = status;
  }

  @Override
  public String identity() {
    return id;
  }
}
