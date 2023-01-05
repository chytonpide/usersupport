package com.nconnect.usersupport.infrastructure.persistence;

import com.nconnect.usersupport.infrastructure.*;
import com.nconnect.usersupport.model.comment.CommentEdited;
import com.nconnect.usersupport.model.comment.CommentMarkAsRemoved;
import com.nconnect.usersupport.model.comment.CommentedToSupportCase;
import com.nconnect.usersupport.model.supportcase.SupportCaseAssigned;
import com.nconnect.usersupport.model.supportcase.SupportCaseClosed;
import com.nconnect.usersupport.model.supportcase.SupportCaseEdited;
import com.nconnect.usersupport.model.supportcase.SupportCaseOpened;
import io.vlingo.xoom.lattice.model.projection.Projectable;
import io.vlingo.xoom.lattice.model.projection.StateStoreProjectionActor;
import io.vlingo.xoom.symbio.Source;
import io.vlingo.xoom.symbio.store.state.StateStore;
import io.vlingo.xoom.turbo.ComponentRegistry;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;

/**
 * See
 * <a href="https://docs.vlingo.io/xoom-lattice/projections#implementing-with-the-statestoreprojectionactor">
 * StateStoreProjectionActor
 * </a>
 */
public class CommentProjectionActor extends StateStoreProjectionActor<CommentView> {

    private static final CommentView Empty = CommentView.empty();

    public CommentProjectionActor() {
        this(ComponentRegistry.withType(QueryModelStateStoreProvider.class).store);
    }

    public CommentProjectionActor(final StateStore stateStore) {
        super(stateStore);
    }

    @Override
    protected CommentView currentDataFor(final Projectable projectable) {
        return Empty;
    }

    @Override
    protected String dataIdFor(final Projectable projectable) {
        String dataId = projectable.dataId();

        if (dataId.isEmpty()) {
            try {
                dataId = dataIdFor(sources().get(0));
            } catch (Exception e) {
                // ignore; fall through
            }
        }

        return dataId;
    }

    private String dataIdFor(Source<?> event) {
        String dataId = typedToIdentifiedDomainEvent(event).identity();
        return dataId;
    }

    @Override
    protected CommentView merge(final CommentView previousData, final int previousVersion, final CommentView currentData, final int currentVersion) {
        //if (previousVersion == currentVersion) return currentData;

        CommentView merged = previousData;

        for (final Source<?> event : sources()) {
            switch (Events.valueOf(event.typeName())) {
                case CommentedToSupportCase: {

                    final CommentedToSupportCase typedEvent = typed(event);
                    final TenantData tenant = TenantData.from(typedEvent.tenant);
                    final CommenterData commenter = CommenterData.from(typedEvent.commenter);
                    final LocalDateTime commentedAt = LocalDateTime.ofInstant(Instant.ofEpochMilli(typedEvent.dateTimeSourced), TimeZone.getDefault().toZoneId());

                    merged = CommentView.from(typedEvent.id, tenant, commenter, typedEvent.body, commentedAt, null);
                    break;
                }

                case CommentEdited: {
                    final CommentEdited typedEvent = typed(event);
                    final LocalDateTime lastEditedAt = LocalDateTime.ofInstant(Instant.ofEpochMilli(typedEvent.dateTimeSourced), TimeZone.getDefault().toZoneId());

                    merged = CommentView.from(previousData.id, previousData.tenant, previousData.commenter, typedEvent.body, previousData.commentedAt, lastEditedAt);
                    break;
                }

                case CommentMarkAsRemoved: {
                    merged = CommentView.empty();
                    break;
                }

                default:
                    logger().warn("Event of type " + event.typeName() + " was not matched.");
                    break;

            }
        }

        return merged;
    }
}
