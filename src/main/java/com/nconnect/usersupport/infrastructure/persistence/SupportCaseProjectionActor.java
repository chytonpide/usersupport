package com.nconnect.usersupport.infrastructure.persistence;

import com.nconnect.usersupport.infrastructure.*;
import com.nconnect.usersupport.model.User;
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
import java.util.stream.Collectors;

/**
 * See
 * <a href="https://docs.vlingo.io/xoom-lattice/projections#implementing-with-the-statestoreprojectionactor">
 * StateStoreProjectionActor
 * </a>
 */
public class SupportCaseProjectionActor extends StateStoreProjectionActor<SupportCaseView> {

    private static final SupportCaseView Empty = SupportCaseView.empty();

    public SupportCaseProjectionActor() {
        this(ComponentRegistry.withType(QueryModelStateStoreProvider.class).store);
    }

    public SupportCaseProjectionActor(final StateStore stateStore) {
        super(stateStore);
    }

    @Override
    protected SupportCaseView currentDataFor(final Projectable projectable) {
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
        String dataId = "";
        switch (Events.valueOf(event.typeName())) {

            case CommentedToSupportCase: {
                final CommentedToSupportCase typedEvent = typed(event);
                dataId = typedEvent.supportCaseId;
                break;
            }

            case CommentEdited: {
                final CommentEdited typedEvent = typed(event);
                dataId = typedEvent.supportCaseId;

                break;
            }

            case CommentMarkAsRemoved: {
                final CommentMarkAsRemoved typedEvent = typed(event);
                dataId = typedEvent.supportCaseId;

                break;
            }

            default: {
                dataId = typedToIdentifiedDomainEvent(event).identity();
                break;
            }
        }
        return dataId;
    }

