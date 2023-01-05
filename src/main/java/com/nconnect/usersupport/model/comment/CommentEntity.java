package com.nconnect.usersupport.model.comment;

import io.vlingo.xoom.common.Completes;
import com.nconnect.usersupport.model.*;

import io.vlingo.xoom.common.Failure;
import io.vlingo.xoom.common.Outcome;
import io.vlingo.xoom.common.Success;
import io.vlingo.xoom.lattice.model.sourcing.EventSourced;

import static io.vlingo.xoom.lattice.model.sourcing.Sourced.registerConsumer;

/**
 * See <a href="https://docs.vlingo.io/xoom-lattice/entity-cqrs#sourced">EventSourced</a>
 */
public final class CommentEntity extends EventSourced implements Comment, TenantOwned {
  private CommentState state;

  public CommentEntity(final String id) {
    super(id);
    this.state = CommentState.identifiedBy(id);
  }

  static {
    registerConsumer(CommentEntity.class, CommentedToSupportCase.class, CommentEntity::applyCommentedToSupportCase);
    registerConsumer(CommentEntity.class, CommentEdited.class, CommentEntity::applyCommentEdited);
    registerConsumer(CommentEntity.class, CommentMarkAsRemoved.class, CommentEntity::applyCommentMarkAsRemoved);
  }

  @Override
  public Completes<Outcome<DomainError, CommentState>> defineWith(final Tenant tenant, final String supportCaseId, final Commenter commenter, final String body) {
    DomainError error = new DomainError();

    if(tenant == null) {
      error.addMessage("A tenant is required.");
    }

    if(supportCaseId == null || supportCaseId.isEmpty()) {
      error.addMessage("A supportCaseId is required.");
    }

    if(commenter == null) {
      error.addMessage("A commenter is required.");
    }

    if(body == null || body.length() == 0) {
      error.addMessage("The body is required.");
    }

    if(!error.isEmpty()) {
      return completes().with(Failure.of(error));
    }

    return apply(new CommentedToSupportCase(state.id, tenant, supportCaseId, commenter, body), () -> Success.of(state));
  }

  @Override
  public Completes<Outcome<DomainError, CommentState>> edit(final Commenter commenter, final String body) {
    DomainError error = new DomainError();

    if(!state.commenter.id.equals(commenter.id)) {
      error.addMessage("No authorization.");
    }

    if(body.isEmpty()) {
      error.addMessage("The comment body is required.");
    }

    if(body != null  && body.length() > 255) {
      error.addMessage("The comment body must be 255 characters or less.");
    }

    if(!error.isEmpty()) {
      return completes().with(Failure.of(error));
    }

    return apply(new CommentEdited(state.id, state.supportCaseId, body), () -> Success.of(state));
  }

  @Override
  public Completes<Outcome<DomainError, CommentState>> markAsRemoved(Commenter commenter) {
    DomainError error = new DomainError();

    if(state.removed) {
      error.addMessage("The comment is removed already.");
    }

    if(commenter == null) {
      error.addMessage("A commenter is required.");
    }

    if(!commenter.id.equals(state.commenter.id)) {
      error.addMessage("No authorization.");
    }

    if(!error.isEmpty()) {
      return completes().with(Failure.of(error));
    }
System.out.println("-------");
    return apply(new CommentMarkAsRemoved(state.id, state.supportCaseId), () -> Success.of(state));
  }

  private void applyCommentedToSupportCase(final CommentedToSupportCase event) {
    state = state.defineWith(event.id, event.tenant, event.supportCaseId, event.commenter, event.body, false);
  }

  private void applyCommentEdited(final CommentEdited event) {
    state = state.edit(event.body);
  }

  private void applyCommentMarkAsRemoved(final CommentMarkAsRemoved event) {
    state = state.markAsRemoved();
  }

  /*
   * Restores my initial state by means of {@code state}.
   *
   * @param snapshot the {@code CommentState} holding my state
   * @param currentVersion the int value of my current version; may be helpful in determining if snapshot is needed
   */
  @Override
  @SuppressWarnings("hiding")
  protected <CommentState> void restoreSnapshot(final CommentState snapshot, final int currentVersion) {
    // OVERRIDE FOR SNAPSHOT SUPPORT
    // See: https://docs.vlingo.io/xoom-lattice/entity-cqrs#eventsourced
  }

  /*
   * Answer the valid {@code CommentState} instance if a snapshot should
   * be taken and persisted along with applied {@code Source<T>} instance(s).
   *
   * @return CommentState
   */
  @Override
  @SuppressWarnings("unchecked")
  protected CommentState snapshot() {
    // OVERRIDE FOR SNAPSHOT SUPPORT
    // See: https://docs.vlingo.io/xoom-lattice/entity-cqrs#eventsourced
    return null;
  }

  @Override
  public Completes<Boolean> isOwnedBy(String tenantId) {
    if(state.tenant.id.equals(tenantId)) {
      return this.completes().with(true);
    } else {
      return this.completes().with(false);
    }
  }
}
