package com.nconnect.usersupport.infrastructure.persistence;

import com.nconnect.usersupport.infrastructure.*;
import com.nconnect.usersupport.model.comment.CommentEdited;
import com.nconnect.usersupport.model.comment.CommentMarkAsRemoved;
import com.nconnect.usersupport.model.supportcase.*;
import io.vlingo.xoom.turbo.annotation.persistence.Persistence;
import io.vlingo.xoom.turbo.annotation.persistence.Persistence.StorageType;
import io.vlingo.xoom.turbo.annotation.persistence.Projections;
import io.vlingo.xoom.turbo.annotation.persistence.Projection;
import io.vlingo.xoom.turbo.annotation.persistence.ProjectionType;
import io.vlingo.xoom.turbo.annotation.persistence.Adapters;
import io.vlingo.xoom.turbo.annotation.persistence.EnableQueries;
import io.vlingo.xoom.turbo.annotation.persistence.QueriesEntry;
import io.vlingo.xoom.turbo.annotation.persistence.DataObjects;
import com.nconnect.usersupport.model.comment.CommentedToSupportCase;

@SuppressWarnings("unused")
@Persistence(basePackage = "com.nconnect.usersupport", storageType = StorageType.JOURNAL, cqrs = true)
@Projections(value = {
        @Projection(actor = SupportCaseProjectionActor.class, becauseOf = {SupportCaseClosed.class, SupportCaseAssigned.class, SupportCaseOpened.class, SupportCaseEdited.class, CommentEdited.class, CommentedToSupportCase.class, CommentMarkAsRemoved.class}),
        @Projection(actor = CommentProjectionActor.class, becauseOf = {CommentEdited.class, CommentedToSupportCase.class, CommentMarkAsRemoved.class})
}, type = ProjectionType.EVENT_BASED)
@Adapters({
        SupportCaseClosed.class,
        CommentEdited.class,
        SupportCaseAssigned.class,
        SupportCaseEdited.class,
        SupportCaseOpened.class,
        CommentedToSupportCase.class
})
@EnableQueries({
        @QueriesEntry(protocol = SupportCaseQueries.class, actor = SupportCaseQueriesActor.class),
        @QueriesEntry(protocol = CommentQueries.class, actor = CommentQueriesActor.class)
})
@DataObjects({SupportCaseView.class, CommentView.class})
public class PersistenceSetup {


}