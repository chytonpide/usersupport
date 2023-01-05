package com.nconnect.usersupport.infrastructure.persistence;

import com.nconnect.usersupport.model.supportcase.SupportCaseStatus;
import io.vlingo.xoom.actors.World;
import io.vlingo.xoom.common.Outcome;
import io.vlingo.xoom.lattice.model.stateful.StatefulTypeRegistry;
import io.vlingo.xoom.symbio.Source;
import io.vlingo.xoom.symbio.store.Result;
import io.vlingo.xoom.symbio.store.StorageException;
import io.vlingo.xoom.symbio.store.dispatch.NoOpDispatcher;
import io.vlingo.xoom.symbio.store.state.StateStore;
import io.vlingo.xoom.symbio.store.state.inmemory.InMemoryStateStoreActor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.nconnect.usersupport.infrastructure.*;

import java.time.LocalDateTime;
import java.util.*;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.*;

public class SupportCaseQueriesTest {

  private World world;
  private StateStore stateStore;
  private SupportCaseQueries queries;

  @BeforeEach
  public void setUp(){
    world = World.startWithDefaults("test-state-store-query");
    stateStore = world.actorFor(StateStore.class, InMemoryStateStoreActor.class, Collections.singletonList(new NoOpDispatcher()));
    StatefulTypeRegistry.registerAll(world, stateStore, SupportCaseView.class);
    queries = world.actorFor(SupportCaseQueries.class, SupportCaseQueriesActor.class, stateStore);
  }

  private static final List<SupportCaseView.CommentListItem> FIRST_QUERY_BY_ID_COMMENT_VIEW_LIST = Arrays.asList(new SupportCaseView.CommentListItem[]{
          SupportCaseView.CommentListItem.from("first-comment-id-1", CommenterData.from("id","name"), "body", LocalDateTime.now(), null),
          SupportCaseView.CommentListItem.from("first-comment-id-2", CommenterData.from("id","name"), "body", LocalDateTime.now(), null)});

  private static final List<EventData> FIRST_QUERY_BY_ID_EVENT_DATA_LIST = Collections.emptyList();

  private static final SupportCaseView FIRST_QUERY_BY_ID_TEST_DATA = SupportCaseView.from(
          "1",
          TenantData.from("tenant-id"),
          CustomerData.from("first-supportcase-user-identity", "first-supportcase-user-name"),
          "first-supportcase-categoryId",
          "first-supportcase-subject",
          "first-supportcase-description",
          SupportCaseStatus.OPENED,
          SupporterData.from("first-supportcase-supporter-identity", "first-supportcase-supporter-name"),
          FIRST_QUERY_BY_ID_COMMENT_VIEW_LIST,
          FIRST_QUERY_BY_ID_EVENT_DATA_LIST,
          LocalDateTime.now(),
          null,
          null,
          null);

  private static final List<SupportCaseView.CommentListItem> SECOND_QUERY_BY_ID_COMMENT_VIEW_LIST = Arrays.asList(new SupportCaseView.CommentListItem[]{
          SupportCaseView.CommentListItem.from("second-comment-id-1", CommenterData.from("id","name"), "body", LocalDateTime.now(), null),
          SupportCaseView.CommentListItem.from("second-comment-id-2", CommenterData.from("id","name"), "body", LocalDateTime.now(), null)});

  private static final List<EventData> SECOND_QUERY_BY_ID_EVENT_DATA_LIST = Collections.emptyList();

  private static final SupportCaseView SECOND_QUERY_BY_ID_TEST_DATA = SupportCaseView.from(
          "2",
          TenantData.from("tenant-id"),
          CustomerData.from("second-supportcase-user-identity", "second-supportcase-user-name"),
          "second-supportcase-categoryId",
          "second-supportcase-subject",
          "second-supportcase-description",
          SupportCaseStatus.OPENED,
          SupporterData.from("second-supportcase-supporter-identity", "second-supportcase-supporter-name"),
          SECOND_QUERY_BY_ID_COMMENT_VIEW_LIST,
          SECOND_QUERY_BY_ID_EVENT_DATA_LIST,
          LocalDateTime.now(),
          null,
          null,
          null);


