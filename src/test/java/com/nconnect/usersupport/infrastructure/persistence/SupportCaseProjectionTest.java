package com.nconnect.usersupport.infrastructure.persistence;

import com.nconnect.usersupport.model.Commenter;
import com.nconnect.usersupport.model.Tenant;
import com.nconnect.usersupport.model.User;
import com.nconnect.usersupport.model.comment.CommentMarkAsRemoved;
import com.nconnect.usersupport.model.comment.CommentState;
import com.nconnect.usersupport.model.comment.CommentedToSupportCase;
import io.vlingo.xoom.actors.World;
import io.vlingo.xoom.actors.testkit.AccessSafely;
import io.vlingo.xoom.common.serialization.JsonSerialization;
import io.vlingo.xoom.lattice.model.projection.Projectable;
import io.vlingo.xoom.lattice.model.projection.Projection;
import io.vlingo.xoom.lattice.model.projection.TextProjectable;
import io.vlingo.xoom.lattice.model.stateful.StatefulTypeRegistry;
import io.vlingo.xoom.symbio.BaseEntry;
import io.vlingo.xoom.symbio.Metadata;
import io.vlingo.xoom.symbio.store.dispatch.NoOpDispatcher;
import io.vlingo.xoom.symbio.store.state.StateStore;
import io.vlingo.xoom.symbio.store.state.inmemory.InMemoryStateStoreActor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import com.nconnect.usersupport.infrastructure.*;
import com.nconnect.usersupport.model.supportcase.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class SupportCaseProjectionTest {

  private World world;
  private StateStore stateStore;
  private Projection projection;
  private Map<String, String> valueToProjectionId;

  @BeforeEach
  public void setUp() {
    world = World.startWithDefaults("test-state-store-projection");
    NoOpDispatcher dispatcher = new NoOpDispatcher();
    valueToProjectionId = new ConcurrentHashMap<>();
    stateStore = world.actorFor(StateStore.class, InMemoryStateStoreActor.class, Collections.singletonList(dispatcher));
    StatefulTypeRegistry statefulTypeRegistry = StatefulTypeRegistry.registerAll(world, stateStore, SupportCaseView.class);
    QueryModelStateStoreProvider.using(world.stage(), statefulTypeRegistry);
    projection = world.actorFor(Projection.class, SupportCaseProjectionActor.class, stateStore);
  }

  @Test
  public void open() {
    // given
    int entryVersion = 1;
    final String supportCaseId = "first-supportcase-id";
    final SupportCaseState dataForProjection = SupportCaseStateStub.firstInstance(supportCaseId);
    final Projectable projectable = createSupportCaseOpenedProjectable(dataForProjection, entryVersion);
    entryVersion = ++entryVersion;

    // when
    final CountingProjectionControl control = new CountingProjectionControl();
    final AccessSafely access = control.afterCompleting(1);
    projection.projectWith(projectable, control);
    final Map<String, Integer> confirmations = access.readFrom("confirmations");

    assertEquals(1, confirmations.size());
    assertEquals(1, valueOfProjectionIdFor(dataForProjection.id, confirmations));

    CountingReadResultInterest interest = new CountingReadResultInterest();
    AccessSafely interestAccess = interest.afterCompleting(1);
    stateStore.read(dataForProjection.id, SupportCaseView.class, interest);
    SupportCaseView readModel = interestAccess.readFrom("item", dataForProjection.id);

    // then
    assertEquals(dataForProjection.id, readModel.id);
    assertEquals(SupportCaseStatus.OPENED, readModel.status);
    assertEquals(dataForProjection.customer.id, readModel.customer.id);
    assertEquals(dataForProjection.customer.name, readModel.customer.name);
    assertEquals(dataForProjection.categoryId, readModel.categoryId);
    assertEquals(dataForProjection.subject, readModel.subject);
    assertEquals(dataForProjection.description, readModel.description);
  }

  @Test
  public void assign() {
    // given
    int entryVersion = 1;
    final String id = "1";
    final SupportCaseState dataForProjection = SupportCaseStateStub.firstInstance(id);
    registerSupportCase(dataForProjection, entryVersion);
    entryVersion = ++entryVersion;
    final Projectable projectable = createSupportCaseAssignedProjectable(dataForProjection, entryVersion);

    assertNotEquals(dataForProjection.status, SupportCaseStatus.ASSIGNED);


    // when
    final CountingProjectionControl control = new CountingProjectionControl();
    final AccessSafely access = control.afterCompleting(1);
    projection.projectWith(projectable, control);
    final Map<String, Integer> confirmations = access.readFrom("confirmations");

    assertEquals(1, confirmations.size());
    assertEquals(1, valueOfProjectionIdFor(dataForProjection.id, confirmations));

    CountingReadResultInterest interest = new CountingReadResultInterest();
    AccessSafely interestAccess = interest.afterCompleting(1);
    stateStore.read(dataForProjection.id, SupportCaseView.class, interest);
    SupportCaseView readModel = interestAccess.readFrom("item", dataForProjection.id);

    // then
    assertEquals(dataForProjection.id, readModel.id);
    assertEquals(SupportCaseStatus.ASSIGNED, readModel.status);
    assertEquals(dataForProjection.supporter.id, readModel.supporter.id);
    assertEquals(dataForProjection.supporter.name, readModel.supporter.name);
  }

  @Test
  public void close() {
    // given
    int entryVersion = 1;
    final String id = "first-supoort-case-id";
    final SupportCaseState dataForProjection = SupportCaseStateStub.firstInstance(id);
    registerSupportCase(dataForProjection, 1);
    entryVersion = ++entryVersion;

    final User closingUserData = User.from("user-id","user-name");
    final Projectable projectable = createSupportCaseClosedProjectable(dataForProjection, closingUserData, entryVersion);

    assertNotEquals(dataForProjection.status, SupportCaseStatus.CLOSED);

    // when
    final CountingProjectionControl control = new CountingProjectionControl();
    final AccessSafely access = control.afterCompleting(1);
    projection.projectWith(projectable, control);
    final Map<String, Integer> confirmations = access.readFrom("confirmations");

    assertEquals(1, confirmations.size());
    assertEquals(1, valueOfProjectionIdFor(dataForProjection.id, confirmations));

    CountingReadResultInterest interest = new CountingReadResultInterest();
    AccessSafely interestAccess = interest.afterCompleting(1);
    stateStore.read(dataForProjection.id, SupportCaseView.class, interest);
    SupportCaseView readModel = interestAccess.readFrom("item", dataForProjection.id);

    // then
    assertEquals(dataForProjection.id, readModel.id);
    assertEquals(SupportCaseStatus.CLOSED, readModel.status);
  }

  @Test
  public void edit() {
    // given
    int entryVersion = 1;
    final String id = "1";
    final SupportCaseState firstDataForProjection = SupportCaseStateStub.firstInstance(id);
    registerSupportCase(firstDataForProjection, entryVersion);
    entryVersion = ++entryVersion;

    final SupportCaseState secondDataForProjection = SupportCaseStateStub.secondInstance(id);
    final Projectable projectable = createSupportCaseEditedProjectable(secondDataForProjection, entryVersion);

    // when
    final CountingProjectionControl control = new CountingProjectionControl();
    final AccessSafely access = control.afterCompleting(1);
    projection.projectWith(projectable, control);
    final Map<String, Integer> confirmations = access.readFrom("confirmations");

    assertEquals(1, confirmations.size());
    assertEquals(1, valueOfProjectionIdFor(firstDataForProjection.id, confirmations));

    CountingReadResultInterest interest = new CountingReadResultInterest();
    AccessSafely interestAccess = interest.afterCompleting(1);
    stateStore.read(firstDataForProjection.id, SupportCaseView.class, interest);
    SupportCaseView readModel = interestAccess.readFrom("item", id);

    // then
    assertEquals(id, readModel.id);
    assertEquals(secondDataForProjection.subject, readModel.subject);
    assertEquals(secondDataForProjection.description, readModel.description);
    assertEquals(secondDataForProjection.categoryId, readModel.categoryId);
  }

  @Test
  public void commentToSupportCase() {
    // given
    int entryVersion = 1;
    final String supportCaseId = "1";
    final SupportCaseState supportCaseDataForProjection = SupportCaseStateStub.firstInstance(supportCaseId);
    registerSupportCase(supportCaseDataForProjection, entryVersion);
    entryVersion = ++entryVersion;

    final CommentState commentDataForProjection = CommentStateStub.firstInstance("first-comment-supportCaseId", supportCaseId);
    final Projectable projectable = createCommentedToSupportCaseProjectable(commentDataForProjection, entryVersion);
    entryVersion = ++entryVersion;

    // when
    final CountingProjectionControl control = new CountingProjectionControl();
    final AccessSafely access = control.afterCompleting(1);
    projection.projectWith(projectable, control);
    final Map<String, Integer> confirmations = access.readFrom("confirmations");

    assertEquals(1, confirmations.size());
    assertEquals(1, valueOfProjectionIdFor(commentDataForProjection.id, confirmations));

    CountingReadResultInterest interest = new CountingReadResultInterest();
    AccessSafely interestAccess = interest.afterCompleting(1);
    stateStore.read(supportCaseDataForProjection.id, SupportCaseView.class, interest);
    SupportCaseView supportCaseView = interestAccess.readFrom("item", supportCaseId);

    // then
    assertEquals(1, supportCaseView.comments.size());
    assertEquals("first-comment-body", supportCaseView.comments.get(0).body);
  }

  @Test
  public void editComment() {
    // TODO: implementation
  }

  @Test
  public void markAsRemovedComment() {
    // given
    int entryVersion = 1;
    final String supportCaseId = "first-supportcase-id";
    final SupportCaseState supportCaseDataForProjection = SupportCaseStateStub.firstInstance(supportCaseId);
    registerSupportCase(supportCaseDataForProjection, entryVersion);
    entryVersion = ++entryVersion;

    final String commentId = "first-comment-id";
    final CommentState commentDataForProjection = CommentStateStub.firstInstance(commentId, supportCaseId);
    registerComment(commentDataForProjection, entryVersion);
    entryVersion = ++entryVersion;

    CountingReadResultInterest interest = new CountingReadResultInterest();
    AccessSafely interestAccess = interest.afterCompleting(1);
    stateStore.read(supportCaseDataForProjection.id, SupportCaseView.class, interest);
    SupportCaseView supportCaseView = interestAccess.readFrom("item", supportCaseId);

    assertEquals(1, supportCaseView.comments.size());


    final Projectable projectable = createCommentMarkAsRemovedProjectable(commentDataForProjection, 3);

    // when
    final CountingProjectionControl control = new CountingProjectionControl();
    final AccessSafely access = control.afterCompleting(1);
    projection.projectWith(projectable, control);
    final Map<String, Integer> confirmations = access.readFrom("confirmations");

    assertEquals(1, confirmations.size());
    assertEquals(1, valueOfProjectionIdFor(commentDataForProjection.id, confirmations));


    interest = new CountingReadResultInterest();
    interestAccess = interest.afterCompleting(1);
    stateStore.read(supportCaseDataForProjection.id, SupportCaseView.class, interest);
    SupportCaseView supportCaseView2 = interestAccess.readFrom("item", supportCaseId);

    // then
    assertEquals(0, supportCaseView2.comments.size());
  }

  private void registerSupportCase(SupportCaseState data, int entryVersion) {
    final CountingProjectionControl control = new CountingProjectionControl();
    final AccessSafely access = control.afterCompleting(1);
    projection.projectWith(createSupportCaseOpenedProjectable(data, entryVersion), control);
    access.readFrom("confirmations");
  }

  private void registerComment(CommentState data, int entryVersion) {
    final CountingProjectionControl control = new CountingProjectionControl();
    final AccessSafely access = control.afterCompleting(1);
    projection.projectWith(createCommentedToSupportCaseProjectable(data, entryVersion), control);
    access.readFrom("confirmations");
  }

  private int valueOfProjectionIdFor(final String valueText, final Map<String, Integer> confirmations) {
    return confirmations.get(valueToProjectionId.get(valueText));
  }

  private Projectable createSupportCaseOpenedProjectable(SupportCaseState data, int entryVersion) {
    final SupportCaseOpened eventData = new SupportCaseOpened(data.id, data.tenant, data.customer, data.categoryId, data.subject, data.description, SupportCaseStatus.OPENED);

    BaseEntry.TextEntry textEntry = new BaseEntry.TextEntry(eventData.getClass(), 1, JsonSerialization.serialized(eventData), entryVersion, Metadata.withObject(eventData));

    final String projectionId = UUID.randomUUID().toString();
    valueToProjectionId.put(data.id, projectionId);

    return new TextProjectable(null, Collections.singletonList(textEntry), projectionId);
  }

  private Projectable createSupportCaseAssignedProjectable(SupportCaseState data, int entryVersion) {
    final SupportCaseAssigned eventData = new SupportCaseAssigned(data.id, data.supporter, SupportCaseStatus.ASSIGNED);

    BaseEntry.TextEntry textEntry = new BaseEntry.TextEntry(eventData.getClass(), 1, JsonSerialization.serialized(eventData), entryVersion, Metadata.withObject(eventData));

    final String projectionId = UUID.randomUUID().toString();
    valueToProjectionId.put(data.id, projectionId);

    return new TextProjectable(null, Collections.singletonList(textEntry), projectionId);
  }

  private Projectable createSupportCaseClosedProjectable(SupportCaseState data, User closingUser, int entryVersion) {
    final SupportCaseClosed eventData = new SupportCaseClosed(data.id, SupportCaseStatus.CLOSED, closingUser);

    BaseEntry.TextEntry textEntry = new BaseEntry.TextEntry(eventData.getClass(), 1, JsonSerialization.serialized(eventData), entryVersion, Metadata.withObject(eventData));

    final String projectionId = UUID.randomUUID().toString();
    valueToProjectionId.put(data.id, projectionId);

    return new TextProjectable(null, Collections.singletonList(textEntry), projectionId);
  }

  private Projectable createSupportCaseEditedProjectable(SupportCaseState data, int entryVersion) {
    final SupportCaseEdited eventData = new SupportCaseEdited(data.id, data.categoryId, data.subject, data.description);

    BaseEntry.TextEntry textEntry = new BaseEntry.TextEntry(eventData.getClass(), 1, JsonSerialization.serialized(eventData), entryVersion, Metadata.withObject(eventData));

    final String projectionId = UUID.randomUUID().toString();
    valueToProjectionId.put(data.id, projectionId);

    return new TextProjectable(null, Collections.singletonList(textEntry), projectionId);
  }

  private Projectable createCommentedToSupportCaseProjectable(CommentState state, int entryVersion) {
    final CommentedToSupportCase eventData = new CommentedToSupportCase(state.id, state.tenant, state.supportCaseId, state.commenter, state.body);

    BaseEntry.TextEntry textEntry = new BaseEntry.TextEntry(eventData.getClass(), 1, JsonSerialization.serialized(eventData), entryVersion, Metadata.withObject(eventData));

    final String projectionId = UUID.randomUUID().toString();
    valueToProjectionId.put(state.id, projectionId);

    return new TextProjectable(null, Collections.singletonList(textEntry), projectionId);
  }

  private Projectable createCommentMarkAsRemovedProjectable(CommentState state, int entryVersion) {
    final CommentMarkAsRemoved eventData = new CommentMarkAsRemoved(state.id, state.supportCaseId);

    BaseEntry.TextEntry textEntry = new BaseEntry.TextEntry(eventData.getClass(), 1, JsonSerialization.serialized(eventData), entryVersion, Metadata.withObject(eventData));

    final String projectionId = UUID.randomUUID().toString();
    valueToProjectionId.put(state.id, projectionId);

    return new TextProjectable(null, Collections.singletonList(textEntry), projectionId);
  }
}
