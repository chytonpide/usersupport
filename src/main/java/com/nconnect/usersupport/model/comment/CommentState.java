package com.nconnect.usersupport.model.comment;

import com.nconnect.usersupport.model.*;

public final class CommentState {

  public final String id;
  public final Tenant tenant;
  public final String supportCaseId;
  public final Commenter commenter;
  public final String body;
  public final boolean removed;

  public static CommentState identifiedBy(final String id) {
    return new CommentState(id, null, null,  null, null, false);
  }

  public CommentState (final String id, final Tenant tenant, final String supportCaseId, final Commenter commenter, final String body, final boolean removed) {
    this.id = id;
    this.tenant = tenant;
    this.supportCaseId = supportCaseId;
    this.commenter = commenter;
    this.body = body;
    this.removed = removed;
  }

  public CommentState defineWith(final String id, final Tenant tenant, final String supportCaseId, final Commenter commenter, final String body, final boolean removed) {
    return new CommentState(id, tenant, supportCaseId, commenter, body, removed);
  }

  public CommentState edit(final String body) {
    return new CommentState(this.id, this.tenant, this.supportCaseId, this.commenter, body, this.removed);
  }

  public CommentState markAsRemoved() {
    return new CommentState(this.id, this.tenant, this.supportCaseId, this.commenter, body, true);
  }

}
