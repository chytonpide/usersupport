package com.nconnect.usersupport.infrastructure.resource;

import com.nconnect.usersupport.infrastructure.SupportCaseView;
import com.nconnect.usersupport.infrastructure.SupportCasesView;
import com.nconnect.usersupport.infrastructure.persistence.QueryModelStateStoreProvider;
import com.nconnect.usersupport.infrastructure.persistence.SupportCaseQueries;
import com.nconnect.usersupport.model.*;
import com.nconnect.usersupport.model.supportcase.SupportCase;
import com.nconnect.usersupport.model.supportcase.SupportCaseEntity;
import com.nconnect.usersupport.model.supportcase.SupportCaseStatus;
import io.vlingo.xoom.actors.Logger;
import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.http.ContentType;
import io.vlingo.xoom.http.Response;
import io.vlingo.xoom.http.resource.DynamicResourceHandler;
import io.vlingo.xoom.http.resource.Resource;
import io.vlingo.xoom.lattice.grid.Grid;
import io.vlingo.xoom.turbo.ComponentRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static io.vlingo.xoom.common.serialization.JsonSerialization.serialized;
import static io.vlingo.xoom.http.Response.Status.*;
import static io.vlingo.xoom.http.resource.ResourceBuilder.resource;

/**
 * See <a href="https://docs.vlingo.io/xoom-turbo/xoom-annotations#resourcehandlers">@ResourceHandlers</a>
 */
public class DemoResource extends DynamicResourceHandler {
    private final static int DEMO_SUPPORT_CASES_COUNT = 20;
    private final Random rand;

    private final Grid $stage;
    private final Logger $logger;
    private final SupportCaseQueries $queries;
    private final TenantOwnedModelResolver<SupportCase, SupportCaseEntity> resolver;

    public DemoResource(final Grid grid) {
        super(grid.world().stage());
        this.$stage = grid;
        this.$logger = super.logger();
        this.$queries = ComponentRegistry.withType(QueryModelStateStoreProvider.class).supportCaseQueries;
        this.resolver = new TenantOwnedModelResolver(grid, SupportCase.class, SupportCaseEntity.class);
        this.rand = new Random();
    }

    public Completes<Response> provisionDemo(DemoProvisioningData data) {
        List<Customer> customers = data.users.stream()
                .filter((demoUser) -> demoUser.role.equals(DemoUserData.Role.CUSTOMER))
                .map((demoUser) -> Customer.from(demoUser.id, demoUser.name)).collect(Collectors.toList());

        List<Supporter> supporters = data.users.stream()
                .filter((demoUser) -> demoUser.role.equals(DemoUserData.Role.SUPPORTER))
                .map((demoUser) -> Supporter.from(demoUser.id, demoUser.name)).collect(Collectors.toList());

        Tenant tenant = Tenant.from(data.tenant.id);

        provisionDemoSupportCases(tenant, customers);
        provisionDemoAssignedSupportCases(tenant, supporters);
        provisionDemoComments(tenant);
        provisionDemoClosedSupportCases(tenant);

        return Completes.withSuccess(entityResponseOf(Created, CorsNegotiation.headers(), ""));
    }

    @Override
    public Resource<?> routes() {
        return resource("DemoResourceHandler", io.vlingo.xoom.http.resource.ResourceBuilder.options("/demo-provisioning").handle(CorsNegotiation::response), io.vlingo.xoom.http.resource.ResourceBuilder.post("/demo-provisioning").body(DemoProvisioningData.class).handle(this::provisionDemo));
    }

    @Override
    protected ContentType contentType() {
        return ContentType.of("application/json", "charset=UTF-8");
    }

    private void provisionDemoSupportCases(Tenant tenant, List<Customer> customers) {
        for(int i = 0; i< DEMO_SUPPORT_CASES_COUNT; i++) {
            Customer customer = customers.get(rand.nextInt(customers.size()));
            SupportCase.open($stage, tenant, customer, "categoryId", "subject", "description").await();
        }
    }

    private void provisionDemoAssignedSupportCases(Tenant tenant, List<Supporter> supporters) {
        SupportCasesView listView = $queries.supportCases(tenant.id, 0, DEMO_SUPPORT_CASES_COUNT).await();
        for(int i=0; i<listView.items.size()-2; i++) {
            SupportCasesView.ListItem item = listView.items.get(i);
            Supporter supporter = supporters.get(rand.nextInt(supporters.size()));
            resolver.resolve(tenant.id, item.id)
                    .andThenTo(supportCase -> supportCase.assign(supporter)).await();
        }
    }

    private void provisionDemoComments(Tenant tenant) {
        SupportCasesView listView = $queries.supportCases(tenant.id, 0, DEMO_SUPPORT_CASES_COUNT).await();
        for(SupportCasesView.ListItem item : listView.items) {
            SupportCaseView view = $queries.supportCaseOf(tenant.id, item.id).await();

            if(view.status.equals(SupportCaseStatus.ASSIGNED)) {
                Commenter commenter = Commenter.from(view.supporter.id, view.supporter.name);
                resolver.resolve(tenant.id, item.id)
                        .andThenTo(supportCase -> supportCase.commentFor($stage, tenant, commenter, "answer")).await();
            }
        }
    }

    private void provisionDemoClosedSupportCases(Tenant tenant) {
        SupportCasesView listView = $queries.supportCases(tenant.id, 0, DEMO_SUPPORT_CASES_COUNT).await();

        List<String> assignedSupportCaseIds = new ArrayList<>();
        //List<SupportCaseView> assignedSupportCaseViews  = new ArrayList<>();

        for(SupportCasesView.ListItem item : listView.items) {
            SupportCaseView view = $queries.supportCaseOf(tenant.id, item.id).await();
            if(view.status.equals(SupportCaseStatus.ASSIGNED)) {
                assignedSupportCaseIds.add(view.id);
            }
        }

        for(int i=0; i<assignedSupportCaseIds.size()-2; i++) {
            String assignedSupportCaseId = assignedSupportCaseIds.get(i);

            SupportCaseView view = $queries.supportCaseOf(tenant.id, assignedSupportCaseId).await();
            User closingUser = User.from(view.supporter.id, view.supporter.name);

            resolver.resolve(tenant.id, assignedSupportCaseIds.get(i))
                    .andThenTo(supportCase -> supportCase.close(closingUser).await());
        }
    }
}
