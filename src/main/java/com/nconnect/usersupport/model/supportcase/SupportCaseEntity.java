package com.nconnect.usersupport.model.supportcase;

import com.nconnect.usersupport.model.comment.Comment;
import com.nconnect.usersupport.model.comment.CommentEdited;
import com.nconnect.usersupport.model.comment.CommentState;
import io.vlingo.xoom.actors.Stage;
import io.vlingo.xoom.common.Completes;
import com.nconnect.usersupport.model.*;

import io.vlingo.xoom.common.Failure;
import io.vlingo.xoom.common.Outcome;
import io.vlingo.xoom.common.Success;
import io.vlingo.xoom.lattice.model.sourcing.EventSourced;

import java.util.List;
import java.util.stream.Collectors;

/**
 * See <a href="https://docs.vlingo.io/xoom-lattice/entity-cqrs#sourced">EventSourced</a>
 */
public final class SupportCaseEntity extends EventSourced implements SupportCase, TenantOwned {
    private SupportCaseState state;

    public SupportCaseEntity(final String id) {
        super(id);
        this.state = SupportCaseState.identifiedBy(id);
    }

    static {
        EventSourced.registerConsumer(SupportCaseEntity.class, SupportCaseOpened.class, SupportCaseEntity::applySupportCaseOpened);
        EventSourced.registerConsumer(SupportCaseEntity.class, SupportCaseClosed.class, SupportCaseEntity::applySupportCaseClosed);
        EventSourced.registerConsumer(SupportCaseEntity.class, SupportCaseAssigned.class, SupportCaseEntity::applySupportCaseAssigned);
        EventSourced.registerConsumer(SupportCaseEntity.class, SupportCaseEdited.class, SupportCaseEntity::applySupportCaseEdited);
    }

    @Override
    public Completes<Outcome<DomainError, SupportCaseState>> open(final Tenant tenant, final Customer customer, final String categoryId, final String subject, final String description) {
        DomainError error = new DomainError();

        if (customer == null) {
            error.addMessage("A customer is required.");
        }

        if (categoryId == null || categoryId.length() == 0) {
            error.addMessage("A categoryId is required.");
        }

        if (subject == null || subject.length() == 0) {
            error.addMessage("A subject is required.");
        }

        if (subject != null && subject.length() > 255) {
            error.addMessage("The subject must be 255 characters or less.");
        }

        if (description == null || description.length() == 0) {
            error.addMessage("A description is required.");
        }

        if (!error.isEmpty()) {
            return completes().with(Failure.of(error));
        }

        SupportCaseStatus initialStatus = SupportCaseStatus.OPENED;

        return apply(new SupportCaseOpened(state.id, tenant, customer, categoryId, subject, description, initialStatus), () -> Success.of(state));
    }


    @Override
    public Completes<Outcome<DomainError, SupportCaseState>> close(User closingUser) {
        DomainError error = new DomainError();

        if (closingUser == null) {
            error.addMessage("A closing user is required.");
        }
        if (closingUserIsNotAssignedSupporter(closingUser) && closingUserIsNotAuthor(closingUser)) {
            error.addMessage("No authorization.");
        }

        if (this.state.status.equals(SupportCaseStatus.CLOSED)) {
            error.addMessage("The case is closed already.");
        }

        if (!error.isEmpty()) {
            return completes().with(Failure.of(error));
        }

        return apply(new SupportCaseClosed(state.id, SupportCaseStatus.CLOSED, closingUser), () -> Success.of(state));
    }

    @Override
    public Completes<Outcome<DomainError, SupportCaseState>> assign(final Supporter supporter) {
        DomainError error = new DomainError();

        if (supporter == null) {
            error.addMessage("A supporter is required.");
        }

        if (this.state.status.equals(SupportCaseStatus.ASSIGNED)) {
            error.addMessage("The case is assigned already.");
        } else if (this.state.status.equals(SupportCaseStatus.CLOSED)) {
            error.addMessage("The case is closed already.");
        }

        if (!error.isEmpty()) {
            return completes().with(Failure.of(error));
        }

        return apply(new SupportCaseAssigned(state.id, supporter, SupportCaseStatus.ASSIGNED), () -> Success.of(state));
    }

