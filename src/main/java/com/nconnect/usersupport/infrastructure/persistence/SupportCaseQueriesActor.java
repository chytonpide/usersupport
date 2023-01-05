package com.nconnect.usersupport.infrastructure.persistence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;

import com.nconnect.usersupport.infrastructure.SupportCaseView;
import com.nconnect.usersupport.infrastructure.SupportCasesView;
import io.vlingo.xoom.actors.Actor;
import io.vlingo.xoom.actors.CompletesEventually;
import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.common.Outcome;
import io.vlingo.xoom.reactivestreams.sink.ConsumerSink;
import io.vlingo.xoom.reactivestreams.sink.TerminalOperationConsumerSink;
import io.vlingo.xoom.symbio.Metadata;
import io.vlingo.xoom.symbio.StateBundle;
import io.vlingo.xoom.symbio.store.QueryExpression;
import io.vlingo.xoom.symbio.store.Result;
import io.vlingo.xoom.symbio.store.StorageException;
import io.vlingo.xoom.symbio.store.state.StateStore;

import com.nconnect.usersupport.infrastructure.SupportCaseData;

/**
 * See <a href="https://docs.vlingo.io/xoom-lattice/entity-cqrs#querying-a-statestore">Querying a StateStore</a>
 */
@SuppressWarnings("all")
public class SupportCaseQueriesActor extends Actor implements SupportCaseQueries, StateStore.ReadResultInterest {
    private final SupportCaseQueries self;
    private final StateStore stateStore;
    private final StateStore.ReadResultInterest readInterest;

    public SupportCaseQueriesActor(StateStore store) {
        //super();
        self = selfAs(SupportCaseQueries.class);
        this.stateStore = store;
        this.readInterest = (StateStore.ReadResultInterest) this.selfAs(StateStore.ReadResultInterest.class);
    }

    @Override
    public Completes<SupportCaseView> supportCaseOf(String tenantId, String id) {
        CompletesEventually completes = this.completesEventually();

        SupportCaseView notFoundState = SupportCaseView.empty();
        Consumer<SupportCaseView> collector = (supportCaseView) -> {
            if (supportCaseView.tenant != null && supportCaseView.tenant.id.equals(tenantId)) {
                completes.with(supportCaseView);
            } else {
                completes.with(notFoundState);
            }
        };

        QueryResultHandler resultHandler = new QueryResultHandler(collector, notFoundState);

        this.stateStore.read(
                id,
                SupportCaseView.class,
                this.readInterest,
                resultHandler);

        return this.completes();
    }


    @Override
    public Completes<SupportCasesView> supportCases(String tenantId, int offset, int limit) {
        CompletesEventually completes = this.completesEventually();
        Consumer<SupportCasesView> consumer = (supportCasesView) -> {
            completes.with(supportCasesView);
        };

        Completes<SupportCasesView> result =
                self.total(tenantId)
                        .andThenTo(total -> self.supportCases(tenantId, offset, limit, total))
                        .andThenConsume(consumer);

        return this.completes();
    }


    @Override
    public Completes<SupportCasesView> supportCases(String tenantId, int offset, int limit, long total) {
        SupportCasesView listView = SupportCasesView.from(new ArrayList<>(), offset, limit, total);

        Consumer<StateBundle> populator = (state) -> {
            SupportCaseView view = (SupportCaseView) state.object;
            listView.addListItem(SupportCasesView.ListItem.from(view));
        };

        CompletesEventually collectorComplete = this.completesEventually();

        Consumer<SupportCasesView> collector = (supportCasesView) -> {
            collectorComplete.with(supportCasesView);
        };

        TerminalOperationConsumerSink sink = new TerminalOperationConsumerSink(populator, listView, collector);

        String query = "select * from tbl_supportcaseview where s_data->''tenant''->>''id''=''" + tenantId + "'' order by s_data->>''openedAt'' DESC limit " + limit + " offset " + offset;

        this.stateStore.streamSomeUsing(QueryExpression.using(SupportCaseView.class, query)).andFinallyConsume((stream) -> {
            stream.flowInto(sink);
        });

        return this.completes();
    }

    /*
    private long syncTotal(String tenantId) {
        Total total = new Total();
        Consumer<StateBundle> populator = (state) -> {
            total.increase();
        };

        int result = 0;
        Completes<Total> totalComplete = new ResultReturns();

        Consumer<Total> collector = (aTotal) -> {

        };

        TerminalOperationConsumerSink sink = new TerminalOperationConsumerSink(populator, total, collector);

        String query = "select * from tbl_supportcaseview where s_data->''tenant''->>''id''=''" + tenantId + "''";

        this.stateStore.streamSomeUsing(QueryExpression.using(SupportCaseView.class, query)).andFinallyConsume((stream) -> {
            stream.flowInto(sink);
        });

        return total.get();
    }
    */


    @Override
    public Completes<Long> total(String tenantId) {
        Total total = new Total();
        Consumer<StateBundle> populator = (state) -> {
            total.increase();
        };

        CompletesEventually completes = this.completesEventually();

        Consumer<Total> collector = (Total) -> {
            completes.with(new Long(total.get()));
        };


        TerminalOperationConsumerSink sink = new TerminalOperationConsumerSink(populator, total, collector);

        String query = "select * from tbl_supportcaseview where s_data->''tenant''->>''id''=''" + tenantId + "''";

        this.stateStore.streamSomeUsing(QueryExpression.using(SupportCaseView.class, query)).andFinallyConsume((stream) -> {
            stream.flowInto(sink);
        });

        return this.completes();
    }

    class Total {
        public long total;

        Total() {
            total = 0;
        }

        void increase() {
            total = total + 1;
        }

        long get() {
            return total;
        }

    }


    @Override
    public final <T> void readResultedIn(Outcome<StorageException, Result> outcome, String id, T state, int stateVersion, Metadata metadata, Object resultHandler) {
        outcome.andThen((result) -> {
            QueryResultHandler.from(resultHandler).completeFoundWith(state);
            return result;
        }).otherwise((cause) -> {
            if (cause.result.isNotFound()) {
                QueryResultHandler.from(resultHandler).completeNotFound();
            } else {
                this.logger().info("Query state not read for update because: " + cause.getMessage(), cause);
            }
            return cause.result;
        });
    }

}
