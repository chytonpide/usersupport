package com.nconnect.usersupport.model.comment;

import io.vlingo.xoom.common.version.SemanticVersion;
import io.vlingo.xoom.lattice.model.IdentifiedDomainEvent;

import com.nconnect.usersupport.model.*;

/**
 * See
 * <a href="https://docs.vlingo.io/xoom-lattice/entity-cqrs#commands-domain-events-and-identified-domain-events">
 *   Commands, Domain Events, and Identified Domain Events
 * </a>
 */
public final class CommentedToSupportCase extends IdentifiedDomainEvent {

  public final String id;
  public final Tenant tenant;
  public final String supportCaseId;
  public final Commenter commenter;
  public final String body;

  public CommentedToSupportCase(final String id, final Tenant tenant, final String supportCaseId, final Commenter commenter, final String body) {
    super(SemanticVersion.from("1.0.0").toValue());
    this.id = id;
    this.tenant = tenant;
    this.supportCaseId = supportCaseId;
    this.commenter = commenter;
    this.body = body;
  }

  @Override
  public String identity() {
    return id;
  }
}
