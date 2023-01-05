package com.nconnect.usersupport.model.comment;

import io.vlingo.xoom.common.version.SemanticVersion;
import io.vlingo.xoom.lattice.model.IdentifiedDomainEvent;


/**
 * See
 * <a href="https://docs.vlingo.io/xoom-lattice/entity-cqrs#commands-domain-events-and-identified-domain-events">
 *   Commands, Domain Events, and Identified Domain Events
 * </a>
 */
public final class CommentMarkAsRemoved extends IdentifiedDomainEvent {

  public final String id;
  public final String supportCaseId;

  public CommentMarkAsRemoved(final String id, final String supportCaseId) {
    super(SemanticVersion.from("1.0.0").toValue());
    this.id = id;
    this.supportCaseId = supportCaseId;
  }

  @Override
  public String identity() {
    return id;
  }
}
