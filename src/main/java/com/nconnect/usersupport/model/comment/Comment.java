package com.nconnect.usersupport.model.comment;

import io.vlingo.xoom.actors.Definition;
import io.vlingo.xoom.common.Completes;
import com.nconnect.usersupport.model.*;
import io.vlingo.xoom.actors.Stage;
import io.vlingo.xoom.common.Outcome;

public interface Comment {

  Completes<Outcome<DomainError, CommentState>> defineWith(final Tenant tenant, final String supportCaseId, final Commenter commenter, final String body);

  static Completes<Outcome<DomainError, CommentState>> defineWith(final Stage stage, final Tenant tenant, final String supportCaseId, final Commenter commenter, final String body) {
    final io.vlingo.xoom.actors.Address _address = stage.addressFactory().uniquePrefixedWith("g-");
    final Comment _comment = stage.actorFor(Comment.class, Definition.has(CommentEntity.class, Definition.parameters(_address.idString())), _address);

    return _comment.defineWith(tenant, supportCaseId, commenter, body);
  }

  Completes<Outcome<DomainError, CommentState>> edit(final Commenter commenter, final String body);

  Completes<Outcome<DomainError, CommentState>> markAsRemoved(final Commenter commenter);

}