  @Test
  public void queryById() {

    stateStore.write("1", FIRST_QUERY_BY_ID_TEST_DATA, 1, NOOP_WRITER);
    stateStore.write("2", SECOND_QUERY_BY_ID_TEST_DATA, 1, NOOP_WRITER);

    final SupportCaseView firstView = queries.supportCaseOf("tenant-id", "1").await();

    assertEquals(firstView.id, "1");
    assertEquals(firstView.tenant.id, "tenant-id");
    assertEquals(firstView.customer.id, "first-supportcase-user-identity");
    assertEquals(firstView.customer.name, "first-supportcase-user-name");
    assertEquals(firstView.categoryId, "first-supportcase-categoryId");
    assertEquals(firstView.subject, "first-supportcase-subject");
    assertEquals(firstView.description, "first-supportcase-description");
    assertEquals(firstView.status, SupportCaseStatus.OPENED);
    assertEquals(firstView.supporter.id, "first-supportcase-supporter-identity");
    assertEquals(firstView.supporter.name, "first-supportcase-supporter-name");
    assertEquals(firstView.comments.get(0).id, "first-comment-id-1");
    assertEquals(firstView.comments.get(1).id, "first-comment-id-2");

    final SupportCaseView secondView = queries.supportCaseOf("tenant-id","2").await();

    assertEquals(secondView.id, "2");
    assertEquals(secondView.tenant.id, "tenant-id");
    assertEquals(secondView.customer.id, "second-supportcase-user-identity");
    assertEquals(secondView.customer.name, "second-supportcase-user-name");
    assertEquals(secondView.categoryId, "second-supportcase-categoryId");
    assertEquals(secondView.subject, "second-supportcase-subject");
    assertEquals(secondView.description, "second-supportcase-description");
    assertEquals(secondView.status, SupportCaseStatus.OPENED);
    assertEquals(secondView.supporter.id, "second-supportcase-supporter-identity");
    assertEquals(secondView.supporter.name, "second-supportcase-supporter-name");
    assertEquals(secondView.comments.get(0).id, "second-comment-id-1");
    assertEquals(secondView.comments.get(1).id, "second-comment-id-2");
  }

  private static final SupportCaseView FIRST_QUERY_ALL_TEST_DATA = SupportCaseView.from("1", TenantData.from("tenant-id"), CustomerData.from("first-supportcase-user-identity", "first-supportcase-user-name"), "first-supportcase-categoryId", "first-supportcase-subject", "first-supportcase-description", SupportCaseStatus.OPENED, SupporterData.from("first-supportcase-supporter-identity", "first-supportcase-supporter-name"),emptyList(),emptyList(), LocalDateTime.now(), null, null, null);
  private static final SupportCaseView SECOND_QUERY_ALL_TEST_DATA = SupportCaseView.from("2", TenantData.from("tenant-id"), CustomerData.from("second-supportcase-user-identity", "second-supportcase-user-name"), "second-supportcase-categoryId", "second-supportcase-subject", "second-supportcase-description", SupportCaseStatus.OPENED, SupporterData.from("second-supportcase-supporter-identity", "second-supportcase-supporter-name"),emptyList(),emptyList(), LocalDateTime.now(), null, null, null);

  @Disabled("streamSomeUsing is not implemented on InMemoryStateStoreActor")
  @Test
  public void queryList() {
    stateStore.write("1", FIRST_QUERY_ALL_TEST_DATA, 1, NOOP_WRITER);
    stateStore.write("2", SECOND_QUERY_ALL_TEST_DATA, 1, NOOP_WRITER);

    final SupportCasesView listView = queries.supportCases("tenant-id", 0, 20).await();
    final SupportCasesView.ListItem firstListItem = listView.items.stream().filter(item -> item.id.equals("1")).findFirst().orElseThrow(RuntimeException::new);

    assertEquals(firstListItem.id, "1");
    assertEquals(firstListItem.customer.id, "first-supportcase-user-identity");
    assertEquals(firstListItem.customer.name, "first-supportcase-user-name");
    assertEquals(firstListItem.categoryId, "first-supportcase-categoryId");
    assertEquals(firstListItem.subject, "first-supportcase-subject");
    assertEquals(firstListItem.status, SupportCaseStatus.OPENED);
    assertEquals(firstListItem.supporter.id, "first-supportcase-supporter-identity");
    assertEquals(firstListItem.supporter.name, "first-supportcase-supporter-name");

    final SupportCasesView.ListItem secondListItem = listView.items.stream().filter(item -> item.id.equals("2")).findFirst().orElseThrow(RuntimeException::new);

    assertEquals(secondListItem.id, "2");
    assertEquals(secondListItem.customer.id, "second-supportcase-user-identity");
    assertEquals(secondListItem.customer.name, "second-supportcase-user-name");
    assertEquals(secondListItem.categoryId, "second-supportcase-categoryId");
    assertEquals(secondListItem.subject, "second-supportcase-subject");
    assertEquals(secondListItem.status, SupportCaseStatus.OPENED);
    assertEquals(secondListItem.supporter.id, "second-supportcase-supporter-identity");
    assertEquals(secondListItem.supporter.name, "second-supportcase-supporter-name");
  }

  @Test
  public void supportCaseOfEmptyResult(){
    final SupportCaseView result = queries.supportCaseOf("none","none").await();
    assertEquals("", result.id);
  }

  private static final StateStore.WriteResultInterest NOOP_WRITER = new StateStore.WriteResultInterest() {
    @Override
    public <S, C> void writeResultedIn(Outcome<StorageException, Result> outcome, String s, S s1, int i, List<Source<C>> list, Object o) {

    }
  };
}