    @Override
    protected SupportCaseView merge(final SupportCaseView previousData, final int previousVersion, final SupportCaseView currentData, final int currentVersion) {
        //if (previousVersion == currentVersion) return currentData;
        System.out.println("previousVersion:"+ previousVersion);
        System.out.println("currentVersion:"+ currentVersion);

        SupportCaseView merged = previousData;

        for (final Source<?> event : sources()) {
            switch (Events.valueOf(event.typeName())) {
                case SupportCaseOpened: {
                    final SupportCaseOpened typedEvent = typed(event);
                    final TenantData tenant = TenantData.from(typedEvent.tenant);
                    final CustomerData customer = CustomerData.from(typedEvent.customer);
                    final LocalDateTime openedAt = LocalDateTime.ofInstant(Instant.ofEpochMilli(typedEvent.dateTimeSourced), TimeZone.getDefault().toZoneId());

                    final List<EventData> events = new ArrayList<>();
                    events.add(EventData.from("Opened by " + customer.name, openedAt));

                    merged = SupportCaseView.from(typedEvent.id, tenant, customer, typedEvent.categoryId, typedEvent.subject, typedEvent.description, typedEvent.status, null, Collections.emptyList(), events, openedAt, null, null, null);
                    break;
                }

                case SupportCaseClosed: {
                    final SupportCaseClosed typedEvent = typed(event);
                    final LocalDateTime closedAt = LocalDateTime.ofInstant(Instant.ofEpochMilli(typedEvent.dateTimeSourced), TimeZone.getDefault().toZoneId());
                    final User closingUser = typedEvent.closingUser;

                    final List<EventData> events = copy(previousData.events);
                    events.add(EventData.from(" Closed by " + closingUser.name, closedAt));

                    merged = SupportCaseView.from(typedEvent.id, previousData.tenant, previousData.customer, previousData.categoryId, previousData.subject, previousData.description, typedEvent.status, previousData.supporter, previousData.comments, events, previousData.openedAt, previousData.assignedAt, previousData.lastEditedAt, closedAt);
                    break;
                }

                case SupportCaseAssigned: {
                    final SupportCaseAssigned typedEvent = typed(event);
                    final SupporterData supporter = SupporterData.from(typedEvent.supporter);
                    final LocalDateTime assignedAt = LocalDateTime.ofInstant(Instant.ofEpochMilli(typedEvent.dateTimeSourced), TimeZone.getDefault().toZoneId());

                    final List<EventData> events = copy(previousData.events);
                    events.add(EventData.from("Assigned to " + supporter.name, assignedAt));

                    merged = SupportCaseView.from(typedEvent.id, previousData.tenant, previousData.customer, previousData.categoryId, previousData.subject, previousData.description, typedEvent.status, supporter, previousData.comments, events, previousData.openedAt, assignedAt, previousData.lastEditedAt, previousData.closedAt);
                    break;
                }

                case SupportCaseEdited: {
                    final SupportCaseEdited typedEvent = typed(event);
                    final LocalDateTime lastEditedAt = LocalDateTime.ofInstant(Instant.ofEpochMilli(typedEvent.dateTimeSourced), TimeZone.getDefault().toZoneId());

                    final List<EventData> events = copy(previousData.events);
                    events.add(EventData.from("Edited", lastEditedAt));

                    merged = SupportCaseView.from(typedEvent.id, previousData.tenant, previousData.customer, typedEvent.categoryId, typedEvent.subject, typedEvent.description, previousData.status, previousData.supporter, previousData.comments, events, previousData.openedAt, previousData.assignedAt, lastEditedAt, previousData.closedAt);
                    break;
                }

                case CommentedToSupportCase: {
                    final CommentedToSupportCase typedEvent = typed(event);

                    final List<SupportCaseView.CommentListItem> comments = new ArrayList<>();
                    for (SupportCaseView.CommentListItem commentListItem : previousData.comments) {
                        comments.add(SupportCaseView.CommentListItem.from(commentListItem.id, commentListItem.commenter, commentListItem.body, commentListItem.commentedAt, commentListItem.lastEditedAt));
                    }

                    final CommenterData commenter = CommenterData.from(typedEvent.commenter);
                    final LocalDateTime commentedAt = LocalDateTime.ofInstant(Instant.ofEpochMilli(typedEvent.dateTimeSourced), TimeZone.getDefault().toZoneId());
                    comments.add(SupportCaseView.CommentListItem.from(typedEvent.id, commenter, typedEvent.body, commentedAt, null));


                    merged = SupportCaseView.from(previousData.id, previousData.tenant, previousData.customer, previousData.categoryId, previousData.subject, previousData.description, previousData.status, previousData.supporter, comments, previousData.events, previousData.openedAt, previousData.assignedAt, previousData.lastEditedAt, previousData.closedAt);
                    break;
                }

                case CommentEdited: {
                    final CommentEdited typedEvent = typed(event);
                    final LocalDateTime lastEditedAt = LocalDateTime.ofInstant(Instant.ofEpochMilli(typedEvent.dateTimeSourced), TimeZone.getDefault().toZoneId());

                    final List<SupportCaseView.CommentListItem> comments =
                            previousData.comments.stream().map(commentListItem -> {
                                if (commentListItem.id.equals(typedEvent.id)) {
                                    return SupportCaseView.CommentListItem.from(commentListItem.id, commentListItem.commenter, typedEvent.body, commentListItem.commentedAt, lastEditedAt);
                                } else {
                                    return commentListItem;
                                }
                            }).collect(Collectors.toList());

                    merged = SupportCaseView.from(previousData.id, previousData.tenant, previousData.customer, previousData.categoryId, previousData.subject, previousData.description, previousData.status, previousData.supporter, comments, previousData.events, previousData.openedAt, previousData.assignedAt, previousData.lastEditedAt, previousData.closedAt);
                    break;
                }

                case CommentMarkAsRemoved: {
                    final CommentMarkAsRemoved typedEvent = typed(event);
                    final List<SupportCaseView.CommentListItem> comments = previousData.comments.stream().filter(commentListItem -> commentListItem.id.equals(typedEvent.id) ? false : true).collect(Collectors.toList());

                    merged = SupportCaseView.from(previousData.id, previousData.tenant, previousData.customer, previousData.categoryId, previousData.subject, previousData.description, previousData.status, previousData.supporter, comments, previousData.events, previousData.openedAt, previousData.assignedAt, previousData.lastEditedAt, previousData.closedAt);
                    break;
                }

                default:
                    logger().warn("Event of type " + event.typeName() + " was not matched.");
                    break;
            }
        }

        return merged;
    }


    private List<EventData> copy(List<EventData> events) {
        if (events.isEmpty())
            return Collections.emptyList();

        final List<EventData> results = new ArrayList<>();
        for (EventData event : events) {
            results.add(EventData.from(event.description, event.occurredAt));
        }
        return results;
    }

}
