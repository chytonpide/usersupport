package com.nconnect.usersupport.model.comment;

import io.vlingo.xoom.actors.World;
import io.vlingo.xoom.actors.testkit.AccessSafely;
import com.nconnect.usersupport.infrastructure.persistence.CommentEditedAdapter;
import io.vlingo.xoom.common.Outcome;
import io.vlingo.xoom.symbio.BaseEntry;
import com.nconnect.usersupport.infrastructure.persistence.CommentedToSupportCaseAdapter;
import com.nconnect.usersupport.infrastructure.persistence.MockDispatcher;
import com.nconnect.usersupport.model.*;
import io.vlingo.xoom.lattice.model.sourcing.SourcedTypeRegistry;
import io.vlingo.xoom.lattice.model.sourcing.SourcedTypeRegistry.Info;
import io.vlingo.xoom.symbio.EntryAdapterProvider;
import io.vlingo.xoom.symbio.store.journal.Journal;
import io.vlingo.xoom.symbio.store.journal.inmemory.InMemoryJournalActor;
import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class CommentEntityTest {

  private World world;
  private Journal<String> journal;
  private MockDispatcher dispatcher;
  private Comment comment;

  @BeforeEach
  @SuppressWarnings({"unchecked", "rawtypes"})
  public void setUp(){
    world = World.startWithDefaults("test-es");

    dispatcher = new MockDispatcher();

    EntryAdapterProvider entryAdapterProvider = EntryAdapterProvider.instance(world);

    entryAdapterProvider.registerAdapter(CommentedToSupportCase.class, new CommentedToSupportCaseAdapter());
    entryAdapterProvider.registerAdapter(CommentEdited.class, new CommentEditedAdapter());

    journal = world.actorFor(Journal.class, InMemoryJournalActor.class, Collections.singletonList(dispatcher));

    new SourcedTypeRegistry(world).register(new Info(journal, CommentEntity.class, CommentEntity.class.getSimpleName()));

    comment = world.actorFor(Comment.class, CommentEntity.class, "#1");
  }

  private static final Tenant TENANT_FOR_ENTITY_FOR_CREATE_TEST = Tenant.from("comment-tenant-id");
  private static final String SUPPORC_CASE_ID_FOR_CREATE_TEST = "1";
  private static final Commenter COMMENTER_FOR_CREATE_TEST = Commenter.from("comment-customer-identity", "comment-customer-name");
  private static final String BODY_FOR_CREATE_TEST = "comment-body";

  @Test
  public void defineWith() throws DomainError {
    final AccessSafely dispatcherAccess = dispatcher.afterCompleting(1);
    final Outcome<DomainError, CommentState> outcome = comment.defineWith(TENANT_FOR_ENTITY_FOR_CREATE_TEST, SUPPORC_CASE_ID_FOR_CREATE_TEST, COMMENTER_FOR_CREATE_TEST, BODY_FOR_CREATE_TEST).await();
    final CommentState state = outcome.get();

    assertEquals(state.tenant.id, "comment-tenant-id");
    assertEquals(state.supportCaseId, "1");
    assertEquals(state.commenter.id, "comment-customer-identity");
    assertEquals(state.commenter.name, "comment-customer-name");
    assertEquals(state.body, "comment-body");
    assertEquals(1, (int) dispatcherAccess.readFrom("entriesCount"));
    assertEquals(CommentedToSupportCase.class.getName(), ((BaseEntry<String>) dispatcherAccess.readFrom("appendedAt", 0)).typeName());
  }

  private static final String BODY_FOR_CHANGE_BODY_TEST = "updated-comment-body";

  @Test
  public void edit() {
    _createEntity();
    final AccessSafely dispatcherAccess = dispatcher.afterCompleting(1);
    final CommentState state = comment.edit(COMMENTER_FOR_ENTITY_CREATION, BODY_FOR_CHANGE_BODY_TEST).await();

    assertEquals(state.commenter.id, "comment-customer-identity");
    assertEquals(state.commenter.name, "comment-customer-name");
    assertEquals(state.body, "updated-comment-body");
    assertEquals(2, (int) dispatcherAccess.readFrom("entriesCount"));
    assertEquals(CommentEdited.class.getName(), ((BaseEntry<String>) dispatcherAccess.readFrom("appendedAt", 1)).typeName());
  }
  private static final Tenant TENANT_FOR_ENTITY_CREATION = Tenant.from("comment-tenant-id");
  private static final String SUPPORT_CASE_ID_FOR_ENTITY_CREATION = "1";
  private static final Commenter COMMENTER_FOR_ENTITY_CREATION = Commenter.from("comment-customer-identity", "comment-customer-name");
  private static final String BODY_FOR_ENTITY_CREATION = "comment-body";

  private void _createEntity() {
    comment.defineWith(TENANT_FOR_ENTITY_CREATION, SUPPORT_CASE_ID_FOR_ENTITY_CREATION, COMMENTER_FOR_ENTITY_CREATION, BODY_FOR_ENTITY_CREATION).await();
  }
}
