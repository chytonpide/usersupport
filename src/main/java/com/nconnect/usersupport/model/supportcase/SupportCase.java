package com.nconnect.usersupport.model.supportcase;

import com.nconnect.usersupport.model.comment.CommentState;
import io.vlingo.xoom.actors.Definition;
import io.vlingo.xoom.common.Completes;
import com.nconnect.usersupport.model.*;
import io.vlingo.xoom.actors.Stage;
import io.vlingo.xoom.common.Outcome;

public interface SupportCase {

  Completes<Outcome<DomainError, SupportCaseState>> open(final Tenant tenant, final Customer customer, final String categoryId, final String subject, final String description);

  static Completes<Outcome<DomainError,SupportCaseState>> open(final Stage stage, final Tenant tenant, final Customer customer, final String categoryId, final String subject, final String description) {
    final io.vlingo.xoom.actors.Address _address = stage.addressFactory().uniquePrefixedWith("g-");
    final SupportCase _supportCase = stage.actorFor(SupportCase.class, Definition.has(SupportCaseEntity.class, Definition.parameters(_address.idString())), _address);
    return _supportCase.open(tenant, customer, categoryId, subject, description);
  }

  Completes<Outcome<DomainError, SupportCaseState>> close(final User closingUser);

  Completes<Outcome<DomainError, SupportCaseState>> assign(final Supporter supporter);

  Completes<Outcome<DomainError, CommentState>> commentFor(final Stage stage, final Tenant tenant, final Commenter commenter, final String commentBody);

  Completes<Outcome<DomainError, SupportCaseState>> edit(final Customer customer, final String categoryId, final String subject, final String description);

}