    @Override
    public Completes<Outcome<DomainError, CommentState>> commentFor(final Stage stage, Tenant tenant, Commenter commenter, String commentBody) {
        DomainError error = new DomainError();

        if (commenter == null || commenter.id.isEmpty() || commenter.name.isEmpty()) {
            error.addMessage("A commenter is required.");
        }

        if (this.state.status.equals(SupportCaseStatus.CLOSED)) {
            error.addMessage("The case is closed already.");
        }

        if (!this.state.customer.id.equals(commenter.id) && !this.state.supporter.id.equals(commenter.id)) {
            error.addMessage("No authorization.");
        }

        if (!error.isEmpty()) {
            return completes().with(Failure.of(error));
        }

        Outcome<DomainError, CommentState> commentStateOutcome = Comment.defineWith(stage, tenant, state.id, commenter, commentBody).await();
        return completes().with(commentStateOutcome);
    }

    @Override
    public Completes<Outcome<DomainError, SupportCaseState>> edit(final Customer customer, final String categoryId, final String subject, final String description) {
        DomainError error = new DomainError();

        if (!state.customer.id.equals(customer.id)) {
            error.addMessage("No authorization.");
        }

        if (this.state.status.equals(SupportCaseStatus.CLOSED)) {
            error.addMessage("The case is closed already.");
        }

        if (subject.isEmpty()) {
            error.addMessage("The case subject is required.");
        }

        if (subject != null && subject.length() > 255) {
            error.addMessage("The case subject must be 255 characters or less.");
        }

        if (categoryId.isEmpty()) {
            error.addMessage("The case category is required.");
        }

        if (description.isEmpty()) {
            error.addMessage("The case description is required.");
        }


        if (!error.isEmpty()) {
            return completes().with(Failure.of(error));
        }

        return apply(new SupportCaseEdited(state.id, categoryId, subject, description), () -> Success.of(state));
    }

    private void applySupportCaseOpened(final SupportCaseOpened event) {
        state = state.open(event.id, event.tenant, event.customer, event.categoryId, event.subject, event.description, event.status);
    }

    private void applySupportCaseEdited(final SupportCaseEdited event) {
        state = state.edit(event.categoryId, event.subject, event.description);
    }

    private void applySupportCaseClosed(final SupportCaseClosed event) {
        state = state.close(event.status);
    }

    private void applySupportCaseAssigned(final SupportCaseAssigned event) {
        state = state.assign(event.supporter, event.status);
    }

    /*
     * Restores my initial state by means of {@code state}.
     *
     * @param snapshot the {@code SupportCaseState} holding my state
     * @param currentVersion the int value of my current version; may be helpful in determining if snapshot is needed
     */
    @Override
    @SuppressWarnings("hiding")
    protected <SupportCaseState> void restoreSnapshot(final SupportCaseState snapshot, final int currentVersion) {
        // OVERRIDE FOR SNAPSHOT SUPPORT
        // See: https://docs.vlingo.io/xoom-lattice/entity-cqrs#eventsourced
    }

    /*
     * Answer the valid {@code SupportCaseState} instance if a snapshot should
     * be taken and persisted along with applied {@code Source<T>} instance(s).
     *
     * @return SupportCaseState
     */
    @Override
    @SuppressWarnings("unchecked")
    protected SupportCaseState snapshot() {
        // OVERRIDE FOR SNAPSHOT SUPPORT
        // See: https://docs.vlingo.io/xoom-lattice/entity-cqrs#eventsourced
        return null;
    }

    @Override
    public Completes<Boolean> isOwnedBy(String tenantId) {
        if (state.tenant.id.equals(tenantId)) {
            return this.completes().with(true);
        } else {
            return this.completes().with(false);
        }
    }

    private boolean closingUserIsNotAssignedSupporter(User closingUser) {
        if (this.state.supporter == null) {
            return true;
        }

        return !this.state.supporter.id.equals(closingUser.id);
    }

    private boolean closingUserIsNotAuthor(User closingUser) {
        return !this.state.customer.id.equals(closingUser.id);
    }

}
