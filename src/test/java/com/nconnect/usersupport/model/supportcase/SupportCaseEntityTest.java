package com.nconnect.usersupport.model.supportcase;

import io.vlingo.xoom.actors.World;
import io.vlingo.xoom.actors.testkit.AccessSafely;
import io.vlingo.xoom.common.Outcome;
import io.vlingo.xoom.symbio.BaseEntry;
import com.nconnect.usersupport.infrastructure.persistence.SupportCaseClosedAdapter;
import com.nconnect.usersupport.infrastructure.persistence.SupportCaseOpenedAdapter;
import com.nconnect.usersupport.infrastructure.persistence.SupportCaseAssignedAdapter;
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

public class SupportCaseEntityTest {

  private World world;
  private Journal<String> journal;
  private MockDispatcher dispatcher;
  private SupportCase supportCase;

  @BeforeEach
  @SuppressWarnings({"unchecked", "rawtypes"})
  public void setUp(){
    world = World.startWithDefaults("test-es");

    dispatcher = new MockDispatcher();

    EntryAdapterProvider entryAdapterProvider = EntryAdapterProvider.instance(world);

    entryAdapterProvider.registerAdapter(SupportCaseOpened.class, new SupportCaseOpenedAdapter());
    entryAdapterProvider.registerAdapter(SupportCaseClosed.class, new SupportCaseClosedAdapter());
    entryAdapterProvider.registerAdapter(SupportCaseAssigned.class, new SupportCaseAssignedAdapter());

    journal = world.actorFor(Journal.class, InMemoryJournalActor.class, Collections.singletonList(dispatcher));

    new SourcedTypeRegistry(world).register(new Info(journal, SupportCaseEntity.class, SupportCaseEntity.class.getSimpleName()));

    //final SupportCaseBox _supportCase = stage.actorFor(SupportCaseBox.class, Definition.has(SupportCaseEntity.class, Definition.parameters(_address.idString())), _address);

    supportCase = world.actorFor(SupportCase.class, SupportCaseEntity.class, "#1");
  }

  private static final Tenant TENANT_FOR_OPEN_TEST = Tenant.from("supportcase-tenant-id");
  private static final Customer CUSTOMER_FOR_OPEN_TEST = Customer.from("supportcase-user-identity", "supportcase-user-name");
  private static final String CATEGORY_ID_FOR_OPEN_TEST = "supportcase-categoryId";
  private static final String SUBJECT_FOR_OPEN_TEST = "supportcase-subject";
  private static final String DESCRIPTION_FOR_OPEN_TEST = "supportcase-description";

  @Test
  public void open() throws DomainError {
    final AccessSafely dispatcherAccess = dispatcher.afterCompleting(1);
    final Outcome<DomainError, SupportCaseState> outcome = supportCase.open(TENANT_FOR_OPEN_TEST, CUSTOMER_FOR_OPEN_TEST, CATEGORY_ID_FOR_OPEN_TEST, SUBJECT_FOR_OPEN_TEST, DESCRIPTION_FOR_OPEN_TEST).await();
    final SupportCaseState state = outcome.get();

    assertEquals(state.tenant.id, "supportcase-tenant-id");
    assertEquals(state.customer.id, "supportcase-user-identity");
    assertEquals(state.customer.name, "supportcase-user-name");
    assertEquals(state.categoryId, "supportcase-categoryId");
    assertEquals(state.subject, "supportcase-subject");
    assertEquals(state.description, "supportcase-description");
    assertEquals(1, (int) dispatcherAccess.readFrom("entriesCount"));
    assertEquals(SupportCaseOpened.class.getName(), ((BaseEntry<String>) dispatcherAccess.readFrom("appendedAt", 0)).typeName());
  }

  private static final String ID_FOR_CLOSE_TEST = "updated-1";

  private static final Supporter SUPPORTER_FOR_CLOSE_TEST = Supporter.from("updated-supportcase-supporter-identity", "updated-supportcase-supporter-name");
  private static final User USER_FOR_CLOSE_TEST = User.from("updated-supportcase-supporter-identity", "updated-supportcase-supporter-name");

  @Test
  public void close() throws DomainError {
    _createEntity();
    final AccessSafely dispatcherAccess = dispatcher.afterCompleting(2);
    supportCase.assign(SUPPORTER_FOR_CLOSE_TEST).await();

    final Outcome<DomainError, SupportCaseState>  outcome = supportCase.close(USER_FOR_CLOSE_TEST).await();
    final SupportCaseState state = outcome.get();

    assertEquals(SupportCaseStatus.CLOSED, state.status);
    assertEquals(3, (int) dispatcherAccess.readFrom("entriesCount"));
    assertEquals(SupportCaseClosed.class.getName(), ((BaseEntry<String>) dispatcherAccess.readFrom("appendedAt", 1)).typeName());
  }

  private static final Supporter SUPPORTER_FOR_ASSIGN_TEST = Supporter.from("updated-supportcase-supporter-identity", "updated-supportcase-supporter-name");

  @Test
  public void assign() throws DomainError {
    _createEntity();
    final AccessSafely dispatcherAccess = dispatcher.afterCompleting(1);
    final Outcome<DomainError, SupportCaseState>  outcome = supportCase.assign(SUPPORTER_FOR_ASSIGN_TEST).await();
    final SupportCaseState state = outcome.get();

    assertEquals(state.customer.id, "supportcase-user-identity");
    assertEquals(state.customer.name, "supportcase-user-name");
    assertEquals(state.categoryId, "supportcase-categoryId");
    assertEquals(state.subject, "supportcase-subject");
    assertEquals(state.description, "supportcase-description");
    assertEquals(state.supporter.id, "updated-supportcase-supporter-identity");
    assertEquals(state.supporter.name, "updated-supportcase-supporter-name");
    assertEquals(2, (int) dispatcherAccess.readFrom("entriesCount"));
    assertEquals(SupportCaseAssigned.class.getName(), ((BaseEntry<String>) dispatcherAccess.readFrom("appendedAt", 1)).typeName());
  }


  @Test
  @Disabled
  public void userComment() {
    /**
     * TODO: Unable to generate tests for method userComment. See {@link SupportCaseEntity#userComment()}
     */
  }


  @Test
  @Disabled
  public void asigineeComment() {
    /**
     * TODO: Unable to generate tests for method asigineeComment. See {@link SupportCaseEntity#asigineeComment()}
     */
  }


  @Test
  @Disabled
  public void edit() {
    /**
     * TODO: Unable to generate tests for method changeContent. See {@link SupportCaseEntity#changeContent()}
     */
  }

  private static final String ID_FOR_ENTITY_CREATION = "1";
  private static final Tenant TENANT_FOR_ENTITY_CREATION = Tenant.from("supportcase-tenant-id");
  private static final Customer CUSTOMER_FOR_ENTITY_CREATION = Customer.from("supportcase-user-identity", "supportcase-user-name");
  private static final String CATEGORY_ID_FOR_ENTITY_CREATION = "supportcase-categoryId";
  private static final String SUBJECT_FOR_ENTITY_CREATION = "supportcase-subject";
  private static final String DESCRIPTION_FOR_ENTITY_CREATION = "supportcase-description";

  private void _createEntity() {
    supportCase.open(TENANT_FOR_ENTITY_CREATION, CUSTOMER_FOR_ENTITY_CREATION, CATEGORY_ID_FOR_ENTITY_CREATION, SUBJECT_FOR_ENTITY_CREATION, DESCRIPTION_FOR_ENTITY_CREATION).await();
  }
}
