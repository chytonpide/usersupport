package com.nconnect.usersupport.infrastructure.exchange;

import io.vlingo.xoom.turbo.actors.Settings;
import io.vlingo.xoom.lattice.exchange.Exchange;
import io.vlingo.xoom.turbo.exchange.ExchangeSettings;
import io.vlingo.xoom.turbo.exchange.ExchangeInitializer;
import io.vlingo.xoom.lattice.exchange.rabbitmq.ExchangeFactory;
import io.vlingo.xoom.lattice.exchange.ConnectionSettings;
import io.vlingo.xoom.lattice.exchange.rabbitmq.Message;
import io.vlingo.xoom.lattice.exchange.rabbitmq.MessageSender;
import io.vlingo.xoom.lattice.exchange.rabbitmq.InactiveBrokerExchangeException;
import io.vlingo.xoom.lattice.exchange.Covey;
import io.vlingo.xoom.lattice.grid.Grid;
import io.vlingo.xoom.symbio.store.dispatch.Dispatcher;

import io.vlingo.xoom.lattice.model.IdentifiedDomainEvent;

public class ExchangeBootstrap implements ExchangeInitializer {

  private Dispatcher<?> dispatcher;

  @Override
  public void init(final Grid stage) {
    final ExchangeSettings exchangeSettings =
                ExchangeSettings.loadOne(Settings.properties());

    final ConnectionSettings usersupportExchangeSettings = exchangeSettings.mapToConnection();

    final Exchange usersupportExchange =
                ExchangeFactory.fanOutInstanceQuietly(usersupportExchangeSettings, exchangeSettings.exchangeName, true);

    try {
      usersupportExchange.register(Covey.of(
          new MessageSender(usersupportExchange.connection()),
          received -> {},
          new CommentProducerAdapter(),
          IdentifiedDomainEvent.class,
          IdentifiedDomainEvent.class,
          Message.class));

      usersupportExchange.register(Covey.of(
          new MessageSender(usersupportExchange.connection()),
          received -> {},
          new SupportCaseProducerAdapter(),
          IdentifiedDomainEvent.class,
          IdentifiedDomainEvent.class,
          Message.class));

    } catch (final InactiveBrokerExchangeException exception) {
      stage.world().defaultLogger().error("Unable to register covey(s) for exchange");
      stage.world().defaultLogger().warn(exception.getMessage());
    }

    this.dispatcher = new ExchangeDispatcher(usersupportExchange);

    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
        usersupportExchange.close();

        System.out.println("\n");
        System.out.println("==================");
        System.out.println("Stopping exchange.");
        System.out.println("==================");
    }));
  }

  @Override
  public Dispatcher<?> dispatcher() {
    return dispatcher;
  }

}